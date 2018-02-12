package com.ctp.example.popularmovies.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by clinton on 2/8/18.
 */

public class MovieDbContract {

    public static final String AUTHORITY = "com.ctp.example.popularmovies.provider";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";



    public static final class MovieFavoriteEntry implements BaseColumns {

        public static final Uri FAVORITE_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                                    .appendPath(PATH_FAVORITES).build();

        public static Uri getUriForMovieId(int id){
            return FAVORITE_CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movieId";

        public static final String COLUMN_MOVIE_POSTER_LINK = "poster";

        public static final String COLUMN_MOVIE_NAME = "movieName";

        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_RATING = "rating";

    }

}
