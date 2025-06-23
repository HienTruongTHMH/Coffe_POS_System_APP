package com.midterm.myposapplication.utils;

import android.content.Context;
import com.midterm.myposapplication.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    
    private static final SimpleDateFormat displayDateFormat = 
        new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.US);
    
    /**
     * Format price with currency symbol
     */
    public static String formatPrice(Context context, double price) {
        return context.getString(R.string.price_format, price);
    }
    
    /**
     * Format order number for display
     */
    public static String formatOrderNumber(Context context, String orderNumber) {
        return context.getString(R.string.order_number_format, orderNumber);
    }
    
    /**
     * Format table number
     */
    public static String formatTableNumber(Context context, String tableNumber) {
        return context.getString(R.string.table_format, tableNumber);
    }
    
    /**
     * Format quantity
     */
    public static String formatQuantity(Context context, int quantity) {
        return context.getString(R.string.quantity_format, quantity);
    }
    
    /**
     * Format timestamp for display
     */
    public static String formatDateTime(long timestamp) {
        return displayDateFormat.format(new Date(timestamp));
    }
    
    /**
     * Format order time
     */
    public static String formatOrderTime(Context context, long timestamp) {
        return context.getString(R.string.order_time_format, formatDateTime(timestamp));
    }
    
    /**
     * Format total amount
     */
    public static String formatTotalAmount(Context context, double amount) {
        return context.getString(R.string.total_amount_format, amount);
    }
}