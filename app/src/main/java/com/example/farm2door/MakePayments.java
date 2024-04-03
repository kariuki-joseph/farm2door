package com.example.farm2door;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm2door.adapters.PaymentItemsAdapter;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.models.CartItem;
import com.example.farm2door.models.PaymentItem;
import com.example.farm2door.viewmodel.CartViewModel;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.PlaceOrderViewModel;
import com.example.stkpush.Mode;
import com.example.stkpush.api.response.STKPushResponse;
import com.example.stkpush.interfaces.STKListener;
import com.example.stkpush.interfaces.TokenListener;
import com.example.stkpush.model.Mpesa;
import com.example.stkpush.model.STKPush;
import com.example.stkpush.model.Token;
import com.example.stkpush.model.Transaction;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MakePayments extends AppCompatActivity {
    private Mpesa mpesa;

    private EditText edtMpesaNumber;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;
    RecyclerView paymentsRecyclerView;
    CartViewModel cartViewModel;
    List<PaymentItem> paymentItemList;
    PaymentItemsAdapter adapter;
    String mPesaNumber="";
    String amount = "1";
    String orderNumber = "";
    int farmersPaid = 0; // keep track of farmers who have been paid
    PlaceOrderViewModel placeOrderViewModel;
    LoadingViewModel loadingViewModel;
    List<CartItem> cartItemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makepayments);

        View includedToolbar = findViewById(R.id.toolbarPayments);
        Toolbar toolbar = includedToolbar.findViewById(R.id.toolbarLayout);

//        ToolBarHelper.setupToolBar(this, toolbar, "Make Payments", true);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        placeOrderViewModel = new ViewModelProvider(this).get(PlaceOrderViewModel.class);
        loadingViewModel = LoadingViewModel.getInstance();
        progressBar = findViewById(R.id.progressBarLayout).findViewById(R.id.progressBar);
        edtMpesaNumber = findViewById(R.id.mPesaNumber);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        paymentsRecyclerView = findViewById(R.id.paymentsRecyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        paymentsRecyclerView.setLayoutManager(linearLayoutManager);
        paymentsRecyclerView.setHasFixedSize(true);

        mpesa = new Mpesa(Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Mode.SANDBOX);

        paymentItemList = new ArrayList<>();

        adapter = new PaymentItemsAdapter(this, paymentItemList, (holder, totalAmount) ->{

            mPesaNumber = edtMpesaNumber.getText().toString().trim();
            if(mPesaNumber.isEmpty()){
                edtMpesaNumber.setError("Please enter your M-Pesa Number");
                edtMpesaNumber.requestFocus();
                return;
            }
            if(mPesaNumber.length() <10 || mPesaNumber.length() > 13){
                edtMpesaNumber.setError("Invalid M-Pesa Number");
                edtMpesaNumber.requestFocus();
                return;
            }

            holder.progressBar.setVisibility(View.VISIBLE);
            holder.btnPay.setText("Paying...");

            if(totalAmount == 0){
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            amount = String.valueOf((int) totalAmount);
            // initiate M-Pesa STK by getting access token first
            progressBar.setVisibility(View.VISIBLE);
            try {
                mpesa.getToken(new TokenListener() {
                    @Override
                    public void onTokenSuccess(Token token) {
                        STKPush stkPush = new STKPush();
                        stkPush.setBusinessShortCode(Config.BUSINESS_SHORT_CODE);
                        stkPush.setPassword(STKPush.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, STKPush.getTimestamp()));
                        stkPush.setTimestamp(STKPush.getTimestamp());
                        stkPush.setTransactionType(Transaction.CUSTOMER_PAY_BILL_ONLINE);
                        stkPush.setAmount(amount);
                        stkPush.setPartyA(STKPush.sanitizePhoneNumber(mPesaNumber));
                        stkPush.setPartyB(Config.PARTYB);
                        stkPush.setPhoneNumber(STKPush.sanitizePhoneNumber(mPesaNumber));
                        stkPush.setCallBackURL(Config.CALLBACKURL);
                        stkPush.setAccountReference("Farm2Door");
                        stkPush.setTransactionDesc("some description");

                        mpesa.startStkPush(token, stkPush, new STKListener() {
                            @Override
                            public void onResponse(STKPushResponse stkPushResponse) {
                                String message = "Please enter your pin to complete transaction";
                                // Handle the response
                                progressBar.setVisibility(View.GONE);
                                holder.btnPay.setText("Resend");
                                // increase the number of farmers paid
                                farmersPaid++;
                                if(farmersPaid == paymentItemList.size()){
                                    // enable place order button
                                    btnPlaceOrder.setEnabled(true);
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Log.e(TAG, "stk onError: " + throwable.getMessage());
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(MakePayments.this, "Error making payment: "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                holder.btnPay.setText("Pay");
                                // Handle the error
                            }
                        });
                    }

                    @Override
                    public void OnTokenError(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                        holder.btnPay.setText("Pay");
                        Toast.makeText(getBaseContext(), "Error making payments: "+throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        paymentsRecyclerView.setAdapter(adapter);

        // listen for loading
        loadingViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading? View.VISIBLE: View.GONE);
        });

        // listen for payment items
        cartViewModel.getCostsPerFarmer().observe(this, paymentItems -> {
            paymentItemList.clear();
            paymentItemList.addAll(paymentItems);
            adapter.notifyDataSetChanged();
        });

        // listen for cart items load success
        cartViewModel.getCartItems().observe(this, cartItems -> {
            if (cartItems == null) {
                Toast.makeText(this, "Error loading cart items", Toast.LENGTH_SHORT).show();
                return;
            }

            cartItemList = new ArrayList<>(cartItems.values());
        });

        // observe when order number has been generated
        placeOrderViewModel.getOrderNumber().observe(this, orderNumber -> {
            if (orderNumber == null) {
                Toast.makeText(this, "An error has occurred placing your order! Please try again", Toast.LENGTH_LONG).show();
                finish();
            }

            this.orderNumber = orderNumber;
            // clear cart items now since the order has been placed successfully
            cartViewModel.deleteCartItems();
        });

        // order placement complete after cart item has been cleared
        cartViewModel.getIsCartItemsDeleted().observe(this, isDeleted -> {
            if (isDeleted) {
                btnPlaceOrder.setText("Order Placed");

                Intent intent = new Intent(MakePayments.this, OrderSuccess.class);
                intent.putExtra("orderNumber", orderNumber); // take the first item in the order
                intent.putExtra("farmerId", cartItemList.get(0).getFarmerId());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to delete all cart items", Toast.LENGTH_SHORT).show();
            }
        });

        // checkout button click listener
        btnPlaceOrder.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            btnPlaceOrder.setText("Placing Order...");
            // place order
            if(cartItemList.isEmpty()){
                Toast.makeText(this, "No items in cart", Toast.LENGTH_SHORT).show();
                return;
            }

            placeOrderViewModel.generateAndPlaceOrders(cartItemList);
        });


        // load cart items
        cartViewModel.fetchCartItems();
    }
}