package com.example.vincent.whynot.UI;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.dummy.EventsFragment;
import com.example.vincent.whynot.UI.dummy.MapsFragment;
import com.example.vincent.whynot.UI.dummy.SlidingTabLayout;
import com.example.vincent.whynot.UI.dummy.ViewPagerAdapter;
import com.squareup.picasso.Picasso;

/**
 * The main activity, containing a view pager that contains 2 fragments, a list fragment
 * displaying a list of all the events and a map fragment displaying all the events as markers
 * on a map.
 */

public class MainActivity extends FragmentActivity implements EventsFragment.EventsFragmentListener {

    public static FragmentManager fragmentManager;

    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;

    private static final CharSequence TITLES[] = {"Events", "Map"};
    private static final int NUMBOFTABS = 2;

    public static App applicationData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        if(applicationData == null) {
            applicationData = new App(getApplicationContext(), this);
        }

        // Creating The ViewPagerAdapter and Passing Fragment Manager, TITLES fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(applicationData, this,fragmentManager, TITLES, NUMBOFTABS);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }

    /** After events have been recieved, stop refreshing, update both the list and maps fragments. **/
    public void updateFromEvents(App app){
        adapter.getMapsFragment().placeMarkers(app);
        adapter.updateList();
    }

   /** Start an intent to send an SMS. **/
    public void startSMS(View view){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", "Let's go to this thing later bro?");
        startActivity(sendIntent);
    }

    /** Switches to maps fragment and zooms on a particular location   **/
    public void switchToMaps(Event event){
        tabs.switchToTab(1);
        MapsFragment.centreMapOnEvent(event);
    }

    /** Only end the activity if you are on the events list tab, if you are on the maps tab, return
     *  to the events list tab. **/
    @Override
    public void onBackPressed(){
        if(tabs.getCurrentTabIndex() == 1) tabs.switchToTab(0);
        else finish();
    }

    public void setRefreshing(){
        adapter.getListFragment().setRefreshing(true);
    }
}
