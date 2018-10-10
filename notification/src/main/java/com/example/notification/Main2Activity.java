package com.example.notification;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    private TextView tv_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tv_content = findViewById(R.id.tv_content);
        Intent intent = getIntent();
        PersonBeen parcelable = (PersonBeen)intent.getParcelableExtra("parcelable");
        tv_content.setText(parcelable.toString());

    }
}
