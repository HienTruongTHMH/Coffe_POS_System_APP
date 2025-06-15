package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderManagementAdapter extends RecyclerView.Adapter<OrderManagementAdapter.OrderCardViewHolder> {
    
    private List<OrderManagement> orderList;
    private OnOrderCardClickListener listener;
    
    public interface OnOrderCardClickListener {
        void onOrderCardClick(OrderManagement order);
        void onOrderStatusUpdate(OrderManagement order, String newStatus);
    }
    
    public OrderManagementAdapter(List<OrderManagement> orderList, OnOrderCardClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public OrderCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new OrderCardViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderCardViewHolder holder, int position) {
        OrderManagement order = orderList.get(position);
        
        // Set basic info
        holder.orderId.setText(order.getOrderNumber());
        holder.orderDateTime.setText(order.getDateTime());
        holder.orderEmployee.setText(order.getEmployee());
        holder.tableInfo.setText(order.getTableInfo());
        
        // ✅ Set amount with item count
        holder.orderAmount.setText(order.getFormattedAmountWithItems());
        
        // Set order status
        holder.orderStatus.setText(order.getOrderStatusDisplayName());
        holder.orderStatus.setBackground(getStatusBackground(holder.itemView.getContext(), order.getOrderStatus()));
        
        // Set payment status
        holder.paymentStatus.setText(order.getPaymentStatusDisplayName());
        holder.paymentStatus.setBackground(getPaymentStatusBackground(holder.itemView.getContext(), order.getPaymentStatus()));
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderCardClick(order);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }
    
    private android.graphics.drawable.Drawable getStatusBackground(android.content.Context context, String status) {
        switch (status) {
            case "preparing":
                return context.getResources().getDrawable(R.drawable.status_preparing_background);
            case "serving":
                return context.getResources().getDrawable(R.drawable.status_serving_background);
            case "waiting_payment":
                return context.getResources().getDrawable(R.drawable.status_pending_background);
            case "paid":
                return context.getResources().getDrawable(R.drawable.status_complete_background);
            default:
                return context.getResources().getDrawable(R.drawable.status_pending_background);
        }
    }
    
    private android.graphics.drawable.Drawable getPaymentStatusBackground(android.content.Context context, String paymentStatus) {
        switch (paymentStatus) {
            case "pending":
                return context.getResources().getDrawable(R.drawable.status_pending_background);
            case "processing":
                return context.getResources().getDrawable(R.drawable.status_preparing_background);
            case "paid":
                return context.getResources().getDrawable(R.drawable.status_paid_background);
            default:
                return context.getResources().getDrawable(R.drawable.status_pending_background);
        }
    }
    
    public void updateOrders(List<OrderManagement> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }
    
    public static class OrderCardViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderAmount, orderDateTime, orderEmployee;
        TextView orderStatus, paymentStatus, tableInfo;
        
        public OrderCardViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderAmount = itemView.findViewById(R.id.order_amount);
            orderDateTime = itemView.findViewById(R.id.order_datetime);
            orderEmployee = itemView.findViewById(R.id.order_employee);
            orderStatus = itemView.findViewById(R.id.order_status);
            paymentStatus = itemView.findViewById(R.id.payment_status);
            tableInfo = itemView.findViewById(R.id.table_info);
        }
    }
}
