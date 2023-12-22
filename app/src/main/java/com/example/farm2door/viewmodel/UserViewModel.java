package com.example.farm2door.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.User;
import com.example.farm2door.repository.AuthRepository;
import com.example.farm2door.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private MutableLiveData<User> userData = new MutableLiveData<>();
    private MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private MutableLiveData<Exception> exception = new MutableLiveData<>();

    public UserViewModel() {
        userRepository = new UserRepository();
        authRepository = new AuthRepository();
    }

    public LiveData<User> getUser() {
        return userData;
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<Exception> getException() {
        return exception;
    }


    public void getUser(String userId){
        userRepository.getUser(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                userData.setValue(user);
            }

            @Override
            public void onError(Exception e) {
                // do nothing
                userData.setValue(null);
            }
        });
    }

    private void saveUser(User user){
        userRepository.saveUser(user, new UserRepository.RegisterCallback() {
            @Override
            public void onSuccess() {
                registerSuccess.setValue(true);
            }

            @Override
            public void onError(Exception e) {
                registerSuccess.setValue(false);
                exception.setValue(e);
            }
        });
    }


    public void registerUser(User user){
        // register with firebase first
        authRepository.registerUser(user.getEmail(), user.getPassword(), new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                // save user to FireStore
                user.setId(firebaseUser.getUid());
                saveUser(user);
            }

            @Override
            public void onError(Exception e) {
                registerSuccess.setValue(false);
                exception.setValue(e);
            }
        });
    }

    public void loginUser(String email, String password){
        authRepository.loginUser(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                loginSuccess.setValue(true);
                // get user information from FireStore
                getUser(user.getUid());
            }

            @Override
            public void onError(Exception e) {
                loginSuccess.setValue(false);
                exception.setValue(e);
            }
        });
    }
}
