package com.ctp.example.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by clinton on 2/8/18.
 */

public class MovieDbProvider extends ContentProvider {

    public static final int FAV = 100;
    public static final int FAV_WITH_ID = 101;


    private MovieDbHelper theHelper;

    private static UriMatcher theMatcher = buildUriMatcher();


    @Override
    public boolean onCreate() {
        Context context = getContext();
        theHelper = new MovieDbHelper(context);
        return true;

    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = theHelper.getWritableDatabase();

        switch (theMatcher.match(uri)) {

            case FAV:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieDbContract.MovieFavoriteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (theMatcher.match(uri)) {

            case FAV_WITH_ID: {

                String movieId = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{movieId};

                cursor = theHelper.getReadableDatabase().query(

                        MovieDbContract.MovieFavoriteEntry.TABLE_NAME,
                        projection,
                        MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            case FAV: {
                cursor = theHelper.getReadableDatabase().query(
                        MovieDbContract.MovieFavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Uri not defined");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = theHelper.getWritableDatabase();

        int match = theMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case FAV:

                long id = db.insert(MovieDbContract.MovieFavoriteEntry.TABLE_NAME, null, contentValues);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MovieDbContract.MovieFavoriteEntry.FAVORITE_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);


        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted = 0;

        if (null == selection) selection = "1";

        switch (theMatcher.match(uri)) {

            case FAV:
                numRowsDeleted = theHelper.getWritableDatabase().delete(
                        MovieDbContract.MovieFavoriteEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Uri not defined");
    }


    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_FAVORITES, FAV);
        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_FAVORITES + "/#", FAV_WITH_ID);

        return uriMatcher;
    }
}
