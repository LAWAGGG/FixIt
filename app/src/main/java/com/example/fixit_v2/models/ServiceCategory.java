package com.example.fixit_v2.models;

import androidx.annotation.NonNull;

public class ServiceCategory {
    private int id;
    private String categoryName;
    private String imagePath;

    public ServiceCategory(int id, String categoryName, String imagePath) {
        this.id = id;
        this.categoryName = categoryName;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @NonNull
    @Override
    public String toString() {
        return this.categoryName;
    }
}
