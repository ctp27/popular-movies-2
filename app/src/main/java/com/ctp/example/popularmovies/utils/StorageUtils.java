package com.ctp.example.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ctp.example.popularmovies.provider.MovieDbContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clinton on 2/8/18.
 */

public class StorageUtils {

    private static ContentValues createTestWeatherContentValues(int i) {
        ContentValues testWeatherValues = new ContentValues();
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE, "theDate"+i);
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_ID, i+1);
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_NAME,"movieName"+i);
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_POSTER_LINK,
                "posterLink"+i);
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_RATING, i+7);
        testWeatherValues.put(MovieDbContract.MovieFavoriteEntry.COLUMN_SYNOPSIS,"synopsis"+i);
        return testWeatherValues;
    }

    public static void insertFakeData(Context context){
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        //loop over 7 days starting today onwards
        for(int i=0; i<7; i++) {
            fakeValues.add(StorageUtils.createTestWeatherContentValues(i));
        }
        context.getContentResolver().bulkInsert(
                MovieDbContract.MovieFavoriteEntry.FAVORITE_CONTENT_URI,
                fakeValues.toArray(new ContentValues[7]));
    }

    public static Cursor getData(Context context){
        Cursor cursor = context.getContentResolver().query(MovieDbContract.MovieFavoriteEntry.FAVORITE_CONTENT_URI,
                null,null,null,null);

        return cursor;

    }

}
