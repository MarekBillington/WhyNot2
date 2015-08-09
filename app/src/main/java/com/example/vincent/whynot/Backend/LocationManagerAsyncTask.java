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

    public LocationManagerAsyncTask(App app, LocationManager locationManager) {
        myApp = app;
        this.locationManager = locationManager;
    }


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        return "Location = " + location;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println(result);
        myApp.setUserLocation(location);
    }

}
