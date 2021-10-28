package com.yangezhu.forumproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.yangezhu.forumproject.Fragments.NewsFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebViewNewsDetailsActivity extends AppCompatActivity {

    private WebView web_view_load_news;
    private String data_from_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_news_details);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("News Browser");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        web_view_load_news = (WebView)findViewById(R.id.web_view_load_news);

        Intent intent = getIntent();
        String url = intent.getStringExtra(NewsFragment.SELECTED_NEWS_URL);

        OkHttpClient mOkHttpClient = new OkHttpClient();                                 //1、
        Request request = new Request.Builder()
                .url(url)
                .build();                                                                //2、
        Call call = mOkHttpClient.newCall(request);                                      //3、
        call.enqueue(new Callback()                                                      //4、
        {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("OkHttponFailure",e.toString());
                // Toast.makeText(WebViewNewsDetailsActivity.this, "Request Failed. Go back and try again.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    data_from_url = "";
                    try {
                        data_from_url = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(data_from_url)){
                        data_from_url = android.util.Base64.encodeToString(data_from_url.getBytes(), Base64.NO_PADDING);

                    }
                    WebViewNewsDetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            web_view_load_news.loadData(data_from_url, "text/html", "base64");
                        }
                    });

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