package com.yangezhu.forumproject.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.UploadTask;
import com.yangezhu.forumproject.MainActivity;
import com.yangezhu.forumproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AddPostFragment extends Fragment {

    private EditText edt_title;
    private EditText edt_description;

    private Spinner category_spinner;
    private Button btn_upload_images;
    private Button btn_post;

    private TextView txt_display_upload_images;

    ArrayList<Uri> uploaded_images_uri_list = new ArrayList<Uri>();

    String[] categories_list = {"Used Items", "Marketing", "Rent", "Used Cars"};
    private String selected_category;
    final int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;

    private String username;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        category_spinner = (Spinner) view.findViewById(R.id.category);
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
        List<String> categories_list = new ArrayList<String>();
        categories_list.add("Used Items");
        categories_list.add("Marketing");
        categories_list.add("Rent");
        categories_list.add("Used Cars");

        ArrayAdapter<String> categories_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories_list);

        // Drop down layout style - list view with radio button
        categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        category_spinner.setAdapter(categories_adapter);

        btn_upload_images = (Button) view.findViewById(R.id.btn_upload_images);
        btn_upload_images.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
        });

        txt_display_upload_images = (TextView) view.findViewById(R.id.txt_display_upload_images);
        edt_title = (EditText) view.findViewById(R.id.title) ;
        edt_description = (EditText) view.findViewById(R.id.description) ;
        btn_post= (Button) view.findViewById(R.id.btn_add_post);

        edt_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(edt_title.getText().toString())){
                    edt_title.setError("Title cannot be blank");
                }else{
                    edt_title.setError(null);
                }
            }
        });

        edt_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(edt_description.getText().toString())){
                    edt_description.setError("Description cannot be blank");
                }else{
                    edt_description.setError(null);
                }
            }
        });

        btn_post.setOnClickListener(view12 -> {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Posting...");
            progressDialog.show();

            String title = edt_title.getText().toString();
            String description = edt_description.getText().toString();
            Date date=new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String current_time = formatter.format(date);

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)){
                Toast.makeText(getContext(),"Please fill out at least title and description fields.", Toast.LENGTH_LONG).show();
            }else{
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
                                    Log.d("YZHU_DATA_SUBMIT", "One image uploaded: " + url);
                                    if (current_index == uploaded_images_uri_list.size()-1){
                                        uploadPost(title, description, current_time, user_id, username, selected_category, uploaded_images_url);
                                    }
                                }));
                            }
                        }else{
                            uploadPost(title, description, current_time, user_id, username, selected_category, new ArrayList<String>());
                        }

                        Log.d("YZHU_DATA_SUBMIT", "title: " + title);
                        Log.d("YZHU_DATA_SUBMIT", "description: " + description);
                        Log.d("YZHU_DATA_SUBMIT", "publish_date: " + current_time);

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

        return view;
    }

    private void uploadPost(String title, String description, String current_time, String user_id, String username, String selected_category, List<String> uploaded_images_url) {
        for (int i = 0; i < uploaded_images_url.size(); i++) {
            Log.d("YZHU_DATA_SUBMIT", "Multiple images --> " + uploaded_images_url.get(i));
        }

        Map<String, Object> post_date_map = new HashMap<>();
        post_date_map.put("title", title);
        post_date_map.put("description", description);
        post_date_map.put("publish_date", current_time);
        post_date_map.put("user_id", user_id);
        post_date_map.put("user_name", username);
        post_date_map.put("category", selected_category);
        post_date_map.put("images", uploaded_images_url);

        DocumentReference new_post_document_ref = firestore.collection("posts").document();
        String new_post_document_id = new_post_document_ref.getId();
        new_post_document_ref.set(post_date_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Post successfully created!", Toast.LENGTH_LONG).show();
                        firestore.collection("users").document(auth.getCurrentUser().getUid()).update("posts", FieldValue.arrayUnion(new_post_document_id)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("YZHU_DATA_SUBMIT", "Error writing document:----", e);
                        Toast.makeText(getContext(), "Failed to create post!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });



    }

    private String getFileExtension(Uri url) {
        ContentResolver contentResolver = getContext().getContentResolver();
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

                    String display_text = "";
                    for (int i = 0; i < uploaded_images_uri_list.size(); i++) {

                        display_text += uploaded_images_uri_list.get(i).toString() + "\n";
                        Log.d("YZHU_IMAGE_SELECT", "Multiple images --> " + imagePath);
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
                Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}