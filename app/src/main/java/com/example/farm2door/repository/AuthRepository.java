package com.example.farm2door.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.farm2door.helpers.AuthHelper;
import com.example.farm2door.models.User;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    FirebaseAuth mAuth;
    MutableLiveData<User> userLiveData = new MutableLiveData<>();
    public AuthRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static String getLoggedInUserId(){
        // check if firebase user is set
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        return AuthHelper.getInstance().getSavedUser().getId();
    }
    public LiveData<User> getLoggedInUser(){
        return userLiveData;
    }
    // login user
    public void loginUser(String email, String password, final AuthCallback callback){
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            if (callback != null) callback.onSuccess(authResult.getUser());
        }).addOnFailureListener(e -> {
            if (callback != null) callback.onError(e);
        });
    }

    // set logged in user
    public void setLoggedInUser(User user){
        userLiveData.setValue(user);
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
