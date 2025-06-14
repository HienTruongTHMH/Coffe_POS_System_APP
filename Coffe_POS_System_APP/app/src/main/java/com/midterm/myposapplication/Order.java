package com.midterm.myposapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private String tableNumber;
    private String tableName;
    private List<OrderItem> items;
    private String status; // "preparing", "ready", "served"
    private long timestamp;
    private String paymentMethod;

    public Order(String tableNumber, String tableName) {
        this.orderId = UUID.randomUUID().toString();
        this.tableNumber = tableNumber;
        this.tableName = tableName;
        this.items = new ArrayList<>();
        this.status = "preparing";
        this.timestamp = System.currentTimeMillis();
    }

    public Order() {
        
    }

    // Thêm món vào đơn hàng
    public void addItem(OrderItem item) {
        // Kiểm tra xem món đã có trong đơn hàng chưa, nếu có thì cập nhật số lượng
        for (OrderItem existingItem : items) {
            if (existingItem.getDrinkId().equals(item.getDrinkId()) &&
                    existingItem.getSize().equals(item.getSize())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        // Nếu không có, thêm món mới vào đơn hàng
        items.add(item);
    }

    // Xóa món khỏi đơn hàng
    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    // Lấy tổng số món trong đơn hàng
    public int getTotalItems() {
        int total = 0;
        for (OrderItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    // Tính tổng giá trị đơn hàng
    public double getTotalAmount() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // Các getter và setter
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
