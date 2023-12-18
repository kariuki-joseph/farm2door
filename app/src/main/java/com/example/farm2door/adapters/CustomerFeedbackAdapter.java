package com.example.farm2door.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm2door.OnRecyclerItemClickListener;
import com.example.farm2door.R;
import com.example.farm2door.models.CustomerFeedback;

import java.util.List;

public class CustomerFeedbackAdapter extends RecyclerView.Adapter<CustomerFeedbackAdapter.ViewHolder>{
    private List<CustomerFeedback> customerFeedbacks;
    private Context context;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public CustomerFeedbackAdapter(Context context, List<CustomerFeedback> customerFeedbacks, OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.context = context;
        this.customerFeedbacks = customerFeedbacks;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @NonNull
    @Override
    public CustomerFeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_feedback_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerFeedbackAdapter.ViewHolder holder, int position) {
        CustomerFeedback customerFeedback = customerFeedbacks.get(position);
        holder.customerName.setText(customerFeedback.getCustomerName());
        holder.customerFeedback.setText(customerFeedback.getCustomerFeedback());
        holder.customerRatingBar.setRating(customerFeedback.getCustomerRating());
        holder.customerRating.setText(String.valueOf(customerFeedback.getCustomerRating()));

        // set click listener
        holder.itemView.setOnClickListener(v -> onRecyclerItemClickListener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return customerFeedbacks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName;
        private TextView customerFeedback;
        private TextView customerRating;
        private RatingBar customerRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerName);
            customerFeedback = itemView.findViewById(R.id.customerFeedback);
            customerRating = itemView.findViewById(R.id.customerRating);
            customerRatingBar = itemView.findViewById(R.id.customerRatingBar);
        }
    }
}
