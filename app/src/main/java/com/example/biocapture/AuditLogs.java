package com.example.biocapture;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AuditLogs {
    @SerializedName("studentId")
    private String studentId;

    @SerializedName("studentName")
    private String studentName;

    @SerializedName("verificationTimestamp")
    private String verificationTimestamp;

    public AuditLogs(String studentId, String studentName, Date verificationTimestamp) {
        this.studentId = studentId;
        this.studentName = studentName;

        // Adjust the date format based on the expected format by the server
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.verificationTimestamp = sdf.format(verificationTimestamp);
    }

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

    public String getVerificationTimestamp() {
        return verificationTimestamp;
    }

    public void setVerificationTimestamp(String verificationTimestamp) {
        this.verificationTimestamp = verificationTimestamp;
    }
}
