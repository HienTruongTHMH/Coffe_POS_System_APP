package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private final List<OrderItem> items;

    public OrderDetailAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        private final TextView quantityText;
        private final TextView nameText;
        private final TextView priceText;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            quantityText = itemView.findViewById(R.id.tv_item_quantity);
            nameText = itemView.findViewById(R.id.tv_item_name);
            priceText = itemView.findViewById(R.id.tv_item_price);
        }

        public void bind(OrderItem item) {
            quantityText.setText(item.getQuantity() + "x");
            nameText.setText(item.getDrinkName());
            double lineTotal = item.getPrice() * item.getQuantity();
            priceText.setText(String.format("$%.2f", lineTotal));
        }
    }
}