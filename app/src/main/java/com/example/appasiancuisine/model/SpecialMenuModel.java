package com.example.appasiancuisine.model;

import java.util.List;

public class SpecialMenuModel {
    private int imageResId;          // Ảnh đại diện danh mục
    private String title;            // Tên danh mục
    private List<String> foodItems;  // Danh sách tên món

    public SpecialMenuModel(int imageResId, String title, List<String> foodItems) {
        this.imageResId = imageResId;
        this.title = title;
        this.foodItems = foodItems;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getFoodItems() {
        return foodItems;
    }
}
