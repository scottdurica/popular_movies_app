package com.emrox_riprap.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.emrox_riprap.popularmovies.POJO.Movie;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback,
        DetailActivityFragment.FavoritesCallback,MainActivityFragment.EmptyFavsListCallback {
    public static final String FRAG_TAG_DETAILS = "detail_fragment";
    public static final String FRAG_TAG_BLANK = "bland_fragment";
    public static boolean sTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            sTablet = true;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if (!sTablet) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Movie.PARCEL_KEY, movie);
            startActivity(intent);
        } else {
            DetailActivityFragment fragment = new DetailActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Movie.PARCEL_KEY, movie);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, FRAG_TAG_DETAILS)
                    .commit();
        }

    }

    @Override
    public void movieDeletedFromFavs(Movie movie) {
        /**
         * Called only when in tablet mode and movie is "un-favorited". This will erase the
         * detailfragments view and replace with placeholder(blank) view.
         */
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment);
        ((MainActivityFragment) frag).fetchMovieData();
        if(MainActivityFragment.getSortOrder().equalsIgnoreCase("favorites")){
            Fragment fragment = BlankFragment.newInstance(R.drawable.no_movie_placeholder);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, FRAG_TAG_BLANK).
                    addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public void favsMovieListEmpty() {
        /**
         * Called only when in tablet mode and favorite movie list has been completely emptied.
         * This will set the view of the Master to placeholder and set the view of the Detail
         * to blank.
         */
        //remove the blank fragment from detail view
        if (getSupportFragmentManager().findFragmentById(R.id.movie_detail_container) instanceof BlankFragment){
            BlankFragment frag = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }
        Fragment fragment = BlankFragment.newInstance(R.drawable.fav_list_empty_placeholder);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment, FRAG_TAG_BLANK)
                .commit();

    }
}
