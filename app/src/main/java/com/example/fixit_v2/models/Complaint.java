package com.example.fixit_v2.models;

public class Complaint {
    private int id;
    private int orderId;
    private String description;
    private String photoPath;
    private String status;
    private String createdAt;

    public Complaint(int id, int orderId, String description, String photoPath, String status, String createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.description = description;
        this.photoPath = photoPath;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
