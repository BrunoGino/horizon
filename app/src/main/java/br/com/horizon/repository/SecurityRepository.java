package br.com.horizon.repository;

import android.util.Log;

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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import br.com.horizon.model.Security;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.resource.Resource;

public class SecurityRepository {
    private final FirebaseFirestore db;
    private final CollectionReference securities;

    public SecurityRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.securities = db.collection("securities");
    }

    public void getAllFilteredByType(String type, LoadedDataCallback<List<Security>> listCallback) {
        runQueryByType(securities, type, listCallback);
    }

    public void getOrderedByAndByType(String titleType, String orderOption,
                                      LoadedDataCallback<List<Security>> listLoadedDataCallback) {
        switch (orderOption) {
            case "Maior rentabilidade em menor prazo": {

                Query query = securities.orderBy("interest", Query.Direction.DESCENDING)
                        .orderBy("totalTime", Query.Direction.ASCENDING);
                Log.d("orderOption", "getOrderedByAndByType: " + "Maior rentabilidade em menor prazo");
                runQueryByType(query, titleType, listLoadedDataCallback);
                break;
            }
            case "Maior juros": {
                Query query = securities.orderBy("interest", Query.Direction.DESCENDING);
                Log.d("orderOption", "getOrderedByAndByType: " + "Maior juros");
                runQueryByType(query, titleType, listLoadedDataCallback);
                break;
            }
            case "Menor juros": {
                Query query = securities.orderBy("interest", Query.Direction.ASCENDING);
                Log.d("orderOption", "getOrderedByAndByType: " + "Menor juros");
                runQueryByType(query, titleType, listLoadedDataCallback);
                break;
            }
            case "Maior prazo": {
                Query query = securities.orderBy("totalTime", Query.Direction.DESCENDING);
                Log.d("orderOption", "getOrderedByAndByType: " + "Maior prazo");
                runQueryByType(query, titleType, listLoadedDataCallback);
                break;
            }
            case "Menor prazo": {
                Query query = securities.orderBy("totalTime", Query.Direction.ASCENDING);
                Log.d("orderOption", "getOrderedByAndByType: " + "Menor prazo");
                runQueryByType(query, titleType, listLoadedDataCallback);
                break;
            }
            case "Menor investimento mínimo": {
                Query query = securities.orderBy("titleValue", Query.Direction.ASCENDING);
                Log.d("orderOption", "getOrderedByAndByType: " + "Menor investimento mínimo");
                runQueryByType(query, titleType, listLoadedDataCallback);
                break;
            }
            default: {
                Query query = securities.orderBy("publisher", Query.Direction.ASCENDING);
                Log.d("orderOption", "getOrderedByAndByType: " + "Emitter");
                runQueryByType(query, titleType, listLoadedDataCallback);
                break;
            }
        }

    }

    private void runQueryByType(Query query, String type, LoadedDataCallback<List<Security>> listCallback) {
        if (type.equals("all")) {
            runQuery(listCallback, query);
        } else if (type.toUpperCase().equals("LCI/LCA")) {
            Query lciLcaQuery = query.whereIn("titleType", Arrays.asList("LCI", "LCA"));
            runQuery(listCallback, lciLcaQuery);
        } else if (type.toUpperCase().equals("CRI/CRA")) {
            Query criCraQuery = query.whereEqualTo("titleType", "CRI")
                    .whereEqualTo("titleType", "CRA");
            runQuery(listCallback, criCraQuery);
        } else {
            runQuery(listCallback, query.whereEqualTo("titleType", type));
        }
    }

    public void getAllSecurities(LoadedDataCallback<List<Security>> listLoadedDataCallback) {
        runQuery(listLoadedDataCallback, securities);
    }

    private void runQuery(LoadedDataCallback<List<Security>> listCallback, Query query) {
        List<Security> securities = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                documents.forEach(documentSnapshot -> {
                    if (documentSnapshot != null) {
                        Security security = documentSnapshot.toObject(Security.class);
                        security.setId(documentSnapshot.getId());
                        securities.add(security);
                        Log.d("RUNQUERY", "runQuery: " + security.toString());
                    }
                });
                listCallback.onSuccess(securities);
            }
        });
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

}
