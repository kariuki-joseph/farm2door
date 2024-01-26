package com.example.farm2door.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.OrderItem;
import com.example.farm2door.models.User;
import com.example.farm2door.repository.OrderRepository;
import com.example.farm2door.repository.UserRepository;
import com.google.android.gms.maps.model.LatLng;

public class TrackOrderViewModel extends ViewModel {
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    LoadingViewModel loadingViewModel;
    private MutableLiveData<User> farmerLiveData = new MutableLiveData<>();
    private MutableLiveData<User> customerLiveData = new MutableLiveData<>();
    private MutableLiveData<LatLng> farmerLocationLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isOrderDeliveredLiveData = new MutableLiveData<>();
    public TrackOrderViewModel() {
        orderRepository = new OrderRepository();
        userRepository = new UserRepository();
        loadingViewModel = LoadingViewModel.getInstance();
    }

    public LiveData<OrderItem> getOrderItem(){
        return orderRepository.getOrderItem();
    }


    public LiveData<User> getFarmer(){
        return farmerLiveData;
    }

    public LiveData<User> getCustomer(){
        return customerLiveData;
    }

    public LiveData<LatLng> getFarmerLocation(){
        return farmerLocationLiveData;
    }
    public LiveData<Boolean> isOrderDelivered(){
        return isOrderDeliveredLiveData;
    }
    // get order information
    public void getOrder(String orderId){
        loadingViewModel.setLoading(true);
        orderRepository.getOrder(orderId, orderItem -> {
            loadingViewModel.setLoading(false);
            orderRepository.setOrder(orderItem);
        });
    }

    // get farmer information
    public void fetchFarmerInfo(String farmerId){
        loadingViewModel.setLoading(true);
       userRepository.getUser(farmerId, new UserRepository.UserCallback() {
           @Override
           public void onSuccess(User user) {
                loadingViewModel.setLoading(false);
                farmerLiveData.setValue(user);
           }

           @Override
           public void onError(Exception e) {
            // handle error
               loadingViewModel.setLoading(false);
           }
       });
    }


    // get customer information
    public void fetchCustomerInfo(String customerId){
        loadingViewModel.setLoading(false);
        userRepository.getUser(customerId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                loadingViewModel.setLoading(false);
                customerLiveData.setValue(user);
            }

            @Override
            public void onError(Exception e) {
            loadingViewModel.setLoading(false);
            }
        });
    }

    // track farmer live location
    void trackFarmerLiveLocation(String farmerId){

    }

    // set an order as delivered
    public void setOrderDelivered(String orderId){
        loadingViewModel.setLoading(true);
        orderRepository.setOrderDelivered(orderId, isSuccessful -> {
            loadingViewModel.setLoading(false);
            isOrderDeliveredLiveData.setValue(isSuccessful);
        });
    }
}
