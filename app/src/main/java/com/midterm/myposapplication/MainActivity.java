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
import com.midterm.myposapplication.utils.FormatUtils;
import com.midterm.myposapplication.utils.IdGenerator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity 
    implements DrinkAdapter.OnDrinkClickListener, 
               CurrentOrderAdapter.OnOrderItemChangeListener,
               OrderManager.OrderListener {
    
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
    private OrderStatusAdapter orderStatusAdapter;

    // Data
    private List<Drink> allDrinks;
    private List<Drink> filteredDrinks;
    private List<CurrentOrderItem> currentOrderItems;
    private List<Order> ordersList;

    // Current state
    private String selectedTableNumber = "";
    private String selectedTableName = "";
    private String currentStatusFilter = Constants.FILTER_ALL;

    // UI Components for status filter
    private TextView tabAll, tabPreparing, tabReady, tabServing;

    // ✅ FIXED: Use only OrderManager and DatabaseManager
    private OrderManager orderManager;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ FIXED: Initialize managers properly
        orderManager = OrderManager.getInstance();
        databaseManager = DatabaseManager.getInstance();
        
        // ✅ FIXED: Register as OrderManager listener only
        orderManager.addListener(this);

        initializeViews();
        setupDrinksData();
        setupRecyclerViews();
        setupStatusFilterTabs();
        setupOrderStatus();
        setupBottomNavigation();
        handleIntent();
        
        // ✅ FIXED: Initial UI update
        updateCurrentOrderUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ✅ FIXED: Unregister listener to prevent memory leaks
        if (orderManager != null) {
            orderManager.removeListener(this);
        }
    }

    private void initializeViews() {
        drinksRecyclerView = findViewById(R.id.drinks_recycler_view);
        currentOrderRecyclerView = findViewById(R.id.current_order_items);
        orderStatusRecycler = findViewById(R.id.order_status_recycler);
        currentOrderSection = findViewById(R.id.current_order_section);
        currentTableText = findViewById(R.id.current_table_text);
        
        // Status filter tabs
        tabAll = findViewById(R.id.tab_all);
        tabPreparing = findViewById(R.id.tab_preparing);
        tabServing = findViewById(R.id.tab_serving);
    }

    private void setupDrinksData() {
        // ✅ FIXED: Use DatabaseManager consistently
        allDrinks = databaseManager.getAllDrinks();
        filteredDrinks = new ArrayList<>(allDrinks);
        currentOrderItems = new ArrayList<>();
        
        Log.d(TAG, "Drinks data setup: " + allDrinks.size() + " drinks loaded");
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
        // ✅ FIXED: Use OrderManager consistently
        ordersList = orderManager.getActiveOrders();
        
        orderStatusAdapter = new OrderStatusAdapter(ordersList, new OrderStatusAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Toast.makeText(MainActivity.this, "Order " + order.getOrderNumber() + " details", Toast.LENGTH_SHORT).show();
            }
        });
        
        orderStatusRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        orderStatusRecycler.setAdapter(orderStatusAdapter);
        
        Log.d(TAG, "Order status setup: " + ordersList.size() + " active orders");
    }

    private void setupStatusFilterTabs() {
        tabAll.setOnClickListener(v -> {
            currentStatusFilter = Constants.FILTER_ALL;
            updateStatusFilterSelection();
            filterOrdersByStatus(Constants.FILTER_ALL);
        });

        tabPreparing.setOnClickListener(v -> {
            currentStatusFilter = Constants.FILTER_PREPARING;
            updateStatusFilterSelection();
            filterOrdersByStatus(Constants.FILTER_PREPARING);
        });

        tabServing.setOnClickListener(v -> {
            currentStatusFilter = Constants.FILTER_ON_SERVICE;
            updateStatusFilterSelection();
            filterOrdersByStatus(Constants.FILTER_ON_SERVICE);
        });
    }

    private void filterOrdersByStatus(String status) {
    List<Order> filteredList;
    
    if (Constants.FILTER_ALL.equals(status)) {
        filteredList = orderManager.getActiveOrders();
    } else {
        Order.OrderStatus targetStatus = getOrderStatusFromString(status);
        if (targetStatus != null) {
            filteredList = orderManager.getOrdersByStatus(targetStatus);
        } else {
            filteredList = new ArrayList<>();
        }
    }
    
    orderStatusAdapter.updateOrders(filteredList);
    Log.d(TAG, "Filtered orders by " + status + ": " + filteredList.size() + " orders");
}

    private Order.OrderStatus getOrderStatusFromString(String status) {
        switch (status) {
            case Constants.FILTER_ON_SERVICE:
                return Order.OrderStatus.ON_SERVICE;
            case Constants.FILTER_PREPARING:
            default:
                return Order.OrderStatus.PREPARING;
        }
    }

    private void updateStatusFilterSelection() {
    // Reset all tabs
    resetTabStyle(tabAll);
    resetTabStyle(tabPreparing);
    resetTabStyle(tabServing);
    
    // Set selected tab
    switch (currentStatusFilter) {
        case Constants.FILTER_ALL:
            setSelectedTabStyle(tabAll);
            break;
        case Constants.FILTER_PREPARING:
            setSelectedTabStyle(tabPreparing);
            break;
        case Constants.FILTER_ON_SERVICE:
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
            selectedTableNumber = intent.getStringExtra(Constants.KEY_TABLE_NUMBER);
            selectedTableName = intent.getStringExtra("SELECTED_TABLE_NAME");
            updateCurrentOrderUI();
        }
    }
}

    @Override
    public void onDrinkClick(Drink drink) {
        Log.d(TAG, "Drink clicked: " + drink.getName());
        
        if (selectedTableNumber.isEmpty()) {
            // No table selected, go to table selection
            Intent intent = new Intent(this, TableSelectionActivity.class);
            intent.putExtra("SELECTED_DRINK_ID", drink.getId());
            intent.putExtra("SELECTED_DRINK_NAME", drink.getName());
            intent.putExtra("SELECTED_DRINK_PRICE", drink.getPrice());
            intent.putExtra("SELECTED_DRINK_SIZE", "M");
            intent.putExtra("SELECTED_DRINK_IMAGE", drink.getImageResId());
            startActivityForResult(intent, REQUEST_TABLE_SELECTION);
        } else {
            // Table already selected, add to current order
            addToCurrentOrder(drink);
        }
    }

    private void addToCurrentOrder(Drink drink) {
        boolean found = false;
        for (CurrentOrderItem item : currentOrderItems) {
            if (item.getDrinkId().equals(drink.getId()) && "M".equals(item.getSize())) {
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
        
        updateCurrentOrderUI();
        Log.d(TAG, "Added " + drink.getName() + " to current order");
    }

    // ✅ FIXED: Central UI update method
    private void updateCurrentOrderUI() {
    if (!selectedTableName.isEmpty()) {
        currentOrderSection.setVisibility(android.view.View.VISIBLE);
        int totalItems = getTotalItems();
        // Sử dụng FormatUtils
        String tableText = FormatUtils.formatTableNumber(this, selectedTableNumber) + " - " + totalItems + " items";
        currentTableText.setText(tableText);
    } else {
        currentOrderSection.setVisibility(android.view.View.GONE);
    }

    if (currentOrderAdapter != null) {
        boolean hasItems = !currentOrderItems.isEmpty();
        Log.d(TAG, "Setting action buttons visibility: " + hasItems + " (items count: " + currentOrderItems.size() + ")");
        currentOrderAdapter.setShowActionButtons(hasItems);
        currentOrderAdapter.notifyDataSetChanged();
    }
    
    Log.d(TAG, "UI updated - Table: " + selectedTableName + ", Items: " + currentOrderItems.size());
}

    private int getTotalItems() {
        int total = 0;
        for (CurrentOrderItem item : currentOrderItems) {
            total += item.getQuantity();
        }
        return total;
    }

    // ✅ CurrentOrderAdapter.OnOrderItemChangeListener implementation
    @Override
    public void onQuantityChanged(CurrentOrderItem item, int newQuantity) {
        if (newQuantity <= 0) {
            currentOrderItems.remove(item);
        } else {
            item.setQuantity(newQuantity);
        }
        updateCurrentOrderUI();
        Log.d(TAG, "Quantity changed for " + item.getDrinkName() + ": " + newQuantity);
    }

    @Override
    public void onItemRemoved(CurrentOrderItem item) {
        currentOrderItems.remove(item);
        updateCurrentOrderUI();
        Log.d(TAG, "Item removed: " + item.getDrinkName());
    }

    @Override
    public void onConfirmOrder(List<CurrentOrderItem> items) {
        if (items.isEmpty()) {
            Toast.makeText(this, "No items to confirm", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedTableNumber.isEmpty()) {
            Toast.makeText(this, "Please select a table", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // ✅ FIXED: Create order and use OrderManager
        Order order = createOrderFromCurrentItems();
        String orderNumber = orderManager.createOrder(order);
        
        // Clear current order
        currentOrderItems.clear();
        selectedTableNumber = "";
        selectedTableName = "";
        updateCurrentOrderUI();

        Toast.makeText(this, "Order confirmed: " + orderNumber, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Order confirmed: " + orderNumber);
    }

    @Override
    public void onCancelOrder() {
        if (currentOrderItems.isEmpty()) {
            Toast.makeText(this, "No items to cancel", Toast.LENGTH_SHORT).show();
            return;
        }
        
        currentOrderItems.clear();
        selectedTableNumber = "";
        selectedTableName = "";
        updateCurrentOrderUI();

        Toast.makeText(this, "Order cancelled", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Order cancelled");
    }

    private Order createOrderFromCurrentItems() {
        Order order = new Order(selectedTableNumber, selectedTableName, "Staff");
        
        for (CurrentOrderItem currentItem : currentOrderItems) {
            OrderItem orderItem = new OrderItem(
                currentItem.getDrinkId(),
                currentItem.getDrinkName(),
                currentItem.getPrice(),
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
            
            // Add the selected drink to current order
            String drinkId = data.getStringExtra("SELECTED_DRINK_ID");
            String drinkName = data.getStringExtra("SELECTED_DRINK_NAME");
            double drinkPrice = data.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
            String drinkSize = data.getStringExtra("SELECTED_DRINK_SIZE");
            int drinkImage = data.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.placeholder_drink);
            
            if (drinkName != null) {
                currentOrderItems.add(new CurrentOrderItem(
                    drinkId, drinkName, drinkPrice, 1, drinkSize, drinkImage
                ));
                Log.d(TAG, "Added from table selection: " + drinkName);
            }
            
            updateCurrentOrderUI();
        }
    }

    @Override
    public void onOrderCreated(Order order) {
        runOnUiThread(this::loadActiveOrders);
    }

    @Override
    public void onOrderUpdated(Order order) {
        runOnUiThread(this::loadActiveOrders);
    }

    @Override
    public void onOrderStatusChanged(Order order) {
        runOnUiThread(this::loadActiveOrders);
    }

    @Override
    public void onOrdersUpdated() {
        runOnUiThread(this::loadActiveOrders);
    }

    private void loadActiveOrders() {
        List<Order> activeOrders = orderManager.getActiveOrders();
        filterOrdersByStatus(currentStatusFilter);
        Log.d(TAG, "Active orders loaded: " + activeOrders.size());
    }
}