package com.example.farm2door;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stkpush.Mode;
import com.example.stkpush.api.response.STKPushResponse;
import com.example.stkpush.interfaces.STKListener;
import com.example.stkpush.interfaces.TokenListener;
import com.example.stkpush.model.Mpesa;
import com.example.stkpush.model.STKPush;
import com.example.stkpush.model.Token;
import com.example.stkpush.model.Transaction;

import java.io.UnsupportedEncodingException;

public class MakePayments extends AppCompatActivity implements TokenListener {
    private Mpesa mpesa;

    private EditText phoneNumberEditText;
     private  EditText amountEditText;
    private Button payButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.makepayments);


        phoneNumberEditText = findViewById(R.id.phoneNumber);
        amountEditText = findViewById(R.id.amount);
        payButton = findViewById(R.id.btnPay);
        progressBar = findViewById(R.id.progressBarLayout).findViewById(R.id.progressBar);

        mpesa = new Mpesa(Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Mode.SANDBOX);

        // Set click listener for the pay button
        payButton.setOnClickListener(v -> {
            // Call a method to handle payment
            startMpesa();
        });
    }

    public  void startMpesa () {
        progressBar.setVisibility(View.VISIBLE);

        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String amount = amountEditText.getText().toString().trim();

        if ( phoneNumber.isEmpty()) {
            Toast.makeText(MakePayments.this, "Phone Number is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount.isEmpty()) {
            Toast.makeText(MakePayments.this, "Amount is required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
           // sweetAlertDialog.show();
            mpesa.getToken(this);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException: " + e.getLocalizedMessage());
        }

    }

    @Override
    public void onTokenSuccess(Token token) {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String amount = amountEditText.getText().toString().trim();

        STKPush stkPush = new STKPush();
        stkPush.setBusinessShortCode(Config.BUSINESS_SHORT_CODE);
        stkPush.setPassword(STKPush.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, STKPush.getTimestamp()));
        stkPush.setTimestamp(STKPush.getTimestamp());
        stkPush.setTransactionType(Transaction.CUSTOMER_PAY_BILL_ONLINE);
        stkPush.setAmount(amount);
        stkPush.setPartyA(STKPush.sanitizePhoneNumber(phoneNumber));
        stkPush.setPartyB(Config.PARTYB);
        stkPush.setPhoneNumber(STKPush.sanitizePhoneNumber(phoneNumber));
        stkPush.setCallBackURL(Config.CALLBACKURL);
        stkPush.setAccountReference("KChama");
        stkPush.setTransactionDesc("some description");

        mpesa.startStkPush(token, stkPush, new STKListener() {
            @Override
            public void onResponse(STKPushResponse stkPushResponse) {
                String message = "Please enter your pin to complete transaction";
                // Handle the response
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "stk onError: " + throwable.getMessage());
                // Handle the error
            }
        });
    }

    @Override
    public void OnTokenError(Throwable throwable) {
    }
    }