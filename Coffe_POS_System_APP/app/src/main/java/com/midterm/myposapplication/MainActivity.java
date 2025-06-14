package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDrinkClickListener, CurrentOrderAdapter.OnOrderItemClickListener {

    private RecyclerView drinksRecyclerView;
    private DrinkAdapter drinkAdapter;
    private List<Drink> drinkList;

    // Current Order UI Components
    private LinearLayout currentOrderSection;
    private TextView currentTableText;
    private RecyclerView currentOrderItems;

    // Temporary order storage
    private Order currentOrder;
    private String selectedTableNumber;
    private String selectedTableName;

    // Modern Activity Result API
    private ActivityResultLauncher<Intent> tableSelectionLauncher;
    private CurrentOrderAdapter currentOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeActivityResultLauncher();
        initializeViews();
        setupDrinkList();
        setupBottomNavigation();
    }

    private void initializeActivityResultLauncher() {
        tableSelectionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleTableSelectionResult(result.getData());
                }
            }
        );
    }

    private void initializeViews() {
        drinksRecyclerView = findViewById(R.id.drinks_recycler_view);
        drinksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize current order UI components
        currentOrderSection = findViewById(R.id.current_order_section);
        currentTableText = findViewById(R.id.current_table_text);
        currentOrderItems = findViewById(R.id.current_order_items);

        // Setup current order RecyclerView
        currentOrderItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupDrinkList() {
        drinkList = new ArrayList<>();
        drinkList.add(new Drink("1", "Cappuccino", 3.00, R.drawable.placeholder_drink, true));
        drinkList.add(new Drink("2", "Espresso", 2.00, R.drawable.placeholder_drink, false));
        drinkList.add(new Drink("3", "Americano", 2.55, R.drawable.placeholder_drink, false));
        drinkList.add(new Drink("4", "Latte", 4.00, R.drawable.placeholder_drink, true));
        drinkList.add(new Drink("5", "Matcha", 3.50, R.drawable.placeholder_drink, false));
        drinkList.add(new Drink("6", "Cold Brew", 4.50, R.drawable.placeholder_drink, false));

        drinkAdapter = new DrinkAdapter(drinkList, this);
        drinksRecyclerView.setAdapter(drinkAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);

        // Set current item as selected
        bottomNavigationView.setSelectedItemId(R.id.nav_order);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_order) {
                // Already on Order screen
                return true;

            } else if (itemId == R.id.nav_list) {
                // Navigate to Table Selection
                Intent intent = new Intent(MainActivity.this, TableSelectionActivity.class);
                intent.putExtra("MODE", "TABLE_FIRST");
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_package) {
                // TODO: Navigate to Package/Delivery screen
                Toast.makeText(this, "Package feature coming soon", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.nav_cart) {
//                 Show current order details
                if (currentOrder != null && !currentOrder.getItems().isEmpty()) {
                    Toast.makeText(this,
                        String.format("Current order: %s - %d items - $%.2f",
                            currentOrder.getTableName(),
                            currentOrder.getTotalItems(),
                            currentOrder.getTotalAmount()),
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "No current orders", Toast.LENGTH_SHORT).show();
                }
                return true;

            } else if (itemId == R.id.nav_profile) {
                // TODO: Navigate to Profile/Settings screen
                Intent intent = new Intent(MainActivity.this, Profile.class);
                intent.putExtra("MODE", "TABLE_FIRST");
                startActivity(intent);
//                Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void handleTableSelectionResult(Intent data) {
        // Get table selection result
        selectedTableNumber = data.getStringExtra("SELECTED_TABLE_NUMBER");
        selectedTableName = data.getStringExtra("SELECTED_TABLE_NAME");

        // Get drink information
        String drinkId = data.getStringExtra("SELECTED_DRINK_ID");
        String drinkName = data.getStringExtra("SELECTED_DRINK_NAME");
        double drinkPrice = data.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
        String drinkSize = data.getStringExtra("SELECTED_DRINK_SIZE");
        int drinkImage = data.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.placeholder_drink);

        // Create or get current order
        if (currentOrder == null || !currentOrder.getTableNumber().equals(selectedTableNumber)) {
            currentOrder = new Order(selectedTableNumber, selectedTableName);
        }

        // Add item to order
        OrderItem newItem = new OrderItem(drinkId, drinkName, drinkPrice, 1, drinkSize, drinkImage);
        currentOrder.addItem(newItem);

        // Show success message
        Toast.makeText(this,
            String.format("Đã thêm %s vào %s", drinkName, selectedTableName),
            Toast.LENGTH_SHORT).show();

        // Update UI section
        updateCurrentOrderUI();
    }

    private void updateCurrentOrderUI() {
        if (currentOrder != null && !currentOrder.getItems().isEmpty()) {
            currentOrderSection.setVisibility(View.VISIBLE);
            currentTableText.setText(String.format("%s - %d món - $%.2f",
                currentOrder.getTableName(),
                currentOrder.getTotalItems(),
                currentOrder.getTotalAmount()));

            // Setup adapter for current order items
            if (currentOrderAdapter == null) {
                currentOrderAdapter = new CurrentOrderAdapter(currentOrder.getItems(), this);
                currentOrderItems.setAdapter(currentOrderAdapter);
            } else {
                currentOrderAdapter.notifyDataSetChanged();
            }
        } else {
            currentOrderSection.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddDrinkClicked(Drink drink, String selectedSize) {
        // Start table selection activity
        Intent intent = new Intent(MainActivity.this, TableSelectionActivity.class);
        intent.putExtra("SELECTED_DRINK_ID", drink.getId());
        intent.putExtra("SELECTED_DRINK_NAME", drink.getName());
        intent.putExtra("SELECTED_DRINK_PRICE", drink.getPrice());
        intent.putExtra("SELECTED_DRINK_SIZE", selectedSize);
        intent.putExtra("SELECTED_DRINK_IMAGE", drink.getImageResId());
        tableSelectionLauncher.launch(intent);
    }

    // Implement CurrentOrderAdapter.OnOrderItemClickListener
    @Override
    public void onRemoveItem(OrderItem item) {
        if (currentOrder != null) {
            currentOrder.removeItem(item);
            updateCurrentOrderUI();
            Toast.makeText(this, "Đã xóa " + item.getDrinkName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuantityChanged(OrderItem item, int newQuantity) {
        updateCurrentOrderUI();
        Toast.makeText(this,
            String.format("Cập nhật %s: %d", item.getDrinkName(), newQuantity),
            Toast.LENGTH_SHORT).show();
    }

    // HI from VS code
}