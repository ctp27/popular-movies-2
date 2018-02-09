package com.ctp.example.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
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
        LoaderManager.LoaderCallbacks<String>,
        JsonDownloadTaskLoader.DownloadTaskLoaderCallbacks{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String LOADER_BUNDLE_URL_KEY="the-url-key";
    public static final int LOADER_JSON_DOWNLOAD_KEY=1000;
    private static final int SPAN_COUNT = 2;

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

        Bundle bundle = new Bundle();
        bundle.putInt(LOADER_BUNDLE_URL_KEY,MovieDbNetworkUtils.POPULAR_MOVIES);
        getSupportLoaderManager().initLoader(LOADER_JSON_DOWNLOAD_KEY,bundle,this);


        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(LOADER_BUNDLE_URL_KEY,MovieDbNetworkUtils.POPULAR_MOVIES);
                getSupportLoaderManager().restartLoader(LOADER_JSON_DOWNLOAD_KEY,bundle,MainActivity.this);
            }
        });

//        StorageUtils.insertFakeData(this);
        Uri uri = MovieDbContract.MovieFavoriteEntry.FAVORITE_CONTENT_URI;
        uri = uri.buildUpon().appendPath("3").build();
        Cursor cursor = getContentResolver().query(uri,null,null,null,
                null);

        while(cursor.moveToNext()){
            long movieId=cursor.getLong
                    (cursor.getColumnIndex(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_NAME));

            String poster = cursor.getString(
                    cursor.getColumnIndex(MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_POSTER_LINK));
            long rating = cursor.getLong(
                    cursor.getColumnIndex(MovieDbContract.MovieFavoriteEntry.COLUMN_RATING));
            String date = cursor.getString(
                    cursor.getColumnIndex(MovieDbContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE));
            String synopsis = cursor.getString(
                    cursor.getColumnIndex(MovieDbContract.MovieFavoriteEntry.COLUMN_SYNOPSIS));
            Movie m = new Movie((int)movieId,poster,name,synopsis,(int)rating,date);
            Log.d(TAG, m.toString());

        }


        Log.d(TAG,"Cursor count is"+cursor.getCount());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
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
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    @Override
    public void onPosterClick(Movie movieClicked) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra("movieObj",movieClicked);
        startActivity(intent);

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_JSON_DOWNLOAD_KEY:
                return new JsonDownloadTaskLoader(this,args.getInt(LOADER_BUNDLE_URL_KEY),this);
        }
        return null;
    }

    @Override
    public void onStartedLoading() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isErrorMessageDisplayed){
                    hideErrorMessage();
                }
                displayProgressBar();
            }
        });

        Log.d(TAG,"Called Start Loading");
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        if(data!=null){
            try {
                moviesList = PopularMovieUtils.getMoviesListFromJson(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideErrorMessage();
            theAdapter = new PopularAdapter(moviesList,MainActivity.this);
            gridRecyclerView.setAdapter(theAdapter);
        }
        else{
            displayErrorMessage();
        }

        hideProgressBar();
        Log.d(TAG,"Called Hide ");


    }


    @Override
    public void onLoaderReset(Loader<String> loader) {

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
        isErrorMessageDisplayed = false;
    }


}
