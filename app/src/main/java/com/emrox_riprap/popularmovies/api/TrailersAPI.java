package com.emrox_riprap.popularmovies.api;

import com.emrox_riprap.popularmovies.POJO.TrailersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by scott on 7/20/2016.
 */
public interface TrailersAPI {

    public static final String TRAILERS_ENDPOINT_BASE_URL = "https://api.themoviedb.org/3/movie/";

    @GET("{movie_id}/videos")
    Call<TrailersResponse> getTrailers(@Path("movie_id") String movieId, @Query("api_key") String api_key);

    //https://api.themoviedb.org/3/movie/244786/videos?api_key=fd43f9d224ea4561836f90efc8f94828

}
