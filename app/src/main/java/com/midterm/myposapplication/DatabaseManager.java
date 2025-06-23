package com.midterm.myposapplication;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {
    
    private static final String TAG = "DatabaseManager";
    private static DatabaseManager instance;
    
    // Data storage
    private final List<Order> orders;
    private final List<Table> tables;
    private final List<Drink> drinks;
    private final Map<String, Order> orderMap; // For fast lookup
    private final Map<String, Table> tableMap; // For fast lookup
    
    // Counters
    private final AtomicInteger orderCounter;
    
    private DatabaseManager() {
        orders = new ArrayList<>();
        tables = new ArrayList<>();
        drinks = new ArrayList<>();
        orderMap = new HashMap<>();
        tableMap = new HashMap<>();
        orderCounter = new AtomicInteger(2100); // Start from 2101
        
        initializeData();
        Log.d(TAG, "DatabaseManager initialized");
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void initializeData() {
        setupDrinks();
        setupTables();
        setupSampleOrders();
        Log.d(TAG, "Database initialized with " + orders.size() + " orders, " + 
                   tables.size() + " tables, " + drinks.size() + " drinks");
    }
    
    // Order operations
    public String addOrder(Order order) {
        String orderNumber = "#" + orderCounter.incrementAndGet();
        
        orders.add(order);
        orderMap.put(order.getOrderId(), order);
        
        Log.d(TAG, "Added order: " + orderNumber + " for table " + order.getTableName());
        return orderNumber;
    }
    
    public void updateOrder(Order order) {
        orderMap.put(order.getOrderId(), order);
        Log.d(TAG, "Updated order: " + order.getOrderNumber());
    }
    
    public void removeOrder(String orderId) {
        Order order = orderMap.remove(orderId);
        if (order != null) {
            orders.remove(order);
            Log.d(TAG, "Removed order: " + order.getOrderNumber());
        }
    }
    
    public Order getOrderById(String orderId) {
        return orderMap.get(orderId);
    }
    
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getOrderStatus() == status) {
                filteredOrders.add(order);
            }
        }
        return filteredOrders;
    }
    
    public List<Order> getOrdersByTable(String tableNumber) {
        List<Order> tableOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getTableNumber().equals(tableNumber)) {
                tableOrders.add(order);
            }
        }
        return tableOrders;
    }
    
    // Table operations
    public List<Table> getAllTables() {
        return new ArrayList<>(tables);
    }
    
    public List<Table> getInsideTables() {
        List<Table> insideTables = new ArrayList<>();
        for (Table table : tables) {
            if (!table.getName().contains("ngoài")) {
                insideTables.add(table);
            }
        }
        return insideTables;
    }
    
    public List<Table> getOutsideTables() {
        List<Table> outsideTables = new ArrayList<>();
        for (Table table : tables) {
            if (table.getName().contains("ngoài")) {
                outsideTables.add(table);
            }
        }
        return outsideTables;
    }
    
    public Table getTableByNumber(String tableNumber) {
        return tableMap.get(tableNumber);
    }
    
    public void updateTableStatus(String tableNumber, String status) {
        Table table = tableMap.get(tableNumber);
        if (table != null) {
            table.setStatus(status);
            table.setLastOrderTime(System.currentTimeMillis());
            Log.d(TAG, "Updated table " + tableNumber + " status to " + status);
        }
    }
    
    // Drink operations
    public List<Drink> getAllDrinks() {
        return new ArrayList<>(drinks);
    }
    
    public Drink getDrinkById(String drinkId) {
        for (Drink drink : drinks) {
            if (drink.getId().equals(drinkId)) {
                return drink;
            }
        }
        return null;
    }
    
    // Statistics
    public double getTotalRevenue() {
        double total = 0.0;
        for (Order order : orders) {
            if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
                total += order.getTotalAmount();
            }
        }
        return total;
    }
    
    public int getAvailableTablesCount() {
        int count = 0;
        for (Table table : tables) {
            if ("available".equals(table.getStatus())) {
                count++;
            }
        }
        return count;
    }
    
    // Data initialization methods
    private void setupDrinks() {
        drinks.clear();
        
        // Coffee
        drinks.add(new Drink("1", "Espresso", 3.50, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("2", "Americano", 3.75, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("3", "Cappuccino", 4.50, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("4", "Latte", 5.00, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("5", "Mocha", 5.25, R.drawable.placeholder_drink, true));
        
        // Tea
        drinks.add(new Drink("6", "Green Tea", 3.00, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("7", "Earl Grey", 3.25, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("8", "Chamomile", 3.25, R.drawable.placeholder_drink, true));
        
        // Cold Drinks
        drinks.add(new Drink("9", "Iced Coffee", 4.25, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("10", "Iced Tea", 3.50, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("11", "Frappuccino", 5.75, R.drawable.placeholder_drink, true));
        
        // Special
        drinks.add(new Drink("12", "Hot Chocolate", 4.75, R.drawable.placeholder_drink, true));
        
        Log.d(TAG, "Initialized " + drinks.size() + " drinks");
    }
    
    private void setupTables() {
        tables.clear();
        tableMap.clear();
        
        // Inside tables (1-12)
        for (int i = 1; i <= 12; i++) {
            int capacity = (i % 3 == 0) ? 6 : (i % 5 == 0) ? 8 : 4;
            Table table = new Table(String.valueOf(i), "Bàn " + i, "available", capacity);
            tables.add(table);
            tableMap.put(table.getNumber(), table);
        }
        
        // Outside tables (13-20)
        for (int i = 1; i <= 8; i++) {
            int tableNumber = i + 12;
            int capacity = (i % 2 == 0) ? 6 : 4;
            Table table = new Table(String.valueOf(tableNumber), "Bàn ngoài " + i, "available", capacity);
            tables.add(table);
            tableMap.put(table.getNumber(), table);
        }
        
        Log.d(TAG, "Initialized " + tables.size() + " tables");
    }
    
    private void setupSampleOrders() {
        orders.clear();
        orderMap.clear();
        
        // Sample Order 1 - Preparing
        Order order1 = new Order("2", "Bàn 2", "Staff A");
        order1.addItem(new OrderItem("1", "Espresso", 3.50, 2, "M", R.drawable.placeholder_drink));
        order1.addItem(new OrderItem("4", "Latte", 5.00, 1, "L", R.drawable.placeholder_drink));
        orders.add(order1);
        orderMap.put(order1.getOrderId(), order1);
        updateTableStatus("2", "occupied");
        
        // Sample Order 2 - Ready
        Order order2 = new Order("5", "Bàn 5", "Staff B");
        order2.addItem(new OrderItem("6", "Green Tea", 3.00, 2, "M", R.drawable.placeholder_drink));
        order2.addItem(new OrderItem("9", "Iced Coffee", 4.25, 1, "L", R.drawable.placeholder_drink));
        order2.updateOrderStatus(Order.OrderStatus.ON_SERVICE);
        orders.add(order2);
        orderMap.put(order2.getOrderId(), order2);
        updateTableStatus("5", "occupied");
        
        // Sample Order 3 - Serving
        Order order3 = new Order("8", "Bàn 8", "Staff C");
        order3.addItem(new OrderItem("3", "Cappuccino", 4.50, 1, "M", R.drawable.placeholder_drink));
        order3.addItem(new OrderItem("12", "Hot Chocolate", 4.75, 2, "L", R.drawable.placeholder_drink));
        order3.updateOrderStatus(Order.OrderStatus.PREPARING);
        orders.add(order3);
        orderMap.put(order3.getOrderId(), order3);
        updateTableStatus("8", "occupied");
        
        Log.d(TAG, "Initialized " + orders.size() + " sample orders");
    }
    
    // Reset method for testing
    public void resetData() {
        orders.clear();
        orderMap.clear();
        orderCounter.set(2100);
        
        // Reset all tables to available
        for (Table table : tables) {
            table.setStatus("available");
        }
        
        setupSampleOrders();
        Log.d(TAG, "Database reset completed");
    }
}