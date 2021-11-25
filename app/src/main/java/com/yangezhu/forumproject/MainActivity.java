package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.yangezhu.forumproject.Fragments.AddPostFragment;
import com.yangezhu.forumproject.Fragments.ForumFragment;
import com.yangezhu.forumproject.Fragments.HomeFragment;
import com.yangezhu.forumproject.Fragments.NewsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment;
    private RelativeLayout main_activity_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            setTheme(R.style.ForumProjectNight);

        }else{
            setTheme(R.style.ForumProjectDay);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        main_activity_container = (RelativeLayout)findViewById(R.id.main_activity_container);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            boolean render = true;
            String tag = "";
            switch (item.getItemId()){
                case R.id.bottom_nav_home:
                    //Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    if (selectedFragment != null && selectedFragment instanceof HomeFragment){
                        render = false;
                    }else{
                        selectedFragment = new HomeFragment();
                        tag = "HomeFragment";
                    }
                    break;
                case R.id.bottom_nav_forum:
                    //Toast.makeText(MainActivity.this, "Forum", Toast.LENGTH_SHORT).show();

                    if (selectedFragment != null && selectedFragment instanceof ForumFragment){
                        render = false;
                    }else{
                        selectedFragment = new ForumFragment();
                        tag = "ForumFragment";
                    }
                    break;
                case R.id.bottom_nav_news:
                    //Toast.makeText(MainActivity.this, "News", Toast.LENGTH_SHORT).show();

                    if (selectedFragment != null && selectedFragment instanceof NewsFragment){
                        render = false;
                    }else{
                        selectedFragment = new NewsFragment();
                        tag = "NewsFragment";
                    }
                    break;
//                case R.id.bottom_nav_add_post:
//                    //Toast.makeText(MainActivity.this, "Add Post", Toast.LENGTH_SHORT).show();
//
//                    if (selectedFragment != null && selectedFragment instanceof AddPostFragment){
//                        render = false;
//                    }else{
//                        selectedFragment = new AddPostFragment();
//                        tag = "AddPostFragment";
//                    }
//                    break;
            }

            if (selectedFragment != null && render){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, selectedFragment,tag).commit();
            }

            return true;
        });
        load_settings();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new HomeFragment(), "HomeFragment").commit();

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            main_activity_container.setBackgroundColor(Color.parseColor("#222222"));
            bottomNavigationView.setBackgroundColor(Color.parseColor("#222222"));

        }else{
            main_activity_container.setBackgroundColor(Color.parseColor("#ffffff"));
            bottomNavigationView.setBackgroundColor(Color.parseColor("#ffffff"));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu_main_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings_action_bar){
            Toast.makeText(MainActivity.this, "Settings clicked.", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }else if (item.getItemId() == R.id.logout_action_bar){
            Toast.makeText(MainActivity.this, "Logout clicked.", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(MainActivity.this, InitialLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}