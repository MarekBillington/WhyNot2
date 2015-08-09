package com.example.vincent.whynot.UI;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;


import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.dummy.SlidingTabLayout;
import com.example.vincent.whynot.UI.dummy.ViewPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {


    // Declaring Your View and Variables

    public static FragmentManager fragmentManager;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Events", "Map"};
    int Numboftabs =2;

    public Application app;
    public App applicationData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        // Creating The Toolbar and setting it as the Toolbar for the activity



        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(fragmentManager,Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
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

        app = getApplication();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout

        applicationData = new App(getApplicationContext(), this);


        //App.events.add(new Event("Christmas parade", "Carols, Santa and all that jazz", "", "", -36.861764, 174.787134, 10, 18, "https://www.facebook.com", null));
        //App.events.add(new Event("Mexican fiesta", "Para todos que quieren festejar toda la noche", "", "", -36.862162, 174.727437, 10, -1, "https://www.google.com",null));
        //App.events.add(new Event("Food parade", "Eat and eat until you feel horrible!", "", "", -36.863668, 174.747231, 10, -1, "https://www.google.com",null));
        //App.events.add(new Event("Lantern Festival", "What a great event, definitely go here", "", "", -36.871569, 174.767090, 10, -1, "https://www.google.com",null));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateFromEvents(App app){
        //applicationData.getEvents();
        System.out.println("empoty");
        for(Event e: app.getEventsArray())
            System.out.println("testing " + e.getName());

        adapter.getMapsFragment().placeMarkers(app);
        adapter.getListFragment().updateList(app);
    }

    public void expandCard(View view){



        View parent = (ViewGroup)view.getParent();
        TextView textView = (TextView) parent.findViewById(R.id.event_description);
        if(textView.isShown()){
            com.example.vincent.whynot.UI.Fx.slide_up(this, textView);
            textView.setVisibility(View.GONE);
        }
        else{
            com.example.vincent.whynot.UI.Fx.slide_down(this, textView);
            textView.setVisibility(View.VISIBLE);
        }

    }

    public void startSMS(View view){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", "Let's go to this thing later bro?");
        startActivity(sendIntent);
    }

    public ArrayList<Event> getEventList(){
        return applicationData.getEventsArray();
    }

//    public void updateEventList(ArrayList<Event> eventArrayList){
//        applicationData.updateEventList(eventArrayList);
//    }


}
