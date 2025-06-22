package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.CurrentOrderViewHolder> {
    
    private List<CurrentOrderItem> currentOrderItems;
    private boolean showActionButtons = false; // ✅ Control button visibility
    
    // ✅ Enhanced interface với action buttons
    public interface OnOrderItemChangeListener {
        void onQuantityChanged(CurrentOrderItem item, int newQuantity);
        void onItemRemoved(CurrentOrderItem item);
        void onConfirmOrder(List<CurrentOrderItem> items); // ✅ New method
        void onCancelOrder(); // ✅ New method
    }
    
    private OnOrderItemChangeListener listener;
    
    public CurrentOrderAdapter(List<CurrentOrderItem> currentOrderItems, OnOrderItemChangeListener listener) {
        this.currentOrderItems = currentOrderItems;
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
        CurrentOrderItem item = currentOrderItems.get(position);
        
        // Set data
        holder.itemName.setText(item.getDrinkName() + " (" + item.getSize() + ")");
        holder.itemPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        holder.totalPrice.setText(String.format("$%.2f", item.getTotalPrice()));
        
        // Set image
        if (item.getimageResId() != 0) {
            holder.itemImage.setImageResource(item.getimageResId());
        } else {
            holder.itemImage.setImageResource(R.drawable.placeholder_drink);
        }
        
        // Quantity controls
        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity() + 1);
            }
        });
        
        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null) {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity <= 0) {
                    listener.onItemRemoved(item);
                } else {
                    listener.onQuantityChanged(item, newQuantity);
                }
            }
        });
        
        // ✅ Show action buttons only for last item when order has items
        if (position == currentOrderItems.size() - 1 && currentOrderItems.size() > 0) {
            holder.actionButtonsContainer.setVisibility(View.VISIBLE);
            
            // Cancel button
            holder.btnCancelOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelOrder();
                }
            });
            
            // Confirm button
            holder.btnConfirmOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConfirmOrder(currentOrderItems);
                }
            });
        } else {
            holder.actionButtonsContainer.setVisibility(View.GONE);
        }
    }
    
    @Override
    public int getItemCount() {
        return currentOrderItems != null ? currentOrderItems.size() : 0;
    }
    
    // ✅ Method to control button visibility
    public void setShowActionButtons(boolean show) {
        this.showActionButtons = show;
        notifyDataSetChanged();
    }
    
    public static class CurrentOrderViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemPrice;
        TextView itemQuantity;
        TextView totalPrice;
        ImageButton btnPlus;
        ImageButton btnMinus;
        LinearLayout actionButtonsContainer; // ✅ New
        Button btnCancelOrder; // ✅ New
        Button btnConfirmOrder; // ✅ New
        
        public CurrentOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            
            // ✅ New action buttons
            actionButtonsContainer = itemView.findViewById(R.id.action_buttons_container);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);
            btnConfirmOrder = itemView.findViewById(R.id.btn_confirm_order);
        }
    }
}