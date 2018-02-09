package com.ctp.example.popularmovies.utils;

import com.ctp.example.popularmovies.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clinton on 11/17/17.
 */

public class PopularMovieUtils  {


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

}
