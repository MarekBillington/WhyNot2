package com.example.vincent.whynot;


import android.app.Activity;
import android.app.Application;
import android.content.Context;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.example.vincent.whynot.Backend.AsyncTasks.ConnectToRESTAsyncTask;
import com.example.vincent.whynot.Backend.AsyncTasks.LocationManagerAsyncTask;
import com.example.vincent.whynot.Backend.PreferencesManager;
import com.example.vincent.whynot.Backend.AsyncTasks.XMLParserAsyncTask;
import com.example.vincent.whynot.UI.EventClasses.Event;
import com.example.vincent.whynot.UI.EventClasses.EventBackgroundTarget;
import com.example.vincent.whynot.UI.Activities.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class App extends Application {

    public static final int ORDER_CLOSEST = 0, ORDER_CHEAPEST = 1, ORDER_EARLIEST = 2;

    public static App app;
    public MainActivity myActivity;
    private Context myContext;
    public static Location userLocation;
    public PreferencesManager preferencesManager;

    /**
     * Refreshing is set to true whenever events are being requested.
     * Waiting request is set to true whenever another request is made before the first
     * request finishes.
     **/
    public boolean refreshing = false;
    public boolean waitingRequest = false;

    /**
     * Buffer array holds all events until all async requests have finished.
     * This means the app can still function while making requests.
     **/
    public ArrayList<Event> eventsArray = new ArrayList<>();
    public ArrayList<Event> watchlistArray = new ArrayList<>();
    private CopyOnWriteArrayList<Event> bufferArray = new CopyOnWriteArrayList<>();

    /**
     * To hold strong references to the targets so that they don't get garbage collected.
     **/
    public static ArrayList<Target> targets = new ArrayList<>();

    /**
     * Fields for EventFinda requests.
     **/
    private int offset = 0;
    private int eventsCount = 0;
    public static double radiusLength = 5;
    public static boolean gigs = true, festivals = true, workshopsClasses = true, exhibitions = true, performingArts = true,
            sports = true;
    public static int order = 0;

    /**
     * Login details for EventFinda API.
     **/
    private static final String eventFindaAPIUsername = "whynot";
    private static final String eventFindaAPIPassword = "kd87ymx3txqv";

    public App(Context context, MainActivity mainActivity) {
        super.onCreate();
        // Holds reference to front end Main Activity to allow callback functions
        myActivity = mainActivity;
        // Holds reference to Applications overall context
        myContext = context;
        app = this;
        preferencesManager = new PreferencesManager(myContext);
        preferencesManager.loadPreferences();
        preferencesManager.loadWatchlist();

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
        System.out.println("Testing: Sending new eventfinda request");
        if (!refreshing) { // New request
            bufferArray.clear();
            refreshing = true;
            waitingRequest = false;
            getUserLocationFromGPS();
        } else { // Multiple simultaneous requests
            waitingRequest = true;
        }

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

    /**
     * Transfer the events from the buffer array and then sort them.
     **/
    public void transferEventsFromBuffer() {
        eventsArray = new ArrayList<>(bufferArray);
        if (App.order == App.ORDER_CLOSEST)
            Collections.sort(eventsArray, Event.ProximityComparator);
        else if (App.order == App.ORDER_CHEAPEST)
            Collections.sort(eventsArray, Event.PriceComparator);
        else if (App.order == App.ORDER_EARLIEST)
            System.out.println("Order by earliest not supported yet.");//Collections.sort(eventsArray, Event.TimeComparator);
    }

    public void setOffset(int newOffset) {
        System.out.println("Testing: Setting Offset (should happen until more than (" + eventsCount + ")) = " + newOffset);
        offset = newOffset;
    }

    public void setEventsCount(int newEventsCount) {
        System.out.println("Testing: Setting Events count (should only happen once) = " + newEventsCount);
        eventsCount = newEventsCount;
    }

    public Event getEventByID(int id) {
        for (Event event : eventsArray) {
            if (Integer.parseInt(event.getId()) == id) return event;
        }
        return null;
    }

    /**
     * Sets an image to the background using Picasso, if event doesn't have an image.
     * it will assign a generic image instead.
     **/
    public static void setEventImage(Activity activity, View banner, Event event) {
        EventBackgroundTarget eventBackgroundTarget = new EventBackgroundTarget(activity, banner);
        if (!event.getImageUrl().isEmpty()) {
            Picasso.with(activity).load(event.getImageUrl()).placeholder(R.drawable.placeholder).into(eventBackgroundTarget);
        } else if (event.getCategory() == Event.CATEGORY_CONCERTS_GIG) {
            Picasso.with(activity).load(R.drawable.gigs).placeholder(R.drawable.placeholder).into(eventBackgroundTarget);
        } else if (event.getCategory() == Event.CATEGORY_EXHIBITIONS) {
            Picasso.with(activity).load(R.drawable.exhibition).placeholder(R.drawable.placeholder).into(eventBackgroundTarget);
        } else if (event.getCategory() == Event.CATEGORY_PERFORMING_ARTS) {
            Picasso.with(activity).load(R.drawable.perform_arts).placeholder(R.drawable.placeholder).into(eventBackgroundTarget);
        } else if (event.getCategory() == Event.CATEGORY_SPORTS_OUTDOORS) {
            Picasso.with(activity).load(R.drawable.sports).placeholder(R.drawable.placeholder).into(eventBackgroundTarget);
        } else if (event.getCategory() == Event.CATEGORY_WORKSHOPS_CLASSES) {
            Picasso.with(activity).load(R.drawable.workshop).placeholder(R.drawable.placeholder).into(eventBackgroundTarget);
        } else if (event.getCategory() == Event.CATEGORY_FESTIVALS_LIFESTYLE) {
            Picasso.with(activity).load(R.drawable.festivals).placeholder(R.drawable.placeholder).into(eventBackgroundTarget);
        }
        App.targets.add(eventBackgroundTarget);
    }

    /**
     * Check to see if the user has changed any preferences
     **/
    public static boolean checkPreferencesHaveChanged(int radius, int order, boolean gigs, boolean festivals, boolean workshopsClasses, boolean exhibitions,
                                                      boolean performingArts, boolean sports) {
        if (App.radiusLength != radius) return true;
        else if (App.order != order) return true;
        else if (App.gigs != gigs) return true;
        else if (App.festivals != festivals) return true;
        else if (App.workshopsClasses != workshopsClasses) return true;
        else if (App.exhibitions != exhibitions) return true;
        else if (App.performingArts != performingArts) return true;
        else if (App.sports != sports) return true;

        return false;
    }

    public PreferencesManager getPreferencesManager() {
        return preferencesManager;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public ArrayList<Event> getEventsArray() {
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
