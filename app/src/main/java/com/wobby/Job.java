package com.wobby;

import com.google.android.gms.maps.model.LatLng;

public class Job {

    //java beans
    //class that has only information

    private String id;
    private String title;
    private String snippet;
    private float wage;
    private LatLng latLng;

    public Job(String id, String title, String snippet, float wage, float latitude, float longitude){
        this.id = id;
        this.title = title;
        this.snippet = snippet;
        this.wage = wage;
        this.latLng = new LatLng(latitude, longitude);
    }

    public Job(String title, String snippet, float wage, LatLng latLng){
        this.title = title;
        this.snippet = snippet;
        this.wage = wage;
        this.latLng = latLng;
    }

    public String getJobId(){
        return id;
    }
    public String getJobTitle() {
        return title;
    }

    public String getJobSnippet(){
        return snippet;
    }

    public float getJobWage(){
        return wage;
    }

    public LatLng getJobLatLng(){
        return latLng;
    }

    public String jobToJSON(){
        return "{\"title\":\"" + getJobTitle() + "\",\"snippet\":\"" + getJobSnippet() + "\",\"wage\":" + getJobWage() + ",\"lat\":" + getJobLatLng().latitude + ",\"long\":" + getJobLatLng().longitude + "}";
    }

    public String jobToJSONWithID(){
        return "{\"_id\":" + getJobId() + "\",\"title\":\"" + getJobTitle() + "\",\"snippet\":\"" + getJobSnippet() + "\",\"wage\":" + getJobWage() + ",\"lat\":" + getJobLatLng().latitude + ",\"long\":" + getJobLatLng().longitude + "}";
    }
}
