package com.example.farm2door.repository;

import com.example.farm2door.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    FirebaseFirestore db;
    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // save user information to FireStore
    public void saveUser(User user, RegisterCallback callback){
        db.collection("users").document(user.getId()).set(user).addOnSuccessListener(aVoid -> {
            if (callback != null) callback.onSuccess();
        }).addOnFailureListener(e -> {
            if (callback != null) callback.onError(e);
        });
    }

    /// get user information from FireStore
    public void getUser(String userId, final UserCallback callback){
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            callback.onSuccess(user);
        });
    }



    // listener for user information
    public interface UserCallback{
        void onSuccess(User user);
        void onError(Exception e);
    }

   // interface that listens whether registration was successful or not
    public interface RegisterCallback{
        void onSuccess();
        void onError(Exception e);
    }

    // interface that listens whether login was successful or not
    public interface LoginCallback{
        void onSuccess();
        void onError(Exception e);
    }

}
