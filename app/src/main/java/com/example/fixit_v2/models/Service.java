package com.example.fixit_v2.models;

import androidx.annotation.NonNull;

import java.text.NumberFormat;
import java.util.Locale;

public class Service {
    private int id;
    private String serviceName;
    private String description; // New field
    private double price;
    private int technicianId;
    private int categoryId;
    private String imagePath;

    public Service(int id, String serviceName, String description, double price, int technicianId, int categoryId, String imagePath) {
        this.id = id;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.technicianId = technicianId;
        this.categoryId = categoryId;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public int getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(int technicianId) {
        this.technicianId = technicianId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(0);
        return serviceName + " - " + format.format(price);
    }
}
