package com.midterm.myposapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartOrderAdapter extends RecyclerView.Adapter<CartOrderAdapter.CartOrderViewHolder> {

    private List<Order> orderList;
    private final OnCartOrderClickListener listener;
    private Context context;

    /**
     * SỬA LỖI: Cập nhật interface để bao gồm các phương thức xử lý cập nhật trạng thái.
     * Điều này sẽ giải quyết lỗi "method does not override".
     */
    public interface OnCartOrderClickListener {
        void onOrderClick(Order order);
        void onOrderStatusUpdate(Order order, Order.OrderStatus newStatus);
        void onPaymentStatusUpdate(Order order, Order.PaymentStatus newStatus);
    }

    public CartOrderAdapter(List<Order> orderList, OnCartOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
        return new CartOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartOrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set basic info
        holder.orderId.setText(order.getOrderNumber());
        holder.orderDateTime.setText(order.getFormattedDateTime());
        holder.orderEmployee.setText(order.getEmployeeName());
        holder.tableInfo.setText(order.getTableInfo());
        holder.orderAmount.setText(order.getFormattedAmountWithItems());

        // Set order status
        holder.orderStatus.setText(order.getOrderStatus().getDisplayName());
        holder.orderStatus.setBackground(getStatusBackground(order.getOrderStatus()));

        // Set payment status
        holder.paymentStatus.setText(order.getPaymentStatus().getDisplayName());
        holder.paymentStatus.setBackground(getPaymentStatusBackground(order.getPaymentStatus()));

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
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

    /**
     * SỬA LỖI: Thêm phương thức getStatusBackground
     * Lấy drawable background tương ứng với trạng thái đơn hàng.
     */
    private Drawable getStatusBackground(Order.OrderStatus status) {
        int drawableRes;
        switch (status) {
            case READY:
                drawableRes = R.drawable.status_badge_ready;
                break;
            case SERVING:
                drawableRes = R.drawable.status_serving_background;
                break;
            case COMPLETED:
            case PAID: // Trạng thái PAID và COMPLETED có thể dùng chung màu
                drawableRes = R.drawable.status_complete_background;
                break;
            case PREPARING:
            default:
                drawableRes = R.drawable.status_preparing_background;
                break;
        }
        return ContextCompat.getDrawable(context, drawableRes);
    }

    /**
     * SỬA LỖI: Thêm phương thức getPaymentStatusBackground
     * Lấy drawable background tương ứng với trạng thái thanh toán.
     */
    private Drawable getPaymentStatusBackground(Order.PaymentStatus status) {
        int drawableRes;
        switch (status) {
            case PAID:
                drawableRes = R.drawable.status_paid_background;
                break;
            case PROCESSING:
                drawableRes = R.drawable.status_complete_background;
                break;
            case PENDING:
            default:
                drawableRes = R.drawable.status_pending_background;
                break;
        }
        return ContextCompat.getDrawable(context, drawableRes);
    }

    public static class CartOrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderAmount, orderDateTime, orderEmployee;
        TextView orderStatus, paymentStatus, tableInfo;

        public CartOrderViewHolder(@NonNull View itemView) {
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