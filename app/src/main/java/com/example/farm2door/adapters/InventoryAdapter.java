package com.example.farm2door.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm2door.R;
import com.example.farm2door.models.InventoryItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>{
    private Context context;
    private List<InventoryItem> inventoryItems;
    private OnInventoryItemClickListener onInventoryItemClickListener;
    public InventoryAdapter(Context context, List<InventoryItem> inventoryItems, OnInventoryItemClickListener onInventoryItemClickListener) {
        this.context = context;
        this.inventoryItems = inventoryItems;
        this.onInventoryItemClickListener = onInventoryItemClickListener;
    }


    @NonNull
    @Override
    public InventoryAdapter.InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);

        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryAdapter.InventoryViewHolder holder, int position) {
        InventoryItem inventoryItem = inventoryItems.get(position);
        holder.name.setText(inventoryItem.getName());
        holder.price.setText("Ksh. "+String.valueOf(inventoryItem.getPrice())+"/"+inventoryItem.getUnitName());
        holder.remaining.setText(String.valueOf(inventoryItem.getRemainingQuantity())+" "+inventoryItem.getUnitName()+"(s)");
        Picasso.get().load(inventoryItem.getImageURL()).into(holder.productImage);

        holder.editButton.setOnClickListener(v -> {
            if (onInventoryItemClickListener != null){
                onInventoryItemClickListener.onEditClick(position);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onInventoryItemClickListener != null){
                onInventoryItemClickListener.onDeleteClick(position);
            }
        });

        holder.predictButton.setOnClickListener(v -> {
            if (onInventoryItemClickListener != null){
                onInventoryItemClickListener.onPredictClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    public class InventoryViewHolder  extends  RecyclerView.ViewHolder{
        TextView name, price, remaining, inStock;
        ImageView productImage;
        ImageButton editButton, deleteButton, predictButton;
        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.price);
            remaining = itemView.findViewById(R.id.remaining);
            inStock = itemView.findViewById(R.id.inStock);
            productImage = itemView.findViewById(R.id.productImage);
            editButton = itemView.findViewById(R.id.editBtn);
            deleteButton = itemView.findViewById(R.id.deleteBtn);
            predictButton = itemView.findViewById(R.id.predictBtn);
        }

    }

    public interface OnInventoryItemClickListener{
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onPredictClick(int position);
    }
}
