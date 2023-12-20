package com.example.farm2door.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm2door.OnRecyclerItemClickListener;
import com.example.farm2door.R;
import com.example.farm2door.models.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>{
    private List<OrderItem> orderItems;
    private Context context;
    private OrderItemListener orderItemListener;

    public OrderItemAdapter(Context context, List<OrderItem> orderItems, OrderItemListener orderItemListener) {
        this.context = context;
        this.orderItems = orderItems;
        this.orderItemListener = orderItemListener;
    }

    @NonNull
    @Override
    public OrderItemAdapter.OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);

        holder.orderName.setText(orderItem.getName());
        holder.orderNumber.setText(orderItem.getOrderNumber());
        holder.orderPrice.setText("Ksh. "+String.valueOf(orderItem.getPrice()));
        holder.orderQuantity.setText(String.valueOf(orderItem.getQuantity()) + " " + orderItem.getUnitName()+"(s)");
        holder.orderDate.setText(orderItem.getOrderDate());

        // TODO switch the text of the "Track Order" to "Deliver" depending on the logged in user

        Picasso.get().load(orderItem.getImageURL())
                .resize(120, 130)
                .centerCrop()
                .into(holder.orderItemImage);

        // set click listener for this item
        holder.btnDeleteOrder.setOnClickListener(v -> orderItemListener.onDeleteClick(position));
        holder.btnTrackOrder.setOnClickListener(v -> orderItemListener.onDynamicButtonClick(position));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private TextView orderName,orderNumber, orderPrice,orderQuantity, orderDate;
        private ImageView orderItemImage;
        Button btnDeleteOrder, btnTrackOrder;


        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);

            orderName = itemView.findViewById(R.id.tvOrderName);
            orderNumber = itemView.findViewById(R.id.tvOrderNumber);
            orderPrice = itemView.findViewById(R.id.tvOrderPrice);
            orderItemImage = itemView.findViewById(R.id.imgOrder);
            orderQuantity = itemView.findViewById(R.id.tvOrderQuantity);
            orderDate = itemView.findViewById(R.id.tvOrderDate);
            btnDeleteOrder = itemView.findViewById(R.id.btnDelete);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
        }
    }


    public interface OrderItemListener {
        void onDeleteClick(int position);
        void onDynamicButtonClick(int position);
    }
}
