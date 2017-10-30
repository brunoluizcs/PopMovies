package com.bomcodigo.popmovies.api.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("author")
    private String author;

    @Expose
    @SerializedName("content")
    private String content;

    @Expose
    @SerializedName("url")
    private String url;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
