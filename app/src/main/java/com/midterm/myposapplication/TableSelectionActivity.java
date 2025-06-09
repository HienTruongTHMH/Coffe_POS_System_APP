package com.midterm.myposapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TableSelectionActivity extends AppCompatActivity implements OnTableClickListener {

    private RecyclerView tablesRecyclerView;
    private TableAdapter tableAdapter;
    private List<Table> tableList;
    private List<Table> filteredTableList;
    private EditText searchBar;
    private LinearLayout orderCardsContainer;

    // Drink information from previous screen
    private String selectedDrinkId;
    private String selectedDrinkName;
    private double selectedDrinkPrice;
    private String selectedDrinkSize;
    private int selectedDrinkImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_table_screen);

        initializeViews();
        getDrinkInformation();
        setupTableList();
        setupSearchFunction();
        setupOrderCards();
    }

    private void initializeViews() {
        tablesRecyclerView = findViewById(R.id.tables_recycler_view);
        searchBar = findViewById(R.id.search_bar);
        orderCardsContainer = findViewById(R.id.order_cards_container);

        tablesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns for tables
    }

    private void getDrinkInformation() {
        Intent intent = getIntent();
        selectedDrinkId = intent.getStringExtra("SELECTED_DRINK_ID");
        selectedDrinkName = intent.getStringExtra("SELECTED_DRINK_NAME");
        selectedDrinkPrice = intent.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
        selectedDrinkSize = intent.getStringExtra("SELECTED_DRINK_SIZE");
        selectedDrinkImage = intent.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.placeholder_drink);
    }

    private void setupTableList() {
        tableList = new ArrayList<>();
        // Create sample tables
        for (int i = 1; i <= 20; i++) {
            String status = (i % 4 == 0) ? "occupied" : "available";
            tableList.add(new Table("" + i, "Bàn " + i, status, 4));
        }

        filteredTableList = new ArrayList<>(tableList);
        tableAdapter = new TableAdapter(filteredTableList, this);
        tablesRecyclerView.setAdapter(tableAdapter);
    }

    private void setupSearchFunction() {
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
        filteredTableList.clear();
        if (searchText.isEmpty()) {
            filteredTableList.addAll(tableList);
        } else {
            for (Table table : tableList) {
                if (table.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        table.getNumber().contains(searchText)) {
                    filteredTableList.add(table);
                }
            }
        }
        tableAdapter.notifyDataSetChanged();
    }

    private void setupOrderCards() {
        // TODO: Add existing order cards display
        // This will show ongoing orders for different tables
    }

    @Override
    public void onTableClick(Table table) {
        if (table.getStatus().equals("occupied")) {
            showOccupiedTableDialog(table);
        } else {
            showOrderConfirmationDialog(table);
        }
    }

    private void showOccupiedTableDialog(Table table) {
        new AlertDialog.Builder(this)
                .setTitle("Bàn đã có khách")
                .setMessage("Bàn " + table.getNumber() + " hiện đang có khách. Bạn có muốn thêm món vào order hiện tại không?")
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
                        "Giá: %.2f$\n\n" +
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
            finish();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}