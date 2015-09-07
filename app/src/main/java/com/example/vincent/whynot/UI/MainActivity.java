package com.example.vincent.whynot.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.widget.PullRefreshLayout;
import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.dummy.EventViewPager;
import com.example.vincent.whynot.UI.dummy.EventsFragment;
import com.example.vincent.whynot.UI.dummy.MapsFragment;
import com.example.vincent.whynot.UI.dummy.SlidingTabLayout;
import com.example.vincent.whynot.UI.dummy.ViewPagerAdapter;


/**
 * The main activity, containing a view pager that contains 2 fragments, a list fragment
 * displaying a list of all the events and a map fragment displaying all the events as markers
 * on a map.
 */

public class MainActivity extends AppCompatActivity implements EventsFragment.EventsFragmentListener {

    public static FragmentManager fragmentManager;

    private EventViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private Toolbar toolbar;
    private Menu menu;
    private View dialogView;

    private static final CharSequence TITLES[] = {"Events", "Map"};
    private static final int NUMBOFTABS = 2;
    private static final int ANIMATION_DURATION = 100;

    public static App applicationData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        if(applicationData == null) {
            applicationData = new App(getApplicationContext(), this);
        }

        // Creating The ViewPagerAdapter and Passing Fragment Manager, TITLES fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(fragmentManager, TITLES, NUMBOFTABS);

        // Assigning ViewPager View and setting the adapter
        pager = (EventViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);


        // Assigning the Sliding Tab Layout View
//        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
//
//        // Setting Custom Color for the Scroll bar indicator of the Tab View
//        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
//            @Override
//            public int getIndicatorColor(int position) {
//                return getResources().getColor(R.color.tabsScrollColor);
//            }
//        });
//
//        // Setting the ViewPager For the SlidingTabsLayout
//        tabs.setViewPager(pager);
//        tabs.setVisibility(View.GONE);
        initialiseToolbar();
    }

    /** Initialise the toolbar **/
    private void initialiseToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");

        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(getApplicationContext(), "ddad " + menuItem.getItemId(), Toast.LENGTH_LONG).show();
                switch (menuItem.getItemId()) {
                    case R.id.action_maps:
                        tabs.switchToTab(1);
                        return true;
                }

                return false;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        refreshToolbar();
    }

    /** Ensure toolbar is showing the correct items. **/
    public void refreshToolbar(){
        if(pager.getCurrentItem() == 1) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Near you");
            animateViewOut(toolbar.findViewById(R.id.action_maps));//menu.findItem(R.id.action_maps).setVisible(false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Events");
            animateViewIn(toolbar.findViewById(R.id.action_maps));//menu.findItem(R.id.action_maps).setVisible(true);
        }
    }


    @Override
    public void onResume(){
        super.onResume();

        refreshToolbar();
    }

    /** After events have been recieved, stop refreshing, update both the list and maps fragments. **/
    public void updateFromEvents(){
        adapter.getMapsFragment().placeMarkers();
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
        //tabs.switchToTab(1);
        pager.setCurrentItem(1);
        refreshToolbar();
        MapsFragment.centreMapOnEvent(event);
    }

    /** Only end the activity if you are on the events list tab, if you are on the maps tab, return
     *  to the events list tab. **/
    @Override
    public void onBackPressed(){
        if(pager.getCurrentItem() == 1) {
            pager.setCurrentItem(0);
            refreshToolbar();
        }
        else finish();
    }

    public void setRefreshing(){
        adapter.getListFragment().setRefreshing(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                pager.setCurrentItem(0);
                refreshToolbar();
                return true;

            case R.id.action_maps:
                pager.setCurrentItem(1);
                refreshToolbar();
                return true;

            case R.id.action_settings:
                openEventPreferenceDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Preference Dialog methods
     */

    public void openEventPreferenceDialog(){
        dialogView = null;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Preferences")
                .titleColorRes(R.color.colorAccent)
                .dividerColorRes(R.color.colorAccent)
                .customView(R.layout.dialog_event_preferences, true)
                .positiveText("OK")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        updatePreferences();
                    }
                })
                .show();
        dialogView = dialog.getCustomView();
        initialiseEventPreferenceDialog(dialogView);
    }

    /** Intialises the dialogs widgets. **/
    public void initialiseEventPreferenceDialog(View dialogView){
        ((com.rey.material.widget.Slider) dialogView.findViewById(R.id.radiusSlider)).setValue((float) App.radiusLength, true);
        ((CheckBox) dialogView.findViewById(R.id.gigsCheckBox)).setChecked(App.gigs);
        ((CheckBox) dialogView.findViewById(R.id.festivalsCheckBox)).setChecked(App.festivals);
        ((CheckBox) dialogView.findViewById(R.id.workshopClassesCheckBox)).setChecked(App.workshopsClasses);
        ((CheckBox) dialogView.findViewById(R.id.exhibitionsCheckBox)).setChecked(App.exhibitions);
        ((CheckBox) dialogView.findViewById(R.id.performingArtsCheckBox)).setChecked(App.performingArts);
        ((CheckBox) dialogView.findViewById(R.id.sportsCheckBox)).setChecked(App.sports);

        com.rey.material.widget.Spinner spinner = (com.rey.material.widget.Spinner) dialogView.findViewById(R.id.orderSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(App.order);
    }

    /** Update the preferences by gathering the data from the dialogs widgets. **/
    public void updatePreferences(){
        float radius = ((com.rey.material.widget.Slider) dialogView.findViewById(R.id.radiusSlider)).getExactValue();
        int order = ((com.rey.material.widget.Spinner) dialogView.findViewById(R.id.orderSpinner)).getSelectedItemPosition();
        boolean gigs = ((CheckBox) dialogView.findViewById(R.id.gigsCheckBox)).isChecked();
        boolean festivals = ((CheckBox) dialogView.findViewById(R.id.festivalsCheckBox)).isChecked();
        boolean workshopsClasses = ((CheckBox) dialogView.findViewById(R.id.workshopClassesCheckBox)).isChecked();
        boolean exhibitions = ((CheckBox) dialogView.findViewById(R.id.exhibitionsCheckBox)).isChecked();
        boolean performingArts = ((CheckBox) dialogView.findViewById(R.id.performingArtsCheckBox)).isChecked();
        boolean sports = ((CheckBox) dialogView.findViewById(R.id.sportsCheckBox)).isChecked();

        // Check the user has chosen atleast 1 category
        if(!gigs && !festivals && !workshopsClasses && ! exhibitions && !performingArts && !sports)
            Toast.makeText(this, "You must choose at least 1 category.", Toast.LENGTH_LONG).show();
        // Check the user has changed at least 1 preference
        else if(!App.checkPreferencesHaveChanged((int) radius, order, gigs, festivals, workshopsClasses, exhibitions, performingArts, sports));
        else {
            App.app.getPreferencesManager().updatePreferences((int) radius, order, gigs, festivals, workshopsClasses, exhibitions, performingArts, sports);
            App.app.startAsyncTaskChain();
            adapter.getListFragment().setRefreshing(true);
        }
    }

    /**
     * Animation methods for animation views out and back in.
     */

    public void animateViewOut(final View view){
        view.animate()
                .alpha(0.0f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public void animateViewIn(final View view){
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
    }
}
