package com.yangezhu.forumproject.model;


import android.text.Html;

import androidx.annotation.NonNull;

public class News {
    private String title;
    private String link;
    private String description;
    private String image_url;
    private String publish_date;

    public News(){

    }

    @NonNull
    @Override
    public String toString() {
        return "title: " + title + ", link: " + link + ", description: " + Html.fromHtml(description) + ", image_url: " + image_url + ", publish_date: " + publish_date;

    }

    public News(String title, String link, String description, String image_url, String date){
        this.title = title;
        this.link = link;
        this.description = description;
        this.image_url = image_url;
        this.publish_date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }
}

