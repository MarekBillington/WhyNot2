package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Event;
import com.example.vincent.whynot.UI.EventActivity;
import com.example.vincent.whynot.UI.EventBackgroundTarget;
import com.example.vincent.whynot.UI.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by hp1 on 21-01-2015.
 */
public class EventsFragment extends Fragment {

    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}
    LayoutInflater inflater;
    LinearLayout layout;
    private MainActivity mainActivity = null;

    // To hold strong references to the targets so that they don't get garbage collected.
    private ArrayList<Target> targets = new ArrayList<>();

    public static String colours[] = {"#FF3A3A", "#9737CC", "#f3E6BC","#59E734","#FFE23A","#2CC1C1"};
    private Context context;
    private ViewGroup container;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.tab_1,container,false);

        this.inflater = inflater;
        this.layout = (LinearLayout) view.findViewById(R.id.tabLinearLayout);
        this.context = view.getContext();
        this.container = container;

        return view;
    }

    /** Remove all event item in the list and re add them. **/
    public void updateList(App app){
        layout.removeViewsInLayout(0, layout.getChildCount());
        displaySurface(app.getEventsArray(), layout, inflater);
    }

    public void addLoadingItem(){

        View card = this.inflater.inflate(R.layout.fragment_splash, container);
        this.layout.addView(card);
    }

    public void displaySurface(CopyOnWriteArrayList<Event> eventArrayList, LinearLayout tabLinearLayout, LayoutInflater inflater){

        for (int i = 0; i < eventArrayList.size(); i++){
            View card = inflater.inflate(R.layout.card_concept4, null);
            final Event event = eventArrayList.get(i);
            setUpCard(card, event);
            formatCard(card);
            tabLinearLayout.addView(card);
        }
    }

    public void formatCard(View card){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0 ,16, 0, 16);
        card.setLayoutParams(params);

        TextView textView = (TextView) card.findViewById(R.id.event_description);
        //textView.setVisibility(View.GONE);


    }

    public void setUpCard(View card, final Event event){
        final TextView event_price = (TextView) card.findViewById(R.id.event_price);
        event_price.setText(event.getCheapest());

        final TextView event_category = (TextView) card.findViewById(R.id.event_category);
        event_category.setText(event.getCategoryString());//(event.getCheapest());

        final View cont = card.findViewById(R.id.image_container);

        final TextView event_name = (TextView) card.findViewById(R.id.event_name);
        String eventName = event.getName();
        event_name.setText(event.formatName());

        final TextView event_distance = (TextView) card.findViewById(R.id.event_distance);
        float distance = Float.parseFloat(event.getDistance())/1000;
        String distanceString = String.format("%.01f", distance);;
        event_distance.setText(distanceString + "km away");

        final TextView eventAddress = (TextView) card.findViewById(R.id.event_location);
        String address = event.getLocation();
        eventAddress.setText(address);

        // Change to maps fragment and go to location
        final Location eventLocation = new Location("");

        eventLocation.setLatitude(event.getLatitude());
        eventLocation.setLongitude(event.getLongitude());
        event_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.switchToMaps(eventLocation);
            }
        });

        card.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.switchToMaps(eventLocation);
            }
        });

        final TextView event_description = (TextView) card.findViewById(R.id.event_description);
        event_description.setText(event.getDescription());

        // Only get the time, not the date
        final TextView event_time = (TextView) card.findViewById(R.id.event_time);
        String time = event.getDt_start().substring(event.getDt_start().length() - 9, event.getDt_start().length() - 3);
        event_time.setText(event.formatTime());

        final RelativeLayout banner = (RelativeLayout) card.findViewById(R.id.image_container);
        setEventImage(banner, event);

        final View compass = card.findViewById(R.id.imageView);

        // Open the event in an event activity if the banner is clicked on
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEventActivity(event, cont, event_name, event_price, event_category, eventAddress, event_distance, event_time, compass);
            }
        });

    }

    /** Sets an image to the background using Picasso, if event doesn't have an image.
     *  it will assign a generic image instead. **/
    public void setEventImage(View banner, Event event){
        EventBackgroundTarget eventBackgroundTarget = new EventBackgroundTarget(context, banner);
        if(!event.getImg_url().isEmpty()) {
            Picasso.with(getActivity()).load(event.getImg_url()).resize(650, 280).centerCrop().into(eventBackgroundTarget);
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


    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }



    private void openEventActivity(Event event, View container, View name, View price, View category, View location,
                                   View distance, View time, View compass){
        Intent intent = new Intent(mainActivity, EventActivity.class);
        // Pass data object in the bundle and populate details activity.

        // Only animate with transitions if SDK 21 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(mainActivity, Pair.create(container, "container"), Pair.create(name, "name"),
                            Pair.create(price, "price"),Pair.create(category, "category"), Pair.create(location, "location"),
                            Pair.create(distance, "distance"),  Pair.create(time, "time"),  Pair.create(compass, "compass"));
            intent.putExtra("id", event.getId());
            mainActivity.startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
            System.out.println("Testing:  SDK too low for transitions");
        }

    }

}