package com.example.biocapture.Administer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.biocapture.BaseActivity;
import com.example.biocapture.R;
import com.example.biocapture.RetrofitClient;

import java.io.IOException;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdminActivity extends BaseActivity {

    private ApiService apiService;

    private EditText useridField;
    private EditText nameField;
    private EditText departmentField;
    private EditText pinField;
    private EditText contactField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        findViewById(R.id.AdduserButton).setOnClickListener(v -> showAdminDialog());

        Spinner actionSpinner = findViewById(R.id.actionSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.action_options, R.layout.spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        actionSpinner.setAdapter(adapter);
    }

    private void showAdminDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_admin, null);

        useridField = dialogLayout.findViewById(R.id.userid);
        nameField = dialogLayout.findViewById(R.id.name);
        departmentField = dialogLayout.findViewById(R.id.department);
        pinField = dialogLayout.findViewById(R.id.pin);
        contactField = dialogLayout.findViewById(R.id.contact);

        // Initialize CheckBox fields
        CheckBox registration = dialogLayout.findViewById(R.id.registration);
        CheckBox verifier = dialogLayout.findViewById(R.id.verifier);
        CheckBox refactor = dialogLayout.findViewById(R.id.refactor);
        CheckBox analytics = dialogLayout.findViewById(R.id.analytics);
        CheckBox management = dialogLayout.findViewById(R.id.management);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Registration");
        builder.setView(dialogLayout);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            //Get the input values
            String userid = useridField.getText().toString();
            String name = nameField.getText().toString();
            String department = departmentField.getText().toString();
            String pin = pinField.getText().toString();
            String contact = contactField.getText().toString();

            // Check if any of the fields are empty
            if (userid.isEmpty() || name.isEmpty() || department.isEmpty() || pin.isEmpty() || contact.isEmpty()) {
                Toast.makeText(AdminActivity.this, "All fields are Mandatory", Toast.LENGTH_SHORT).show();
                return; // Don't proceed with registration
            }
            // Create a new Biousers object
            Biousers user = new Biousers(userid, name, department, pin, contact,
                    registration.isChecked(), verifier.isChecked(),
                    refactor.isChecked(), analytics.isChecked(),
                    management.isChecked());

            // Initialize apiService
            Retrofit retrofit = RetrofitClient.getClient();
            apiService = retrofit.create(ApiService.class);

            // Make a POST request to register the user
            Call<Biousers> call = apiService.registerUser(user);
            call.enqueue(new Callback<Biousers>() {
                @Override
                public void onResponse(Call<Biousers> call, Response<Biousers> response) {
                    if (response.isSuccessful()) {
                        Biousers registeredUser = response.body();
                        Toast.makeText(AdminActivity.this, "User registered successfully: " + registeredUser.getUserId(), Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("Registration Error", errorBody);
                            Toast.makeText(AdminActivity.this, "Registration failed: " + errorBody, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e("Registration Error", "Failed to get error message", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Biousers> call, Throwable t) {
                    Toast.makeText(AdminActivity.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
                    Log.e("Network Error", t.getMessage(), t);
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
