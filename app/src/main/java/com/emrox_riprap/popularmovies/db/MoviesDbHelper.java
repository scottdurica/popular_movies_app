package com.emrox_riprap.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by scott on 7/3/2016.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "favorites.db";

    public static final int COL_INDEX_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_INDEX_TITLE = 2;
    public static final int COL_INDEX_OVERVIEW = 3;
    public static final int COL_INDEX_POSTER_PATH = 4;
    public static final int COL_INDEX_RELEASE_DATE = 5;
    public static final int COL_INDEX_VOTER_AVERAGE = 6;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MoviesContract.FavoritesEntry.TABLE_NAME +
                " (" +
                MoviesContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_VOTER_AVERAGE + " TEXT NOT NULL" +
                ")";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
