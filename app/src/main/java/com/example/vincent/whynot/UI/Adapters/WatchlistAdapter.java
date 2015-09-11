package com.example.vincent.whynot.UI.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.EventClasses.Event;


/**
 * EventsAdapter is responsible for creating and inserting event items into the the
 * RecyclerView in the EventsFragment class.
 */
public class WatchlistAdapter extends RecyclerView.Adapter<EventsAdapter.CardViewHolder> {

    private Activity mainActivity;

    public WatchlistAdapter(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void add(int position, Event item) {
        App.app.watchlistArray.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Event item) {
        int position = App.app.watchlistArray.indexOf(item);
        App.app.watchlistArray.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public EventsAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        EventsAdapter.CardViewHolder vh = new EventsAdapter.CardViewHolder(v, mainActivity, true);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EventsAdapter.CardViewHolder holder, int position) {

        holder.setEvent(App.app.watchlistArray.get(position));
        holder.setWatchlist(true);
        holder.initialiseViews();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return App.app.watchlistArray.size();
    }

}
