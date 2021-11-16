package com.yangezhu.forumproject.model;

import java.util.Date;
import java.util.List;

public class Comment {
    private String comment_id;
    private String content;
    private Date reply_date;
    private String user_name;
    private String name;
    private String user_id;

    private List<String> images;

    public Comment() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getReply_date() {
        return reply_date;
    }

    public void setReply_date(Date reply_date) {
        this.reply_date = reply_date;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Comment(String comment_id, String content, Date reply_date, String user_name, String user_id, List<String> images) {
        this.comment_id = comment_id;
        this.content = content;
        this.reply_date = reply_date;
        this.user_name = user_name;
        this.user_id = user_id;
        this.images = images;
    }
}
