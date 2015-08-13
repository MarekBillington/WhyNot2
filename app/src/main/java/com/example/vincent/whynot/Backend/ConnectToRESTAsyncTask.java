package com.example.vincent.whynot.Backend;

import com.example.vincent.whynot.App;

import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ConnectToRESTAsyncTask extends AsyncTask<Void, Void, String> {

    private App myApp;
    private int offset;

    public ConnectToRESTAsyncTask(App app, int asyncTaskOffset) {
        myApp = app;
        offset = asyncTaskOffset;
    }

    // Overridden class methods

    @Override
    protected String doInBackground(Void... params) {
        String results = getDataStringFromURL();
        if (myApp.getEventsCount() == 1) {
            myApp.setEventsCount(getEventsCountFromXMLString(results));
        }
        return results;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Call the XMLParser async task and pass it the String retrieved from the url
        if (offset == 0) {
            while (myApp.getOffset() < myApp.getEventsCount()) {
                myApp.getEventsStringHTTPRequest(myApp.getOffset());
                System.out.println("Testing: Spawning thread " + myApp.getOffset() / 20);
                myApp.setOffset(myApp.getOffset() + 20);
            }
        } else {
            System.out.println("Testing: Thread " + offset / 20 + " about to parse xml string into events");
            myApp.getEventsArrayFromString(result, offset);
        }
    }

    // Helper methods

    private String getDataStringFromURL() {

        // Initialise url to be used
        URL url;
        // Initialise string to be returned
        String results = "";

        // Try to establish URL connection with HttpURLConnection object
        try {
            if (myApp.getUserLocation() == null) {
                url = new URL("http://api.eventfinda.co.nz/v2/events.xml?order=date" +
                        "&rows=20" +
                        "&end_date=2015-08-13%2023:59:59" +
                        "&offset=" + offset);
            } else {
                url = new URL("http://api.eventfinda.co.nz/v2/events.xml?order=date" +
                        "&rows=20" +
                        "&end_date=" + getEndDateTimeString() +
                        "&offset=" + offset +
                        "&point=" + getUserLocationCoordinateString() +
                        "&radius=" + myApp.getRadiusLength());
            }
            System.out.println("Testing: URL = " + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // Try to initialise a buffered input stream from the url connection,
            // read it using the readStream helper function, and assign the returned
            // string to 'results'
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                results = readStream(in);

                // Close the url connection
            } finally {
                urlConnection.disconnect();
            }
        // Deal with issues...
        } catch (MalformedURLException mue) {
            System.out.println("Testing: Issue with URL");
            mue.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Issue with retrieving data from eventfinda");
            //Toast.makeText(myApp.getApplicationContext(),"Issue with retrieving data from eventfinda",Toast.LENGTH_SHORT).show();
            ioe.printStackTrace();
        }
        // Pass the retrieved string to onPostExecute() method
        if (results.length() > 50) {
            System.out.println("Testing: Data successfully obtained from EventFinda API");
        }
        results = results.replaceAll("[^\\x20-\\x7e]", "");
        return results;
    }

    // Returns the total count of events returned by the http request
    private int getEventsCountFromXMLString(String xmlString) {
        int eventsCount = 0;
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            NodeList events = doc.getElementsByTagName("events");
            eventsCount = Integer.valueOf(events.item(0).getAttributes().
                    getNamedItem("count").getNodeValue());
            System.out.println("Testing: Events count = " + events.item(0).getAttributes().
                    getNamedItem("count").getNodeValue());
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (UnsupportedEncodingException uce) {
            uce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();;
        }
        return eventsCount;
    }

    // Returns a string of the end of today's date and time
    private String getEndDateTimeString() {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar calendar = new GregorianCalendar();
        String dateString = date_format.format(calendar.getTime());
        String endDateTimeString = dateString + "%2023:59:59";
        System.out.println("Testing: End date time string = " + endDateTimeString);
        return endDateTimeString;
    }

    // returns a string of the users location coordinates for use in the url request parameters
    private String getUserLocationCoordinateString() {
        Location userLocation = myApp.getUserLocation();
        String latitude = Double.toString(userLocation.getLatitude());
        String longitude = Double.toString(userLocation.getLongitude());
        String coordinateString = latitude + "," + longitude;
        System.out.println("Testing: user location = " + coordinateString);
        return coordinateString;
    }

    // Reads an input stream, turns it into a string, and returns it
    private String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
