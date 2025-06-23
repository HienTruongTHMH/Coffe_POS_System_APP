package com.midterm.myposapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    
    private static final AtomicInteger orderCounter = new AtomicInteger(1);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
    
    /**
     * Generate order ID with pattern: ORD_YYYYMMDD_XXXX
     * Example: ORD_20231223_0001
     */
    public static String generateOrderId() {
        String dateStr = dateFormat.format(new Date());
        int counter = orderCounter.getAndIncrement();
        return String.format(Locale.US, "%s_%s_%04d", 
            Constants.ORDER_PREFIX, dateStr, counter);
    }
    
    /**
     * Generate order number for display: YYYYMMDDXXXX
     * Example: 202312230001
     */
    public static String generateOrderNumber() {
        String dateStr = dateFormat.format(new Date());
        int counter = orderCounter.get();
        return String.format(Locale.US, "%s%04d", dateStr, counter);
    }
    
    /**
     * Generate table ID: TBL_XX
     */
    public static String generateTableId(String tableNumber) {
        return String.format(Locale.US, "%s_%s", Constants.TABLE_PREFIX, tableNumber);
    }
    
    /**
     * Generate user ID: USR_timestamp
     */
    public static String generateUserId() {
        return String.format(Locale.US, "%s_%d", Constants.USER_PREFIX, System.currentTimeMillis());
    }
}