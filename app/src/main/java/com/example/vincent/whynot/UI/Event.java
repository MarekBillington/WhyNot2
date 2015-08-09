package com.example.vincent.whynot.UI;

import android.location.Location;
import android.media.Image;


import com.example.vincent.whynot.App;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Vincent on 8/08/2015.
 */
public class Event {
    private App myApp;
    private String id;
    private String name;
    private String description;
    private String dt_start;
    private String location;
    private double latitude;
    private double longitude;
    private ArrayList<String> price;
    private boolean free;
    private String restrictions;
    private String img_url;
    private String distanceTo;
    private String cheapest;
    private String webpage;

    public Event(App app) {
        myApp = app;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setWebpage(String wp) {
        this.webpage = wp;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String desc){
        desc = desc.substring(9,desc.length()-1);
        this.description = desc + "...";
    }

    public void setDt_start(String dstart){
        this.dt_start = dstart;
    }

    public void setLocation(String loc){
        this.location = loc;
    }

    public void setLatitude(float lat){
        this.latitude = lat;
    }

    public void setLongitude(float lng){
        this.longitude = lng;
    }

    public void setFree(String free){
        if(free == "0") {
            this.free = false;
        } else {
            this.free = true;
        }
    }

    public void setCheapest(String cheap ) {
        this.cheapest = cheap;
    }

    public void setRestrictions(String rest){
        this.restrictions = rest;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public String getCheapest() {
        return this.cheapest;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public String getDt_start(){
        return this.dt_start;
    }

    public String getLocation(){
        return this.location;
    }

    public String getRestrictions(){
        return this.restrictions;
    }

    public Boolean getFree() { return this.free; }

    public String getWebpage() {
        return this.webpage;
    }

    public void setImgUrl(String url) {
        this.img_url = url;
    }

    public String getDistance() {
        return this.distanceTo ;
    }


    public void setDistance(String dist) {
        this.distanceTo = dist;
    }

    public boolean verifyLocation(){
        Location userLocation = myApp.getUserLocation();
        double userLat = userLocation.getLatitude();
        double userLong = userLocation.getLongitude();
        float[] results = {1};
        userLocation.distanceBetween(userLat, userLong, this.latitude, this.longitude, results);
        System.out.print("Distance to event = " + results[0]);
        String dist = String.valueOf(results[0]);
        setDistance(dist);
        if (results[0] < myApp.radiusLength) {
            return true;
        } else {
            return false;
        }
    }



    public boolean verifyEvent() {
        if(getFree()) setCheapest("Free");
        else setCheapest("Paid");

        if (verifyLocation()) {
            return true;
        } else {
            return false;
        }
    }

    public String isItCheap() {
        ArrayList<String> prices = new ArrayList<String>();

        String cheap = "Free";

        prices = this.price;
        if(this.getFree() == false) {
            double pointer;
            double compare;
            for (String p : prices) {
                pointer = Double.parseDouble(p);
                for(String pp : prices) {
                    compare = Double.parseDouble(pp);
                    if(pointer > compare) {
                        cheap = String.valueOf(compare);
                    }
                    if(pointer < compare) {
                        cheap = String.valueOf(compare);
                    }
                }
            }
        }

        return cheap;
    }

    public String getOverview(){
        String nl = System.getProperty("line.separator");
        String output = "\n" + this.description + "\n\n";
        output += "" + this.cheapest + " event";

        //if(restriction > 0) output += "\nMinimum age: " + this.ageRestriction;

        //output += "%" + this.link + "\n";
        return output;
    }



}
