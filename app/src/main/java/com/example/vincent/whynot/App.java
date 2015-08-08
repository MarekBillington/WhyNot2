package com.example.vincent.whynot;

import android.app.Application;
import android.content.res.Configuration;

import com.example.vincent.whynot.UI.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class App extends Application{

    private static ArrayList<Event> eventList;

    public ArrayList<Event> getEventList(){
        return eventList;
    }

    public void updateEventList(ArrayList<Event> eventArrayList){
        eventList = eventArrayList;
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
}
