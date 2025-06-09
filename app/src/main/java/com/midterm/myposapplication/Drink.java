package com.midterm.myposapplication; // Thay đổi package name của bạn

public class Drink {
    private String name;
    private double price;
    private int imageResId; // Resource ID của ảnh
    private boolean hasSizes; // Kiểm tra có tùy chọn size M, L hay không

    public Drink(String name, double price, int imageResId, boolean hasSizes) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.hasSizes = hasSizes;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean hasSizes() {
        return hasSizes;
    }
}
