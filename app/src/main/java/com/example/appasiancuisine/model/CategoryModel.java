package com.example.appasiancuisine.model;

public class CategoryModel {
    private int imageResId;
    private String title;

    public CategoryModel(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }
}
