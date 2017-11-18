package com.ctp.example.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctp.example.popularmovies.Model.Movie;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView synopsis;
    private TextView releaseDate;
    private TextView userRating;
    private Movie movie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        moviePoster = findViewById(R.id.details_mov_poster);
        movieTitle =  findViewById(R.id.details_mov_title);
        synopsis = findViewById(R.id.details_synopsis_content);
        releaseDate = findViewById(R.id.details_release_date);
        userRating = findViewById(R.id.details_usr_rating);

        getMovieObjectFromSender();

        movieTitle.setText(movie.getTitle());
        Picasso.with(this).load(movie.getThumbnailLink())
                .error(R.drawable.placeholder_img)
                .placeholder(R.drawable.placeholder_img)
                .into(moviePoster);

        userRating.append(" "+Integer.toString(movie.getUserRating()));
        releaseDate.append(" " +movie.getReleaseDate());
        synopsis.setText(movie.getSynopsis());

    }


    private void getMovieObjectFromSender() {
        Intent receivedIntent = getIntent();
        if(receivedIntent!=null){

            if(receivedIntent.hasExtra("movieObj")){

                movie = (Movie) receivedIntent.getSerializableExtra("movieObj");

            }
        }
    }
}
