package com.emrox_riprap.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.emrox_riprap.popularmovies.POJO.Movie;
import com.emrox_riprap.popularmovies.adapters.GridItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    GridItemAdapter mGridItemAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gv_movies);
        mGridItemAdapter = new GridItemAdapter(getActivity());
        gridView.setAdapter(mGridItemAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(new Intent(getActivity(),DetailActivity.class));
                Bundle bundle = new Bundle();
                bundle.putParcelable(Movie.PARCEL_KEY,mGridItemAdapter.mDataList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        return rootView;
    }

    private void updateMovieData() {
        String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));

        if(sortOrder.equals("popular")){
            getActivity().setTitle(getString(R.string.title_most_popular_movies));
        }else if (sortOrder.equals("top_rated")){
            getActivity().setTitle(getString(R.string.title_top_rated_movies));
        }

        FetchMoviesTask fetchData = new FetchMoviesTask();

        fetchData.execute(sortOrder);
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        public final String TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String mFetchedJsonStr = null;
            String apiKey = getString(R.string.movie_db_api_key);

            try {
                final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";
                final String PATH_MOVIE = "movie";
                final String PATH_SORT_BY_VALUE = params[0];
                final String API_KEY_PARAM = "api_key";

                //example of url needed
                //  https://api.themoviedb.org/3/movie/popular?api_key=[API KEY]

                Uri buildUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(PATH_MOVIE)
                        .appendPath(PATH_SORT_BY_VALUE)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                URL url = new URL(buildUri.toString());

                // Create the request to MovieDB and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                mFetchedJsonStr = buffer.toString();
                try {
                    return getMovieDataFromJson(mFetchedJsonStr);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieList) {
            if(movieList !=null){
                mGridItemAdapter.mDataList.clear();
                for (Movie m: movieList) {
                    mGridItemAdapter.mDataList.add(m);
                }
                mGridItemAdapter.notifyDataSetChanged();
            }
        }

        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_LIST = "results";

           //needed to complete path for image.
            final String POSTER_IMAGE_BASE = "http://image.tmdb.org/t/p";
            final String PATH_IMAGE_SIZE_PHONE = "/w500/";
//            final String PATH_IMAGE_SIZE_PHONE = "/w342/";
//            final String PATH_IMAGE_SIZE_PHONE = "/w185/";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray moviesArray = movieJson.getJSONArray(TMDB_LIST);


            ArrayList<Movie> movieList = new ArrayList<Movie>();
            for(int i = 0; i < moviesArray.length(); i++) {

                int id;
                String title;
                String overview;
                String posterPath;
                String releaseDate;
                double voterRating;

                // Get the JSON object representing the day
                JSONObject movieObj = moviesArray.getJSONObject(i);
                id = movieObj.getInt("id");
                title = movieObj.getString("title");
                overview = movieObj.getString("overview");
                String relPath = movieObj.getString("poster_path");
                posterPath = POSTER_IMAGE_BASE + PATH_IMAGE_SIZE_PHONE + relPath;
                releaseDate = movieObj.getString("release_date");
                voterRating = movieObj.getDouble("vote_average");

                Movie movie = new Movie(id,title,overview,posterPath,releaseDate,voterRating);

                movieList.add(movie);

            }

            return movieList;

        }

    }



}
