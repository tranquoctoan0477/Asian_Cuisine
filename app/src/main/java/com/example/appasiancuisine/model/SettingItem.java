package com.example.appasiancuisine.model;

public class SettingItem {
    private int id; // 👈 Thêm id
    private int iconResId;
    private String title;

    public SettingItem(int id, int iconResId, String title) {
        this.id = id;
        this.iconResId = iconResId;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }
}
