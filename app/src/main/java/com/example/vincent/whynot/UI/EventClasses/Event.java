package com.example.vincent.whynot.UI.EventClasses;


import android.content.Context;
import android.location.Location;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;


public class Event {

    public static final int CATEGORY_CONCERTS_GIG = 6, CATEGORY_SPORTS_OUTDOORS = 7, CATEGORY_WORKSHOPS_CLASSES = 3,
            CATEGORY_FESTIVALS_LIFESTYLE = 190, CATEGORY_PERFORMING_ARTS = 1, CATEGORY_EXHIBITIONS = 11;

    private String id;
    private String name;
    private String description;
    private String time;
    private String startDate;
    private String endDate;
    private String address;
    private Location location;
    private double latitude;
    private double longitude;
    private String restrictions;
    private String imageUrl;
    private String ticketUrl;
    private String distance;
    private String cheapest;
    private String webpage;
    private int category;


    // New event constructor that initialises all values rather than using setters
    // as Event objects are read only effectively. Yet to be used to build events, still
    // using setters
    public Event(String id, String name, String description, String address,
                 double latitude, double longitude, String restrictions,
                 String webpage, int category, String imageUrl, String startDate,
                 String endDate, String ticketUrl) {
        this.id = id;
        this.name = formatName(name);
        this.description = stripCDATA(description);
        this.address = makeAddressShort(address);
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = makeLocation();
        this.restrictions = restrictions;
        this.webpage = webpage;
        this.category = category;
        this.imageUrl = imageUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.time = formatTime(startDate);
        this.ticketUrl = ticketUrl;
        setDistance();
    }

    public Event() {
    }


    /**
     * Removes the city name out of the address string.
     **/
    public String makeAddressShort(String address) {
        address = stripCDATA(address);
        String[] parts = address.split(",");
        String shortened = "";
        if (parts.length > 1) {
            for (int i = 0; i < parts.length - 1; i++) {
                shortened += parts[i];
            }
        } else shortened = parts[0];
        if (shortened.length() > 28) shortened = shortened.substring(0, 25) + "...";

        return shortened;
    }

    public String makeDistanceShort(String distanceString){
        float distance = Float.parseFloat(distanceString)/1000;
        return String.format("%.01f", distance);
    }

    public Location makeLocation() {
        // Change to maps fragment and go to location
        final Location eventLocation = new Location("");

        eventLocation.setLatitude(this.getLatitude());
        eventLocation.setLongitude(this.getLongitude());
        return eventLocation;
    }

    /**
     * Returns the name of the event to a max length of 50 chars.
     **/
    public String formatName(String name) {
        name = stripCDATA(name);
        if (name.length() > 35) {
            return name.substring(0, 33) + "...";
        } else {
            return name;
        }
    }

    /**
     * Returns the time of the event formatted.
     **/
    public String formatTime(String startDate) {
        String time = startDate.substring(startDate.length() - 9, startDate.length() - 3);
        if (Integer.parseInt(time.substring(1, 3)) > 12) {
            return String.valueOf(Integer.parseInt(time.substring(1, 3)) - 12) + time.substring(3, 6) + "pm";
        } else {
            return time + "am";
        }
    }

