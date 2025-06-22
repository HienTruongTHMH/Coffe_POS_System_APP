package com.midterm.myposapplication;

import java.util.ArrayList;
import java.util.List;

public class OrderDataManager {
    
    private static OrderDataManager instance;
    private List<OrderDataListener> listeners;
    
    private OrderDataManager() {
        listeners = new ArrayList<>();
    }
    
    public static OrderDataManager getInstance() {
        if (instance == null) {
            instance = new OrderDataManager();
        }
        return instance;
    }
    
    // Interface for listening to data changes
    public interface OrderDataListener {
        void onOrdersUpdated(List<Order> orders);
        void onOrderStatusChanged(Order order);
        void onOrderAdded(Order order);
        void onOrderRemoved(String orderId);
    }
    
    // Register/Unregister listeners
    public void addListener(OrderDataListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(OrderDataListener listener) {
        listeners.remove(listener);
    }
    
    // ✅ Centralized data access methods
    public List<Order> getAllOrders() {
        return Local_Database_Staff.getInstance().getAllOrders();
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return Local_Database_Staff.getInstance().getOrdersByStatus(status);
    }
    
    public List<Order> getOrdersByPaymentStatus(Order.PaymentStatus status) {
        return Local_Database_Staff.getInstance().getOrdersByPaymentStatus(status);
    }
    
    // ✅ Centralized update methods with notification
    public String addNewOrder(Order order) {
        String orderNumber = Local_Database_Staff.getInstance().addNewOrder(order);
        order.setOrderNumber(orderNumber);
        
        // Notify all listeners
        notifyOrderAdded(order);
        notifyOrdersUpdated();
        
        return orderNumber;
    }
    
    public void updateOrderStatus(Order order, Order.OrderStatus newStatus) {
        order.updateOrderStatus(newStatus);
        
        // Notify all listeners
        notifyOrderStatusChanged(order);
        notifyOrdersUpdated();
    }
    
    public void updatePaymentStatus(Order order, Order.PaymentStatus newStatus) {
        order.updatePaymentStatus(newStatus);
        
        // Notify all listeners
        notifyOrderStatusChanged(order);
        notifyOrdersUpdated();
    }
    
    public void removeOrder(String orderId) {
        Order order = findOrderById(orderId);
        if (order != null) {
            Local_Database_Staff.getInstance().removeOrder(order.getTableNumber());
            
            // Notify all listeners
            notifyOrderRemoved(orderId);
            notifyOrdersUpdated();
        }
    }
    
    // ✅ Helper methods
    private Order findOrderById(String orderId) {
        List<Order> orders = getAllOrders();
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
    
    // ✅ Notification methods
    private void notifyOrdersUpdated() {
        List<Order> currentOrders = getAllOrders();
        for (OrderDataListener listener : listeners) {
            listener.onOrdersUpdated(currentOrders);
        }
    }
    
    private void notifyOrderStatusChanged(Order order) {
        for (OrderDataListener listener : listeners) {
            listener.onOrderStatusChanged(order);
        }
    }
    
    private void notifyOrderAdded(Order order) {
        for (OrderDataListener listener : listeners) {
            listener.onOrderAdded(order);
        }
    }
    
    private void notifyOrderRemoved(String orderId) {
        for (OrderDataListener listener : listeners) {
            listener.onOrderRemoved(orderId);
        }
    }
    
    // ✅ Statistics methods
    public int getTotalOrdersCount() {
        return getAllOrders().size();
    }
    
    public int getOrdersCountByStatus(Order.OrderStatus status) {
        return getOrdersByStatus(status).size();
    }
    
    public double getTotalRevenue() {
        return Local_Database_Staff.getInstance().getTotalRevenue();
    }
}
