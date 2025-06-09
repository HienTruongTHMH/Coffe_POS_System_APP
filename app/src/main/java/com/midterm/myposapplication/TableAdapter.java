package com.midterm.myposapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);

        holder.tableName.setText(table.getName());
        holder.tableStatus.setText(getStatusText(table.getStatus()));

        // Set card color based on status
        setCardAppearance(holder, table.getStatus());

        // Handle click event
        holder.cardTable.setOnClickListener(v -> {
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
            case "reserved":
                return "Đặt trước";
            default:
                return "Không xác định";
        }
    }

    private void setCardAppearance(TableViewHolder holder, String status) {
        switch (status) {
            case "available":
                holder.cardTable.setCardBackgroundColor(Color.parseColor("#E8F5E8"));
                holder.tableStatus.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "occupied":
                holder.cardTable.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.tableStatus.setTextColor(Color.parseColor("#F44336"));
                break;
            case "reserved":
                holder.cardTable.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
                holder.tableStatus.setTextColor(Color.parseColor("#FF9800"));
                break;
            default:
                holder.cardTable.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                holder.tableStatus.setTextColor(Color.parseColor("#757575"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        CardView cardTable;
        TextView tableName;
        TextView tableStatus;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTable = itemView.findViewById(R.id.card_table);
            tableName = itemView.findViewById(R.id.tv_table_name);
            tableStatus = itemView.findViewById(R.id.tv_table_status);
        }
    }
}