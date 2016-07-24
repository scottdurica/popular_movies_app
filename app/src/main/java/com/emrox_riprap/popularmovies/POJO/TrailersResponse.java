package com.emrox_riprap.popularmovies.POJO;

import java.util.ArrayList;

/**
 * Created by scott on 7/20/2016.
 */
public class TrailersResponse {

    private ArrayList<Trailer> results;
    private int id;

    public TrailersResponse() {
    }

    public ArrayList<Trailer> getResults() {
        return results;
    }

    public void setResults(ArrayList<Trailer> results) {
        this.results = results;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
