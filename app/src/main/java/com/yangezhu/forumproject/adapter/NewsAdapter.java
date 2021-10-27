package com.yangezhu.forumproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.model.News;

import java.util.List;

public class NewsAdapter extends ArrayAdapter {

    private Context context;
    private int resourceID;

    public NewsAdapter(@NonNull Context context, int resource, @NonNull List<News> newsList) {
        super(context, resource, newsList);

        this.context = context;
        this.resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resourceID, parent, false);
        }

        // float font_size = load_font_size_settings();

        News news = (News) getItem(position);

        TextView title = convertView.findViewById(R.id.news_title);
        TextView date = convertView.findViewById(R.id.news_date);
        TextView link = convertView.findViewById(R.id.news_link);

        title.setText(news.getTitle());
        date.setText(news.getPublish_date());
        link.setText(news.getLink());

//        date.setTextSize(font_size);
//        link.setTextSize(font_size);
//
//        Log.d("ZHU_JSON_MSG", news.toString());

        return convertView;
    }
}
