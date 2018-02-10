package com.ctp.example.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctp.example.popularmovies.AsyncTasks.JsonDownloadTaskLoader;
import com.ctp.example.popularmovies.Model.Movie;
import com.ctp.example.popularmovies.provider.MovieDbContract;
import com.ctp.example.popularmovies.utils.MovieDbNetworkUtils;
import com.ctp.example.popularmovies.utils.PopularMovieUtils;

import org.json.JSONException;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PopularAdapter.PopularAdapterClickListener,
        LoaderManager.LoaderCallbacks<Object>{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String LOADER_BUNDLE_URL_KEY="the-url-key";
    public static final int LOADER_JSON_DOWNLOAD_KEY=1000;
    public static final int LOADER_CURSOR_LOADER_KEY = 2000;

    private static final String BUNDLE_SAVE_FAV_STATE_KEY="fav_state_jet";


    private boolean isDisplayingFavorites;

    private List<Movie> moviesList;

    private RecyclerView gridRecyclerView;
    private ProgressBar progressBar;

    private TextView errorMessage;
    private Button refreshBtn;

    private boolean isErrorMessageDisplayed;

    private PopularAdapter theAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isErrorMessageDisplayed = false;
        gridRecyclerView = findViewById(R.id.recycler_grid);
        progressBar =  findViewById(R.id.loading_indicator);
        errorMessage = findViewById(R.id.error_message_display);
        refreshBtn = findViewById(R.id.refresh_btn);

        LinearLayoutManager theManager = new GridLayoutManager(this,numberOfColumns());

        gridRecyclerView.setLayoutManager(theManager);
        gridRecyclerView.setHasFixedSize(true);



        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(LOADER_BUNDLE_URL_KEY,MovieDbNetworkUtils.POPULAR_MOVIES);
                getSupportLoaderManager().restartLoader(LOADER_JSON_DOWNLOAD_KEY,bundle,MainActivity.this);
            }
        });

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(BUNDLE_SAVE_FAV_STATE_KEY)){
                isDisplayingFavorites = savedInstanceState.getBoolean(BUNDLE_SAVE_FAV_STATE_KEY);
            }
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isDisplayingFavorites){
            getSupportLoaderManager().restartLoader(LOADER_CURSOR_LOADER_KEY,null,this);
        }else{
            Bundle bundle = new Bundle();
            bundle.putInt(LOADER_BUNDLE_URL_KEY, MovieDbNetworkUtils.POPULAR_MOVIES);
            getSupportLoaderManager().initLoader(LOADER_JSON_DOWNLOAD_KEY, bundle, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isDisplayingFavorites){
            outState.putBoolean(BUNDLE_SAVE_FAV_STATE_KEY,isDisplayingFavorites);
        }else{
            outState.putBoolean(BUNDLE_SAVE_FAV_STATE_KEY,isDisplayingFavorites);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        switch (id){
            case R.id.sort_popular_btn:
                bundle.putInt(LOADER_BUNDLE_URL_KEY,MovieDbNetworkUtils.POPULAR_MOVIES);
                getSupportLoaderManager().restartLoader(LOADER_JSON_DOWNLOAD_KEY,bundle,this);

                break;
            case R.id.sort_toprated_btn:
                bundle.putInt(LOADER_BUNDLE_URL_KEY,MovieDbNetworkUtils.TOP_RATED_MOVIES);
                getSupportLoaderManager().restartLoader(LOADER_JSON_DOWNLOAD_KEY,bundle,this);

                break;
            case R.id.sort_favorite_btn:
                getSupportLoaderManager().restartLoader(LOADER_CURSOR_LOADER_KEY,null,this);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    @Override
    public void onPosterClick(Movie movieClicked,boolean isCursorData) {

        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(DetailsActivity.MOVIE_IS_CURSOR_DATA_KEY,isCursorData);
        if(!isCursorData){
            intent.putExtra(DetailsActivity.MOVIE_OBJECT_TRANSFER_KEY,movieClicked);
        }
        else{
            intent.putExtra(DetailsActivity.MOVIE_STORED_ID_KEY,movieClicked.getId());
        }
        startActivity(intent);

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (isErrorMessageDisplayed) {
            hideErrorMessage();
           }
            displayProgressBar();
            switch (id) {
                case LOADER_JSON_DOWNLOAD_KEY:
                    return new JsonDownloadTaskLoader(this, args.getInt(LOADER_BUNDLE_URL_KEY));
                case LOADER_CURSOR_LOADER_KEY:
                    return new CursorLoader(this,
                            MovieDbContract.MovieFavoriteEntry.FAVORITE_CONTENT_URI,
                            PopularMovieUtils.cursorLoaderProjection,
                            null,
                            null,
                            null);
                default:
                    return null;
            }

        }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        int id = loader.getId();
        switch (id) {
            case LOADER_JSON_DOWNLOAD_KEY:
                String theData = (String)data;
                displayJsonData(theData);
                break;
            case LOADER_CURSOR_LOADER_KEY:
                Cursor cursor = (Cursor) data;
                displayCursorData(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        updateRecyclerView(null,true);
    }






    private void displayJsonData(String theData){
        if (theData != null) {
            try {
                moviesList = PopularMovieUtils.getMoviesListFromJson(theData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(isErrorMessageDisplayed) {
                hideErrorMessage();
                Log.d(TAG,"Error message is hidden");
            }
            updateRecyclerView(moviesList,false);
            hideProgressBar();

        } else {
            hideProgressBar();
            displayErrorMessage();
        }

        isDisplayingFavorites = false;
    }

    private void displayCursorData(Cursor data) {
        if(data.getCount()<=0){
            /* TODO: Show empty cursor display */
            return;
        }

        moviesList = PopularMovieUtils.getMovieListFromCursor(data);

        updateRecyclerView(moviesList,true);
        isDisplayingFavorites = true;
        Log.d(TAG,"DisplayCursorDataCalled");
        if(isErrorMessageDisplayed){
            hideErrorMessage();
            Log.d(TAG,"Error message is hidden");
        }
        hideProgressBar();
    }

    private void updateRecyclerView(List<Movie> moviesList, boolean isCursorData){
        if (theAdapter == null) {
            theAdapter = new PopularAdapter(moviesList, this,isCursorData);
            gridRecyclerView.setAdapter(theAdapter);
        } else {
            theAdapter.swapData(moviesList,isCursorData);
        }
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 500;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }


    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        gridRecyclerView.setVisibility(View.VISIBLE);
    }

    private void displayProgressBar() {
        gridRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void displayErrorMessage() {
        gridRecyclerView.setVisibility(View.INVISIBLE);
        refreshBtn.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
        isErrorMessageDisplayed = true;
    }

    public void hideErrorMessage(){

        errorMessage.setVisibility(View.INVISIBLE);
        refreshBtn.setVisibility(View.INVISIBLE);
        gridRecyclerView.setVisibility(View.VISIBLE);
        isErrorMessageDisplayed = false;
    }


}
