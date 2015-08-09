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
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Event;
import com.example.vincent.whynot.UI.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab2 extends SupportMapFragment {

    public GoogleMap mMap;
    private MapView mapView;

//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.tab_2,container,false);
//        //mapView = (MapView) v.findViewById(R.id.mapView);
//       //mapView.onCreate(savedInstanceState);
//        setUpMapIfNeeded();
//
//        return v;
//    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.



        // Gets to GoogleMap from the MapView and does initialization stuff
        //mapView.getMapAsync(onMapReadyCallback)
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            //mMap = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
//            ((SupportMapFragment) MainActivity.fragmentManager
//                    .findFragmentById(R.id.map))
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
            System.out.println("debug2");
        }
    };

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    public void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(-36.861964, 174.757032)).title("Marker"));
        mMap.setMyLocationEnabled(true);

        centreMapOnUser();
        //placeMarkers(app);

        mMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {
                    boolean doNotMoveCameraToCenterMarker = true;

                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();

                        return doNotMoveCameraToCenterMarker;
                    }
                });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            final Context context = getActivity().getApplicationContext();
            @Override
            public void onInfoWindowClick(Marker marker) {
//                Intent viewIntent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse(marker.getSnippet().split("%")[1]));
//                viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(viewIntent);
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            public View getInfoContents(Marker marker) {

                final Context context = getActivity().getApplicationContext(); //or getActivity(), YourActivity.this, etc.

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
                //snippet.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                snippet.setGravity(Gravity.CENTER);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(description);

                Button openSite = new Button(context);
                openSite.setText("Open site");
                openSite.setClickable(true);
                openSite.setEnabled(true);

                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//
//                        try {
//                            Intent viewIntent = new Intent("Intent.ACTION_VIEW",
//                                    Uri.parse("http://www.google.com"));
//                            context.startActivity(viewIntent);
//                        } catch (Exception e) {
//                            Log.e("CC", e.getMessage());
//                        }
                    }
                });

                info.addView(title);
                info.addView(snippet);
                info.addView(openSite);

                return info;
            }
        });

    }

    public void placeMarkers(App app){

       for(Event e: app.getEventsArray()) {
           mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(e.getLatitude(), e.getLongitude()))
                    .title(e.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.eventicon)))
                    .setSnippet(e.getOverview());
                    //.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.eventicon));

       }
    }

    //Focuses zoom on to user
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

}