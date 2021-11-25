package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yangezhu.forumproject.adapter.CommentListAdapter;
import com.yangezhu.forumproject.adapter.PostImageAdapter;
import com.yangezhu.forumproject.adapter.PostListAdapter;
import com.yangezhu.forumproject.model.Comment;
import com.yangezhu.forumproject.model.News;
import com.yangezhu.forumproject.model.Post;
import com.yangezhu.forumproject.utilities.DateUtilities;
import com.yangezhu.forumproject.utilities.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

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

    private Button delete;
    private Button update;
    private ImageView author_avatar;

    private RelativeLayout container_layout;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private PostImageAdapter postImageAdapter;
    private CommentListAdapter commentListAdapter;

    private List<String> post_images_list;
    private List<Comment> comments_list;

    private final String TAG = "POST_WITH_COMMENTS_YZHU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details_with_comments);

        container_layout = (RelativeLayout) findViewById(R.id.container);

        post_title = (TextView) findViewById(R.id.post_title);
        post_username = (TextView) findViewById(R.id.post_username);
        post_publish_time = (TextView) findViewById(R.id.post_publish_time);
        post_description = (TextView) findViewById(R.id.post_description);
        edt_input_comment_box = (EditText)findViewById(R.id.edt_input_comment_box);
        author_avatar= (ImageView) findViewById(R.id.author_avatar);

        Gson gson = new Gson();
        selected_post = gson.fromJson(getIntent().getStringExtra(PostListAdapter.SELECTED_POST), Post.class);

        post_username.setText(selected_post.getUser_name());
        post_publish_time.setText(DateUtilities.timeFormatterWithFullMonthFirst(selected_post.getPublish_date()));

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(selected_post.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        // auth.getCurrentUser().getUid()

        firestore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String avatar_url = document.getString ("avatar");
                    Picasso.get().load(avatar_url).resize(50,50).placeholder(R.drawable.default_avatar).transform(new CropCircleTransformation() ).into(author_avatar);
                }
            }
        });

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
                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
                String name = "";
                if (signInAccount != null){
                    name = signInAccount.getDisplayName();
                }

