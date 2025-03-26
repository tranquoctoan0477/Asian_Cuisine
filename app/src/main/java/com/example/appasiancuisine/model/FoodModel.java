package com.example.appasiancuisine.model;

public class FoodModel {
    private int imageResId;     // Resource ID ảnh món ăn
    private String name;        // Tên món
    private double price;       // Giá
    private double rating;      // Đánh giá sao

    public FoodModel(int imageResId, String name, double price, double rating) {
        this.imageResId = imageResId;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }
}
