package com.example.farm2door.repository;

import android.net.Uri;

import com.example.farm2door.models.OrderItem;
import com.example.farm2door.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProductRepository {

    FirebaseFirestore db;
    FirebaseStorage storage;
    public ProductRepository(){
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void fetchProducts(final OnProductsReceived callback){
        db.collection("products").get().addOnSuccessListener(queryDocumentSnapshots -> {
            callback.onReceived(queryDocumentSnapshots.toObjects(Product.class));
        }).addOnFailureListener(e -> {
            callback.onReceived(null);
        });
    }

    public void uploadProduct(Product product, final ProductCallback callback){
        String productId = db.collection("products").document().getId();
        product.setProductId(productId);
        db.collection("products").document(productId).set(product).addOnSuccessListener(aVoid -> {
            callback.onSuccess(product);
        }).addOnFailureListener(e -> {
            callback.onError(e);
        });
    }

    public void uploadImage(Uri imageUri, final ImageCallback callback){
        StorageReference storageRef = storage.getReference().child("images/" +System.currentTimeMillis()+imageUri.getLastPathSegment());
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                callback.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                callback.onError(e);
            });
        }).addOnFailureListener(e -> {
            callback.onError(e);
        });
    }

    public void getFarmerProducts(String farmerId, final OnProductsReceived callback){
        db.collection("products").whereEqualTo("farmerId", farmerId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
            callback.onReceived(productList);
        }).addOnFailureListener(e -> {
            callback.onReceived(null);
        });
    }

    public void getProduct(String productId, final ProductCallback callback){
        db.collection("products").document(productId).get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            callback.onSuccess(product);
        }).addOnFailureListener(e -> {
            callback.onError(e);
        });
    }

    public void deleteProduct(String productId, final OnProductDeleted callback){
        db.collection("products").document(productId).delete().addOnSuccessListener(aVoid -> {
            // delete all orders associated with this product too
            db.collection("orders").whereEqualTo("productId", productId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<OrderItem> orderItemsList = queryDocumentSnapshots.toObjects(OrderItem.class);
                int count = 0;
                for (OrderItem orderItem : orderItemsList){
                    db.collection("orders").document(orderItem.getId()).delete();

                    if(++count == orderItemsList.size()) {
                        callback.onDeleted(true);
                    }
                }
            });
        }).addOnFailureListener(e -> {
            callback.onDeleted(false);
        });
    }

    public interface ProductCallback{
        void onSuccess(Product product);
        void onError(Exception e);
    }

    public interface ImageCallback{
        void onSuccess(String url);
        void onError(Exception e);
    }

    public interface OnProductsReceived{
        void onReceived(List<Product> productList);
    }

    public interface OnProductDeleted{
        void onDeleted(boolean isDeleted);
    }
}
