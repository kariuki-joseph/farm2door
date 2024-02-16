package com.example.farm2door;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.farm2door.databinding.ActivityAddProductBinding;
import com.example.farm2door.helpers.LocationManagerHelper;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.models.Product;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.ProductViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddProduct extends AppCompatActivity {
    ActivityAddProductBinding binding;
    String name, description, price, unitName, totalInStock;
    double latitude = 0, longitude = 0;
    List<Uri> images;
    private static int imagesAdded = 0;
    LoadingViewModel loadingViewModel;
    ProductViewModel productViewModel;
    private Product product;
    FirebaseUser firebaseUser;

    // take images using camera
    String currentPhotoPath;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private final int UPLOAD_IMAGE_REQUEST_CODE = 2;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "Add Product", true);
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
                    }
                }
            }
        });

        loadingViewModel = LoadingViewModel.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        images = new ArrayList<>(3);
        imagesAdded = 0;

        // request for camera permissions
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        // add location of the farmer
        binding.btnAddLocation.setOnClickListener(v -> {
            LocationManagerHelper locationManagerHelper = new LocationManagerHelper(this, location -> {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                binding.btnAddLocation.setTextColor(Color.WHITE);
                binding.btnAddLocation.getIcon().setTint(Color.GREEN);
                binding.btnAddLocation.setText("Location Added");
            });

            locationManagerHelper.requestSingleLocationUpdate();
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
            product.setFarmerId(firebaseUser.getUid());
            product.setDescription(description);
            product.setPrice(Double.valueOf(price));
            product.setUnitName(unitName);
            product.setTotalInStock(Integer.valueOf(totalInStock));
            product.setLatitude(latitude);
            product.setLongitude(longitude);

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

        // take picture using camera
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success) {
                // Image capture successful, display the captured image
                displayCapturedImage();
            } else {
                // Image capture canceled or failed
                Toast.makeText(AddProduct.this, "Image capture canceled or failed", Toast.LENGTH_SHORT).show();
            }
        });


        // open camera when user clicks on the capture icon
        binding.btnCaptureImages.setOnClickListener(v -> {
            captureImage();
        });

        // select image from gallery
        binding.btnUploadImages.setOnClickListener(v -> {
            if (imagesAdded == 3){
                Toast.makeText(this, "Maximum number of photos reached", Toast.LENGTH_SHORT).show();
                return;
            }

           Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
           intent.setType("image/*");
           if(intent.resolveActivity(getPackageManager()) == null){
               Toast.makeText(this, "No app found to handle pick image action", Toast.LENGTH_SHORT).show();
               return;
           }
           pickImageLauncher.launch(intent);
        });

    }

    private void captureImage() {
        // Create a temporary file to store the captured image
        Uri photoUri = createImageFile();
        if (imagesAdded == 3){
            Toast.makeText(this, "Maximum number of photos reached", Toast.LENGTH_SHORT).show();
            return;
        }
        // Launch the camera app to capture an image
        takePictureLauncher.launch(photoUri);
    }
    private Uri createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save the path for use with ACTION_VIEW intents
            currentPhotoPath = imageFile.getAbsolutePath();

            return FileProvider.getUriForFile(this, "com.example.farm2door.fileprovider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayCapturedImage() {
        // Display the captured image in the ImageView
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        if(imagesAdded < 3){
            images.add(Uri.fromFile(new File(currentPhotoPath)));
            imagesAdded++;
        }

        if(imagesAdded == 1){
            binding.img1.setImageBitmap(bitmap);
        }else if (imagesAdded == 2){
            binding.img2.setImageBitmap(bitmap);
        }else {
            binding.img3.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                captureImage();
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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
            Toast.makeText(this, "Please capture 3 images of this product"+images.size(), Toast.LENGTH_LONG).show();
            return false;
        }
        if(latitude == 0 || longitude == 0){
            Toast.makeText(this, "Please add Location", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}