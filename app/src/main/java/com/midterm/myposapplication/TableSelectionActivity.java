package com.midterm.myposapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashMap;
import java.util.Map;

public class TableSelectionActivity extends AppCompatActivity implements OnTableClickListener {

    private EditText searchBar;
    private LinearLayout orderCardsContainer;
    private GridLayout insideTablesGrid;
    private GridLayout outsideTablesGrid;

    // Table data structure
    private Map<String, Table> tablesData;

    // Selected drink information
    private String selectedDrinkId;
    private String selectedDrinkName;
    private double selectedDrinkPrice;
    private String selectedDrinkSize;
    private int selectedDrinkImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_table_screen);

        // Get drink information from intent
        getDrinkInformation();

        // Initialize views
        initializeViews();

        // Setup table data
        setupTableData();

        // Setup search functionality
        setupSearchFunctionality();

        // Setup table click listeners
        setupTableClickListeners();

        // Setup bottom navigation
        setupBottomNavigation();

        // Update order cards display
        updateOrderCardsDisplay();
    }

    private void getDrinkInformation() {
        Intent intent = getIntent();
        selectedDrinkId = intent.getStringExtra("SELECTED_DRINK_ID");
        selectedDrinkName = intent.getStringExtra("SELECTED_DRINK_NAME");
        selectedDrinkPrice = intent.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
        selectedDrinkSize = intent.getStringExtra("SELECTED_DRINK_SIZE");
        selectedDrinkImage = intent.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.placeholder_drink);
    }

    private void initializeViews() {
        searchBar = findViewById(R.id.search_bar);
        orderCardsContainer = findViewById(R.id.order_cards_container);
        insideTablesGrid = findViewById(R.id.inside_tables_grid);
        outsideTablesGrid = findViewById(R.id.outside_tables_grid);
    }

    private void setupTableData() {
        tablesData = new HashMap<>();

        // Inside tables - tương ứng với XML layout
        tablesData.put("inside_table_1", new Table("1", "Table 1", "occupied", 4)); // In Service
        tablesData.put("inside_table_2", new Table("2", "Table 2", "preparing", 4)); // Preparing
        tablesData.put("inside_table_3", new Table("3", "Table 3", "available", 4)); // Empty

        // Outside tables - tương ứng với XML layout
        tablesData.put("outside_table_1", new Table("4", "Table 4", "occupied", 4)); // In Service
        tablesData.put("outside_table_2", new Table("5", "Table 5", "available", 4)); // Empty
        tablesData.put("outside_table_3", new Table("6", "Table 6", "available", 4)); // Empty
    }

    private void setupSearchFunctionality() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTables(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterTables(String searchText) {
        // Simple table filtering logic
        for (Map.Entry<String, Table> entry : tablesData.entrySet()) {
            String tableId = entry.getKey();
            Table table = entry.getValue();
            CardView tableCard = findViewById(getTableCardId(tableId));

            if (tableCard != null) {
                boolean shouldShow = searchText.isEmpty() ||
                        table.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        table.getNumber().toLowerCase().contains(searchText.toLowerCase());

                tableCard.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
            }
        }
    }

    private int getTableCardId(String tableKey) {
        switch (tableKey) {
            case "inside_table_1": return R.id.inside_table_1;
            case "inside_table_2": return R.id.inside_table_2;
            case "inside_table_3": return R.id.inside_table_3;
            case "outside_table_1": return R.id.outside_table_1;
            case "outside_table_2": return R.id.outside_table_2;
            case "outside_table_3": return R.id.outside_table_3;
            default: return -1;
        }
    }

    private void setupTableClickListeners() {
        // Setup click listeners cho từng table card
        setupTableClickListener("inside_table_1", R.id.inside_table_1);
        setupTableClickListener("inside_table_2", R.id.inside_table_2);
        setupTableClickListener("inside_table_3", R.id.inside_table_3);
        setupTableClickListener("outside_table_1", R.id.outside_table_1);
        setupTableClickListener("outside_table_2", R.id.outside_table_2);
        setupTableClickListener("outside_table_3", R.id.outside_table_3);
    }

    private void setupTableClickListener(String tableKey, int cardViewId) {
        CardView tableCard = findViewById(cardViewId);
        Table table = tablesData.get(tableKey);

        if (tableCard != null && table != null) {
            tableCard.setOnClickListener(v -> onTableClick(table));
        }
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

    private void updateOrderCardsDisplay() {
        // TODO: Hiển thị các order đang có trong orderCardsContainer
        // Có thể load từ database hoặc shared preferences
    }

    @Override
    public void onTableClick(Table table) {
        if (table.getStatus().equals("occupied") || table.getStatus().equals("preparing")) {
            showOccupiedTableDialog(table);
        } else {
            showOrderConfirmationDialog(table);
        }
    }

    private void showOccupiedTableDialog(Table table) {
        String statusText = table.getStatus().equals("occupied") ? "đang có khách" : "đang chuẩn bị món";

        new AlertDialog.Builder(this)
                .setTitle("Bàn " + statusText)
                .setMessage("Bàn " + table.getNumber() + " hiện " + statusText + ". Bạn có muốn thêm món vào order hiện tại không?")
                .setPositiveButton("Thêm món", (dialog, which) -> {
                    showOrderConfirmationDialog(table);
                })
                .setNegativeButton("Chọn bàn khác", null)
                .show();
    }

    private void showOrderConfirmationDialog(Table table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận order");

        String message = String.format(
                "Bàn: %s\n" +
                        "Món: %s (%s)\n" +
                        "Giá: $%.2f\n\n" +
                        "Xác nhận thêm món này vào order?",
                table.getName(),
                selectedDrinkName,
                selectedDrinkSize,
                selectedDrinkPrice
        );

        builder.setMessage(message);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            // Return result to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_TABLE_NUMBER", table.getNumber());
            resultIntent.putExtra("SELECTED_TABLE_NAME", table.getName());
            resultIntent.putExtra("SELECTED_DRINK_ID", selectedDrinkId);
            resultIntent.putExtra("SELECTED_DRINK_NAME", selectedDrinkName);
            resultIntent.putExtra("SELECTED_DRINK_PRICE", selectedDrinkPrice);
            resultIntent.putExtra("SELECTED_DRINK_SIZE", selectedDrinkSize);
            resultIntent.putExtra("SELECTED_DRINK_IMAGE", selectedDrinkImage);

            setResult(RESULT_OK, resultIntent);

            // Show success message
            Toast.makeText(this, "Đã thêm " + selectedDrinkName + " vào " + table.getName(), Toast.LENGTH_SHORT).show();

            finish();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}