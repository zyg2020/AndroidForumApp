package com.yangezhu.forumproject.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.model.News;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter {

    private Context context;
    private int resourceID;
    private ImageView image;

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
        TextView date = convertView.findViewById(R.id.post_date);
        image = convertView.findViewById(R.id.post_image);
        TextView news_description = convertView.findViewById(R.id.post_description);

        // set text color
        String text_color = load_text_color();
        title.setTextColor(Color.parseColor(text_color));
        date.setTextColor(Color.parseColor(text_color));
        news_description.setTextColor(Color.parseColor(text_color));

        // set text fize
        float font_size = load_font_size_settings();
        date.setTextSize(font_size);
        news_description.setTextSize(font_size);

        String image_url = news.getImage_url();
        if (TextUtils.isEmpty(image_url)){
            image.setVisibility(View.GONE);
        }else{
            image.setVisibility(View.VISIBLE);
            Picasso.get().load(news.getImage_url()).resize(100,100).into(image);
        }

        title.setText(news.getTitle());
        news_description.setText(Html.fromHtml(news.getDescription()));

        String dateValue = news.getPublish_date();
        SimpleDateFormat date_format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        DateFormat display_date_format = new SimpleDateFormat("MMMM dd, yyyy HH:mm");

        Date dateStringToDateObj = null;
        String display_date = dateValue;
        try {
            dateStringToDateObj = date_format.parse(dateValue);
            display_date = display_date_format.format(dateStringToDateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        date.setText(display_date);

//        date.setTextSize(font_size);
//        link.setTextSize(font_size);
//
//        Log.d("ZHU_JSON_MSG", news.toString());

        return convertView;
    }

    private String load_text_color(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean chk_night = sp.getBoolean("NIGHT", false);
        String text_color = "";
        if (chk_night){
            text_color = "#b5b5b5";
        }else{
            text_color = "#333333";
        }
        return text_color;
    }

    private float load_font_size_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String font_size = sp.getString("FONT_SIZE", "false");
        float settings_font_size = 10;
        if ("Small".equals(font_size)){
            settings_font_size=12;
        }else if ("Medium".equals(font_size)){
            settings_font_size=16;
        }else if ("Large".equals(font_size)){
            settings_font_size=20;
        }

        return settings_font_size;
    }
}
