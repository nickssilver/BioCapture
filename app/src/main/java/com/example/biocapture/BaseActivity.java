package com.example.biocapture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biocapture.Administer.AdminActivity;
import com.example.biocapture.Administer.LoginActivity;
import com.example.biocapture.Analytics.AnalyticsActivity;

public class BaseActivity extends AppCompatActivity {

    private MenuItem registerItem;
    private MenuItem verifyItem;
    private MenuItem refactorItem;
    private MenuItem analyticsItem;
    private MenuItem manageItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);

        registerItem = menu.findItem(R.id.register);
        verifyItem = menu.findItem(R.id.verify);
        refactorItem = menu.findItem(R.id.refactor);
        analyticsItem = menu.findItem(R.id.analytics);
        manageItem = menu.findItem(R.id.manage);
        MenuItem logoutItem = menu.findItem(R.id.logout);

        updateMenuVisibility();
        // Get the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Permissions", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Get the isAdmin flag from SharedPreferences
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        // Check if the user is not an admin before setting the title of the logout menu item
        if (!username.isEmpty() && !isAdmin) {
            logoutItem.setTitle("Logout (" + username + ")");
        } else {
            logoutItem.setTitle("Logout");
        }
        return true;
    }

    private void updateMenuVisibility() {
        SharedPreferences sharedPreferences = getSharedPreferences("Permissions", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        if (isAdmin) {
            // Admin has full access to all pages
            registerItem.setVisible(true);
            verifyItem.setVisible(true);
            refactorItem.setVisible(true);
            analyticsItem.setVisible(true);
            manageItem.setVisible(true);
        } else {
            // Regular user's permissions
            boolean registrationPermission = sharedPreferences.getBoolean("registration", false);
            boolean verifyPermission = sharedPreferences.getBoolean("verify", false);
            boolean refactorPermission = sharedPreferences.getBoolean("refactor", false);
            boolean analyticsPermission = sharedPreferences.getBoolean("analytics", false);
            boolean managementPermission = sharedPreferences.getBoolean("management", false);

            registerItem.setVisible(registrationPermission);
            verifyItem.setVisible(verifyPermission);
            refactorItem.setVisible(refactorPermission);
            analyticsItem.setVisible(analyticsPermission);
            manageItem.setVisible(managementPermission);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.register) {
            startActivity(new Intent(this, RegisterActivity.class));
            return true;
        } else if (id == R.id.verify) {
            startActivity(new Intent(this, VerifyActivity.class));
            return true;
        } else if (id == R.id.refactor) {
            startActivity(new Intent(this, RefactorActivity.class));
            return true;
        } else if (id == R.id.analytics) {
            startActivity(new Intent(this, AnalyticsActivity.class));
            return true;
        } else if (id == R.id.manage) {
            startActivity(new Intent(this, AdminActivity.class));
            return true;
        } else if (id == R.id.logout) {
            // Clear user session data
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Navigate to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Finish the current activity to prevent going back to login
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}

