package com.example.vincent.whynot.UI.Fragments;

/**
 * Created by George on 8/8/2015.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Adapters.WatchlistAdapter;

/**
 *  Fragment which contains a RecyclerView, listing all the watchlist event items.
 *  The RecyclerView is filled with events by the WatchlistAdapter.
 */
public class WatchlistFragment extends EventsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view =inflater.inflate(R.layout.fragment_watchlist,container,false);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container){
        View view =inflater.inflate(R.layout.fragment_watchlist,container,false);
        return view;
    }


    @Override
    protected void initialisePullToRefresh(View view){
        pullToRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.pullToRefreshLayout);
        pullToRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Send another request for all the events.
                //App.app.startAsyncTaskChain();
            }
        });
        // For now, the app will be refreshing from the start
        //setRefreshing(App.app.refreshing);
    }

    @Override
    public void updateList(){
        adapter.notifyDataSetChanged();
    }

    @Override
    protected RecyclerView.Adapter initialiseAdapter(){
        // Specify the adapter and add animations
        return new WatchlistAdapter(getActivity());
    }


}