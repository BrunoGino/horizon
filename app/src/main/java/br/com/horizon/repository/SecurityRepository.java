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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.horizon.MainActivity;
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

    public void getSecuritiesFiltered(List<String> selectedIrs, List<String> selectedPublishers,
                                      String orderBy, String titleType, LoadedDataCallback<List<Security>> listLoadedDataCallback) {
        Query query = getQueryOrderedBy(orderBy);
        if (selectedPublishers != null && !selectedPublishers.isEmpty()) {
            Log.d("QUERYfirebase", "query.whereIn(publisher, " + selectedPublishers.toString() + ")");
            query = query.whereIn("publisher", selectedPublishers);
            Log.d("QUERYfirebase", "getSecuritiesFiltered: publishers " + selectedPublishers.toString());
        }
        Log.d("QUERYfirebase", "getSecuritiesFiltered: end " + query.toString());
        runQueryByType(query, titleType, listLoadedDataCallback);
    }

    public void getAllPublishersBySecurityType(String titleType, LoadedDataCallback<Set<String>> listLoadedDataCallback) {
        Set<String> publishers = new HashSet<>();
        if (titleType.equals("Todos os títulos")) {
            queryPublishers(listLoadedDataCallback, publishers, securities);
        } else if (titleType.toUpperCase().equals("LCI/LCA")) {
            Query lciLcaQuery = securities.whereIn("titleType", Arrays.asList("LCI", "LCA"));
            queryPublishers(listLoadedDataCallback, publishers, lciLcaQuery);
        } else if (titleType.toUpperCase().equals("CRI/CRA")) {
            Query criCraQuery = securities.whereIn("titleType", Arrays.asList("CRI", "CRA"));
            queryPublishers(listLoadedDataCallback, publishers, criCraQuery);
        } else {
            queryPublishers(listLoadedDataCallback, publishers, securities.whereEqualTo("titleType", titleType));
        }
    }

    private void queryPublishers(LoadedDataCallback<Set<String>> listLoadedDataCallback,
                                 Set<String> publishers, Query query) {
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = Objects.requireNonNull(task.getResult()).getDocuments();
                documents.forEach(documentSnapshot -> {
                    Security security = documentSnapshot.toObject(Security.class);
                    String publisher = Objects.requireNonNull(security).getPublisher();
                    publishers.add(publisher);
                });
                listLoadedDataCallback.onSuccess(publishers);
            }
        });
    }

    private Query getQueryOrderedBy(String orderOption) {
        switch (orderOption) {
            case "Maior rentabilidade em menor prazo":
                return securities.orderBy("totalTime", Query.Direction.ASCENDING)
                        .orderBy("interest", Query.Direction.DESCENDING);
            case "Maior juros":
                return securities.orderBy("interest", Query.Direction.DESCENDING);
            case "Menor juros":
                return securities.orderBy("interest", Query.Direction.ASCENDING);
            case "Maior prazo":
                return securities.orderBy("totalTime", Query.Direction.DESCENDING);
            case "Menor prazo":
                return securities.orderBy("totalTime", Query.Direction.ASCENDING);
            case "Menor investimento mínimo":
                return securities.orderBy("titleValue", Query.Direction.ASCENDING);
            default:
                return securities.orderBy("publisher", Query.Direction.ASCENDING);
        }
    }


    private void runQueryByType(Query query, String type, LoadedDataCallback<List<Security>> listCallback) {
        if (type.equals("Todos os títulos")) {
            Log.d("QUERYfirebase", "runQueryByType: " + "Todos os títulos");
            runQuery(listCallback, query);
        } else if (type.toUpperCase().equals("LCI/LCA")) {
            Log.d("QUERYfirebase", "runQueryByType: " + "LCI/LCA");
            Query lciLcaQuery = query.whereIn("titleType", Arrays.asList("LCI", "LCA"));
            runQuery(listCallback, lciLcaQuery);
        } else if (type.toUpperCase().equals("CRI/CRA")) {
            Log.d("QUERYfirebase", "runQueryByType: " + "CRI/CRA");
            Query criCraQuery = query.whereEqualTo("titleType", "CRI")
                    .whereEqualTo("titleType", "CRA");
            runQuery(listCallback, criCraQuery);
        } else {
            Log.d("QUERYfirebase", "runQueryByType: " + type);
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
                    }
                });
                Log.d("QUERYfirebase", "runQuery: " + securities.toString());
                listCallback.onSuccess(securities);
            } else {
                Log.d("QUERYfirebase", "runQuery: " + task.getException().getMessage());
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
