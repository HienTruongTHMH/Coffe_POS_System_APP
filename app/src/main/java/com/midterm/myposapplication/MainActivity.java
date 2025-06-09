package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDrinkClickListener {

    private RecyclerView drinksRecyclerView;
    private DrinkAdapter drinkAdapter;
    private List<Drink> drinkList;

    // Temporary order storage
    private Order currentOrder;
    private String selectedTableNumber;
    private String selectedTableName;

    private static final int REQUEST_TABLE_SELECTION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupDrinkList();
        setupBottomNavigation();
    }

    private void initializeViews() {
        drinksRecyclerView = findViewById(R.id.drinks_recycler_view);
        drinksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
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
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_order) {
                return true;
            } else if (itemId == R.id.nav_list) {
                return true;
            } else if (itemId == R.id.nav_package) {
                return true;
            } else if (itemId == R.id.nav_cart) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
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
        startActivityForResult(intent, REQUEST_TABLE_SELECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TABLE_SELECTION && resultCode == RESULT_OK && data != null) {
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

            // TODO: Show ordering details dialog or update UI
            // This will be implemented in the next step
        }
    }
}