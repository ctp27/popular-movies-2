package com.ctp.example.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctp.example.popularmovies.Model.Trailer;
import com.ctp.example.popularmovies.R;

import java.util.List;

/**
 * Created by clinton on 2/11/18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>{

    private List<Trailer> trailerList;
    private TrailerListClickCallback clickCallback;
    private String movieName;

    public TrailerAdapter(List<Trailer> trailerList, TrailerListClickCallback clickCallback, String movieName) {
        this.trailerList = trailerList;
        this.clickCallback = clickCallback;
        this.movieName = movieName;
    }

    public interface TrailerListClickCallback{
        void onTrailerClicked(Trailer t);
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View theView = inflater.inflate(R.layout.trailer_list_row_item,parent,false);

        return new TrailerAdapterViewHolder(theView);

    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {

        Trailer t = trailerList.get(position);
        String trailerTitle = movieName +" -  "+t.getName();
        holder.trailerName.setText(trailerTitle);

    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView trailerName;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            trailerName = itemView.findViewById(R.id.trailer_list_trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            clickCallback.onTrailerClicked(trailerList.get(position));
        }
    }
}
