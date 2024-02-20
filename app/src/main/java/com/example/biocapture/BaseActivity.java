package com.example.biocapture;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biocapture.Administer.AdminActivity;
import com.example.biocapture.Administer.LoginActivity;
import com.example.biocapture.Analytics.AnalyticsActivity;


public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.register) {
            startActivity(new Intent(this, RegisterActivity.class));
            return true;
        } else if (item.getItemId() == R.id.verify) {
            startActivity(new Intent(this, VerifyActivity.class));
            return true;
        } else if (item.getItemId() == R.id.refactor) {
            startActivity(new Intent(this, RefactorActivity.class));
            return true;
        } else if (item.getItemId() == R.id.analytics) {
            startActivity(new Intent(this, AnalyticsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.manage) {
            startActivity(new Intent(this, AdminActivity.class));
            return true;
        } else if (item.getItemId() == R.id.login) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
