package com.yangezhu.forumproject.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yangezhu.forumproject.InitialLoginActivity;
import com.yangezhu.forumproject.MainActivity;
import com.yangezhu.forumproject.MyPostsActivity;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.RegisterActivity;
import com.yangezhu.forumproject.utilities.SharedPreferencesManager;

import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private String username;
    private String avatar_url;

    private TextView initial_edit_text;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private Button btn_to_view_my_posts;
    private Button btn_update_avatar;
    private RelativeLayout container_relativeLayout;
    private BottomNavigationView bottomNavigationView;

    private ImageView image_view_avatar;

    private Uri imageUri;
    private String uploaded_avatar_url;

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

                    String name = document.getString ("name");

                    String email = document.getString ("email");

                    avatar_url = document.getString("avatar");

                    HomeFragment myFragment = (HomeFragment)getActivity().getSupportFragmentManager().findFragmentByTag("HomeFragment");
                    if (myFragment != null && myFragment.isVisible()) {
                        initial_edit_text = (TextView)getView().findViewById(R.id.initial_message);
                        textView1 = (TextView) getView().findViewById(R.id.textView1);
                        textView2 = (TextView) getView().findViewById(R.id.textView2);

                        if (!TextUtils.isEmpty(username)){
                            initial_edit_text.setText("Hello, " + username);
                        }

                        if (!TextUtils.isEmpty(name)){
                            textView1.setText("Name:  " + name);
                        }

                        if (!TextUtils.isEmpty(email)){
                            textView2.setText("Email: "+ email);
                        }

                        if (avatar_url != null && !TextUtils.isEmpty(avatar_url)){
                            Picasso.get().load(avatar_url).placeholder(R.drawable.default_avatar).transform(new CropCircleTransformation() ).resize(200,200).into(image_view_avatar);
                        }else{
                            Picasso.get().load(R.drawable.default_avatar).into(image_view_avatar);
                        }

                        load_settings();
                        SharedPreferencesManager.getInstance(getContext()).setUsername(username);
                        SharedPreferencesManager.getInstance(getContext()).setUserId(user_id);
                    }
                }
            }
        });
    }

    private String getFileExtension(Uri url) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String type = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(url));

        return type;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            // image_view_avatar.setImageURI(imageUri);
            Picasso.get().load(imageUri).transform(new CropCircleTransformation() ).into(image_view_avatar);

            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploaded_avatar_url = uri.toString();

                            if (avatar_url != null && !TextUtils.isEmpty(avatar_url)){
                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(avatar_url);
                                storageReference.delete().addOnSuccessListener(unused -> {
                                    Toast.makeText(getContext(), "Delete Old Image successfully", Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("IMAGE_DELTE", avatar_url);
                                    Log.d("IMAGE_DELTE", e.getMessage());
                                });

                                avatar_url = uploaded_avatar_url;
                            }

                            firestore.collection("users").document(auth.getCurrentUser().getUid()).update("avatar", uploaded_avatar_url);
                            Toast.makeText(getContext(), "Update new Image successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });



        }else {
            Toast.makeText(getContext(), "Try again!", Toast.LENGTH_SHORT).show();

            // Reload current fragment
//            Fragment frg = null;
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//
//            frg = fragmentManager.findFragmentByTag("HomeFragment");
//            final FragmentTransaction ft = fragmentManager.beginTransaction();
//            ft.detach(frg);
//            ft.attach(frg);
//            ft.commit();
        }
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
            btn_update_avatar.setTextColor(Color.parseColor("#222222"));
            initial_edit_text.setTextColor(Color.parseColor("#b5b5b5"));
        }else{
            container_relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            bottomNavigationView.setBackgroundColor(Color.parseColor("#ffffff"));

            textView1.setTextColor(Color.parseColor("#333333"));
            textView2.setTextColor(Color.parseColor("#333333"));
            textView3.setTextColor(Color.parseColor("#333333"));
            btn_to_view_my_posts.setTextColor(Color.parseColor("#ffffff"));
            btn_update_avatar.setTextColor(Color.parseColor("#ffffff"));
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
        image_view_avatar = (ImageView) view.findViewById(R.id.image_view_avatar);

        btn_update_avatar = (Button) view.findViewById(R.id.btn_update_avatar);
        btn_update_avatar.setOnClickListener(view12 -> CropImage.activity().start(getContext(), this));

        btn_to_view_my_posts = (Button) view.findViewById(R.id.btn_to_view_my_posts);
        btn_to_view_my_posts.setOnClickListener(view1 -> {
            // sdf
            Intent intent = new Intent(getContext(), MyPostsActivity.class);
            startActivity(intent);
        });
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

        String user_id = auth.getCurrentUser().getUid();

        if(signInAccount != null){

            initial_edit_text.setText("Hello, " + signInAccount.getDisplayName());

            textView1.setText("Name:  " + signInAccount.getDisplayName());
            textView2.setText("Email: "+signInAccount.getEmail());
            // textView3.setText("getId" + signInAccount.getId() + "\ngetFamilyName: " + signInAccount.getFamilyName() + "\nUID: " + auth.getCurrentUser().getUid());

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
        }else{

        }

        return view;
    }

}