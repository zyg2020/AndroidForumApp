package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.yangezhu.forumproject.adapter.PostListAdapter;
import com.yangezhu.forumproject.adapter.SelectedImagesWhenUpdateAdapter;
import com.yangezhu.forumproject.model.Post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePostActivity extends AppCompatActivity {
    private Post selected_post;

    private EditText edt_title;
    private EditText edt_description;

    private Spinner category_spinner;
    private Button btn_upload_images;
    private Button btn_post;

    private TextView txt_display_upload_images;

    private RecyclerView recycle_view_selected_images;
    private SelectedImagesWhenUpdateAdapter selectedImagesWhenUpdateAdapter;
    private List<String> selected_post_images_urls;

    ArrayList<Uri> uploaded_images_uri_list = new ArrayList<Uri>();
    Map<Uri, String> selected_images_key_uploaded_url_value = new HashMap<>();

    List<String> categories_list = Arrays.asList("Used Items", "Marketing", "Rent", "Used Cars");
    private String selected_category;
    final int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;

    private String username;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_post);

        progressDialog = new ProgressDialog(this);

        category_spinner = (Spinner) findViewById(R.id.category);
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                String item = adapterView.getItemAtPosition(i).toString();
                selected_category = item;
                // Showing selected spinner item
                Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> categories_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories_list);

        // Drop down layout style - list view with radio button
        categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        category_spinner.setAdapter(categories_adapter);

        btn_upload_images = (Button) findViewById(R.id.btn_upload_images);
        btn_upload_images.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
        });

        txt_display_upload_images = (TextView) findViewById(R.id.txt_display_upload_images);
        edt_title = (EditText) findViewById(R.id.title) ;
        edt_description = (EditText) findViewById(R.id.description) ;
        btn_post= (Button) findViewById(R.id.btn_add_post);
        btn_post.setText("Update");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btn_post.setOnClickListener(view12 -> {

            String title = edt_title.getText().toString();
            String description = edt_description.getText().toString();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)){
                Toast.makeText(UpdatePostActivity.this,"Please fill out at least title and description fields.", Toast.LENGTH_LONG).show();
                if (TextUtils.isEmpty(title)){
                    edt_title.setError("Title cannot be blank");
                }else if (TextUtils.isEmpty(description)){
                    edt_description.setError("Description cannot be blank");
                }

            }else{
                progressDialog = new ProgressDialog(UpdatePostActivity.this);
                progressDialog.setMessage("Posting...");
                progressDialog.show();

                // String current_time = DateUtilities.getCurrentTime();
                Date current_time =new Date();

                String user_id = auth.getCurrentUser().getUid();
                DocumentReference post_ref = firestore.collection("posts").document(selected_post.getPost_id());
                post_ref.update("title", title, "description", description,"category", selected_category ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UpdatePostActivity.this,"Update successfully.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        onBackPressed();
                    }
                });

            }
        });
        Gson gson = new Gson();
        selected_post = gson.fromJson(getIntent().getStringExtra(PostListAdapter.SELECTED_POST), Post.class);

        uploaded_images_uri_list.clear();

        selected_post_images_urls = selected_post.getImages();
        selectedImagesWhenUpdateAdapter = new SelectedImagesWhenUpdateAdapter(selected_post_images_urls, selected_post.getPost_id(),this);

        recycle_view_selected_images = (RecyclerView) findViewById(R.id.recycle_view_selected_images);
        recycle_view_selected_images.setHasFixedSize(true);

        // Set recyclerView orientation.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycle_view_selected_images.setLayoutManager(new GridLayoutManager(this, 2));

        // Add devide bar
        recycle_view_selected_images.addItemDecoration(
                new DividerItemDecoration(this,
                        DividerItemDecoration.HORIZONTAL));
        recycle_view_selected_images.addItemDecoration(
                new DividerItemDecoration(this,
                        DividerItemDecoration.VERTICAL)
        );

        recycle_view_selected_images.setAdapter(selectedImagesWhenUpdateAdapter);

        edt_title.setText(selected_post.getTitle());
        edt_description.setText(selected_post.getDescription());

        //categories_list.indexOf(selected_post.getCategory())
        category_spinner.setSelection(categories_list.indexOf(selected_post.getCategory()));

    }

    private String getFileExtension(Uri url) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(url));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data
                progressDialog.setMessage("Updating...");
                progressDialog.show();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){
                    String imagePath = data.getData().getPath();
                    Log.d("YZHU_IMAGE_SELECT", "One image --> " + imagePath);
                    Uri mImageUri=data.getData();
//                    uploaded_images_uri_list.add(mImageUri);
//                    selectedImagesWhenUpdateAdapter.notifyDataSetChanged();
//                    selected_images_key_uploaded_url_value.put(mImageUri, "");

                    StorageReference fileRef = FirebaseStorage
                            .getInstance()
                            .getReference()
                            .child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

                    fileRef.putFile(mImageUri).addOnCompleteListener(task1 -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = uri.toString();

                        // selected_post.getImages().add(url);

                        String post_id = selected_post.getPost_id();
                        DocumentReference post_ref = firestore.collection("posts").document(post_id);
                        post_ref.update("images", FieldValue.arrayUnion(url)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UpdatePostActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();

                                selected_post_images_urls.add(url);
                                progressDialog.dismiss();

                                selectedImagesWhenUpdateAdapter.notifyDataSetChanged();
                            }
                        });

                    }));
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

                        int count = mClipData.getItemCount();
                        for (int i = 0; i < count; i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);

                            String imagePath = item.toString();
                            Log.d("YZHU_IMAGE_SELECT", "Multiple images --> " + imagePath);
                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(UpdatePostActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(UpdatePostActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}