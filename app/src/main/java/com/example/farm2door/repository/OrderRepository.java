package com.example.farm2door.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.farm2door.models.OrderItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.Write;

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
        return db.collection("orders").document().getId().toUpperCase().substring(0, 8);
    }
    public String generateOrderId(){
        return db.collection("orders").document().getId();
    }


    // get order items by a certain user
    public void getUserOrderItems(String userId, boolean isFarmer, final OnOrderItemsLoadedListener callback){
    db.collection("orders").whereEqualTo(isFarmer? "farmerId": "customerId", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
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
            batch.set(db.collection("orders").document(orderItem.getId()), orderItem);
        }


        batch.commit().addOnSuccessListener(aVoid -> {
            // decrease the quantity of the product in the stock
            WriteBatch updateQuantityBatch = db.batch();
            AtomicInteger counter = new AtomicInteger(0);
            for(OrderItem orderItem: orders){
                db.collection("products").whereEqualTo("productId", orderItem.getProductId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.getDocuments().isEmpty()){
                        double newQuantity = queryDocumentSnapshots.toObjects(OrderItem.class).get(0).getQuantity() - orderItem.getQuantity();
                        updateQuantityBatch.update(db.collection("products").document(queryDocumentSnapshots.getDocuments().get(0).getId()), "totalInStock", newQuantity);
                    }
                    if(counter.incrementAndGet() == orders.size()){
                        updateQuantityBatch.commit().addOnSuccessListener(aVoid1 -> {
                            callback.onOrdersPlaced(orders.get(0).getOrderNumber());
                        }).addOnFailureListener(e -> {
                            callback.onOrdersPlaced(null);
                        });
                    }
                }).addOnFailureListener(e -> {
                    callback.onOrdersPlaced(null);
                });
            }
        }).addOnFailureListener(e -> {
            callback.onOrdersPlaced(null);
        });
    }

    // get a single order item by order id
    public void getOrder(String orderNumber, final OnOrderItemLoadedListener callback){
       db.collection("orders").where(Filter.equalTo("orderNumber", orderNumber)).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.getDocuments().isEmpty()){
                OrderItem orderItem = queryDocumentSnapshots.toObjects(OrderItem.class).get(0);
                callback.onOrderItemLoaded(orderItem);
            }else{
               callback.onOrderItemLoaded(null);
            }
        }).addOnFailureListener(e -> {
            callback.onOrderItemLoaded(null);
        });
    }
    public void setOrderDelivered(String orderNumber, final OnOrderDeliveredListener callback){
        // get all orders with the same order number
        db.collection("orders").where(Filter.equalTo("orderNumber", orderNumber)).get().addOnSuccessListener(
                queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.getDocuments().isEmpty()){
                        List<OrderItem> orderItems = queryDocumentSnapshots.toObjects(OrderItem.class);
                        // batch write
                        WriteBatch batch = db.batch();
                        for(OrderItem item: orderItems){
                            batch.update(db.collection("orders").document(item.getId()), "delivered", true);
                            batch.update(db.collection("orders").document(item.getId()), "confirmed", true);
                        }

                        batch.commit().addOnSuccessListener(aVoid -> {
                            callback.onOrderDelivered(true);
                        }).addOnFailureListener(e -> {
                            callback.onOrderDelivered(false);
                        });
                    }else{
                        callback.onOrderDelivered(false);
                    }
                }
        ).addOnFailureListener(e -> {
            callback.onOrderDelivered(false);
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
        void onOrdersPlaced(String orderNumber);
    }

    public interface OnOrderDeliveredListener{
        void onOrderDelivered(boolean isSuccessful);
    }
}
