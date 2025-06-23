package com.midterm.myposapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm.myposapplication.base.BaseActivity;
import com.midterm.myposapplication.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends BaseActivity implements OrderManager.OrderListener {
    
    private static final String TAG = "OrderDetailActivity";
    
    // ✅ FIXED: Map to correct IDs from layout
    private TextView tvOrderNumber, tvOrderStatus, tvTableInfo, tvSubtotal, tvDiscount, tvTotalPrice;
    private RecyclerView rvOrderItems;
    private Button btnAction;
    private OrderDetailAdapter itemsAdapter;
    private Order currentOrder;
    private String orderId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        
        // Get order ID from intent
        orderId = getIntent().getStringExtra(Constants.KEY_ORDER_ID);
        if (orderId == null || orderId.isEmpty()) {
            Log.e(TAG, "No order ID provided in intent");
            showToast(R.string.error_no_order_id);
            finish();
            return;
        }
        
        initializeViews();
        loadOrderDetails();
        
        // Register as listener for order updates
        orderManager.addOrderListener(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        orderManager.removeOrderListener(this);
    }
    
    private void initializeViews() {
        // ✅ FIXED: Use correct IDs from activity_order_detail.xml
        tvOrderNumber = findViewById(R.id.tv_order_detail_id);
        tvOrderStatus = findViewById(R.id.tv_order_detail_status);
        tvTableInfo = findViewById(R.id.tv_order_detail_table);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDiscount = findViewById(R.id.tv_discount);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        rvOrderItems = findViewById(R.id.rv_order_items);
        btnAction = findViewById(R.id.btn_action);
        
        // ✅ FIXED: Setup RecyclerView with empty list initially
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        itemsAdapter = new OrderDetailAdapter(new ArrayList<>());
        rvOrderItems.setAdapter(itemsAdapter);
    }
    
    private void loadOrderDetails() {
        currentOrder = orderManager.getOrderById(orderId);
        if (currentOrder == null) {
            Log.e(TAG, "Order not found: " + orderId);
            showToast(R.string.error_no_order_id);
            finish();
            return;
        }
        
        updateUI();
    }
    
    private void updateUI() {
        if (currentOrder == null) return;
        
        // ✅ FIXED: Update order information using correct format
        tvOrderNumber.setText(FormatUtils.formatOrderNumber(this, currentOrder.getOrderNumber()));
        tvTableInfo.setText(FormatUtils.formatTableNumber(this, currentOrder.getTableNumber()));
        
        // Calculate amounts
        double totalAmount = currentOrder.getTotalAmount();
        double discount = 0.0; // You can add discount logic here
        double subtotal = totalAmount + discount;
        
        tvSubtotal.setText(FormatUtils.formatPrice(this, subtotal));
        tvDiscount.setText(FormatUtils.formatPrice(this, -discount));
        tvTotalPrice.setText(FormatUtils.formatPrice(this, totalAmount));
        
        // Update status badge
        updateStatusBadge(currentOrder.getOrderStatus());
        
        // ✅ FIXED: Update items list using correct method
        updateItemsList(currentOrder.getItems());
        
        // Setup action button
        setupActionButton();
    }
    
    private void updateStatusBadge(Order.OrderStatus status) {
        if (status == Order.OrderStatus.PREPARING) {
            tvOrderStatus.setText(R.string.status_preparing);
            tvOrderStatus.setBackgroundResource(R.drawable.status_badge_preparing);
        } else if (status == Order.OrderStatus.ON_SERVICE) {
            tvOrderStatus.setText(R.string.status_on_service);
            tvOrderStatus.setBackgroundResource(R.drawable.status_badge_serving);
        }
    }
    
    // ✅ FIXED: Create method to update items list
    private void updateItemsList(List<OrderItem> items) {
        if (itemsAdapter != null && items != null) {
            // Create new adapter with updated items
            itemsAdapter = new OrderDetailAdapter(items);
            rvOrderItems.setAdapter(itemsAdapter);
        }
    }
    
    private void setupActionButton() {
        btnAction.setVisibility(View.VISIBLE);
        
        if (currentOrder.getOrderStatus() == Order.OrderStatus.PREPARING) {
            // For PREPARING orders - Button to mark as served
            btnAction.setText(R.string.btn_mark_served);
            btnAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.status_serving)));
            btnAction.setOnClickListener(v -> markOrderAsServed());
        } else {
            // For ON_SERVICE orders - Button to proceed to payment
            btnAction.setText(R.string.btn_pay);
            btnAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_brown)));
            btnAction.setOnClickListener(v -> proceedToPayment());
        }
    }
    
    private void markOrderAsServed() {
        if (currentOrder != null) {
            orderManager.updateOrderStatus(currentOrder.getOrderId(), Order.OrderStatus.ON_SERVICE);
            showToast(R.string.msg_order_updated);
        }
    }
    
    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(Constants.KEY_ORDER_ID, currentOrder.getOrderId());
        startActivity(intent);
    }
    
    // OrderManager.OrderListener implementation
    @Override
    public void onOrderCreated(Order order) {}
    
    @Override
    public void onOrderUpdated(Order order) {
        if (order.getOrderId().equals(orderId)) {
            currentOrder = order;
            runOnUiThread(this::updateUI);
        }
    }
    
    @Override
    public void onOrderStatusChanged(Order order) {
        if (order.getOrderId().equals(orderId)) {
            currentOrder = order;
            runOnUiThread(this::updateUI);
        }
    }
    
    @Override
    public void onOrdersUpdated() {}
}