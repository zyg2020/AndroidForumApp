package com.yangezhu.forumproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yangezhu.forumproject.adapter.PostImageAdapter;
import com.yangezhu.forumproject.adapter.PostListAdapter;
import com.yangezhu.forumproject.model.News;
import com.yangezhu.forumproject.model.Post;
import com.yangezhu.forumproject.utilities.DateUtilities;
import com.yangezhu.forumproject.utilities.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostDetailsWithCommentsActivity extends AppCompatActivity {

    private TextView post_title;
    private TextView post_username;
    private TextView post_publish_time;
    private TextView post_description;
    private RecyclerView recycle_view_post_images;
    private RecyclerView recycle_view_post_comments;
    private Button btn_reply;
    private EditText edt_input_comment_box;
    private Post selected_post;

    private FirebaseFirestore firestore;
    private PostImageAdapter postImageAdapter;

    private List<String> post_images_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details_with_comments);

        post_title = (TextView) findViewById(R.id.post_title);
        post_username = (TextView) findViewById(R.id.post_username);
        post_publish_time = (TextView) findViewById(R.id.post_publish_time);
        post_description = (TextView) findViewById(R.id.post_description);
        edt_input_comment_box = (EditText)findViewById(R.id.edt_input_comment_box);

        Gson gson = new Gson();
        selected_post = gson.fromJson(getIntent().getStringExtra(PostListAdapter.SELECTED_POST), Post.class);

        post_title.setText(selected_post.getTitle());
        post_username.setText(selected_post.getUser_name());
        post_publish_time.setText(DateUtilities.timeFormatterWithYear(selected_post.getPublish_date()));

        post_description.setText(selected_post.getDescription());

        firestore = FirebaseFirestore.getInstance();

        post_images_list = selected_post.getImages();
        postImageAdapter = new PostImageAdapter(post_images_list, this);

        btn_reply = (Button) findViewById(R.id.btn_reply);
        btn_reply.setOnClickListener(view -> {
            String comment_content = edt_input_comment_box.getText().toString().trim();

            if (TextUtils.isEmpty(comment_content)){
                Toast.makeText(this, "Please add comment frist.", Toast.LENGTH_LONG).show();
            }else{
                String post_id = selected_post.getPost_id();
                Date current_time =new Date();

                String username = SharedPreferencesManager.getInstance(this).getUsername();
                String userId = SharedPreferencesManager.getInstance(this).getUserId();
                if (TextUtils.isEmpty(username)){
                    Toast.makeText(this, "Error occur when retrieving the username", Toast.LENGTH_LONG).show();
                }else{
                    Log.d("USERNAME_YZHU", username);
                    Log.d("USERNAME_YZHU", userId);
                }

            }

        });

        initiateRecycleViewForPostImages();
        initiateRecycleViewForPostComments();
    }


    private void initiateRecycleViewForPostComments() {
    }

    private void initiateRecycleViewForPostImages() {
        recycle_view_post_images = (RecyclerView)findViewById(R.id.recycle_view_post_images);
        recycle_view_post_images.setHasFixedSize(true);

        // Set recyclerView orientation.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycle_view_post_images.setLayoutManager(linearLayoutManager);

        // Add devide bar
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycle_view_post_images.getContext(),
                linearLayoutManager.getOrientation());
        recycle_view_post_images.addItemDecoration(dividerItemDecoration);

         recycle_view_post_images.setAdapter(postImageAdapter);


    }
}