    public void setDistance() {
        Location userLocation = App.userLocation;
        double userLat = userLocation.getLatitude();
        double userLong = userLocation.getLongitude();
        float[] results = {1};
        userLocation.distanceBetween(userLat, userLong, this.latitude, this.longitude, results);
        this.distance = makeDistanceShort(Float.toString(results[0]));
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

    /**
     * Strips CDATA tag off of XML text.
     **/
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

    /**
     * Returns the correct marker based on the Event's category.
     **/
    public BitmapDescriptor getMarker(Context context) {
        BitmapDescriptor marker = null;

        if (this.category == CATEGORY_CONCERTS_GIG)
            marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_gigmarker);
        else if (this.category == CATEGORY_EXHIBITIONS)
            marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_exhibitionmarker);
        else if (this.category == CATEGORY_FESTIVALS_LIFESTYLE)
            marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_festivalmarker);
        else if (this.category == CATEGORY_PERFORMING_ARTS)
            marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_performancemarker);
        else if (this.category == CATEGORY_SPORTS_OUTDOORS)
            marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_sportmarker);
        else if (this.category == CATEGORY_WORKSHOPS_CLASSES)
            marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_workshopmarker);
        else marker = BitmapDescriptorFactory.fromResource(R.drawable.eventicon);

        return marker;
    }


    /**
     * Seeing as category is stored as an int, this gives a string representation of it
     **/
    public String getCategoryString() {
        if (this.category == CATEGORY_CONCERTS_GIG) return "Concerts and Gigs";
        else if (this.category == CATEGORY_EXHIBITIONS) return "Exhibitions";
        else if (this.category == CATEGORY_FESTIVALS_LIFESTYLE) return "Festivals and Lifestyle";
        else if (this.category == CATEGORY_SPORTS_OUTDOORS) return "Sports and Outdoors";
        else if (this.category == CATEGORY_WORKSHOPS_CLASSES)
            return "Workshops, Classes and Courses";
        else if (this.category == CATEGORY_PERFORMING_ARTS) return "Performing Arts";
        else return "Other";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Event)) return false;
        else if (((Event) obj).getId().equals(this.id)) return true;
        else return false;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Comparators for sorting.
     * **/

    /**
     * Order events by proximity, closest events prioritised.
     **/
    public static Comparator<Event> ProximityComparator = new Comparator<Event>() {

        public int compare(Event event1, Event event2) {
            float event1Distance = App.userLocation.distanceTo(event1.getLocation());
            float event2Distance = App.userLocation.distanceTo(event2.getLocation());
            int ordering = -1;
            if (event1Distance > event2Distance) ordering = 1;
            else if (event1Distance == event2Distance) ordering = 0;

            return ordering;

        }
    };

    /**
     * Order events by price, cheapest events prioritised.
     **/
    public static Comparator<Event> PriceComparator = new Comparator<Event>() {

        public int compare(Event event1, Event event2) {

            int ordering = 0;
            if (event1.getCheapest().equals("Free") && event2.getCheapest().equals("Paid"))
                ordering = -1;
            else if (event1.getCheapest().equals("Paid") && event2.getCheapest().equals("Free"))
                ordering = 1;

            return ordering;

        }
    };

    /** Order events by proximity, closest events prioritised. **/
    /**
     * Needs to be fixed, cannot parse time String in it's current format.
     **/
    public static Comparator<Event> TimeComparator = new Comparator<Event>() {

        public int compare(Event event1, Event event2) {

            Date event1Time = new Date(Date.parse(event1.getStartDate()));
            Date event2Time = new Date(Date.parse(event1.getStartDate()));

            return event1Time.compareTo(event2Time);

        }
    };

    /**
     * Getters and setters
     */


    public void setId(String id) {
        this.id = id;
    }

    public void setWebpage(String wp) {
        this.webpage = wp;
    }

    public void setName(String name) {
        this.name = stripCDATA(name);
    }

    public void setDescription(String desc) {

        if (desc.length() > 10) {
            this.description = stripCDATA(desc);
        } else {
            this.description = "No available description";
        }
    }

    public Location getLocation(){
        return this.location;
    }

    public void setStartDate(String dstart) {
        this.startDate = dstart;
    }

    public void setEndDate(String endDateString) {
        this.endDate = endDateString;
    }

    public void setAddress(String loc) {
        this.address = stripCDATA(loc);
    }

    public void setLatitude(float lat) {
        this.latitude = lat;
    }

    public void setLongitude(float lng) {
        this.longitude = lng;
    }


    public void setCheapest(String cheap) {
        this.cheapest = cheap;
    }

    public void setRestrictions(String rest) {
        this.restrictions = rest;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getCheapest() {
        return this.cheapest;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public String getAddress() {
        return this.address;
    }

    public String getRestrictions() {
        return this.restrictions;
    }

//    public Boolean getFree() { return this.free; }

    public String getWebpage() {
        return this.webpage;
    }

    public void setImgUrl(String url) {
        this.imageUrl = url;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDistance() {
        return this.distance;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public String getTime(){ return this.time; }
}
