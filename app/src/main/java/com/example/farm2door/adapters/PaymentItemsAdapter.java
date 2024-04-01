package com.example.farm2door.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm2door.R;
import com.example.farm2door.models.PaymentItem;

import java.util.List;

public class PaymentItemsAdapter extends RecyclerView.Adapter<PaymentItemsAdapter.PaymentItemViewHolder> {
    private Context context;
    private List<PaymentItem> paymentItemList;
    private OnPayButtonClickListener payButtonClickListener;
    public PaymentItemsAdapter(Context context, List<PaymentItem> paymentItemList, OnPayButtonClickListener payButtonClickListener){
        this.context = context;
        this.paymentItemList = paymentItemList;
        this.payButtonClickListener = payButtonClickListener;
    }
    @NonNull
    @Override
    public PaymentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.payment_item, parent, false);
        return new PaymentItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentItemViewHolder holder, int position) {
        PaymentItem paymentItem = paymentItemList.get(position);
        holder.deliveryFees.setText("Ksh. "+paymentItem.getDeliveryFees());
        holder.itemFees.setText("Ksh. "+paymentItem.getItemsTotalCost());
        holder.totalAmount.setText((int)(Math.round(paymentItem.getDeliveryFees()+paymentItem.getItemsTotalCost()))+"");

        // pay on click listener of the button
        holder.btnPay.setOnClickListener(v -> {
            payButtonClickListener.onClick(holder,paymentItem.getDeliveryFees()+paymentItem.getItemsTotalCost());
        });
    }

    @Override
    public int getItemCount() {
        return paymentItemList.size();
    }

    public class PaymentItemViewHolder extends RecyclerView.ViewHolder {
        public TextView deliveryFees, itemFees;
        public EditText totalAmount;
        public Button btnPay;
        public ProgressBar progressBar;
        private View progressBarLayout;
        public PaymentItemViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryFees = itemView.findViewById(R.id.txtDeliveryFees);
            itemFees = itemView.findViewById(R.id.txtItemFees);
            totalAmount = itemView.findViewById(R.id.edtAmount);
            btnPay = itemView.findViewById(R.id.btnPay);
            progressBarLayout = itemView.findViewById(R.id.progressBarLayout);
            progressBar = progressBarLayout.findViewById(R.id.progressBar);
        }
    }


    public interface OnPayButtonClickListener{
        void onClick(PaymentItemViewHolder holder, double amount);
    }
}
