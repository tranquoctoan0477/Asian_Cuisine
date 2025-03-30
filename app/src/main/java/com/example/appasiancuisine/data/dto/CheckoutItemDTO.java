package com.example.appasiancuisine.data.dto;

import java.io.Serializable;

public class CheckoutItemDTO implements Serializable {
    private long productId;
    private String productName;
    private int quantity;
    private String note;
    private double price; // ✅ Giá mỗi món
    private String thumbnail; // ✅ Link ảnh đại diện món ăn (nếu có)

    public CheckoutItemDTO(long productId, String productName, int quantity, String note, double price, String thumbnail) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.note = note;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNote() {
        return note;
    }

    public double getPrice() {
        return price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    // Nếu sau này cần sửa đổi giá trị (setter)
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
