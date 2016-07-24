package com.emrox_riprap.popularmovies.POJO;

/**
 * Created by scott on 7/23/2016.
 */
public class Review extends ListItemObject{

    private String author;
    private String url;

    public Review() {
    }

    public Review(String author, String url) {
        this.author = author;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
