package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity 
    implements OrderAdapter.OnOrderClickListener, OrderDataManager.OrderDataListener {

    private RecyclerView ordersRecyclerView;
    private TextView totalOrdersCount;
    private OrderAdapter orderAdapter;
    private List<Order> allOrders;
    private List<Order> filteredOrders;
    private String currentFilter = "all";

    // Filter tabs
    private TextView btnAll, btnPreparing, btnReady, btnServing, btnCompleted;

    // ✅ Data Manager
    private OrderDataManager orderDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Initialize data manager and register listener
        orderDataManager = OrderDataManager.getInstance();
        orderDataManager.addListener(this);

        initializeViews();
        setupOrdersData();
        setupRecyclerView();
        setupFilterTabs();
        setupBottomNavigation();
        updateOrderCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ✅ Unregister listener to prevent memory leaks
        if (orderDataManager != null) {
            orderDataManager.removeListener(this);
        }
    }

    // ✅ OrderDataListener implementation
    @Override
    public void onOrdersUpdated(List<Order> orders) {
        allOrders = orders;
        filterOrders(currentFilter); // Re-apply current filter
    }

    @Override
    public void onOrderStatusChanged(Order order) {
        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onOrderAdded(Order order) {
        // Update count display
        updateOrderCount();
    }

    @Override
    public void onOrderRemoved(String orderId) {
        // Update count display
        updateOrderCount();
    }

    private void initializeViews() {
        ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        totalOrdersCount = findViewById(R.id.total_orders_count);
        
        // Filter tabs
        btnAll = findViewById(R.id.btn_all);
        btnPreparing = findViewById(R.id.btn_preparing);
        btnReady = findViewById(R.id.btn_preparing);
        btnServing = findViewById(R.id.btn_serving);
        btnCompleted = findViewById(R.id.btn_completed);
    }

    private void setupOrdersData() {
        // ✅ Use OrderDataManager instead of Local_Database_Staff directly
        allOrders = orderDataManager.getAllOrders();
        filteredOrders = new ArrayList<>(allOrders);
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter(filteredOrders, this);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(orderAdapter);
    }

    private void setupFilterTabs() {
        btnAll.setOnClickListener(v -> filterOrders("all"));
        btnPreparing.setOnClickListener(v -> filterOrders("preparing"));
        btnReady.setOnClickListener(v -> filterOrders("ready"));
        btnServing.setOnClickListener(v -> filterOrders("serving"));
        btnCompleted.setOnClickListener(v -> filterOrders("completed"));
    }

    private void filterOrders(String filter) {
        currentFilter = filter;
        updateFilterTabs();
        
        if ("all".equals(filter)) {
            filteredOrders = new ArrayList<>(allOrders);
        } else {
            // ✅ Use OrderDataManager for consistent filtering
            Order.OrderStatus targetStatus = getOrderStatusFromString(filter);
            filteredOrders = orderDataManager.getOrdersByStatus(targetStatus);
        }
        
        orderAdapter.updateOrders(filteredOrders);
        updateOrderCount();
    }

    private Order.OrderStatus getOrderStatusFromString(String status) {
        switch (status) {
            case "preparing":
                return Order.OrderStatus.PREPARING;
            case "ready":
                return Order.OrderStatus.READY;
            case "serving":
                return Order.OrderStatus.SERVING;
            case "completed":
                return Order.OrderStatus.COMPLETED;
            default:
                return Order.OrderStatus.PREPARING;
        }
    }

    private void updateFilterTabs() {
        resetTabStyle(btnAll);
        resetTabStyle(btnPreparing);
        resetTabStyle(btnReady);
        resetTabStyle(btnServing);
        resetTabStyle(btnCompleted);
        
        switch (currentFilter) {
            case "all":
                setSelectedTabStyle(btnAll);
                break;
            case "preparing":
                setSelectedTabStyle(btnPreparing);
                break;
            case "ready":
                setSelectedTabStyle(btnReady);
                break;
            case "serving":
                setSelectedTabStyle(btnServing);
                break;
            case "completed":
                setSelectedTabStyle(btnCompleted);
                break;
        }
    }

    private void setSelectedTabStyle(TextView tab) {
        tab.setBackground(getResources().getDrawable(R.drawable.tab_rounded_selected));
        tab.setTextColor(getResources().getColor(R.color.white));
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackground(getResources().getDrawable(R.drawable.tab_rounded_unselected));
        tab.setTextColor(getResources().getColor(R.color.text_primary));
    }

    private void updateOrderCount() {
        totalOrdersCount.setText(filteredOrders.size() + " Orders");
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_order) {
                Intent intent = new Intent(Cart.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_list) {
                Intent intent = new Intent(Cart.this, TableSelectionActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
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

    @Override
    public void onOrderClick(Order order) {
        Toast.makeText(this, "Order details: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }

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
}