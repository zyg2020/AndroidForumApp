package com.yangezhu.forumproject.Fragments;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yangezhu.forumproject.MainActivity;
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

    private FloatingActionButton btn_add_post;
    private RelativeLayout container_relativeLayout;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView forumTypeNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            getActivity().setTheme(R.style.ForumProjectNight);
        }else{
            getActivity().setTheme(R.style.ForumProjectDay);
        }

        super.onCreate(savedInstanceState);
        bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        posts_list = new ArrayList<>();
        postListAdapter = new PostListAdapter(posts_list, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        btn_add_post = (FloatingActionButton)view.findViewById(R.id.btn_add_post);
        btn_add_post.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.fragment_container_view, new AddPostFragment()).addToBackStack("").commit();
        });

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

        loadPosts("Used Items");

        forumTypeNavigationView = (BottomNavigationView)view.findViewById(R.id.forumTypeNavigationView);

        forumTypeNavigationView.setOnNavigationItemSelectedListener(item -> {

            boolean render = true;
            String tag = "";
            switch (item.getItemId()){
                case R.id.used_items_type:
                    Toast.makeText(getContext(), "used_items_type", Toast.LENGTH_SHORT).show();
                    loadPosts("Used Items");
                    break;
                case R.id.used_cars_type:
                    Toast.makeText(getContext(), "used_cars_type", Toast.LENGTH_SHORT).show();
                    loadPosts("Used Cars");
                    break;
                case R.id.rent_type:
                    Toast.makeText(getContext(), "rent_type", Toast.LENGTH_SHORT).show();
                    loadPosts("Rent");
                    break;
                case R.id.marketing_type:
                    Toast.makeText(getContext(), "marketing_type", Toast.LENGTH_SHORT).show();
                    loadPosts("Marketing");
                    break;
            }

            return true;
        });

        container_relativeLayout = (RelativeLayout)view.findViewById(R.id.container);
        return view;
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).setActionBarTitle("View Posts");
        load_settings();
        if (postListAdapter!=null){
            postListAdapter.notifyDataSetChanged();
        }
        //loadPosts("Used Items");
//        if (((MainActivity) getActivity()).ifLoad){
//            Fragment frg = null;
//            frg = getActivity().getSupportFragmentManager().findFragmentByTag("ForumFragment");
//            final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//            ft.detach(frg);
//            ft.attach(frg);
//            ((MainActivity) getActivity()).ifLoad = false;
//            ft.commit();
//        }

        super.onResume();
    }

    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            container_relativeLayout.setBackgroundColor(Color.parseColor("#222222"));
            bottomNavigationView.setBackgroundColor(Color.parseColor("#222222"));
            forumTypeNavigationView.setBackgroundColor(Color.parseColor("#222222"));
        }else{
            container_relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            forumTypeNavigationView.setBackgroundColor(Color.parseColor("#ffffff"));
            bottomNavigationView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        String orien = sp.getString("ORIENTATION", "false");
        if ("Auto".equals(orien)){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        }else if ("Portrait".equals(orien)){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if ("Landscape".equals(orien)){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void loadPosts(String category) {
        if (posts_list != null){
            posts_list.clear();
            postListAdapter.notifyDataSetChanged();
        }

        firestore.collection("posts").whereEqualTo("category", category).orderBy("publish_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.d(TAG, "Error: " + error.getMessage());
                    return;
                }

                for (DocumentChange documentChange: documentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        QueryDocumentSnapshot queryDocumentSnapshot = documentChange.getDocument();
                        Post post = queryDocumentSnapshot.toObject(Post.class);
                        post.setPost_id(queryDocumentSnapshot.getId());
                        posts_list.add(post);

                        postListAdapter.notifyDataSetChanged();

                        String name = documentChange.getDocument().getString("title");
                        List<String> images = (List<String>) documentChange.getDocument().get("images");
                        Gson gson = new Gson();

                        Log.d(TAG, "name: " + name);
                        Log.d(TAG, "category: " + documentChange.getDocument().getString("category"));
                        Log.d(TAG, "images: " + gson.toJson(images));
                    }
                }
            }
        });
    }
}