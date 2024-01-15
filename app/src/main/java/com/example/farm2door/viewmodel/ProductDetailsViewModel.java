package com.example.farm2door.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.Product;
import com.example.farm2door.repository.ProductRepository;

public class ProductDetailsViewModel extends ViewModel {

    private MutableLiveData<Product> productLiveData = new MutableLiveData<>();
    private MutableLiveData<Exception> exceptionMutableLiveData = new MutableLiveData<>();
    private ProductRepository productRepository;
    LoadingViewModel loadingViewModel;
    public ProductDetailsViewModel() {
        productRepository = new ProductRepository();
        loadingViewModel = LoadingViewModel.getInstance();
    }

    public LiveData<Product> getProduct() {
        return productLiveData;
    }



    // get a single product from repository
    public void fetchProduct(String productId){
        loadingViewModel.setLoading(true);
        productRepository.getProduct(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product loadedProduct) {
                loadingViewModel.setLoading(false);
                productLiveData.setValue(loadedProduct);
            }
            @Override
            public void onError(Exception e) {
                loadingViewModel.setLoading(false);
                exceptionMutableLiveData.setValue(e);
            }
        });
    }
}
