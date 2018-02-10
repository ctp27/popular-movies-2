package com.ctp.example.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ctp.example.popularmovies.Model.Movie;
import com.ctp.example.popularmovies.provider.MovieDbContract;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by clinton on 2/8/18.
 */

public class FavoritesUtils {

    public static final String ACTION_ADD_TO_FAVORITES = "add_favorites";
    public static final String ACTION_DELETE_FROM_FAVORITES = "delete_favorite";
    public static final String FAVORITES_SERIALIZABLE_KEY = "serial-key";

    public static void executeTasks(Context context, Movie m, String action){

        switch (action){
            case ACTION_ADD_TO_FAVORITES:
                addMovieToFavorites(m,context);
                break;
            case ACTION_DELETE_FROM_FAVORITES:
                removeFromFavorites(m,context);
        }


    }

    public static Cursor getData(Context context){
        Cursor cursor = context.getContentResolver().query(MovieDbContract.MovieFavoriteEntry.FAVORITE_CONTENT_URI,
                null,null,null,null);

        return cursor;

    }


    public static void addMovieToFavorites(Movie m, Context context){

        Bitmap img = downloadImageBitmap(m.getThumbnailLink());
        byte[] data = getBitmapAsByteArray(img); // this is a function

        ContentValues cv = populateContentValueFromMovie(m);
        cv.put(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_POSTER_LINK,data);

        context.getContentResolver().insert(MovieDbContract.MovieFavoriteEntry.FAVORITE_CONTENT_URI,
                cv);

    }

    public static void removeFromFavorites(Movie m, Context context){

        context.getContentResolver().delete(MovieDbContract.MovieFavoriteEntry.FAVORITE_CONTENT_URI,
                MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_ID+" = ?",new String[]{m.getId()+""});

    }

    private static ContentValues populateContentValueFromMovie(Movie movie) {
        ContentValues testWeatherValues = new ContentValues();
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_ID, movie.getId());
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_NAME,movie.getTitle());
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_RATING, movie.getUserRating());
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_SYNOPSIS,movie.getSynopsis());

        return testWeatherValues;
    }

    private static Bitmap downloadImageBitmap(String imageURL){

        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


}
