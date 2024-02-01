package com.example.biocapture;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.Date;

import api.ApiService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerifyActivity extends BaseActivity {

    EditText editTextStudentDetails, editTextArrears;
    Button buttonSave, buttonFingerScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        // Initialize the EditText fields

        buttonFingerScan = findViewById(R.id.buttonFingerScan);
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
        editTextStudentDetails.setText(Html.fromHtml("{ID:} " + studentId + "<br>{Name:} " + studentName + "<br><br>{Class ID:} " + classId + "<br>{Status:} " + status));
        editTextArrears.setText("Arrears: " + arrears);

        // Set up the Save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the fields
                editTextStudentDetails.setText("");
                editTextArrears.setText("");

                // Create an instance of AuditLogs
                AuditLogs log = new AuditLogs(studentId, studentName, new Date());


                // Send the JSON to your server using a POST request
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.0.208:83/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);
                Call<ResponseBody> call = apiService.postAuditLog(log);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "Failed to post audit log. Response code: " + response.code());
                            try {
                                Log.e(TAG, "Error body: " + response.errorBody().string());
                            } catch (IOException e) {
                                Log.e(TAG, "Failed to read response body");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Failed to post audit log. Exception: ", t);
                    }
                });


            }
        });

        // Set OnClickListener for editTextFingerScan
        buttonFingerScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open FpSensorActivity when editTextFingerScan is clicked
                Intent fpSensorIntent = new Intent(VerifyActivity.this, FpSensorActivity.class);
                startActivity(fpSensorIntent);
            }
        });
    }
}
