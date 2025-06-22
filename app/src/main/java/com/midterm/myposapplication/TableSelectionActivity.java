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
    implements OnTableClickListener, OrderAdapter.OnOrderClickListener, OrderDataManager.OrderDataListener {

    private static final String TAG = "TableSelectionActivity";

    // UI Components
    private RecyclerView insideTablesRecycler;
    private RecyclerView outsideTablesRecycler;
    private RecyclerView orderStatusRecycler;
    private TableAdapter insideTableAdapter;
    private TableAdapter outsideTableAdapter;
    private OrderAdapter orderAdapter;

    // Data
    private List<Table> insideTables;
    private List<Table> outsideTables;
    private List<Order> ordersList;

    // Selected drink information
    private String selectedDrinkId;
    private String selectedDrinkName;
    private double selectedDrinkPrice;
    private String selectedDrinkSize;
    private int selectedDrinkImage;

    // ✅ Data Manager
    private OrderDataManager orderDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_table_screen);

        // ✅ Initialize data manager and register listener
        orderDataManager = OrderDataManager.getInstance();
        orderDataManager.addListener(this);

        initializeViews();
        getDrinkInformation();
        setupTableData();
        setupRecyclerViews();
        setupOrderStatus();
        setupBottomNavigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ✅ Unregister listener
        if (orderDataManager != null) {
            orderDataManager.removeListener(this);
        }
    }

    // ✅ OrderDataListener implementation
    @Override
    public void onOrdersUpdated(List<Order> orders) {
        ordersList = orders;
        if (orderAdapter != null) {
            orderAdapter.updateOrders(orders);
        }
    }

    @Override
    public void onOrderStatusChanged(Order order) {
        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onOrderAdded(Order order) {
        // Handle new order if needed
    }

    @Override
    public void onOrderRemoved(String orderId) {
        // Handle order removal if needed
    }

    private void initializeViews() {
        insideTablesRecycler = findViewById(R.id.inside_tables_recycler);
        outsideTablesRecycler = findViewById(R.id.outside_tables_recycler);
        orderStatusRecycler = findViewById(R.id.order_status_recycler);
    }

    private void getDrinkInformation() {
        Intent intent = getIntent();
        selectedDrinkId = intent.getStringExtra("SELECTED_DRINK_ID");
        selectedDrinkName = intent.getStringExtra("SELECTED_DRINK_NAME");
        selectedDrinkPrice = intent.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
        selectedDrinkSize = intent.getStringExtra("SELECTED_DRINK_SIZE");
        selectedDrinkImage = intent.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.placeholder_drink);
    }

    private void setupTableData() {
        insideTables = Local_Database_Staff.getInstance().getInsideTables();
        outsideTables = Local_Database_Staff.getInstance().getOutsideTables();
    }

    private void setupRecyclerViews() {
        if (insideTablesRecycler != null && insideTables != null) {
            insideTableAdapter = new TableAdapter(insideTables, this);
            insideTablesRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            insideTablesRecycler.setAdapter(insideTableAdapter);
        }

        if (outsideTablesRecycler != null && outsideTables != null) {
            outsideTableAdapter = new TableAdapter(outsideTables, this);
            outsideTablesRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            outsideTablesRecycler.setAdapter(outsideTableAdapter);
        }
    }

    private void setupOrderStatus() {
        if (orderStatusRecycler != null) {
            // ✅ Use OrderDataManager instead of Local_Database_Staff directly
            ordersList = orderDataManager.getAllOrders();
            
            orderAdapter = new OrderAdapter(ordersList, this);
            orderStatusRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            orderStatusRecycler.setAdapter(orderAdapter);
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
                    return true;
                } else if (itemId == R.id.nav_cart) {
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

    @Override
    public void onOrderClick(Order order) {
        if (order.getOrderStatus() == Order.OrderStatus.READY) {
            order.updateOrderStatus(Order.OrderStatus.SERVING);
            orderAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Order " + order.getOrderNumber() + " đang được phục vụ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Order " + order.getOrderNumber() + " details", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ OrderDataListener implementation
    @Override
    public void onOrderStatusUpdate(Order order, Order.OrderStatus newStatus) {
        // ✅ Use OrderDataManager for consistent updates
        orderDataManager.updateOrderStatus(order, newStatus);
    }

    @Override
    public void onPaymentStatusUpdate(Order order, Order.PaymentStatus newStatus) {
        // ✅ Use OrderDataManager for consistent updates
        orderDataManager.updatePaymentStatus(order, newStatus);
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