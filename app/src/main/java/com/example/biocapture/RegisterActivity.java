package com.example.biocapture;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Initialize UI elements
        studentNumberEditText = findViewById(R.id.editTextStudentNo);
        studentNameEditText = findViewById(R.id.editTextStudentName);
        courseEditText = findViewById(R.id.editTextCourse);
        departmentEditText = findViewById(R.id.editTextDepartment);
        statusEditText = findViewById(R.id.editTextStatus);
        classEditText = findViewById(R.id.editTextClass);

        submitButton = findViewById(R.id.buttonSubmitReg);

        // Set up event listeners for the submit button or any other elements as needed
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement your registration logic here
                // You can access the user input using the UI elements (e.g., studentNumberEditText.getText().toString())
                // and send the data to your server.
            }
        });
    }
}