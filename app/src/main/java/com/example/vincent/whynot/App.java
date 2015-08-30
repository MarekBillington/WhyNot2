package com.example.vincent.whynot;


import android.app.Activity;
import android.app.Application;
import android.content.Context;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.view.View;

import com.example.vincent.whynot.Backend.ConnectToRESTAsyncTask;
import com.example.vincent.whynot.Backend.LocationManagerAsyncTask;
import com.example.vincent.whynot.Backend.XMLParserAsyncTask;
import com.example.vincent.whynot.UI.Event;
import com.example.vincent.whynot.UI.EventBackgroundTarget;
import com.example.vincent.whynot.UI.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


public class App extends Application{

    public static App app;
    public MainActivity myActivity;
    private Context myContext;
    public static Location userLocation;

    /** Buffer array holds all events until all async requests have finished.
     * This means the app can still function while making requests. **/
    public static CopyOnWriteArrayList<Event> eventsArray = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Event> bufferArray = new CopyOnWriteArrayList<>();

    /** To hold strong references to the targets so that they don't get garbage collected. **/
    public static ArrayList<Target> targets = new ArrayList<>();

    /** Fields for EventFinda requests. **/
    private int offset = 0;
    private int eventsCount = 0;
    public static double radiusLength = 5;

    /** Login details for EventFinda API. **/
    private static final String eventFindaAPIUsername = "whynot";
    private static final String eventFindaAPIPassword = "kd87ymx3txqv";


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
    public void startAsyncTaskChain() {
        // Initial async task that finds out where the user is located,
        // additional async task for requesting the data and parsing
        // the returned xml file are initialised within each preceding
        // async task in a chain like format
        bufferArray.clear();
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
        locationManagerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setEventsCountFromHTTPRequest() {

    }

    // Get events data string from eventfinda, called from
    // the LocationManagerAsyncTask onPostExecute() method
    public void getEventsStringHTTPRequest(int asyncTaskOffset) {
        ConnectToRESTAsyncTask httpRequest = new ConnectToRESTAsyncTask(this, asyncTaskOffset);
        httpRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Build the events array from the string returned by
    // the http request, called in the ConnectToRESTAsyncTask onPostExecute() method
    public void getEventsArrayFromString(String xmlString, int asyncTaskOffset) {
        XMLParserAsyncTask xmlParser = new XMLParserAsyncTask(this, xmlString, asyncTaskOffset);
        xmlParser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Class property setters and getters

    public static void setUserLocation(Location newLocation) {
        userLocation = newLocation;
    }

    public void setBufferArray(CopyOnWriteArrayList<Event> newEventsArray) {
        bufferArray = newEventsArray;
    }

    public void appendToBuffer(CopyOnWriteArrayList<Event> newEventsArray) {
        for (Event event : newEventsArray) {
            bufferArray.add(event);
        }
    }

    public void transferEventsFromBuffer(){
        eventsArray = new CopyOnWriteArrayList<>(bufferArray);
        //bufferArray.clear();
    }

    public void setOffset(int newOffset) {
        System.out.println("Testing: Setting Offset (should happen until more than (" + eventsCount + ")) = " + newOffset);
        offset = newOffset;
    }

    public void setEventsCount(int newEventsCount) {
        System.out.println("Testing: Setting Events count (should only happen once) = " + newEventsCount);
        eventsCount = newEventsCount;
    }

    public static Event getEventByID(int id){
        for(Event event: eventsArray){
            if(Integer.parseInt(event.getId()) == id) return event;
        }
        return null;
    }

    /**
     * Sets an image to the background using Picasso, if event doesn't have an image.
     * it will assign a generic image instead.
     **/
    public static void setEventImage(Activity activity, View banner, Event event) {
        EventBackgroundTarget eventBackgroundTarget = new EventBackgroundTarget(activity, banner);
        if (!event.getImg_url().isEmpty()) {
            Picasso.with(activity).load(event.getImg_url()).resize(650, 280).centerCrop().into(eventBackgroundTarget);
            App.targets.add(eventBackgroundTarget);
        } else {
            if (event.getCategory() == Event.CATEGORY_CONCERTS_GIG)
                banner.setBackgroundResource(R.drawable.gigs);
            else if (event.getCategory() == Event.CATEGORY_EXHIBITIONS)
                banner.setBackgroundResource(R.drawable.exhibition);
            else if (event.getCategory() == Event.CATEGORY_PERFORMING_ARTS)
                banner.setBackgroundResource(R.drawable.perform_arts);
            else if (event.getCategory() == Event.CATEGORY_SPORTS_OUTDOORS)
                banner.setBackgroundResource(R.drawable.sports);
            else if (event.getCategory() == Event.CATEGORY_WORKSHOPS_CLASSES)
                banner.setBackgroundResource(R.drawable.workshop);
            else if (event.getCategory() == Event.CATEGORY_FESTIVALS_LIFESTYLE)
                banner.setBackgroundResource(R.drawable.festivals);
        }
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
