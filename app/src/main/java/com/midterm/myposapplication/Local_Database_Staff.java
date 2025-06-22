package com.midterm.myposapplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Local_Database_Staff {
    
    private static Local_Database_Staff instance;
    
    // ✅ Core data storage - only 3 main objects
    private List<Drink> drinks;
    private List<Order> orders;
    private List<Table> tables;
    
    // Order counter for generating order numbers
    private int orderCounter = 2100;
    
    private Local_Database_Staff() {
        initializeData();
    }
    
    public static Local_Database_Staff getInstance() {
        if (instance == null) {
            instance = new Local_Database_Staff();
        }
        return instance;
    }
    
    private void initializeData() {
        setupDrinks();
        setupTables();
        setupOrders();
    }
    
    // ✅ Setup Drinks Data
    private void setupDrinks() {
        drinks = new ArrayList<>();
        
        // Coffee drinks
        drinks.add(new Drink("1", "Espresso", 3.50, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("2", "Americano", 4.00, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("3", "Cappuccino", 4.50, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("4", "Latte", 5.00, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("5", "Macchiato", 5.50, R.drawable.placeholder_drink, true));
        
        // Tea drinks
        drinks.add(new Drink("6", "Green Tea", 3.00, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("7", "Earl Grey", 3.25, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("8", "Chamomile", 3.25, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("9", "Jasmine Tea", 3.50, R.drawable.placeholder_drink, true));
        
        // Cold drinks
        drinks.add(new Drink("10", "Iced Coffee", 4.25, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("11", "Frappuccino", 5.75, R.drawable.placeholder_drink, false));
        drinks.add(new Drink("12", "Iced Tea", 3.75, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("13", "Smoothie", 6.00, R.drawable.placeholder_drink, false));
        
        // More variety
        drinks.add(new Drink("14", "Mocha", 5.25, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("15", "Black Coffee", 2.50, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("16", "Matcha Latte", 5.50, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("17", "Hot Chocolate", 4.75, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("18", "Bubble Tea", 6.25, R.drawable.placeholder_drink, false));
        drinks.add(new Drink("19", "Vietnamese Coffee", 4.50, R.drawable.placeholder_drink, true));
        drinks.add(new Drink("20", "Coconut Water", 3.00, R.drawable.placeholder_drink, false));
    }
    
    // ✅ Setup Tables Data
    private void setupTables() {
        tables = new ArrayList<>();
        
        // Inside tables (1-12)
        tables.add(new Table("1", "Bàn 1", "occupied", 4));
        tables.add(new Table("2", "Bàn 2", "preparing", 4));
        tables.add(new Table("3", "Bàn 3", "available", 4));
        tables.add(new Table("4", "Bàn 4", "available", 4));
        tables.add(new Table("5", "Bàn 5", "occupied", 6));
        tables.add(new Table("6", "Bàn 6", "available", 2));
        tables.add(new Table("7", "Bàn 7", "available", 4));
        tables.add(new Table("8", "Bàn 8", "preparing", 4));
        tables.add(new Table("9", "Bàn 9", "available", 6));
        tables.add(new Table("10", "Bàn 10", "occupied", 8));
        tables.add(new Table("11", "Bàn 11", "available", 2));
        tables.add(new Table("12", "Bàn 12", "available", 6));
        
        // Outside tables (13-20)
        tables.add(new Table("13", "Bàn ngoài 1", "available", 4));
        tables.add(new Table("14", "Bàn ngoài 2", "occupied", 6));
        tables.add(new Table("15", "Bàn ngoài 3", "available", 4));
        tables.add(new Table("16", "Bàn ngoài 4", "preparing", 4));
        tables.add(new Table("17", "Bàn ngoài 5", "available", 2));
        tables.add(new Table("18", "Bàn ngoài 6", "available", 8));
        tables.add(new Table("19", "Bàn ngoài 7", "occupied", 6));
        tables.add(new Table("20", "Bàn ngoài 8", "available", 4));
    }
    
    // ✅ Setup Orders Data
    private void setupOrders() {
        orders = new ArrayList<>();
    
        // Sample Order 1
        Order order1 = new Order("2", "Bàn 2", "Nhân viên A");
        order1.addItem(new OrderItem("1", "Espresso", 3.50, 2, "M", R.drawable.placeholder_drink));
        order1.addItem(new OrderItem("4", "Latte", 5.00, 1, "L", R.drawable.placeholder_drink));
        order1.setOrderNumber("#2101");
        orders.add(order1);
        
        // Sample Order 2
        Order order2 = new Order("5", "Bàn 5", "Nhân viên B");
        order2.addItem(new OrderItem("6", "Green Tea", 3.00, 2, "M", R.drawable.placeholder_drink));
        order2.addItem(new OrderItem("10", "Iced Coffee", 4.25, 1, "L", R.drawable.placeholder_drink));
        order2.updateOrderStatus(Order.OrderStatus.READY);
        order2.setOrderNumber("#2102");
        orders.add(order2);
        
        // Sample Order 3
        Order order3 = new Order("8", "Bàn 8", "Nhân viên C");
        order3.addItem(new OrderItem("3", "Cappuccino", 4.50, 1, "M", R.drawable.placeholder_drink));
        order3.addItem(new OrderItem("17", "Hot Chocolate", 4.75, 2, "L", R.drawable.placeholder_drink));
        order3.updateOrderStatus(Order.OrderStatus.SERVING);
        order3.updatePaymentStatus(Order.PaymentStatus.PROCESSING);
        order3.setOrderNumber("#2103");
        orders.add(order3);
    }
    
    // ✅ Getter methods
    public List<Drink> getAllDrinks() {
        return new ArrayList<>(drinks);
    }
    
    public List<Table> getAllTables() {
        return new ArrayList<>(tables);
    }
    
    public List<Table> getInsideTables() {
        List<Table> insideTables = new ArrayList<>();
        for (Table table : tables) {
            // Inside tables have numbers 1-12
            int tableNum = Integer.parseInt(table.getNumber());
            if (tableNum <= 12) {
                insideTables.add(table);
            }
        }
        return insideTables;
    }
    
    public List<Table> getOutsideTables() {
        List<Table> outsideTables = new ArrayList<>();
        for (Table table : tables) {
            // Outside tables have numbers 13-20
            int tableNum = Integer.parseInt(table.getNumber());
            if (tableNum > 12) {
                outsideTables.add(table);
            }
        }
        return outsideTables;
    }
    
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
    
    // ✅ Search methods
    public Table getTableByNumber(String tableNumber) {
        for (Table table : tables) {
            if (table.getNumber().equals(tableNumber)) {
                return table;
            }
        }
        return null;
    }
    
    public Drink getDrinkById(String drinkId) {
        for (Drink drink : drinks) {
            if (drink.getId().equals(drinkId)) {
                return drink;
            }
        }
        return null;
    }
    
    public Order getOrderByTableNumber(String tableNumber) {
        for (Order order : orders) {
            if (order.getTableNumber().equals(tableNumber)) {
                return order;
            }
        }
        return null;
    }

    // ✅ Updated filter methods
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : orders) {
            if (order.getOrderStatus() == status) {
                filtered.add(order);
            }
        }
        return filtered;
    }

    public List<Order> getOrdersByPaymentStatus(Order.PaymentStatus status) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : orders) {
            if (order.getPaymentStatus() == status) {
                filtered.add(order);
            }
        }
        return filtered;
    }
    
    // ✅ Add method to get orders by table number
    public List<Order> getOrdersByTableNumber(String tableNumber) {
        List<Order> tableOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getTableNumber().equals(tableNumber)) {
                tableOrders.add(order);
            }
        }
        return tableOrders;
    }
    
    // ✅ Add method to update table status 
    public void updateTableStatus(String tableNumber, String newStatus) {
        Table table = getTableByNumber(tableNumber);
        if (table != null) {
            table.setStatus(newStatus);
            android.util.Log.d("TableStatus", "Updated table " + tableNumber + " to " + newStatus);
        }
    }
    
    // ✅ Fix addNewOrder method to update table status
    public String addNewOrder(Order order) {
        orderCounter++;
        String orderNumber = "#" + orderCounter;
        order.setOrderNumber(orderNumber);
        orders.add(order);
        
        // ✅ Update table status to occupied when order is added
        updateTableStatus(order.getTableNumber(), "occupied");
        
        return orderNumber;
    }
    
    public void removeOrder(String tableNumber) {
        orders.removeIf(order -> order.getTableNumber().equals(tableNumber));
        
        // Update table status back to available
        updateTableStatus(tableNumber, "available");
    }
    
    // ✅ Statistics methods
    public int getTotalOrdersCount() {
        return orders.size();
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
    
    public double getTotalRevenue() {
        double total = 0;
        for (Order order : orders) {
            total += order.getTotalAmount();
        }
        return total;
    }
    
    // ✅ Reset data method (for testing)
    public void resetData() {
        orders.clear();
        orderCounter = 2100;
        initializeData();
    }
}