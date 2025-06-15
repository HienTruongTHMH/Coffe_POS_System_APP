package com.midterm.myposapplication;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.OrderStatusViewHolder> {
    
    private List<OrderStatus> orderStatusList;
    private OnOrderStatusClickListener listener;
    private Handler doubleClickHandler = new Handler();
    
    public interface OnOrderStatusClickListener {
        void onOrderStatusClick(OrderStatus orderStatus);
        void onOrderStatusDoubleClick(OrderStatus orderStatus); // ✅ New double click
    }
    
    public OrderStatusAdapter(List<OrderStatus> orderStatusList, OnOrderStatusClickListener listener) {
        this.orderStatusList = orderStatusList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public OrderStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_status, parent, false);
        return new OrderStatusViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderStatusViewHolder holder, int position) {
        OrderStatus orderStatus = orderStatusList.get(position);
        
        // Set data
        holder.orderNumber.setText(orderStatus.getOrderNumber());
        holder.orderCount.setText(orderStatus.getItemCount() + " items");
        holder.tableName.setText(orderStatus.getTableName());
        
        // Set status badge
        updateStatusBadge(holder.statusBadge, orderStatus.getStatus());
        
        // ✅ Double click handler
        setupDoubleClickListener(holder, orderStatus);
    }
    
    private void setupDoubleClickListener(OrderStatusViewHolder holder, OrderStatus orderStatus) {
        final int[] clickCount = {0};
        
        holder.itemView.setOnClickListener(v -> {
            clickCount[0]++;
            
            if (clickCount[0] == 1) {
                // Single click - wait for potential second click
                doubleClickHandler.postDelayed(() -> {
                    if (clickCount[0] == 1) {
                        // Single click action
                        if (listener != null) {
                            listener.onOrderStatusClick(orderStatus);
                        }
                    }
                    clickCount[0] = 0;
                }, 300); // 300ms delay
                
            } else if (clickCount[0] == 2) {
                // Double click action
                doubleClickHandler.removeCallbacksAndMessages(null);
                
                if (listener != null) {
                    listener.onOrderStatusDoubleClick(orderStatus);
                }
                
                // Update status to "in_service"
                if ("ready".equals(orderStatus.getStatus())) {
                    orderStatus.setStatus("in_service");
                    updateStatusBadge(holder.statusBadge, "in_service");
                }
                
                clickCount[0] = 0;
            }
        });
    }
    
    private void updateStatusBadge(TextView statusBadge, String status) {
        switch (status) {
            case "preparing":
                statusBadge.setText("Đang làm");
                statusBadge.setBackground(statusBadge.getContext().getResources().getDrawable(R.drawable.status_badge_preparing));
                break;
            case "ready":
                statusBadge.setText("Sẵn sàng");
                statusBadge.setBackground(statusBadge.getContext().getResources().getDrawable(R.drawable.status_badge_ready));
                break;
            case "in_service":
                statusBadge.setText("Đang phục vụ");
                statusBadge.setBackground(statusBadge.getContext().getResources().getDrawable(R.drawable.status_badge_serving));
                break;
            default:
                statusBadge.setText("Unknown");
                statusBadge.setBackground(statusBadge.getContext().getResources().getDrawable(R.drawable.status_badge_preparing));
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        return orderStatusList != null ? orderStatusList.size() : 0;
    }
    
    public void updateOrderStatus(List<OrderStatus> newOrderStatusList) {
        this.orderStatusList = newOrderStatusList;
        notifyDataSetChanged();
    }
    
    public static class OrderStatusViewHolder extends RecyclerView.ViewHolder {
        TextView statusBadge, orderNumber, orderCount, tableName;
        
        public OrderStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            statusBadge = itemView.findViewById(R.id.status_badge);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderCount = itemView.findViewById(R.id.order_count);
            tableName = itemView.findViewById(R.id.table_name);
        }
    }
}
