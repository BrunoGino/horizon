package br.com.horizon.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import br.com.horizon.model.Security;
import br.com.horizon.repository.resource.Resource;
import lombok.Value;

@Value
public class SecurityRepository {
    private FirebaseFirestore db;
    private CollectionReference securities;
    private MutableLiveData<Resource<List<Security>>> liveData;

    public SecurityRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.securities = db.collection("securities");
        this.liveData = new MutableLiveData<>();
    }

    public LiveData<Resource<List<Security>>> fetchAll() {
        securities.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(task.getResult().toObjects(Security.class), null));
            } else {
                liveData.setValue(new Resource<>(null, task.getException().getMessage()));
            }
        });

        return liveData;
    }


}
