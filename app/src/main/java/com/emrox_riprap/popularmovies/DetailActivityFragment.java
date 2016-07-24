package com.emrox_riprap.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emrox_riprap.popularmovies.POJO.Movie;
import com.emrox_riprap.popularmovies.POJO.Review;
import com.emrox_riprap.popularmovies.POJO.ReviewsResponse;
import com.emrox_riprap.popularmovies.POJO.Trailer;
import com.emrox_riprap.popularmovies.POJO.TrailersResponse;
import com.emrox_riprap.popularmovies.adapters.TrailerListAdapter;
import com.emrox_riprap.popularmovies.api.ReviewsAPI;
import com.emrox_riprap.popularmovies.api.TrailersAPI;
import com.emrox_riprap.popularmovies.db.MoviesContract;
import com.emrox_riprap.popularmovies.db.MoviesDbHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private Movie mMovie;
    private String movieId;
    private TrailerListAdapter mTrailerListAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTrailerInfo();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getActivity() instanceof MainActivity) {
            // coming from MainActivity so it's on a tablet
            mMovie = (Movie) getArguments().getParcelable(Movie.PARCEL_KEY);
            movieId = String.valueOf(mMovie.getId());
        } else if (getActivity().getIntent() != null && getActivity().getIntent().getParcelableExtra(Movie.PARCEL_KEY) != null) {
            mMovie = (Movie) getActivity().getIntent().getParcelableExtra(Movie.PARCEL_KEY);
            movieId = String.valueOf(mMovie.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mTrailerListAdapter = new TrailerListAdapter(getActivity());
        ListView trailerList = (ListView) rootView.findViewById(R.id.lv_trailers);
        View headerView = inflater.inflate(R.layout.header_detail_listview, trailerList, false);
//        addReviews(inflater, headerView);
        trailerList.addHeaderView(headerView, null, false);
        trailerList.setAdapter(mTrailerListAdapter);
        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mTrailerListAdapter.mDataList.get(position) instanceof Trailer){
                    //trailer was selected.  Take user to youtube to watch trailer
                    Trailer t = (Trailer)mTrailerListAdapter.mDataList.get(position);
//                String clipId = mTrailerListAdapter.mDataList.get(position).getClipKey();
                    String clipId = t.getClipKey();
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + clipId)));
                    watchYoutubeVideo(clipId);
                }else{
                    //reiview was selected.  Take user to web to read review
                    Log.e("REVIEW SELECTED"," F YEAH!");
                }

            }
        });

        TextView title = (TextView) rootView.findViewById(R.id.tv_title);
        TextView overview = (TextView) headerView.findViewById(R.id.tv_overview);
        TextView releaseDate = (TextView) headerView.findViewById(R.id.tv_year);
        TextView voterRating = (TextView) headerView.findViewById(R.id.tv_rating);
        final Button favButton = (Button) headerView.findViewById(R.id.b_add_as_favorite);
        if (movieIsInFavsDb()) {
            favButton.setText(getResources().getString(R.string.detail_b_display_text_remove));
        }

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //movie is NOT in db...add it to db
                if (favButton.getText().equals(getResources().getString(R.string.detail_b_display_text_mark))) {
                    ContentValues values = new ContentValues();
                    values.put(MoviesContract.FavoritesEntry._ID, mMovie.getId());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_TITLE, mMovie.getTitle());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_POSTER_PATH, mMovie.getPoster_path());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_RELEASE_DATE, mMovie.getRelease_date());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_VOTER_AVERAGE, mMovie.getVote_average());

                    Uri uri = getActivity().getContentResolver().insert(MoviesContract.FavoritesEntry.CONTENT_URI, values);
                    favButton.setText(getResources().getString(R.string.detail_b_display_text_remove));

                    if (uri != null) {
                        Toast.makeText(getActivity(), "Added to Favs!", Toast.LENGTH_LONG).show();


                    }
                } else {
                    //movie is already in db...delete it from db
                    String args = String.valueOf(mMovie.getId());
                    int success = getActivity().getContentResolver().delete(
                            MoviesContract.FavoritesEntry.CONTENT_URI,
                            MoviesContract.FavoritesEntry._ID + "=?",
                            new String[]{args});

                    if (success != 0) {
                        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_LONG).show();
                        favButton.setText(getResources().getString(R.string.detail_b_display_text_mark));
                    }
                }

            }
        });

        ImageView poster = (ImageView) rootView.findViewById(R.id.iv_detail_fragment_poster);
        //should never be null, but checking anyways...
        if (mMovie != null) {
            title.setText(mMovie.getTitle());
            overview.setText(mMovie.getOverview());
            Picasso.with(getActivity()).load(mMovie.getPoster_path()).into(poster);
            releaseDate.setText(mMovie.getRelease_date().substring(0, 4));
            voterRating.setText(String.valueOf(mMovie.getVote_average()) + "/10");
        }


        return rootView;
    }

