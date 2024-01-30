package com.example.biocapture;

import com.google.gson.annotations.SerializedName;

public class FingerprintTemplate {

    @SerializedName("studentId")
    private String StudentId;
    @SerializedName("studentName")
    private String StudentName;
    @SerializedName("classId")
    private String ClassId;
    @SerializedName("status")
    private String Status;
    @SerializedName("arrears")
    private double Arrears;
    @SerializedName("fingerprint1")
    private String Fingerprint1;
    @SerializedName("fingerprint2")
    private String Fingerprint2;

    // Getters
    public String getStudentId() { return StudentId; }
    public String getStudentName() { return StudentName; }
    public String getClassId() { return ClassId; }
    public String getStatus() { return Status; }
    public double getArrears() { return Arrears; }
    public String getFingerprint1() { return Fingerprint1; }
    public String getFingerprint2() { return Fingerprint2; }

    // Setters
    public void setStudentId(String StudentId) { this.StudentId = StudentId; }
    public void setStudentName(String StudentName) { this.StudentName = StudentName; }
    public void setClassId(String ClassId) { this.ClassId = ClassId; }
    public void setStatus(String Status) { this.Status = Status; }
    public void setArrears(double Arrears) { this.Arrears = Arrears; }

    public void setFingerprint1(String Fingerprint1) { this.Fingerprint1 = Fingerprint1; }
    public void setFingerprint2(String Fingerprint2) { this.Fingerprint2 = Fingerprint2; }
}
