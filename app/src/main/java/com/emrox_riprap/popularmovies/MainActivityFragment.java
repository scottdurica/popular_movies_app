package com.emrox_riprap.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.emrox_riprap.popularmovies.POJO.Movie;
import com.emrox_riprap.popularmovies.POJO.MoviesResponse;
import com.emrox_riprap.popularmovies.adapters.GridItemAdapter;
import com.emrox_riprap.popularmovies.api.MoviesAPI;
import com.emrox_riprap.popularmovies.db.MoviesContract;
import com.emrox_riprap.popularmovies.db.MoviesDbHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements Callback<MoviesResponse>{

    public static final String TAG = MainActivityFragment.class.getSimpleName();
    GridItemAdapter mGridItemAdapter;
    private int mPageIndex;
    private boolean mIsLoading;
    private String mSortOrder;
    private GridView mGridView;
    private int mGridViewPos;
    private boolean mLegitScroll;

    public static final String ARGS_DATA_LIST = "data_list";
    public static final String ARGS_PAGE_INDEX = "page_index";
    public static final String ARGS_SORT_ORDER = "sort_order";


//    public static final String ARGS_GRID_ITEM_POSITION = "gridview_pos";
    public interface Callback{
        public void onMovieSelected(Bundle bundle);
    }
    public MainActivityFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        //coming here from settings menu.  Sort order changed
        String sortOrder=getSortOrderFromPrefs();
        if (mSortOrder !=null  && mSortOrder != getSortOrderFromPrefs()){
            mPageIndex = 1;
            updateMovieData();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        if (mGridItemAdapter == null) {
            mGridItemAdapter = new GridItemAdapter(getActivity());
            //first time the page is being loaded
            if (savedInstanceState == null) {
                mPageIndex = 1;
                updateMovieData();
            }
            //reloading the page
            else {
                mGridItemAdapter.mDataList.clear();
                ArrayList<Movie> list = savedInstanceState.getParcelableArrayList(ARGS_DATA_LIST);
                mGridItemAdapter.mDataList.addAll(list);
                mPageIndex = savedInstanceState.getInt(ARGS_PAGE_INDEX);

            }
        }



    }

    private String getSortOrderFromPrefs(){
        return PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gv_movies);
//       mGridItemAdapter = new GridItemAdapter(getActivity());
        mGridView.setAdapter(mGridItemAdapter);
//        mGridView.setSelection(mGridViewPos);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    mLegitScroll = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mPageIndex < 20 && mLegitScroll){
                    int bottomPosition = firstVisibleItem + visibleItemCount;
                    if (bottomPosition > totalItemCount - 4 && !mIsLoading){

                        mPageIndex++;
                        updateMovieData();
                    }
                }

            }
        });
        mSortOrder = getSortOrderFromPrefs();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(new Intent(getActivity(),DetailActivity.class));
                Bundle bundle = new Bundle();
                bundle.putParcelable(Movie.PARCEL_KEY,mGridItemAdapter.mDataList.get(position));
//                intent.putExtras(bundle);
//                startActivity(intent);

                ((Callback)getActivity()).onMovieSelected(bundle);

            }
        });
//        mPageIndex = 1;
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARGS_DATA_LIST,mGridItemAdapter.mDataList);
        outState.putInt(ARGS_PAGE_INDEX, mPageIndex);
        outState.putString(ARGS_SORT_ORDER, mSortOrder);
