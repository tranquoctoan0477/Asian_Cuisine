package com.example.appasiancuisine.data.dto;

public class ProductDTO {
    private int id;
    private String name;
    private String description;
    private double price;
    private String mainImg;
    private int categoryId;
    private String createdAt;
    private String voucherCode;

    public ProductDTO() {
    }

    public ProductDTO(int id, String name, String description, double price, String mainImg, int categoryId, String createdAt, String voucherCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.mainImg = mainImg;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.voucherCode = voucherCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
