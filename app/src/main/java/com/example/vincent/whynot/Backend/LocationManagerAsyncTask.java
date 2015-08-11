package com.example.vincent.whynot.Backend;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import com.example.vincent.whynot.App;


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
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        myApp.setUserLocation(location);
        return "Testing: User's current location = " + location;
    }

    // Begins a new async task to connect to the eventfinda REST API
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Start the ConnectToRESTAsyncTask async task
        myApp.getEventsStringHTTPRequest();
        System.out.println(result);
    }

}
