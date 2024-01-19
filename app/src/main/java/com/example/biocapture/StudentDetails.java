package com.example.biocapture;

import com.google.gson.annotations.SerializedName;

public class StudentDetails {
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

    // Getters
    public String getStudentId() { return StudentId; }
    public String getStudentName() { return StudentName; }
    public String getClassId() { return ClassId; }
    public String getStatus() { return Status; }
    public double getArrears() { return Arrears; }

    // Setters
    public void setStudentId(String StudentId) { this.StudentId = StudentId; }
    public void setStudentName(String StudentName) { this.StudentName = StudentName; }
    public void setClassId(String ClassId) { this.ClassId = ClassId; }
    public void setStatus(String Status) { this.Status = Status; }
    public void setArrears(double Arrears) { this.Arrears = Arrears; }
}
