package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.farm2door.adapters.CustomerFeedbackAdapter;
import com.example.farm2door.adapters.ImagePagerAdapter;
import com.example.farm2door.databinding.ActivityProductDetailsBinding;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.models.CustomerFeedback;
import com.example.farm2door.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDetails extends AppCompatActivity implements OnRecyclerItemClickListener {

    ActivityProductDetailsBinding binding;

    List<CustomerFeedback> customerFeedbacks;
    Product receivedProduct;
    List<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // enable toolbar
        ToolBarHelper.setupToolBar(this, binding.toolbarLayout.toolbar, "Product Details", true);

        receivedProduct = (Product) getIntent().getSerializableExtra("product");

        binding.productName.setText(receivedProduct.getName());
        binding.productPrice.setText(String.format("Ksh. %s", receivedProduct.getPrice()));
//        binding.productDescription.setText(receivedProduct.getDescription());

        imageUrls = getImageUrls();
        // load image adapter for our carousel(ViewPager)
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, imageUrls);
        binding.viewPager.setAdapter(imagePagerAdapter);

        // handle previous and next button clicks
        binding.btnPrev.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            if (currentItem > 0) {
                binding.viewPager.setCurrentItem(currentItem - 1);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            if (currentItem < imageUrls.size() - 1) {
                binding.viewPager.setCurrentItem(currentItem + 1);
            }
        });

        customerFeedbacks = createCustomerFeedbacks();
        // Layout manager for our recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerview.setLayoutManager(layoutManager);
        // Adapter for our recyclerview
        CustomerFeedbackAdapter adapter = new CustomerFeedbackAdapter(this, customerFeedbacks,this);
        // Set the recyclerview to read data from our adapter
        binding.recyclerview.setAdapter(adapter);



        // add product to cart
        binding.btnAddToCart.setOnClickListener(v -> {
            startActivity(new Intent(this, MyCart.class));
        });
    }

    private List<CustomerFeedback> createCustomerFeedbacks() {
        List<CustomerFeedback> feedbacks = new ArrayList<>();
        feedbacks.add(new CustomerFeedback("Jim Cook", "This is a very good product", 4.5f, "18/12/2023"));
        feedbacks.add(new CustomerFeedback("Alex Murimi", "Lorem dolor", 4.5f, "15/12/2020"));
        feedbacks.add(new CustomerFeedback("Geoffrey Maina", "Delivery in time", 4.5f, "18/12/2023"));
        feedbacks.add(new CustomerFeedback("Alexander Okoth", "Fair price. I love this", 4.5f, "11/12/2023"));
        feedbacks.add(new CustomerFeedback("Anita Gee", "Package delivered in good condition", 4.5f, "14/12/2023"));
        feedbacks.add(new CustomerFeedback("Alice Kimani", "The package was very fresh. I recommend", 4.5f, "14/12/2023"));
        feedbacks.add(new CustomerFeedback("Joyce Kamau", "Polite and friendly", 4.5f, "14/12/2023"));

        return feedbacks;
    }


    @Override
    public void onItemClick(int position) {
        CustomerFeedback customerFeedback = customerFeedbacks.get(position);
        Toast.makeText(this, customerFeedback.getCustomerName(), Toast.LENGTH_SHORT).show();
    }

    private List<String> getImageUrls() {
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://www.shutterstock.com/image-photo/various-dairy-products-600nw-627224804.jpg");
        imageUrls.add("https://www.shutterstock.com/image-photo/various-dairy-products-600nw-627224804.jpg");
        imageUrls.add("https://www.shutterstock.com/image-photo/various-dairy-products-600nw-627224804.jpg");
        imageUrls.add("https://www.shutterstock.com/image-photo/various-dairy-products-600nw-627224804.jpg");
        imageUrls.add("https://www.shutterstock.com/image-photo/various-dairy-products-600nw-627224804.jpg");
        imageUrls.add("https://www.shutterstock.com/image-photo/various-dairy-products-600nw-627224804.jpg");
        return imageUrls;
    }
}