package com.example.appasiancuisine.data.dto;

public class OrderDTO {
    private int id;
    private String status;
    private double total;
    private String createdAt; // 🔍 Thay LocalDate -> String

    // Constructor
    public OrderDTO(int id, String status, double total, String createdAt) {
        this.id = id;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
