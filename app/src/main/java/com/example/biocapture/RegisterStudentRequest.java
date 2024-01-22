package com.example.biocapture;

import com.google.gson.annotations.SerializedName;

public class RegisterStudentRequest {
    @SerializedName("studentId")
    private String studentId;
    @SerializedName("studentName")
    private String studentName;
    @SerializedName("classId")
    private String classId;
    @SerializedName("status")
    private String status;
    @SerializedName("arrears")
    private double arrears;
    @SerializedName("fingerprint1")
    private String fingerprint1;
    @SerializedName("fingerprint2")
    private String fingerprint2;

    // Constructor
    public RegisterStudentRequest(String studentId, String studentName, String classId, String status, double arrears, String fingerprint1, String fingerprint2) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.classId = classId;
        this.status = status;
        this.arrears = arrears;
        this.fingerprint1 = fingerprint1;
        this.fingerprint2 = fingerprint2;
    }

    // Getters and setters for each field
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getArrears() {
        return arrears;
    }

    public void setArrears(double arrears) {
        this.arrears = arrears;
    }

    public String getFingerprint1() {
        return fingerprint1;
    }

    public void setFingerprint1(String fingerprint1) {
        this.fingerprint1 = fingerprint1;
    }

    public String getFingerprint2() {
        return fingerprint2;
    }

    public void setFingerprint2(String fingerprint2) {
        this.fingerprint2 = fingerprint2;
    }
}