package com.example.vincent.whynot.UI.dummy;

/**
 * Created by George on 8/8/2015.
 */
import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;
import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Event;
import com.example.vincent.whynot.UI.EventActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Fragment containing a Google Map that is used to display all the various events.
 * It is loaded asynchronously and once the events have been retrieved, the placeMarkers
 * method is called and the markers are placed.
 */
public class MapsFragment extends SupportMapFragment {

    /** Heights for centering the camera, default is for centering on events, initial is the initial
     *  height of the camera. **/
    public static final int DEFAULT_HEIGHT = 16, INITIAL_HEIGHT = 13;

    public static GoogleMap mMap;

    /** If the map hasn't been retrieved, get it, then set it up, otherwise set it up straight away.**/
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            this.getExtendedMapAsync(onMapReadyCallback);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /** Callback for setting up the map once it has been retrieved. **/
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
        // Open event activity on click
         mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
          @Override
          public void onInfoWindowClick(Marker marker) {
              Intent intent = new Intent(getActivity(), EventActivity.class);
              intent.putExtra("id", marker.getSnippet());
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  getActivity().startActivity(intent);
          }
         });

        // Set the info window adapter, place the markers if the events have already loaded, and
        // Centre the map on the user if the location has been retrieved.
        mMap.setInfoWindowAdapter(new EventInfoWindowAdapter());
        if(!App.app.getEventsArray().isEmpty()) placeMarkers();
        if(App.userLocation != null) centreMapOnLocation(App.userLocation, INITIAL_HEIGHT);
    }

    /** Places all the event markers on the map. */
    public void placeMarkers(){
        mMap.clear();
       for(Event e: App.app.getEventsArray()) {
           mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(e.getLatitude(), e.getLongitude()))
                    .title(e.getName())
                    .icon(e.getMarker(getContext())))
                    .setSnippet(e.getId() + "");

       }

    }

    /** Get the marker with the id entered. **/
    public static Marker getMarker(int id){
        for (Marker marker: mMap.getMarkers()){
            if(Integer.parseInt(marker.getSnippet()) == id) return marker;
        }
        return null;
    }

    /** Focuses zoom on to given location. **/
    public static void centreMapOnLocation(Location location, int height){
        LatLng myLocation = null;

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        if(mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, height));
    }

    /** Same as above but focuses on an even and opens the events info as well. **/
    public static void centreMapOnEvent(Event event){
        centreMapOnLocation(event.getLocation(), DEFAULT_HEIGHT);
        getMarker(Integer.parseInt(event.getId())).showInfoWindow();
    }

    /** Inner class for custom event info windows. **/
    private class EventInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

        /** Views for the info windows.**/
        private View myContentsView;
        private TextView titleTextView, categoryTextView, timeTextView, addressTextView;
        private View banner;

            public EventInfoWindowAdapter(){
                myContentsView = getActivity().getLayoutInflater().inflate(R.layout.event_info_window, null);
            }

            public View getInfoWindow(Marker arg0) {
                return null;
            }

            public View getInfoContents(Marker marker) {
                Event event = App.getEventByID(Integer.parseInt(marker.getSnippet().trim()));

                titleTextView = (TextView) myContentsView.findViewById(R.id.event_name);
                titleTextView.setText(event.getName());

                categoryTextView = (TextView) myContentsView.findViewById(R.id.event_category);
                categoryTextView.setText(event.getCategoryString());

                addressTextView = (TextView) myContentsView.findViewById(R.id.event_location);
                addressTextView.setText(event.getAddressShort());

                timeTextView = (TextView) myContentsView.findViewById(R.id.event_time);
                timeTextView.setText(event.formatTime());

                banner = myContentsView.findViewById(R.id.image_container);
                App.setEventImage(getActivity(), banner, event);

                return myContentsView;
            }

        }

}