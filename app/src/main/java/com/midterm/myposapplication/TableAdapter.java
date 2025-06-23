package com.midterm.myposapplication;

import android.util.Log;
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

    private static final String TAG = "TableAdapter";
    
    private List<Table> tableList;
    private OnTableClickListener listener;
    
    private OrderManager orderManager;
    private DatabaseManager databaseManager;

    public TableAdapter(List<Table> tableList, OnTableClickListener listener) {
        this.tableList = tableList;
        this.listener = listener;
        this.orderManager = OrderManager.getInstance();
        this.databaseManager = DatabaseManager.getInstance();
        Log.d(TAG, "TableAdapter initialized with " + tableList.size() + " tables");
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✅ Đảm bảo bạn đang sử dụng đúng file layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_card, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);
        if (table == null) {
            Log.e(TAG, "Table object is null at position: " + position);
            return; // Tránh crash nếu data không nhất quán
        }

        // ✅ FIXED: Thêm kiểm tra null trước khi gọi setText để chống crash
        if (holder.tableName != null) {
            holder.tableName.setText(table.getName());
        } else {
            Log.w(TAG, "holder.tableName is null at position " + position);
        }
        
        if (holder.tableCapacity != null) {
            holder.tableCapacity.setText(table.getCapacity() + " chỗ ngồi");
        } else {
            Log.w(TAG, "holder.tableCapacity is null at position " + position);
        }
        
        setTableStatus(holder, table);
        setOrderCount(holder, table);

        if (holder.cardView != null) {
            holder.cardView.setOnClickListener(v -> {
                v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start())
                    .start();

                if (listener != null) {
                    listener.onTableClick(table);
                }
            });
        }
    }

    private void setTableStatus(TableViewHolder holder, Table table) {
        boolean hasActiveOrders = !orderManager.getActiveOrdersByTable(table.getNumber()).isEmpty();
        String statusText;
        int statusColorRes;
        int indicatorColorRes;

        if (hasActiveOrders) {
            statusText = "Có khách";
            statusColorRes = R.drawable.status_occupied_background;
            indicatorColorRes = R.color.status_occupied;
            table.setStatus("occupied");
        } else {
            statusText = "Trống";
            statusColorRes = R.drawable.status_available_background;
            indicatorColorRes = R.color.status_available;
            table.setStatus("available");
        }

        // ✅ FIXED: Thêm kiểm tra null
        if (holder.tableStatus != null) {
            holder.tableStatus.setText(statusText);
            holder.tableStatus.setBackground(
                ContextCompat.getDrawable(holder.itemView.getContext(), statusColorRes)
            );
        }
        if (holder.statusIndicator != null) {
            holder.statusIndicator.setBackgroundTintList(
                ContextCompat.getColorStateList(holder.itemView.getContext(), indicatorColorRes)
            );
        }
    }

    private void setOrderCount(TableViewHolder holder, Table table) {
        int orderCount = orderManager.getActiveOrdersByTable(table.getNumber()).size();
        
        // ✅ FIXED: Thêm kiểm tra null
        if (holder.orderCount != null) {
            if (orderCount > 0) {
                holder.orderCount.setVisibility(View.VISIBLE);
                holder.orderCount.setText(orderCount + " order" + (orderCount > 1 ? "s" : ""));
            } else {
                holder.orderCount.setVisibility(View.GONE);
            }
        }
    }

    public void refreshData() {
        Log.d(TAG, "Refreshing table data");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tableList != null ? tableList.size() : 0;
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView tableIcon;
        TextView tableName;
        TextView tableCapacity;
        TextView tableStatus;
        TextView orderCount;
        View statusIndicator;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // ✅ FIXED: Đảm bảo tất cả findViewById khớp với layout XML
            cardView = (CardView) itemView;
            tableIcon = itemView.findViewById(R.id.table_icon);
            tableName = itemView.findViewById(R.id.table_name);
            tableCapacity = itemView.findViewById(R.id.table_capacity);
            tableStatus = itemView.findViewById(R.id.table_status);
            orderCount = itemView.findViewById(R.id.order_count);
            statusIndicator = itemView.findViewById(R.id.status_indicator);

            // ✅ Log để giúp debug view nào không tìm thấy
            if (tableName == null) Log.e(TAG, "ViewHolder Error: R.id.table_name not found in layout.");
            if (tableCapacity == null) Log.e(TAG, "ViewHolder Error: R.id.table_capacity not found in layout.");
            if (tableStatus == null) Log.e(TAG, "ViewHolder Error: R.id.table_status not found in layout.");
        }
    }
}