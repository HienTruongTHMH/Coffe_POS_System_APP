package com.midterm.myposapplication;

public class Constants {
    
    // Intent Keys
    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_TABLE_ID = "table_id";
    public static final String KEY_TABLE_NUMBER = "table_number";
    public static final String KEY_PAYMENT_METHOD = "payment_method";
    
    // Order Status Filters
    public static final String FILTER_ALL = "all";
    public static final String FILTER_PREPARING = "preparing";
    public static final String FILTER_ON_SERVICE = "on_service";
    
    // Payment Methods
    public static final String PAYMENT_CASH = "cash";
    public static final String PAYMENT_CARD = "card";
    public static final String PAYMENT_DIGITAL = "digital";
    
    // Database Constants
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_TABLES = "tables";
    public static final String TABLE_PRODUCTS = "products";
    
    // Order ID Prefixes
    public static final String ORDER_PREFIX = "ORD";
    public static final String TABLE_PREFIX = "TBL";
    public static final String USER_PREFIX = "USR";
    
    // Date Format
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy HH:mm";
    public static final String DATE_FORMAT_FILE = "yyyyMMdd_HHmmss";
    
    // Default Values
    public static final int DEFAULT_QUANTITY = 1;
    public static final double DEFAULT_PRICE = 0.0;
    
    // Validation Constants
    public static final int MIN_ORDER_ITEMS = 1;
    public static final int MAX_TABLE_NUMBER = 50;
    public static final int MIN_PASSWORD_LENGTH = 6;
}
