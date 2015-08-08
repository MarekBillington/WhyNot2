package com.example.vincent.whynot;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by Vincent on 8/08/2015.
 */
public class App extends Application{

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
