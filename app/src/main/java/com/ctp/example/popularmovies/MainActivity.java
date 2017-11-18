package com.ctp.example.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctp.example.popularmovies.Model.Movie;
import com.ctp.example.popularmovies.utils.MovieDbNetworkUtils;
import com.ctp.example.popularmovies.utils.PopularMovieUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopularAdapter.PopularAdapterClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
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

        LinearLayoutManager theManager = new GridLayoutManager(this,SPAN_COUNT);

        gridRecyclerView.setLayoutManager(theManager);
        gridRecyclerView.setHasFixedSize(true);


        displayMovieData(MovieDbNetworkUtils.POPULAR_MOVIES);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMovieData(MovieDbNetworkUtils.POPULAR_MOVIES);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sort_popular_btn:
                displayMovieData(MovieDbNetworkUtils.POPULAR_MOVIES);
                break;
            case R.id.sort_toprated_btn:
                displayMovieData(MovieDbNetworkUtils.TOP_RATED_MOVIES);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    @Override
    public void onPosterClick(Movie movieClicked) {
//        TODO:Right intent to redirect to movie details activity
//        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra("movieObj",movieClicked);
        startActivity(intent);

    }

    private void displayMovieData(int sortType){
        URL url = MovieDbNetworkUtils.buildUrl(sortType);
        new DownloadJsonTask().execute(url);
    }


    /**
     * DownloadJsonTask class which defines the Async Task to perform the network operation
     * to download the JSON data from MovieDBUtils
     */

    private class DownloadJsonTask extends AsyncTask<URL,Void,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isErrorMessageDisplayed){
                hideErrorMessage();
            }
            displayProgressBar();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null){
                try {
                    moviesList = PopularMovieUtils.getMoviesListFromJson(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                TODO: show the recyclerview
                hideErrorMessage();
                theAdapter = new PopularAdapter(moviesList,MainActivity.this);
                gridRecyclerView.setAdapter(theAdapter);
            }
            else{
                displayErrorMessage();
            }
            hideProgressBar();

        }

        @Override
        protected String doInBackground(URL... urls) {

            URL thisUrl = urls[0];
            String jsonData = null;
            try {
                jsonData = MovieDbNetworkUtils.getJsonResponseFromHttpUrl(thisUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonData;
        }


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
//        TODO: Create layout and logic for displaying error message
    }
    private void hideErrorMessage(){

        errorMessage.setVisibility(View.INVISIBLE);
        refreshBtn.setVisibility(View.INVISIBLE);
        isErrorMessageDisplayed = false;
    }

}
