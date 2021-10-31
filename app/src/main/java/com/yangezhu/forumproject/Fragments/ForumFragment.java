package com.yangezhu.forumproject.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.adapter.PostListAdapter;
import com.yangezhu.forumproject.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private final String TAG = "FORUM_FRAGMENT_YZHU";

    private List<Post> posts_list;
    private PostListAdapter postListAdapter;
    private RecyclerView recycle_view_posts_entries_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        posts_list = new ArrayList<>();
        postListAdapter = new PostListAdapter(posts_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        recycle_view_posts_entries_list = (RecyclerView)view.findViewById(R.id.recycle_view_posts_entries_list);
        recycle_view_posts_entries_list.setHasFixedSize(true);

        // Set recyclerView orientation.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycle_view_posts_entries_list.setLayoutManager(linearLayoutManager);

        // Add devide bar
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycle_view_posts_entries_list.getContext(),
                linearLayoutManager.getOrientation());
        recycle_view_posts_entries_list.addItemDecoration(dividerItemDecoration);

        recycle_view_posts_entries_list.setAdapter(postListAdapter);

        firestore.collection("posts").orderBy("publish_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.d(TAG, "Error: " + error.getMessage());
                }

                for (DocumentChange documentChange: documentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){

                        Post post = documentChange.getDocument().toObject(Post.class);
                        posts_list.add(post);

                        postListAdapter.notifyDataSetChanged();

                        String name = documentChange.getDocument().getString("title");
                        List<String> images = (List<String>) documentChange.getDocument().get("images");
                        Gson gson = new Gson();

                        Log.d(TAG, "name: " + name);
                        Log.d(TAG, "images: " + gson.toJson(images));
                    }
                }
            }
        });

        return view;
    }
}