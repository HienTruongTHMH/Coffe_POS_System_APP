package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    implements OrderAdapter.OnOrderClickListener, OrderManager.OrderListener {

    private static final String TAG = "Cart";
    
    private RecyclerView ordersRecyclerView;
    private TextView totalOrdersCount;
    private OrderAdapter orderAdapter;
    private List<Order> allOrders;
    private List<Order> filteredOrders;
    private String currentFilter = "all";

    // Filter tabs
    private TextView btnAll, btnPreparing, btnReady, btnServing, btnCompleted;

    // ✅ Replace with new managers
    private OrderManager orderManager;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Initialize managers and register listener
        orderManager = OrderManager.getInstance();
        databaseManager = DatabaseManager.getInstance();
        orderManager.addListener(this);

        initializeViews();
        setupOrdersData();
        setupRecyclerView();
        setupFilterTabs();
        setupBottomNavigation();
        updateOrderCount();
        
        Log.d(TAG, "Cart activity initialized successfully");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ✅ Unregister listener to prevent memory leaks
        if (orderManager != null) {
            orderManager.removeListener(this);
        }
    }

    // ✅ OrderManager.OrderListener implementation
    @Override
    public void onOrdersUpdated(List<Order> orders) {
        allOrders = orders;
        filterOrders(currentFilter); // Re-apply current filter
        Log.d(TAG, "Orders updated: " + orders.size() + " total orders");
    }

    @Override
    public void onOrderStatusChanged(Order order) {
        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "Order status changed: " + order.getOrderNumber());
    }

    @Override
    public void onOrderAdded(Order order) {
        // Update count display
        updateOrderCount();
        Log.d(TAG, "Order added: " + order.getOrderNumber());
    }

    @Override
    public void onOrderRemoved(String orderId) {
        // Update count display
        updateOrderCount();
        Log.d(TAG, "Order removed: " + orderId);
    }

    private void initializeViews() {
        ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        totalOrdersCount = findViewById(R.id.total_orders_count);

        // ✅ FIXED: Try different ID variations for filter tabs
        btnAll = findViewById(R.id.tab_all);
        if (btnAll == null) btnAll = findViewById(R.id.btn_all);

        btnPreparing = findViewById(R.id.tab_preparing);
        if (btnPreparing == null) btnPreparing = findViewById(R.id.btn_preparing);

        btnServing = findViewById(R.id.tab_serving);
        if (btnServing == null) btnServing = findViewById(R.id.btn_serving);

        btnCompleted = findViewById(R.id.btn_completed);
        if (btnCompleted == null) btnCompleted = findViewById(R.id.btn_completed);

        // ✅ Log which views were found for debugging
        Log.d(TAG, "Filter tabs found - " +
                "all: " + (btnAll != null) +
                ", preparing: " + (btnPreparing != null) +
                ", ready: " + (btnReady != null) +
                ", serving: " + (btnServing != null) +
                ", completed: " + (btnCompleted != null));
    }

    private void setupOrdersData() {
        // ✅ Use OrderManager instead of Local_Database_Staff directly
        allOrders = orderManager.getAllOrders();
        filteredOrders = new ArrayList<>(allOrders);
        Log.d(TAG, "Orders data setup: " + allOrders.size() + " total orders");
    }

    private void setupRecyclerView() {
        CartOrderAdapter cartOrderAdapter = new CartOrderAdapter(filteredOrders, 
            new CartOrderAdapter.OnCartOrderClickListener() {
                @Override
                public void onOrderClick(Order order) {
                    // Navigate to Payment Activity when order is clicked
                    Intent intent = new Intent(Cart.this, PaymentActivity.class);
                    
                    // Pass order information to the Payment Activity
                    intent.putExtra("ORDER_ID", order.getOrderId());
                    intent.putExtra("ORDER_NUMBER", order.getOrderNumber());
                    intent.putExtra("ORDER_TOTAL", order.getTotalAmount());  // Sử dụng getTotalAmount() thay vì calculateTotal()
                    intent.putExtra("TABLE_INFO", order.getTableInfo());
                    
                    // Start Payment Activity
                    startActivity(intent);
                    
                    Log.d(TAG, "Navigating to Payment for order: " + order.getOrderNumber());
                }
                
                @Override
                public void onOrderStatusUpdate(Order order, Order.OrderStatus newStatus) {
                    // Implementation remains the same
                    orderManager.updateOrderStatus(order.getOrderId(), newStatus);
                    Log.d(TAG, "Order status updated: " + order.getOrderNumber() + " -> " + newStatus.getDisplayName());
                }
                
                @Override
                public void onPaymentStatusUpdate(Order order, Order.PaymentStatus newStatus) {
                    // Implementation remains the same
                    order.updatePaymentStatus(newStatus);
                    databaseManager.updateOrder(order);
                    
                    if (orderAdapter != null) {
                        orderAdapter.notifyDataSetChanged();
                    }
                    
                    Log.d(TAG, "Payment status updated: " + order.getOrderNumber() + " -> " + newStatus.getDisplayName());
                }
            });
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(cartOrderAdapter);
        Log.d(TAG, "RecyclerView setup completed");
    }

    private void setupFilterTabs() {
        if (btnAll != null) {
            btnAll.setOnClickListener(v -> filterOrders("all"));
        }
        if (btnPreparing != null) {
            btnPreparing.setOnClickListener(v -> filterOrders("preparing"));
        }
        if (btnReady != null) {
            btnReady.setOnClickListener(v -> filterOrders("ready"));
        }
        if (btnServing != null) {
            btnServing.setOnClickListener(v -> filterOrders("serving"));
        }
        if (btnCompleted != null) {
            btnCompleted.setOnClickListener(v -> filterOrders("completed"));
        }
        
        Log.d(TAG, "Filter tabs setup completed");
    }

    private void filterOrders(String filter) {
        currentFilter = filter;
        updateFilterTabs();
        
        if ("all".equals(filter)) {
            filteredOrders = new ArrayList<>(allOrders);
        } else {
            // ✅ Use OrderManager for consistent filtering
            Order.OrderStatus targetStatus = getOrderStatusFromString(filter);
            filteredOrders = orderManager.getOrdersByStatus(targetStatus);
        }
        
        if (orderAdapter != null) {
            orderAdapter.updateOrders(filteredOrders);
        }
        updateOrderCount();
        
        Log.d(TAG, "Filtered orders by '" + filter + "': " + filteredOrders.size() + " orders");
    }

    private Order.OrderStatus getOrderStatusFromString(String status) {
        switch (status) {
            case "on_service":
                return Order.OrderStatus.ON_SERVICE;
            case "preparing":
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
        if (tab != null) {
            tab.setBackground(getResources().getDrawable(R.drawable.tab_rounded_selected));
            tab.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void resetTabStyle(TextView tab) {
        if (tab != null) {
            tab.setBackground(getResources().getDrawable(R.drawable.tab_rounded_unselected));
            tab.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    private void updateOrderCount() {
        if (totalOrdersCount != null && filteredOrders != null) {
            totalOrdersCount.setText(filteredOrders.size() + " Orders");
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        if (bottomNavigationView != null) {
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
                    // ✅ THÊM MỚI: Chuyển đến màn hình Profile
                    Intent profileIntent = new Intent(Cart.this, Profile.class);
                    startActivity(profileIntent);
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public void onOrderClick(Order order) {
        Toast.makeText(this, "Order details: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Order clicked: " + order.getOrderNumber());
    }

    @Override
    public void onOrderStatusUpdate(Order order, Order.OrderStatus newStatus) {
        // ✅ Use OrderManager for consistent updates
        orderManager.updateOrderStatus(order.getOrderId(), newStatus);
        Log.d(TAG, "Order status updated: " + order.getOrderNumber() + " -> " + newStatus.getDisplayName());
    }

    @Override
    public void onPaymentStatusUpdate(Order order, Order.PaymentStatus newStatus) {
        // ✅ Update payment status directly on order object
        order.updatePaymentStatus(newStatus);
        databaseManager.updateOrder(order);
        
        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }
        
        Log.d(TAG, "Payment status updated: " + order.getOrderNumber() + " -> " + newStatus.getDisplayName());
    }
        @Override
        protected void onResume() {
            super.onResume();
            
            // Tải lại thông tin đơn hàng từ database để đảm bảo dữ liệu mới nhất
        }
}