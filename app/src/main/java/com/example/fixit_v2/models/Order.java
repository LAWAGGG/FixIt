package com.example.fixit_v2.models;

import androidx.annotation.NonNull;

public class Order {
    private int id;
    private int userId;
    private int serviceId; // Changed from technicianId and categoryId
    private String address;
    private String orderDate;
    private String status;

    public Order(int id, int userId, int serviceId, String address, String orderDate, String status) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.address = address;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        // This needs to be updated to show service name, which requires another DB query.
        // For now, we'll keep it simple.
        return "Order ID: " + id + "\nAlamat: " + address + " | Status: " + status;
    }
}
