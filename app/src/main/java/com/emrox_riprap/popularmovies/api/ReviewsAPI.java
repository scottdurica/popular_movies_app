package com.emrox_riprap.popularmovies.api;

import com.emrox_riprap.popularmovies.POJO.ReviewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by scott on 7/23/2016.
 */
public interface ReviewsAPI {
    public static final String REVIEWS_ENDPOINT_BASE_URL = "https://api.themoviedb.org/3/movie/";
    @GET("{id}/reviews")
    Call<ReviewsResponse> getReviews(@Path("id") String movieId, @Query("api_key") String api_key);
}
//http://api.themoviedb.org/3/movie/{id}/reviews
