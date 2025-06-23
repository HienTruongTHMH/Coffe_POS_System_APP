package com.midterm.myposapplication;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderManager {
    
    private static final String TAG = "OrderManager";
    private static OrderManager instance;
    private List<Order> orders;
    private List<OrderListener> listeners;
    
    public interface OrderListener {
        void onOrderCreated(Order order);
        void onOrderUpdated(Order order);
        void onOrderStatusChanged(Order order);
        void onOrdersUpdated();
    }
    
    private OrderManager() {
        this.orders = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }
    
    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    
    // ✅ FIXED: Order CRUD operations with proper return types
    public String createOrder(Order order) {
        orders.add(order);
        notifyOrderCreated(order);
        notifyOrdersUpdated();
        Log.d(TAG, "Created order: " + order.getOrderId());
        // ✅ Return order number for toast display
        return order.getOrderNumber();
    }
    
    public void updateOrder(Order order) {
        int index = findOrderIndex(order.getOrderId());
        if (index != -1) {
            orders.set(index, order);
            notifyOrderUpdated(order);
            notifyOrdersUpdated();
            Log.d(TAG, "Updated order: " + order.getOrderId());
        }
    }
    
    public Order getOrderById(String orderId) {
        return orders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }
    
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
    
    // ✅ FIXED: Add getActiveOrders method for workflow
    public List<Order> getActiveOrders() {
        return orders.stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.PREPARING || 
                                order.getOrderStatus() == Order.OrderStatus.ON_SERVICE)
                .collect(Collectors.toList());
    }
    
    // ✅ FIXED: Add getActiveOrdersByTable method for table-specific orders
    public List<Order> getActiveOrdersByTable(String tableNumber) {
        return orders.stream()
                .filter(order -> order.getTableNumber().equals(tableNumber))
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.PREPARING || 
                                order.getOrderStatus() == Order.OrderStatus.ON_SERVICE)
                .collect(Collectors.toList());
    }
    
    // ✅ FIXED: Filter methods supporting both String and Enum
    public List<Order> getOrdersByStatus(String statusFilter) {
        switch (statusFilter) {
            case Constants.FILTER_PREPARING:
                return orders.stream()
                        .filter(order -> order.getOrderStatus() == Order.OrderStatus.PREPARING)
                        .collect(Collectors.toList());
            case Constants.FILTER_ON_SERVICE:
                return orders.stream()
                        .filter(order -> order.getOrderStatus() == Order.OrderStatus.ON_SERVICE)
                        .collect(Collectors.toList());
            case Constants.FILTER_ALL:
            default:
                return getActiveOrders(); // Only active orders for main display
        }
    }
    
    // ✅ NEW: Overloaded method for enum parameter
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orders.stream()
                .filter(order -> order.getOrderStatus() == status)
                .collect(Collectors.toList());
    }
    
    public List<Order> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus) {
        return orders.stream()
                .filter(order -> order.getPaymentStatus() == paymentStatus)
                .collect(Collectors.toList());
    }
    
    public List<Order> getOrdersByTable(String tableNumber) {
        return orders.stream()
                .filter(order -> order.getTableNumber().equals(tableNumber))
                .collect(Collectors.toList());
    }
    
    // Status update methods
    public void updateOrderStatus(String orderId, Order.OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.updateOrderStatus(newStatus);
            updateOrder(order);
            notifyOrderStatusChanged(order);
        }
    }
    
    public void updatePaymentStatus(String orderId, Order.PaymentStatus newStatus) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.updatePaymentStatus(newStatus);
            updateOrder(order);
        }
    }
    
    // Helper methods
    private int findOrderIndex(String orderId) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(orderId)) {
                return i;
            }
        }
        return -1;
    }
    
    // ✅ FIXED: Listener management with correct method names
    public void addListener(OrderListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void addOrderListener(OrderListener listener) {
        addListener(listener); // Delegate to maintain compatibility
    }
    
    public void removeListener(OrderListener listener) {
        listeners.remove(listener);
    }
    
    public void removeOrderListener(OrderListener listener) {
        removeListener(listener); // Delegate to maintain compatibility
    }
    
    // Notification methods
    private void notifyOrderCreated(Order order) {
        for (OrderListener listener : listeners) {
            listener.onOrderCreated(order);
        }
    }
    
    private void notifyOrderUpdated(Order order) {
        for (OrderListener listener : listeners) {
            listener.onOrderUpdated(order);
        }
    }
    
    protected void notifyOrderStatusChanged(Order order) {
        for (OrderListener listener : listeners) {
            listener.onOrderStatusChanged(order);
        }
    }
    
    protected void notifyOrdersUpdated() {
        for (OrderListener listener : listeners) {
            listener.onOrdersUpdated();
        }
    }
}