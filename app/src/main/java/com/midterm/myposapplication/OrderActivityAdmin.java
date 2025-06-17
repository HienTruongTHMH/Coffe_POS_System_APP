package com.midterm.myposapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderActivityAdmin extends AppCompatActivity {
    private EditText searchEditText;
    private LinearLayout ordersContainer;
    private final List<View> allOrderViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_import_history);

        // Khởi tạo views
        searchEditText = findViewById(R.id.searchEditText);
        ordersContainer = findViewById(R.id.ordersContainer);

        // Lưu trữ tất cả các view đơn hàng ban đầu
        saveAllOrderViews();

        // Thiết lập sự kiện tìm kiếm
        setupSearchFunction();
    }

    private void saveAllOrderViews() {
        // Lấy tất cả các view con trong ordersContainer (mỗi đơn hàng là một LinearLayout)
        for (int i = 0; i < ordersContainer.getChildCount(); i++) {
            View child = ordersContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                allOrderViews.add(child);
            }
        }
    }

    private void setupSearchFunction() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterOrders(String query) {
        // Xóa tất cả các view hiện tại
        ordersContainer.removeAllViews();

        // Nếu query rỗng, hiển thị tất cả
        if (query.isEmpty()) {
            for (View orderView : allOrderViews) {
                ordersContainer.addView(orderView);
            }
            return;
        }

        // Lọc và hiển thị các đơn hàng phù hợp
        for (View orderView : allOrderViews) {
            // Tìm TextView chứa tên sản phẩm trong mỗi orderView
            TextView productNameTextView = findProductNameTextView(orderView);
            if (productNameTextView != null) {
                String productName = productNameTextView.getText().toString().toLowerCase();
                if (productName.contains(query.toLowerCase())) {
                    ordersContainer.addView(orderView);
                }
            }
        }
    }

    private TextView findProductNameTextView(View orderView) {
        if (orderView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) orderView;
            // Tìm theo ID
            TextView productNameView = viewGroup.findViewById(R.id.productName1);
            if (productNameView == null)
                productNameView = viewGroup.findViewById(R.id.productName2);
            if (productNameView == null)
                productNameView = viewGroup.findViewById(R.id.productName3);
            if (productNameView == null)
                productNameView = viewGroup.findViewById(R.id.productName4);
            if (productNameView == null)
                productNameView = viewGroup.findViewById(R.id.productName5);

            if (productNameView != null) {
                return productNameView;
            }

            // Duyệt đệ quy nếu không tìm thấy bằng ID
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof TextView) {
                    TextView textView = (TextView) child;
                    if (textView.getText().toString().equals("Capuchino")) {
                        return textView;
                    }
                } else if (child instanceof ViewGroup) {
                    TextView found = findProductNameTextView(child);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }
}