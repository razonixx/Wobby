package com.wobby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PinDetailActivity extends AppCompatActivity {

    private TextView tv, tv2, tv3;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_detail);
        tv = findViewById(R.id.textView2);
        tv2 = findViewById(R.id.textView6);
        tv3 = findViewById(R.id.textView7);

        b = findViewById(R.id.button5);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        String title = intent.getStringExtra("JOB_TITLE");
        String snippet = intent.getStringExtra("JOB_SNIPPET");
        float wage = intent.getFloatExtra("JOB_WAGE", 0);
        tv.setText(title);
        tv2.setText(snippet);
        tv3.setText(Float.toString(wage));
    }
}
