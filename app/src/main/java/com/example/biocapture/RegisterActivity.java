package com.example.biocapture;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI elements
        TextView StudentId = (TextView) findViewById(R.id.editTextStudentId);
        TextView StudentName = (TextView) findViewById(R.id.editTextStudentName);
        TextView ClassId = (TextView) findViewById(R.id.editTextClassId);
        TextView Status = (TextView) findViewById(R.id.editTextStatus);
        TextView Arrears = (TextView) findViewById(R.id.editTextArrears);
        TextView Fingerprint1 = (TextView) findViewById(R.id.fingerPrint1);
        TextView Fingerprint2 = (TextView) findViewById(R.id.fingerPrint2);
        Button submitButton = (Button) findViewById(R.id.buttonSubmitReg);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the connection to the database
                Connection connection = MainActivity.connectionclass();

                // Use the connection to the database to insert data into the database
                if (connection != null) {
                    String sqlinsert = "Insert into Biometrics values ('" + StudentId.getText().toString() + "','" + StudentName.getText().toString() + "','" + ClassId.getText().toString() + "','" + Status.getText().toString() + "'," + Arrears.getText().toString() + ",'" + Fingerprint2.getText().toString() + "')";
                    Statement st = null;
                    try {
                        st = connection.createStatement();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        ResultSet rs = st.executeQuery(sqlinsert);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }
}
