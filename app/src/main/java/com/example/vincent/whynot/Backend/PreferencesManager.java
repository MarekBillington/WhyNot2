package com.example.vincent.whynot.Backend;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.UI.EventClasses.Event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Manager class to deal with saving and loading user's preferences from device.
 */
public class PreferencesManager {

    public static final String PREFS_NAME = "WHYNOT_PREFS";
    private static final String RADIUS_KEY = "RADIUS", GIGS_KEY = "GIGS", FESTIVALS_KEY = "FESTIVALS", WORKSHOPS_CLASSES_KEY = "WORKSHOPSCLASSES",
            EXHIBITIONS_KEY = "EXHIBITIONS", PERFORMINGARTS_KEY = "PERFORMINGARTS", SPORTS_KEY = "SPORTS", ORDER_KEY = "ORDER";
    private static final String WATCHLIST = "watchlist";

    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** SharedPreference methods. **/

    /**
     * Saves the preferences passed to the SharedPreferences, then re-loads the preferences.
     **/
    public void updatePreferences(int radius, int order, boolean gigs, boolean festivals, boolean workshopsClasses, boolean exhibitions,
                                  boolean performingArts, boolean sports) {
        saveToPreferences(radius, order, gigs, festivals, workshopsClasses, exhibitions, performingArts, sports);
        loadPreferences();
    }

    /**
     * Saves the preferences passed to the SharedPreferences
     **/
    public void saveToPreferences(int radius, int order, boolean gigs, boolean festivals, boolean workshopsClasses, boolean exhibitions,
                                  boolean performingArts, boolean sports) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(RADIUS_KEY, radius);
        editor.putInt(ORDER_KEY, order);
        editor.putBoolean(GIGS_KEY, gigs);
        editor.putBoolean(FESTIVALS_KEY, festivals);
        editor.putBoolean(WORKSHOPS_CLASSES_KEY, workshopsClasses);
        editor.putBoolean(EXHIBITIONS_KEY, exhibitions);
        editor.putBoolean(PERFORMINGARTS_KEY, performingArts);
        editor.putBoolean(SPORTS_KEY, sports);
        editor.commit();
        System.out.println("Testing: order " + order);
    }

    /**
     * Load preferences from the SharedPreferences into the App class
     */
    public void loadPreferences() {
        App.radiusLength = sharedPreferences.getInt(RADIUS_KEY, 5);
        App.order = sharedPreferences.getInt(ORDER_KEY, 0);
        App.gigs = sharedPreferences.getBoolean(GIGS_KEY, true);
        App.festivals = sharedPreferences.getBoolean(FESTIVALS_KEY, true);
        App.workshopsClasses = sharedPreferences.getBoolean(WORKSHOPS_CLASSES_KEY, true);
        App.exhibitions = sharedPreferences.getBoolean(EXHIBITIONS_KEY, true);
        App.performingArts = sharedPreferences.getBoolean(PERFORMINGARTS_KEY, true);
        App.sports = sharedPreferences.getBoolean(SPORTS_KEY, true);
    }

    /**
     * Convert the current watchlist array to a set of strings containing only the event IDs,
     * then store them in the SharedPreferences.
     */
    public void saveWatchlist() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<String> eventIds = new ArrayList<>();
        for (Event event : App.app.watchlistArray) eventIds.add(event.getId());

        Set<String> watchlistSet = new HashSet<>(eventIds);
        editor.putStringSet(WATCHLIST, watchlistSet);
        editor.commit();
    }

    /**
     * Load the watchlist event ids from the SharedPreferences, then send out requests for each
     * id and store them in the watchlist.
     */
    public void loadWatchlist() {
        Set<String> watchlistSet = sharedPreferences.getStringSet(WATCHLIST, new HashSet<String>());
        ArrayList<String> eventIds = new ArrayList<>(watchlistSet);

        for (String id : eventIds) System.out.println("Testing: watchlist activity id = " + id);
    }

}
