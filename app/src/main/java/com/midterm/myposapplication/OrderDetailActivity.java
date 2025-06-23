package com.midterm.myposapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
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

    private Button btnAction; // Renamed from btnPay
    private OrderManager orderManager;

    private Order currentOrder;

    private TextView tvOrderId, tvTable, tvStatus, tvSubtotal, tvDiscount, tvTotalPrice;
    private RecyclerView rvItems;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        
        // Initialize managers
        orderManager = OrderManager.getInstance();
        
        String orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID đơn hàng.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Order ID is missing from intent.");
            finish();
            return;
        }
        
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

        // Bind views with đúng ID
        toolbar = findViewById(R.id.toolbar);
        rvItems = findViewById(R.id.rv_order_items); // Sửa thành ID đúng
        tvStatus = findViewById(R.id.tv_order_detail_status); // Sửa thành ID đúng
        tvTable = findViewById(R.id.tv_order_detail_table); // Sửa thành ID đúng
        tvOrderId = findViewById(R.id.tv_order_detail_id); // Sửa thành ID đúng
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDiscount = findViewById(R.id.tv_discount);
        tvTotalPrice = findViewById(R.id.tv_total_price); // Sửa thành ID đúng
        btnAction = findViewById(R.id.btn_action);

        // Setup toolbar
        setupToolbar();
        
        // Load order details
        loadOrderDetails();
        
        // Setup action button based on order status
        setupActionButton();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadOrderDetails() {
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
            btnAction.setVisibility(View.GONE);
        } else {
            btnAction.setVisibility(View.VISIBLE);
        }
    }

    private void setStatusBadge(Order.OrderStatus status) {
        tvStatus.setText(status.getDisplayName());
        int backgroundRes;
        
        switch (status) {
            case ON_SERVICE:
                backgroundRes = R.drawable.status_badge_serving;
                break;
            case PREPARING:
            default:
                backgroundRes = R.drawable.status_badge_preparing;
                break;
        }
        
        tvStatus.setBackground(ContextCompat.getDrawable(this, backgroundRes));
    }

    /**
     * Setup action button based on order status
     */
    private void setupActionButton() {
        // Luôn đảm bảo button hiển thị
        btnAction.setVisibility(View.VISIBLE);
        
        if (currentOrder != null) {
            Order.OrderStatus status = currentOrder.getOrderStatus();
            
            if (status == Order.OrderStatus.PREPARING) {
                // Trạng thái đang chuẩn bị: hiển thị nút "Đánh dấu đã giao"
                btnAction.setText("Đánh dấu đã giao đến bàn");
                btnAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.status_serving)));
                
                btnAction.setOnClickListener(v -> {
                    updateOrderToDelivered();
                });
            } else if (status == Order.OrderStatus.ON_SERVICE) {
                // Trạng thái đã giao đến bàn: hiển thị nút thanh toán
                btnAction.setText("Thanh toán");
                btnAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_brown)));
                
                btnAction.setOnClickListener(v -> {
                    proceedToPayment();
                });
            }
        }
    }

    /**
     * Update order status to ON_SERVICE (Delivered to table)
     */
    private void updateOrderToDelivered() {
        if (currentOrder != null) {
            try {
                // Check class Order to see which method updates the status
                currentOrder.updateOrderStatus(Order.OrderStatus.ON_SERVICE);
                // Or
                // currentOrder.setOrderStatus(Order.OrderStatus.ON_SERVICE);
                
                // Update in database
                orderManager.updateOrder(currentOrder);
                
                // Update UI
                setStatusBadge(Order.OrderStatus.ON_SERVICE);
                
                // Show confirmation
                Toast.makeText(this, "Đơn hàng đã được giao đến bàn!", Toast.LENGTH_SHORT).show();
                
                // Update button
                setupActionButton();
            } catch (Exception e) {
                Log.e("OrderDetail", "Error updating order: " + e.getMessage());
                Toast.makeText(this, "Không thể cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void animateStatusChange() {
        // Animate status badge change
        tvStatus.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction(() -> {
                setStatusBadge(Order.OrderStatus.ON_SERVICE);
                tvStatus.animate().alpha(1f).setDuration(300).start();
            }).start();
    }

    /**
     * Navigate to payment screen
     */
    private void proceedToPayment() {
        Intent intent = new Intent(OrderDetailActivity.this, PaymentActivity.class);
        
        // ✅ Đảm bảo orderId không null và được truyền đúng
        if (currentOrder != null && currentOrder.getOrderId() != null) {
            intent.putExtra("order_id", currentOrder.getOrderId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Không thể xử lý thanh toán: ID đơn hàng không tồn tại", Toast.LENGTH_SHORT).show();
        }
    }
}