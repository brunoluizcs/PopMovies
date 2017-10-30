package com.bomcodigo.popmovies.api.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Video {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("iso_639_1")
    private String iso6491;

    @Expose
    @SerializedName("iso_3166_1")
    private String iso31661;

    @Expose
    @SerializedName("key")
    private String key;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("site")
    private String site;

    @Expose
    @SerializedName("size")
    private String size;

    @Expose
    @SerializedName("type")
    private String type;

    public String getId() {
        return id;
    }

    public String getIso6491() {
        return iso6491;
    }

    public String getIso31661() {
        return iso31661;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}
