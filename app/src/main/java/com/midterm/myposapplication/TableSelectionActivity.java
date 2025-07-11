package com.midterm.myposapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class TableSelectionActivity extends AppCompatActivity 
    implements OnTableClickListener, OrderStatusAdapter.OnOrderClickListener, OrderManager.OrderListener {

    private static final String TAG = "TableSelectionActivity";

    // UI Components
    private RecyclerView insideTablesRecycler;
    private RecyclerView outsideTablesRecycler;
    private RecyclerView orderStatusRecycler;
    
    // ✅ Add filter tab TextViews
    private TextView tabAll, tabPreparing, tabServing;
    private String currentStatusFilter = "all"; // ✅ Default to "all"
    
    // Adapters
    private TableAdapter insideTableAdapter;
    private TableAdapter outsideTableAdapter;
    private OrderStatusAdapter orderStatusAdapter;

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

    // ✅ Replace with new managers
    private OrderManager orderManager;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_table_screen);

        // ✅ Initialize managers and register listener
        orderManager = OrderManager.getInstance();
        databaseManager = DatabaseManager.getInstance();
        orderManager.addListener(this);

        initializeViews();
        getDrinkInformation();
        setupTableData();
        setupRecyclerViews();
        setupStatusFilterTabs();
        setupOrderStatus();
        setupBottomNavigation();
        
        Log.d(TAG, "TableSelectionActivity initialized successfully");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderManager != null) {
            orderManager.removeListener(this);
        }
    }

    // ✅ OrderManager.OrderListener implementation
    @Override
    public void onOrdersUpdated(List<Order> orders) {
        ordersList = orders;
        if (orderStatusAdapter != null) {
            orderStatusAdapter.updateOrders(orders);
        }
        // ✅ FIXED: Refresh table adapters when orders change
        refreshTableAdapters();
        Log.d(TAG, "Orders updated: " + orders.size() + " total orders");
    }

    @Override
    public void onOrderStatusChanged(Order order) {
        if (orderStatusAdapter != null) {
            orderStatusAdapter.notifyDataSetChanged();
        }
        // ✅ FIXED: Refresh table adapters when order status changes
        refreshTableAdapters();
        Log.d(TAG, "Order status changed: " + order.getOrderNumber());
    }

    @Override
    public void onOrderAdded(Order order) {
        // ✅ FIXED: Refresh table adapters when new order is added
        refreshTableAdapters();
        if (orderStatusAdapter != null) {
            orderStatusAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "Order added: " + order.getOrderNumber());
    }

    @Override
    public void onOrderRemoved(String orderId) {
        // ✅ FIXED: Refresh table adapters when order is removed
        refreshTableAdapters();
        if (orderStatusAdapter != null) {
            orderStatusAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "Order removed: " + orderId);
    }

    // ✅ FIXED: Improved refresh method
    private void refreshTableAdapters() {
        if (insideTableAdapter != null) {
            insideTableAdapter.refreshData();
        }
        if (outsideTableAdapter != null) {
            outsideTableAdapter.refreshData();
        }
        Log.d(TAG, "Table adapters refreshed");
    }

    private void initializeViews() {
        insideTablesRecycler = findViewById(R.id.inside_tables_recycler);
        outsideTablesRecycler = findViewById(R.id.outside_tables_recycler);
        orderStatusRecycler = findViewById(R.id.order_status_recycler);
        
        // ✅ Cập nhật: Chỉ giữ lại 2 tab status thay vì 3 tab cũ
        tabAll = findViewById(R.id.tab_all);
        tabPreparing = findViewById(R.id.tab_preparing);
        tabServing = findViewById(R.id.tab_serving);  // Đổi tên từ tabReady thành tabServing
        
        // ✅ Xóa bỏ tabReady vì không còn trạng thái này
        // tabReady = findViewById(R.id.tab_ready);
        
        // ✅ Log cập nhật
        Log.d(TAG, "Tab views initialized: all=" + (tabAll != null) + 
            ", preparing=" + (tabPreparing != null) + 
            ", serving=" + (tabServing != null));
    }

    private void getDrinkInformation() {
        Intent intent = getIntent();
        selectedDrinkId = intent.getStringExtra("SELECTED_DRINK_ID");
        selectedDrinkName = intent.getStringExtra("SELECTED_DRINK_NAME");
        selectedDrinkPrice = intent.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
        selectedDrinkSize = intent.getStringExtra("SELECTED_DRINK_SIZE");
        selectedDrinkImage = intent.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.placeholder_drink);
        
        Log.d(TAG, "Drink information: " + selectedDrinkName + " - $" + selectedDrinkPrice);
    }

    private void setupTableData() {
        // ✅ Use DatabaseManager instead of Local_Database_Staff
        insideTables = databaseManager.getInsideTables();
        outsideTables = databaseManager.getOutsideTables();
        
        Log.d(TAG, "Table data setup: " + insideTables.size() + " inside, " + outsideTables.size() + " outside");
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
        
        Log.d(TAG, "RecyclerViews setup completed");
    }

    // ✅ Add method to setup filter tabs
    private void setupStatusFilterTabs() {
        if (tabAll != null) {
            tabAll.setOnClickListener(v -> filterOrdersByStatus("all"));
        }
        if (tabPreparing != null) {
            tabPreparing.setOnClickListener(v -> filterOrdersByStatus("preparing"));
        }
        if (tabServing != null) {
            // ✅ Cập nhật: Đổi tên tab từ "ready" thành "on_service"
            tabServing.setOnClickListener(v -> filterOrdersByStatus("on_service"));
        }
        // ✅ Xóa bỏ tabServing (cũ) vì đã được thay thế bởi tabServing (mới)
        
        Log.d(TAG, "Status filter tabs setup completed");
    }

    // ✅ Add method to filter orders using OrderManager
    private void filterOrdersByStatus(String status) {
        currentStatusFilter = status;
        updateStatusFilterSelection();

        List<Order> filteredList;
        if ("all".equals(status)) {
            filteredList = orderManager.getActiveOrders();
        } else {
            Order.OrderStatus targetStatus = getOrderStatusFromString(status);
            if (targetStatus != null) {
                filteredList = orderManager.getOrdersByStatus(targetStatus);
            } else {
                filteredList = new ArrayList<>();
            }
        }
        
        if (orderStatusAdapter != null) {
            orderStatusAdapter.updateOrders(filteredList);
        }
        
        Log.d(TAG, "Filtered orders by status '" + status + "': " + filteredList.size() + " orders");
    }

    // ✅ Add helper to convert string to enum
    private Order.OrderStatus getOrderStatusFromString(String status) {
        switch (status) {
            case "on_service":
                return Order.OrderStatus.ON_SERVICE;
            case "preparing":
            default:
                return Order.OrderStatus.PREPARING;
        }
    }

    // ✅ Add method to update tab UI
    private void updateStatusFilterSelection() {
        resetTabStyle(tabAll);
        resetTabStyle(tabPreparing);
        resetTabStyle(tabServing);
        // ✅ Xóa bỏ tabReady vì không còn trạng thái này

        switch (currentStatusFilter) {
            case "all":
                setSelectedTabStyle(tabAll);
                break;
            case "preparing":
                setSelectedTabStyle(tabPreparing);
                break;
            case "on_service":
                setSelectedTabStyle(tabServing);
                break;
            // ✅ Xóa bỏ trạng thái "ready", "serving" vì không còn nữa
        }
    }

    private void setSelectedTabStyle(TextView tab) {
        if (tab != null) {
            tab.setBackground(getResources().getDrawable(R.drawable.tab_rounded_selected));
            tab.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void resetTabStyle(TextView tab) {
        if (tab != null) {
            tab.setBackground(getResources().getDrawable(R.drawable.tab_rounded_unselected));
            tab.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private void setupOrderStatus() {
        if (orderStatusRecycler != null) {
            // ✅ Initially load all active orders using OrderManager
            ordersList = orderManager.getActiveOrders();
            
            orderStatusAdapter = new OrderStatusAdapter(ordersList, this);
            orderStatusRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            orderStatusRecycler.setAdapter(orderStatusAdapter);
            
            Log.d(TAG, "Order status setup with " + ordersList.size() + " orders");
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
                    // ✅ THÊM MỚI: Chuyển đến màn hình Profile
                    Intent profileIntent = new Intent(TableSelectionActivity.this, Profile.class);
                    startActivity(profileIntent);
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
        Toast.makeText(this, "Clicked Order: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Order clicked: " + order.getOrderNumber());
    }
    
    private void showOrderConfirmationDialog(Table table) {
        // ✅ Check if table has active orders using OrderManager
        List<Order> activeOrders = orderManager.getActiveOrdersByTable(table.getNumber());
        boolean hasActiveOrders = !activeOrders.isEmpty();
        
        if (hasActiveOrders) {
            new AlertDialog.Builder(this)
                .setTitle("Table Occupied")
                .setMessage("Table " + table.getName() + " is occupied. Do you want to add order to this table?")
                .setPositiveButton("Add Order", (dialog, which) -> confirmOrder(table))
                .setNegativeButton("Cancel", null)
                .show();
        } else {
            confirmOrder(table);
        }
    }

    private void confirmOrder(Table table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Order");

        String message = String.format(
                "Table: %s\n" +
                "Item: %s (%s)\n" +
                "Price: $%.2f\n\n" +
                "Confirm adding this item to order?",
                table.getName(),
                selectedDrinkName != null ? selectedDrinkName : "N/A",
                selectedDrinkSize != null ? selectedDrinkSize : "M",
                selectedDrinkPrice
        );

        builder.setMessage(message);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_TABLE_NUMBER", table.getNumber());
            resultIntent.putExtra("SELECTED_TABLE_NAME", table.getName());
            resultIntent.putExtra("SELECTED_DRINK_ID", selectedDrinkId);
            resultIntent.putExtra("SELECTED_DRINK_NAME", selectedDrinkName);
            resultIntent.putExtra("SELECTED_DRINK_PRICE", selectedDrinkPrice);
            resultIntent.putExtra("SELECTED_DRINK_SIZE", selectedDrinkSize);
            resultIntent.putExtra("SELECTED_DRINK_IMAGE", selectedDrinkImage);

            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Added " + selectedDrinkName + " to " + table.getName(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Order confirmed for table " + table.getName());
            finish();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void selectTableForNewOrder(Table table) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("SELECTED_TABLE_NUMBER", table.getNumber());
        resultIntent.putExtra("SELECTED_TABLE_NAME", table.getName());
        resultIntent.putExtra("MODE", "TABLE_SELECTED");
        
        Log.d(TAG, "Table selected for new order: " + table.getName());
        startActivity(resultIntent);
        finish();
    }
}