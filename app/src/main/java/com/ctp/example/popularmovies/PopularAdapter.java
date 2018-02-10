package com.ctp.example.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ctp.example.popularmovies.Model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by clinton on 11/18/17.
 */

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularAdapterViewHolder> {


    private List<Movie> movieList;
    private PopularAdapterClickListener theClickListener;
    Context context;
    private boolean isCursorData;

    public PopularAdapter(List<Movie> movieList, PopularAdapterClickListener theClickListener,boolean isCursorData) {
        this.movieList = movieList;
        this.theClickListener = theClickListener;
        this.isCursorData = isCursorData;
    }


    public interface PopularAdapterClickListener{
        void onPosterClick(Movie movieClicked,boolean isCursorData);
    }



    @Override
    public PopularAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(context==null) {
            context = parent.getContext();
        }
        LayoutInflater inflater = LayoutInflater.from(context);

        View theView = inflater.inflate(R.layout.recycler_row_item,parent,false);

        return new PopularAdapterViewHolder(theView);

    }

    @Override
    public void onBindViewHolder(PopularAdapterViewHolder holder, int position) {

        String path = movieList.get(position).getThumbnailLink();
        if(isCursorData){
            byte[] data = movieList.get(position).getBitmap();
            Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
            holder.moviePoster.setImageBitmap(bitmap);
        }else {
            Picasso.with(context).load(path)
                    .error(R.drawable.placeholder_img)
                    .placeholder(R.drawable.placeholder_img)
                    .into(holder.moviePoster);
        }


    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }


    public void swapData(List<Movie> newMovieList, boolean isCursorData){
        if(movieList!=null){
            movieList = null;
        }
        movieList = newMovieList;
        this.isCursorData = isCursorData;
        notifyDataSetChanged();
    }



    public class PopularAdapterViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{

        private ImageView moviePoster;

        public PopularAdapterViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.recycler_grid_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            theClickListener.onPosterClick(movieList.get(position),isCursorData);
        }
    }
}
