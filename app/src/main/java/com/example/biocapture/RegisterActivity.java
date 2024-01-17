package com.example.biocapture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class RegisterActivity extends BaseActivity {
    EditText studentIdEditText, studentNameEditText, classIdEditText, statusEditText, arrearsEditText, editTextFingerprint1, editTextFingerprint2;
    Button captureButton, submitButton;
    ApiService apiService;

    private int counter = 0;
    byte[][] fingerprints;
    int[] captureBitmapIds = {R.id.fingerPrint1, R.id.fingerPrint2};
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the EditText fields
        studentIdEditText = findViewById(R.id.editTextStudentId);
        studentNameEditText = findViewById(R.id.editTextStudentName);
        classIdEditText = findViewById(R.id.editTextClassId);
        statusEditText = findViewById(R.id.editTextStatus);
        arrearsEditText = findViewById(R.id.editTextArrears);
        editTextFingerprint1 = findViewById(R.id.fingerPrint1);
        editTextFingerprint2 = findViewById(R.id.fingerPrint2);

        captureButton = findViewById(R.id.captureButton);
        submitButton = findViewById(R.id.buttonSubmitReg);



        // Set up the capture button
        captureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Increment the counter
                counter++;
                // Disable the submit button
                submitButton.setEnabled(false);
                // Start FpSensorActivity to capture fingerprints
                Intent intent = new Intent(RegisterActivity.this, FpSensorActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_FINGERPRINTS);
            }
        });

        // Set up the Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http:192.168.16.91:5223/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Add a TextWatcher to the studentIdEditText field
        studentIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is intentionally left blank
            }


            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    // Clear all of the fields
                    studentNameEditText.setText("");
                    classIdEditText.setText("");
                    statusEditText.setText("");
                    arrearsEditText.setText("");

                    Call<FetchStudentData> call = apiService.FetchStudentData(s.toString());
                    call.enqueue(new Callback<FetchStudentData>() {
                        @Override
                        public void onResponse(@NonNull Call<FetchStudentData> call, @NonNull Response<FetchStudentData> response) {
                            if (response.isSuccessful()) {
                                FetchStudentData studentData = response.body();
                                // Use the student data to fill the other fields
                                assert studentData != null;
                                studentNameEditText.setText(studentData.getNames());
                                classIdEditText.setText(studentData.getTheClass());
                                statusEditText.setText(studentData.getStudStatus());
                                arrearsEditText.setText(String.valueOf(studentData.getArrears()));

                            } else {
                                // Parse error message from response body
                                String errorMessage = "";
                                try {
                                    errorMessage = response.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                int statusCode = response.code();
                                if (statusCode == 404) {
                                    Toast.makeText(RegisterActivity.this, "Student ID not found", Toast.LENGTH_SHORT).show();
                                } else if (statusCode == 409) {
                                    Toast.makeText(RegisterActivity.this, "Student is already registered", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "An error occurred: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<FetchStudentData> call, @NonNull Throwable t) {
                            // Handle the failure
                        }
                    });
                }
            }
        });

        editTextFingerprint1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Enable the submit button if the EditText fields are not empty
                submitButton.setEnabled(!areFieldsEmpty());
            }
        });

        editTextFingerprint2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Enable the submit button if the EditText fields are not empty
                submitButton.setEnabled(!areFieldsEmpty());
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check network connection
                ConnectivityManager cm = (ConnectivityManager) ContextCompat.getSystemService(RegisterActivity.this, ConnectivityManager.class);
                assert cm != null;
                Network activeNetwork = cm.getActiveNetwork();
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
                boolean isConnected = activeNetwork != null && networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                if (!isConnected) {
                    Toast.makeText(RegisterActivity.this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (areFieldsEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter all of the required fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String studentId = studentIdEditText.getText().toString();
                String studentName = studentNameEditText.getText().toString();
                String classId = classIdEditText.getText().toString();
                String status = statusEditText.getText().toString();
                double arrears = Double.parseDouble(arrearsEditText.getText().toString());

                String fingerprint1 = fingerprints != null && fingerprints.length > 0 ? Base64.encodeToString(fingerprints[0], Base64.DEFAULT) : "";
                String fingerprint2 = fingerprints != null && fingerprints.length > 1 ? Base64.encodeToString(fingerprints[1], Base64.DEFAULT) : "";



                RegisterStudentRequest request = new RegisterStudentRequest(
                        studentId,
                        studentName,
                        classId,
                        status,
                        arrears,
                        fingerprint1,
                        fingerprint2
                );

                Call<Void> call = apiService.registerStudent(request);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            // Reset the EditText fields
                            studentIdEditText.setText("");
                            studentNameEditText.setText("");
                            classIdEditText.setText("");
                            statusEditText.setText("");
                            arrearsEditText.setText("");
                            editTextFingerprint1.setText("");
                            editTextFingerprint2.setText("");

                            // Reset the counter
                            counter = 0;


                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        handleRegistrationFailure(t);
                    }
                });
            }
        });
        submitButton.setEnabled(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAPTURE_FINGERPRINTS && resultCode == RESULT_OK) {
            // Decrement the counter
            counter--;
            // Check if all operations are complete
            if (counter == 0) {
                // All operations are complete
                // Enable the submit button
                submitButton.setEnabled(true);
            }
            // Fingerprint data is captured successfully
            // Retrieve fingerprint data from the intent extras

            byte[][] fingerprints = (byte[][]) data.getSerializableExtra(FpSensorActivity.EXTRA_FINGERPRINTS);


            // Convert the fingerprints to strings and set them as the text of the EditText fields
           /*String fingerprint1 = Base64.encodeToString(fingerprints[0], Base64.DEFAULT);
            String fingerprint2 = Base64.encodeToString(fingerprints[1], Base64.DEFAULT);

            editTextFingerprint1.setText(fingerprint1);
            editTextFingerprint2.setText(fingerprint2);*/

            // Set the raw fingerprint data as the text of the EditText fields
            editTextFingerprint1.setText(new String(fingerprints[0]));
            editTextFingerprint2.setText(new String(fingerprints[1]));

        }
    }


    // Extracted method to check if any required fields are empty
    private boolean areFieldsEmpty() {
        return studentIdEditText.getText().toString().isEmpty() ||
                studentNameEditText.getText().toString().isEmpty() ||
                classIdEditText.getText().toString().isEmpty() ||
                statusEditText.getText().toString().isEmpty() ||
                arrearsEditText.getText().toString().isEmpty() ||
                editTextFingerprint1.getText().toString().isEmpty() ||
                editTextFingerprint2.getText().toString().isEmpty();
    }


    // Extracted method to handle different registration failure scenarios
    private void handleRegistrationFailure(Throwable t) {
        Log.e("RegisterActivity", "Network error", t);
        if (t instanceof SocketTimeoutException) {
            Toast.makeText(RegisterActivity.this, "Request timed out. Please check your network connection and try again.", Toast.LENGTH_SHORT).show();
        } else if (t instanceof HttpException) {
            Response<?> response = ((HttpException) t).response();
            if (response != null) {
                switch (response.code()) {
                    case 400:
                        Toast.makeText(RegisterActivity.this, "Bad Request. Please check your input and try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        Toast.makeText(RegisterActivity.this, "Resource not found. Please try again later.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(RegisterActivity.this, "An HTTP error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        } else if (t instanceof UnknownHostException) {
            Toast.makeText(RegisterActivity.this, "The server could not be reached. Please check your network connection and try again.", Toast.LENGTH_SHORT).show();
        } else if (t instanceof IOException) {
            Toast.makeText(RegisterActivity.this, "A network error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterActivity.this, "An unexpected error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int REQUEST_CODE_CAPTURE_FINGERPRINTS = 123;
}


