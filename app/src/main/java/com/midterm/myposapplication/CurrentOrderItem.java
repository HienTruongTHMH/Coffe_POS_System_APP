package com.midterm.myposapplication;

public class CurrentOrderItem {
    private String drinkId;
    private String drinkName;
    private double price;
    private int quantity;
    private String size;
    private int imageResId; // ✅ Changed to match Drink and OrderItem

    // ✅ Keep existing constructor signature but fix field name
    public CurrentOrderItem(String drinkId, String drinkName, double price, int quantity, String size, int imageResourceId) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.imageResId = imageResourceId; // ✅ Map parameter to correct field
    }

    // Getters and Setters
    public String getDrinkId() { return drinkId; }
    public void setDrinkId(String drinkId) { this.drinkId = drinkId; }

    public String getDrinkName() { return drinkName; }
    public void setDrinkName(String drinkName) { this.drinkName = drinkName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    // ✅ Standardize method name to match Drink
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    // ✅ Keep backward compatibility method
    @Deprecated
    public int getImageResourceId() { return imageResId; }
    @Deprecated
    public void setImageResourceId(int imageResourceId) { this.imageResId = imageResourceId; }

    // Helper method
    public double getTotalPrice() {
        return price * quantity;
    }
}
