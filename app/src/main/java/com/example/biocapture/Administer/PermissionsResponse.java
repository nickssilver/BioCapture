package com.example.biocapture.Administer;

import com.google.gson.annotations.SerializedName;

public class PermissionsResponse {
    @SerializedName("userId")
    private String userId;
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

    // Constructors
    public PermissionsResponse(String userId, boolean registerPermission, boolean verifyPermission, boolean refactorPermission,
                               boolean analyticsPermission, boolean managementPermission) {
        this.userId = userId;
        this.registerPermission = registerPermission;
        this.verifyPermission = verifyPermission;
        this.refactorPermission = refactorPermission;
        this.analyticsPermission = analyticsPermission;
        this.managementPermission = managementPermission;
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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