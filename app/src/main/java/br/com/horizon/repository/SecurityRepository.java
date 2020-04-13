package br.com.horizon.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.horizon.model.Security;
import br.com.horizon.repository.resource.Resource;
import lombok.Value;

@Value
public class SecurityRepository {
    private FirebaseFirestore db;
    private CollectionReference securities;
    private MediatorLiveData<Resource<List<Security>>> mediator;
    private MutableLiveData<Resource<Security>> securityLiveData;

    public SecurityRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.securities = db.collection("securities");
        this.mediator = new MediatorLiveData<>();
        this.securityLiveData = new MutableLiveData<>();
    }

    public LiveData<Resource<List<Security>>> fetchAll() {
        securities.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                populateLiveDataWithFetchedDocuments();
            } else {
                addFirebaseFailResource(task);
            }
        });
        return mediator;
    }

    public LiveData<Resource<Security>> fetchById(String id) {
        securities.addSnapshotListener((queryDocumentSnapshots, e) -> {
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
            documents.forEach(documentSnapshot -> {
                if (id.equals(documentSnapshot.getId())) {
                    Security security = documentSnapshot.toObject(Security.class);
                    securityLiveData.setValue(new Resource<>(security, null));
                }
            });
        });
        return securityLiveData;
    }

    private void populateLiveDataWithFetchedDocuments() {
        securities.addSnapshotListener((queryDocumentSnapshots, e) -> {
            List<DocumentSnapshot> documents = Objects.requireNonNull(queryDocumentSnapshots).getDocuments();
            List<Security> securities = new ArrayList<>();
            documents.forEach(documentSnapshot -> {
                Security newSecurity = documentSnapshot.toObject(Security.class);
                Objects.requireNonNull(newSecurity).setId(documentSnapshot.getId());
                securities.add(newSecurity);
            });

            mediator.setValue(new Resource<>(securities, null));
        });
    }

    private void addFirebaseFailResource(Task<QuerySnapshot> task) {
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

}
