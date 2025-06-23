package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    
    private OrderManager orderManager;
    private DatabaseManager databaseManager;
    private Order currentOrder;
    
    // UI components
    private TextView totalAmountTextView;
    private TextView subtotalTextView;
    private TextView taxTextView;
    private TextView tableInfoTextView;
    private Button bankingButton;
    private Button cashingButton;
    private ImageView backButton;
    private RecyclerView orderItemsRecyclerView;
    private LinearLayout emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        // Khởi tạo managers
        orderManager = OrderManager.getInstance();
        databaseManager = DatabaseManager.getInstance();
        
        // Khởi tạo UI components
        initializeViews();
        
        // Lấy thông tin đơn hàng từ intent
        String orderId = getIntent().getStringExtra("order_id");
        if (orderId == null || orderId.isEmpty()) {
            Log.e("PaymentActivity", "No order ID provided in intent");
            Toast.makeText(this, "Không thể xử lý thanh toán: Thiếu thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình trước
            return;
        }
        
        Log.d(TAG, "Received order ID from intent: " + orderId);
        loadOrderDetails(orderId);
        
        // Setup click listeners
        setupClickListeners();
    }
    
    private void initializeViews() {
        // Tìm views bằng ID
        totalAmountTextView = findViewById(R.id.total_amount);
        subtotalTextView = findViewById(R.id.subtotal_amount);
        taxTextView = findViewById(R.id.tax_amount);
        tableInfoTextView = findViewById(R.id.table_info);
        bankingButton = findViewById(R.id.btn_banking);
        cashingButton = findViewById(R.id.btn_cashing);
        backButton = findViewById(R.id.btn_back);
        orderItemsRecyclerView = findViewById(R.id.order_items_recycler_view);
        
        // Đặt LayoutManager cho RecyclerView
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void loadOrderDetails(String orderId) {
        Log.d(TAG, "Attempting to load order with ID: " + orderId);
        
        // Thử tìm bằng orderId (UUID) trước
        currentOrder = orderManager.getOrderById(orderId);
        
        if (currentOrder == null) {
            Log.d(TAG, "Order not found by ID, trying by order number");
            // Nếu không tìm thấy bằng UUID, thử tìm bằng orderNumber
            currentOrder = orderManager.getOrderByOrderNumber(orderId);
        }
        
        if (currentOrder == null) {
            Log.e(TAG, "Order not found with ID/Number: " + orderId);
            Toast.makeText(this, "Lỗi: Không tìm thấy đơn hàng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        Log.d(TAG, "Order loaded successfully: " + currentOrder.getOrderNumber());
        
        // Hiển thị thông tin đơn hàng lên UI
        displayOrderInfo();
    }

    private void displayOrderInfo() {
        if (currentOrder == null) return;
        
        // Tính toán giá tiền
        double subtotal = currentOrder.getTotalAmount();
        double tax = subtotal * 0.1; // 10% thuế
        double total = subtotal + tax;
        
        // Format và hiển thị giá tiền với định dạng VND
        subtotalTextView.setText(String.format("%.0f VND", subtotal));
        taxTextView.setText(String.format("%.0f VND", tax));
        totalAmountTextView.setText(String.format("%.0f VND", total));
        
        // Hiển thị thông tin bàn
        tableInfoTextView.setText(currentOrder.getTableInfo());
        
        // Cập nhật tiêu đề với mã đơn hàng
        setTitle("Thanh toán - " + currentOrder.getOrderNumber());
        
        // Hiển thị danh sách món đã đặt
        setupOrderItemsRecyclerView();
        
        // Cập nhật trạng thái của nút thanh toán dựa vào trạng thái hiện tại
        updatePaymentButtonsState();
    }
    
    private void setupOrderItemsRecyclerView() {
        List<OrderItem> orderItems = currentOrder.getItems();
        
        if (orderItems != null && !orderItems.isEmpty()) {
            // Tạo và thiết lập adapter cho các món đã đặt
            PaymentItemAdapter adapter = new PaymentItemAdapter(orderItems, this);
            orderItemsRecyclerView.setAdapter(adapter);
            
            // Đảm bảo RecyclerView hiển thị và empty state ẩn
            orderItemsRecyclerView.setVisibility(View.VISIBLE);
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.GONE);
            }
        } else {
            // Nếu không có món nào, hiển thị trạng thái trống
            orderItemsRecyclerView.setVisibility(View.GONE);
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
            
            Log.w(TAG, "No items found in order " + currentOrder.getOrderNumber());
        }
    }
    
    private void updatePaymentButtonsState() {
        // Vô hiệu hóa nút thanh toán nếu đơn hàng đã được thanh toán
        boolean isPaid = currentOrder.getPaymentStatus() == Order.PaymentStatus.PAID;
        
        bankingButton.setEnabled(!isPaid);
        cashingButton.setEnabled(!isPaid);
        
        if (isPaid) {
            bankingButton.setAlpha(0.5f);
            cashingButton.setAlpha(0.5f);
            
            Toast.makeText(this, "Order has already been paid", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupClickListeners() {
        // Back button click
        backButton.setOnClickListener(v -> finish());
        
        // Banking payment button click
        bankingButton.setOnClickListener(v -> processPayment(Order.PaymentStatus.PAID, "Banking"));
        
        // Cash payment button click
        cashingButton.setOnClickListener(v -> processPayment(Order.PaymentStatus.PAID, "Cash"));
    }
    
    private void processPayment(Order.PaymentStatus status, String paymentMethod) {
        if (currentOrder != null) {
            // Cập nhật trạng thái thanh toán
            currentOrder.updatePaymentStatus(status);
            currentOrder.setPaymentMethod(paymentMethod);
            
            // Không còn cần đánh dấu đơn hàng là COMPLETED vì không còn trạng thái này
            // Thay vào đó, chỉ cần đánh dấu là đã thanh toán
            
            // Cập nhật trong database
            databaseManager.updateOrder(currentOrder);
            
            // Hiển thị thông báo thành công
            String message = "Payment successful via " + paymentMethod;
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.d(TAG, message);
            
            // Vô hiệu hóa nút thanh toán sau khi đã thanh toán
            updatePaymentButtonsState();
            
            // Chờ 1 giây trước khi quay lại màn hình Cart
            new android.os.Handler().postDelayed(this::finish, 1000);
        }
    }
}