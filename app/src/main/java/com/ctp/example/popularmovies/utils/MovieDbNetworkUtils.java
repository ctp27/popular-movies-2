package com.ctp.example.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.ctp.example.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by clinton on 11/17/17.
 */

public class MovieDbNetworkUtils {


    private static final String MOVIEDB_API_KEY= BuildConfig.API_KEY;

    private static final String TAG = MovieDbNetworkUtils.class.getSimpleName();

    public static final int POPULAR_MOVIES = 1;
    public static final int TOP_RATED_MOVIES = 2;


    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String PATH_POPULAR = "popular";
    private static final String PATH_TOP_RATED = "top_rated";
    private static final String PATH_REVIEWS= "reviews";
    private static final String PATH_TRAILERS = "videos";

    private static final String QUERY_API_KEY="api_key";
    private static final String QUERY_LANGUAGE = "language";

    private static final String QUERY_LANGUAGE_ENGLISH = "en-US";


    /**
     * Builds the URL for the sorting type passed as a parameter
     * @param sortType integer parameter corresponding to popular movies or top_rated movies
     * @return returns the URL object
     */

    public static URL buildUrl(int sortType){
        String path = null;


        if(sortType==POPULAR_MOVIES){
            path =PATH_POPULAR;
        }
        else {
            path = PATH_TOP_RATED;
        }

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(QUERY_API_KEY, MOVIEDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Built URI " + url);
        return url;
    }


    public static URL buildReviewsUrlForMovieId(int movieId){

        String idToAppend = Integer.toString(movieId);

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(idToAppend)
                        .appendPath(PATH_REVIEWS)
                        .appendQueryParameter(QUERY_API_KEY,MOVIEDB_API_KEY)
                        .appendQueryParameter(QUERY_LANGUAGE,QUERY_LANGUAGE_ENGLISH)
                        .build();

        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.d(TAG,"Built URL is "+url);
        return url;
    }

    public static URL buildTrailersUrlForMovieId(int movieId){

//       TODO: Build this method to return a URI for getting the trailers

        String idToAppend = Integer.toString(movieId);

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(idToAppend)
                .appendPath(PATH_TRAILERS)
                .appendQueryParameter(QUERY_API_KEY,MOVIEDB_API_KEY)
                .appendQueryParameter(QUERY_LANGUAGE,QUERY_LANGUAGE_ENGLISH)
                .build();

        URL url = null;

        try{
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Network method which connects to the internet using the URL passed as a parameter
     * It returns the response as a String. In this case, a JSON string
     *
     * @param url the url to get response from
     * @return the response from the HttpUrl in the form of a String
     * @throws IOException
     */

    public static String getJsonResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}
