package com.wobby;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
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
    private Intent startIntent;
    private BackEndManager b;

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
        b = new BackEndManager();

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("MODE", "JOB_EDIT");
                intent.putExtra("JOB_ID", startIntent.getStringExtra("JOB_ID"));
                intent.putExtra("JOB_TITLE", editTextTitle.getText().toString());
                intent.putExtra("JOB_SNIPPET", editTextDescription.getText().toString());
                intent.putExtra("JOB_WAGE", Float.valueOf(editTextWage.getText().toString()));
                intent.putExtra("JOB_LAT", startIntent.getDoubleExtra("JOB_LAT", 0));
                intent.putExtra("JOB_LONG", startIntent.getDoubleExtra("JOB_LONG", 0));
                finish();
                startActivity(intent);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = startIntent.getDoubleExtra("JOB_LAT", 0);
                double lng = startIntent.getDoubleExtra("JOB_LONG", 0);
                Job newJob = new Job(startIntent.getStringExtra("JOB_ID"),editTextTitle.getText().toString(), editTextDescription.getText().toString(), Float.valueOf(editTextWage.getText().toString()), lat, lng);
                Log.wtf("JOB", newJob.jobToJSONWithID());
                editJob(newJob);
                Toast.makeText(getApplicationContext(), "Job edited succesfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    @SuppressLint("StaticFieldLeak")
    public void editJob(final Job job) {

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                return b.Modify_Job(job.getJobId(),job.getJobTitle(), job.getJobSnippet(), job.getJobLatLng().latitude, job.getJobLatLng().longitude, (double)job.getJobWage());
            }
            @Override
            protected void onPostExecute(String r) {
                super.onPostExecute(r);
                Log.wtf("POST RESULT", r);
            }
        }.execute();
    }
}
