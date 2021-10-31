package com.yangezhu.forumproject.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String title;
    private String description;
    private String publish_date;
    private String lastest_reply_date;
    private String user_name;
    private String user_id;
    private String category;
    private List<String> images;
    private String comments;

    public Post(String title, String description, String publish_date, String lastest_reply_date, String user_name, String user_id, String category, List<String> images, String comments) {
        this.title = title;
        this.description = description;
        this.publish_date = publish_date;
        this.lastest_reply_date = lastest_reply_date;
        this.user_name = user_name;
        this.user_id = user_id;
        this.category = category;
        this.images = images;
        this.comments = comments;
    }

    public Post() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public String getLastest_reply_date() {
        return lastest_reply_date;
    }

    public void setLastest_reply_date(String lastest_reply_date) {
        this.lastest_reply_date = lastest_reply_date;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