//        outState.putInt(ARGS_GRID_ITEM_POSITION,mGridView.getFirstVisiblePosition());
    }


    private void updateMovieData() {
        mIsLoading = true;
        String sortOrder = getSortOrderFromPrefs();
        if (!sortOrder.equals("favorites")){
            if (sortOrder.equals("popular")){
                getActivity().setTitle(getString(R.string.title_most_popular_movies));
            }else{
                getActivity().setTitle(getString(R.string.title_top_rated_movies));
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MoviesAPI.MOVIES_ENDPOINT_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);
            Call<MoviesResponse> call = moviesAPI.getMovies(sortOrder,getString(R.string.movie_db_api_key),mPageIndex);
            call.enqueue(this);
        }else{
            getActivity().setTitle(getString(R.string.title_favorites));
            Cursor cursor = getActivity().getContentResolver().query(MoviesContract.FavoritesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                mGridItemAdapter.mDataList.clear();
                while (cursor.moveToNext()) {
                    Movie m = new Movie();
                    m.setId(cursor.getInt(MoviesDbHelper.COL_INDEX_ID));
                    m.setOverview(cursor.getString(MoviesDbHelper.COL_INDEX_OVERVIEW));
                    m.setPoster_path(cursor.getString(MoviesDbHelper.COL_INDEX_POSTER_PATH));
                    m.setRelease_date(cursor.getString(MoviesDbHelper.COL_INDEX_RELEASE_DATE));
                    m.setTitle(cursor.getString(MoviesDbHelper.COL_INDEX_TITLE));
                    m.setVote_average(cursor.getDouble(MoviesDbHelper.COL_INDEX_VOTER_AVERAGE));
                    mGridItemAdapter.mDataList.add(m);
                }
                mGridItemAdapter.notifyDataSetChanged();
            }
        }
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(MoviesAPI.MOVIES_ENDPOINT_BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);
//        Call<MoviesResponse> call = moviesAPI.getMovies(sortOrder,getString(R.string.movie_db_api_key));
//        call.enqueue(this);


//        if (sortOrder.equals("popular")) {
//            getActivity().setName(getString(R.string.title_most_popular_movies));
////            FetchMoviesTask fetchData = new FetchMoviesTask();
////            fetchData.execute(sortOrder, String.valueOf(mPageIndex));
//        } else if (sortOrder.equals("top_rated")) {
//            getActivity().setName(getString(R.string.title_top_rated_movies));
////            FetchMoviesTask fetchData = new FetchMoviesTask();
////            fetchData.execute(sortOrder, String.valueOf(mPageIndex));
//        } else if (sortOrder.equals("favorites")) {
//            getActivity().setName(getString(R.string.title_favorites));
//            Cursor cursor = getActivity().getContentResolver().query(MoviesContract.FavoritesEntry.CONTENT_URI,
//                    null,
//                    null,
//                    null,
//                    null);
//            if (cursor.moveToFirst()) {
//                mGridItemAdapter.mDataList.clear();
//                while (cursor.moveToNext()) {
//                    Movie m = new Movie();
//                    m.setId(cursor.getInt(MoviesDbHelper.COL_INDEX_ID));
//                    m.setOverview(cursor.getString(MoviesDbHelper.COL_INDEX_OVERVIEW));
//                    m.setPoster_path(cursor.getString(MoviesDbHelper.COL_INDEX_POSTER_PATH));
//                    m.setRelease_date(cursor.getString(MoviesDbHelper.COL_INDEX_RELEASE_DATE));
//                    m.setName(cursor.getString(MoviesDbHelper.COL_INDEX_TITLE));
//                    m.setVote_average(cursor.getDouble(MoviesDbHelper.COL_INDEX_VOTER_AVERAGE));
//                    mGridItemAdapter.mDataList.add(m);
//                }
//                mGridItemAdapter.notifyDataSetChanged();
//            }
//
//        }



    }

    @Override
    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
        MoviesResponse moviesResponse = response.body();
        ArrayList<Movie> movieList = moviesResponse.getResults();
        for (Movie m: movieList){
            String relPath = m.getPoster_path();
            final String POSTER_IMAGE_BASE = "http://image.tmdb.org/t/p";
            final String PATH_IMAGE_SIZE_PHONE = "/w500/";
            String posterPath = POSTER_IMAGE_BASE + PATH_IMAGE_SIZE_PHONE + relPath;
            m.setPoster_path(posterPath);
        }
        mIsLoading = false;
        if(movieList !=null){
            String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));
            if (sortOrder != mSortOrder){
                mGridItemAdapter.mDataList.clear();
                mSortOrder = sortOrder;
            }

            for (Movie m: movieList) {
                mGridItemAdapter.mDataList.add(m);
            }
            mGridItemAdapter.notifyDataSetChanged();

        }
