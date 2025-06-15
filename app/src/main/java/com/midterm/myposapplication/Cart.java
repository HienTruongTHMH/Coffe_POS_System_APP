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

public class Cart extends AppCompatActivity implements OrderManagementAdapter.OnOrderCardClickListener {

    private RecyclerView ordersRecyclerView;
    private TextView totalOrdersCount;
    private OrderManagementAdapter orderManagementAdapter;
    private List<OrderManagement> allOrders;
    private List<OrderManagement> filteredOrders;
    private String currentFilter = "all";

    // Filter tabs
    private TextView btnAll, btnPreparing, btnServing, btnWaitingPayment, btnPaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        // ✅ Fix: Handle window insets properly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupOrdersData();
        setupRecyclerView();
        setupFilterTabs();
        setupBottomNavigation();
        updateOrderCount();
    }

    private void initializeViews() {
        ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        totalOrdersCount = findViewById(R.id.total_orders_count);
        
        // Filter tabs
        btnAll = findViewById(R.id.btn_all);
        btnPreparing = findViewById(R.id.btn_preparing);
        btnServing = findViewById(R.id.btn_serving);
        btnWaitingPayment = findViewById(R.id.btn_waiting_payment);
        btnPaid = findViewById(R.id.btn_paid);
    }

    private void setupOrdersData() {
        // ✅ Sample data using existing colors
        allOrders = new ArrayList<>();
        allOrders.add(new OrderManagement("1", "#01015", "Regular", "Masud Rana", "paid", "paid", 250, "Inside, table 2", 3));
        allOrders.add(new OrderManagement("2", "#01016", "Regular", "Masud Rana", "waiting_payment", "pending", 250, "Outside, chair 1", 2));
        allOrders.add(new OrderManagement("3", "#01017", "Regular", "Masud Rana", "serving", "pending", 250, "Inside, table 5", 1));
        allOrders.add(new OrderManagement("4", "#01018", "Regular", "Masud Rana", "preparing", "pending", 250, "Inside, table 8", 4));

        filteredOrders = new ArrayList<>(allOrders);
    }

    private void setupRecyclerView() {
        orderManagementAdapter = new OrderManagementAdapter(filteredOrders, this);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(orderManagementAdapter);
    }

    private void setupFilterTabs() {
        btnAll.setOnClickListener(v -> filterOrders("all"));
        btnPreparing.setOnClickListener(v -> filterOrders("preparing"));
        btnServing.setOnClickListener(v -> filterOrders("serving"));
        btnWaitingPayment.setOnClickListener(v -> filterOrders("waiting_payment"));
        btnPaid.setOnClickListener(v -> filterOrders("paid"));
    }

    private void filterOrders(String filter) {
        currentFilter = filter;
        updateFilterTabs();
        
        filteredOrders.clear();
        
        if ("all".equals(filter)) {
            filteredOrders.addAll(allOrders);
        } else {
            for (OrderManagement order : allOrders) {
                if (filter.equals(order.getOrderStatus())) {
                    filteredOrders.add(order);
                }
            }
        }
        
        orderManagementAdapter.updateOrders(filteredOrders);
        updateOrderCount();
    }

    private void updateFilterTabs() {
        // Reset all tabs
        resetTabStyle(btnAll);
        resetTabStyle(btnPreparing);
        resetTabStyle(btnServing);
        resetTabStyle(btnWaitingPayment);
        resetTabStyle(btnPaid);
        
        // Set selected tab
        switch (currentFilter) {
            case "all":
                setSelectedTabStyle(btnAll);
                break;
            case "preparing":
                setSelectedTabStyle(btnPreparing);
                break;
            case "serving":
                setSelectedTabStyle(btnServing);
                break;
            case "waiting_payment":
                setSelectedTabStyle(btnWaitingPayment);
                break;
            case "paid":
                setSelectedTabStyle(btnPaid);
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
        bottomNavigationView.setSelectedItemId(R.id.nav_cart); // ✅ Set selected
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_order) {
                Intent intent = new Intent(Cart.this, MainActivity.class);
                startActivity(intent);
                finish(); // ✅ Finish current activity
                return true;
            } else if (itemId == R.id.nav_list) {
                Intent intent = new Intent(Cart.this, TableSelectionActivity.class);
                startActivity(intent);
                finish(); // ✅ Finish current activity
                return true;
            } else if (itemId == R.id.nav_cart) {
                return true; // Already on this screen
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
    public void onOrderCardClick(OrderManagement order) {
        // TODO: Navigate to order details screen
        Toast.makeText(this, "Order details: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderStatusUpdate(OrderManagement order, String newStatus) {
        order.setOrderStatus(newStatus);
        orderManagementAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Order " + order.getOrderNumber() + " status updated", Toast.LENGTH_SHORT).show();
    }
}