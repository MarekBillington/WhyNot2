package com.example.vincent.whynot.UI;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.dummy.EventsFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


/**
 * Created by Vincent on 20/08/2015.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Activity mainActivity;
    private Context context;
    private EventsFragment eventsFragment;


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View v;
        private RelativeLayout banner;
        private TextView nameTextView, categoryTextView, priceTextView,
                distanceTextView, addressTextView, timeTextView;

        private Event event;

        // To hold strong references to the targets so that they don't get garbage collected.
        private ArrayList<Target> targets = new ArrayList<>();

        public ViewHolder(View v) {
            super(v);
            this.v = v;
        }

        public void initialiseViews(){
            priceTextView = (TextView) v.findViewById(R.id.event_price);
            priceTextView.setText(event.getCheapest());

            categoryTextView = (TextView) v.findViewById(R.id.event_category);
            categoryTextView.setText(event.getCategoryString());

            nameTextView = (TextView) v.findViewById(R.id.event_name);
            nameTextView.setText(event.formatName());

            distanceTextView = (TextView) v.findViewById(R.id.event_distance);
            float distance = Float.parseFloat(event.getDistance())/1000;
            String distanceString = String.format("%.01f", distance);;
            distanceTextView.setText(distanceString + "km away");

            addressTextView = (TextView) v.findViewById(R.id.event_location);
            addressTextView.setText("at " + event.getLocationShort());

            // Change to maps fragment and go to location
            final Location eventLocation = new Location("");

            eventLocation.setLatitude(event.getLatitude());
            eventLocation.setLongitude(event.getLongitude());
            distanceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventsFragment.openLocation(eventLocation);
                }
            });

            v.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventsFragment.openLocation(eventLocation);
                }
            });

            // Only get the time, not the date
            timeTextView = (TextView) v.findViewById(R.id.event_time);
            timeTextView.setText(event.formatTime());

            banner = (RelativeLayout) v.findViewById(R.id.image_container);
            setEventImage(banner, event);

            final View compass = v.findViewById(R.id.imageView);

            // Open the event in an event activity if the banner is clicked on
            banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEventActivity(event, v, nameTextView, categoryTextView, addressTextView, distanceTextView, timeTextView, compass);
                }
            });
        }

        /** Sets an image to the background using Picasso, if event doesn't have an image.
         *  it will assign a generic image instead. **/
        public void setEventImage(View banner, Event event){
            EventBackgroundTarget eventBackgroundTarget = new EventBackgroundTarget(context, banner);
            if(!event.getImg_url().isEmpty()) {
                Picasso.with(mainActivity).load(event.getImg_url()).resize(650, 280).centerCrop().into(eventBackgroundTarget);
                targets.add(eventBackgroundTarget);
            }else{
                if(event.getCategory() == Event.CATEGORY_CONCERTS_GIG) banner.setBackgroundResource(R.drawable.gigs);
                else if(event.getCategory() == Event.CATEGORY_EXHIBITIONS) banner.setBackgroundResource(R.drawable.exhibition);
                else if(event.getCategory() == Event.CATEGORY_PERFORMING_ARTS) banner.setBackgroundResource(R.drawable.perform_arts);
                else if(event.getCategory() == Event.CATEGORY_SPORTS_OUTDOORS) banner.setBackgroundResource(R.drawable.sports);
                else if(event.getCategory() == Event.CATEGORY_WORKSHOPS_CLASSES) banner.setBackgroundResource(R.drawable.workshop);
                else if(event.getCategory() == Event.CATEGORY_FESTIVALS_LIFESTYLE) banner.setBackgroundResource(R.drawable.festivals);
            }
        }

        /** Open the event into an event activity, using a transition for all their shared views.**/
        private void openEventActivity(Event event, View container, View name, View category, View location,
                                       View distance, View time, View compass){
            Intent intent = new Intent(context, EventActivity.class);
            intent.putExtra("id", event.getId());

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

    public EventsAdapter(Activity mainActivity, EventsFragment eventsFragment) {
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        this.eventsFragment = eventsFragment;
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
