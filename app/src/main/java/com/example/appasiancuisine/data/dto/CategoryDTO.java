package com.example.appasiancuisine.data.dto;

public class CategoryDTO {
    private int id;
    private String name;
    private String categoryImg;

    public CategoryDTO() {
    }

    public CategoryDTO(int id, String name, String categoryImg) {
        this.id = id;
        this.name = name;
        this.categoryImg = categoryImg;
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

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }
}
