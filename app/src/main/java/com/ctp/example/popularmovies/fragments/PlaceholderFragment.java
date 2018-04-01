package com.ctp.example.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctp.example.popularmovies.AsyncTasks.JsonDownloadTaskLoader;
import com.ctp.example.popularmovies.DetailsActivity;
import com.ctp.example.popularmovies.MainActivity;
import com.ctp.example.popularmovies.Model.Movie;
import com.ctp.example.popularmovies.PopularAdapter;
import com.ctp.example.popularmovies.R;
import com.ctp.example.popularmovies.provider.MovieDbContract;
import com.ctp.example.popularmovies.utils.MovieDbNetworkUtils;
import com.ctp.example.popularmovies.utils.PopularMovieUtils;

import org.json.JSONException;

import java.util.List;

public class PlaceholderFragment extends Fragment implements
        PopularAdapter.PopularAdapterClickListener,
        LoaderManager.LoaderCallbacks<Object> {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LOADER_BUNDLE_URL_KEY="the-url-key";
    private static final int LOADER_JSON_DOWNLOAD_KEY=1000;
    private static final int LOADER_CURSOR_LOADER_KEY = 2000;

    private static final String BUNDLE_SAVE_FAV_STATE_KEY="fav_state_jet";

    private static int WIDTH_DIVIDER = 0;


    private boolean isDisplayingFavorites;

    private List<Movie> moviesList;

    private RecyclerView gridRecyclerView;


    private TextView errorMessage;


    private boolean isErrorMessageDisplayed;

    private PopularAdapter theAdapter;

    private SwipeRefreshLayout refreshLayout;


    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
        isErrorMessageDisplayed = false;
        refreshLayout = rootView.findViewById(R.id.swipe_refresh);
        gridRecyclerView = rootView.findViewById(R.id.recycler_grid);

        errorMessage = rootView.findViewById(R.id.error_message_display);
        WIDTH_DIVIDER = Integer.parseInt(getString(R.string.width_divider));


        GridLayoutManager theManager = new GridLayoutManager(getContext(),numberOfColumns());

        gridRecyclerView.setLayoutManager(theManager);
        gridRecyclerView.setHasFixedSize(true);




        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(BUNDLE_SAVE_FAV_STATE_KEY)){
                isDisplayingFavorites = savedInstanceState.getBoolean(BUNDLE_SAVE_FAV_STATE_KEY);
            }
        }

        final int n = getArguments().getInt(ARG_SECTION_NUMBER);

        startLoaderForTabNumber(n);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoaderForTabNumber(n);
            }
        });

        return rootView;
    }


    private void startLoaderForTabNumber(int n){
        Bundle bundle = new Bundle();
        switch (n){

            case 1:
                bundle.putInt(LOADER_BUNDLE_URL_KEY,MovieDbNetworkUtils.POPULAR_MOVIES);
                getLoaderManager().restartLoader(LOADER_JSON_DOWNLOAD_KEY,bundle,this);
                break;
            case 2:
                bundle.putInt(LOADER_BUNDLE_URL_KEY,MovieDbNetworkUtils.TOP_RATED_MOVIES);
                getLoaderManager().restartLoader(LOADER_JSON_DOWNLOAD_KEY,bundle,this);
                break;
            case 3:
                getLoaderManager().restartLoader(LOADER_CURSOR_LOADER_KEY,null,this);
                break;

        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        preLoadChecks();
        switch (id) {
            case LOADER_JSON_DOWNLOAD_KEY:
                return new JsonDownloadTaskLoader(getContext(), args.getInt(LOADER_BUNDLE_URL_KEY));
            case LOADER_CURSOR_LOADER_KEY:
                return new CursorLoader(getContext(),
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
            hideProgressBar();
//            TODO: show no movies message
            displayErrorMessage();
            errorMessage.setText(getString(R.string.favorites_empty_msg));
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

    @Override
    public void onPosterClick(Movie movieClicked, boolean isCursorData) {
        Intent intent = new Intent(getContext(),DetailsActivity.class);
        intent.putExtra(DetailsActivity.MOVIE_IS_CURSOR_DATA_KEY,isCursorData);
        if(!isCursorData){
            intent.putExtra(DetailsActivity.MOVIE_OBJECT_TRANSFER_KEY,movieClicked);
        }
        else{
            intent.putExtra(DetailsActivity.MOVIE_STORED_ID_KEY,movieClicked.getId());
        }
        startActivity(intent);
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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 500;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void preLoadChecks(){
        if (isErrorMessageDisplayed) {
            hideErrorMessage();
        }
        displayProgressBar();
    }

    private void hideProgressBar() {
        gridRecyclerView.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
    }

    private void displayProgressBar() {
        gridRecyclerView.setVisibility(View.INVISIBLE);
        refreshLayout.setRefreshing(true);

    }

    private void displayErrorMessage() {
        gridRecyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
        isErrorMessageDisplayed = true;
    }

    private void hideErrorMessage(){

        errorMessage.setVisibility(View.INVISIBLE);
        gridRecyclerView.setVisibility(View.VISIBLE);
        isErrorMessageDisplayed = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isDisplayingFavorites){
            outState.putBoolean(BUNDLE_SAVE_FAV_STATE_KEY,isDisplayingFavorites);
        }else{
            outState.putBoolean(BUNDLE_SAVE_FAV_STATE_KEY,isDisplayingFavorites);
        }
    }


}
