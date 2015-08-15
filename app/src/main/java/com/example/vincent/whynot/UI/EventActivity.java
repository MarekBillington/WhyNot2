package com.example.vincent.whynot.UI;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vincent.whynot.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class EventActivity extends AppCompatActivity {

    /** A large more descriptive view of each activity. **/

    private TextView nameTextView, categoryTextView, priceTextView, descriptionTextView,
                     distanceTextView, addressTextView, timeTextView;
    private RelativeLayout banner;

    private Event event;

    private Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Bundle bundle = getIntent().getExtras();
        int eventId = Integer.parseInt(bundle.getString("id"));
        event = MainActivity.applicationData.getEventByID(eventId);

        initialiseViews();
    }

    private void initialiseViews(){
        priceTextView = (TextView) findViewById(R.id.event_price);
        priceTextView.setText(event.getCheapest());

        categoryTextView = (TextView) findViewById(R.id.event_category);
        categoryTextView.setText(event.getCategoryString());

        nameTextView = (TextView) findViewById(R.id.event_name);
        nameTextView.setText(event.formatName());

        distanceTextView = (TextView) findViewById(R.id.event_distance);
        float distance = Float.parseFloat(event.getDistance())/1000;
        String distanceString = String.format("%.01f", distance);;
        distanceTextView.setText(distanceString + "km away");

        addressTextView = (TextView) findViewById(R.id.event_location);
        String address = event.getLocation();
        addressTextView.setText(address);
//
//        // Change to maps fragment and go to location
//        final Location eventLocation = new Location("");
//
//        eventLocation.setLatitude(event.getLatitude());
//        eventLocation.setLongitude(event.getLongitude());
//        event_distance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainActivity.switchToMaps(eventLocation);
//            }
//        });
//
//        card.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainActivity.switchToMaps(eventLocation);
//            }
//        });
//
        descriptionTextView = (TextView) findViewById(R.id.event_description);
        descriptionTextView.setText(event.getDescription());
//
        // Only get the time, not the date
        timeTextView = (TextView) findViewById(R.id.event_time);
        timeTextView.setText(event.formatTime());

        banner = (RelativeLayout) findViewById(R.id.image_container);
        setEventImage(banner, event);
    }


    /** Sets an image to the background using Picasso, if event doesn't have an image.
     *  it will assign a generic image instead. **/
    public void setEventImage(View banner, Event event){
        EventBackgroundTarget eventBackgroundTarget = new EventBackgroundTarget(getApplicationContext(), banner);
        if(!event.getImg_url().isEmpty()) {
            Picasso.with(this).load(event.getImg_url()).resize(650, 280).centerCrop().into(eventBackgroundTarget);
            target = eventBackgroundTarget;
        }else{
            if(event.getCategory() == Event.CATEGORY_CONCERTS_GIG) banner.setBackgroundResource(R.drawable.gigs);
            else if(event.getCategory() == Event.CATEGORY_EXHIBITIONS) banner.setBackgroundResource(R.drawable.exhibition);
            else if(event.getCategory() == Event.CATEGORY_PERFORMING_ARTS) banner.setBackgroundResource(R.drawable.perform_arts);
            else if(event.getCategory() == Event.CATEGORY_SPORTS_OUTDOORS) banner.setBackgroundResource(R.drawable.sports);
            else if(event.getCategory() == Event.CATEGORY_WORKSHOPS_CLASSES) banner.setBackgroundResource(R.drawable.workshop);
            else if(event.getCategory() == Event.CATEGORY_FESTIVALS_LIFESTYLE) banner.setBackgroundResource(R.drawable.festivals);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStop(){
        super.onStop();
        supportFinishAfterTransition();
    }
}
