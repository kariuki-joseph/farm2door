package com.example.farm2door.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.farm2door.models.CartItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

public class CartRepository {
    FirebaseFirestore db;
    private MutableLiveData<Map<String, CartItem>> cartItemsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> totalAmountLiveData = new MutableLiveData<>();

    public CartRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<Map<String, CartItem>> getCartItems() {
        return  cartItemsLiveData;
    }
    public LiveData<Integer> getTotalAmount() {
        return totalAmountLiveData;
    }


    public void setCartItems(Map<String, CartItem> data){
        cartItemsLiveData.setValue(data);
    }
    public void setTotalAmount(int totalAmount){
        totalAmountLiveData.setValue(totalAmount);
    }

    // get all cart items for a certain user
    public void getUserCartItems(String userId, final OnCartItemsLoadedListener callback){
        db.collection("users").document(userId).collection("cart").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<CartItem> cartItems = queryDocumentSnapshots.toObjects(CartItem.class);
            callback.onCartItemsLoaded(cartItems);
        }).addOnFailureListener(e -> {
            callback.onCartItemsLoaded(null);
        });
    }

    // save item to firebase
    public void addItemToCart(String userId, CartItem cartItem, final OnCartItemAddedListener callback){
        db.collection("users").document(userId).collection("cart").document(cartItem.getId()).set(cartItem).addOnSuccessListener(aVoid -> {
            callback.onCartItemAdded(true);
        }).addOnFailureListener(e -> {
            callback.onCartItemAdded(false);
        });
    }

    // update a cart item
    public void updateCartItem(String userId, CartItem cartItem, final OnCartItemUpdatedListener callback){
        db.collection("users").document(userId).collection("cart").document(cartItem.getId()).set(cartItem).addOnSuccessListener(aVoid -> {
            callback.onCartItemUpdated(true);
        }).addOnFailureListener(e -> {
            callback.onCartItemUpdated(false);
        });
    }

    // delete cart items
    public void deleteCartItem(String userId, String cartId, final OnCartItemDeleteListener callback){
        db.collection("users").document(userId).collection("cart").document(cartId).delete().addOnSuccessListener(aVoid -> {
            callback.onCartItemDeleted(true);
        }).addOnFailureListener(e -> {
            callback.onCartItemDeleted(false);
        });
    }

    // delete all cart items of a certain user
    public void deleteCartItems(String userId, final OnCartItemDeleteListener callback){
        // use batch delete
        WriteBatch batch = db.batch();
        db.collection("users").document(userId).collection("cart").get().addOnSuccessListener(queryDocumentSnapshots -> {
            // delete each of the documents
            for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                batch.delete(snapshot.getReference());
            }

            batch.commit().addOnSuccessListener(aVoid -> {
                callback.onCartItemDeleted(true);
            }).addOnFailureListener(e -> {
                callback.onCartItemDeleted(false);
            });
        });
    }

    // returns loaded cart items to the view model
   public interface OnCartItemsLoadedListener {
        void onCartItemsLoaded(List<CartItem> cartItems);
    }

    // listens for cart item added success
    public interface OnCartItemAddedListener {
        void onCartItemAdded(boolean success);
    }

    // listener for cart update success
    public interface OnCartItemUpdatedListener {
        void onCartItemUpdated(boolean success);
    }
    public interface OnCartItemDeleteListener{
        void onCartItemDeleted(boolean success);
    }
}
