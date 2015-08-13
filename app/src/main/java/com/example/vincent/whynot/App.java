package com.example.vincent.whynot;


import android.app.Application;
import android.content.Context;

import android.location.Location;
import android.location.LocationManager;

import com.example.vincent.whynot.Backend.ConnectToRESTAsyncTask;
import com.example.vincent.whynot.Backend.LocationManagerAsyncTask;
import com.example.vincent.whynot.Backend.XMLParserAsyncTask;
import com.example.vincent.whynot.UI.Event;
import com.example.vincent.whynot.UI.MainActivity;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


public class App extends Application{

    public static App app;
    public MainActivity myActivity;
    private Context myContext;
    private Location userLocation;
    private static CopyOnWriteArrayList<Event> eventsArray;
    private int offset = 0;
    private int eventsCount = 0;
    public static double radiusLength = 1;
    private final String eventFindaAPIUsername = "whynot";
    private final String eventFindaAPIPassword = "kd87ymx3txqv";


    public App(Context context, MainActivity mainActivity) {
        super.onCreate();

        // Holds reference to front end Main Activity to allow callback functions
        myActivity = mainActivity;
        // Holds reference to Applications overall context
        myContext = context;
        // Set the overall HTTP Authentication credentials
        setEventFindaAPIAuthentication();
        // Begin the chain of async tasks that will eventually
        // return the array of events to this App class
        startAsyncTaskChain();

    }

    // Set the global authentication credentials
    private void setEventFindaAPIAuthentication() {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(eventFindaAPIUsername,
                        eventFindaAPIPassword.toCharArray());
            }
        });
    }

    // begin the async task chain that will end with building the events array
    private void startAsyncTaskChain() {
        // Initial async task that finds out where the user is located,
        // additional async task for requesting the data and parsing
        // the returned xml file are initialised within each preceding
        // async task in a chain like format
        getUserLocationFromGPS();
    }

    // Functions for creating and executing relevant async tasks,

    // Get user location async task, called in the App constructor
    private void getUserLocationFromGPS() {
        // creates and begins a new async task that assigns the user's current
        // location to this App instances userLocation attribute
        LocationManager locationManager =
                (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
        LocationManagerAsyncTask locationManagerAsyncTask =
                new LocationManagerAsyncTask(this, locationManager);
        locationManagerAsyncTask.execute();
    }

    public void setEventsCountFromHTTPRequest() {

    }

    // Get events data string from eventfinda, called from
    // the LocationManagerAsyncTask onPostExecute() method
    public void getEventsStringHTTPRequest(int asyncTaskOffset) {
        ConnectToRESTAsyncTask httpRequest = new ConnectToRESTAsyncTask(this, asyncTaskOffset);
        httpRequest.execute();
    }

    // Build the events array from the string returned by
    // the http request, called in the ConnectToRESTAsyncTask onPostExecute() method
    public void getEventsArrayFromString(String xmlString, int asyncTaskOffset) {
        XMLParserAsyncTask xmlParser = new XMLParserAsyncTask(this, xmlString, asyncTaskOffset);
        xmlParser.execute();
    }

    // Class property setters and getters

    public void setUserLocation(Location newLocation) {
        this.userLocation = newLocation;
    }

    public void setEventsArray(CopyOnWriteArrayList<Event> newEventsArray) {
        eventsArray = newEventsArray;
    }

    public void appendEvents(CopyOnWriteArrayList<Event> newEventsArray) {
        for (Event event : newEventsArray) {
            eventsArray.add(event);
        }
    }

    public void setOffset(int newOffset) {
        System.out.println("Testing: Setting Offset (should happen until more than (" + eventsCount + ")) = " + newOffset);
        offset = newOffset;
    }

    public void setEventsCount(int newEventsCount) {
        System.out.println("Testing: Setting Events count (should only happen once) = " + newEventsCount);
        eventsCount = newEventsCount;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public CopyOnWriteArrayList<Event> getEventsArray() {
        return eventsArray;
    }

    public double getRadiusLength() {
        return radiusLength;
    }

    public int getOffset() {
        return offset;
    }

    public int getEventsCount() {
        return eventsCount;
    }

}
