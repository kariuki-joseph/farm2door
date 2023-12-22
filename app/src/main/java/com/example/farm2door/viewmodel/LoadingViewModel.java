package com.example.farm2door.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// handle loading state using a singleton pattern
public class LoadingViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private static LoadingViewModel instance;

    private LoadingViewModel() {
        // prevent instantiation
    }

    public static LoadingViewModel getInstance(){
        if (instance == null) instance = new LoadingViewModel();
        return instance;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public void setLoading(boolean loading){
        isLoading.setValue(loading);
    }
}