//    private void addReviews(LayoutInflater inflater, View headerView) {
//        TextView title = new TextView(getActivity());
//        title.setText("Reivews");
//        LinearLayout container = (LinearLayout) headerView.findViewById(R.id.ll_reviews_container);
//        container.addView(title);
//    }

    //if movie is in favorites db then change button text/action for option to remove
    //instead of add to db
    private boolean movieIsInFavsDb() {
        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(MoviesDbHelper.COL_INDEX_ID);

                if (id == Integer.parseInt(movieId)) {
                    return true;
                }
            }

        }
        return false;
    }

    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    private void updateTrailerInfo() {
//        mTrailerListAdapter.mDataList.clear();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ReviewsAPI.REVIEWS_ENDPOINT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ReviewsAPI reviewsAPI = retrofit.create(ReviewsAPI.class);
        Call<ReviewsResponse> callback = reviewsAPI.getReviews(movieId, getString(R.string.movie_db_api_key));
        callback.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                ReviewsResponse reviewsResponse = response.body();
                ArrayList<Review> reviewList = reviewsResponse.getResults();
                if (reviewList != null){
                    for (Review r: reviewList){
                        mTrailerListAdapter.mDataList.add(r);
                    }
                }
                getTrailers();
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                getTrailers();
            }
        });



//        FetchMovieTrailersTask task = new FetchMovieTrailersTask();
//        String[]paramaters = new String[]{movieId};
//        task.execute(movieId);
    }
    void getTrailers(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TrailersAPI.TRAILERS_ENDPOINT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final TrailersAPI trailersAPI = retrofit.create(TrailersAPI.class);
        Call<TrailersResponse> call = trailersAPI.getTrailers(movieId, getString(R.string.movie_db_api_key));
        call.enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                TrailersResponse trailersResponse = response.body();
                ArrayList<Trailer> trailerList = trailersResponse.getResults();
//                Log.e("List count: ", " " + trailerList.size());
                if (trailerList != null) {
//                    mTrailerListAdapter.mDataList.clear();
                    for (Trailer t : trailerList) {
//                        Log.e("TYPE: ", t.getType());
                        if (t.getType().equalsIgnoreCase("Trailer")) {
                            mTrailerListAdapter.mDataList.add(t);
//                            Log.e("Size of list", "" + mTrailerListAdapter.mDataList.size());
                        }
                    }
//                    addTestTrailers();
                    mTrailerListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {

            }
        });
    }

//    public class FetchMovieTrailersTask extends AsyncTask<String, Void, ArrayList<Trailer>> {
//
//        public final String TAG = DetailActivityFragment.FetchMovieTrailersTask.class.getSimpleName();
//
//        @Override
//        protected ArrayList<Trailer> doInBackground(String... params) {
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            String mFetchedJsonStr = null;
//            String format = "json";
//            String apiKey = getString(R.string.movie_db_api_key);
//
//            try {
//
//                final String MOVIES_ENDPOINT_BASE_URL = "https://api.themoviedb.org/3";
//                final String PATH_MOVIE = "movie";
//                final String PATH_VIDEOS = "videos";
//                final String API_KEY_PARAM = "api_key";
//                String movieId = params[0];
//
//                Uri buildUri = Uri.parse(MOVIES_ENDPOINT_BASE_URL).buildUpon()
//                        .appendPath(PATH_MOVIE)
//                        .appendPath(movieId)
//                        .appendPath(PATH_VIDEOS)
//                        .appendQueryParameter(API_KEY_PARAM, apiKey)
//                        .build();
//                URL url = new URL(buildUri.toString());
////                https://api.themoviedb.org/3/movie/244786/videos?api_key=fd43f9d224ea4561836f90efc8f94828
////                Log.e("URL: ", buildUri.toString());
//                // Create the request to MovieDB and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                mFetchedJsonStr = buffer.toString();
//                try {
//                    return getMovieTrailerDataFromJson(mFetchedJsonStr);
//                } catch (JSONException e) {
//                    Log.e(TAG, e.getMessage(), e);
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Error ", e);
//                // If the code didn't successfully get the data, there's no point in attempting
//                // to parse it.
//                return null;
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(TAG, "Error closing stream", e);
//                    }
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Trailer> trailerList) {
//            if(trailerList !=null){
//                mTrailerListAdapter.mDataList.clear();
//                for (Trailer t: trailerList) {
//                    mTrailerListAdapter.mDataList.add(t);
//                }
//                mTrailerListAdapter.notifyDataSetChanged();
//            }
//        }
//
//        private ArrayList<Trailer> getMovieTrailerDataFromJson(String movieJsonStr)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String TMDB_LIST = "results";
//            ArrayList<Trailer> trailerList = new ArrayList<Trailer>();
//
//
//            JSONObject movieJson = new JSONObject(movieJsonStr);
//            int id = movieJson.getInt("id");
//
//
//            JSONArray videosArray = movieJson.getJSONArray(TMDB_LIST);
//
//
//
//            for(int i = 0; i < videosArray.length(); i++) {
//
//                JSONObject videoObj = videosArray.getJSONObject(i);
//                //if this video type is a Trailer
//                if (videoObj.getString("type").equalsIgnoreCase("Trailer")){
//                    String trailerId;
//                    String title;
//                    String clipKey;
//
//                    String type;
//
//
//                    trailerId = videoObj.getString("id");
//                    int movieId = id;
//                    title = videoObj.getString("name");
//                    clipKey = videoObj.getString("key");
//
//                    type = videoObj.getString("type");
//
//                    Trailer trailer = new Trailer(trailerId,movieId,title,clipKey,type);
//
//                    trailerList.add(trailer);
//
//                }
//
//
//            }
//
//            return trailerList;
//
//        }
//
//    }


}
