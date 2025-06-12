package com.midterm.myposapplication;

public class OrderStatus {
    private String orderId;
    private String orderNumber;
    private String tableName;
    private String status; // "preparing", "ready", "served"
    private int itemCount;
    private long timestamp;

    public OrderStatus(String orderId, String orderNumber, String tableName, String status, int itemCount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.tableName = tableName;
        this.status = status;
        this.itemCount = itemCount;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatusText() {
        switch (status) {
            case "preparing":
                return "Preparing";
            case "ready":
                return "Ready to serve";
            case "served":
                return "Served";
            default:
                return "Unknown";
        }
    }

    public String getOrderCountText() {
        return itemCount + (itemCount == 1 ? " order" : " orders");
    }
}
