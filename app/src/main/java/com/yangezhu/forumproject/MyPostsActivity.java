package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yangezhu.forumproject.adapter.MyPostsListAdapter;
import com.yangezhu.forumproject.adapter.PostListAdapter;
import com.yangezhu.forumproject.model.Post;
import com.yangezhu.forumproject.utilities.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private List<Post> posts_list;
    private MyPostsListAdapter postListAdapter;
    private RecyclerView recycle_view_my_posts;

    private final String TAG = "FORUM_FRAGMENT_YZHU";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Posts");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        posts_list = new ArrayList<>();
        posts_list.clear();
        postListAdapter = new MyPostsListAdapter(posts_list, this);

        recycle_view_my_posts = (RecyclerView)findViewById(R.id.recycle_view_my_posts);
        recycle_view_my_posts.setHasFixedSize(true);

        // Set recyclerView orientation.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycle_view_my_posts.setLayoutManager(linearLayoutManager);

        // Add devide bar
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        recycle_view_my_posts.addItemDecoration(dividerItemDecoration);

        recycle_view_my_posts.setAdapter(postListAdapter);

        userId = SharedPreferencesManager.getInstance(this).getUserId();
    }

    @Override
    protected void onResume() {
        Toast.makeText(MyPostsActivity.this, "Reload my posts.", Toast.LENGTH_SHORT).show();
        posts_list.clear();
        postListAdapter.notifyDataSetChanged();
        InitiateMyPostsList();
        super.onResume();
    }

    private void InitiateMyPostsList() {
        firestore.collection("posts").whereEqualTo("user_id", userId).orderBy("publish_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.d(TAG, "Error: " + error.getMessage());
                    return;
                }

                for (DocumentChange documentChange: documentSnapshots.getDocumentChanges()){
                    switch (documentChange.getType()) {
                        case ADDED:
                            Log.d(TAG, "Add successfully");
                            QueryDocumentSnapshot queryDocumentSnapshot = documentChange.getDocument();
                            Post post = queryDocumentSnapshot.toObject(Post.class);
                            post.setPost_id(queryDocumentSnapshot.getId());
                            posts_list.add(post);

                            postListAdapter.notifyDataSetChanged();

                            String name = documentChange.getDocument().getString("title");
                            List<String> images = (List<String>) documentChange.getDocument().get("images");
                            Gson gson = new Gson();

                            Log.d(TAG, "userId: " + userId);
                            Log.d(TAG, "name: " + post.getTitle());
                            Log.d(TAG, "description: " + post.getDescription());
                            Log.d(TAG, "images: " + gson.toJson(post.getImages()));
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified city: " + documentChange.getDocument().getData());
                            break;
                        case REMOVED:
                            Toast.makeText(MyPostsActivity.this, "trigger removed document change type", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Removed city: " + documentChange.getDocument().getData());
                            break;
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}