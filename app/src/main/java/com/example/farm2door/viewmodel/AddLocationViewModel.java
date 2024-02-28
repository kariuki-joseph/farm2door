package com.example.farm2door.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.AddLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public class AddLocationViewModel extends ViewModel {
    private LoadingViewModel loadingViewModel;
    private MutableLiveData<List<Polyline>> polyLineLiveData = new MutableLiveData<>();
    public AddLocationViewModel(){
        loadingViewModel = LoadingViewModel.getInstance();
    }


    public LiveData<List<Polyline>> getPolyLine(){
        return polyLineLiveData;
    }

    // get polyline between two points
    public void getPolyLine(LatLng origin, LatLng dest){
    }
}
