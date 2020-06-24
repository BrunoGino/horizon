package br.com.horizon.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;

import br.com.horizon.model.User;
import br.com.horizon.repository.callback.LoadedDataCallback;

public class UserRepository {
    private final FirebaseFirestore db;
    private final CollectionReference users;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
    }

    public void getUserByUID(String uid, LoadedDataCallback<User> callback) {
        Query query = users.whereEqualTo("userUID", uid);
        runQuery(query, callback);
    }

    public void createNewUser(User newUser, LoadedDataCallback<User> callback) {
        users.add(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(newUser);
            } else {
                callback.onFail(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    private void runQuery(Query query, LoadedDataCallback<User> callback) {
        User user = new User();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = Objects.requireNonNull(task.getResult()).getDocuments();
                documents.forEach(documentSnapshot -> {
                    if (documentSnapshot != null) {
                        User foundUser = Objects.requireNonNull(documentSnapshot.toObject(User.class));
                        user.setUserUID(foundUser.getUserUID());
                        user.setEmail(foundUser.getEmail());
                        user.setFirstName(foundUser.getFirstName());
                        user.setLastName(foundUser.getLastName());
                    }
                });
                callback.onSuccess(user);
            }
        });
    }

}
