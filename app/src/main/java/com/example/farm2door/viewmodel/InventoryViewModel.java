package com.example.farm2door.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.InventoryItem;
import com.example.farm2door.models.Product;
import com.example.farm2door.repository.AuthRepository;
import com.example.farm2door.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class InventoryViewModel extends ViewModel {
    ProductRepository productRepository;
    LoadingViewModel loadingViewModel;
    private MutableLiveData<List<InventoryItem>> inventoryItems = new MutableLiveData<>();

    public InventoryViewModel() {
        productRepository = new ProductRepository();
        loadingViewModel = LoadingViewModel.getInstance();
    }

    public LiveData<List<InventoryItem>> getInventoryItems() {
        return inventoryItems;
    }


    public void fetchInventoryItems(String farmerId) {
        loadingViewModel.setLoading(true);
        productRepository.getFarmerProducts(farmerId, productList -> {
            loadingViewModel.setLoading(false);
            List<InventoryItem> items = new ArrayList<>();

            if(productList != null){
                for(Product product: productList){
                    InventoryItem item = new InventoryItem();
                    item.setProductId(product.getProductId());
                    item.setName(product.getName());
                    item.setPrice(product.getPrice());
                    item.setRemainingQuantity(product.getTotalInStock());
                    item.setUnitName(product.getUnitName());
                    item.setImageURL(product.getImages().get(0));

                    items.add(item);
                }
            }

            inventoryItems.setValue(items);
        });
    }


    public void deleteInventoryItem(InventoryItem inventoryItem){
        loadingViewModel.setLoading(true);
        productRepository.deleteProduct(inventoryItem.getProductId(), isDeleted -> {
            loadingViewModel.setLoading(false);
            if(isDeleted){
                fetchInventoryItems(AuthRepository.getLoggedInUserId());
            }
        });
    }

}
