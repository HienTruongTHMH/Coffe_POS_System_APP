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
    implements OnTableClickListener, OrderStatusAdapter.OnOrderClickListener, OrderDataManager.OrderDataListener {

    private static final String TAG = "TableSelectionActivity";

    // UI Components
    private RecyclerView insideTablesRecycler;
    private RecyclerView outsideTablesRecycler;
    private RecyclerView orderStatusRecycler;
    
    // ✅ Add filter tab TextViews
    private TextView tabAll, tabPreparing, tabReady, tabServing;
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

    // Data Manager
    private OrderDataManager orderDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_table_screen);

        // Initialize data manager and register listener
        orderDataManager = OrderDataManager.getInstance();
        orderDataManager.addListener(this);

        initializeViews();
        getDrinkInformation();
        setupTableData();
        setupRecyclerViews();
        setupStatusFilterTabs(); // ✅ Add this method
        setupOrderStatus();
        setupBottomNavigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderDataManager != null) {
            orderDataManager.removeListener(this);
        }
    }

    // ✅ OrderDataListener implementation
    @Override
    public void onOrdersUpdated(List<Order> orders) {
        ordersList = orders;
        if (orderStatusAdapter != null) {
            orderStatusAdapter.updateOrders(orders);
        }
        refreshTableAdapters();
    }

    @Override
    public void onOrderStatusChanged(Order order) {
        if (orderStatusAdapter != null) {
            int index = ordersList.indexOf(order);
            if (index != -1) {
                orderStatusAdapter.notifyItemChanged(index);
            } else {
                orderStatusAdapter.notifyDataSetChanged();
            }
        }
        refreshTableAdapters();
    }

    @Override
    public void onOrderAdded(Order order) {
        // Handle new order if needed
    }

    @Override
    public void onOrderRemoved(String orderId) {
        // Handle order removal if needed
    }

    private void refreshTableAdapters() {
        if (insideTableAdapter != null) {
            insideTableAdapter.notifyDataSetChanged();
        }
        if (outsideTableAdapter != null) {
            outsideTableAdapter.notifyDataSetChanged();
        }
    }

    private void initializeViews() {
        insideTablesRecycler = findViewById(R.id.inside_tables_recycler);
        outsideTablesRecycler = findViewById(R.id.outside_tables_recycler);
        orderStatusRecycler = findViewById(R.id.order_status_recycler);
        
        // ✅ Initialize filter tabs - check if they exist in layout
        tabAll = findViewById(R.id.tab_all);
        tabPreparing = findViewById(R.id.tab_preparing);
        tabReady = findViewById(R.id.tab_ready);
        tabServing = findViewById(R.id.tab_serving);
        
        // ✅ Log to debug which views are found
        android.util.Log.d(TAG, "Tab views initialized: all=" + (tabAll != null) + 
            ", preparing=" + (tabPreparing != null) + 
            ", ready=" + (tabReady != null) + 
            ", serving=" + (tabServing != null));
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

    // ✅ Add method to setup filter tabs
    private void setupStatusFilterTabs() {
        if (tabAll != null) {
            tabAll.setOnClickListener(v -> filterOrdersByStatus("all"));
        }
        if (tabPreparing != null) {
            tabPreparing.setOnClickListener(v -> filterOrdersByStatus("preparing"));
        }
        if (tabReady != null) {
            tabReady.setOnClickListener(v -> filterOrdersByStatus("ready"));
        }
        if (tabServing != null) {
            tabServing.setOnClickListener(v -> filterOrdersByStatus("serving"));
        }
    }

    // ✅ Add method to filter orders
    private void filterOrdersByStatus(String status) {
        currentStatusFilter = status;
        updateStatusFilterSelection();

        List<Order> filteredList;
        if ("all".equals(status)) {
            filteredList = orderDataManager.getAllActiveOrders();
        } else {
            Order.OrderStatus targetStatus = getOrderStatusFromString(status);
            if (targetStatus != null) {
                filteredList = orderDataManager.getOrdersByStatus(targetStatus);
            } else {
                filteredList = new ArrayList<>();
            }
        }
        
        if (orderStatusAdapter != null) {
            orderStatusAdapter.updateOrders(filteredList);
        }
    }

    // ✅ Add helper to convert string to enum
    private Order.OrderStatus getOrderStatusFromString(String status) {
        switch (status) {
            case "preparing":
                return Order.OrderStatus.PREPARING;
            case "ready":
                return Order.OrderStatus.READY;
            case "serving":
                return Order.OrderStatus.SERVING;
            default:
                return null;
        }
    }

    // ✅ Add method to update tab UI
    private void updateStatusFilterSelection() {
        resetTabStyle(tabAll);
        resetTabStyle(tabPreparing);
        resetTabStyle(tabReady);
        resetTabStyle(tabServing);

        switch (currentStatusFilter) {
            case "all":
                setSelectedTabStyle(tabAll);
                break;
            case "preparing":
                setSelectedTabStyle(tabPreparing);
                break;
            case "ready":
                setSelectedTabStyle(tabReady);
                break;
            case "serving":
                setSelectedTabStyle(tabServing);
                break;
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
            // ✅ Initially load all active orders
            ordersList = orderDataManager.getAllActiveOrders();
            
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
        Toast.makeText(this, "Clicked Order: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
        // Add detail logic if needed
    }
    
    private void showOrderConfirmationDialog(Table table) {
        if (!table.isAvailable()) {
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
        
        startActivity(resultIntent);
        finish();
    }
}