package com.emrox_riprap.popularmovies.POJO;

import java.util.ArrayList;

/**
 * Created by scott on 7/23/2016.
 */
public class ReviewsResponse {

    private ArrayList<Review> results;

    public ReviewsResponse() {
    }

    public ArrayList<Review> getResults() {
        return results;
    }

    public void setResults(ArrayList<Review> results) {
        this.results = results;
    }
}
