package com.midterm.myposapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.OrderStatusViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;
    private Context context;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderStatusAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        // ✅ Inflate đúng layout item_order_status
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_status, parent, false);
        return new OrderStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderStatusViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);

        // ✅ FIXED: Open OrderDetailActivity on click
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }
    
    public void updateOrders(List<Order> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }

    class OrderStatusViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, statusBadge, tableName, itemCount;

        public OrderStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.order_number);
            statusBadge = itemView.findViewById(R.id.status_badge);
            tableName = itemView.findViewById(R.id.table_name);
            itemCount = itemView.findViewById(R.id.order_count);
        }

        public void bind(final Order order) {
            orderNumber.setText(order.getOrderNumber());
            tableName.setText(order.getTableName());
            itemCount.setText(order.getItems().size() + " món");
            
            // Set status badge
            statusBadge.setText(order.getOrderStatus().getDisplayName());
            statusBadge.setBackground(getStatusBackground(order.getOrderStatus()));
        }
        
        private Drawable getStatusBackground(Order.OrderStatus status) {
            int drawableRes;
            switch (status) {
                case ON_SERVICE:
                    drawableRes = R.drawable.status_badge_serving;
                    break;
                case PREPARING:
                default:
                    drawableRes = R.drawable.status_badge_preparing;
                    break;
            }
            return ContextCompat.getDrawable(context, drawableRes);
        }
    }
}
