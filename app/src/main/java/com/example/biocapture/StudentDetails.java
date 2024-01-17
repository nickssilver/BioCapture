package com.example.biocapture;

public class StudentDetails {
    private String StudentId;
    private String StudentName;
    private String ClassId;
    private String Status;
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
