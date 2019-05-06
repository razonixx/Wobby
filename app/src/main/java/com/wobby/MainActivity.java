package com.wobby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    final static String uri = "https://sheltered-retreat-56384.herokuapp.com";
    private static final int LOGIN_CODE = 1;
    private TextView tv;
    private Button b1, b2;
    private SharedPreferences prefs;


    private Boolean isSignedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        if (prefs.getString("token", "").equals("")) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, LOGIN_CODE);
        }
        else{
            Log.wtf(
                    "USER",
                    " \n" + prefs.getString("token", "")       +  "\n" +
                            prefs.getString("name", "")       +   "\n" +
                            prefs.getString("last_name", "")       +         "\n" +
                            prefs.getString("username", "")       +         "\n" +
                            prefs.getBoolean("isJobProvider", false)       +       "\n" +
                            prefs.getBoolean("isWorker", false)
            );
            //Log.wtf("BASE64",prefs.getString("profile_img", "No Image Found") + "\n");
            Intent intent = new Intent(getApplicationContext(), JobProviderActivity.class);
            intent.putExtra("USER_FIRST_NAME", prefs.getString("name", ""));
            intent.putExtra("USER_LAST_NAME", prefs.getString("last_name", ""));
            intent.putExtra("USERNAME", prefs.getString("username", ""));
            intent.putExtra("ISJOBPROVIDER", prefs.getBoolean("isJobProvider", false));
            intent.putExtra("PROVIDERIMAGE", prefs.getString("profile_img", ""));
            intent.putExtra("ISJOBSEEKER", prefs.getBoolean("isWorker", false));
            startActivity(intent);
            finish();
        }

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            b2 = findViewById(R.id.button2);
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), JobProviderActivity.class);
                    intent.putExtra("USER_FIRST_NAME", prefs.getString("name", ""));
                    intent.putExtra("USER_LAST_NAME", prefs.getString("last_name", ""));
                    intent.putExtra("USERNAME", prefs.getString("username", ""));
                    intent.putExtra("ISJOBPROVIDER", prefs.getBoolean("isJobProvider", false));
                    intent.putExtra("ISJOBSEEKER", prefs.getBoolean("isWorker", false));
                    startActivity(intent);
                    finish();
                }
            });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case LOGIN_CODE:
                if(resultCode == Activity.RESULT_OK){
                    isSignedIn = true;
                    Log.wtf(
                            "USER",
                            " \n" + prefs.getString("token", "")       +  "\n" +
                                    prefs.getString("name", "")       +   "\n" +
                                    prefs.getString("last_name", "")       +         "\n" +
                                    prefs.getString("username", "")       +         "\n" +
                                    prefs.getBoolean("isJobProvider", false)       +       "\n" +
                                    prefs.getBoolean("isWorker", false)
                    );
                    Intent intent = new Intent(getApplicationContext(), JobProviderActivity.class);
                    intent.putExtra("USER_FIRST_NAME", prefs.getString("name", ""));
                    intent.putExtra("USER_LAST_NAME", prefs.getString("last_name", ""));
                    intent.putExtra("USERNAME", prefs.getString("username", ""));
                    intent.putExtra("ISJOBPROVIDER", prefs.getBoolean("isJobProvider", false));
                    intent.putExtra("ISJOBSEEKER", prefs.getBoolean("isWorker", false));
                    startActivity(intent);
                }
            break;
        }
    }
}
