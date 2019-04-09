package com.wobby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PinDetailActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_detail);
        tv = findViewById(R.id.textView2);

        Intent intent = getIntent();
        String title = intent.getStringExtra("Title");
        tv.setText(title);
    }
}
