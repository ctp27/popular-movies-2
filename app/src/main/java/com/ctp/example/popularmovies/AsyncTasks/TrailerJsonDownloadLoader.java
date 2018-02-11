package com.ctp.example.popularmovies.AsyncTasks;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.ctp.example.popularmovies.utils.MovieDbNetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by clinton on 2/10/18.
 */

public class TrailerJsonDownloadLoader extends AsyncTaskLoader<String>{

    private int movieId;
    private String cachedTrailerData;


    public TrailerJsonDownloadLoader(Context context, int movieId) {
        super(context);
        this.movieId = movieId;
    }


    @Override
    protected void onStartLoading() {
        if(cachedTrailerData!=null){
            deliverResult(cachedTrailerData);
        }
        else {
            forceLoad();
        }
    }


    @Override
    public String loadInBackground() {
        URL url = MovieDbNetworkUtils.buildTrailersUrlForMovieId(movieId);

        String s = null;
        try {
            s= MovieDbNetworkUtils.getJsonResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }


    @Override
    public void deliverResult(String data) {
        cachedTrailerData = data;
        super.deliverResult(data);
    }
}
