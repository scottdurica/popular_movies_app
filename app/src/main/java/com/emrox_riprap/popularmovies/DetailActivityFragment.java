package com.emrox_riprap.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.emrox_riprap.popularmovies.POJO.Movie;
import com.emrox_riprap.popularmovies.POJO.Review;
import com.emrox_riprap.popularmovies.POJO.ReviewChildObject;
import com.emrox_riprap.popularmovies.POJO.Trailer;
import com.emrox_riprap.popularmovies.adapters.ReviewsAdapter;
import com.emrox_riprap.popularmovies.adapters.TrailersAdapter;
import com.emrox_riprap.popularmovies.api.IdTrailersReviewsAPI;
import com.emrox_riprap.popularmovies.api.IdTrailersReviewsResponse;
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
 * Fragment for movie details
 */
public class DetailActivityFragment extends Fragment implements TrailersAdapter.TrailerViewCLickListener {

    private Movie mMovie;
    private String movieId;
    private TrailersAdapter mTrailersAdapter;
    private ArrayList<Trailer> mTrailersList;
    private RecyclerView mTrailersRecyclerView;

    private ReviewsAdapter mReviewsAdapter;
    private RecyclerView mReviewsRecyclerView;

    private TextView runtime;
    private TextView mReviewsHeading;
    private TextView mTrailersHeading;
    private Context mContext;

    interface FavoritesCallback {
        void movieDeletedFromFavs(Movie movie);
    }

