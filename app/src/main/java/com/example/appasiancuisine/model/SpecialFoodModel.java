package com.example.appasiancuisine.model;

public class SpecialFoodModel {
    private final int imageResId;
    private final String name;
    private final String price;
    private final String description;

    public SpecialFoodModel(int imageResId, String name, String price, String description) {
        this.imageResId = imageResId;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public int getImageResId() { return imageResId; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
}

