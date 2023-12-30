package com.example.farm2door.viewmodel;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.farm2door.models.Product;
import com.example.farm2door.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;


public class ProductViewModel extends ViewModel {
    private LoadingViewModel loadingViewModel;
    private ProductRepository productRepository;

    private MutableLiveData<Boolean> productUploadSuccess = new MutableLiveData<>();
    private MutableLiveData<Exception> exception = new MutableLiveData<>();
    // keep track of uploaded images count
    int uploadedImagesCount = 0;
    List<String> urls = new ArrayList<>();

    public ProductViewModel() {
        productRepository = new ProductRepository();
        loadingViewModel = LoadingViewModel.getInstance();
    }

    // observe this to know if the product upload was successful
    public LiveData<Boolean> getProductUploadSuccess() {
        return productUploadSuccess;
    }

    // observe for errors in the product upload process
    public LiveData<Exception> getException() {
        return exception;
    }

    public void uploadProduct(Product product, List<Uri> images) {
        // upload images first
        loadingViewModel.setLoading(true);
        for (Uri image : images) {
            productRepository.uploadImage(image, new ProductRepository.ImageCallback() {
                @Override
                public void onSuccess(String url) {
                    uploadedImagesCount++;
                    urls.add(url);

                    if (uploadedImagesCount == images.size()) {
                        // upload product new
                        product.setImages(urls);
                        uploadProduct(product);
                    }
                }
                @Override
                public void onError(Exception e) {
                    loadingViewModel.setLoading(false);
                    exception.setValue(e);
                }
            });
        }
    }

    // upload product to FireStore db
    private void uploadProduct(Product product){
        loadingViewModel.setLoading(true);
        productRepository.uploadProduct(product, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                loadingViewModel.setLoading(false);
                productUploadSuccess.setValue(true);
            }

            @Override
            public void onError(Exception e) {
                loadingViewModel.setLoading(false);
                exception.setValue(e);
            }
        });
    }
}