//        Toast.makeText(getActivity(),"Movies Count: " + movieList.size(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(Call<MoviesResponse> call, Throwable t) {

    }


//    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
//
//        public final String TAG = FetchMoviesTask.class.getSimpleName();
//
//        @Override
//        protected ArrayList<Movie> doInBackground(String... params) {
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            String mFetchedJsonStr = null;
//            String apiKey = getString(R.string.movie_db_api_key);
////            https://api.themoviedb.org/3/movie/popular?api_key=fd43f9d224ea4561836f90efc8f94828
//            try {
//                final String MOVIES_ENDPOINT_BASE_URL = "https://api.themoviedb.org/3";
//                final String PATH_MOVIE = "movie";
//                final String PATH_SORT_BY_VALUE = params[0];
//                final String PARAM_PAGE = "page";
//                final String PARAM_PAGE_VALUE = params[1];
//                final String API_KEY_PARAM = "api_key";
//
//                //example of url needed
//                //  https://api.themoviedb.org/3/movie/popular?api_key=[API KEY]
//
//                Uri buildUri = Uri.parse(MOVIES_ENDPOINT_BASE_URL).buildUpon()
//                        .appendPath(PATH_MOVIE)
//                        .appendPath(PATH_SORT_BY_VALUE)
//                        .appendQueryParameter(API_KEY_PARAM, apiKey)
//                        .appendQueryParameter(PARAM_PAGE, PARAM_PAGE_VALUE)
//                        .build();
//                URL url = new URL(buildUri.toString());
//                Log.e(TAG,"URI IS: " + url);
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
//                    return getMovieDataFromJson(mFetchedJsonStr);
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
//        protected void onPostExecute(ArrayList<Movie> movieList) {
//
//                mIsLoading = false;
//            if(movieList !=null){
//                String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity())
//                        .getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));
//                if (sortOrder != mSortOrder){
//                    mGridItemAdapter.mDataList.clear();
//                    mSortOrder = sortOrder;
//                }
//
//                for (Movie m: movieList) {
//                    mGridItemAdapter.mDataList.add(m);
//                }
//                mGridItemAdapter.notifyDataSetChanged();
//
//            }
//        }
//
//        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String TMDB_LIST = "results";
//
//           //needed to complete path for image.
//            final String POSTER_IMAGE_BASE = "http://image.tmdb.org/t/p";
//            final String PATH_IMAGE_SIZE_PHONE = "/w500/";
////            final String PATH_IMAGE_SIZE_PHONE = "/w342/";
////            final String PATH_IMAGE_SIZE_PHONE = "/w185/";
//
//            JSONObject movieJson = new JSONObject(movieJsonStr);
//            JSONArray moviesArray = movieJson.getJSONArray(TMDB_LIST);
//
//
//            ArrayList<Movie> movieList = new ArrayList<Movie>();
//            for(int i = 0; i < moviesArray.length(); i++) {
//
//                int id;
//                String title;
//                String overview;
//                String posterPath;
//                String releaseDate;
//                double voterRating;
//
//                // Get the JSON object representing the day
//                JSONObject movieObj = moviesArray.getJSONObject(i);
//                id = movieObj.getInt("id");
//                title = movieObj.getString("title");
//                overview = movieObj.getString("overview");
//                String relPath = movieObj.getString("poster_path");
//                posterPath = POSTER_IMAGE_BASE + PATH_IMAGE_SIZE_PHONE + relPath;
//                releaseDate = movieObj.getString("release_date");
//                voterRating = movieObj.getDouble("vote_average");
//
//                Movie movie = new Movie(id,title,overview,posterPath,releaseDate,voterRating);
//
//                movieList.add(movie);
//
//            }
//
//            return movieList;
//
//        }
//
//    }



}
