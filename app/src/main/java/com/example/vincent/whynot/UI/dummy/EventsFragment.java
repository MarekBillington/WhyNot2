package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Event;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by hp1 on 21-01-2015.
 */
public class EventsFragment extends Fragment {

    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}
    LayoutInflater inflater;
    LinearLayout layout;
    View view;
    ViewGroup container;

    RelativeLayout banner;

    public static String colours[] = {"#FF3A3A", "#9737CC", "#f3E6BC","#59E734","#FFE23A","#2CC1C1"};
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.tab_1,container,false);
        this.container = container;
        this.inflater = inflater;
        this.layout = (LinearLayout) view.findViewById(R.id.tabLinearLayout);
        this.context = getActivity().getApplicationContext();

        return view;
    }

    public void updateList(App app){

        displaySurface(app.getEventsArray(), layout, inflater);

    }


    public void displaySurface(ArrayList<Event> eventArrayList, LinearLayout tabLinearLayout, LayoutInflater inflater){

        for (int i = 0; i < eventArrayList.size(); i++){
            View card = inflater.inflate(R.layout.card_2, null);
            setUpCard(card, eventArrayList.get(i));
            formatCard(card);
            tabLinearLayout.addView(card);
            ((RelativeLayout) card.findViewById(R.id.banner)).setBackgroundColor(Color.parseColor(colours[i%6]));
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
        textView.setVisibility(View.GONE);


    }

    public void setUpCard(View card, Event event){
        TextView event_price = (TextView) card.findViewById(R.id.event_price);
        event_price.setText(event.getCheapest());
        TextView event_name = (TextView) card.findViewById(R.id.event_name);
        event_name.setText(event.getName());
        TextView event_distance = (TextView) card.findViewById(R.id.event_distance);
        //float distance = Float.parseFloat(event.getDistance())/1000;
        float distance = 4 /*Math.round(distance)*/;
        String distanceString = distance + "";
        event_distance.setText(distanceString + "km");
        final TextView event_description = (TextView) card.findViewById(R.id.event_description);
        event_description.setText(event.getDescription());
        TextView event_time = (TextView) card.findViewById(R.id.event_time);
        event_time.setText(event.getDt_start());



    }



}