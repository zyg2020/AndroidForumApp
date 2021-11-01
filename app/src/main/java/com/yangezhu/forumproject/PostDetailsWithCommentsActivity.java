package com.yangezhu.forumproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yangezhu.forumproject.adapter.PostListAdapter;
import com.yangezhu.forumproject.model.News;
import com.yangezhu.forumproject.model.Post;

public class PostDetailsWithCommentsActivity extends AppCompatActivity {

    private TextView post_title;
    private TextView post_username;
    private TextView post_publish_time;
    private TextView post_description;
    private RecyclerView recycle_view_post_images;
    private RecyclerView recycle_view_post_comments;

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
    }
}