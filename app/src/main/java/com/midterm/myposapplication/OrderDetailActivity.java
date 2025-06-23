package com.midterm.myposapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "EXTRA_ORDER_ID";
    private static final String TAG = "OrderDetailActivity";

    private OrderManager orderManager;
    private Order currentOrder;

    private TextView tvOrderId, tvTable, tvStatus, tvSubtotal, tvDiscount, tvTotalPrice;
    private RecyclerView rvItems;
    private Button btnPay;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        String orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Order ID is missing from intent.");
            finish();
            return;
        }

        orderManager = OrderManager.getInstance();
        
        // Thử tìm bằng cả hai cách (orderId và orderNumber)
        currentOrder = orderManager.getOrderByOrderNumber(orderId); // Tìm theo UUID
        
        if (currentOrder == null) {
            // Nếu không tìm thấy, thử tìm theo orderNumber
            currentOrder = orderManager.getOrderByOrderNumber(orderId);
        }

        if (currentOrder == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy đơn hàng.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Order with ID " + orderId + " not found.");
            finish();
            return;
        }

        // Tiếp tục xử lý nếu tìm thấy đơn hàng
        initializeViews();
        setupToolbar();
        populateData();
        setupListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvOrderId = findViewById(R.id.tv_order_detail_id);
        tvTable = findViewById(R.id.tv_order_detail_table);
        tvStatus = findViewById(R.id.tv_order_detail_status);
        rvItems = findViewById(R.id.rv_order_items);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDiscount = findViewById(R.id.tv_discount);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnPay = findViewById(R.id.btn_pay);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void populateData() {
        tvOrderId.setText("Đơn hàng " + currentOrder.getOrderNumber());
        tvTable.setText(currentOrder.getTableName());
        
        // ✅ FIXED: Gọi đúng phương thức getOrderStatus()
        setStatusBadge(currentOrder.getOrderStatus());

        OrderDetailAdapter adapter = new OrderDetailAdapter(currentOrder.getItems());
        rvItems.setAdapter(adapter);

        // ✅ FIXED: Gọi đúng phương thức getTotalAmount()
        double subtotal = currentOrder.getTotalAmount();
        double discount = 0.0;
        double total = subtotal - discount;

        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvDiscount.setText(String.format("-$%.2f", discount));
        tvTotalPrice.setText(String.format("$%.2f", total));

        // ✅ FIXED: Ẩn nút thanh toán nếu đơn hàng đã hoàn thành hoặc đã thanh toán
        if (currentOrder.isCompleted() || currentOrder.isPaid()) {
            btnPay.setVisibility(View.GONE);
        } else {
            btnPay.setVisibility(View.VISIBLE);
        }
    }

    private void setStatusBadge(Order.OrderStatus status) {
        tvStatus.setText(status.getDisplayName());
        int backgroundRes;
        // ✅ FIXED: Switch case đầy đủ cho OrderStatus
        switch (status) {
            case PREPARING:
                backgroundRes = R.drawable.status_badge_preparing;
                break;
            case READY:
                backgroundRes = R.drawable.status_badge_ready;
                break;
            case SERVING:
                backgroundRes = R.drawable.status_badge_serving;
                break;
            case COMPLETED:
            case PAID: // Gộp chung PAID và COMPLETED để hiển thị
                backgroundRes = R.drawable.status_badge_paid;
                break;
            default:
                backgroundRes = R.drawable.status_badge_default;
                break;
        }
        tvStatus.setBackground(ContextCompat.getDrawable(this, backgroundRes));
    }

    private void setupListeners() {
        btnPay.setOnClickListener(v -> {
            // ✅ FIXED: Gọi phương thức xử lý thanh toán chuyên biệt
            orderManager.processPaymentForOrder(currentOrder.getOrderId());
            Toast.makeText(this, "Đã thanh toán đơn hàng " + currentOrder.getOrderNumber(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}