    @Override
    public void trailerItemClicked(Trailer trailer) {
        //callback from adapter onClick
        String clipId = trailer.getKey();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + clipId));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + clipId));
            startActivity(intent);
        }
    }

    public DetailActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        getTrailers();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getActivity() instanceof MainActivity) {
            mMovie = getArguments().getParcelable(Movie.PARCEL_KEY);
            movieId = String.valueOf(mMovie.getId());
        } else if (getActivity().getIntent() != null && getActivity().getIntent().getParcelableExtra(Movie.PARCEL_KEY) != null) {
            mMovie = getActivity().getIntent().getParcelableExtra(Movie.PARCEL_KEY);
            movieId = String.valueOf(mMovie.getId());
        }
        mTrailersList = new ArrayList<Trailer>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mTrailersAdapter = new TrailersAdapter(getActivity(), this);
        mTrailersRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_trailers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mTrailersRecyclerView.setLayoutManager(layoutManager);

        mReviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_reviews);

        TextView title = (TextView) rootView.findViewById(R.id.tv_title);
        TextView overview = (TextView) rootView.findViewById(R.id.tv_overview);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.tv_year);
        TextView voterRating = (TextView) rootView.findViewById(R.id.tv_rating);
        runtime = (TextView) rootView.findViewById(R.id.tv_length);

        mReviewsHeading = (TextView) rootView.findViewById(R.id.tv_reviews_heading);
        mTrailersHeading = (TextView) rootView.findViewById(R.id.tv_trailers_heading);

        final ImageButton favButton = (ImageButton) rootView.findViewById(R.id.b_fav_button);
        if (movieIsInFavsDb()) {
            favButton.setImageResource(R.drawable.isa_fava_fav);
            favButton.setTag("minus");
        } else {
            favButton.setImageResource(R.drawable.nota_fava_fav);
            favButton.setTag("plus");
        }

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //movie is NOT in db...add it to db
                if (favButton.getTag().equals("plus")) {
                    ContentValues values = new ContentValues();
                    values.put(MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID, mMovie.getId());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_TITLE, mMovie.getTitle());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_POSTER_PATH, mMovie.getPoster_path());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_RELEASE_DATE, mMovie.getRelease_date());
                    values.put(MoviesContract.FavoritesEntry.COLUMN_VOTER_AVERAGE, mMovie.getVote_average());

                    Uri uri = getActivity().getContentResolver().insert(MoviesContract.FavoritesEntry.CONTENT_URI, values);
                    favButton.setImageResource(R.drawable.isa_fava_fav);
                    favButton.setTag("minus");

                    if (uri != null) {
                        Toast.makeText(getActivity(), "Added to favs list", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //movie is already in db...delete it from db
                    String args = String.valueOf(mMovie.getId());
                    int success = getActivity().getContentResolver().delete(
                            MoviesContract.FavoritesEntry.CONTENT_URI,
                            MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                            new String[]{args});

                    if (success != 0) {
                        Toast.makeText(getActivity(), "Removed from favs list", Toast.LENGTH_LONG).show();
                        favButton.setImageResource(R.drawable.nota_fava_fav);
                        favButton.setTag("plus");

                        if (MainActivity.sTablet) {
                            //the 'favorites' movie list is visible.  update the adapter to reflect the deleted movie
                            ((FavoritesCallback) getActivity()).movieDeletedFromFavs(mMovie);
                        }
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


    //if movie is in favorites db then change button text/action for option to remove
    //instead of add to db
    private boolean movieIsInFavsDb() {
        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(MoviesDbHelper.COL_MOVIE_ID);

                if (id == Integer.parseInt(movieId)) {
                    return true;
                }
            }

        }
        return false;
    }

    private void getTrailers() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IdTrailersReviewsAPI.TRAILERS_ENDPOINT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final IdTrailersReviewsAPI trailersAPI = retrofit.create(IdTrailersReviewsAPI.class);
        Call<IdTrailersReviewsResponse> call = trailersAPI.getTrailers(movieId, getString(R.string.movie_db_api_key), "videos,reviews");
        call.enqueue(new Callback<IdTrailersReviewsResponse>() {
            @Override
            public void onResponse(Call<IdTrailersReviewsResponse> call, Response<IdTrailersReviewsResponse> response) {
                //get the complete JSON Object
                IdTrailersReviewsResponse trailersResponse = response.body();

                //Extract the runtime value from the response
                int run = trailersResponse.getRuntime();
                runtime.setText(run + " min");

                //Extract the 'videos' object from it.
                IdTrailersReviewsResponse.TrailersResults results = trailersResponse.getVideos();
                //And extract the arraylist from that object...
                ArrayList<Trailer> trailerList = results.getResults();

                if (trailerList != null) {
                    mTrailersList.clear();
                    for (Trailer t : trailerList) {
                        if (t.getType().equalsIgnoreCase("Trailer")) {
                            mTrailersList.add(t);
                        }
                    }
                    if (mTrailersList.size() > 0) {
                        mTrailersAdapter.swapData(mTrailersList);

                    } else {
                        mTrailersHeading.setText("No Trailers Available");
                    }
                }

                //Extract the Reviews from the response
                IdTrailersReviewsResponse.ReviewResults reviewResults = trailersResponse.getReviews();
                //And extract the arraylist from that object
                ArrayList<Review> reviewList = reviewResults.getResults();
                ArrayList<ParentObject> parentObjects = new ArrayList<>();
                if (reviewList.size() > 0 && reviewList != null && reviewResults != null) {
                    for (Review r : reviewList) {
                        ArrayList<Object> childList = new ArrayList<>();
                        childList.add(new ReviewChildObject(r.getContent()));
                        r.setChildObjectList(childList);
                        parentObjects.add(r);
                    }

                    //setting all of this stuff here because bignerd doesn't have a way to notifydatasetchanged...
                    mReviewsAdapter = new ReviewsAdapter(mContext, parentObjects);
                    mReviewsAdapter.setCustomParentAnimationViewId(R.id.ib_list_item_expand_arrow);
                    mReviewsAdapter.setParentClickableViewAnimationDefaultDuration();
                    mReviewsAdapter.setParentAndIconExpandOnClick(true);
                    mReviewsRecyclerView.setAdapter(mReviewsAdapter);
                    mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                } else {
                    mReviewsHeading.setText("No Reviews Available");
                }


            }

            @Override
            public void onFailure(Call<IdTrailersReviewsResponse> call, Throwable t) {

            }
        });
    }


}

