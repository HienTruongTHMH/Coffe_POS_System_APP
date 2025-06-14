package com.midterm.myposapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class TableSelectionActivity extends AppCompatActivity implements OnTableClickListener {

    private static final String TAG = "TableSelectionActivity";

    // UI Components
    private RecyclerView insideTablesRecycler;
    private RecyclerView outsideTablesRecycler;
    private TableAdapter insideTableAdapter;
    private TableAdapter outsideTableAdapter;

    // Data
    private List<Table> insideTables;
    private List<Table> outsideTables;

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

        Log.d(TAG, "onCreate started");

        // Initialize components
        initializeViews();
        getDrinkInformation();
        setupTableData();
        setupRecyclerViews();
        setupBottomNavigation();

        Log.d(TAG, "onCreate completed");
    }

    private void initializeViews() {
        insideTablesRecycler = findViewById(R.id.inside_tables_recycler);
        outsideTablesRecycler = findViewById(R.id.outside_tables_recycler);

        Log.d(TAG, "Views initialized - Inside: " + (insideTablesRecycler != null) + 
                   ", Outside: " + (outsideTablesRecycler != null));
    }

    private void getDrinkInformation() {
        Intent intent = getIntent();
        selectedDrinkId = intent.getStringExtra("SELECTED_DRINK_ID");
        selectedDrinkName = intent.getStringExtra("SELECTED_DRINK_NAME");
        selectedDrinkPrice = intent.getDoubleExtra("SELECTED_DRINK_PRICE", 0);
        selectedDrinkSize = intent.getStringExtra("SELECTED_DRINK_SIZE");
        selectedDrinkImage = intent.getIntExtra("SELECTED_DRINK_IMAGE", R.drawable.ic_profile_avatar);

        Log.d(TAG, "Drink info - Name: " + selectedDrinkName + ", Price: " + selectedDrinkPrice);
    }

    private void setupTableData() {
        // Inside tables
        insideTables = new ArrayList<>();
        insideTables.add(new Table("1", "Bàn 1", "occupied", 4));
        insideTables.add(new Table("2", "Bàn 2", "preparing", 4));
        insideTables.add(new Table("3", "Bàn 3", "available", 4));

        // Outside tables  
        outsideTables = new ArrayList<>();
        outsideTables.add(new Table("4", "Bàn 4", "occupied", 6));
        outsideTables.add(new Table("5", "Bàn 5", "available", 6));
        outsideTables.add(new Table("6", "Bàn 6", "available", 4));

        Log.d(TAG, "Table data setup - Inside: " + insideTables.size() + 
                   ", Outside: " + outsideTables.size());
    }

    private void setupRecyclerViews() {
        if (insideTablesRecycler != null && insideTables != null) {
            // Set fixed height for testing
            ViewGroup.LayoutParams params = insideTablesRecycler.getLayoutParams();
            params.height = 400; // Fixed height 400dp
            insideTablesRecycler.setLayoutParams(params);
            
            insideTableAdapter = new TableAdapter(insideTables, this);
            insideTablesRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            insideTablesRecycler.setAdapter(insideTableAdapter);
            Log.d(TAG, "Inside RecyclerView setup completed with height: " + params.height);
        }

        if (outsideTablesRecycler != null && outsideTables != null) {
            // Set fixed height for testing
            ViewGroup.LayoutParams params = outsideTablesRecycler.getLayoutParams();
            params.height = 400; // Fixed height 400dp
            outsideTablesRecycler.setLayoutParams(params);
            
            outsideTableAdapter = new TableAdapter(outsideTables, this);
            outsideTablesRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            outsideTablesRecycler.setAdapter(outsideTableAdapter);
            Log.d(TAG, "Outside RecyclerView setup completed with height: " + params.height);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_list);
            
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                if (itemId == R.id.nav_order) {
                    Intent intent = new Intent(TableSelectionActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_list) {
                    return true;
                } else {
                    Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    @Override
    public void onTableClick(Table table) {
        Log.d(TAG, "Table clicked: " + table.getName());
        
        if (selectedDrinkName != null && !selectedDrinkName.isEmpty()) {
            showOrderConfirmationDialog(table);
        } else {
            selectTableForNewOrder(table);
        }
    }

    private void showOrderConfirmationDialog(Table table) {
        if (!table.isAvailable()) {
            new AlertDialog.Builder(this)
                .setTitle("Bàn đã có khách")
                .setMessage("Bàn " + table.getName() + " đã có khách. Bạn có muốn thêm order vào bàn này không?")
                .setPositiveButton("Thêm order", (dialog, which) -> confirmOrder(table))
                .setNegativeButton("Hủy", null)
                .show();
        } else {
            confirmOrder(table);
        }
    }

    private void confirmOrder(Table table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận order");

        String message = String.format(
                "Bàn: %s\n" +
                "Món: %s (%s)\n" +
                "Giá: $%.2f\n\n" +
                "Xác nhận thêm món này vào order?",
                table.getName(),
                selectedDrinkName != null ? selectedDrinkName : "N/A",
                selectedDrinkSize != null ? selectedDrinkSize : "M",
                selectedDrinkPrice
        );

        builder.setMessage(message);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_TABLE_NUMBER", table.getNumber());
            resultIntent.putExtra("SELECTED_TABLE_NAME", table.getName());
            resultIntent.putExtra("SELECTED_DRINK_ID", selectedDrinkId);
            resultIntent.putExtra("SELECTED_DRINK_NAME", selectedDrinkName);
            resultIntent.putExtra("SELECTED_DRINK_PRICE", selectedDrinkPrice);
            resultIntent.putExtra("SELECTED_DRINK_SIZE", selectedDrinkSize);
            resultIntent.putExtra("SELECTED_DRINK_IMAGE", selectedDrinkImage);

            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, "Đã thêm " + selectedDrinkName + " vào " + table.getName(), Toast.LENGTH_SHORT).show();
            finish();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void selectTableForNewOrder(Table table) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("SELECTED_TABLE_NUMBER", table.getNumber());
        resultIntent.putExtra("SELECTED_TABLE_NAME", table.getName());
        resultIntent.putExtra("MODE", "TABLE_SELECTED");
        
        startActivity(resultIntent);
        finish();
    }
}