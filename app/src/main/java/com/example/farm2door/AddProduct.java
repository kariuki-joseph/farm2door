package com.example.farm2door;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.farm2door.databinding.ActivityAddProductBinding;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.models.Product;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddProduct extends AppCompatActivity {

    ActivityAddProductBinding binding;
    String name, description, price, unitName, totalInStock;
    List<Uri> images;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private static int imagesAdded = 0;
    LoadingViewModel loadingViewModel;
    ProductViewModel productViewModel;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "Add Product", true);
        loadingViewModel = LoadingViewModel.getInstance();
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // set up the activity result launcher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if (data != null){
                    Uri imageUri = data.getData();
                    if (imageUri != null){
                        if(images == null){
                            images = new ArrayList<>(3);
                        }
                        if(images.size() < 3){
                            images.add(imageUri);
                            imagesAdded++;
                            // set the image to the image view
                            if(imagesAdded == 1){
                                binding.img1.setImageURI(imageUri);
                            }else if(imagesAdded == 2){
                                binding.img2.setImageURI(imageUri);
                            }else if(imagesAdded == 3) {
                                binding.img3.setImageURI(imageUri);
                            }
                        }
                        // disable the button if the user has added 3 images
                        if(images.size() == 3){
                            binding.btnSelectImages.setEnabled(false);
                        }

                    }
                }
            }
        });

        // open camera when user clicks on the upload icon
        binding.btnSelectImages.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });


        // add the product to the database
        binding.btnAddProduct.setOnClickListener(v -> {
            name = binding.productName.getText().toString();
            description = binding.productDescription.getText().toString();
            price = binding.price.getText().toString();
            unitName = binding.unitName.getText().toString();
            totalInStock = binding.inStock.getText().toString();

            // validate that there are no errors in the inputs
            if(!validateInputs()){
                return;
            }

            product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(Double.valueOf(price));
            product.setUnitName(unitName);
            product.setTotalInStock(Integer.valueOf(totalInStock));

            // upload the product
            productViewModel.uploadProduct(product, images);
        });

        // observe for loading status
        loadingViewModel.getIsLoading().observe(this, isLoading -> {
            if(isLoading){
                binding.btnAddProduct.setEnabled(false);
                binding.btnAddProduct.setText("Uploading...");
                binding.progressBarLayout.progressBar.setVisibility(View.VISIBLE);
            }else{
                binding.btnAddProduct.setEnabled(true);
                binding.btnAddProduct.setText("Add Product");
                binding.progressBarLayout.progressBar.setVisibility(View.GONE);
            }
        });

        // observe for product upload success
        productViewModel.getProductUploadSuccess().observe(this, success -> {
            if(success){
                Toast.makeText(this, "Product uploaded successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // observe for errors
        productViewModel.getException().observe(this, e -> {
            if(e != null){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    // validate the inputs
    private boolean validateInputs(){
        if(name.isEmpty()){
            binding.productName.setError("Product name is required");
            binding.productName.requestFocus();
            return false;
        }
        if(description.isEmpty()){
            binding.productDescription.setError("Product description is required");
            binding.productDescription.requestFocus();
            return false;
        }
        if(price.isEmpty()){
            binding.price.setError("Product price is required");
            binding.price.requestFocus();
            return false;
        }
        if(unitName.isEmpty()){
            binding.unitName.setError("Product unit name is required");
            binding.unitName.requestFocus();
            return false;
        }
        if(totalInStock.isEmpty()){
            binding.inStock.setError("Product quantity is required");
            binding.inStock.requestFocus();
            return false;
        }
        if(images.size() < 3){
            Toast.makeText(this, "Please select 3 images", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}