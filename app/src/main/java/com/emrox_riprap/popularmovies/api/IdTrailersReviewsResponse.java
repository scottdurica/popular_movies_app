package com.emrox_riprap.popularmovies.api;

import com.emrox_riprap.popularmovies.POJO.Review;
import com.emrox_riprap.popularmovies.POJO.Trailer;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by scott on 7/20/2016.
 */
public class IdTrailersReviewsResponse {


    private int id;
    private String key;
    private int runtime;
    @Expose
    private TrailersResults videos;
    private ReviewResults reviews;


    public IdTrailersReviewsResponse() {}

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public TrailersResults getVideos() {return videos;}

    public ReviewResults getReviews() {return reviews;}

    public int getRuntime() {return runtime;}



    /**
     * Create new objects because the array of trailers and reviews
     * are nested inside an object named 'videos' and 'reviews'
     */

    public class TrailersResults {

        private ArrayList<Trailer> results;

        public ArrayList<Trailer> getResults() {
            return results;
        }
    }

    public class ReviewResults {

        private ArrayList<Review> results;
        private int total_pages;
        private int total_results;

        public ArrayList<Review> getResults() {
            return results;
        }

        public int getTotal_pages() {
            return total_pages;
        }

        public int getTotal_results() {
            return total_results;
        }
    }
}
