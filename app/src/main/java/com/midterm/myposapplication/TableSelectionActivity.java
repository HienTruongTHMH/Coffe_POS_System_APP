package com.midterm.myposapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class TableSelectionActivity extends AppCompatActivity 
    implements OnTableClickListener, OrderStatusAdapter.OnOrderStatusClickListener {

    private static final String TAG = "TableSelectionActivity";

    // UI Components
    private RecyclerView insideTablesRecycler;
    private RecyclerView outsideTablesRecycler;
    private RecyclerView orderStatusRecycler;
    private TableAdapter insideTableAdapter;
    private TableAdapter outsideTableAdapter;
    private OrderStatusAdapter orderStatusAdapter;

    // Data
    private List<Table> insideTables;
    private List<Table> outsideTables;
    private List<OrderStatus> orderStatusList;

    // Selected drink information
    private String selectedDrinkId;
    private String selectedDrinkName;
    private double selectedDrinkPrice;
    private String selectedDrinkSize;
    private int selectedDrinkImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_table_screen);

        Log.d(TAG, "onCreate started");

        initializeViews();
        getDrinkInformation();
        setupTableData();
        setupRecyclerViews();
        setupOrderStatus();
        setupBottomNavigation();

        Log.d(TAG, "onCreate completed");
    }

    private void initializeViews() {
        insideTablesRecycler = findViewById(R.id.inside_tables_recycler);
        outsideTablesRecycler = findViewById(R.id.outside_tables_recycler);
        orderStatusRecycler = findViewById(R.id.order_status_recycler);

        Log.d(TAG, "Views initialized - Inside: " + (insideTablesRecycler != null) + 
                   ", Outside: " + (outsideTablesRecycler != null) +
                   ", OrderStatus: " + (orderStatusRecycler != null));
    }

    private void getDrinkInformation() {
        Intent intent = getIntent();
        selectedDrinkId = intent.getStringExtra("SELECTED_DRINK_ID");
        selectedDrinkName = intent.getStringExtra("SELECTED_DRINK_NAME");
        selectedDrinkPrice = intent.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
        selectedDrinkSize = intent.getStringExtra("SELECTED_DRINK_SIZE");
        selectedDrinkImage = intent.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.placeholder_drink);

        Log.d(TAG, "Drink info - Name: " + selectedDrinkName + ", Price: " + selectedDrinkPrice);
    }

    private void setupTableData() {
        // Inside tables
        insideTables = new ArrayList<>();
        insideTables.add(new Table("1", "Bàn 1", "occupied", 4));
        insideTables.add(new Table("2", "Bàn 2", "preparing", 4));
        insideTables.add(new Table("3", "Bàn 3", "available", 4));

        // Outside tables  
        outsideTables = new ArrayList<>();
        outsideTables.add(new Table("4", "Bàn 4", "occupied", 6));
        outsideTables.add(new Table("5", "Bàn 5", "available", 6));
        outsideTables.add(new Table("6", "Bàn 6", "available", 4));

        Log.d(TAG, "Table data setup - Inside: " + insideTables.size() + 
                   ", Outside: " + outsideTables.size());
    }

    private void setupRecyclerViews() {
        if (insideTablesRecycler != null && insideTables != null) {
            insideTableAdapter = new TableAdapter(insideTables, this);
            insideTablesRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            insideTablesRecycler.setAdapter(insideTableAdapter);
            Log.d(TAG, "Inside RecyclerView setup completed");
        } else {
            Log.e(TAG, "Inside RecyclerView setup failed");
        }

        if (outsideTablesRecycler != null && outsideTables != null) {
            outsideTableAdapter = new TableAdapter(outsideTables, this);
            outsideTablesRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            outsideTablesRecycler.setAdapter(outsideTableAdapter);
            Log.d(TAG, "Outside RecyclerView setup completed");
        } else {
            Log.e(TAG, "Outside RecyclerView setup failed");
        }
    }

    private void setupOrderStatus() {
        if (orderStatusRecycler != null) {
            orderStatusList = new ArrayList<>();
            orderStatusList.add(new OrderStatus("1", "#2100", "Inside, table 2", "ready", 3));
            orderStatusList.add(new OrderStatus("2", "#2101", "Outside, chair 1", "preparing", 2));

            orderStatusAdapter = new OrderStatusAdapter(orderStatusList, this);
            orderStatusRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            orderStatusRecycler.setAdapter(orderStatusAdapter);
            
            Log.d(TAG, "Order Status RecyclerView setup completed");
        } else {
            Log.e(TAG, "Order Status RecyclerView not found");
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_list);
            
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                if (itemId == R.id.nav_order) {
                    Intent intent = new Intent(TableSelectionActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_list) {
                    return true; // Already on this screen
                } else if (itemId == R.id.nav_cart) {
                    // ✅ Add Cart navigation
                    Intent intent = new Intent(TableSelectionActivity.this, Cart.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_package) {
                    Toast.makeText(this, "Package feature coming soon", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public void onTableClick(Table table) {
        Log.d(TAG, "Table clicked: " + table.getName());
        
        if (selectedDrinkName != null && !selectedDrinkName.isEmpty()) {
            showOrderConfirmationDialog(table);
        } else {
            selectTableForNewOrder(table);
        }
    }

    // ✅ Fix: Implement missing onOrderStatusDoubleClick method
    @Override
    public void onOrderStatusClick(OrderStatus orderStatus) {
        if ("ready".equals(orderStatus.getStatus())) {
            orderStatus.setStatus("served");
            orderStatusAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Order " + orderStatus.getOrderNumber() + " marked as served", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Order " + orderStatus.getOrderNumber() + " details", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ Fix: Add missing onOrderStatusDoubleClick method
    @Override
    public void onOrderStatusDoubleClick(OrderStatus orderStatus) {
        if ("ready".equals(orderStatus.getStatus())) {
            // Change status to in_service on double click
            orderStatus.setStatus("in_service");
            orderStatusAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Order " + orderStatus.getOrderNumber() + " đang được phục vụ", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Order " + orderStatus.getOrderNumber() + " status changed to in_service");
        } else if ("preparing".equals(orderStatus.getStatus())) {
            // Change status to ready on double click
            orderStatus.setStatus("ready");
            orderStatusAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Order " + orderStatus.getOrderNumber() + " sẵn sàng phục vụ", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Order " + orderStatus.getOrderNumber() + " status changed to ready");
        } else {
            Toast.makeText(this, "Không thể thay đổi trạng thái order " + orderStatus.getOrderNumber(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrderConfirmationDialog(Table table) {
        if (!table.isAvailable()) {
            new AlertDialog.Builder(this)
                .setTitle("Bàn đã có khách")
                .setMessage("Bàn " + table.getName() + " đã có khách. Bạn có muốn thêm order vào bàn này không?")
                .setPositiveButton("Thêm order", (dialog, which) -> confirmOrder(table))
                .setNegativeButton("Hủy", null)
                .show();
        } else {
            confirmOrder(table);
        }
    }

    private void confirmOrder(Table table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận order");

        String message = String.format(
                "Bàn: %s\n" +
                "Món: %s (%s)\n" +
                "Giá: $%.2f\n\n" +
                "Xác nhận thêm món này vào order?",
                table.getName(),
                selectedDrinkName != null ? selectedDrinkName : "N/A",
                selectedDrinkSize != null ? selectedDrinkSize : "M",
                selectedDrinkPrice
        );

        builder.setMessage(message);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_TABLE_NUMBER", table.getNumber());
            resultIntent.putExtra("SELECTED_TABLE_NAME", table.getName());
            resultIntent.putExtra("SELECTED_DRINK_ID", selectedDrinkId);
            resultIntent.putExtra("SELECTED_DRINK_NAME", selectedDrinkName);
            resultIntent.putExtra("SELECTED_DRINK_PRICE", selectedDrinkPrice);
            resultIntent.putExtra("SELECTED_DRINK_SIZE", selectedDrinkSize);
            resultIntent.putExtra("SELECTED_DRINK_IMAGE", selectedDrinkImage);

            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, "Đã thêm " + selectedDrinkName + " vào " + table.getName(), Toast.LENGTH_SHORT).show();
            finish();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void selectTableForNewOrder(Table table) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("SELECTED_TABLE_NUMBER", table.getNumber());
        resultIntent.putExtra("SELECTED_TABLE_NAME", table.getName());
        resultIntent.putExtra("MODE", "TABLE_SELECTED");
        
        startActivity(resultIntent);
        finish();
    }
}