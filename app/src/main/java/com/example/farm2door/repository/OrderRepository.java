package com.example.farm2door.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.farm2door.models.OrderItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderRepository {
    private FirebaseFirestore db;
    private MutableLiveData<OrderItem> orderItemLiveData = new MutableLiveData<>();
    private MutableLiveData<LatLng> customerLocationLiveData = new MutableLiveData<>();
    public OrderRepository(){
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<OrderItem> getOrderItem(){
        return orderItemLiveData;
    }

    // get current location of the user placing the order
    public LiveData<LatLng> getCustomerLocation(){
        return customerLocationLiveData;
    }

    // set the current order for the current user
    public void setOrder(OrderItem  orderItem){
        orderItemLiveData.setValue(orderItem);
    }

    // set the current location of the user placing the order
    public void setCustomerLocation(LatLng location){
        customerLocationLiveData.setValue(location);
    }

    public String generateOrderNumber(){
        return db.collection("orders").document().getId().toUpperCase().substring(0, 6);
    }
    public String generateOrderId(){
        return db.collection("orders").document().getId();
    }


    // get order items by a certain user
    public void getUserOrderItems(String userId, final OnOrderItemsLoadedListener callback){
    db.collection("orders").whereEqualTo("customerId", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
        List<OrderItem> orderItems = queryDocumentSnapshots.toObjects(OrderItem.class);
        callback.onOrderItemLoaded(orderItems);
    }).addOnFailureListener(e -> {
        callback.onOrderItemLoaded(null);
    });
    }

    // delete an order item
    public void deleteOrderItem(String orderId, final OnOrderDeletedListener callback){
        db.collection("orders").document(orderId).delete().addOnSuccessListener(aVoid -> {
            callback.onOrderDeleted(true);
        }).addOnFailureListener(e -> {
            callback.onOrderDeleted(false);
        });
    }

    // place orders
    public void placeOrders(List<OrderItem> orders, final OnOrdersPlacedListener callback){
        // write in batch
        WriteBatch batch = db.batch();
        for(OrderItem orderItem: orders){
            batch.set(db.collection("orders").document(), orderItem);
        }

        batch.commit().addOnSuccessListener(aVoid -> {
            callback.onOrdersPlaced(true);
        }).addOnFailureListener(e -> {
            callback.onOrdersPlaced(false);
        });
    }

    // get a single order item by order id
    public void getOrder(String orderId, final OnOrderItemLoadedListener callback){
        db.collection("orders").document(orderId).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                OrderItem orderItem = documentSnapshot.toObject(OrderItem.class);
                callback.onOrderItemLoaded(orderItem);
            }else{
               callback.onOrderItemLoaded(null);
            }
        }).addOnFailureListener(e -> {
            callback.onOrderItemLoaded(null);
        });
    }


    // interface to return order items to ViewModel
    public interface OnOrderItemsLoadedListener{
        void onOrderItemLoaded(List<OrderItem> orderItems);
    }

    // get a single order item
    public interface OnOrderItemLoadedListener{
        void onOrderItemLoaded(OrderItem orderItem);
    }

    public interface OnOrderDeletedListener{
        void onOrderDeleted(boolean isDeleted);
    }

    public interface OnOrdersPlacedListener{
        void onOrdersPlaced(boolean isPlaced);
    }
}
