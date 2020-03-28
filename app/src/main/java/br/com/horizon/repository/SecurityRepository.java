package br.com.horizon.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    public SecurityRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.securities = db.collection("securities");
        this.mediator = new MediatorLiveData<>();
    }

    public LiveData<Resource<List<Security>>> fetchAll() {
        securities.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mediator.setValue(new Resource<>(task.getResult().toObjects(Security.class), null));
            } else {
                addFirebaseFailResource(task);
            }
        });

        return mediator;
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
