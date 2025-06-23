package com.midterm.myposapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter hiển thị danh sách các món trong đơn hàng trên màn hình thanh toán
 */
public class PaymentItemAdapter extends RecyclerView.Adapter<PaymentItemAdapter.PaymentItemViewHolder> {

    private final List<OrderItem> items;
    private final Context context;

    public PaymentItemAdapter(List<OrderItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment, parent, false);
        return new PaymentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        
        // Hiển thị số lượng
        holder.quantityText.setText(item.getQuantity() + "x");
        
        // Hiển thị tên món
        holder.nameText.setText(item.getDrinkName());
        
        // SỬA LỖI: Đơn giản hóa thông tin chi tiết, chỉ hiển thị size
        holder.detailsText.setText("Size: " + item.getSize());
        
        // Hiển thị giá (đơn giá * số lượng)
        double totalItemPrice = item.getPrice() * item.getQuantity();
        holder.priceText.setText(String.format("$%.2f", totalItemPrice));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * ViewHolder cho item trong danh sách thanh toán
     */
    static class PaymentItemViewHolder extends RecyclerView.ViewHolder {
        TextView quantityText;
        TextView nameText;
        TextView detailsText;
        TextView priceText;

        public PaymentItemViewHolder(@NonNull View itemView) {
            super(itemView);
            quantityText = itemView.findViewById(R.id.item_quantity);
            nameText = itemView.findViewById(R.id.item_name);
            detailsText = itemView.findViewById(R.id.item_details);
            priceText = itemView.findViewById(R.id.item_price);
        }
    }
}