package com.example.biocapture.Administer;

import com.google.gson.annotations.SerializedName;

public class UserLoginRequest {
    @SerializedName("pin")
    private String pin;

    public UserLoginRequest(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
