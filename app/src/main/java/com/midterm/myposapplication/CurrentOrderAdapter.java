package com.midterm.myposapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.CurrentOrderViewHolder> {
    
    private static final String TAG = "CurrentOrderAdapter";
    
    private List<CurrentOrderItem> currentOrderItems;
    private OnOrderItemChangeListener listener;
    private boolean showActionButtons = false;
    
    public interface OnOrderItemChangeListener {
        void onQuantityChanged(CurrentOrderItem item, int newQuantity);
        void onItemRemoved(CurrentOrderItem item);
        void onConfirmOrder(List<CurrentOrderItem> items);
        void onCancelOrder();
    }

    public CurrentOrderAdapter(List<CurrentOrderItem> items, OnOrderItemChangeListener listener) {
        this.currentOrderItems = items;
        this.listener = listener;
        Log.d(TAG, "CurrentOrderAdapter initialized with " + items.size() + " items");
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
        if (item == null) {
            Log.e(TAG, "CurrentOrderItem is null at position: " + position);
            return;
        }

        Log.d(TAG, "Binding item: " + item.getDrinkName() + " x" + item.getQuantity());
        
        // ✅ FIXED: Thêm kiểm tra null cho tất cả views
        if (holder.drinkName != null) {
            holder.drinkName.setText(item.getDrinkName());
        }
        
        if (holder.drinkPrice != null) {
            // Calculate total price for the line item (price * quantity)
            double lineItemTotal = item.getPrice() * item.getQuantity();
            holder.drinkPrice.setText(String.format("$%.2f", lineItemTotal));
        }
        
        if (holder.quantityText != null) {
            holder.quantityText.setText(String.valueOf(item.getQuantity()));
        }

        if (holder.drinkImage != null) {
            holder.drinkImage.setImageResource(item.getImageResId());
        }

        // Plus button
        if (holder.plusButton != null) {
            holder.plusButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(item, item.getQuantity() + 1);
                    Log.d(TAG, "Increased quantity for " + item.getDrinkName());
                }
            });
        }

        // Minus button
        if (holder.minusButton != null) {
            holder.minusButton.setOnClickListener(v -> {
                if (listener != null) {
                    int newQuantity = item.getQuantity() - 1;
                    if (newQuantity <= 0) {
                        listener.onItemRemoved(item);
                        Log.d(TAG, "Removed item: " + item.getDrinkName());
                    } else {
                        listener.onQuantityChanged(item, newQuantity);
                        Log.d(TAG, "Decreased quantity for " + item.getDrinkName());
                    }
                }
            });
        }

        // ✅ FIXED: Chỉ hiển thị action buttons ở item cuối cùng
        boolean isLastItem = (position == currentOrderItems.size() - 1);
        boolean shouldShowButtons = showActionButtons && isLastItem;
        
        int actionButtonsVisibility = shouldShowButtons ? View.VISIBLE : View.GONE;
        
        // Find the action buttons container
        View actionButtonsLayout = holder.itemView.findViewById(R.id.action_buttons_layout);
        if (actionButtonsLayout != null) {
            actionButtonsLayout.setVisibility(actionButtonsVisibility);
            Log.d(TAG, "Action buttons layout visibility set to: " + 
                (actionButtonsVisibility == View.VISIBLE ? "VISIBLE" : "GONE") + 
                " for position " + position + " (isLast: " + isLastItem + ")");
        }
        
        if (holder.cancelButton != null) {
            holder.cancelButton.setVisibility(actionButtonsVisibility);
            if (shouldShowButtons) {
                holder.cancelButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCancelOrder();
                        Log.d(TAG, "Cancel order clicked");
                    }
                });
            }
        }

        if (holder.confirmButton != null) {
            holder.confirmButton.setVisibility(actionButtonsVisibility);
            if (shouldShowButtons) {
                holder.confirmButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onConfirmOrder(currentOrderItems);
                        Log.d(TAG, "Confirm order clicked with " + currentOrderItems.size() + " items");
                    }
                });
            }
        }
    }
    
    @Override
    public int getItemCount() {
        return currentOrderItems != null ? currentOrderItems.size() : 0;
    }
    
    public void setShowActionButtons(boolean show) {
        Log.d(TAG, "setShowActionButtons called with: " + show);
        this.showActionButtons = show;
        notifyDataSetChanged();
        Log.d(TAG, "Action buttons visibility set to: " + show + ", notifyDataSetChanged() called");
    }
    
    public static class CurrentOrderViewHolder extends RecyclerView.ViewHolder {
        ImageView drinkImage;
        TextView drinkName, drinkPrice, quantityText;
        ImageButton plusButton, minusButton;
        Button cancelButton, confirmButton;

        public CurrentOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // ✅ FIXED: Sử dụng đúng ID từ layout XML
            drinkImage = itemView.findViewById(R.id.drink_image);
            drinkName = itemView.findViewById(R.id.drink_name);
            drinkPrice = itemView.findViewById(R.id.drink_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            
            plusButton = itemView.findViewById(R.id.btn_plus);
            minusButton = itemView.findViewById(R.id.btn_minus);
            
            // ✅ FIXED: Sử dụng đúng ID cho action buttons
            cancelButton = itemView.findViewById(R.id.btn_cancel_order);
            confirmButton = itemView.findViewById(R.id.btn_confirm_order);
            
            // ✅ Debug logging để kiểm tra view nào bị null
            Log.d(TAG, "ViewHolder initialized - " +
                "drinkImage: " + (drinkImage != null) +
                ", drinkName: " + (drinkName != null) +
                ", drinkPrice: " + (drinkPrice != null) +
                ", quantityText: " + (quantityText != null) +
                ", plusButton: " + (plusButton != null) +
                ", minusButton: " + (minusButton != null) +
                ", cancelButton: " + (cancelButton != null) +
                ", confirmButton: " + (confirmButton != null));
        }
    }
}