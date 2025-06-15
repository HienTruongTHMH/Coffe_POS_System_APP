package com.midterm.myposapplication;

public class PaymentItem {
    private String itemId;
    private String itemName;
    private String size;
    private String sugarLevel;
    private String iceLevel;
    private int quantity;
    private double price;
    private int imageResId; // ✅ Consistent naming

    // ✅ Constructor following existing pattern
    public PaymentItem(String itemId, String itemName, String size, String sugarLevel, String iceLevel, int quantity, double price, int imageResId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.size = size;
        this.sugarLevel = sugarLevel;
        this.iceLevel = iceLevel;
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getSugarLevel() { return sugarLevel; }
    public void setSugarLevel(String sugarLevel) { this.sugarLevel = sugarLevel; }

    public String getIceLevel() { return iceLevel; }
    public void setIceLevel(String iceLevel) { this.iceLevel = iceLevel; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getImageResId() { return imageResId; } // ✅ Consistent method name
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    // Helper method
    public double getTotalPrice() {
        return price * quantity;
    }
}
