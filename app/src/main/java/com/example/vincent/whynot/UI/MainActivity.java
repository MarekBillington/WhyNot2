package com.example.vincent.whynot.UI;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.dummy.SlidingTabLayout;
import com.example.vincent.whynot.UI.dummy.ViewPagerAdapter;

/**
 * The main activity, containing a view pager that contains 2 fragments, a list fragment
 * displaying a list of all the events and a map fragment displaying all the events as markers
 * on a map.
 */

public class MainActivity extends FragmentActivity {

    public static FragmentManager fragmentManager;

    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;

    private static final CharSequence TITLES[] = {"Events", "Map"};
    private static final int NUMBOFTABS = 2;

    public App applicationData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        // Creating The ViewPagerAdapter and Passing Fragment Manager, TITLES fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(fragmentManager, TITLES, NUMBOFTABS);

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

        applicationData = new App(getApplicationContext(), this);
    }

    /** After events have been recieved, update both the list and maps fragments. **/
    public void updateFromEvents(App app){
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

   /** Start an intent to send an SMS. **/
    public void startSMS(View view){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", "Let's go to this thing later bro?");
        startActivity(sendIntent);
    }

}
