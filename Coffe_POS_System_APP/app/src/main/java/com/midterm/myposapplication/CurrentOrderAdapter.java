package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.CurrentOrderViewHolder> {

    private List<OrderItem> orderItems;
    private OnOrderItemClickListener listener;

    public interface OnOrderItemClickListener {
        void onRemoveItem(OrderItem item);
        void onQuantityChanged(OrderItem item, int newQuantity);
    }

    public CurrentOrderAdapter(List<OrderItem> orderItems, OnOrderItemClickListener listener) {
        this.orderItems = orderItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CurrentOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order, parent, false);
        return new CurrentOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentOrderViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        holder.drinkName.setText(item.getDrinkName());
        holder.drinkSize.setText(item.getSize());
        holder.drinkPrice.setText(String.format("$%.2f", item.getTotalPrice()));
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.drinkImage.setImageResource(item.getImageResId());

        // Handle quantity buttons
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onQuantityChanged(item, item.getQuantity());
                }
            } else {
                if (listener != null) {
                    listener.onRemoveItem(item);
                }
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class CurrentOrderViewHolder extends RecyclerView.ViewHolder {
        ImageView drinkImage;
        TextView drinkName;
        TextView drinkSize;
        TextView drinkPrice;
        TextView quantity;
        ImageView btnMinus;
        ImageView btnPlus;

        public CurrentOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            drinkImage = itemView.findViewById(R.id.item_image);
            drinkName = itemView.findViewById(R.id.item_name);
            drinkSize = itemView.findViewById(R.id.item_size);
            drinkPrice = itemView.findViewById(R.id.item_price);
            quantity = itemView.findViewById(R.id.item_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }
    }
}