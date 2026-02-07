package com.example.fixit_v2.models;

public class Payment {
    private int id;
    private int orderId;
    private String method;
    private double amount;

    public Payment(int id, int orderId, String method, double amount) {
        this.id = id;
        this.orderId = orderId;
        this.method = method;
        this.amount = amount;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
