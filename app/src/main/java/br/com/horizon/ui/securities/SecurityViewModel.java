package br.com.horizon.ui.securities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import br.com.horizon.model.Security;

public class SecurityViewModel extends ViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference securities = db.collection("securities");
    private Query query = securities;
    private MutableLiveData<List<Security>> securitiesLiveData;


    public SecurityViewModel() {
        securitiesLiveData = new MutableLiveData<>();

        query.addSnapshotListener((queryDocumentSnapshots, e)
                -> securitiesLiveData.postValue(queryDocumentSnapshots.toObjects(Security.class)));

    }

    public MutableLiveData<List<Security>> getSecuritiesLiveData() {
        return securitiesLiveData;
    }
}