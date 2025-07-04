package com.midterm.myposapplication;

public class OrderItem {
    private String drinkId;
    private String drinkName;
    private double price;
    private int quantity;
    private String size;
    private int imageResId;

    public OrderItem(String drinkId, String drinkName, double price, int quantity, String size, int imageResId) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.imageResId = imageResId;
    }

    // Calculations
    public double getTotalPrice() {
        return price * quantity;
    }

    public String getFormattedUnitPrice() {
        return String.format("$ %.2f", price);
    }

    public String getFormattedTotalPrice() {
        return String.format("$ %.2f", getTotalPrice());
    }

    public String getDisplayName() {
        return drinkName + " (" + size + ")";
    }

    // Getters and Setters
    public String getDrinkId() { return drinkId; }
    public String getDrinkName() { return drinkName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSize() { return size; }
    public int getImageResId() { return imageResId; }
}