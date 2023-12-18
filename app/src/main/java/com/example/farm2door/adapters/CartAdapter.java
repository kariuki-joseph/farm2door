package com.example.farm2door.adapters;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm2door.R;
import com.example.farm2door.models.CartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
     private List<CartItem> cartItems;
    private Context context;
    private OnQuantityClickListener onQuantityClickListener;

    public CartAdapter(Context context,List<CartItem> cartItems,  OnQuantityClickListener onQuantityClickListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.onQuantityClickListener = onQuantityClickListener;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText(String.valueOf(cartItem.getProductPrice()));
        holder.productQuantity.setText(String.valueOf(cartItem.getProductQuantity()));
        holder.productTotalPrice.setText(String.valueOf(cartItem.getProductTotalPrice()));

        Picasso.get().load(cartItem.getProductImage())
                .resize(100, 100)
                .centerCrop()
                .into(holder.productImage);

        // listen for quantity increase and decrease
        holder.btnIncrease.setOnClickListener(v -> {
            if(onQuantityClickListener != null) {
                onQuantityClickListener.onIncreaseClick(position);
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if(onQuantityClickListener != null) {
                onQuantityClickListener.onDecreaseClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static final class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageButton btnDecrease, btnIncrease;
        TextView productName, productPrice, productQuantity, productTotalPrice;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.quantity);
            productTotalPrice = itemView.findViewById(R.id.totalPrice);
        }
    }


    // listen for quantity increase and decrease
    public interface OnQuantityClickListener {
        void onIncreaseClick(int position);
        void onDecreaseClick(int position);
    }
}
