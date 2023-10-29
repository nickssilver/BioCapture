package com.example.biocapture;

public class FetchStudentData {
    private String AdmnNo; // StudentId
    private String names; // StudentName
    private String Class; // ClassId
    private String StudStatus; // Status
    private double Arrears; // Arrears

    // Getters and setters for each field...

    public String getAdmnNo() {
        return AdmnNo;
    }

    public void setAdmnNo(String admnNo) {
        AdmnNo = admnNo;
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

    public void setTheClass(String aClass) {
        Class = aClass;
    }

    public String getStudStatus() {
        return StudStatus;
    }

    public void setStudStatus(String studStatus) {
        StudStatus = studStatus;
    }

    public double getArrears() {
        return Arrears;
    }

    public void setArrears(double arrears) {
        Arrears = arrears;
    }
}
