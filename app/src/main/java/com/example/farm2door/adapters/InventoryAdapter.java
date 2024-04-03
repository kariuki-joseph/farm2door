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

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>{
    private Context context;
    private List<InventoryItem> inventoryItems = new ArrayList<>();
    private OnInventoryItemClickListener onInventoryItemClickListener;
    public InventoryAdapter(Context context, OnInventoryItemClickListener onInventoryItemClickListener) {
        this.context = context;
        this.onInventoryItemClickListener = onInventoryItemClickListener;
    }

    public void setInventoryItems(List<InventoryItem> inventoryItems){
        this.inventoryItems = inventoryItems;
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

        // check if item is in stock or not
        if(inventoryItem.getRemainingQuantity() == 0){
            holder.inStock.setText("Out of Stock");
            holder.inStock.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.editButton.setOnClickListener(v -> {
            if (onInventoryItemClickListener != null){
                onInventoryItemClickListener.onEditClick(inventoryItem);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onInventoryItemClickListener != null){
                onInventoryItemClickListener.onDeleteClick(inventoryItem);
            }
        });

        holder.predictButton.setOnClickListener(v -> {
            if (onInventoryItemClickListener != null){
                onInventoryItemClickListener.onPredictClick(inventoryItem);
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
        void onEditClick(InventoryItem inventoryItem);
        void onDeleteClick(InventoryItem inventoryItem);
        void onPredictClick(InventoryItem inventoryItem);
    }
}
