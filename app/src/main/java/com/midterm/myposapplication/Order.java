package com.midterm.myposapplication;

import com.midterm.myposapplication.utils.IdGenerator;
import java.util.ArrayList;
import java.util.List;

public class Order {
    
    public enum OrderStatus {
        PREPARING("preparing"),
        ON_SERVICE("on_service");
        
        private final String value;
        OrderStatus(String value) { this.value = value; }
        public String getValue() { return value; }
    }
    
    public enum PaymentStatus {
        WAITING("waiting"),
        PAID("paid");
        
        private final String value;
        PaymentStatus(String value) { this.value = value; }
        public String getValue() { return value; }
    }
    
    private String orderId;
    private String orderNumber;
    private String tableNumber;
    private String tableName;
    private String employeeName;
    private List<OrderItem> items;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private long createdAt;
    private long updatedAt;
    
    // Constructor
    public Order(String tableNumber, String tableName, String employeeName) {
        this.orderId = IdGenerator.generateOrderId();
        this.orderNumber = IdGenerator.generateOrderNumber();
        this.tableNumber = tableNumber;
        this.tableName = tableName;
        this.employeeName = employeeName;
        this.items = new ArrayList<>();
        this.orderStatus = OrderStatus.PREPARING;
        this.paymentStatus = PaymentStatus.WAITING;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
    
    // Status update methods
    public void updateOrderStatus(OrderStatus status) {
        this.orderStatus = status;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public void setPaymentMethod(String method) {
        this.paymentMethod = method;
        this.updatedAt = System.currentTimeMillis();
    }
    
    // Item management
    public void addItem(OrderItem item) {
        this.items.add(item);
        this.updatedAt = System.currentTimeMillis();
    }
    
    public void removeItem(OrderItem item) {
        this.items.remove(item);
        this.updatedAt = System.currentTimeMillis();
    }
    
    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public String getTableNumber() { return tableNumber; }
    public String getTableName() { return tableName; }
    public String getEmployeeName() { return employeeName; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
}