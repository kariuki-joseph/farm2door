package com.example.farm2door.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.CartItem;
import com.example.farm2door.models.Product;
import com.example.farm2door.repository.CartRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class CartViewModel extends ViewModel {

    private CartRepository cartRepository;
    String loggedInUserId;
    LoadingViewModel loadingViewModel;
    private MutableLiveData<Boolean> cartItemAddSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCartItemsDeleted = new MutableLiveData<>();
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

    // load cart items from firebase
    public void fetchCartItems() {
        loadingViewModel.setLoading(true);
        cartRepository.getUserCartItems(loggedInUserId, cartItems -> {
            HashMap<String, CartItem> cartItemsMap = new HashMap<>();
            int totalAmount = 0;
            for(CartItem cartItem: cartItems){
                cartItemsMap.put(cartItem.getId(), cartItem);
                totalAmount += cartItem.getProductTotalPrice();
            }

            this.cartItems = cartItemsMap;
            cartRepository.setCartItems(cartItemsMap);
            cartRepository.setTotalAmount(totalAmount);
            loadingViewModel.setLoading(false);
        });
    }

    // add an item to the cart
    public void addItemToCart(Product product){
        CartItem cartItem = new CartItem(product.getProductId(), product.getName(), product.getPrice(), product.getUnitName(), product.getImages().get(0), product.getFarmerId());
        //  if item already in cart, increase it's quantity
        if(cartItems.get(cartItem.getId()) != null){
            increaseQuantity(cartItem);
            cartItemAddSuccess.setValue(true);
            return;
        }

        cartItems.put(cartItem.getId(), cartItem);
        updateCartLiveData();
        loadingViewModel.setLoading(true);
        cartRepository.addItemToCart(loggedInUserId, cartItem, success -> {
            loadingViewModel.setLoading(false);
            cartItemAddSuccess.setValue(success);
        });
    }

    // increase quantity of an item
    public void increaseQuantity(CartItem cartItem){
        cartItems.get(cartItem.getId()).setProductQuantity(cartItem.getProductQuantity()+1);
        cartItems.get(cartItem.getId()).setProductTotalPrice(cartItem.getProductPrice()*cartItem.getProductQuantity());
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
        cartItems.get(cartItem.getId()).setProductQuantity(cartItem.getProductQuantity()-1);
        cartItems.get(cartItem.getId()).setProductTotalPrice(cartItem.getProductPrice()*cartItem.getProductQuantity());

        updateCartLiveData();

        loadingViewModel.setLoading(true);
        cartRepository.updateCartItem(loggedInUserId, cartItem, success -> {
            loadingViewModel.setLoading(false);
        });
    }

    // delete cart item
    private void deleteCartItem(CartItem itemToRemove){
        cartItems.remove(itemToRemove.getId());
        updateCartLiveData();
        // delete from database too
        loadingViewModel.setLoading(true);
        cartRepository.deleteCartItem(loggedInUserId, itemToRemove.getId(), success -> {
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
       cartRepository.setTotalAmount(totalAmount);
    }
}
