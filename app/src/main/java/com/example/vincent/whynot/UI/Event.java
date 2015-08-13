package com.example.vincent.whynot.UI;


import android.location.Location;

import com.example.vincent.whynot.App;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Event {
    private App myApp;
    private String id;
    private String name;
    private String description;
    private String dt_start;
    private String endDate;
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
    private String category;


    // New event constructor that initialises all values rather than using setters
    // as Event objects are read only effectively. Yet to be used to build events, still
    // using setters
    public Event(App app, String id, String name, String description, String location,
                 double latitude, double longitude, boolean free, String restrictions,
                 String distanceTo, String cheapest, String webpage, String category) {
        this.myApp = app;
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.free = free;
        this.restrictions = restrictions;
        this.distanceTo = distanceTo;
        this.cheapest = cheapest;
        this.webpage = webpage;
        this.category = category;
    }

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
        this.name = stripCDATA(name);
    }

    public void setDescription(String desc){

        if (desc.length() > 10) {
            this.description = stripCDATA(desc);
        } else {
            this.description = "No available description";
        }
    }

    public void setDt_start(String dstart){
        this.dt_start = dstart;
    }

    public void setEndDate(String endDateString) {
        this.endDate = endDateString;
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

    public String getEndDate() {
        return this.endDate;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = stripCDATA(category);
    }

    public String getDistance() {
        return this.distanceTo ;
    }

    public void setDistance() {
        Location userLocation = myApp.getUserLocation();
        double userLat = userLocation.getLatitude();
        double userLong = userLocation.getLongitude();
        float[] results = {1};
        userLocation.distanceBetween(userLat, userLong, this.latitude, this.longitude, results);
        this.distanceTo = Float.toString(results[0]);
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

    public boolean verifyDate() {
        GregorianCalendar eventEndDate = new GregorianCalendar();
        GregorianCalendar endOfToday = new GregorianCalendar();
        endOfToday.set(Calendar.HOUR_OF_DAY, 23);
        endOfToday.set(Calendar.MINUTE, 59);
        endOfToday.set(Calendar.SECOND, 59);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(endDate);
            eventEndDate.setTime(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        long timeDifference = endOfToday.getTimeInMillis() - eventEndDate.getTimeInMillis();
        if (timeDifference >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verifyLocation(){
        Location userLocation = myApp.getUserLocation();
        double userLat = userLocation.getLatitude();
        double userLong = userLocation.getLongitude();
        float[] results = {1};
        userLocation.distanceBetween(userLat, userLong, this.latitude, this.longitude, results);
        String dist = String.valueOf(results[0]);
        if (results[0] < myApp.radiusLength) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verifySelf() {
        if (verifyDate()) {
            return true;
        } else {
            return false;
        }
    }

    public String getOverview(){
        String nl = System.getProperty("line.separator");
        String output = "\n" + this.description + "\n\n";
        output += "" + this.cheapest + " event";

        //if(restriction > 0) output += "\nMinimum age: " + this.ageRestriction;

        output += "%" + this.getWebpage() + "\n";
        return output;
    }

    public String stripCDATA(String s) {
        s = s.trim();
        if (s.startsWith("<![CDATA[")) {
            s = s.substring(9);
            int i = s.indexOf("]]>");
            if (i == -1) {
                i = s.length();
            }
            s = s.substring(0, i);
        }
        return s;
    }


    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
