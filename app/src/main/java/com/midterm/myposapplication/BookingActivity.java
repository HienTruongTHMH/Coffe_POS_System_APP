package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private TextView drinkNameTextView;
    private TextView drinkPriceTextView;
    private TextView selectedSizeTextView;
    private Button btnAddToCart;
    private Button btnDecrease;
    private Button btnIncrease;
    private TextView tvQuantity;

    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_activity); // Đảm bảo rằng bạn đã có layout cho màn hình này.

        // Liên kết các phần tử giao diện
        drinkNameTextView = findViewById(R.id.drinkNameTextView);
        drinkPriceTextView = findViewById(R.id.drinkPriceTextView);
        selectedSizeTextView = findViewById(R.id.selectedSizeTextView);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        tvQuantity = findViewById(R.id.tvQuantity);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String drinkName = intent.getStringExtra("SELECTED_DRINK_NAME");
        double drinkPrice = intent.getDoubleExtra("SELECTED_DRINK_PRICE", 0.0);
        String selectedSize = intent.getStringExtra("SELECTED_DRINK_SIZE");

        // Hiển thị dữ liệu lên UI
        drinkNameTextView.setText(drinkName);
        drinkPriceTextView.setText(String.format("$%.2f", drinkPrice));
        selectedSizeTextView.setText(selectedSize);

        // Xử lý sự kiện khi bấm vào nút thêm vào giỏ
        btnAddToCart.setOnClickListener(v -> {
            // Code để thêm vào giỏ hàng hoặc thực hiện hành động khác
            Toast.makeText(this, "Đã thêm " + drinkName + " vào giỏ", Toast.LENGTH_SHORT).show();
            // Quay lại màn hình trước (hoặc chuyển đến màn hình giỏ hàng)
            finish();
        });

        // Xử lý sự kiện giảm số lượng
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        // Xử lý sự kiện tăng số lượng
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });
    }
}
