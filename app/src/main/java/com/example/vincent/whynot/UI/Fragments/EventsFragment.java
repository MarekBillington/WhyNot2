package com.example.vincent.whynot.UI.Fragments;

/**
 * Created by George on 8/8/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.EventClasses.Event;
import com.example.vincent.whynot.UI.Adapters.EventsAdapter;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Fragment which contains a RecyclerView, listing all the individual event items.
 * The RecyclerView is filled with events by the EventsAdapter.
 */
public class EventsFragment extends Fragment {

    public static final int ANIMATION_DURATION = 250;// Time in ms of event item transitions

    private Context context;
    protected PullRefreshLayout pullToRefreshLayout;

    /**
     * Recycler view variables.
     **/
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    protected RecyclerView.Adapter adapter;

    private EventsFragmentListener mapsCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflateView(inflater, container);

        this.context = view.getContext();
        setRetainInstance(true);
        initialiseRecycleView(view);
        initialisePullToRefresh(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //adapter.notifyDataSetChanged();
        //initialisePullToRefresh();
    }

    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        return view;
    }

    /**
     * Make sure the Activity implements EventsFragmentListener, otherwise throw an exception.
     **/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mapsCallback = (EventsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement EventsFragmentListener");
        }
    }

    private void initialiseRecycleView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        // If screen is in portrait, use a linear layout, in landscape use a  gridlayout
        // with 2 columns
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            layoutManager = new LinearLayoutManager(context);
        else
            layoutManager = new GridLayoutManager(context, 2);

        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter eventsAdapter = initialiseAdapter();

        //AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(eventsAdapter);
        //alphaAdapter.setDuration(ANIMATION_DURATION);
        //alphaAdapter.setInterpolator(new OvershootInterpolator());
        //ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(eventsAdapter);

        //scaleAdapter.setDuration(ANIMATION_DURATION);
        //scaleAdapter.setInterpolator(new OvershootInterpolator());
        adapter = eventsAdapter;
        recyclerView.setAdapter(adapter);
        //recyclerView.setItemAnimator(new );
    }

    protected RecyclerView.Adapter initialiseAdapter() {
        // Specify the adapter and add animations
        return new EventsAdapter(getActivity());
    }

    /**
     * Open the loction of the event on the map and show it's info window.
     **/
    public void openLocation(Event event) {
        mapsCallback.switchToMaps(event);
    }


    /**
     * Initialises the pull to refresh layout.
     **/
    protected void initialisePullToRefresh(View view) {
        pullToRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.pullToRefreshLayout);
        pullToRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Send another request for all the events.
                App.app.startAsyncTaskChain();
            }
        });
        // For now, the app will be refreshing from the start
        setRefreshing(App.app.refreshing);
        System.out.println("Testing: refreshing = " + App.app.refreshing);
    }

    public void setRefreshing(boolean refreshing) {
        pullToRefreshLayout.setRefreshing(refreshing);
    }

    public void updateList() {
        pullToRefreshLayout.setRefreshing(App.app.waitingRequest); // If there is a waiting request, keep refreshing, otherwise stop refreshing
        adapter.notifyDataSetChanged();
    }


    /**
     * Listener for interacting with the MainActivity.
     **/
    public interface EventsFragmentListener {
        public void switchToMaps(Event event);
    }

    /**
     * Getters and setters
     **/

    public RecyclerView.Adapter getEventsAdapter() {
        return adapter;
    }

    public void setEventsAdapter(RecyclerView.Adapter eventsAdapter) {
        this.adapter = eventsAdapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}