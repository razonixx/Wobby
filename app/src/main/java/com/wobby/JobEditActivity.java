package com.wobby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class JobEditActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextWage;
    private Button b3, b4;
    Intent startIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_edit);

        startIntent = getIntent();

        editTextTitle = findViewById(R.id.editTextJobTitle);
        editTextDescription = findViewById(R.id.editTextJobDescription);
        editTextWage = findViewById(R.id.editTextWage);
        b3 = findViewById(R.id.button3);
        b4 = findViewById(R.id.button4);

        editTextTitle.setText(startIntent.getStringExtra("JOB_TITLE"));
        editTextDescription.setText(startIntent.getStringExtra("JOB_SNIPPET"));
        editTextWage.setText(String.valueOf(startIntent.getFloatExtra("JOB_WAGE", 0)));

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("MODE", "JOB_CREATE");
                intent.putExtra("JOB_ID", startIntent.getStringExtra("JOB_ID"));
                intent.putExtra("JOB_TITLE", editTextTitle.getText().toString());
                intent.putExtra("JOB_SNIPPET", editTextDescription.getText().toString());
                intent.putExtra("JOB_WAGE", Float.valueOf(editTextWage.getText().toString()));
                finish();
                startActivity(intent);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float lat = startIntent.getFloatExtra("JOB_LAT", 0);
                float lng = startIntent.getFloatExtra("JOB_LONG", 0);
                Job newJob = new Job(startIntent.getStringExtra("JOB_ID"),editTextTitle.getText().toString(), editTextDescription.getText().toString(), (float)Float.valueOf(editTextWage.getText().toString()), lat, lng);
                Log.wtf("JOB", newJob.jobToJSONWithID());
                Toast.makeText(getApplicationContext(), "Job edited succesfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
