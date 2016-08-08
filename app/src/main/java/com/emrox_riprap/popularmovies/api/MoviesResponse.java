package com.emrox_riprap.popularmovies.api;

import com.emrox_riprap.popularmovies.POJO.Movie;

import java.util.ArrayList;

/**
 * Created by scott on 7/19/2016.
 */
public class MoviesResponse {

    private int page;
    private ArrayList<Movie> results;
    private int total_results;
    private int total_pages;

    public MoviesResponse() {
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<Movie> getResults() {
        return results;
    }

    public void setResults(ArrayList<Movie> results) {
        this.results = results;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }
}
