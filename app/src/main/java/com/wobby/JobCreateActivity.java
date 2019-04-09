package com.wobby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class JobCreateActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextWage;
    private Button b3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_create);

        editTextTitle = findViewById(R.id.editTextJobTitle);
        editTextDescription = findViewById(R.id.editTextJobDescription);
        editTextWage = findViewById(R.id.editTextWage);
        b3 = findViewById(R.id.button3);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("MODE", "JOB_CREATE");
                intent.putExtra("JOB_TITLE", editTextTitle.getText().toString());
                intent.putExtra("JOB_SNIPPET", editTextDescription.getText().toString());
                intent.putExtra("JOB_WAGE", Float.valueOf(editTextWage.getText().toString()));
                finish();
                startActivity(intent);
            }
        });

    }
}
