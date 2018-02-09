package com.ctp.example.popularmovies.AsyncTasks;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.ctp.example.popularmovies.utils.MovieDbNetworkUtils;

import java.io.IOException;
import java.net.URL;


/**
 * Created by clinton on 2/8/18.
 */

public class JsonDownloadTaskLoader extends AsyncTaskLoader<String> {

    private String theJsonData;
    int sortOrder;

    private DownloadTaskLoaderCallbacks loaderCallbacks;

    public interface DownloadTaskLoaderCallbacks{
        void onStartedLoading();
    }

    public JsonDownloadTaskLoader(Context context, int sortOrder, DownloadTaskLoaderCallbacks callbacks) {
        super(context);
        loaderCallbacks = callbacks;
        this.sortOrder = sortOrder;
    }


    @Override
    protected void onStartLoading() {

        if(theJsonData!=null){
            deliverResult(theJsonData);
        }
        else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        loaderCallbacks.onStartedLoading();
        String jsonData = null;

        try {
            jsonData = MovieDbNetworkUtils.getJsonResponseFromHttpUrl(getUrlForSort(sortOrder));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    @Override
    public void deliverResult(String data) {
        theJsonData = data;
        super.deliverResult(data);
    }


    private URL getUrlForSort(int sortType) {
        URL url = MovieDbNetworkUtils.buildUrl(sortType);
        return url;
    }
}
