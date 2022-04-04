package com.example.mylocationandactivityapp;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MyLocation implements Serializable {
    private String activity;
    private String address;
    private String lastUpdateTime;
    private double latitude;
    private double longitude;
    private String key;

    public MyLocation (double latitude, double longitude, String address, String lastUpdateTime, String activity){
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setAddress(address);
        this.setLastUpdateTime(lastUpdateTime);
        this.setActivity(activity);

    }

    public LatLng getLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
