package com.emrox_riprap.popularmovies.POJO;

/**
 * Created by administrator on 6/27/16.
 */

public class Trailer {


    private String trailerId;
    private int movieId;
    private String name;
    private String key;
    private String type;


    public Trailer(String trailerId, int movieId, String title, String clipKey, String type) {
        this.trailerId = trailerId;
        this.movieId = movieId;
        this.name = title;
        this.key = clipKey;
        this.type = type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
