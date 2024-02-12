package com.example.biocapture;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.stream.Collectors;

import api.ApiService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RefactorActivity extends AppCompatActivity {

    private EditText studentAdmn, editDetails;
    private Button buttonDelete;

    private static final String BASE_URL = "http://192.168.0.47:5000";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static ApiService apiService = retrofit.create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refactor);

        studentAdmn = findViewById(R.id.studentAdmn);
        editDetails = findViewById(R.id.editDetails);
        buttonDelete = findViewById(R.id.buttonDelete);

        studentAdmn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String studentId = s.toString();
                fetchAllTemplates(studentId);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentId = studentAdmn.getText().toString();
                deleteStudent(studentId);
            }
        });
    }

    private void deleteStudent(final String studentId) {
        Call<ResponseBody> call = apiService.deleteStudent(studentId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RefactorActivity.this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                            // Clear the fields
                            studentAdmn.setText("");
                            editDetails.setText("");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RefactorActivity.this, "Failed to delete student", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RefactorActivity.this, "Failed to delete student", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchAllTemplates(final String studentId) {
        Call<List<FingerprintTemplate>> call = apiService.getAllTemplatesFromDatabase();
        call.enqueue(new Callback<List<FingerprintTemplate>>() {
            @Override
            public void onResponse(Call<List<FingerprintTemplate>> call, Response<List<FingerprintTemplate>> response) {
                if (response.isSuccessful()) {
                    List<FingerprintTemplate> students = response.body();
                    // Filter the list to only include the student with the matching studentId
                    List<FingerprintTemplate> filteredStudents = students.stream()
                            .filter(student -> student.getStudentId().equals(studentId))
                            .collect(Collectors.toList());
                    // Check if any student was found
                    if (!filteredStudents.isEmpty()) {
                        // Get the first student in the filtered list
                        FingerprintTemplate student = filteredStudents.get(0);
                        // Display the student's details
                        String studentDetails = "ID: " + student.getStudentId() + ", Name: " + student.getStudentName() + ", Class: " + student.getClassId() + ", Status: " + student.getStatus() + ", Arrears: " + student.getArrears();
                        editDetails.setText(studentDetails);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RefactorActivity.this, "No student found with the entered ID", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RefactorActivity.this, "Failed to fetch all templates", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<FingerprintTemplate>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RefactorActivity.this, "Failed to fetch all templates", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



}
