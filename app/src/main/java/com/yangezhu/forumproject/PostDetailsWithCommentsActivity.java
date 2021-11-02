package com.yangezhu.forumproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yangezhu.forumproject.adapter.PostImageAdapter;
import com.yangezhu.forumproject.adapter.PostListAdapter;
import com.yangezhu.forumproject.model.News;
import com.yangezhu.forumproject.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsWithCommentsActivity extends AppCompatActivity {

    private TextView post_title;
    private TextView post_username;
    private TextView post_publish_time;
    private TextView post_description;
    private RecyclerView recycle_view_post_images;
    private RecyclerView recycle_view_post_comments;

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

        Gson gson = new Gson();
        Post selected_post = gson.fromJson(getIntent().getStringExtra(PostListAdapter.SELECTED_POST), Post.class);

        post_title.setText(selected_post.getTitle());
        post_username.setText(selected_post.getUser_name());
        post_publish_time.setText(selected_post.getPublish_date().toString());
        post_description.setText(selected_post.getDescription());

        firestore = FirebaseFirestore.getInstance();

        post_images_list = selected_post.getImages();
        postImageAdapter = new PostImageAdapter(post_images_list, this);

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