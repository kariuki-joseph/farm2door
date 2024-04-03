package com.example.farm2door.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.helpers.AuthHelper;
import com.example.farm2door.models.OrderItem;
import com.example.farm2door.repository.OrderRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

public class OrdersViewModel extends ViewModel {
    private OrderRepository orderRepository;
    private LoadingViewModel loadingViewModel;
    String loggedInUserId;

    private MutableLiveData<List<OrderItem>> orderItemsLiveData = new MutableLiveData<>();

    public OrdersViewModel(){
        orderRepository = new OrderRepository();
        loadingViewModel = LoadingViewModel.getInstance();
        loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public LiveData<List<OrderItem>> getOrderItems(){
        return orderItemsLiveData;
    }
    // create a new order
    public void placeOrder(){

    }

    // get order items from firebase
    public void fetchOrderItems(boolean isFarmer){
        loadingViewModel.setLoading(true);
        Log.d("LoggedInUserId", loggedInUserId);
        Log.d("IsFarmer", String.valueOf(isFarmer));

        orderRepository.getUserOrderItems(loggedInUserId,  isFarmer, orderItems -> {
            loadingViewModel.setLoading(false);

            // sort by not delivered first
            Collections.sort(orderItems, (o1, o2) -> {
                if(o1.isDelivered() && !o2.isDelivered()){
                    return 1;
                }else if(!o1.isDelivered() && o2.isDelivered()){
                    return -1;
                }else{
                    return 0;
                }
            });

            orderItemsLiveData.setValue(orderItems);
        });
    }

    // delete an order item
    public void deleteOrderItem(OrderItem orderItem ){
        loadingViewModel.setLoading(true);
        orderRepository.deleteOrderItem(orderItem.getId(), isDeleted -> {
            loadingViewModel.setLoading(false);
            if(isDeleted){
                fetchOrderItems(orderItem.getFarmerId().equals(loggedInUserId));
            }
        });
    }
}
