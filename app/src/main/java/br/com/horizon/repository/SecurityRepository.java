package br.com.horizon.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import br.com.horizon.model.Filter;
import br.com.horizon.model.Security;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.resource.Resource;
import lombok.Value;

@Value
public class SecurityRepository {
    private FirebaseFirestore db;
    private CollectionReference securities;
    private MediatorLiveData<Resource<List<Security>>> mediator;

    public SecurityRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.securities = db.collection("securities");
        this.mediator = new MediatorLiveData<>();
    }

    public void getMaxOrMinInterest(Query.Direction direction, LoadedDataCallback<Double> rangeCallback) {
        Query query = securities.orderBy("interest", direction).limit(1);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                DocumentSnapshot documentSnapshot = documents.get(0);
                Double interest = documentSnapshot.getDouble("interest");
                rangeCallback.onSuccess(interest);
            }
        });
    }

    public void getAllPublishers(LoadedDataCallback<Set<String>> callback) {
        Set<String> publishers = new HashSet<>();
        securities.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot result = task.getResult();
                List<DocumentSnapshot> documents = result.getDocuments();
                documents.forEach(documentSnapshot -> {
                    Security foundSecurity = documentSnapshot.toObject(Security.class);
                    publishers.add(foundSecurity.getPublisher());
                });
                callback.onSuccess(publishers);
            }
        });
    }

    public void getAllEmitters(LoadedDataCallback<Set<String>> callback) {
        Set<String> emitters = new HashSet<>();
        securities.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot result = task.getResult();
                List<DocumentSnapshot> documents = result.getDocuments();
                documents.forEach(documentSnapshot -> {
                    Security foundSecurity = documentSnapshot.toObject(Security.class);
                    emitters.add(foundSecurity.getEmitter());
                });
                callback.onSuccess(emitters);
            }
        });
    }

    public LiveData<Resource<List<Security>>> fetchAll() {
        securities.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                populateMediatorWithAllSecurities();
            } else {
                addFirebaseFailResourceForList(task);
            }
        });
        return mediator;
    }

    public void fetchById(String id, LoadedDataCallback<Security> callback) {
        securities.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                Security security = Objects.requireNonNull(result).toObject(Security.class);
                security.setId(result.getId());
                callback.onSuccess(security);
            } else {
                callback.onFail(Objects.requireNonNull(task.getException()).getMessage());
            }
        });

    }

    private void populateMediatorWithAllSecurities() {
        securities.addSnapshotListener((queryDocumentSnapshots, e) -> {

            List<DocumentSnapshot> documents = Objects
                    .requireNonNull(queryDocumentSnapshots).getDocuments();
            List<Security> securities = new ArrayList<>();

            documents.forEach(documentSnapshot -> {
                Security newSecurity = documentSnapshot.toObject(Security.class);
                Objects.requireNonNull(newSecurity).setId(documentSnapshot.getId());
                securities.add(newSecurity);
            });

            mediator.setValue(new Resource<>(securities, null));
        });
    }

    private void addFirebaseFailResourceForList(Task<QuerySnapshot> task) {
        MutableLiveData<Resource<List<Security>>> firebaseFails = new MutableLiveData<>();
        mediator.addSource(firebaseFails, failResource -> {
            Resource<List<Security>> currentResource = mediator.getValue();
            Resource<List<Security>> newResource = currentResource != null ?
                    new Resource<>(currentResource.getData(), failResource.getError())
                    : failResource;
            mediator.setValue(newResource);
        });

        firebaseFails.setValue(new Resource<>(null, Objects.requireNonNull(task
                .getException()).getMessage()));
    }

    public LiveData<Resource<List<Security>>> fetchFiltered(Filter filter) {
        return null;
    }
}
