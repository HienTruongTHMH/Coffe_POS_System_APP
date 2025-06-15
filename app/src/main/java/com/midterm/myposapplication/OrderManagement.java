package com.midterm.myposapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderManagement {
    private String orderId;
    private String orderNumber;
    private String dateTime;
    private String orderType;
    private String employee;
    private String orderStatus; // "preparing", "serving", "waiting_payment", "paid"
    private String paymentStatus; // "pending", "processing", "paid"
    private double amount;
    private String tableInfo;
    private long timestamp;
    private int itemCount;

    public OrderManagement(String orderId, String orderNumber, String orderType, String employee, 
                          String orderStatus, String paymentStatus, double amount, String tableInfo, int itemCount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.employee = employee;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.tableInfo = tableInfo;
        this.itemCount = itemCount;
        this.timestamp = System.currentTimeMillis();
        
        // Format datetime
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        this.dateTime = sdf.format(new Date(timestamp));
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getEmployee() { return employee; }
    public void setEmployee(String employee) { this.employee = employee; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTableInfo() { return tableInfo; }
    public void setTableInfo(String tableInfo) { this.tableInfo = tableInfo; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    // Helper methods
    public String getFormattedAmount() {
        return String.format("$ %.0f", amount);
    }

    // ✅ Add new method for formatted amount with item count
    public String getFormattedAmountWithItems() {
        return String.format("$ %.0f (%d items)", amount, itemCount);
    }

    public String getOrderStatusDisplayName() {
        switch (orderStatus) {
            case "preparing": return "Đang làm";
            case "serving": return "Đang phục vụ";
            case "waiting_payment": return "Chờ thanh toán";
            case "paid": return "Hoàn thành";
            default: return "Unknown";
        }
    }

    public String getPaymentStatusDisplayName() {
        switch (paymentStatus) {
            case "pending": return "Chờ thanh toán";
            case "processing": return "Đang thanh toán";
            case "paid": return "Đã thanh toán";
            default: return "Unknown";
        }
    }
}
