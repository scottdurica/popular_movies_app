package com.emrox_riprap.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by scott on 7/3/2016.
 */
public class MoviesProvider extends ContentProvider{

    private MoviesDbHelper mOpenHelper;

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int FAVORITES = 100;
    static final int FAVORITES_WITH_TITLE_AND_RELEASE_DATE = 101;
    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String autoritiy = MoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(autoritiy, MoviesContract.PATH_FAVORTIES,FAVORITES);
        matcher.addURI(autoritiy, MoviesContract.PATH_FAVORTIES + "/*/*",FAVORITES_WITH_TITLE_AND_RELEASE_DATE);

        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITES:
                retCursor = db.query(
                        MoviesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw  new UnsupportedOperationException("-query()-Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITES:
                return MoviesContract.FavoritesEntry.CONTENT_TYPE;
            case FAVORITES_WITH_TITLE_AND_RELEASE_DATE:
                return MoviesContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("-getType()-Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri retUri;

        switch (match){
            case FAVORITES:{
                long _id = db.insert(MoviesContract.FavoritesEntry.TABLE_NAME, null, values);
                if (_id > 0){
                    retUri = MoviesContract.FavoritesEntry.buildFavoriteUri(_id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw  new UnsupportedOperationException("-insert()-Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        //this makes delete all rows retrun the number of rows deleted
        if(selection == null) selection = "1";
        switch (match){
            case FAVORITES:
                rowsDeleted = db.delete(MoviesContract.FavoritesEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("-delete()-Unknown Uri: " + uri);
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // No need to be able to update the entries in the table...Only user options are either
        // to add a new favorite or delete an existing one.
        return 0;
    }
}
