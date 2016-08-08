package com.emrox_riprap.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emrox_riprap.popularmovies.POJO.Movie;
import com.emrox_riprap.popularmovies.api.MoviesResponse;
import com.emrox_riprap.popularmovies.adapters.MoviesAdapter;
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
public class MainActivityFragment extends Fragment implements Callback<MoviesResponse>, MoviesAdapter.RecyclerViewCLickListener {

    public static final String TAG = MainActivityFragment.class.getSimpleName();
    private int mPageIndex;
    private static String sSortOrder;

    public static final String ARGS_DATA_LIST = "data_list";
    public static final String ARGS_PAGE_INDEX = "page_index";
    public static final String ARGS_SORT_ORDER = "sort_order";

    MoviesAdapter mAdapter;
    private ArrayList<Movie> mMoviesList;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public static String getSortOrder() {
        return sSortOrder;
    }

    public interface Callback {
        void onMovieSelected(Movie movie);
    }

    public interface EmptyFavsListCallback {
        void favsMovieListEmpty();
    }

    @Override
    public void recyclerViewListClicked(Movie movie) {
        ((Callback) getActivity()).onMovieSelected(movie);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortOrder = getSortOrderFromPrefs();
        if (sSortOrder != null && !(sSortOrder.equals(getSortOrderFromPrefs()))) {
            if (MainActivity.sTablet) {
                //clear the fragment
                createAndShowBlankFragment();
            }
            //coming here from settings menu.  Sort order changed
            mPageIndex = 1;
            sSortOrder = sortOrder;
            fetchMovieData();
        }
        if (sSortOrder.equals("favorites")) {
            fetchMovieData();
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAdapter = new MoviesAdapter(getActivity(), this);
        if (mMoviesList == null) {
            mMoviesList = new ArrayList<Movie>();
        }
        //first time the page is being loaded
        if (savedInstanceState == null) {
            mPageIndex = 1;
            fetchMovieData();
        }
        //reloading the page
        else {
            ArrayList<Movie> list = savedInstanceState.getParcelableArrayList(ARGS_DATA_LIST);
            mMoviesList.clear();
            mMoviesList.addAll(list);
            mPageIndex = savedInstanceState.getInt(ARGS_PAGE_INDEX);
            mAdapter.swapData(mMoviesList);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_movies);
//        mRecyclerView.setHasFixedSize(true);
        sSortOrder = getSortOrderFromPrefs();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //this wont run until the Activity's onCreate() is done. Fragment.onCreateView() sometimes runs before
        //Activity.onCreate() is done.

        if (MainActivity.sTablet) {
            mLayoutManager = new GridLayoutManager(getActivity(), 3);

            createAndShowBlankFragment();

        } else {
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mPageIndex++;
                fetchMovieData();
            }
        });

    }

    private void createAndShowBlankFragment() {
        Fragment fragment = BlankFragment.newInstance(R.drawable.no_movie_placeholder);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment, "ss")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARGS_DATA_LIST, mAdapter.mDataList);
        outState.putInt(ARGS_PAGE_INDEX, mPageIndex);
        outState.putString(ARGS_SORT_ORDER, sSortOrder);
    }

    private String getSortOrderFromPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));
    }

    public void fetchMovieData() {
        String sortOrder = getSortOrderFromPrefs();
        if (!sortOrder.equals("favorites")) {
            //remove blank fragment instance from view if no longer on "favs list" screen
            if (getFragmentManager().findFragmentById(R.id.fragment) instanceof BlankFragment) {
                BlankFragment blankFrag = (BlankFragment) getFragmentManager().findFragmentById(R.id.fragment);
                getFragmentManager().beginTransaction().remove(blankFrag).commit();
            }
            if (sortOrder.equals("popular")) {
                getActivity().setTitle(getString(R.string.title_most_popular_movies));
            } else {
                getActivity().setTitle(getString(R.string.title_top_rated_movies));
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MoviesAPI.MOVIES_ENDPOINT_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);
            Call<MoviesResponse> call = moviesAPI.getMovies(sortOrder, getString(R.string.movie_db_api_key), mPageIndex);
            call.enqueue(this);
        } else {
            mMoviesList.clear();
            getActivity().setTitle(getString(R.string.title_favorites));
            Cursor cursor = getActivity().getContentResolver().query(MoviesContract.FavoritesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Movie m = new Movie();
                    m.setId(cursor.getInt(MoviesDbHelper.COL_MOVIE_ID));
                    m.setOverview(cursor.getString(MoviesDbHelper.COL_INDEX_OVERVIEW));
                    m.setPoster_path(cursor.getString(MoviesDbHelper.COL_INDEX_POSTER_PATH));
                    m.setRelease_date(cursor.getString(MoviesDbHelper.COL_INDEX_RELEASE_DATE));
                    m.setTitle(cursor.getString(MoviesDbHelper.COL_INDEX_TITLE));
                    m.setVote_average(cursor.getDouble(MoviesDbHelper.COL_INDEX_VOTER_AVERAGE));
                    mMoviesList.add(m);
                }
            } else {
                //set fragments view to empty list placeholder.
                ((EmptyFavsListCallback) getActivity()).favsMovieListEmpty();
            }
            mAdapter.swapData(mMoviesList);
        }
    }

    @Override
    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
        mMoviesList.clear();

        MoviesResponse moviesResponse = response.body();

        if (moviesResponse != null && moviesResponse.getResults() != null) {
            ArrayList<Movie> movieList = moviesResponse.getResults();
            for (Movie m : movieList) {
                String relPath = m.getPoster_path();
                final String POSTER_IMAGE_BASE = "http://image.tmdb.org/t/p";
                final String PATH_IMAGE_SIZE = (MainActivity.sTablet) ? "/w185" : "/w500";
                String posterPath = POSTER_IMAGE_BASE + PATH_IMAGE_SIZE + relPath;
                m.setPoster_path(posterPath);
            }
            if (movieList != null) {
                String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));
                if (sortOrder != sSortOrder) {
                    sSortOrder = sortOrder;
                }
                for (Movie m : movieList) {
                    mMoviesList.add(m);
                }
            }
            if (mPageIndex == 1) {
                mAdapter.swapData(mMoviesList);
            } else {
                mAdapter.appendData(mMoviesList);
            }
        }
    }

    @Override
    public void onFailure(Call<MoviesResponse> call, Throwable t) {
        Log.e("Cause: " + t, "Check internet connection");

    }

}
