package com.example.farm2door.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.CartItem;
import com.example.farm2door.models.PaymentItem;
import com.example.farm2door.models.Product;
import com.example.farm2door.repository.CartRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartViewModel extends ViewModel {

    private CartRepository cartRepository;
    String loggedInUserId;
    LoadingViewModel loadingViewModel;
    private MutableLiveData<Boolean> cartItemAddSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCartItemsDeleted = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDeliveryFeesUpdated = new MutableLiveData<>(false);
    private MutableLiveData<List<PaymentItem>> costsPerFarmer = new MutableLiveData<>();
    private Map<String, CartItem> cartItems = new HashMap<>();
    public CartViewModel() {
        cartRepository = new CartRepository();
        loadingViewModel = LoadingViewModel.getInstance();
        loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public LiveData<Map<String, CartItem>> getCartItems() {
        return  cartRepository.getCartItems();
    }
    public LiveData<Integer> getTotalAmount() {
        return cartRepository.getTotalAmount();
    }
    public LiveData<Boolean> getIsCartItemsDeleted() {
        return isCartItemsDeleted;
    }

    public LiveData<Boolean> getCartItemAddSuccess() {
        return  cartItemAddSuccess;
    }
    public LiveData<Boolean> getIsDeliveryFeesUpdated(){
        return isDeliveryFeesUpdated;
    }
    public LiveData<List<PaymentItem>> getCostsPerFarmer(){return costsPerFarmer;}

    // load cart items from firebase
    public void fetchCartItems() {
        loadingViewModel.setLoading(true);
        cartRepository.getUserCartItems(loggedInUserId, cartItems -> {
            HashMap<String, CartItem> cartItemsMap = new HashMap<>();
            int totalAmount = 0;
            for(CartItem cartItem: cartItems){
                cartItemsMap.put(cartItem.getProductId(), cartItem);
                totalAmount += cartItem.getProductTotalPrice();
            }

            this.cartItems = cartItemsMap;

            cartRepository.setCartItems(cartItemsMap);
            cartRepository.setTotalAmount(totalAmount);
            loadingViewModel.setLoading(false);
            // calculate costs on items load
            this.calculateCostsPerFarmer(cartItemsMap);
        });
    }

    // add an item to the cart
    public void addItemToCart(Product product){
        CartItem cartItem = new CartItem(product.getProductId(), product.getName(), product.getPrice(), product.getUnitName(), product.getImages().get(0), product.getFarmerId());
        cartItem.setFarmerName(product.getFarmerName());
        //  if item already in cart, increase it's quantity
        if(cartItems.get(cartItem.getProductId()) != null){
            increaseQuantity(cartItem);
            cartItemAddSuccess.setValue(true);
            return;
        }

        cartItems.put(cartItem.getProductId(), cartItem);
        updateCartLiveData();
        loadingViewModel.setLoading(true);
        cartRepository.addItemToCart(loggedInUserId, cartItem, success -> {
            loadingViewModel.setLoading(false);
            cartItemAddSuccess.setValue(success);
        });
    }

    // increase quantity of an item
    public void increaseQuantity(CartItem cartItem){
        cartItems.get(cartItem.getProductId()).setProductQuantity(cartItem.getProductQuantity()+1);
        cartItems.get(cartItem.getProductId()).setProductTotalPrice(cartItem.getProductPrice()*cartItem.getProductQuantity());
        updateCartLiveData();

        loadingViewModel.setLoading(true);
        cartRepository.updateCartItem(loggedInUserId, cartItem, success -> {
            loadingViewModel.setLoading(false);
        });
    }

    // decrease quantity of an item
    public void decreaseQuantity(CartItem cartItem){
        // if quantity is 1, delete the item
        if(cartItem.getProductQuantity() == 1){
            deleteCartItem(cartItem);
            return;
        }

        // decrease quantity otherwise
        cartItems.get(cartItem.getProductId()).setProductQuantity(cartItem.getProductQuantity()-1);
        cartItems.get(cartItem.getProductId()).setProductTotalPrice(cartItem.getProductPrice()*cartItem.getProductQuantity());

        updateCartLiveData();

        loadingViewModel.setLoading(true);
        cartRepository.updateCartItem(loggedInUserId, cartItem, success -> {
            loadingViewModel.setLoading(false);
        });
    }

    // delete cart item
    private void deleteCartItem(CartItem itemToRemove){
        cartItems.remove(itemToRemove.getProductId());
        updateCartLiveData();
        // delete from database too
        loadingViewModel.setLoading(true);
        cartRepository.deleteCartItem(loggedInUserId, itemToRemove.getProductId(), success -> {
            loadingViewModel.setLoading(false);
        });
    }

    // clear cart items after order is placed successfully
    public void deleteCartItems(){
        loadingViewModel.setLoading(true);
        cartRepository.deleteCartItems(loggedInUserId, success -> {

            loadingViewModel.setLoading(false);
            isCartItemsDeleted.setValue(success);
        });
    }

    private void updateCartLiveData() {
        cartRepository.setCartItems(cartItems);
        int totalAmount = 0;

        for(CartItem cartItem : cartItems.values()){
            totalAmount += cartItem.getProductTotalPrice();
        }
        // set total amount observable
        cartRepository.setTotalAmount(totalAmount);
        calculateCostsPerFarmer(cartItems);
    }

    // update delivery fees for an item
    public void updateDeliveryFees(String productId, double deliveryFees){
        loadingViewModel.setLoading(true);
        cartRepository.updateDeliveryFees(loggedInUserId, productId, deliveryFees, success -> {
            loadingViewModel.setLoading(false);
            isDeliveryFeesUpdated.setValue(success);
            if(success){
                cartItems.get(productId).setDeliveryFees(deliveryFees);
                updateCartLiveData();
            }
        });
    }

    // calculate summary costs per each farmer whose item is in the cart
    // delivery cost per farmer is the max delivery cost for all items from that farmer
    private void calculateCostsPerFarmer(Map<String, CartItem> cartItems) {
        List<PaymentItem> paymentItemList = new ArrayList<>();
        // get unique farmers only
        Set<String> uniqueFarmers = new HashSet<>();
        for (CartItem item: cartItems.values()){
            uniqueFarmers.add(item.getFarmerId());
        }

        // get max delivery fees for each farmer + cost of items from that farmer
        for(String farmerId: uniqueFarmers){
            // get max deliverFees from CartItem
            CartItem withMaxDelivery = null;
            double itemsTotalCost = 0;

            for(CartItem item: cartItems.values()){
                if(item.getFarmerId().equals(farmerId)){
                    itemsTotalCost += item.getProductTotalPrice();

                    if(withMaxDelivery == null) withMaxDelivery = item;
                    if (item.getDeliveryFees() > withMaxDelivery.getDeliveryFees()){
                        withMaxDelivery = item;
                    }
                }
            }

            // set the information in the map
            if(withMaxDelivery != null){
                PaymentItem paymentItem = new PaymentItem();
                paymentItem.setFarmerId(withMaxDelivery.getFarmerId());
                paymentItem.setFarmerName(withMaxDelivery.getFarmerName());
                paymentItem.setDeliveryFees(withMaxDelivery.getDeliveryFees());
                paymentItem.setItemsTotalCost(itemsTotalCost);
                paymentItemList.add(paymentItem);
            }
        }

        // set to the observable
        costsPerFarmer.setValue(paymentItemList);
    }
}
