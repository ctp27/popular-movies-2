package com.ctp.example.popularmovies.Model;

/**
 * Created by clinton on 2/10/18.
 */

public class Trailer {

    private String trailerId;

    private String isoString;

    private String key;

    private String name;

    private String site;

    private String type;

    public Trailer(String trailerId, String isoString, String key, String name, String site, String type) {
        this.trailerId = trailerId;
        this.isoString = isoString;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    public Trailer(){}


    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public String getIsoString() {
        return isoString;
    }

    public void setIsoString(String isoString) {
        this.isoString = isoString;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "trailerId='" + trailerId + '\'' +
                ", isoString='" + isoString + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
