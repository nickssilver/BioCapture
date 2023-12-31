package com.example.biocapture;

import com.google.gson.annotations.SerializedName;

public class FetchStudentData {
    @SerializedName("admnNo")
    private String AdmnNo; // StudentId
    @SerializedName("names")
    private String names; // StudentName
    @SerializedName("class")
    private String Class; // ClassId
    @SerializedName("studStatus")
    private String StudStatus; // Status
    @SerializedName("arrears")
    private double Arrears; // Arrears

    // Getters and setters for each field...

    public String getAdminNo() {
        return AdmnNo;
    }

    public void setAdmnNo(String AdmnNo) {
        this.AdmnNo = AdmnNo;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getTheClass() {
        return Class;
    }

    public void setTheClass(String Class) {
        this.Class = Class;
    }

    public String getStudStatus() {
        return StudStatus;
    }

    public void setStudStatus(String StudStatus) {
        this.StudStatus = StudStatus;
    }

    public double getArrears() {
        return Arrears;
    }

    public void setArrears(double Arrears) {
        this.Arrears = Arrears;
    }
}
