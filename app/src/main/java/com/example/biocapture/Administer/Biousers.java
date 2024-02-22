package com.example.biocapture.Administer;

import com.google.gson.annotations.SerializedName;

public class Biousers {
    @SerializedName("userId")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("department")
    private String department;

    @SerializedName("pin")
    private String pin;

    @SerializedName("contact")
    private String contact;

    @SerializedName("permissions")
    private int permissions; // Change to int to match the backend

    // Constructor, getters, and setters

    public Biousers(String userId, String name, String department, String pin, String contact, int permissions) {
        this.userId = userId;
        this.name = name;
        this.department = department;
        this.pin = pin;
        this.contact = contact;
        this.permissions = permissions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public enum Permissions {
        @SerializedName("Page1")
        Page1(0),
        @SerializedName("Page2")
        Page2(1),
        @SerializedName("Page3")
        Page3(2),
        @SerializedName("Page4")
        Page4(3);

        private final int value;

        Permissions(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
