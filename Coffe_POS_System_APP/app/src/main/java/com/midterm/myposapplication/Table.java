package com.midterm.myposapplication;

public class Table {
    private String number;
    private String name;
    private String status; // "available", "occupied", "preparing", "reserved"
    private int capacity;
    private long lastOrderTime;

    public Table(String number, String name, String status, int capacity) {
        this.number = number;
        this.name = name;
        this.status = status;
        this.capacity = capacity;
        this.lastOrderTime = System.currentTimeMillis();
    }

    // ✅ Thêm method này
    public boolean isAvailable() {
        return "available".equals(this.status);
    }

    // Existing getters and setters
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public long getLastOrderTime() { return lastOrderTime; }
    public void setLastOrderTime(long lastOrderTime) { this.lastOrderTime = lastOrderTime; }

    public boolean isOccupied() {
        return "occupied".equals(status);
    }

    public boolean isPreparing() {
        return "preparing".equals(status);
    }

    public boolean isReserved() {
        return "reserved".equals(status);
    }
}