package com.midterm.myposapplication;

public class Drink {
    private String id;
    private String name;
    private double price;
    private int imageResId;
    private boolean hasSizes;

    public Drink(String id, String name, double price, int imageResId, boolean hasSizes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.hasSizes = hasSizes;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public boolean hasSizes() { return hasSizes; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setHasSizes(boolean hasSizes) { this.hasSizes = hasSizes; }
}