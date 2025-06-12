package com.midterm.myposapplication;

public class CurrentOrderItem {
    private String drinkId;
    private String drinkName;
    private double price;
    private int quantity;
    private String size;
    private int imageResourceId;

    // ✅ Constructor với 6 parameters
    public CurrentOrderItem(String drinkId, String drinkName, double price, int quantity, String size, int imageResourceId) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.imageResourceId = imageResourceId;
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

    public int getImageResourceId() { return imageResourceId; }
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }

    // Helper method
    public double getTotalPrice() {
        return price * quantity;
    }
}
