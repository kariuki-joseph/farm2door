package com.example.farm2door.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.CartItem;
import com.example.farm2door.models.OrderItem;
import com.example.farm2door.repository.CartRepository;
import com.example.farm2door.repository.OrderRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaceOrderViewModel extends ViewModel {
    private OrderRepository orderRepository;
    private CartRepository cartRepository;
    private String loggedInUserId;
    LoadingViewModel loadingViewModel;
    private MutableLiveData<Boolean> isOrderPlacedLiveData = new MutableLiveData<>();
    private MutableLiveData<String> orderNumberLiveData = new MutableLiveData<>();
    public PlaceOrderViewModel(){
        orderRepository = new OrderRepository();
        cartRepository = new CartRepository();
        loadingViewModel = LoadingViewModel.getInstance();
        loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // track if the order is placed
    public LiveData<Boolean> getIsOrderPlaced(){
        return isOrderPlacedLiveData;
    }
    // get order number of the placed item
    public LiveData<String> getOrderNumber() {
        return orderNumberLiveData;
    }

    // generate an order item based on the current cart items
    private List<OrderItem> generateOrders(List<CartItem> cartItems){
        List<OrderItem> orderItems = new ArrayList<>();
        String orderNumber = orderRepository.generateOrderNumber();
        String orderDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        for(CartItem cartItem: cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setId(orderRepository.generateOrderId());
            orderItem.setOrderNumber(orderNumber);
            orderItem.setName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getProductTotalPrice());
            orderItem.setUnitName(cartItem.getUnitName());
            orderItem.setQuantity(cartItem.getProductQuantity());
            orderItem.setOrderDate(orderDate);
            orderItem.setImageURL(cartItem.getProductImage());
            orderItem.setLatitude(orderRepository.getCustomerLocation().getValue().latitude);
            orderItem.setLongitude(orderRepository.getCustomerLocation().getValue().longitude);
            orderItem.setFarmerId(cartItem.getFarmerId());
            orderItem.setCustomerId(loggedInUserId);

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    public void setCustomerLocation(LatLng location){
        orderRepository.setCustomerLocation(location);
    }

    public void generateAndPlaceOrders(List<CartItem> cartItems){
        List<OrderItem> orders = this.generateOrders(cartItems);
        loadingViewModel.setLoading(true);
        orderRepository.placeOrders(orders, orderNumber -> {
            loadingViewModel.setLoading(false);
            // cart cleared successfully
            orderNumberLiveData.setValue(orderNumber);
            isOrderPlacedLiveData.setValue(true);
        });
    }

}
