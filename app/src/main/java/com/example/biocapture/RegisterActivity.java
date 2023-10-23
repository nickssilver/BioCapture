package com.example.biocapture;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.util.Log;

public class RegisterActivity extends BaseActivity {

    EditText studentNumberEditText, studentNameEditText, courseEditText, departmentEditText, statusEditText, classEditText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI elements
        studentNumberEditText = findViewById(R.id.editTextStudentNo);
        studentNameEditText = findViewById(R.id.editTextStudentName);
        courseEditText = findViewById(R.id.editTextCourse);
        departmentEditText = findViewById(R.id.editTextDepartment);
        statusEditText = findViewById(R.id.editTextStatus);
        classEditText = findViewById(R.id.editTextClass);
        submitButton = findViewById(R.id.buttonSubmitReg);

        //event listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input from UI elements
                String studentNumber = studentNumberEditText.getText().toString();
                String studentName = studentNameEditText.getText().toString();
                String course = courseEditText.getText().toString();
                String department = departmentEditText.getText().toString();
                String status = statusEditText.getText().toString();
                String classValue = classEditText.getText().toString();

                // Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://localhost:5223") // Replace with API's base URL
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // API service interface
                ApiService apiService = retrofit.create(ApiService.class);

                // Make an API call to send the data
                Call<Void> call = apiService.registerStudent(studentNumber, studentName, course, department, status, classValue);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // The request was successful
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // The request failed
                            int responseCode = response.code();
                            switch (responseCode) {
                                case 400:
                                    // Handle a specific error (e.g., validation error)
                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                                    break;
                                case 500:
                                    // Handle a server error
                                    Toast.makeText(RegisterActivity.this, "Server error: " + response.message(), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    // Handle other cases
                                    Toast.makeText(RegisterActivity.this, "Registration failed with status code: " + responseCode, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // An error occurred during the HTTP request
                        // handle network errors, timeouts, or other issues here

                        //Show an error toast message
                        Toast.makeText(RegisterActivity.this, "Registration failed. Please check your network connection.", Toast.LENGTH_SHORT).show();

                        // log the error for debugging purposes
                        Log.e("NetworkError", "Error: " + t.getMessage());
                    }
                });
            }
        });
    }
}
