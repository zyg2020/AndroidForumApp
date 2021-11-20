package com.yangezhu.forumproject;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.yangezhu.forumproject.adapter.PostListAdapter;
import com.yangezhu.forumproject.adapter.SelectedImagesAdapter;
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
    private SelectedImagesAdapter selectedImagesAdapter;

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
                auth = FirebaseAuth.getInstance();
                firestore = FirebaseFirestore.getInstance();

                String user_id = auth.getCurrentUser().getUid();
                firestore.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        username = document.getString ("username");

                        if (uploaded_images_uri_list.size() > 0){
                            List<String> uploaded_images_url = new ArrayList<String>();

                            boolean complete_upload = false;
                            for (int i = 0; i < uploaded_images_uri_list.size(); i++) {
                                StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                                        .child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(uploaded_images_uri_list.get(i)));
                                int current_index = i;
                                fileRef.putFile(uploaded_images_uri_list.get(i)).addOnCompleteListener(task1 -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String url = uri.toString();

                                    uploaded_images_url.add(url);

                                    selected_images_key_uploaded_url_value.put(uploaded_images_uri_list.get(current_index), url);
                                    Log.d("YZHU_DATA_SUBMIT", "One image uploaded: " + url);
//                                    if (current_index == uploaded_images_uri_list.size()-1){
//                                        uploadPost(title, description, current_time, user_id, username, selected_category, uploaded_images_url);
//                                    }

                                    if (uploaded_images_url.size() == uploaded_images_uri_list.size()){
                                        updatePost(title, description, current_time, user_id, username, selected_category, uploaded_images_url);
                                    }
                                }));
                            }
                        }else{
                            updatePost(title, description, current_time, user_id, username, selected_category, new ArrayList<String>());
                        }

                        Log.d("YZHU_DATA_SUBMIT", "title: " + title);
                        Log.d("YZHU_DATA_SUBMIT", "description: " + description);
                        Log.d("YZHU_DATA_SUBMIT", "publish_date: " + current_time.toString());

                        Log.d("YZHU_DATA_SUBMIT", "user_id: " + user_id);
                        Log.d("YZHU_DATA_SUBMIT", "username: " + username);
                        Log.d("YZHU_DATA_SUBMIT", "category: " + selected_category);
                        for (int i = 0; i < uploaded_images_uri_list.size(); i++) {
                            Log.d("YZHU_DATA_SUBMIT", "Multiple images --> " + uploaded_images_uri_list.get(i).toString());
                        }
                    }
                });
            }
        });


        uploaded_images_uri_list.clear();
        selectedImagesAdapter = new SelectedImagesAdapter(uploaded_images_uri_list, selected_images_key_uploaded_url_value, this);

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

        recycle_view_selected_images.setAdapter(selectedImagesAdapter);


        Gson gson = new Gson();
        selected_post = gson.fromJson(getIntent().getStringExtra(PostListAdapter.SELECTED_POST), Post.class);

        edt_title.setText(selected_post.getTitle());
        edt_description.setText(selected_post.getDescription());

        //categories_list.indexOf(selected_post.getCategory())
        category_spinner.setSelection(categories_list.indexOf(selected_post.getCategory()));

    }

    private void updatePost(String title, String description, Date current_time, String user_id, String username, String selected_category, List<String> uploaded_images_url) {
        for (int i = 0; i < uploaded_images_url.size(); i++) {
            Log.d("YZHU_DATA_SUBMIT", "Multiple images --> " + uploaded_images_url.get(i));
        }

        uploaded_images_url.clear();
        for (int i = 0; i < uploaded_images_uri_list.size(); i++) {
            uploaded_images_url.add(selected_images_key_uploaded_url_value.get(uploaded_images_uri_list.get(i)));
            Log.d("YZHU_Correct_ORDER", "Multiple images --> " + uploaded_images_url.get(i));
        }

        Map<String, Object> post_date_map = new HashMap<>();
        post_date_map.put("title", title);
        post_date_map.put("description", description);
        post_date_map.put("publish_date", current_time);
        post_date_map.put("user_id", user_id);
        post_date_map.put("user_name", username);
        post_date_map.put("category", selected_category);
        post_date_map.put("images", uploaded_images_url);


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

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){
                    String imagePath = data.getData().getPath();
                    Log.d("YZHU_IMAGE_SELECT", "One image --> " + imagePath);
                    Uri mImageUri=data.getData();
                    uploaded_images_uri_list.add(mImageUri);
                    selectedImagesAdapter.notifyDataSetChanged();
                    selected_images_key_uploaded_url_value.put(mImageUri, "");

                    String display_text = "";
                    for (int i = 0; i < uploaded_images_uri_list.size(); i++) {

                        display_text += uploaded_images_uri_list.get(i).toString() + "\n";
                        Log.d("YZHU_IMAGE_SELECT", "Multiple images --> " + uploaded_images_uri_list.get(i));
                    }
                    txt_display_upload_images.setText(display_text);

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