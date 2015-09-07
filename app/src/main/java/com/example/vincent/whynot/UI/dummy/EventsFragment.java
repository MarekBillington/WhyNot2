package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;

import com.baoyz.widget.PullRefreshLayout;
import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Event;
import com.example.vincent.whynot.UI.EventsAdapter;
import com.example.vincent.whynot.UI.MainActivity;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 *  Fragment which contains a RecyclerView, listing all the individual event items.
 *  The RecyclerView is filled with events by the EventsAdapter.
 */
public class EventsFragment extends Fragment {

    public static final int ANIMATION_DURATION = 150;// Time in ms of event item transitions

    private Context context;
    private PullRefreshLayout pullToRefreshLayout;

    /** Recycler view variables. **/
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private EventsFragmentListener mapsCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_events,container,false);

        this.context = view.getContext();
        setRetainInstance(true);
        initialiseRecycleView(view);
        initialisePullToRefresh(view);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        //initialisePullToRefresh();
    }

    /** Make sure the Activity implements EventsFragmentListener, otherwise throw an exception. **/
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

    private void initialiseRecycleView(View view){
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

        // Specify the adapter and add animations
        EventsAdapter eventsAdapter = new EventsAdapter(getActivity(), this);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(eventsAdapter);
        alphaAdapter.setDuration(ANIMATION_DURATION);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);

        scaleAdapter.setDuration(ANIMATION_DURATION);
        adapter = scaleAdapter;
        recyclerView.setAdapter(adapter);

        //recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(0));
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
    }

    /** Open the loction of the event on the map and show it's info window. **/
    public void openLocation(Event event){
        mapsCallback.switchToMaps(event);
    }


    /** Initialises the pull to refresh layout. **/
    private void initialisePullToRefresh(View view){
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

    public void setRefreshing(boolean refreshing){
        pullToRefreshLayout.setRefreshing(refreshing);
    }

    public void updateList(){
        pullToRefreshLayout.setRefreshing(false);
        System.out.println("Testing: refreshing finished");
        adapter.notifyDataSetChanged();
    }


    /**
     * Spacing for the RecyclerView
     */
    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
            outRect.left = mVerticalSpaceHeight;
            outRect.right = mVerticalSpaceHeight;
        }
    }

    /** Listener for interacting with the MainActivity. **/
    public interface EventsFragmentListener{
        public void switchToMaps(Event event);
    }

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