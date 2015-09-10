package com.example.vincent.whynot.UI.Adapters;

/**
 * Created by George on 8/8/2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.vincent.whynot.UI.Fragments.EventsFragment;
import com.example.vincent.whynot.UI.Fragments.MapsFragment;
import com.example.vincent.whynot.UI.Fragments.WatchlistFragment;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    /**
     * The 3 fragment are stored as variables, to be referred to later.
     **/
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

        //initialiseFragments();
    }


    @Override
    public Fragment getItem(int position) {
        //initialiseFragments();
        if (position == 0) {
            EventsFragment listFragment = new EventsFragment();
            return listFragment;
        } else if (position == 1) {
            MapsFragment mapsFragment = new MapsFragment();
            mapsFragment.setRetainInstance(true);
            mapsFragment.setUpMapIfNeeded();
            return mapsFragment;
        } else if (position == 2) {
            WatchlistFragment watchlistFragment = new WatchlistFragment();
            return watchlistFragment;
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


    public void updateList() {
        System.out.println("Testing: listlength " + registeredFragments.size());
        ((EventsFragment) registeredFragments.get(0)).updateList();
    }

    public void updateWatchlist() {
        ((WatchlistFragment) registeredFragments.get(2)).updateList();
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
        return (MapsFragment) getRegisteredFragment(1);
    }

    public EventsFragment getListFragment() {
        return (EventsFragment) getRegisteredFragment(0);
    }

    public WatchlistFragment getWatchlistFragment() {
        return (WatchlistFragment) getRegisteredFragment(2);
    }
}