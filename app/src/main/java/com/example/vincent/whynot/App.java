package com.example.vincent.whynot;

import android.app.Application;
import android.content.res.Configuration;

import com.example.vincent.whynot.UI.Event;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

/**
 * Created by Vincent on 8/08/2015.
 */
public class App extends Application{

    public static ArrayList<Event> events = new ArrayList<>();


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

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
