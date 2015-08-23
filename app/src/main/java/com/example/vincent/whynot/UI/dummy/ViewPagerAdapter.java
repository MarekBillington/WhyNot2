package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.UI.MainActivity;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    /** The 2 fragment are stored as variables, to be referred to later. **/
    private App app;
    private MapsFragment mapsFragment;
    private MainActivity mainActivity;
    private EventsFragment listFragment;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(App app, MainActivity mainActivity,FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.app = app;
        this.mainActivity = mainActivity;
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

        initialiseFragments();
    }

    private void initialiseFragments(){
        System.out.println("Testing initialising frags");
        listFragment = new EventsFragment();
        listFragment.setMainActivity(mainActivity);

        if(mapsFragment == null) mapsFragment = new MapsFragment();
        mapsFragment.setApp(app);
        mapsFragment.setRetainInstance(true);
        mapsFragment.setUpMapIfNeeded();
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) return listFragment;
        else return mapsFragment;
    }


    public void updateList(){
        listFragment.updateList(this.app);
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    public MapsFragment getMapsFragment() {
        return mapsFragment;
    }
    public EventsFragment getListFragment() {
        return listFragment;
    }
}