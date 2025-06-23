package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DashboardActivityAdmin extends AppCompatActivity {
    
    private static final String TAG = "DashboardActivityAdmin";
    
    private UserManager userManager;
    private OrderManager orderManager;
    private DatabaseManager databaseManager;
    
    // UI Components
    private TextView tvRevenue;
    private TextView tvTotalExpense;
    private TextView tvCashRevenue;
    private TextView tvBankTransferRevenue;
    private TextView tvActualRevenue;
    
    // Sales quantity TextViews
    private TextView tvCafeSuaQuantity;
    private TextView tvBacXiuQuantity;
    private TextView tvMatchaLatteQuantity;
    
    // Sales percentage TextViews
    private TextView tvCafeSuaPercentage;
    private TextView tvBacXiuPercentage;
    private TextView tvMatchaLatPercentage;
    
    // Bottom Navigation
    private LinearLayout listTab, packageTab, cartTab, profileTab;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        // Initialize components
        initializeComponents();
        
        // Check if user is admin
        if (!userManager.isAdmin()) {
            Toast.makeText(this, "Không có quyền truy cập", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }
        
        // Load dashboard data
        loadDashboardData();
        
        // Setup navigation
        setupBottomNavigation();
    }
    
    private void initializeComponents() {
        userManager = UserManager.getInstance(this);
        orderManager = OrderManager.getInstance();
        databaseManager = DatabaseManager.getInstance();
        
        // Revenue TextViews
        tvRevenue = findViewById(R.id.tv_revenue);
        tvTotalExpense = findViewById(R.id.tv_total_expense);
        tvCashRevenue = findViewById(R.id.tv_cash_revenue);
        tvBankTransferRevenue = findViewById(R.id.tv_bank_transfer_revenue);
        tvActualRevenue = findViewById(R.id.tv_actual_revenue);
        
        // Sales quantity TextViews
        tvCafeSuaQuantity = findViewById(R.id.tv_cafe_sua_quantity);
        tvBacXiuQuantity = findViewById(R.id.tv_bac_xiu_quantity);
        tvMatchaLatteQuantity = findViewById(R.id.tv_matcha_latte_quantity);
        
        // Sales percentage TextViews
        tvCafeSuaPercentage = findViewById(R.id.tv_cafe_sua_percentage);
        tvBacXiuPercentage = findViewById(R.id.tv_bac_xiu_percentage);
        tvMatchaLatPercentage = findViewById(R.id.tv_matcha_lat_percentage);
        
        // Bottom Navigation
        listTab = findViewById(R.id.listTab);
        packageTab = findViewById(R.id.packageTab);
        cartTab = findViewById(R.id.cartTab);
        profileTab = findViewById(R.id.profileTab);
    }
    
    private void loadDashboardData() {
        try {
            // Get all paid orders for calculation
            List<Order> paidOrders = orderManager.getOrdersByPaymentStatus(Order.PaymentStatus.PAID);
            
            // Calculate revenue data
            double totalRevenue = calculateTotalRevenue(paidOrders);
            double cashRevenue = calculateCashRevenue(paidOrders);
            double bankRevenue = calculateBankRevenue(paidOrders);
            double totalExpense = 200000; // Mock data - replace with actual expense calculation
            double actualRevenue = totalRevenue - totalExpense;
            
            // Update revenue displays
            tvRevenue.setText(formatCurrency(totalRevenue));
            tvTotalExpense.setText(formatCurrency(totalExpense));
            tvCashRevenue.setText(formatCurrency(cashRevenue));
            tvBankTransferRevenue.setText(formatCurrency(bankRevenue));
            tvActualRevenue.setText(formatCurrency(actualRevenue));
            
            // Calculate and display sales statistics
            updateSalesStatistics(paidOrders);
            
            Log.d(TAG, "Dashboard data loaded successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading dashboard data", e);
            Toast.makeText(this, "Lỗi tải dữ liệu dashboard", Toast.LENGTH_SHORT).show();
        }
    }
    
    private double calculateTotalRevenue(List<Order> orders) {
        double total = 0;
        for (Order order : orders) {
            total += order.getTotalAmount();
        }
        return total;
    }
    
    private double calculateCashRevenue(List<Order> orders) {
        double total = 0;
        for (Order order : orders) {
            if ("Cash".equals(order.getPaymentMethod()) || "Cashing".equals(order.getPaymentMethod())) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }
    
    private double calculateBankRevenue(List<Order> orders) {
        double total = 0;
        for (Order order : orders) {
            if ("Banking".equals(order.getPaymentMethod())) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }
    
    private void updateSalesStatistics(List<Order> orders) {
        int cafeSuaCount = 0;
        int bacXiuCount = 0;
        int matchaLatteCount = 0;
        int totalItems = 0;
        
        // Count items by type
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                totalItems += item.getQuantity();
                String drinkName = item.getDrinkName().toLowerCase();
                
                if (drinkName.contains("cà phê sữa") || drinkName.contains("cafe sua")) {
                    cafeSuaCount += item.getQuantity();
                } else if (drinkName.contains("bạc xíu") || drinkName.contains("bac xiu")) {
                    bacXiuCount += item.getQuantity();
                } else if (drinkName.contains("matcha latte")) {
                    matchaLatteCount += item.getQuantity();
                }
            }
        }
        
        // Calculate percentages
        if (totalItems > 0) {
            double cafeSuaPercent = (cafeSuaCount * 100.0) / totalItems;
            double bacXiuPercent = (bacXiuCount * 100.0) / totalItems;
            double matchaPercent = (matchaLatteCount * 100.0) / totalItems;
            
            // Update UI
            tvCafeSuaQuantity.setText(cafeSuaCount + " đơn");
            tvBacXiuQuantity.setText(bacXiuCount + " đơn");
            tvMatchaLatteQuantity.setText(matchaLatteCount + " đơn");
            
            tvCafeSuaPercentage.setText(String.format("%.1f%%", cafeSuaPercent));
            tvBacXiuPercentage.setText(String.format("%.1f%%", bacXiuPercent));
            tvMatchaLatPercentage.setText(String.format("%.1f%%", matchaPercent));
        }
    }
    
    private String formatCurrency(double amount) {
        return String.format("%.0f VND", amount);
    }
    
    private void setupBottomNavigation() {
        listTab.setOnClickListener(v -> {
            startActivity(new Intent(this, Cart.class));
        });
        
        packageTab.setOnClickListener(v -> {
            Toast.makeText(this, "Package functionality coming soon", Toast.LENGTH_SHORT).show();
        });
        
        cartTab.setOnClickListener(v -> {
            startActivity(new Intent(this, Cart.class));
        });
        
        profileTab.setOnClickListener(v -> showLogoutDialog());
    }
    
    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    userManager.logoutUser();
                    redirectToLogin();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void redirectToLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData(); // Refresh data when returning to dashboard
    }
}