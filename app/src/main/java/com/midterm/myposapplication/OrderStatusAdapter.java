package com.midterm.myposapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm.myposapplication.utils.FormatUtils;
import java.util.List;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;

    // ✅ FIXED: Constructor now only takes a Context. Data is updated via a method.
    public OrderStatusAdapter(Context context) {
        this.context = context;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Context is already available, no need to get it here
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order_status, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        if (orders == null) return;
        Order order = orders.get(position);
        holder.bind(order, context);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    // ✅ FIXED: ViewHolder now matches item_order_status.xml
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumberTextView;
        TextView statusBadgeTextView;
        TextView tableNameTextView;
        TextView orderCountTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // ✅ FIXED: IDs now match the XML file
            orderNumberTextView = itemView.findViewById(R.id.order_number);
            statusBadgeTextView = itemView.findViewById(R.id.status_badge);
            tableNameTextView = itemView.findViewById(R.id.table_name);
            orderCountTextView = itemView.findViewById(R.id.order_count);
        }

        public void bind(Order order, Context context) {
            // ✅ FIXED: Use FormatUtils and set text to the correct TextViews
            orderNumberTextView.setText(FormatUtils.formatOrderNumber(context, order.getOrderNumber()));
            tableNameTextView.setText(order.getTableName());

            String itemCountText = order.getItems().size() + " items";
            orderCountTextView.setText(itemCountText);

            // Set status
            if (order.getOrderStatus() == Order.OrderStatus.PREPARING) {
                statusBadgeTextView.setText(R.string.status_preparing);
                statusBadgeTextView.setBackgroundResource(R.drawable.status_badge_preparing);
            } else {
                statusBadgeTextView.setText(R.string.status_on_service);
                statusBadgeTextView.setBackgroundResource(R.drawable.status_badge_serving);
            }

            // Click listener to open OrderDetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra(Constants.KEY_ORDER_ID, order.getOrderId());
                context.startActivity(intent);
            });
        }
    }
}
