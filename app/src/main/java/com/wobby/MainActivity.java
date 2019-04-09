package com.wobby;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_CODE = 1;
    final static String uri = "https://sheltered-retreat-56384.herokuapp.com";
    private TextView tv;
    private Button b1, b2;
    private BackendManager bm;

    private Boolean isSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //isSignedIn = false;
        isSignedIn = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);
        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSignedIn){
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("MODE", "JOB_SEEKER");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_CODE);
                }

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JobProviderActivity.class);
                startActivity(intent);
            }
        });

        bm = new BackendManager(this);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case LOGIN_CODE:
                if(resultCode == Activity.RESULT_OK){
                    tv.setText("Welcome back, " + data.getStringExtra("email"));
                    bm.login(data.getStringExtra("email"), data.getStringExtra("password"));

                    isSignedIn = true;
                }
            break;
        }
    }
}
