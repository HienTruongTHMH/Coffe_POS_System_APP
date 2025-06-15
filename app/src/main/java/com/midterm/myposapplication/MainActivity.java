package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
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
               CurrentOrderAdapter.OnOrderItemChangeListener, // ✅ Change to OnOrderItemClickListener
               OrderStatusAdapter.OnOrderStatusClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_TABLE_SELECTION = 1001;

    // UI Components
    private RecyclerView drinksRecyclerView;
    private RecyclerView currentOrderRecyclerView;
    private RecyclerView orderStatusRecycler; // ✅ New component
    private LinearLayout categoryTabsContainer;
    private LinearLayout currentOrderSection;
    private TextView currentTableText;

    // Adapters
    private DrinkAdapter drinkAdapter;
    private CurrentOrderAdapter currentOrderAdapter;
    private OrderStatusAdapter orderStatusAdapter; // ✅ New adapter

    // Data
    private List<Drink> allDrinks;
    private List<Drink> filteredDrinks;
    private List<CurrentOrderItem> currentOrderItems; // ✅ FIXED data type
    private List<OrderStatus> orderStatusList; // ✅ New data

    // Current state
    // private String currentCategory = "All";
    private String selectedTableNumber = "";
    private String selectedTableName = "";
    private String currentStatusFilter = "preparing"; // Default filter

    // UI Components for status filter
    private LinearLayout statusFilterContainer;
    private TextView tabPreparing, tabReady, tabServing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupDrinksData();
        setupRecyclerViews();
        setupStatusFilterTabs(); // ✅ Add this
        setupOrderStatus();
        setupBottomNavigation();
        handleIntent();
    }

    private void initializeViews() {
        drinksRecyclerView = findViewById(R.id.drinks_recycler_view);
        currentOrderRecyclerView = findViewById(R.id.current_order_items);
        orderStatusRecycler = findViewById(R.id.order_status_recycler); // ✅ New
        // categoryTabsContainer = findViewById(R.id.category_tabs_container);
        currentOrderSection = findViewById(R.id.current_order_section);
        currentTableText = findViewById(R.id.current_table_text);
        statusFilterContainer = findViewById(R.id.status_filter_container);
        tabPreparing = findViewById(R.id.tab_preparing);
        tabReady = findViewById(R.id.tab_ready);
        tabServing = findViewById(R.id.tab_serving);
    }

    private void setupDrinksData() {
        allDrinks = new ArrayList<>();

        // ✅ Use correct constructor: (id, name, price, imageResId, hasSizes)
        // Coffee drinks
        allDrinks.add(new Drink("1", "Espresso", 3.50, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("2", "Americano", 4.00, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("3", "Cappuccino", 4.50, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("4", "Latte", 5.00, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("5", "Macchiato", 5.50, R.drawable.placeholder_drink, true));

        // Tea drinks
        allDrinks.add(new Drink("6", "Green Tea", 3.00, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("7", "Earl Grey", 3.25, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("8", "Chamomile", 3.25, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("9", "Jasmine Tea", 3.50, R.drawable.placeholder_drink, true));

        // Cold drinks
        allDrinks.add(new Drink("10", "Iced Coffee", 4.25, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("11", "Frappuccino", 5.75, R.drawable.placeholder_drink, false));
        allDrinks.add(new Drink("12", "Iced Tea", 3.75, R.drawable.placeholder_drink, true));
        allDrinks.add(new Drink("13", "Smoothie", 6.00, R.drawable.placeholder_drink, false));

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

    // private void setupCategoryTabs() {
    //     String[] categories = {"All", "Coffee", "Tea", "Cold"};
        
    //     for (String category : categories) {
    //         Button categoryButton = new Button(this);
    //         categoryButton.setText(category);
    //         categoryButton.setLayoutParams(new LinearLayout.LayoutParams(
    //             ViewGroup.LayoutParams.WRAP_CONTENT,
    //             ViewGroup.LayoutParams.WRAP_CONTENT
    //         ));
            
    //         // Set initial selection
    //         if (category.equals(currentCategory)) {
    //             categoryButton.setSelected(true);
    //         }
            
    //         categoryButton.setOnClickListener(v -> {
    //             currentCategory = category;
    //             filterDrinksByCategory(category);
    //             updateCategoryTabSelection();
    //         });
            
    //         categoryTabsContainer.addView(categoryButton);
    //     }
    // }

    // ✅ New method - Setup Order Status
    private void setupOrderStatus() {
        // Sample data
        orderStatusList = new ArrayList<>();
        orderStatusList.add(new OrderStatus("1", "#2100", "Inside, table 2", "ready", 3));
        orderStatusList.add(new OrderStatus("2", "#2101", "Outside, chair 1", "preparing", 2));
        orderStatusList.add(new OrderStatus("3", "#2102", "Inside, table 5", "ready", 1));

        // Setup adapter
        orderStatusAdapter = new OrderStatusAdapter(orderStatusList, this);
        orderStatusRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        orderStatusRecycler.setAdapter(orderStatusAdapter);
    }

    private void setupStatusFilterTabs() {
        tabPreparing.setOnClickListener(v -> {
            currentStatusFilter = "preparing";
            updateStatusFilterSelection();
            filterOrderStatusByType("preparing");
        });

        tabReady.setOnClickListener(v -> {
            currentStatusFilter = "ready";
            updateStatusFilterSelection();
            filterOrderStatusByType("ready");
        });

        tabServing.setOnClickListener(v -> {
            currentStatusFilter = "in_service";
            updateStatusFilterSelection();
            filterOrderStatusByType("in_service");
        });
    }

    private void updateStatusFilterSelection() {
        // Reset all tabs
        resetTabStyle(tabPreparing);
        resetTabStyle(tabReady);
        resetTabStyle(tabServing);
        
        // Set selected tab
        switch (currentStatusFilter) {
            case "preparing":
                setSelectedTabStyle(tabPreparing);
                break;
            case "ready":
                setSelectedTabStyle(tabReady);
                break;
            case "in_service":
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

    private void filterOrderStatusByType(String statusType) {
        List<OrderStatus> filteredList = new ArrayList<>();
        for (OrderStatus orderStatus : orderStatusList) {
            if (statusType.equals(orderStatus.getStatus())) {
                filteredList.add(orderStatus);
            }
        }
        orderStatusAdapter.updateOrderStatus(filteredList);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_order);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_order) {
                return true; // Already on this screen
            } else if (itemId == R.id.nav_list) {
                Intent intent = new Intent(MainActivity.this, TableSelectionActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_cart) {
                // ✅ Add Cart navigation
                Intent intent = new Intent(MainActivity.this, Cart.class);
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
            intent.putExtra("SELECTED_DRINK_IMAGE", drink.getImageResId()); // ✅ Correct method name
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
                drink.getImageResId() // ✅ Correct method name
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

    // ✅ Change method names to match OnOrderItemClickListener interface
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

    // ✅ New method - Handle Order Status clicks
    @Override
    public void onOrderStatusClick(OrderStatus orderStatus) {
        if ("ready".equals(orderStatus.getStatus())) {
            // Mark as served
            orderStatus.setStatus("served");
            orderStatusAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Order " + orderStatus.getOrderNumber() + " marked as served", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Order " + orderStatus.getOrderNumber() + " details", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ Add to MainActivity.java - new interface methods
    @Override
    public void onConfirmOrder(List<CurrentOrderItem> items) {
        if (items.isEmpty()) {
            Toast.makeText(this, "Không có món nào để xác nhận", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create Order from CurrentOrderItems
        Order order = createOrderFromCurrentItems();
        
        // Generate order number
        String orderNumber = "#" + (2100 + orderStatusList.size() + 1);
        
        // Create OrderStatus
        OrderStatus newOrderStatus = new OrderStatus(
            order.getOrderId(),
            orderNumber,
            selectedTableName,
            "preparing", // Initial status
            items.size()
        );
        
        // Add to order status list
        orderStatusList.add(newOrderStatus);
        orderStatusAdapter.notifyDataSetChanged();
        
        // Clear current order
        currentOrderItems.clear();
        currentOrderAdapter.notifyDataSetChanged();
        
        // Reset table selection
        selectedTableNumber = "";
        selectedTableName = "";
        updateCurrentOrderDisplay();
        
        // Show success message
        Toast.makeText(this, "Đã xác nhận đơn hàng " + orderNumber, Toast.LENGTH_SHORT).show();
        
        Log.d(TAG, "Order confirmed: " + orderNumber + " with " + items.size() + " items");
    }

    @Override
    public void onCancelOrder() {
        if (currentOrderItems.isEmpty()) {
            Toast.makeText(this, "Không có món nào để hủy", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Clear current order
        currentOrderItems.clear();
        currentOrderAdapter.notifyDataSetChanged();
        
        // Reset table selection
        selectedTableNumber = "";
        selectedTableName = "";
        updateCurrentOrderDisplay();
        
        Toast.makeText(this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
    }

    private OrderItem convertToOrderItem(CurrentOrderItem currentItem) {
        return new OrderItem(
            currentItem.getDrinkId(),
            currentItem.getDrinkName(),
            currentItem.getPrice(),
            currentItem.getQuantity(),
            currentItem.getSize(),
            currentItem.getImageResourceId()
        );
    }

    private List<OrderItem> convertCurrentOrderToOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CurrentOrderItem currentItem : currentOrderItems) {
            orderItems.add(convertToOrderItem(currentItem));
        }
        return orderItems;
    }

    // Khi cần tạo Order object:
    private Order createOrderFromCurrentItems() {
        Order order = new Order(selectedTableNumber, selectedTableName);
        List<OrderItem> orderItems = convertCurrentOrderToOrderItems();
        for (OrderItem item : orderItems) {
            order.addItem(item);
        }
        return order;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_TABLE_SELECTION && resultCode == RESULT_OK && data != null) {
            selectedTableNumber = data.getStringExtra("SELECTED_TABLE_NUMBER");
            selectedTableName = data.getStringExtra("SELECTED_TABLE_NAME");
            
            // Add the drink to order
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
            
            Log.d(TAG, "Table selected: " + selectedTableName);
        }
    }

    // ✅ Add to OrderStatusClickListener implementation
    @Override
    public void onOrderStatusDoubleClick(OrderStatus orderStatus) {
        if ("ready".equals(orderStatus.getStatus())) {
            orderStatus.setStatus("in_service");
            orderStatusAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Order " + orderStatus.getOrderNumber() + " đang được phục vụ", Toast.LENGTH_SHORT).show();
            
            // Update filter if currently viewing "ready" status
            if ("ready".equals(currentStatusFilter)) {
                filterOrderStatusByType("ready");
            }
        }
    }
}