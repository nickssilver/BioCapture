package com.example.biocapture;

import com.google.gson.annotations.SerializedName;

public class Biousers {
    @SerializedName("UserId")
    private String userId;
    @SerializedName("Name")
    private String name;
    @SerializedName("Department")
    private String department;
    @SerializedName("Pin")
    private String pin;
    @SerializedName("Contact")
    private String contact;
    @SerializedName("Permissions")
    private Permissions permissions;


    // Getters and setters for each field
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

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public enum Permissions {
        None(0),
        Page1(1),
        Page2(2),
        Page3(4),
        Page4(8);

        private final int value;

        Permissions(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
