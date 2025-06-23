package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm.myposapplication.base.BaseActivity;
import com.midterm.myposapplication.utils.FormatUtils;
import java.util.ArrayList;

public class PaymentActivity extends BaseActivity {

    private static final String TAG = "PaymentActivity";

    // ✅ FIXED: Views now match activity_payment.xml
    private TextView tvTableInfo, tvSubtotalAmount, tvTaxAmount, tvTotalAmount;
    private RecyclerView orderItemsRecyclerView;
    private Button btnBanking, btnCashing;
    private ImageView btnBack;
    private OrderDetailAdapter itemsAdapter;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        String orderId = getIntent().getStringExtra(Constants.KEY_ORDER_ID);
        if (orderId == null || orderId.isEmpty()) {
            Log.e(TAG, "No order ID provided in intent");
            showToast(R.string.error_no_order_id);
            finish();
            return;
        }

        currentOrder = orderManager.getOrderById(orderId);
        if (currentOrder == null) {
            Log.e(TAG, "Order not found: " + orderId);
            showToast(R.string.error_no_order_id);
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        updateUI();
    }

    private void initializeViews() {
        // ✅ FIXED: IDs now match the XML file
        btnBack = findViewById(R.id.btn_back);
        orderItemsRecyclerView = findViewById(R.id.order_items_recycler_view);
        tvTableInfo = findViewById(R.id.table_info);
        tvSubtotalAmount = findViewById(R.id.subtotal_amount);
        tvTaxAmount = findViewById(R.id.tax_amount);
        tvTotalAmount = findViewById(R.id.total_amount);
        btnBanking = findViewById(R.id.btn_banking);
        btnCashing = findViewById(R.id.btn_cashing);

        // Setup RecyclerView
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsAdapter = new OrderDetailAdapter(new ArrayList<>()); // Start with empty list
        orderItemsRecyclerView.setAdapter(itemsAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnBanking.setOnClickListener(v -> processPayment(Constants.PAYMENT_CARD)); // Assuming Banking is Card/Digital
        btnCashing.setOnClickListener(v -> processPayment(Constants.PAYMENT_CASH));
    }

    private void updateUI() {
        if (currentOrder == null) return;

        // Update table info
        tvTableInfo.setText(currentOrder.getTableName());

        // Update order items
        itemsAdapter = new OrderDetailAdapter(currentOrder.getItems());
        orderItemsRecyclerView.setAdapter(itemsAdapter);

        // Calculate and display amounts
        double subtotal = currentOrder.getTotalAmount();
        double tax = subtotal * 0.10; // Assuming 10% tax
        double total = subtotal + tax;

        tvSubtotalAmount.setText(FormatUtils.formatPrice(this, subtotal));
        tvTaxAmount.setText(FormatUtils.formatPrice(this, tax));
        tvTotalAmount.setText(FormatUtils.formatPrice(this, total));
    }

    private void processPayment(String paymentMethod) {
        if (currentOrder != null) {
            // Update payment status and method
            currentOrder.setPaymentMethod(paymentMethod);
            orderManager.updatePaymentStatus(currentOrder.getOrderId(), Order.PaymentStatus.PAID);

            showToast(R.string.msg_payment_success);

            // Navigate back to main screen
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}