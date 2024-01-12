package com.example.farm2door.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.InventoryItem;
import com.example.farm2door.models.Product;
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
                    InventoryItem item = new InventoryItem(product.getName(), product.getPrice(), product.getTotalInStock(), product.getUnitName(), product.getImages().get(0));
                    items.add(item);
                }
            }

            inventoryItems.setValue(items);
        });
    }

}
