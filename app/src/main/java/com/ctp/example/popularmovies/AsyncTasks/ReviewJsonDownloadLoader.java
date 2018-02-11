package com.ctp.example.popularmovies.AsyncTasks;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.ctp.example.popularmovies.utils.MovieDbNetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by clinton on 2/10/18.
 */

public class ReviewJsonDownloadLoader extends AsyncTaskLoader<String> {

    private int movieId;
    private String reviewCachedData;


    public ReviewJsonDownloadLoader(Context context, int movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    protected void onStartLoading() {

        if(reviewCachedData!=null){
            deliverResult(reviewCachedData);
        }
        else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        URL url = MovieDbNetworkUtils.buildReviewsUrlForMovieId(movieId);

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
        reviewCachedData = data;
        super.deliverResult(data);
    }
}
