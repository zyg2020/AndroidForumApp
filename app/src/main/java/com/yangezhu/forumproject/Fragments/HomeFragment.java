package com.yangezhu.forumproject.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yangezhu.forumproject.InitialLoginActivity;
import com.yangezhu.forumproject.MainActivity;
import com.yangezhu.forumproject.MyPostsActivity;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.RegisterActivity;
import com.yangezhu.forumproject.utilities.SharedPreferencesManager;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private String username;
    private TextView initial_edit_text;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private Button btn_to_view_my_posts;
    private RelativeLayout container_relativeLayout;
    private BottomNavigationView bottomNavigationView;

    public HomeFragment() {
        // Required empty public constructor
    }

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

        String user_id = auth.getCurrentUser().getUid();
        firestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    username = document.getString ("username");

                    HomeFragment myFragment = (HomeFragment)getActivity().getSupportFragmentManager().findFragmentByTag("HomeFragment");
                    if (myFragment != null && myFragment.isVisible()) {
                        initial_edit_text = (TextView)getView().findViewById(R.id.initial_message);
                        initial_edit_text.setText("Hello, " + username);
                        load_settings();
                        SharedPreferencesManager.getInstance(getContext()).setUsername(username);
                        SharedPreferencesManager.getInstance(getContext()).setUserId(user_id);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).setActionBarTitle("Forum");
        load_settings();
        super.onResume();
    }

    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            container_relativeLayout.setBackgroundColor(Color.parseColor("#222222"));
            bottomNavigationView.setBackgroundColor(Color.parseColor("#222222"));

            textView1.setTextColor(Color.parseColor("#b5b5b5"));
            textView2.setTextColor(Color.parseColor("#b5b5b5"));
            textView3.setTextColor(Color.parseColor("#b5b5b5"));
            btn_to_view_my_posts.setTextColor(Color.parseColor("#222222"));
            initial_edit_text.setTextColor(Color.parseColor("#b5b5b5"));
        }else{
            container_relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            bottomNavigationView.setBackgroundColor(Color.parseColor("#ffffff"));

            textView1.setTextColor(Color.parseColor("#333333"));
            textView2.setTextColor(Color.parseColor("#333333"));
            textView3.setTextColor(Color.parseColor("#333333"));
            btn_to_view_my_posts.setTextColor(Color.parseColor("#ffffff"));
            initial_edit_text.setTextColor(Color.parseColor("#333333"));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initial_edit_text = (TextView)view.findViewById(R.id.initial_message);
        container_relativeLayout = (RelativeLayout)view.findViewById(R.id.container);
//        TextView initial_edit_text = view.findViewById(R.id.initial_message);
//        initial_edit_text.setText("Hello, " + username);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView3 = (TextView) view.findViewById(R.id.textView3);

        btn_to_view_my_posts = (Button) view.findViewById(R.id.btn_to_view_my_posts);
        btn_to_view_my_posts.setOnClickListener(view1 -> {
            // sdf
            Intent intent = new Intent(getContext(), MyPostsActivity.class);
            startActivity(intent);
        });
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if(signInAccount != null){
            textView1.setText("getDisplayName" + signInAccount.getDisplayName());
            textView2.setText("getEmail"+signInAccount.getEmail());
            textView3.setText("getId" + signInAccount.getId() + "\ngetFamilyName: " + signInAccount.getFamilyName() + "\nUID: " + auth.getCurrentUser().getUid());
            String user_id = auth.getCurrentUser().getUid();
            DocumentReference userRef = firestore.collection("users").document(user_id);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if ( task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()){


                            HashMap<String, Object> user_data_map = new HashMap<>();
                            user_data_map.put("name", signInAccount.getDisplayName());
                            user_data_map.put("email", signInAccount.getEmail());
                            user_data_map.put("id", user_id);
                            userRef.set(user_data_map);
                        }
                    }
                }
            });
        }

        return view;
    }

}