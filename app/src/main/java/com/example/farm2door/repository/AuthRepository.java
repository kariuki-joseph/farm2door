package com.example.farm2door.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    FirebaseAuth mAuth;
    public AuthRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    // login user
    public void loginUser(String email, String password, final AuthCallback callback){
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            if (callback != null) callback.onSuccess(authResult.getUser());
        }).addOnFailureListener(e -> {
            if (callback != null) callback.onError(e);
        });
    }

    // register user
    public void registerUser(String email, String password, final AuthCallback callback){
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            if (callback != null) callback.onSuccess(authResult.getUser());
        }).addOnFailureListener(e -> {
            if (callback != null) callback.onError(e);
        });
    }

    // interface that listens whether registration was successful or not
    public interface AuthCallback{
        void onSuccess(FirebaseUser user);
        void onError(Exception e);
    }
}
