package com.lap.application.beans;

import java.io.Serializable;

public class StartModuleResourcebean implements Serializable {
    String image_url;
    String title;
    String description;
    String video;
    String url;
    String page_cat;


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPage_cat() {
        return page_cat;
    }

    public void setPage_cat(String page_cat) {
        this.page_cat = page_cat;
    }
}
