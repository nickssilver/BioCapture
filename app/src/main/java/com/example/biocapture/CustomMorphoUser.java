package com.example.biocapture;

import java.util.Base64;

public class CustomMorphoUser implements Cloneable {
    private String studentId;
    private String studentName;
    private String classId;
    private String status;
    private double arrears;
    private byte[] fingerprint1;
    private byte[] fingerprint2;

    public CustomMorphoUser() {
        this.studentId = studentId;
        this.studentName = studentName;
        this.classId = classId;
        this.status = status;
        this.arrears = arrears;
        this.fingerprint1 = fingerprint1;
        this.fingerprint2 = fingerprint2;
    }
    // Implement the clone method
    @Override
    public CustomMorphoUser clone() {
        try {
            return (CustomMorphoUser) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we're Cloneable
            throw new RuntimeException(e);
        }
    }

    // Getters and setters
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

    public byte[] getFingerprint1() {
        return fingerprint1;
    }

    public void setFingerprint1(byte[] fingerprint1) {
        this.fingerprint1 = fingerprint1;
    }

    public byte[] getFingerprint2() {
        return fingerprint2;
    }

    public void setFingerprint2(byte[] fingerprint2) {
        this.fingerprint2 = fingerprint2;
    }

    // Helper method to encode fingerprint byte arrays to Base64 strings
    public String getEncodedFingerprint1() {
        return Base64.getEncoder().encodeToString(fingerprint1);
    }

    // Helper method to encode fingerprint byte arrays to Base64 strings
    public String getEncodedFingerprint2() {
        return Base64.getEncoder().encodeToString(fingerprint2);
    }
}
