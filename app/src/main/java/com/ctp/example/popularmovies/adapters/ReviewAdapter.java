package com.ctp.example.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctp.example.popularmovies.Model.Review;
import com.ctp.example.popularmovies.R;

import java.util.List;

/**
 * Created by clinton on 2/11/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View theView = inflater.inflate(R.layout.review_list_row_item,parent,false);

        return new ReviewAdapterViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {

        Review thisReview = reviews.get(position);
        holder.author.setText(thisReview.getAuthor());
        holder.content.setText(thisReview.getContent());

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void swapData(List<Review> newReviewData){
        if(reviews!=null){
            reviews=null;
        }
        reviews = newReviewData;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView author;
        private TextView content;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.review_list_reviewer_name);
            content = itemView.findViewById(R.id.review_list_content);
        }

    }

}
