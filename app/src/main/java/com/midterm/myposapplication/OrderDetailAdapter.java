package com.midterm.myposapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm.myposapplication.utils.FormatUtils;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private final List<OrderItem> items;
    private Context context;

    public OrderDetailAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.bind(item, context);
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

        public void bind(OrderItem item, Context context) {
            // ✅ FIXED: Use FormatUtils for consistent formatting
            quantityText.setText(FormatUtils.formatQuantity(context, item.getQuantity()));
            nameText.setText(item.getDrinkName());
            
            double lineTotal = item.getPrice() * item.getQuantity();
            priceText.setText(FormatUtils.formatPrice(context, lineTotal));
        }
    }
}