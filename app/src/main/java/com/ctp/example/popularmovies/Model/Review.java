package com.ctp.example.popularmovies.Model;

/**
 * Created by clinton on 2/10/18.
 */

public class Review {

    private String reviewId;

    private String author;

    private String content;

    private String url;

    public Review(){}

    public Review(String reviewId, String author, String content, String url) {
        this.reviewId = reviewId;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
