package com.yangezhu.forumproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class InitialLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_login);

        Button btn_to_register_activity = findViewById(R.id.btn_to_register_activity);

        btn_to_register_activity.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}