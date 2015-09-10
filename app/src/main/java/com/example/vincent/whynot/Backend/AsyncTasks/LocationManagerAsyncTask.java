package com.example.vincent.whynot.Backend.AsyncTasks;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.UI.Fragments.MapsFragment;


public class LocationManagerAsyncTask extends AsyncTask<Void, Void, String> {

    App myApp;
    LocationManager locationManager;
    Location location;

    // Constructor that passes a reference to the App class to this object
    public LocationManagerAsyncTask(App app, LocationManager locationManager) {
        myApp = app;
        this.locationManager = locationManager;
    }

    // Overridden class methods

    // Gets the users current location and sets it to the App.userLocation property
    @Override
    protected String doInBackground(Void... params) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            App.setUserLocation(location);
            System.out.println("Testing: User location found using GPS");
        } else if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            App.setUserLocation(location);
            System.out.println("Testing: User location found using Network Provider");
        } else {
            System.out.println("Testing: User location unable to be found");
        }
        System.out.println("Testing: Users current location = " + location);
        return "Testing: Users current location = " + location;
    }

    // Begins a new async task to connect to the eventfinda REST API
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println("Testing location onpostexecute");
        myApp.getEventsStringHTTPRequest(myApp.getOffset());
        // Centre the map on the user
        MapsFragment.centreMapOnLocation(App.userLocation, MapsFragment.INITIAL_HEIGHT);
    }

}
