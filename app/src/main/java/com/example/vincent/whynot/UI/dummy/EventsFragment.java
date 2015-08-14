package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Event;
import com.example.vincent.whynot.UI.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    RelativeLayout banner;
    private MainActivity mainActivity = null;

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


        //setUpSlider(view);




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
            Event event = eventArrayList.get(i);
            setUpCard(card, event);
            formatCard(card);
            tabLinearLayout.addView(card);
            final RelativeLayout banner = (RelativeLayout) card.findViewById(R.id.image_container);
            banner.setBackgroundColor(Color.parseColor(colours[i % 6]));

            Picasso.with(getActivity()).load(event.getImg_url()).resize(650, 280).centerCrop().into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    banner.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
                    Log.d("TAG", "FAILED");
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                    Log.d("TAG", "Prepare Load");
                }
            });
        }
    }

    public void formatCard(View card){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0, 8, 0, 8);
        card.setLayoutParams(params);

        TextView textView = (TextView) card.findViewById(R.id.event_description);
        //textView.setVisibility(View.GONE);


    }

    public void setUpCard(View card, Event event){
        TextView event_price = (TextView) card.findViewById(R.id.event_price);
       //event_price.setText(event.getCheapest());

        TextView event_category = (TextView) card.findViewById(R.id.event_category);
        event_category.setText(event.getCategory());//(event.getCheapest());

        TextView event_name = (TextView) card.findViewById(R.id.event_name);
        String eventName = event.getName();
        event_name.setText(formatName(eventName));

        TextView event_distance = (TextView) card.findViewById(R.id.event_distance);
        float distance = Float.parseFloat(event.getDistance())/1000;
        String distanceString = String.format("%.01f", distance);;
        event_distance.setText(distanceString + "km");

        TextView eventAddress = (TextView) card.findViewById(R.id.event_location);
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
        TextView event_time = (TextView) card.findViewById(R.id.event_time);
        String time = event.getDt_start().substring(event.getDt_start().length() - 9, event.getDt_start().length() - 3);
        event_time.setText(formatTime(time));
    }


    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public String formatName(String title){
        if (title.length() > 35){
            return title.substring(0, 35) + "...";
        } else{
            return title;
        }
    }

    public String formatTime(String time){
        if (Integer.parseInt(time.substring(1, 3)) > 12 ){
            return String.valueOf(Integer.parseInt(time.substring(1, 3)) - 12) + time.substring(3, 6) + "pm";
        } else{
            return time + "am";
        }
    }
}