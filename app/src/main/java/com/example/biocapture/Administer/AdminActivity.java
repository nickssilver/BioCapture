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

    public enum UserPermissions {
        REGISTRATION(1),
        VERIFIER(2),
        DELETE(4),
        REPORTS(8);

        private final int value;

        UserPermissions(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
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
            int permissions =  0;
            if (registration.isChecked()) permissions |= UserPermissions.REGISTRATION.getValue();
            if (verifier.isChecked()) permissions |= UserPermissions.VERIFIER.getValue();
            if (delete.isChecked()) permissions |= UserPermissions.DELETE.getValue();
            if (reports.isChecked()) permissions |= UserPermissions.REPORTS.getValue();

            Biousers user = new Biousers();
            user.setUserId(userid);
            user.setName(name);
            user.setDepartment(department);
            user.setPin(pin);
            user.setContact(contact);
            user.setPermissions(permissions);


            RegisterViewModel registerViewModel = new RegisterViewModel();
            registerViewModel.setUser(user);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.16.71:5223/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<Void> call = apiService.registerUser(registerViewModel);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(AdminActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(AdminActivity.this, "Registration failed", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
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
