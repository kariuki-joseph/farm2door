package com.example.farm2door.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm2door.OnRecyclerItemClickListener;
import com.example.farm2door.R;
import com.example.farm2door.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

 public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productsList;

    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public ProductAdapter(Context context, List<Product> productsList, OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.context = context;
        this.productsList = productsList;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productsList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());
        Picasso.get().load(product.getImageURL())
                .resize(250, 250)
                .centerCrop()
                .into(holder.productImage);

        // set click listener for this item
        holder.itemView.setOnClickListener(v -> onRecyclerItemClickListener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public static final class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.tvProductName);
            productPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
