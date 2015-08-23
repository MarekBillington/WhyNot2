package com.example.vincent.whynot.UI;


import android.graphics.Bitmap;
import android.location.Location;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Event {

    public static final int CATEGORY_CONCERTS_GIG = 6, CATEGORY_SPORTS_OUTDOORS = 7, CATEGORY_WORKSHOPS_CLASSES = 3,
                            CATEGORY_FESTIVALS_LIFESTYLE = 190, CATEGORY_PERFORMING_ARTS = 1, CATEGORY_EXHIBITIONS = 11;

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
    private String ticketUrl;
    private String distanceTo;
    private String cheapest;
    private String webpage;
    private int category;


    // New event constructor that initialises all values rather than using setters
    // as Event objects are read only effectively. Yet to be used to build events, still
    // using setters
    public Event(App app, String id, String name, String description, String location,
                 double latitude, double longitude, boolean free, String restrictions,
                 String distanceTo, String cheapest, String webpage, int category) {
        this.myApp = app;
        this.id = id;
        this.name = stripCDATA(name);
        this.description = stripCDATA(description);
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
        this.location = stripCDATA(loc);
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

    /** Removes the city name out of the location string. **/
    public String getLocationShort(){
        String[] parts = this.location.split(",");
        String shortened = "";
        if(parts.length > 1) {
            for (int i = 0; i < parts.length - 1; i++) {
                shortened += parts[i];
            }
        } else shortened = parts[0];
        if (shortened.length() > 35) shortened = shortened.substring(0, 35) + "...";

        return shortened;
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDistance() {
        return this.distanceTo ;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    /** Returns the name of the event to a max length of 50 chars. **/
    public String formatName(){
        if (name.length() > 50){
            return name.substring(0, 50) + "...";
        } else{
            return name;
        }
    }

    /** Returns the time of the event formatted. **/
    public String formatTime(){
        String time = dt_start.substring(dt_start.length() - 9, dt_start.length() - 3);
        if (Integer.parseInt(time.substring(1, 3)) > 12 ){
            return String.valueOf(Integer.parseInt(time.substring(1, 3)) - 12) + time.substring(3, 6) + "pm";
        } else{
            return time + "am";
        }
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

    /** Strips CDATA tag off of XML text. **/
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

    /** Returns the correct marker based on the Event's category. **/
    public BitmapDescriptor getMarker(){
        BitmapDescriptor marker = null;

        if(this.category == CATEGORY_CONCERTS_GIG) marker = BitmapDescriptorFactory.fromResource(R.drawable.gig_marker);
        else if(this.category == CATEGORY_EXHIBITIONS) marker = BitmapDescriptorFactory.fromResource(R.drawable.museum_marker);
        else if(this.category == CATEGORY_FESTIVALS_LIFESTYLE) marker = BitmapDescriptorFactory.fromResource(R.drawable.festival_marker);
        else if(this.category == CATEGORY_PERFORMING_ARTS) marker = BitmapDescriptorFactory.fromResource(R.drawable.performance_marker);
        else if(this.category == CATEGORY_SPORTS_OUTDOORS) marker = BitmapDescriptorFactory.fromResource(R.drawable.sport_marker);
        else if(this.category == CATEGORY_WORKSHOPS_CLASSES) marker = BitmapDescriptorFactory.fromResource(R.drawable.workshop_marker);
        else marker = BitmapDescriptorFactory.fromResource(R.drawable.eventicon);

        return marker;
    }


    /** Seeing as category is stored as an int, this gives a string representation of it **/
    public String getCategoryString(){
        if(this.category == CATEGORY_CONCERTS_GIG) return "Concerts and Gigs";
        else if(this.category == CATEGORY_EXHIBITIONS) return "Exhibitions";
        else if(this.category == CATEGORY_FESTIVALS_LIFESTYLE) return "Festivals and Lifestyle";
        else if(this.category == CATEGORY_SPORTS_OUTDOORS) return "Sports and Outdoors";
        else if(this.category == CATEGORY_WORKSHOPS_CLASSES) return "Workshops, Classes and Courses";
        else if(this.category == CATEGORY_PERFORMING_ARTS) return "Performing Arts";
        else return "Other";
    }


    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
