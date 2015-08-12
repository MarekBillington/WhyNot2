package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Fragment containing a Google Map that is used to display all the various events.
 * It is loaded asynchronously and once the events have been retrieved, the placeMarkers
 * method is called and the markers are placed.
 */
public class MapsFragment extends SupportMapFragment {

    public GoogleMap mMap;

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        // Gets to GoogleMap from the MapView and does initialization stuff

        if (mMap == null) {
            this.getMapAsync(onMapReadyCallback);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            setUpMap();
        }
    };

    /**
     * Set up the map, including the custom info windows.
     */
    public void setUpMap() {
        mMap.setMyLocationEnabled(true);

        centreMapOnUser();
        //This code stops the map camera from focusing on the info window when clicking on a marker
        //however it also stops the direction buttons from appearing
//        mMap.setOnMarkerClickListener(
//                new GoogleMap.OnMarkerClickListener() {
//                    boolean doNotMoveCameraToCenterMarker = true;
//
//                    public boolean onMarkerClick(Marker marker) {
//                        marker.showInfoWindow();
//
//                        return doNotMoveCameraToCenterMarker;
//                    }
//                });

      mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            final Context context = getActivity().getApplicationContext();
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(marker.getSnippet().split("%")[1]));
                viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewIntent);
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            public View getInfoContents(Marker marker) {

                final Context context = getActivity().getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                //Custom info window containing 2 text views and a rendered button
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                String description = marker.getSnippet().split("%")[0];
                //String url = marker.getSnippet().split("%")[1];

                TextView title = new TextView(context);
                title.setPadding(8,8,8,8);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setGravity(Gravity.CENTER);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(description);

                //Button is only rendered and is non-functional
                Button openSite = new Button(context);
                openSite.setText("Visit site");

                info.addView(title);
                info.addView(snippet);
                info.addView(openSite);

                return info;
            }
        });

    }

    /** Places all the event markers on the map. */
    public void placeMarkers(App app){

       for(Event e: app.getEventsArray()) {
           mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(e.getLatitude(), e.getLongitude()))
                    .title(e.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.eventicon)))
                    .setSnippet(e.getOverview());
       }
    }

    public void centreMapOnUser(){

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        LatLng myLocation = null;

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
    }

    //Focuses zoom on to user

    public void centreMapOnLocation(Location location){

        LatLng myLocation = null;

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
    }

}