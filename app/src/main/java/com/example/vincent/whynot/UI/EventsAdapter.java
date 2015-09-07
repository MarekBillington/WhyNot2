package com.example.vincent.whynot.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.dummy.EventsFragment;


/**
 * EventsAdapter is responsible for creating and inserting event items into the the
 * RecyclerView in the EventsFragment class.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Activity mainActivity;
    private Context context;
    private EventsFragment eventsFragment;

    public EventsAdapter(Activity mainActivity, EventsFragment eventsFragment) {
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        this.eventsFragment = eventsFragment;
    }


    /** Viewholder object, where each event item is initialised. **/
    public class ViewHolder extends RecyclerView.ViewHolder {

        private Event event;

        /** Views and UI objects. **/
        private View v;
        private RelativeLayout banner;
        private TextView nameTextView, categoryTextView, priceTextView,
                distanceTextView, addressTextView, timeTextView;
        private ImageView priceBanner;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
        }

        public void initialiseViews(){
            priceBanner = (ImageView) v.findViewById(R.id.price_banner);
            if(event.getCheapest().equals("Paid")) priceBanner.setVisibility(View.INVISIBLE);
            else priceBanner.setVisibility(View.VISIBLE);


            categoryTextView = (TextView) v.findViewById(R.id.event_category);
            categoryTextView.setText(event.getCategoryString());

            nameTextView = (TextView) v.findViewById(R.id.event_name);
            nameTextView.setText(event.formatName());

            distanceTextView = (TextView) v.findViewById(R.id.event_distance);
            float distance = Float.parseFloat(event.getDistance())/1000;
            String distanceString = String.format("%.01f", distance);;
            distanceTextView.setText(distanceString + "km away");

            addressTextView = (TextView) v.findViewById(R.id.event_location);
            addressTextView.setText("at " + event.getAddressShort());

            /** Clicking on the globe icon, distance or address opens the location. **/
            addressTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventsFragment.openLocation(event);
                }
            });

            distanceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventsFragment.openLocation(event);
                }
            });

            v.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventsFragment.openLocation(event);
                }
            });

            // Only get the time, not the date
            timeTextView = (TextView) v.findViewById(R.id.event_time);
            timeTextView.setText(event.formatTime());

            banner = (RelativeLayout) v.findViewById(R.id.image_container);
            App.setEventImage(mainActivity, banner, event);

            final View compass = v.findViewById(R.id.imageView);

            // Open the event in an event activity if the banner is clicked on
            banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEventActivity(event, v, nameTextView, categoryTextView, addressTextView, distanceTextView, timeTextView, compass);
                }
            });
        }


        /** Open the event into an event activity, using a transition for all their shared views.**/
        private void openEventActivity(Event event, View container, View name, View category, View location,
                                       View distance, View time, View compass){
            Intent intent = new Intent(context, EventActivity.class);
            intent.putExtra("id", event.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Only animate with transitions if SDK 21 or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mainActivity, Pair.create(container, "container"), Pair.create(name, "name"),
                                Pair.create(category, "category"), Pair.create(location, "location"), Pair.create(distance, "distance"),
                                Pair.create(time, "time"),  Pair.create(compass, "compass"));

                mainActivity.startActivity(intent, options.toBundle());
            } else { // Open activity without animation
                context.startActivity(intent);
                System.out.println("Testing:  SDK too low for transitions");
            }
        }

        public void setEvent(Event event){
            this.event = event;
        }
    }

    public void add(int position, Event item) {
        App.eventsArray.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Event item) {
        int position = App.eventsArray.indexOf(item);
        App.eventsArray.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.setEvent(App.eventsArray.get(position));
        holder.initialiseViews();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return App.eventsArray.size();
    }

}
