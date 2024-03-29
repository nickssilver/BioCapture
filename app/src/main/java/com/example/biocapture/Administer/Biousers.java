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

    @SerializedName("registerPermission")
    private boolean registerPermission;

    @SerializedName("verifyPermission")
    private boolean verifyPermission;

    @SerializedName("refactorPermission")
    private boolean refactorPermission;

    @SerializedName("analyticsPermission")
    private boolean analyticsPermission;

    @SerializedName("managementPermission")
    private boolean managementPermission;

    // Constructor
    public Biousers(String userId, String name, String department, String pin, String contact,
                    boolean registerPermission, boolean verifyPermission, boolean refactorPermission,
                    boolean analyticsPermission, boolean managementPermission) {
        this.userId = userId;
        this.name = name;
        this.department = department;
        this.pin = pin;
        this.contact = contact;
        this.registerPermission = registerPermission;
        this.verifyPermission = verifyPermission;
        this.refactorPermission = refactorPermission;
        this.analyticsPermission = analyticsPermission;
        this.managementPermission = managementPermission;
    }

    // Getters and Setters
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

    public boolean isRegisterPermission() {
        return registerPermission;
    }

    public void setRegisterPermission(boolean registerPermission) {
        this.registerPermission = registerPermission;
    }

    public boolean isVerifyPermission() {
        return verifyPermission;
    }

    public void setVerifyPermission(boolean verifyPermission) {
        this.verifyPermission = verifyPermission;
    }

    public boolean isRefactorPermission() {
        return refactorPermission;
    }

    public void setRefactorPermission(boolean refactorPermission) {
        this.refactorPermission = refactorPermission;
    }

    public boolean isAnalyticsPermission() {
        return analyticsPermission;
    }

    public void setAnalyticsPermission(boolean analyticsPermission) {
        this.analyticsPermission = analyticsPermission;
    }

    public boolean isManagementPermission() {
        return managementPermission;
    }

    public void setManagementPermission(boolean managementPermission) {
        this.managementPermission = managementPermission;
    }
}
