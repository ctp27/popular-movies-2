package com.ctp.example.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctp.example.popularmovies.AsyncTasks.ReviewJsonDownloadLoader;
import com.ctp.example.popularmovies.AsyncTasks.TrailerJsonDownloadLoader;
import com.ctp.example.popularmovies.Model.Movie;
import com.ctp.example.popularmovies.Model.Review;
import com.ctp.example.popularmovies.Model.Trailer;
import com.ctp.example.popularmovies.adapters.ReviewAdapter;
import com.ctp.example.popularmovies.adapters.TrailerAdapter;
import com.ctp.example.popularmovies.provider.MovieDbContract;
import com.ctp.example.popularmovies.services.DataPersistenceService;
import com.ctp.example.popularmovies.utils.FavoritesUtils;
import com.ctp.example.popularmovies.utils.PopularMovieUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

public class DetailsActivity extends AppCompatActivity
                    implements LoaderManager.LoaderCallbacks<Object>,
                    TrailerAdapter.TrailerListClickCallback{

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final int CURSOR_LOADER_ALL_DETAILS_KEY = 111;
    private static final int CURSOR_LOADER_CHECK_IF_FAVORITE_KEY = 112;
    private static final int LOADER_GET_REVIEWS_KEY = 113;
    private static final int LOADER_GET_TRAILERS_KEY = 114;

    private static final String RATING_OUT_OF = "/10";
    private static final String YOUTUBE_APP_BASE_URI = "vnd.youtube:";
    private static final String WEB_BROWSER_BASE_URI = "http://www.youtube.com/watch?v=";


    public static final String MOVIE_OBJECT_TRANSFER_KEY="movie-tranfer-object";
    public static final String MOVIE_IS_CURSOR_DATA_KEY = "cursor-data-key";
    public static final String MOVIE_STORED_ID_KEY="stored-movie_id";



    private static final String LOADER_BUNDLE_MOVIE_KEY="movie-id-key";

    private static final String INSTANCE_BOOLEAN_STATE_KEY = "booll";
    private static final String BUNDLE_SCROLL_POSITION = "scroll-bundle" ;


    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView synopsis;
    private TextView releaseDate;
    private TextView userRating;
    private ImageButton favoriteBtn;
    private TextView noTrailerTextView;
    private TextView noReviewTextView;

    private Movie movie;
    private Toast theToast;

    private RecyclerView trailerListView;
    private RecyclerView reviewListView;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private Trailer firstTrailer;
    private ScrollView scrollView;

    private boolean isAddedToFavorites;
    private boolean isCursorData;

    private int scrollY = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        scrollView = findViewById(R.id.details_scroll_view);
        moviePoster = findViewById(R.id.details_movie_poster);
        movieTitle =  findViewById(R.id.details_movie_title);
        synopsis = findViewById(R.id.details_synopsis);
        releaseDate = findViewById(R.id.details_release_date);
        userRating = findViewById(R.id.details_user_rating);
        favoriteBtn = findViewById(R.id.add_to_fav_btn);
        trailerListView = findViewById(R.id.details_trailer_list);
        reviewListView = findViewById(R.id.details_review_list);
        noTrailerTextView = findViewById(R.id.details_no_trailer_textview);
        noReviewTextView = findViewById(R.id.details_no_reviews_textview);

        reviewListView.setNestedScrollingEnabled(false);
        trailerListView.setNestedScrollingEnabled(false);

        LinearLayoutManager manager1 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        LinearLayoutManager manager2 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        trailerListView.setLayoutManager(manager1);
        reviewListView.setLayoutManager(manager2);
        trailerListView.setHasFixedSize(true);
        reviewListView.setHasFixedSize(true);

        getMovieObjectFromSender();

        if(savedInstanceState!=null){
            scrollY = savedInstanceState.getInt(BUNDLE_SCROLL_POSITION);
        }




    }


    private void getMovieObjectFromSender() {
        Intent receivedIntent = getIntent();
        if(receivedIntent!=null){

            if(receivedIntent.hasExtra(MOVIE_IS_CURSOR_DATA_KEY)){
                isCursorData = receivedIntent.getBooleanExtra(MOVIE_IS_CURSOR_DATA_KEY,false);

            }

            if(isCursorData){
                int movieId = receivedIntent.getIntExtra(MOVIE_STORED_ID_KEY,-1);
//                TODO: startCursorLoader to get this movie information
                Bundle bundle = new Bundle();
                bundle.putInt(MOVIE_STORED_ID_KEY,movieId);
                getSupportLoaderManager().restartLoader(CURSOR_LOADER_ALL_DETAILS_KEY,bundle,this);

            }
            else{
                movie = (Movie) receivedIntent.getSerializableExtra(MOVIE_OBJECT_TRANSFER_KEY);
                displayDataFromMovieObject();
//               TODO: Call method to initialize the data
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(INSTANCE_BOOLEAN_STATE_KEY,isAddedToFavorites);
        outState.putInt(BUNDLE_SCROLL_POSITION,scrollView.getScrollY());

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.share_btn:
                shareMovieTrailer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

            case LOADER_GET_REVIEWS_KEY:
                int movieId = args.getInt(LOADER_BUNDLE_MOVIE_KEY);
                return new ReviewJsonDownloadLoader(this,movieId);

            case LOADER_GET_TRAILERS_KEY:
                int movId = args.getInt(LOADER_BUNDLE_MOVIE_KEY);
                return new TrailerJsonDownloadLoader(this,movId);
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
            case LOADER_GET_REVIEWS_KEY:
                String reviewData = (String)data;
                displayReviewData(reviewData);
                break;
            case LOADER_GET_TRAILERS_KEY:
                String trailerData = (String)data;
                displayTrailerData(trailerData);
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
        executeTrailerAndReviewLoaders(movie.getId());
        //TODO: start task to get Movies and reviews. Create a method


    }

    private void displayDataFromMovieObject(){
        movieTitle.setText(movie.getTitle());

        Picasso.with(this).load(movie.getThumbnailLink())
                .error(R.drawable.placeholder_img)
                .placeholder(R.drawable.placeholder_img)
                .into(moviePoster);

        userRating.append(Integer.toString(movie.getUserRating())+RATING_OUT_OF);
        releaseDate.append(" " +movie.getReleaseDate());
        synopsis.setText(movie.getSynopsis());

        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_STORED_ID_KEY,movie.getId());
        getSupportLoaderManager().restartLoader(CURSOR_LOADER_CHECK_IF_FAVORITE_KEY,bundle,this);
        executeTrailerAndReviewLoaders(movie.getId());


    }

    private void displayReviewData(String reviewData){

        if(reviewData==null){
            displayNoReviewsMessage();
            return;
        }

        List<Review> reviewsList=null;

        try {
           reviewsList =  PopularMovieUtils.getReviewListFromJson(reviewData);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        if(!reviewsList.isEmpty()){
            reviewAdapter = new ReviewAdapter(reviewsList);
            reviewListView.setAdapter(reviewAdapter);

        }else {
            displayNoReviewsMessage();
        }
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,scrollY);
            }
        });
    }

    private void displayTrailerData(String trailerData){

        if(trailerData ==null){
            displayNoTrailerMessage();
            return;
        }

        List<Trailer> trailers = null;

        try{
            trailers = PopularMovieUtils.getTrailerListFromJson(trailerData);
        }catch (JSONException e){

        }

        if(!trailers.isEmpty()){
            trailerAdapter = new TrailerAdapter(trailers,this,movie.getTitle());
            trailerListView.setAdapter(trailerAdapter);
            firstTrailer = trailers.get(0);
        }
        else{
            displayNoTrailerMessage();
        }
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
    public void onTrailerClicked(Trailer t) {

        Uri youtubeAppUri = Uri.parse(YOUTUBE_APP_BASE_URI+t.getKey());
        Intent appIntent = new Intent(Intent.ACTION_VIEW, youtubeAppUri);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(WEB_BROWSER_BASE_URI + t.getKey()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }

    }

    private void shareMovieTrailer(){

        if(firstTrailer==null) {
            Toast.makeText(this,"Currently no trailers to share",Toast.LENGTH_LONG).show();
            return;
        }

        String mimeType = "text/plain";
        String title = movie.getTitle() +" - "+ firstTrailer.getName();
        String content = getString(R.string.share_movie_text)+" "+movie.getTitle()+"\n"+
                WEB_BROWSER_BASE_URI+firstTrailer.getKey();

        ShareCompat.IntentBuilder
                .from(this).setType(mimeType)
                .setChooserTitle(title)
                .setText(content)
                .startChooser();
    }


    private void showAddingToFavoriteToast(){
        if(theToast !=null){
            theToast.cancel();
        }
        theToast= Toast.makeText(this,getString(R.string.added_to_fav_toast),Toast.LENGTH_LONG);
        theToast.show();
    }

    private void showRemovingFromFavoriteToast(){
        if(theToast !=null){
            theToast.cancel();
        }
        theToast= Toast.makeText(this,getString(R.string.rem_from_fav_toast),Toast.LENGTH_LONG);
        theToast.show();

    }

    private void highlightFavoriteButtonBasedOnBoolean(boolean isAFavorite){
        if(isAFavorite)
            favoriteBtn.setImageResource(R.drawable.ic_love_heart_svg);
        else
            favoriteBtn.setImageResource(R.drawable.ic_love_heart_svg_white);
    }


    private void executeTrailerAndReviewLoaders(int movieId){
        Bundle bundle = new Bundle();
        bundle.putInt(LOADER_BUNDLE_MOVIE_KEY,movieId);
        getSupportLoaderManager().restartLoader(LOADER_GET_REVIEWS_KEY,
                bundle,this);
        getSupportLoaderManager().restartLoader(LOADER_GET_TRAILERS_KEY,
                bundle,this);
    }

    private void displayNoTrailerMessage(){
        noTrailerTextView.setVisibility(View.VISIBLE);
        trailerListView.setVisibility(View.GONE);
    }

    private void displayNoReviewsMessage(){
        noReviewTextView.setVisibility(View.VISIBLE);
        reviewListView.setVisibility(View.GONE);
    }



}

