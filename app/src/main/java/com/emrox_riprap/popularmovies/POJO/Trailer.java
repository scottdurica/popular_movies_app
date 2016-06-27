package com.emrox_riprap.popularmovies.POJO;

/**
 * Created by administrator on 6/27/16.
 */

public class Trailer {


    private String trailerId;
    private int movieId;
    private String title;
    private String clipKey;

    public Trailer(String trailerId, int movieId, String title, String clipKey) {
        this.trailerId = trailerId;
        this.movieId = movieId;
        this.title = title;
        this.clipKey = clipKey;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClipKey() {
        return clipKey;
    }

    public void setClipKey(String clipKey) {
        this.clipKey = clipKey;
    }
}
