package com.ctp.example.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctp.example.popularmovies.Model.Movie;
import com.ctp.example.popularmovies.provider.MovieDbContract;
import com.ctp.example.popularmovies.services.DataPersistenceService;
import com.ctp.example.popularmovies.utils.FavoritesUtils;
import com.ctp.example.popularmovies.utils.PopularMovieUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsActivity extends AppCompatActivity
                    implements LoaderManager.LoaderCallbacks<Object>{

    private static final String TAG = DetailsActivity.class.getSimpleName();

    public static final String MOVIE_OBJECT_TRANSFER_KEY="movie-tranfer-object";
    public static final String MOVIE_IS_CURSOR_DATA_KEY = "cursor-data-key";
    public static final String MOVIE_STORED_ID_KEY="stored-movie_id";
    private static final int CURSOR_LOADER_ALL_DETAILS_KEY = 111;
    private static final int CURSOR_LOADER_CHECK_IF_FAVORITE_KEY = 112;
    private static final int LOADER_GET_REVIEWS_AND_TRAILERS = 113;
    private static final String INSTANCE_BOOLEAN_STATE_KEY = "booll";



    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView synopsis;
    private TextView releaseDate;
    private TextView userRating;
    private ImageButton favoriteBtn;
    private Movie movie;
    private Toast theToast;

    private boolean isAddedToFavorites;
    private boolean isCursorData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        moviePoster = findViewById(R.id.details_movie_poster);
        movieTitle =  findViewById(R.id.details_movie_title);
        synopsis = findViewById(R.id.details_synopsis);
        releaseDate = findViewById(R.id.details_release_date);
        userRating = findViewById(R.id.details_user_rating);
        favoriteBtn = findViewById(R.id.add_to_fav_btn);

        getMovieObjectFromSender();
    }


    private void getMovieObjectFromSender() {
        Intent receivedIntent = getIntent();
        if(receivedIntent!=null){
            Log.d(TAG,"Received intent is not null");
            if(receivedIntent.hasExtra(MOVIE_IS_CURSOR_DATA_KEY)){
                isCursorData = receivedIntent.getBooleanExtra(MOVIE_IS_CURSOR_DATA_KEY,false);
                Log.d(TAG,"isCursorData is "+isCursorData);
            }

            if(isCursorData){
                int movieId = receivedIntent.getIntExtra(MOVIE_STORED_ID_KEY,-1);
//                TODO: startCursorLoader to get this movie information
                Bundle bundle = new Bundle();
                bundle.putInt(MOVIE_STORED_ID_KEY,movieId);
                getSupportLoaderManager().restartLoader(CURSOR_LOADER_ALL_DETAILS_KEY,bundle,this);
                Log.d(TAG,"Restarted Loader");
            }
            else{
                movie = (Movie) receivedIntent.getSerializableExtra(MOVIE_OBJECT_TRANSFER_KEY);
                displayDataFromMovieObject();
                Log.d(TAG,"Displaying from object movie "+movie.getTitle());
//               TODO: Call method to initialize the data
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(INSTANCE_BOOLEAN_STATE_KEY,isAddedToFavorites);

    }

    public void addToFavorites(View view){

        if(isAddedToFavorites) {
            highlightFavoriteButtonBasedOnBoolean(false);
            Intent intent = new Intent(this,DataPersistenceService.class);
            movie.setBitmap(null);
            intent.putExtra(FavoritesUtils.FAVORITES_SERIALIZABLE_KEY,movie);
            intent.setAction(FavoritesUtils.ACTION_DELETE_FROM_FAVORITES);
            startService(intent);
            isAddedToFavorites = false;
            showRemovingFromFavoriteToast();
            if(isCursorData){
                finish();
            }
        }
        else{
            highlightFavoriteButtonBasedOnBoolean(true);

            Intent intent = new Intent(this, DataPersistenceService.class);
            intent.putExtra(FavoritesUtils.FAVORITES_SERIALIZABLE_KEY,movie);
            intent.setAction(FavoritesUtils.ACTION_ADD_TO_FAVORITES);
            startService(intent);

            isAddedToFavorites = true;
            showAddingToFavoriteToast();
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        int key=  args.getInt(MOVIE_STORED_ID_KEY);
        Uri uri = MovieDbContract.MovieFavoriteEntry.getUriForMovieId(key);

        switch (id){
            case CURSOR_LOADER_ALL_DETAILS_KEY:
                return new CursorLoader(this,
                        uri, PopularMovieUtils.cursorLoaderProjection,
                        null,null,null);
            case CURSOR_LOADER_CHECK_IF_FAVORITE_KEY:
                return new CursorLoader(this,
                        uri, new String[]{MovieDbContract.MovieFavoriteEntry.COLUMN_MOVIE_ID},
                        null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

        int id = loader.getId();

        switch (id){
            case CURSOR_LOADER_ALL_DETAILS_KEY:
                Cursor cursor = (Cursor)data;
                displayDataFromCursor(cursor);
                break;
            case CURSOR_LOADER_CHECK_IF_FAVORITE_KEY:
                Cursor theCursor = (Cursor)data;
                setTheFavoritesButton(theCursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    private void setTheFavoritesButton(Cursor cursor){
        if(cursor.getCount()>0){
            isAddedToFavorites = true;
            highlightFavoriteButtonBasedOnBoolean(isAddedToFavorites);
        }else {
            isAddedToFavorites = false;
            highlightFavoriteButtonBasedOnBoolean(isAddedToFavorites);
        }
    }

    private void displayDataFromCursor(Cursor cursor){

        List<Movie> lister = PopularMovieUtils.getMovieListFromCursor(cursor);
        if(lister.isEmpty()){
            return;
        }
        movie = lister.get(0);
        byte [] byteImg = movie.getBitmap();
        Bitmap bitmap= BitmapFactory.decodeByteArray(byteImg,0,byteImg.length);
        moviePoster.setImageBitmap(bitmap);
        movieTitle.setText(movie.getTitle());
        userRating.setText(movie.getUserRating()+"/10");
        releaseDate.setText(movie.getReleaseDate());
        synopsis.setText(movie.getSynopsis());
        isAddedToFavorites = true;
        highlightFavoriteButtonBasedOnBoolean(isAddedToFavorites);
        //TODO: start task to get Movies and reviews. Create a method
    }

    private void displayDataFromMovieObject(){
        movieTitle.setText(movie.getTitle());

        Picasso.with(this).load(movie.getThumbnailLink())
                .error(R.drawable.placeholder_img)
                .placeholder(R.drawable.placeholder_img)
                .into(moviePoster);

        userRating.append(Integer.toString(movie.getUserRating())+"/10");
        releaseDate.append(" " +movie.getReleaseDate());
        synopsis.setText(movie.getSynopsis());

        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_STORED_ID_KEY,movie.getId());
        getSupportLoaderManager().restartLoader(CURSOR_LOADER_CHECK_IF_FAVORITE_KEY,bundle,this);
    }


    private void showAddingToFavoriteToast(){
        if(theToast !=null){
            theToast.cancel();
        }
        theToast= Toast.makeText(this,"Adding to favorites",Toast.LENGTH_LONG);
        theToast.show();
    }

    private void showRemovingFromFavoriteToast(){
        if(theToast !=null){
            theToast.cancel();
        }
        theToast= Toast.makeText(this,"Removing from favorites",Toast.LENGTH_LONG);
        theToast.show();

    }

    private void highlightFavoriteButtonBasedOnBoolean(boolean isAFavorite){
        if(isAFavorite)
            favoriteBtn.setImageResource(R.drawable.ic_love_heart_svg);
        else
            favoriteBtn.setImageResource(R.drawable.ic_love_heart_svg_white);
    }
}

