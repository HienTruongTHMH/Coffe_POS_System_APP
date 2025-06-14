package com.midterm.myposapplication;

public class OrderItem {
    private String drinkId;
    private String drinkName;
    private double price;
    private int quantity;
    private String size; // "S", "M", "L"
    private int imageResId;

    public OrderItem(String drinkId, String drinkName, double price, int quantity, String size, int imageResId) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.imageResId = imageResId;
    }

    public OrderItem(String cappuccino, double priceCappuccino, int quantity1) {

    }

    // Tính tổng giá trị cho một món
    public double getTotalPrice() {
        return price * quantity;
    }

    // Các getter và setter
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

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
