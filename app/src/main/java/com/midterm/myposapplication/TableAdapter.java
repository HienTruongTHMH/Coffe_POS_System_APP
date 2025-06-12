package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<Table> tableList;
    private OnTableClickListener listener;

    public TableAdapter(List<Table> tableList, OnTableClickListener listener) {
        this.tableList = tableList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_card, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);

        // Set table data
        holder.tableNumber.setText(table.getNumber());
        holder.tableStatus.setText(getStatusText(table.getStatus()));

        // Set styling based on status
        setTableStyling(holder, table);

        // Handle click event with animation
        holder.cardView.setOnClickListener(v -> {
            // Add click animation
            v.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start();
                })
                .start();

            if (listener != null) {
                listener.onTableClick(table);
            }
        });
    }

    private String getStatusText(String status) {
        switch (status) {
            case "available":
                return "Trống";
            case "occupied":
                return "Có khách";
            case "preparing":
                return "Đang chuẩn bị";
            case "reserved":
                return "Đặt trước";
            default:
                return "Không xác định";
        }
    }

    private void setTableStyling(TableViewHolder holder, Table table) {
        int statusColor;
        int cardBackgroundColor;
        boolean showOrderCount = false;
        String orderCountText = "";

        switch (table.getStatus()) {
            case "available":
                statusColor = R.color.status_available;
                cardBackgroundColor = R.color.card_available;
                break;
                
            case "occupied":
                statusColor = R.color.status_occupied;
                cardBackgroundColor = R.color.card_occupied;
                showOrderCount = true;
                orderCountText = "3";
                break;
                
            case "preparing":
                statusColor = R.color.status_preparing;
                cardBackgroundColor = R.color.card_preparing;
                showOrderCount = true;
                orderCountText = "1";
                break;
                
            case "reserved":
                statusColor = R.color.status_reserved;
                cardBackgroundColor = R.color.card_reserved;
                break;
                
            default:
                statusColor = R.color.status_default;
                cardBackgroundColor = android.R.color.white;
                break;
        }

        // Apply colors
        try {
            holder.statusIndicator.setBackgroundTintList(
                ContextCompat.getColorStateList(holder.itemView.getContext(), statusColor)
            );
            
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), cardBackgroundColor)
            );

            // Show/hide order count
            if (showOrderCount) {
                holder.orderCount.setVisibility(View.VISIBLE);
                holder.orderCount.setText(orderCountText);
            } else {
                holder.orderCount.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            // Fallback styling
            holder.statusIndicator.setBackgroundTintList(
                ContextCompat.getColorStateList(holder.itemView.getContext(), android.R.color.darker_gray)
            );
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white)
            );
        }
    }

    @Override
    public int getItemCount() {
        return tableList != null ? tableList.size() : 0;
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView tableIcon;
        TextView tableNumber;
        TextView tableStatus;
        TextView orderCount;
        View statusIndicator;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = (CardView) itemView;
            tableIcon = itemView.findViewById(R.id.table_icon);
            tableNumber = itemView.findViewById(R.id.table_number);
            tableStatus = itemView.findViewById(R.id.table_status);
            orderCount = itemView.findViewById(R.id.order_count);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }
    }
}