package com.example.biocapture;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RefactorActivity extends BaseActivity {

    EditText studentAdmn, editDetails;
    Button buttonDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refactor);
    }
}