package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class InitialLoginActivity extends AppCompatActivity {

    private EditText email_edt_text;
    private EditText password_edt_text;
    private Button login_btn;
    private Button btn_to_register_activity;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_login);

        email_edt_text = (EditText)findViewById(R.id.email);
        password_edt_text = (EditText)findViewById(R.id.password);
        login_btn = findViewById(R.id.login);
        btn_to_register_activity = (Button) findViewById(R.id.btn_to_register_activity);

        auth = FirebaseAuth.getInstance();

        login_btn.setOnClickListener(view -> {
            String email_value = email_edt_text.getText().toString();
            String password_value = password_edt_text.getText().toString();

            if (TextUtils.isEmpty(email_value) || TextUtils.isEmpty(password_value)){
                Toast.makeText(InitialLoginActivity.this, "Please fill out the form!", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email_value , password_value);
            }
        });

        btn_to_register_activity.setOnClickListener(view -> {
            Intent intent = new Intent(InitialLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(InitialLoginActivity.this , MainActivity.class));
            finish();
        }
    }

    private void loginUser(String email_value, String password_value) {
        auth.signInWithEmailAndPassword(email_value, password_value).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(InitialLoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InitialLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}