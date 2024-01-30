package com.example.biocapture;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class VerifyActivity extends BaseActivity {

    EditText editTextFingerScan, editTextStudentDetails, editTextArrears;
    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        // Initialize the EditText fields
        editTextFingerScan = findViewById(R.id.editTextFingerScan);
        editTextStudentDetails = findViewById(R.id.editTextStudentDetails);
        editTextArrears = findViewById(R.id.editTextArrears);
        buttonSave = findViewById(R.id.buttonSave);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String studentId = intent.getStringExtra("studentId");
        String studentName = intent.getStringExtra("studentName");
        double arrears = intent.getDoubleExtra("arrears", 0.0);
        String classId = intent.getStringExtra("classId");
        String status = intent.getStringExtra("status");

        // Display data in EditText fields
        editTextFingerScan.setText("Fingerprint verified");
        //editTextStudentDetails.setText("\nID: " + studentId + "\n\nName: " + studentName + "\n\nClass ID: " + classId + "\n\nStatus: " + status);
        editTextStudentDetails.setText(Html.fromHtml("ID: " + studentId + "<br>Name: " + studentName + "<br><br>Class ID: " + classId + "<br>Status: " + status));

        editTextArrears.setText("Arrears: " + arrears);

        // Set up the Save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the fields
                editTextFingerScan.setText("");
                editTextStudentDetails.setText("");
                editTextArrears.setText("");
            }
        });

        // Set OnClickListener for editTextFingerScan
        editTextFingerScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open FpSensorActivity when editTextFingerScan is clicked
                Intent fpSensorIntent = new Intent(VerifyActivity.this, FpSensorActivity.class);
                startActivity(fpSensorIntent);
            }
        });
    }
}
