package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yangezhu.forumproject.Fragments.ForumFragment;
import com.yangezhu.forumproject.Fragments.HomeFragment;
import com.yangezhu.forumproject.Fragments.NewsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.bottom_nav_home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.bottom_nav_forum:
                        Toast.makeText(MainActivity.this, "Forum", Toast.LENGTH_SHORT).show();
                        selectedFragment = new ForumFragment();
                        break;
                    case R.id.bottom_nav_news:
                        Toast.makeText(MainActivity.this, "News", Toast.LENGTH_SHORT).show();
                        selectedFragment = new NewsFragment();
                        break;
                }

                if (selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, selectedFragment).commit();
                }

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new HomeFragment()).commit();
    }
}