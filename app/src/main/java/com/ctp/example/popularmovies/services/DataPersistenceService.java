package com.ctp.example.popularmovies.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.ctp.example.popularmovies.Model.Movie;
import com.ctp.example.popularmovies.utils.FavoritesUtils;

/**
 * Created by clinton on 2/9/18.
 */

public class DataPersistenceService extends IntentService {

    public DataPersistenceService(){
        super("DataPersistenceService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        Movie m = (Movie)intent.getSerializableExtra(FavoritesUtils.FAVORITES_SERIALIZABLE_KEY);
        FavoritesUtils.executeTasks(this,m,action);
    }
}
