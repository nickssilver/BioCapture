package com.example.biocapture.Administer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.biocapture.BaseActivity;
import com.example.biocapture.R;

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
            });
        }
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
                // Handle login logic here
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
}
