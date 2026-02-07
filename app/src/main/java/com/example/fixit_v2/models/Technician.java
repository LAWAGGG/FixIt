package com.example.fixit_v2.models;

import androidx.annotation.NonNull;

public class Technician {
    private int id;
    private int userId;
    private String name;
    private String phoneNumber;
    private double earnings;

    public Technician(int id, int userId, String name, String phoneNumber, double earnings) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.earnings = earnings;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name; // This will be displayed in the ListView/Spinner
    }
}
