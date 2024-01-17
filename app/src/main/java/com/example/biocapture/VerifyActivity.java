package com.example.biocapture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerifyActivity extends BaseActivity {

        EditText editTextFingerScan, editTextStudentDetails, editTextArrears;
        Button buttonSave;
        ApiService apiService;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_verify);

            // Initialize the EditText fields
            editTextFingerScan = findViewById(R.id.editTextFingerScan);
            editTextStudentDetails = findViewById(R.id.editTextStudentDetails);
            editTextArrears = findViewById(R.id.editTextArrears);
            buttonSave = findViewById(R.id.buttonSave);

            // Set up the Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http:192.168.16.100:5223/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);

            // Set up the EditText for fingerprint scanning
            editTextFingerScan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // Start FpSensorActivityV to capture fingerprint
                        Intent intent = new Intent(VerifyActivity.this, FpSensorActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_VERIFY_FINGERPRINT);
                    }
                }
            });
            byte[] fingerprints = getIntent().getByteArrayExtra(FpSensorActivity.EXTRA_FINGERPRINTS);

            // Set up the Save button
            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fingerprint = editTextFingerScan.getText().toString();
                    Call<StudentDetails> call = apiService.VerifyFingerprint(fingerprint);
                    call.enqueue(new Callback<StudentDetails>() {
                        @Override
                        public void onResponse(Call<StudentDetails> call, Response<StudentDetails> response) {
                            if (response.isSuccessful()) {
                                StudentDetails student = response.body();
                                // Display the student details
                                editTextStudentDetails.setText(student.getStudentId() + ", " + student.getStudentName() + ", " + student.getClassId() + ", " + student.getStatus());
                                editTextArrears.setText(Double.toString(student.getArrears()));

                            } else {
                                // Display an error message
                                Toast.makeText(VerifyActivity.this, "Record not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<StudentDetails> call, Throwable t) {
                            // Handle the failure
                        }
                    });
                }
            });

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE_VERIFY_FINGERPRINT && resultCode == RESULT_OK) {
                // Fingerprint data is captured successfully
                // Retrieve fingerprint data from the intent extras
                byte[][] fingerprints = (byte[][]) data.getSerializableExtra(FpSensorActivity.EXTRA_FINGERPRINTS);

                // Convert the fingerprint data to a Base64 string and set it in the EditText field
                String fingerprint = Base64.encodeToString(fingerprints[0], Base64.DEFAULT);
                editTextFingerScan.setText(fingerprint);
            }
        }
            private static final int REQUEST_CODE_VERIFY_FINGERPRINT = 123;
    }
