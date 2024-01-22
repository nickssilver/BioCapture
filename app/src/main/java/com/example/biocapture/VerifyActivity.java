package com.example.biocapture;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
                .baseUrl("http://192.168.6.155:5223/")
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


        // Add a TextWatcher to the editTextFingerScan EditText field
        editTextFingerScan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String fingerprint = editTextFingerScan.getText().toString();


                ConnectivityManager cm = (ConnectivityManager) ContextCompat.getSystemService(VerifyActivity.this, ConnectivityManager.class);
                assert cm != null;
                Network activeNetwork = cm.getActiveNetwork();
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
                boolean isConnected = activeNetwork != null && networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                if (!isConnected) {
                    Toast.makeText(VerifyActivity.this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!fingerprint.isEmpty()) {

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
                                // error case
                                switch (response.code()) {
                                    case 404:
                                        Toast.makeText(VerifyActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 500:
                                        Toast.makeText(VerifyActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(VerifyActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StudentDetails> call, Throwable t) {
                            // Handle the failure
                            Toast.makeText(VerifyActivity.this, "Network failure", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VERIFY_FINGERPRINT && resultCode == RESULT_OK) {
            // Retrieve fingerprint data from the intent extras
            byte[] fingerprints = (byte[]) data.getSerializableExtra(FpSensorActivity.VERIFYFINGER);
            // Convert the fingerprint data to a Base64 string and set it in the EditText field
            String fingerprint = Base64.encodeToString(fingerprints, Base64.DEFAULT);

            editTextFingerScan.setText(new String(fingerprint));
        }
    }
    private static final int REQUEST_CODE_VERIFY_FINGERPRINT = 456;
}
