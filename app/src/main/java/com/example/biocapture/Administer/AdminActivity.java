package com.example.biocapture.Administer;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.biocapture.BaseActivity;
import com.example.biocapture.R;
import com.example.biocapture.RetrofitClient;

import java.io.IOException;
import java.util.List;

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

        // Initialize apiService here
        Retrofit retrofit = RetrofitClient.getClient();
        apiService = retrofit.create(ApiService.class);

        // Call fetchUsersAndDisplay here
        fetchUsersAndDisplay();
    }
    void fetchUsersAndDisplay() {
        apiService.getAllUsersFromDatabase().enqueue(new Callback<List<Biousers>>() {
            @Override
            public void onResponse(Call<List<Biousers>> call, Response<List<Biousers>> response) {
                if (response.isSuccessful()) {
                    List<Biousers> users = response.body();
                    TableLayout userTable = findViewById(R.id.userTable);
//               // Clear existing table rows except for the header row
                userTable.removeViews(1, userTable.getChildCount() - 1);

                    for (Biousers user : users) {
                        TableRow tableRow = new TableRow(AdminActivity.this);
                        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                        // UserID
                        TextView userIdTextView = new TextView(AdminActivity.this);
                        userIdTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        userIdTextView.setText(user.getUserId());
                        tableRow.addView(userIdTextView);

                        // Name
                        TextView name = new TextView(AdminActivity.this);
                        name.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        name.setText(user.getName());
                        tableRow.addView(name);

                        // Department
                        TextView department = new TextView(AdminActivity.this);
                        department.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        department.setText(user.getDepartment());
                        tableRow.addView(department);

                        // Pin No
                        TextView pin = new TextView(AdminActivity.this);
                        pin.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        pin.setText(user.getPin());
                        tableRow.addView(pin);

                        // Contact
                        TextView contact = new TextView(AdminActivity.this);
                        contact.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        contact.setText(user.getContact());
                        tableRow.addView(contact);

                        // Create a Button for the action column
                        Button actionButton = new Button(AdminActivity.this);
                        actionButton.setText("Action");
                        // Set the layout_gravity to center to align the button
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParams.gravity = Gravity.CENTER;
                        actionButton.setLayoutParams(layoutParams);
                        tableRow.addView(actionButton);

                        userTable.addView(tableRow);

                        // Set a click listener for the action button
                        actionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PopupMenu popupMenu = new PopupMenu(AdminActivity.this, actionButton);
                                // Inflate the menu from XML
                                popupMenu.getMenuInflater().inflate(R.menu.action_menu, popupMenu.getMenu());

                                // Set an OnMenuItemClickListener
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        int itemId = item.getItemId();
                                        if (itemId == R.id.action_edit) {
                                            // Handle action 1 - Open dialog for editing user data
                                            showEditDialog(user);
                                            return true;
                                        } else if (itemId == R.id.action_delete) {
                                            // Extract user ID from TextView
                                            String userId = userIdTextView.getText().toString();
                                            // Call the method to delete user
                                            deleteUser(userId);
                                            return true;
                                        } else if (itemId == R.id.action_view) {
                                            // Handle action 3
                                            return true;
                                        }
                                        // Add more conditions for other actions if needed
                                        return false;
                                    }
                                });

                                // Show the popup menu
                                popupMenu.show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Biousers>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Method to show edit dialog with user data pre-filled
    private void showEditDialog(Biousers user) {
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

        // Set user data to edit fields
        useridField.setText(user.getUserId());
        nameField.setText(user.getName());
        departmentField.setText(user.getDepartment());
        pinField.setText(user.getPin());
        contactField.setText(user.getContact());

        // Set CheckBoxes
        registration.setChecked(user.isRegisterPermission());
        verifier.setChecked(user.isVerifyPermission());
        refactor.setChecked(user.isRefactorPermission());
        analytics.setChecked(user.isAnalyticsPermission());
        management.setChecked(user.isManagementPermission());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit User");
        builder.setView(dialogLayout);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            //Get the input values
            String userId = useridField.getText().toString();
            String name = nameField.getText().toString();
            String department = departmentField.getText().toString();
            String pin = pinField.getText().toString();
            String contact = contactField.getText().toString();

            // Check if any of the fields are empty
            if (userId.isEmpty() || name.isEmpty() || department.isEmpty() || pin.isEmpty() || contact.isEmpty()) {
                Toast.makeText(AdminActivity.this, "All fields are Mandatory", Toast.LENGTH_SHORT).show();
                return; // Don't proceed with registration
            }

            // Create a new Biousers object
            Biousers updatedUser = new Biousers(userId, name, department, pin, contact,
                    registration.isChecked(), verifier.isChecked(),
                    refactor.isChecked(), analytics.isChecked(),
                    management.isChecked());

            // Call method to update user data
            updateUser(user.getUserId(), updatedUser);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Method to update user data
    private void updateUser(String userId, Biousers updatedUser) {
        // Call the edit user API
        apiService.editUser(userId, updatedUser).enqueue(new Callback<Biousers>() {
            @Override
            public void onResponse(Call<Biousers> call, Response<Biousers> response) {
                if (response.isSuccessful()) {
                    // User updated successfully
                    Toast.makeText(AdminActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    // Refresh the user list after update
                    fetchUsersAndDisplay();
                } else {
                    // User update failed
                    Toast.makeText(AdminActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Biousers> call, Throwable t) {
                // Failure in network call or processing response
                Toast.makeText(AdminActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to delete user
    private void deleteUser(String userId) {
        // Call the delete user API
        apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // User deleted successfully
                    Toast.makeText(AdminActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    // Refresh the user list after deletion
                    fetchUsersAndDisplay();
                } else {
                    // User deletion failed
                    Toast.makeText(AdminActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Failure in network call or processing response
                Toast.makeText(AdminActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
            }
        });
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
                        // Refresh the user list after registration
                        fetchUsersAndDisplay();
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