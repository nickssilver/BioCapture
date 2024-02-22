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
import com.example.biocapture.Biousers;
import com.example.biocapture.R;
import com.example.biocapture.RegisterViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminActivity extends BaseActivity {

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
        CheckBox delete = dialogLayout.findViewById(R.id.delete);
        CheckBox reports = dialogLayout.findViewById(R.id.reports);


        Biousers user = new Biousers();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register User");
        builder.setView(dialogLayout);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String userid = useridField.getText().toString();
            String name = nameField.getText().toString();
            String department = departmentField.getText().toString();
            String pin = pinField.getText().toString();
            String contact = contactField.getText().toString();

            // Calculate permissions
            RegisterViewModel registerViewModel = new RegisterViewModel();
            registerViewModel.setUser(user);
            List<Biousers.Permissions> permissionsList = new ArrayList<>();
            if (registration.isChecked()) permissionsList.add(Biousers.Permissions.Page1);
            if (verifier.isChecked()) permissionsList.add(Biousers.Permissions.Page2);
            if (delete.isChecked()) permissionsList.add(Biousers.Permissions.Page3);
            if (reports.isChecked()) permissionsList.add(Biousers.Permissions.Page4);
            registerViewModel.setPermissions(permissionsList);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.2.38:5223/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<Biousers> call = apiService.registerUser(registerViewModel);
            call.enqueue(new Callback<Biousers>() {

                @Override
                public void onResponse(Call<Biousers> call, Response<Biousers> response) {
                    if (response.isSuccessful()) {
                        Biousers registeredUser = response.body();
                        runOnUiThread(() -> Toast.makeText(AdminActivity.this, "User registered successfully: " + registeredUser.getUserId(), Toast.LENGTH_SHORT).show());
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("Registration Error", errorBody);
                            runOnUiThread(() -> Toast.makeText(AdminActivity.this, "Registration failed: " + errorBody, Toast.LENGTH_SHORT).show());
                        } catch (IOException e) {
                            Log.e("Registration Error", "Failed to get error message", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Biousers> call, Throwable t) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminActivity.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
                        Log.e("Network Error", t.getMessage(), t);
                    });
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