//                if (TextUtils.isEmpty(username)){
//                    Toast.makeText(this, "Error occur when retrieving the username", Toast.LENGTH_LONG).show();
//                }else{
//                    Log.d("USERNAME_YZHU", username);
//                    Log.d("USERNAME_YZHU", userId);
//                    uploadComment(username, name, userId, comment_content, current_time, post_id);
//                }
                Log.d("USERNAME_YZHU", username);
                Log.d("USERNAME_YZHU", userId);
                uploadComment(username, name, userId, comment_content, current_time, post_id);

            }

        });


        initiateRecycleViewForPostComments();

        delete = (Button) findViewById(R.id.delete);
        update = (Button)findViewById(R.id.update);

        Intent intent = getIntent();
        String from_which_activity = intent.getStringExtra("Activity");
        if (from_which_activity.equals("FROM_MY_POSTS")){
            delete.setVisibility(View.VISIBLE);
            update.setVisibility(View.VISIBLE);

            delete.setOnClickListener(view -> {

                firestore.collection("posts").document(selected_post.getPost_id())
                        .delete()
                        .addOnSuccessListener(unused -> {
//                        posts_list.remove(position);
//                        notifyItemRemoved(position);
                          Toast.makeText(PostDetailsWithCommentsActivity.this, "Delete successfully ", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(PostDetailsWithCommentsActivity.this, "Error deleting document", Toast.LENGTH_SHORT).show();
                        });
            });

            update.setOnClickListener(view -> {
                Toast.makeText(this, "update clicked", Toast.LENGTH_SHORT).show();

                Intent intent_update = new Intent(PostDetailsWithCommentsActivity.this, UpdatePostActivity.class);
                intent_update.putExtra("SELECTED_POST", getIntent().getStringExtra(PostListAdapter.SELECTED_POST));
                intent_update.putExtra("Activity", "FROM_MY_POST_DETAILS");
                PostDetailsWithCommentsActivity.this.startActivity(intent_update);
            });
        }
    }

    @Override
    protected void onResume() {
        load_settings();
        super.onResume();
        reloadPost();
    }

    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            container_layout.setBackgroundColor(Color.parseColor("#222222"));

            post_title.setTextColor(Color.parseColor("#b5b5b5"));
            post_username.setTextColor(Color.parseColor("#b5b5b5"));
            post_publish_time.setTextColor(Color.parseColor("#b5b5b5"));
            post_description.setTextColor(Color.parseColor("#b5b5b5"));
            btn_reply.setTextColor(Color.parseColor("#b5b5b5"));
            delete.setTextColor(Color.parseColor("#b5b5b5"));
            update.setTextColor(Color.parseColor("#b5b5b5"));
            edt_input_comment_box.setTextColor(Color.parseColor("#b5b5b5"));
        }else{
            container_layout.setBackgroundColor(Color.parseColor("#ffffff"));

            post_title.setTextColor(Color.parseColor("#333333"));
            post_username.setTextColor(Color.parseColor("#333333"));
            post_publish_time.setTextColor(Color.parseColor("#333333"));
            post_description.setTextColor(Color.parseColor("#333333"));
            btn_reply.setTextColor(Color.parseColor("#ffffff"));
            delete.setTextColor(Color.parseColor("#ffffff"));
            update.setTextColor(Color.parseColor("#ffffff"));
            edt_input_comment_box.setTextColor(Color.parseColor("#333333"));
        }

        String orien = sp.getString("ORIENTATION", "false");
        if ("Auto".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        }else if ("Portrait".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if ("Landscape".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void reloadPost() {

        firestore.collection("posts").document(selected_post.getPost_id()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                Post new_post = document.toObject(Post.class);

                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(new_post.getTitle());
                }

                post_title.setText(new_post.getTitle());
                post_description.setText(new_post.getDescription());

                post_images_list = new_post.getImages();
                postImageAdapter = new PostImageAdapter(post_images_list, PostDetailsWithCommentsActivity.this);

                initiateRecycleViewForPostImages();
            }
        });


    }

    private void uploadComment(String username, String name, String userId, String comment_content, Date current_time, String post_id) {
        Map<String, Object> comment_data = new HashMap<>();
        comment_data.put("user_name", username);
        comment_data.put("name", name);
        comment_data.put("user_id", userId);
        comment_data.put("reply_date", current_time);
        comment_data.put("content", comment_content);

        DocumentReference new_comment = firestore.collection("posts").document(post_id).collection("comments").document();
        new_comment.set(comment_data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PostDetailsWithCommentsActivity.this, "Comment successfully created!", Toast.LENGTH_LONG).show();
                edt_input_comment_box.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("YZHU_DATA_SUBMIT", "Error writing document:----", e);
                Toast.makeText(PostDetailsWithCommentsActivity.this, "Failed to create comment!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initiateRecycleViewForPostComments() {
        comments_list = new ArrayList<>();
        commentListAdapter = new CommentListAdapter(comments_list, this);

        recycle_view_post_comments = (RecyclerView)findViewById(R.id.recycle_view_post_comments);
        recycle_view_post_comments.setHasFixedSize(true);

        // Set recyclerView orientation.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycle_view_post_comments.setLayoutManager(linearLayoutManager);

        // Add devide bar
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycle_view_post_comments.getContext(),
//                linearLayoutManager.getOrientation());
//        recycle_view_post_comments.addItemDecoration(dividerItemDecoration);

        recycle_view_post_comments.setAdapter(commentListAdapter);

        firestore.collection("posts").document(selected_post.getPost_id()).collection("comments").orderBy("reply_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.d(TAG, "Error: " + error.getMessage());
                }

                for (DocumentChange documentChange: value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        QueryDocumentSnapshot queryDocumentSnapshot = documentChange.getDocument();
                        Comment comment = queryDocumentSnapshot.toObject(Comment.class);
                        comment.setComment_id(queryDocumentSnapshot.getId());
                        comments_list.add(comment);

                        commentListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });
    }

    private void initiateRecycleViewForPostImages() {
        recycle_view_post_images = (RecyclerView)findViewById(R.id.recycle_view_post_images);
        recycle_view_post_images.setHasFixedSize(true);

        // Set recyclerView orientation.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycle_view_post_images.setLayoutManager(linearLayoutManager);

        // Add devide bar
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycle_view_post_images.getContext(),
//                linearLayoutManager.getOrientation());
//        recycle_view_post_images.addItemDecoration(dividerItemDecoration);

         recycle_view_post_images.setAdapter(postImageAdapter);


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