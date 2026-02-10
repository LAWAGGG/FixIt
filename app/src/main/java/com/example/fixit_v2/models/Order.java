package com.example.fixit_v2.models;

import androidx.annotation.NonNull;

public class Order {
    private int id;
    private int userId;
    private int serviceId; // Changed from technicianId and categoryId
    private String address;
    private String orderDate;
    private String status;
    private String notes;
    private String completionImage;
    private String paymentMethod;
    private String paymentStatus;

    public Order(int id, int userId, int serviceId, String address, String orderDate, String status, String notes, String completionImage, String paymentMethod, String paymentStatus) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.address = address;
        this.orderDate = orderDate;
        this.status = status;
        this.notes = notes;
        this.completionImage = completionImage;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCompletionImage() {
        return completionImage;
    }

    public void setCompletionImage(String completionImage) {
        this.completionImage = completionImage;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return "Order ID: " + id + "\nAlamat: " + address + " | Status: " + status + (notes != null ? "\nCatatan: " + notes : "");
    }
}
