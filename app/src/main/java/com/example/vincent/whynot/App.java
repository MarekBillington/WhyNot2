package com.example.vincent.whynot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import com.example.vincent.whynot.Backend.ConnectToRESTAsyncTask;
import com.example.vincent.whynot.Backend.LocationManagerAsyncTask;
import com.example.vincent.whynot.Backend.XMLParserAsyncTask;
import com.example.vincent.whynot.UI.Event;
import com.example.vincent.whynot.UI.MainActivity;
import com.google.android.gms.maps.GoogleMap;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.example.vincent.whynot.UI.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class App extends Application{


    public static App app;


    public MainActivity myActivity;
    private Context myContext;
    private String eventString;

    private Location userLocation;
    private static ArrayList<Event> eventsArray;
    public static double radiusLength = 100000;
    private final String username = "whynot";
    private final String password = "kd87ymx3txqv";


    public App(Context context, MainActivity mainActivity) {
        super.onCreate();
        myActivity = mainActivity;
        myContext = context;
        getEventsDataHTTPRequest();
        getUserLocationFromGPS();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void getEventsNodeList(String xmlString) {
        XMLParserAsyncTask xmlParser = new XMLParserAsyncTask(this, xmlString);
        xmlParser.execute();
    }

    private void getEventsDataHTTPRequest() {

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
        System.out.println("building async task");
        ConnectToRESTAsyncTask httpRequest = new ConnectToRESTAsyncTask(this, this);
        httpRequest.execute();
    }

    public void setEventsString(String eventString) {
        this.eventString = eventString;
    }

    private void getUserLocationFromGPS() {
        LocationManager locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
        LocationManagerAsyncTask locationManagerAsyncTask = new LocationManagerAsyncTask(this, locationManager);
        locationManagerAsyncTask.execute();
    }

    public void setUserLocation(Location newLocation) {
        this.userLocation = newLocation;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setEventsArray(ArrayList<Event> newEventsArray) {
        eventsArray = newEventsArray;
    }

    public ArrayList<Event> getEventsArray() {
        return eventsArray;
    }

    public String getEndDateTimeString() {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar calendar = new GregorianCalendar();
        String dateString = date_format.format(calendar.getTime());
        String endDateTimeString = dateString + "%2023:59:59";
        System.out.println("End date time string = " + endDateTimeString);
        return endDateTimeString;
    }
}
