package com.midterm.myposapplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Order {
    // Basic info
    private String orderId;
    private String orderNumber;
    private String tableNumber;
    private String tableName;
    private String employeeName;
    
    // Order items
    private List<OrderItem> items;
    
    // Status tracking
    private OrderStatus orderStatus;      // preparing, ready, serving, completed
    private PaymentStatus paymentStatus;  // pending, processing, paid
    
    // Timestamps
    private long createdAt;
    private long updatedAt;
    
    // Order status enum
    public enum OrderStatus {
        PREPARING("Đang chuẩn bị"),
        READY("Sẵn sàng"),
        SERVING("Đang phục vụ"),
        PAID("Đã thanh toán"), // ✅ FIXED: Thêm trạng thái PAID

        COMPLETED("Hoàn thành");

        
        private final String displayName;
        
        OrderStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Payment status enum
    public enum PaymentStatus {
        PENDING("pending", "Chờ thanh toán"),
        PROCESSING("processing", "Đang thanh toán"),
        PAID("paid", "Đã thanh toán");
        
        private final String code;
        private final String displayName;
        
        PaymentStatus(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }
        
        public String getCode() { return code; }
        public String getDisplayName() { return displayName; }
    }
    
    // Constructor
    public Order(String tableNumber, String tableName, String employeeName) {
        this.orderId = UUID.randomUUID().toString();
        this.tableNumber = tableNumber;
        this.tableName = tableName;
        this.employeeName = employeeName;
        this.items = new ArrayList<>();
        this.orderStatus = OrderStatus.PREPARING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
    
    // Item management
    public void addItem(OrderItem item) {
        // Check if item already exists, update quantity
        for (OrderItem existingItem : items) {
            if (existingItem.getDrinkId().equals(item.getDrinkId()) && 
                existingItem.getSize().equals(item.getSize())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                updateTimestamp();
                return;
            }
        }
        items.add(item);
        updateTimestamp();
    }
    
    public void removeItem(String drinkId, String size) {
        items.removeIf(item -> item.getDrinkId().equals(drinkId) && item.getSize().equals(size));
        updateTimestamp();
    }
    
    public void updateItemQuantity(String drinkId, String size, int newQuantity) {
        for (OrderItem item : items) {
            if (item.getDrinkId().equals(drinkId) && item.getSize().equals(size)) {
                if (newQuantity <= 0) {
                    removeItem(drinkId, size);
                } else {
                    item.setQuantity(newQuantity);
                    updateTimestamp();
                }
                return;
            }
        }
    }
    
    // Calculations
    public int getTotalItems() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }
    
    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }
    
    // Status management
    public void updateOrderStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
        updateTimestamp();
    }
    
    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
        updateTimestamp();
    }
    
    // Helper methods
    public String getFormattedAmount() {
        return String.format("$ %.2f", getTotalAmount());
    }
    
    public String getFormattedAmountWithItems() {
        return String.format("$ %.2f (%d items)", getTotalAmount(), getTotalItems());
    }
    
    public String getFormattedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        return sdf.format(new Date(createdAt));
    }
    
    public String getTableInfo() {
        return tableName.contains("ngoài") ? "Outside, " + tableName : "Inside, " + tableName;
    }
    
    private void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public String getTableNumber() { return tableNumber; }
    public String getTableName() { return tableName; }
    public String getEmployeeName() { return employeeName; }
    
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    
    public OrderStatus getOrderStatus() { return orderStatus; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    
    public boolean isEmpty() { return items.isEmpty(); }
    public boolean isCompleted() { return orderStatus == OrderStatus.COMPLETED; }
    public boolean isPaid() { return paymentStatus == PaymentStatus.PAID; }
}