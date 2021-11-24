package com.yangezhu.forumproject.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.yangezhu.forumproject.MainActivity;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.RSSParseHandler;
import com.yangezhu.forumproject.WebViewNewsDetailsActivity;
import com.yangezhu.forumproject.adapter.NewsAdapter;
import com.yangezhu.forumproject.model.News;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NewsFragment extends Fragment {

    public static final String SELECTED_NEWS_URL = "SELECTED_NEWS_URL";
    private List<News> newsList;
    private DownloadAndParseRSS downloadAndParseRSS;
    private RelativeLayout container_relativeLayout;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            getActivity().setTheme(R.style.ForumProjectNight);
        }else{
            getActivity().setTheme(R.style.ForumProjectDay);
        }

        super.onCreate(savedInstanceState);
        bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation);
        downloadAndParseRSS = (DownloadAndParseRSS) new DownloadAndParseRSS().execute("https://globalnews.ca/winnipeg/feed/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        container_relativeLayout = (RelativeLayout)view.findViewById(R.id.container);
        String news_location = sp.getString("NEWS_FROM_LOCATION", "Winnipeg");

        ((MainActivity)getActivity()).setActionBarTitle(news_location + " News");


        return view;
    }

    @Override
    public void onResume() {
        load_settings();

        String news_location = sp.getString("NEWS_FROM_LOCATION", "Winnipeg");

        ((MainActivity)getActivity()).setActionBarTitle(news_location + " News");

        String url = "";
        switch (news_location) {
            case "BC":
                url = "https://globalnews.ca/bc/feed/";
                break;
            case "Calgary":
                url = "https://globalnews.ca/calgary/feed/";
                break;
            case "Edmonton":
                url = "https://globalnews.ca/edmonton/feed/";
                break;
            case "Lethbridge":
                url = "https://globalnews.ca/lethbridge/feed/";
                break;
            case "Regina":
                url = "https://globalnews.ca/regina/feed/";
                break;
            case "Saskatoon":
                url = "https://globalnews.ca/saskatoon/feed/";
                break;
            case "Winnipeg":
                url = "https://globalnews.ca/winnipeg/feed/";
                break;
            case "Toronto":
                url = "https://globalnews.ca/toronto/feed/";
                break;
            case "Montreal":
                url = "https://globalnews.ca/montreal/feed/";
                break;
            case "Halifax":
                url = "https://globalnews.ca/halifax/feed/";
                break;
            case "New Brunswick":
                url = "https://globalnews.ca/new-brunswick/feed/";
                break;
            case "Okanagan":
                url = "https://globalnews.ca/okanagan/feed/";
                break;
            case "London":
                url = "https://globalnews.ca/london/feed/";
                break;
            case "Hamilton":
                url = "https://globalnews.ca/hamilton/feed/";
                break;
            case "Guelph":
                url = "https://globalnews.ca/guelph/feed/";
                break;
            case "Peterborough":
                url = "https://globalnews.ca/peterborough/feed/";
                break;
            case "Kingston":
                url = "https://globalnews.ca/kingston/feed/";
                break;
            case "Barrie":
                url = "https://globalnews.ca/barrie/feed/";
                break;
            case "Ottawa":
                url = "https://globalnews.ca/ottawa/feed/";
                break;
            default:
                url = "https://globalnews.ca/winnipeg/feed/";
                break;
        }


        downloadAndParseRSS = (DownloadAndParseRSS) new DownloadAndParseRSS().execute(url);
        super.onResume();
    }

    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            container_relativeLayout.setBackgroundColor(Color.parseColor("#222222"));
            bottomNavigationView.setBackgroundColor(Color.parseColor("#222222"));
        }else{
            container_relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
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

    @Override
    public void onStop() {
        super.onStop();
        if (downloadAndParseRSS != null){
            downloadAndParseRSS.cancel(true);
        }
    }

    private class DownloadAndParseRSS extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            URL rssURL = null;
            HttpsURLConnection connection = null;
            InputStream inputStream = null;

            try {
                rssURL = new URL(args[0]);
                connection = (HttpsURLConnection) rssURL.openConnection();
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SAXParserFactory spf = SAXParserFactory.newInstance();

            try {
                SAXParser saxParser = spf.newSAXParser();
                // First argument is the data to parse
                // Second Argument is the directions on how to parse
                RSSParseHandler rssParseHandler = new RSSParseHandler();
                saxParser.parse(inputStream, rssParseHandler);
                newsList = rssParseHandler.getNewsList();

                Gson gson = new Gson();
                String newsListJson = gson.toJson(newsList);
                Log.d("ZHU_JSON_MSG", newsListJson);

            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            ListView mListView = (ListView) getView().findViewById(R.id.lv_news_fragment_list_view);

            NewsAdapter newsListAdapter = new NewsAdapter(getContext(), R.layout.news_layout, newsList);

            mListView.setAdapter(newsListAdapter);
            mListView.setOnItemClickListener(newsClick);
        }
    }

    AdapterView.OnItemClickListener newsClick = (adapterView, view, position, id) -> {
        News news = (News) adapterView.getItemAtPosition(position);
        Log.d("ZHU_JSON_MSG", news.toString());

            Intent intent = new Intent(getContext(), WebViewNewsDetailsActivity.class);
            Gson gson = new Gson();
            String url = news.getLink();
            intent.putExtra(SELECTED_NEWS_URL, url);

            getContext().startActivity(intent);
    };
}