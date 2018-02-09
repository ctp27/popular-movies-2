package com.ctp.example.popularmovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by clinton on 2/8/18.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";

    public static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = "CREATE TABLE "  + MovieDbContract.MovieFavoriteEntry.TABLE_NAME + " (" +
                MovieDbContract.MovieFavoriteEntry._ID                + " INTEGER PRIMARY KEY, " +
                MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_ID   + " INTEGER NOT NULL, " +
                MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_POSTER_LINK +" TEXT NOT NULL, "+
                MovieDbContract.MovieFavoriteEntry.COLUMN_RATING + " INTEGER NOT NULL, "+
                MovieDbContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "+
                MovieDbContract.MovieFavoriteEntry.COLUMN_SYNOPSIS+ ");";

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieDbContract.MovieFavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
