package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.UI.MainActivity;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    /** The 2 fragment are stored as variables, to be referred to later. **/
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

        //initialiseFragments();
    }

    private void initialiseFragments(){
        System.out.println("Testing initialising frags");
        //listFragment = new EventsFragment();
//
//        if(mapsFragment == null) mapsFragment = new MapsFragment();
//        mapsFragment.setRetainInstance(true);
//        mapsFragment.setUpMapIfNeeded();
    }

    @Override
    public Fragment getItem(int position) {
        //initialiseFragments();
        if(position == 0) {
            EventsFragment listFragment = new EventsFragment();
            return listFragment;
        } else {
            MapsFragment mapsFragment = new MapsFragment();
            mapsFragment.setRetainInstance(true);
            mapsFragment.setUpMapIfNeeded();
            return mapsFragment;
        }
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


    public void updateList(){
        ((EventsFragment) registeredFragments.get(0)).updateList();
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
}