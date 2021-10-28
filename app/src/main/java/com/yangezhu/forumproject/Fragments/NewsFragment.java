package com.yangezhu.forumproject.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {

    public static final String SELECTED_NEWS_URL = "SELECTED_NEWS_URL";
    private List<News> newsList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment News.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        new DownloadAndParseRSS().execute("https://globalnews.ca/toronto/feed/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);


        return view;
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