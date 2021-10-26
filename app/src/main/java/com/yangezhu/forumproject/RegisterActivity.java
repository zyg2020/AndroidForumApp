package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button register_btn;

    private FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        register_btn = (Button) findViewById(R.id.register);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        register_btn.setOnClickListener(view -> {
            String txt_username = username.getText().toString();
            String txt_name = name.getText().toString();
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();

            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(RegisterActivity.this, "Please fill out the form.", Toast.LENGTH_SHORT).show();
            }else if (txt_password.length() < 5){
                Toast.makeText(RegisterActivity.this, "Password too short!!!", Toast.LENGTH_SHORT).show();
            }else{
                registerUser(txt_username, txt_name, txt_email, txt_password);
            }
        });
    }

    private void registerUser(String txt_username, String txt_name, String txt_email, String txt_password) {
        CollectionReference users_collection = firestore.collection("users");
        Query query = users_collection.whereEqualTo("username", txt_username);

        // onComplete will run regardless seccess or failure.
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    for (DocumentSnapshot documentSnapshot : task.getResult()){
//                        String retrieved_username = documentSnapshot.getString("username");
//
//                        if (username.equals(retrieved_username)){
//                            Toast.makeText(RegisterActivity.this, "Username already exists!!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
                if (task.getResult().size() == 0){
                    // onSucess will run when work is done with no errors.
                    auth.createUserWithEmailAndPassword(txt_email, txt_password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            String user_id = auth.getCurrentUser().getUid();

                            HashMap<String, Object> user_data_map = new HashMap<>();
                            user_data_map.put("username", txt_username);
                            user_data_map.put("name", txt_name);
                            user_data_map.put("email", txt_email);
                            user_data_map.put("id", user_id);

                            firestore.collection("users").document(user_id).set(user_data_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Successfully save username, email, name after creating a user account.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this , InitialLoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("YZHU_DEBUG", e.getMessage());
                        }
                    });
                }else{
                    Toast.makeText(RegisterActivity.this, "Username already exists!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}