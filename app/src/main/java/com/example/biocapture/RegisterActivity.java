package com.example.biocapture;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.morpho.morphosmart.sdk.MorphoImage;

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

public class RegisterActivity extends BaseActivity implements MorphoSmartFingerprintCapture.CaptureObserver {

    EditText studentIdEditText, studentNameEditText, classIdEditText, statusEditText, arrearsEditText, fingerPrint1EditText, fingerPrint2EditText;
    Button captureButton, submitButton;
    ApiService apiService;

    MorphoSmartFingerprintCapture morphoSmartFingerprintCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        studentIdEditText = findViewById(R.id.editTextStudentId);
        studentNameEditText = findViewById(R.id.editTextStudentName);
        classIdEditText = findViewById(R.id.editTextClassId);
        statusEditText = findViewById(R.id.editTextStatus);
        arrearsEditText = findViewById(R.id.editTextArrears);
        fingerPrint1EditText = findViewById(R.id.fingerPrint1);
        fingerPrint2EditText = findViewById(R.id.fingerPrint2);
        captureButton = findViewById(R.id.captureButton);
        submitButton = findViewById(R.id.buttonSubmitReg);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.88.192:5223/")
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
                        public void onResponse(Call<FetchStudentData> call, Response<FetchStudentData> response) {
                            if (response.isSuccessful()) {
                                FetchStudentData studentData = response.body();
                                // Use the student data to fill the other fields
                                studentNameEditText.setText(studentData.getNames());
                                classIdEditText.setText(studentData.getTheClass());
                                statusEditText.setText(studentData.getStudStatus());
                                arrearsEditText.setText(String.valueOf(studentData.getArrears()));
                            }
                        }

                        @Override
                        public void onFailure(Call<FetchStudentData> call, Throwable t) {
                            // Handle the failure
                        }
                    });
                }
            }
        });
        // Create a counter to track which finger is being captured
        int fingerCounter = 0;

        morphoSmartFingerprintCapture = new MorphoSmartFingerprintCapture(this);
        morphoSmartFingerprintCapture.registerObserver(this);


        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morphoSmartFingerprintCapture.captureTwoFingerprints();
            }
        });







        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check network connection
                ConnectivityManager cm = (ConnectivityManager) ContextCompat.getSystemService(RegisterActivity.this, ConnectivityManager.class);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (!isConnected) {
                    Toast.makeText(RegisterActivity.this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (studentIdEditText.getText().toString().isEmpty() ||
                        studentNameEditText.getText().toString().isEmpty() ||
                        classIdEditText.getText().toString().isEmpty() ||
                        statusEditText.getText().toString().isEmpty() ||
                        arrearsEditText.getText().toString().isEmpty() ||
                        fingerPrint1EditText.getText().toString().isEmpty() ||
                        fingerPrint2EditText.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter all of the required fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String studentId = studentIdEditText.getText().toString();
                String studentName = studentNameEditText.getText().toString();
                String classId = classIdEditText.getText().toString();
                String status = statusEditText.getText().toString();
                double arrears = Double.parseDouble(arrearsEditText.getText().toString());
                byte[] fingerprint1byte = fingerPrint1EditText.getText().toString().getBytes();
                byte[] fingerprint2byte = fingerPrint2EditText.getText().toString().getBytes();
                String fingerprint1 = Base64.encodeToString(fingerprint1byte, Base64.DEFAULT);
                String fingerprint2 = Base64.encodeToString(fingerprint2byte, Base64.DEFAULT);

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
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
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
                });
            }
        });}
    @Override
    public void onCaptureStart() {
        // This method is intentionally left blank
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        morphoSmartFingerprintCapture.closeDevice();
    }
    @Override
    public void onCaptureComplete(MorphoImage[] fingerprints) {
        fingerPrint1EditText.setText(fingerprints[0].toString());
        fingerPrint2EditText.setText(fingerprints[1].toString());
    }

    @Override
    public void onCaptureFailure(Exception e) {
        Toast.makeText(this, "Fingerprint capture failed. Please try again.", Toast.LENGTH_SHORT).show();
    }
    }


