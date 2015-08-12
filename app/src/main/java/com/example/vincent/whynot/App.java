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



public class App extends Application{

    public static App app;
    public MainActivity myActivity;
    private Context myContext;
    private Location userLocation;
    private static ArrayList<Event> eventsArray;
    private int offset = 0;
    private int eventsCount = 0;
    public static double radiusLength = 5;
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

    // Get events data string from eventfinda, called from
    // the LocationManagerAsyncTask onPostExecute() method
    public void getEventsStringHTTPRequest() {
        ConnectToRESTAsyncTask httpRequest = new ConnectToRESTAsyncTask(this);
        httpRequest.execute();
    }

    // Build the events array from the string returned by
    // the http request, called in the ConnectToRESTAsyncTask onPostExecute() method
    public void getEventsArrayFromString(String xmlString) {
        XMLParserAsyncTask xmlParser = new XMLParserAsyncTask(this, xmlString);
        xmlParser.execute();
    }

    // Class property setters and getters

    public void setUserLocation(Location newLocation) {
        this.userLocation = newLocation;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setEventsArray(ArrayList<Event> newEventsArray) {
        eventsArray = newEventsArray;
    }

    public void appendEvents(ArrayList<Event> newEventsArray) {
        for (Event event : newEventsArray) {
            eventsArray.add(event);
        }
    }

    public ArrayList<Event> getEventsArray() {
        return eventsArray;
    }

    public double getRadiusLength() {
        return radiusLength;
    }

    public void setOffset(int newOffset) {
        System.out.println("Testing: Offset = " + newOffset);
        System.out.println("Testing: Events count = " + eventsCount);
        offset = newOffset;
    }

    public void setEventsCount(int newEventsCount) {
        System.out.println("Testing: Events count = " + newEventsCount);
        eventsCount = newEventsCount;
    }

    public int getOffset() {
        return offset;
    }

    public int getEventsCount() {
        return eventsCount;
    }

}
