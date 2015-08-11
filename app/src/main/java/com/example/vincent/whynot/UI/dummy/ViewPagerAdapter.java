package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    /** The 2 fragment are stored as variables, to be referred to later. **/
    private MapsFragment mapsFragment;
    private EventsFragment listFragment;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            EventsFragment tab1;
            // The splash screen will be implemented later
            //if(App.events.isEmpty()) tab1 = new SplashFragment();
            //else
            tab1 = new EventsFragment();
            listFragment = tab1;
            return tab1;
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            MapsFragment mapsFragment = new MapsFragment();
            mapsFragment.setUpMapIfNeeded();
            this.mapsFragment = mapsFragment;
            return mapsFragment;
        }


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


    public void setMapsFragment(MapsFragment mapsFragment) {
        this.mapsFragment = mapsFragment;
    }
}