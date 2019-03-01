package com.example.mobileliarsdice.Models;

import java.util.List;

public class Users extends UserInfo{


    private double longitude;
    private double latitude;
    private Boolean online;
    private float score;
    private int distance;
    public  Users(){
        super();
    }


    public Users(String userName, String url, String id, double longitude, double latitude, Boolean online, float score, int distance) {
        super(id,userName, url);
        this.longitude = longitude;
        this.latitude = latitude;
        this.online = online;
        this.score = score;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }


}
