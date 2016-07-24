package com.emrox_riprap.popularmovies.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by scott on 7/3/2016.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.emrox_riprap.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible URI's. (Paths to various DB tables)
    public static final String PATH_FAVORTIES = "favorites";



    public static final class FavoritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORTIES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORTIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORTIES;

        public static final String TABLE_NAME = "favorites";

        //Column names
        public static final String COLUMN_TITLE  = "title";
        public static final String COLUMN_OVERVIEW  = "overview";
        public static final String COLUMN_POSTER_PATH  = "poster_path";
        public static final String COLUMN_RELEASE_DATE  = "release_date";
        public static final String COLUMN_VOTER_AVERAGE  = "voter_average";

        public static Uri buildFavoriteUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }

}
