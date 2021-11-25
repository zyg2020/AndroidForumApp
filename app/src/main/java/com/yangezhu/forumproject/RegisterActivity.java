package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Collection;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button register_btn;
    private Button btn_to_login_activity;
    private Button btn_avatar;
    private ImageView image_view_avatar;

    private Uri imageUri;
    private String uploaded_avatar_url;
    private FirebaseAuth auth;
    FirebaseFirestore firestore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        register_btn = (Button) findViewById(R.id.register);
        btn_to_login_activity = (Button)findViewById(R.id.btn_to_login_activity);
        btn_avatar = (Button)findViewById(R.id.btn_avatar);
        image_view_avatar = (ImageView)findViewById(R.id.image_view_avatar);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

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
                progressDialog.setMessage("Please Wait!");
                progressDialog.show();
                registerUser(txt_username, txt_name, txt_email, txt_password);
            }
        });

        btn_to_login_activity.setOnClickListener(view -> {
            onBackPressed();
        });

        btn_avatar.setOnClickListener(view -> CropImage.activity().start(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            image_view_avatar.setImageURI(imageUri);
        }else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this , RegisterActivity.class));
            finish();
        }
    }

    private String getFileExtension(Uri url) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(url));
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

                    if (imageUri != null){
                        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                        fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uploaded_avatar_url = uri.toString();
                                        CreateUserProfile(txt_username, txt_email, txt_name, txt_password, uploaded_avatar_url);
                                    }
                                });
                            }
                        });
                    }else{
                        CreateUserProfile(txt_username, txt_email, txt_name, txt_password, "");
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Username already exists!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });


    }

    private void CreateUserProfile(String txt_username, String txt_email, String txt_name, String txt_password, String uploaded_avatar_url) {
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
                user_data_map.put("avatar", uploaded_avatar_url);


                firestore.collection("users").document(user_id).set(user_data_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Successfully save username, email, name after creating a user account.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this , InitialLoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            progressDialog.dismiss();
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
                progressDialog.dismiss();
            }
        });
    }
}