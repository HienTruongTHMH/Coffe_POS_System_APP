package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView drinksRecyclerView;
    private DrinkAdapter drinkAdapter;
    private List<Drink> drinkList;

    private static final int REQUEST_TABLE_SELECTION = 101;

    @Override
    // Phương thức này được gọi khi Activity được tạo
    // Chúng ta sẽ khởi tạo các thành phần giao diện người dùng và dữ liệu ở đây
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drinksRecyclerView = findViewById(R.id.drinks_recycler_view);
        drinksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 cột

        // Khởi tạo dữ liệu thức uống (ví dụ)
        drinkList = new ArrayList<>();
        drinkList.add(new Drink("Cappuccino", 3.00, R.drawable.placeholder_drink, true));
        drinkList.add(new Drink("Espresso", 2.00, R.drawable.placeholder_drink, false));
        drinkList.add(new Drink("Americano", 2.55, R.drawable.placeholder_drink, false));
        drinkList.add(new Drink("Latte", 4.00, R.drawable.placeholder_drink, true));
        drinkList.add(new Drink("Matcha", 3.50, R.drawable.placeholder_drink, false));
        drinkList.add(new Drink("Cold Brew", 4.50, R.drawable.placeholder_drink, false));


        drinkAdapter = new DrinkAdapter(drinkList);
        drinksRecyclerView.setAdapter(drinkAdapter);

        // Khởi tạo Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Xử lý các sự kiện click trên Bottom Navigation Bar
            int itemId = item.getItemId();
            if (itemId == R.id.nav_order) {
                // Điều hướng đến màn hình Order
                return true;
            } else if (itemId == R.id.nav_list) {
                // Điều hướng đến màn hình List
                return true;
            } else if (itemId == R.id.nav_package) {
                // Điều hướng đến màn hình Package
                return true;
            } else if (itemId == R.id.nav_cart) {
                // Điều hướng đến màn hình Cart
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Điều hướng đến màn hình Profile
                return true;
            }
            return false;
        });

        private void setupAddOrderButton() {
            View addOrderButton = findViewById(R.id.add_order_button);
            addOrderButton.setOnClickListener(v -> {
                // Create an intent to open the table selection screen
                Intent intent = new Intent(MainActivity.this, TableSelectionActivity.class);
                // Pass the selected drink information
                intent.putExtra("SELECTED_DRINK_ID", currentSelectedDrinkId);
                intent.putExtra("SELECTED_DRINK_NAME", currentSelectedDrinkName);
                intent.putExtra("SELECTED_DRINK_PRICE", currentSelectedDrinkPrice);
                intent.putExtra("SELECTED_DRINK_SIZE", currentSelectedSize);
                startActivityForResult(intent, REQUEST_TABLE_SELECTION);
            });
        }

    }


}