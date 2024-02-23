package com.example.biocapture.Administer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.biocapture.Analytics.AnalyticsActivity;
import com.example.biocapture.BaseActivity;
import com.example.biocapture.DefaultActivity;
import com.example.biocapture.R;
import com.example.biocapture.RefactorActivity;
import com.example.biocapture.RegisterActivity;
import com.example.biocapture.VerifyActivity;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class LoginActivity extends BaseActivity {

    private EditText[] pinInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize pin input EditText fields
        pinInputs = new EditText[]{
                findViewById(R.id.pin_input_1),
                findViewById(R.id.pin_input_2),
                findViewById(R.id.pin_input_3),
                findViewById(R.id.pin_input_4)
        };

        // Set click listeners for numeric buttons
        setNumericButtonListeners();

        // Set click listener for the delete button
        findViewById(R.id.button_delete).setOnClickListener(v -> deleteLastCharacter());

        // Set click listener for the Admin Login TextView
        findViewById(R.id.Admin).setOnClickListener(v -> showLoginDialog());
    }

    private void setNumericButtonListeners() {
        // Array of numeric button IDs
        int[] numericButtonIds = new int[]{
                R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
                R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9
        };

        for (int i = 0; i < numericButtonIds.length; i++) {
            Button button = findViewById(numericButtonIds[i]);
            final int number = i; // Numeric value associated with the button
            button.setOnClickListener(v -> {
                // Update the first empty pin input EditText with the clicked numeric value
                for (EditText pinInput : pinInputs) {
                    if (TextUtils.isEmpty(pinInput.getText())) {
                        pinInput.setText(String.valueOf(number));
                        // Move focus to the next pin input EditText if available
                        focusNextPinInput(pinInput);
                        break;
                    }
                }
                // Check if all four digits are entered
                if (allPinsEntered()) {
                    // Concatenate the entered digits to form the PIN
                    String pin = "";
                    for (EditText pinInput : pinInputs) {
                        pin += pinInput.getText();
                    }
                    authenticateUser(pin); // Call the method to authenticate the user
                }
            });
        }
    }
    private boolean allPinsEntered() {
        for (EditText pinInput : pinInputs) {
            if (TextUtils.isEmpty(pinInput.getText())) {
                return false;
            }
        }
        return true;
    }
    private void focusNextPinInput(EditText currentPinInput) {
        // Find the index of the current pin input EditText
        int currentIndex = -1;
        for (int i = 0; i < pinInputs.length; i++) {
            if (pinInputs[i] == currentPinInput) {
                currentIndex = i;
                break;
            }
        }

        // Move focus to the next pin input EditText if available
        if (currentIndex >= 0 && currentIndex < pinInputs.length - 1) {
            pinInputs[currentIndex + 1].requestFocus();
        }
    }

    private void deleteLastCharacter() {
        for (int i = pinInputs.length - 1; i >= 0; i--) {
            EditText editText = pinInputs[i];
            if (!TextUtils.isEmpty(editText.getText())) {
                // Remove the last character from the EditText field
                editText.setText(editText.getText().subSequence(0, editText.length() - 1));
                // Move focus back to this EditText
                editText.requestFocus();
                break; // Stop after deleting one character
            }
        }
    }
    private void showLoginDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_login, null);

        // Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setView(dialogLayout);

        // Set up the positive button to capture the user's input
        builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText usernameField = dialogLayout.findViewById(R.id.username);
                EditText passwordField = dialogLayout.findViewById(R.id.password);
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                // Call method to authenticate user using API
                authenticateAdmin(username, password);
            }
        });

        // Set up the negative button to dismiss the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void authenticateUser(String pin) {
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.38:5223/") // Update with your actual backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service interface
        ApiService apiService = retrofit.create(ApiService.class);

        // Create authentication request body
        UserLoginRequest userLoginRequest = new UserLoginRequest(pin);

        // Send authentication request
        Call<PermissionsResponse> call = apiService.authenticateUser(pin);
        call.enqueue(new Callback<PermissionsResponse>() {
            @Override
            public void onResponse(Call<PermissionsResponse> call, Response<PermissionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PermissionsResponse permissionsResponse = response.body();
                    // Extract permissions and navigate accordingly
                    boolean registrationPermission = permissionsResponse.isRegisterPermission();
                    boolean verifyPermission = permissionsResponse.isVerifyPermission();
                    boolean refactorPermission = permissionsResponse.isRefactorPermission();
                    boolean analyticsPermission = permissionsResponse.isAnalyticsPermission();
                    boolean managementPermission = permissionsResponse.isManagementPermission();

                    // Store permissions in SharedPreferences or any other suitable storage
                    SharedPreferences sharedPreferences = getSharedPreferences("Permissions", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("registration", registrationPermission);
                    editor.putBoolean("verify", verifyPermission);
                    editor.putBoolean("refactor", refactorPermission);
                    editor.putBoolean("analytics", analyticsPermission);
                    editor.putBoolean("management", managementPermission);

                    // Ensure isAdmin flag is set to false
                    editor.putBoolean("isAdmin", false);
                    // Store username in SharedPreferences
                    editor.putString("username", permissionsResponse.getUserId());

                    editor.apply();

                    // Determine which activity to navigate to based on permissions
                    Intent intent;
                    if (registrationPermission) {
                        intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    } else if (verifyPermission) {
                        intent = new Intent(LoginActivity.this, VerifyActivity.class);
                    } else if (refactorPermission) {
                        intent = new Intent(LoginActivity.this, RefactorActivity.class);
                    } else if (analyticsPermission) {
                        intent = new Intent(LoginActivity.this, AnalyticsActivity.class);
                    } else if (managementPermission) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        // No permissions granted, navigate to a default activity or show a message
                        intent = new Intent(LoginActivity.this, DefaultActivity.class);
                    }

                    startActivity(intent);
                    finish(); // Finish the current activity to prevent going back to login
                } else {
                    // Authentication failed, show error message
                    Toast.makeText(LoginActivity.this, "Invalid PIN. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PermissionsResponse> call, Throwable t) {
                // Handle failure (e.g., network error)
                Toast.makeText(LoginActivity.this, "Failed to connect to server. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authenticateAdmin(String username, String password) {
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.38:5223/") // Update with your actual backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service interface
        ApiService apiService = retrofit.create(ApiService.class);

        // Create login request body
        AdminLoginRequest adminLoginRequest = new AdminLoginRequest(username, password);

        // Send login request
        Call<Void> call = apiService.adminLogin(adminLoginRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Login successful, set isAdmin flag to true
                    SharedPreferences sharedPreferences = getSharedPreferences("Permissions", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isAdmin", true);
                    editor.apply();

                    // Navigate to AdminActivity
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish(); // Finish the current activity to prevent going back to login
                } else {
                    // Login failed, show error message
                    Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure (e.g., network error)
                Toast.makeText(LoginActivity.this, "Failed to connect to server. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
