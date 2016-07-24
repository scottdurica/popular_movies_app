package com.emrox_riprap.popularmovies.api;

import com.emrox_riprap.popularmovies.POJO.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by scott on 7/19/2016.
 */
public interface MoviesAPI {
    public static final String MOVIES_ENDPOINT_BASE_URL = "https://api.themoviedb.org/3/movie/";
    @GET("{sort_order}")
    Call<MoviesResponse> getMovies(@Path("sort_order") String sortOrder, @Query("api_key") String api_key, @Query("page") int page_index);
//    public void getMovies(@Path("sort_order") String sortOrder,@Query("api_key") String api_key, Callback<ArrayList<Movie>> response);
//   https://api.themoviedb.org/3/movie/popular?api_key=fd43f9d224ea4561836f90efc8f94828

}
