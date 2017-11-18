package com.ctp.example.popularmovies.Model;

import java.io.Serializable;

/**
 * Created by clinton on 11/17/17.
 */

public class Movie implements Serializable{

    private int id;

    /* movie poster image thumbnail link*/
    private String thumbnailLink;

    /* original title */
    private String title;

    /* A plot synopsis (called overview in the api) */
    private String synopsis;

    /*  user rating (called vote_average in the api) */
    private int userRating;

    /*  release date  */
    private String releaseDate;

    public Movie(){}

    public Movie(int id, String thumbnailLink, String title, String synopsis,
                                    int userRating, String releaseDate) {
        this.id = id;
        this.thumbnailLink = thumbnailLink;
        this.title = title;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", thumbnailLink='" + thumbnailLink + '\'' +
                ", title='" + title + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", userRating=" + userRating +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
