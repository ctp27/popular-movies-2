package com.ctp.example.popularmovies.utils;

import android.database.Cursor;

import com.ctp.example.popularmovies.Model.Movie;
import com.ctp.example.popularmovies.Model.Review;
import com.ctp.example.popularmovies.Model.Trailer;
import com.ctp.example.popularmovies.provider.MovieDbContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clinton on 11/17/17.
 */

public class PopularMovieUtils  {

    public static final String TAG = PopularMovieUtils.class.getSimpleName();

    public static final String[] cursorLoaderProjection = {
            MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_ID,
            MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_NAME,
            MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_POSTER_LINK,
            MovieDbContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE,
            MovieDbContract.MovieFavoriteEntry.COLUMN_RATING,
            MovieDbContract.MovieFavoriteEntry.COLUMN_SYNOPSIS
    };

    private static final int MOVIE_ID_INDEX = 0;
    private static final int MOVIE_NAME_INDEX = 1;
    private static final int MOVIE_POSTER_INDEX=2;
    private static final int MOVIE_RELEASE_DATE_INDEX=3;
    private static final int MOVIE_RATING_INDEX=4;
    private static final int MOVIE_SYNOPSIS_INDEX = 5;

    private static final String IMG_BASE_URL="http://image.tmdb.org/t/p/w780";


    public static List<Movie> getMoviesListFromJson(String jsonString) throws JSONException {

        List<Movie> theList = new ArrayList<>();

        JSONObject mainObject = new JSONObject(jsonString);

        JSONArray movieResultsArray = mainObject.getJSONArray("results");


        for(int i = 0;i<movieResultsArray.length();i++){

            JSONObject thisObject = movieResultsArray.getJSONObject(i);

            int id = thisObject.getInt("id");

            String thumbnailLink = buildImgUrl(thisObject.getString("poster_path"));

            String title = thisObject.getString("original_title");

            String synopsis = thisObject.getString("overview");

            int userRating = thisObject.getInt("vote_average");

            String releaseDate = thisObject.getString("release_date");

            theList.add(new Movie(id,thumbnailLink,title,synopsis,userRating,releaseDate));
        }

        return theList;
    }


    private static String buildImgUrl(String relativePath){

        return IMG_BASE_URL+relativePath;
    }


    public static List<Movie> getMovieListFromCursor(Cursor cursor){

        List<Movie> listToSend = new ArrayList<>();

        while(cursor.moveToNext()){

            long movieId=cursor.getLong(MOVIE_ID_INDEX);
            String name = cursor.getString(MOVIE_NAME_INDEX);

            byte[] poster = cursor.getBlob(MOVIE_POSTER_INDEX);

            long rating = cursor.getLong(MOVIE_RATING_INDEX);
            String date = cursor.getString(MOVIE_RELEASE_DATE_INDEX);
            String synopsis = cursor.getString(MOVIE_SYNOPSIS_INDEX);
//            Log.d(TAG,new Movie((int)movieId,theBitmap,name,synopsis,(int)rating,date).toString());
            listToSend.add(new Movie((int)movieId,poster,name,synopsis,(int)rating,date));

        }

        return listToSend;

    }

    public static List<Review> getReviewListFromJson(String jsonData) throws JSONException{

        List<Review> theList = new ArrayList<>();

        JSONObject mainObject = new JSONObject(jsonData);

        JSONArray reviewResultsArray = mainObject.getJSONArray("results");

        for(int i=0; i<reviewResultsArray.length(); i++){

            JSONObject thisObject = reviewResultsArray.getJSONObject(i);

            String id = thisObject.getString("id");

            String author = thisObject.getString("author");

            String content = thisObject.getString("content");

            String url = thisObject.getString("url");

            theList.add(new Review(id,author,content,url));

        }

        return theList;
    }

    public static List<Trailer> getTrailerListFromJson(String jsonData)
                                throws JSONException{

        List<Trailer> theList = new ArrayList<>();

        JSONObject mainObject = new JSONObject(jsonData);

        JSONArray trailerResultsArray = mainObject.getJSONArray("results");


        for(int i=0; i<trailerResultsArray.length(); i++){

            JSONObject thisObject = trailerResultsArray.getJSONObject(i);

            String type = thisObject.getString("type");

            if(type.equalsIgnoreCase("Trailer") ||
                    type.equalsIgnoreCase("Teaser")) {

                String id = thisObject.getString("id");

                String isoString = thisObject.getString("iso_639_1");

                String key = thisObject.getString("key");

                String name = thisObject.getString("name");

                String site = thisObject.getString("site");


                theList.add(new Trailer(id, isoString, key, name, site, type));
            }

        }

        return theList;

    }

}
