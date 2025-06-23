package com.midterm.myposapplication;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderManager {
    
    private static final String TAG = "OrderManager";
    private static OrderManager instance;
    private final CopyOnWriteArrayList<OrderListener> listeners;
    private final DatabaseManager databaseManager;
    
    public interface OrderListener {
        void onOrdersUpdated(List<Order> orders);
        void onOrderAdded(Order order);
        void onOrderStatusChanged(Order order);
        void onOrderRemoved(String orderId);
    }
    
    private OrderManager() {
        listeners = new CopyOnWriteArrayList<>();
        databaseManager = DatabaseManager.getInstance();
        Log.d(TAG, "OrderManager initialized");
    }
    
    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    
    // Listener management
    public void addListener(OrderListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            Log.d(TAG, "Listener added. Total: " + listeners.size());
        }
    }
    
    public void removeListener(OrderListener listener) {
        listeners.remove(listener);
        Log.d(TAG, "Listener removed. Total: " + listeners.size());
    }
    
    // Core operations
    public String createOrder(Order order) {
        Log.d(TAG, "Creating order for table: " + order.getTableName());
        
        String orderNumber = databaseManager.addOrder(order);
        order.setOrderNumber(orderNumber);
        
        // Update table status
        databaseManager.updateTableStatus(order.getTableNumber(), "occupied");
        
        // Notify listeners
        notifyOrderAdded(order);
        notifyOrdersUpdated();
        
        Log.d(TAG, "Order created successfully: " + orderNumber);
        return orderNumber;
    }
    
    public void updateOrderStatus(String orderId, Order.OrderStatus newStatus) {
        Order order = databaseManager.getOrderById(orderId);
        if (order != null) {
            Log.d(TAG, "Updating order " + order.getOrderNumber() + " to " + newStatus.getDisplayName());
            
            order.updateOrderStatus(newStatus);
            databaseManager.updateOrder(order);
            
            // Check if order is completed to free table
            if (newStatus == Order.OrderStatus.COMPLETED) {
                List<Order> tableOrders = getActiveOrdersByTable(order.getTableNumber());
                if (tableOrders.isEmpty()) {
                    databaseManager.updateTableStatus(order.getTableNumber(), "available");
                }
            }
            
            notifyOrderStatusChanged(order);
            notifyOrdersUpdated();
        }
    }

    // ✅ FIXED: Thêm phương thức xử lý thanh toán chuyên biệt
    public void processPaymentForOrder(String orderId) {
        Order order = databaseManager.getOrderById(orderId);
        if (order != null) {
            Log.d(TAG, "Processing payment for order: " + order.getOrderNumber());

            // Cập nhật cả hai trạng thái
            order.updatePaymentStatus(Order.PaymentStatus.PAID);
            order.updateOrderStatus(Order.OrderStatus.COMPLETED); // Chuyển trạng thái đơn hàng thành "Hoàn thành"

            // Lưu thay đổi vào database
            databaseManager.updateOrder(order);

            // Giải phóng bàn nếu không còn đơn hàng nào khác
            List<Order> remainingOrders = getActiveOrdersByTable(order.getTableNumber());
            if (remainingOrders.isEmpty()) {
                databaseManager.updateTableStatus(order.getTableNumber(), "available");
            }

            // Thông báo cho các listeners
            notifyOrderStatusChanged(order); // Thông báo trạng thái đã thay đổi
            notifyOrdersUpdated(); // Cập nhật lại toàn bộ danh sách
        } else {
            Log.e(TAG, "Could not process payment. Order not found with ID: " + orderId);
        }
    }
    
    public void removeOrder(String orderId) {
        Order order = databaseManager.getOrderById(orderId);
        if (order != null) {
            Log.d(TAG, "Removing order: " + order.getOrderNumber());
            
            databaseManager.removeOrder(orderId);
            
            // Check if table should be freed
            List<Order> remainingOrders = getActiveOrdersByTable(order.getTableNumber());
            if (remainingOrders.isEmpty()) {
                databaseManager.updateTableStatus(order.getTableNumber(), "available");
            }
            
            notifyOrderRemoved(orderId);
            notifyOrdersUpdated();
        }
    }
    
    // Query operations
    public List<Order> getAllOrders() {
        return databaseManager.getAllOrders();
    }
    
    public List<Order> getActiveOrders() {
        List<Order> activeOrders = new ArrayList<>();
        for (Order order : getAllOrders()) {
            if (order.getOrderStatus() != Order.OrderStatus.COMPLETED) {
                activeOrders.add(order);
            }
        }
        Log.d(TAG, "Found " + activeOrders.size() + " active orders");
        return activeOrders;
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return databaseManager.getOrdersByStatus(status);
    }
    
    public List<Order> getOrdersByTable(String tableNumber) {
        return databaseManager.getOrdersByTable(tableNumber);
    }
    
    public List<Order> getActiveOrdersByTable(String tableNumber) {
        List<Order> activeOrders = new ArrayList<>();
        for (Order order : getOrdersByTable(tableNumber)) {
            if (order.getOrderStatus() != Order.OrderStatus.COMPLETED) {
                activeOrders.add(order);
            }
        }
        return activeOrders;
    }

    // ✅ BỔ SUNG PHƯƠNG THỨC NÀY
    public Order getOrderByOrderNumber(String orderNumber) {
        if (orderNumber == null) return null;
        List<Order> allOrders = getAllOrders();
        for (Order order : allOrders) {
            if (orderNumber.equals(order.getOrderNumber())) {
                return order;
            }
        }
        return null;
    }

    // Statistics
    public int getTotalOrdersCount() {
        return getAllOrders().size();
    }
    
    public int getActiveOrdersCount() {
        return getActiveOrders().size();
    }
    
    public double getTotalRevenue() {
        return databaseManager.getTotalRevenue();
    }
    
    // Notification helpers
    private void notifyOrdersUpdated() {
        List<Order> orders = getAllOrders();
        for (OrderListener listener : listeners) {
            listener.onOrdersUpdated(orders);
        }
    }
    
    private void notifyOrderAdded(Order order) {
        for (OrderListener listener : listeners) {
            listener.onOrderAdded(order);
        }
    }
    
    private void notifyOrderStatusChanged(Order order) {
        for (OrderListener listener : listeners) {
            listener.onOrderStatusChanged(order);
        }
    }
    
    private void notifyOrderRemoved(String orderId) {
        for (OrderListener listener : listeners) {
            listener.onOrderRemoved(orderId);
        }
    }
}