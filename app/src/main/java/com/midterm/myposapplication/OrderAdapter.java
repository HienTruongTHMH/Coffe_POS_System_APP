package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    
    private List<Order> orderList;
    private OnOrderClickListener listener;
    
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onOrderStatusUpdate(Order order, Order.OrderStatus newStatus);
        void onPaymentStatusUpdate(Order order, Order.PaymentStatus newStatus);
    }
    
    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        // Set basic info
        holder.orderId.setText(order.getOrderNumber());
        holder.orderDateTime.setText(order.getFormattedDateTime());
        holder.orderEmployee.setText(order.getEmployeeName());
        holder.tableInfo.setText(order.getTableInfo());
        holder.orderAmount.setText(order.getFormattedAmountWithItems());
        
        // Set order status
        holder.orderStatus.setText(order.getOrderStatus().getDisplayName());
        holder.orderStatus.setBackground(getStatusBackground(holder.itemView.getContext(), order.getOrderStatus()));
        
        // Set payment status
        holder.paymentStatus.setText(order.getPaymentStatus().getDisplayName());
        holder.paymentStatus.setBackground(getPaymentStatusBackground(holder.itemView.getContext(), order.getPaymentStatus()));
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }
    
    private android.graphics.drawable.Drawable getStatusBackground(android.content.Context context, Order.OrderStatus status) {
        switch (status) {
            case PREPARING:
                return context.getResources().getDrawable(R.drawable.status_preparing_background);
            case READY:
                return context.getResources().getDrawable(R.drawable.status_badge_ready);
            case SERVING:
                return context.getResources().getDrawable(R.drawable.status_serving_background);
            case COMPLETED:
                return context.getResources().getDrawable(R.drawable.status_complete_background);
            default:
                return context.getResources().getDrawable(R.drawable.status_preparing_background);
        }
    }
    
    private android.graphics.drawable.Drawable getPaymentStatusBackground(android.content.Context context, Order.PaymentStatus status) {
        switch (status) {
            case PENDING:
                return context.getResources().getDrawable(R.drawable.status_pending_background);
            case PROCESSING:
                return context.getResources().getDrawable(R.drawable.status_preparing_background);
            case PAID:
                return context.getResources().getDrawable(R.drawable.status_paid_background);
            default:
                return context.getResources().getDrawable(R.drawable.status_pending_background);
        }
    }
    
    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }
    
    public void updateOrders(List<Order> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }
    
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderAmount, orderDateTime, orderEmployee;
        TextView orderStatus, paymentStatus, tableInfo;
        
        public OrderViewHolder(@NonNull View itemView) {
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