package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity 
    implements DrinkAdapter.OnDrinkClickListener,
               CurrentOrderAdapter.OnOrderItemChangeListener,
               OrderAdapter.OnOrderClickListener,
               OrderDataManager.OrderDataListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_TABLE_SELECTION = 1001;

    // UI Components
    private RecyclerView drinksRecyclerView;
    private RecyclerView currentOrderRecyclerView;
    private RecyclerView orderStatusRecycler;
    private LinearLayout currentOrderSection;
    private TextView currentTableText;

    // Adapters
    private DrinkAdapter drinkAdapter;
    private CurrentOrderAdapter currentOrderAdapter;
    private OrderAdapter orderAdapter;

    // Data
    private List<Drink> allDrinks;
    private List<Drink> filteredDrinks;
    private List<CurrentOrderItem> currentOrderItems;
    private List<Order> ordersList;

    // Current state
    private String selectedTableNumber = "";
    private String selectedTableName = "";
    private String currentStatusFilter = "preparing";

    // UI Components for status filter
    private LinearLayout statusFilterContainer;
    private TextView tabPreparing, tabReady, tabServing;

    // ✅ Data Manager
    private OrderDataManager orderDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Initialize data manager and register listener
        orderDataManager = OrderDataManager.getInstance();
        orderDataManager.addListener(this);

        initializeViews();
        setupDrinksData();
        setupRecyclerViews();
        setupStatusFilterTabs();
        setupOrderStatus();
        setupBottomNavigation();
        handleIntent();
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
        ordersList = orders;
        filterOrderStatusByType(getCurrentStatusFilter());
    }

    @Override
    public void onOrderStatusChanged(Order order) {
        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onOrderAdded(Order order) {
        Toast.makeText(this, "Đơn hàng mới: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderRemoved(String orderId) {
        // Handle order removal if needed
    }

    private Order.OrderStatus getCurrentStatusFilter() {
        switch (currentStatusFilter) {
            case "ready": return Order.OrderStatus.READY;
            case "serving": return Order.OrderStatus.SERVING;
            default: return Order.OrderStatus.PREPARING;
        }
    }

    private void initializeViews() {
        drinksRecyclerView = findViewById(R.id.drinks_recycler_view);
        currentOrderRecyclerView = findViewById(R.id.current_order_items);
        orderStatusRecycler = findViewById(R.id.order_status_recycler);
        currentOrderSection = findViewById(R.id.current_order_section);
        currentTableText = findViewById(R.id.current_table_text);
        statusFilterContainer = findViewById(R.id.status_filter_container);
        tabPreparing = findViewById(R.id.tab_preparing);
        tabReady = findViewById(R.id.tab_ready);
        tabServing = findViewById(R.id.tab_serving);
    }

    private void setupDrinksData() {
        allDrinks = Local_Database_Staff.getInstance().getAllDrinks();
        filteredDrinks = new ArrayList<>(allDrinks);
        currentOrderItems = new ArrayList<>();
    }

    private void setupRecyclerViews() {
        // Drinks RecyclerView
        drinkAdapter = new DrinkAdapter(filteredDrinks, this);
        drinksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        drinksRecyclerView.setAdapter(drinkAdapter);

        // Current Order RecyclerView
        currentOrderAdapter = new CurrentOrderAdapter(currentOrderItems, this);
        currentOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentOrderRecyclerView.setAdapter(currentOrderAdapter);
    }

    private void setupOrderStatus() {
        // ✅ Use OrderDataManager instead of Local_Database_Staff directly
        ordersList = orderDataManager.getOrdersByStatus(Order.OrderStatus.PREPARING);
        
        orderAdapter = new OrderAdapter(ordersList, this);
        orderStatusRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        orderStatusRecycler.setAdapter(orderAdapter);
    }

    private void setupStatusFilterTabs() {
        tabPreparing.setOnClickListener(v -> {
            currentStatusFilter = "preparing";
            updateStatusFilterSelection();
            filterOrderStatusByType(Order.OrderStatus.PREPARING);
        });

        tabReady.setOnClickListener(v -> {
            currentStatusFilter = "ready";
            updateStatusFilterSelection();
            filterOrderStatusByType(Order.OrderStatus.READY);
        });

        tabServing.setOnClickListener(v -> {
            currentStatusFilter = "serving";
            updateStatusFilterSelection();
            filterOrderStatusByType(Order.OrderStatus.SERVING);
        });
    }

    private void filterOrderStatusByType(Order.OrderStatus status) {
        // ✅ Use OrderDataManager for consistent data
        List<Order> filteredList = orderDataManager.getOrdersByStatus(status);
        orderAdapter.updateOrders(filteredList);
    }

    private void updateStatusFilterSelection() {
        resetTabStyle(tabPreparing);
        resetTabStyle(tabReady);  
        resetTabStyle(tabServing);
        
        switch (currentStatusFilter) {
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
        tab.setBackground(getResources().getDrawable(R.drawable.tab_rounded_selected));
        tab.setTextColor(getResources().getColor(R.color.white));
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackground(getResources().getDrawable(R.drawable.tab_rounded_unselected));
        tab.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_order);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_order) {
                return true;
            } else if (itemId == R.id.nav_list) {
                Intent intent = new Intent(MainActivity.this, TableSelectionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_cart) {
                Intent intent = new Intent(MainActivity.this, Cart.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_package) {
                Toast.makeText(this, "Package feature coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String mode = intent.getStringExtra("MODE");
            if ("TABLE_SELECTED".equals(mode)) {
                selectedTableNumber = intent.getStringExtra("SELECTED_TABLE_NUMBER");
                selectedTableName = intent.getStringExtra("SELECTED_TABLE_NAME");
                updateCurrentOrderDisplay();
            }
        }
    }

    @Override
    public void onDrinkClick(Drink drink) {
        if (selectedTableNumber.isEmpty()) {
            Intent intent = new Intent(this, TableSelectionActivity.class);
            intent.putExtra("SELECTED_DRINK_ID", drink.getId());
            intent.putExtra("SELECTED_DRINK_NAME", drink.getName());
            intent.putExtra("SELECTED_DRINK_PRICE", drink.getPrice());
            intent.putExtra("SELECTED_DRINK_SIZE", "M");
            intent.putExtra("SELECTED_DRINK_IMAGE", drink.getImageResId());
            startActivityForResult(intent, REQUEST_TABLE_SELECTION);
        } else {
            addToCurrentOrder(drink);
        }
    }

    private void addToCurrentOrder(Drink drink) {
        boolean found = false;
        for (CurrentOrderItem item : currentOrderItems) {
            if (item.getDrinkId().equals(drink.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }
        
        if (!found) {
            currentOrderItems.add(new CurrentOrderItem(
                drink.getId(),
                drink.getName(),
                drink.getPrice(),
                1,
                "M",
                drink.getImageResId()
            ));
        }
        
        currentOrderAdapter.notifyDataSetChanged();
        updateCurrentOrderDisplay();
    }

    private void updateCurrentOrderDisplay() {
        if (!selectedTableName.isEmpty() && !currentOrderItems.isEmpty()) {
            currentOrderSection.setVisibility(android.view.View.VISIBLE);
            currentTableText.setText(selectedTableName + " - " + getTotalItems() + " món");
        } else if (!selectedTableName.isEmpty()) {
            currentOrderSection.setVisibility(android.view.View.VISIBLE);
            currentTableText.setText(selectedTableName + " - 0 món");
        } else {
            currentOrderSection.setVisibility(android.view.View.GONE);
        }
    }

    private int getTotalItems() {
        int total = 0;
        for (CurrentOrderItem item : currentOrderItems) {
            total += item.getQuantity();
        }
        return total;
    }

    @Override
    public void onQuantityChanged(CurrentOrderItem item, int newQuantity) {
        if (newQuantity <= 0) {
            currentOrderItems.remove(item);
        } else {
            item.setQuantity(newQuantity);
        }
        currentOrderAdapter.notifyDataSetChanged();
        updateCurrentOrderDisplay();
    }

    @Override
    public void onItemRemoved(CurrentOrderItem item) {
        currentOrderItems.remove(item);
        currentOrderAdapter.notifyDataSetChanged();
        updateCurrentOrderDisplay();
    }

    @Override
    public void onOrderClick(Order order) {
        Toast.makeText(this, "Order " + order.getOrderNumber() + " details", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onConfirmOrder(List<CurrentOrderItem> items) {
        if (items.isEmpty()) {
            Toast.makeText(this, "Không có món nào để xác nhận", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Order order = createOrderFromCurrentItems();
        
        // ✅ Use OrderDataManager for consistent updates
        String orderNumber = orderDataManager.addNewOrder(order);
        
        // Clear current order
        currentOrderItems.clear();
        currentOrderAdapter.notifyDataSetChanged();
        
        // Reset table selection
        selectedTableNumber = "";
        selectedTableName = "";
        updateCurrentOrderDisplay();
        
        Toast.makeText(this, "Đã xác nhận đơn hàng " + orderNumber, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelOrder() {
        if (currentOrderItems.isEmpty()) {
            Toast.makeText(this, "Không có món nào để hủy", Toast.LENGTH_SHORT).show();
            return;
        }
        
        currentOrderItems.clear();
        currentOrderAdapter.notifyDataSetChanged();
        
        selectedTableNumber = "";
        selectedTableName = "";
        updateCurrentOrderDisplay();
        
        Toast.makeText(this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
    }

    private Order createOrderFromCurrentItems() {
        Order order = new Order(selectedTableNumber, selectedTableName, "Nhân viên");
        for (CurrentOrderItem currentItem : currentOrderItems) {
            OrderItem orderItem = new OrderItem(
                currentItem.getDrinkId(),
                currentItem.getDrinkName(),
                currentItem.getPrice(), // ✅ Fix: Use getPrice() instead of getUnitPrice()
                currentItem.getQuantity(),
                currentItem.getSize(),
                currentItem.getImageResId()
            );
            order.addItem(orderItem);
        }
        return order;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_TABLE_SELECTION && resultCode == RESULT_OK && data != null) {
            selectedTableNumber = data.getStringExtra("SELECTED_TABLE_NUMBER");
            selectedTableName = data.getStringExtra("SELECTED_TABLE_NAME");
            
            String drinkId = data.getStringExtra("SELECTED_DRINK_ID");
            String drinkName = data.getStringExtra("SELECTED_DRINK_NAME");
            double drinkPrice = data.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
            String drinkSize = data.getStringExtra("SELECTED_DRINK_SIZE");
            int drinkImage = data.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.placeholder_drink);
            
            if (drinkName != null) {
                currentOrderItems.add(new CurrentOrderItem(
                    drinkId, drinkName, drinkPrice, 1, drinkSize, drinkImage
                ));
                currentOrderAdapter.notifyDataSetChanged();
            }
            
            updateCurrentOrderDisplay();
        }
    }
}