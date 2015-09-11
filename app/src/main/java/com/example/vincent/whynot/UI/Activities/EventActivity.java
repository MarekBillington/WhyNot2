package com.example.vincent.whynot.UI.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.EventClasses.Event;


/**
 * A large more descriptive view of each activity.
 **/

public class EventActivity extends AppCompatActivity {

    private Context context;
    private Event event;

    /** Views and UI objects. **/
    private TextView nameTextView, categoryTextView, descriptionTextView, distanceTextView,
            addressTextView, timeTextView, restrictionsTextView, websiteTextView, ticketTextView;
    private ImageView websiteButton, ticketButton, directionsButton;
    private RelativeLayout banner;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Allow transitions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade());
            getWindow().setEnterTransition(new Fade());
        }

        setContentView(R.layout.activity_event_polished);

        this.context = getApplicationContext();

        // Get Event using bundled event id
        Bundle bundle = getIntent().getExtras();
        String eventId = bundle.getString("id");

        event = App.app.getEventByID(Integer.parseInt(eventId));

        initialiseToolbar();
        initialiseViews();
    }

    /** Initialise the toolbar **/
    private void initialiseToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Initialise all the EventActivity's views. t
     **/
    private void initialiseViews() {
        categoryTextView = (TextView) findViewById(R.id.event_category);
        categoryTextView.setText(event.getCategoryString());

        nameTextView = (TextView) findViewById(R.id.event_name);
        nameTextView.setText(event.getName());

        distanceTextView = (TextView) findViewById(R.id.event_distance);
        distanceTextView.setText(event.getDistance() + "km away");

        addressTextView = (TextView) findViewById(R.id.event_location);
        addressTextView.setText("at " + event.getAddress());

        directionsButton = (ImageView) findViewById(R.id.button_route);
        directionsButton.setOnClickListener(openDirectionsOnClickListener);

        restrictionsTextView = (TextView) findViewById(R.id.event_restrictions);
        restrictionsTextView.setText("Event rated: " + event.getRestrictions());

        descriptionTextView = (TextView) findViewById(R.id.event_description);
        String description = event.getDescription();
        // TEMPORARY: just to check scrollView behaviour inside Activity
        String extralong = description + description + description;
        descriptionTextView.setText(extralong);

        // Only get the time, not the date
        timeTextView = (TextView) findViewById(R.id.event_time);
        timeTextView.setText(event.getTime());

        initialiseBottomButtons();

        banner = (RelativeLayout) findViewById(R.id.image_container);
        App.setEventImage(this, banner, event);
    }

    /** Initialise all the buttons at the bottom of the activity and set
     * their onClick listeners. **/
    private void initialiseBottomButtons() {
        websiteButton = (ImageView) findViewById(R.id.button_website);
        websiteButton.setOnClickListener(websiteOnClickListener);
        websiteTextView = (TextView) findViewById(R.id.text_website);
        websiteTextView.setOnClickListener(websiteOnClickListener);

        ticketButton = (ImageView) findViewById(R.id.button_ticket);
        ticketButton.setOnClickListener(ticketOnClickListener);
        ticketTextView = (TextView) findViewById(R.id.text_tickets);
        ticketTextView.setOnClickListener(ticketOnClickListener);
    }

    /** onClick listeners for website and tickets buttons.**/
    private View.OnClickListener websiteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openWebsite(event.getWebpage());
        }
    };

    private View.OnClickListener ticketOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!event.getTicketUrl().equals(""))
                openWebsite(event.getTicketUrl());
        }
    };

    private View.OnClickListener openDirectionsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?f=d&daddr=" + event.getLatitude() + "," + event.getLongitude()));
            intent.setComponent(new ComponentName("com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"));
            startActivity(intent);
        }
    };

    /**
     * Opens the eventfinda or booking website link.
     **/
    private void openWebsite(String url) {
        supportFinishAfterTransition();
        Intent viewIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(viewIntent);
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
}
