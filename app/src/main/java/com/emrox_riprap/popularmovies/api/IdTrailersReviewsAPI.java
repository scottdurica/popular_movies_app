package com.emrox_riprap.popularmovies.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by scott on 7/20/2016.
 */
public interface IdTrailersReviewsAPI {
    /*
    call with id in order to get runtime, add trailers and reviews onto call with append_to_response
    Example:
    https://api.themoviedb.org/3/movie/244786?api_key=fd43f9d224ea4561836f90efc8f94828&append_to_response=videos,reviews
     */

    final String TRAILERS_ENDPOINT_BASE_URL = "https://api.themoviedb.org/3/movie/";

    @GET("{movie_id}")
    Call<IdTrailersReviewsResponse> getTrailers(@Path("movie_id") String movieId, @Query("api_key") String api_key, @Query("append_to_response") String extra_calls);




}
