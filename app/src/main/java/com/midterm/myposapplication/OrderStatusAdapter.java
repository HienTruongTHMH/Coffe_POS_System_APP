package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.OrderStatusViewHolder> {

    private List<OrderStatus> orderStatusList;
    private OnOrderStatusClickListener listener;

    public interface OnOrderStatusClickListener {
        void onOrderStatusClick(OrderStatus orderStatus);
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
        holder.statusBadge.setText(orderStatus.getStatusText());
        holder.orderNumber.setText(orderStatus.getOrderNumber());
        holder.orderCount.setText(orderStatus.getOrderCountText());
        holder.tableName.setText(orderStatus.getTableName());

        // Set status styling
        setStatusStyling(holder, orderStatus.getStatus());

        // Handle click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderStatusClick(orderStatus);
            }
        });
    }

    private void setStatusStyling(OrderStatusViewHolder holder, String status) {
        int backgroundRes;
        int textColor;

        switch (status) {
            case "ready":
                backgroundRes = R.drawable.status_badge_ready;
                textColor = R.color.white;
                break;
            case "preparing":
                backgroundRes = R.drawable.status_badge_preparing;
                textColor = R.color.white;
                break;
            default:
                backgroundRes = R.drawable.status_background;
                textColor = R.color.text_secondary;
                break;
        }

        holder.statusBadge.setBackgroundResource(backgroundRes);
        holder.statusBadge.setTextColor(
            ContextCompat.getColor(holder.itemView.getContext(), textColor)
        );
    }

    @Override
    public int getItemCount() {
        return orderStatusList != null ? orderStatusList.size() : 0;
    }

    public void updateOrderStatusList(List<OrderStatus> newOrderStatusList) {
        this.orderStatusList = newOrderStatusList;
        notifyDataSetChanged();
    }

    public static class OrderStatusViewHolder extends RecyclerView.ViewHolder {
        TextView statusBadge;
        TextView orderNumber;
        TextView orderCount;
        TextView tableName;

        public OrderStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            statusBadge = itemView.findViewById(R.id.status_badge);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderCount = itemView.findViewById(R.id.order_count);
            tableName = itemView.findViewById(R.id.table_name);
        }
    }
}
