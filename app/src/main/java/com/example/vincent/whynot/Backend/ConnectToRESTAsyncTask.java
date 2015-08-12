package com.example.vincent.whynot.Backend;

import com.example.vincent.whynot.App;

import android.location.Location;
import android.os.AsyncTask;

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
    private int offset = 0;
    private int eventsCount;
    private int eventsPulled = 0;

    public ConnectToRESTAsyncTask(App app) {
        myApp = app;
    }

    // Overridden class methods

    @Override
    protected String doInBackground(Void... params) {
        String results = getDataStringFromURL();
        eventsCount = getEventsCountFromXMLString(results);
        /*offset += 20;
        while (offset < eventsCount) {
            System.out.println("Testing: events count = " + eventsCount + ", Offset = " + offset);
            results += getDataStringFromURL();
            offset += 20;
        }*/
        return results;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Call the XMLParser async task and pass it the String retrieved from the url
        myApp.getEventsArrayFromString(result);
        // reset the value of offset
        offset = 0;
    }

    // Helper methods

    private String getDataStringFromURL() {

        // Initialise string to be returned
        String results = "";
        // Try to establish URL connection with HttpURLConnection object
        try {
//            URL url = new URL("http://api.eventfinda.co.nz/v2/events.xml?rows=20&end_date=" +
//                    getEndDateTimeString() + "&offset=" + offset +
//                    "&point=-36.84846,174.763332&radius=" + myApp.getRadiusLength());

            URL url = new URL("http://api.eventfinda.co.nz/v2/events.xml?rows=20&end_date=" +
                    getEndDateTimeString() + "&offset=" + offset +
                    "&point=" + getUserLocationCoordinateString() +
                    "&radius=" + myApp.getRadiusLength());

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
            System.out.println("Issue with URL");
            mue.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Issue with retrieving data from eventfinda");
            ioe.printStackTrace();
        }
        // Pass the retrieved string to onPostExecute() method
        System.out.println("Testing: String obtained from EventFinda API");
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
            eventsCount = Integer.valueOf(events.item(0).getAttributes().getNamedItem("count").getNodeValue());
            System.out.println("Testing: Events count = " + events.item(0).getAttributes().getNamedItem("count").getNodeValue());
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
            String xmlCheck = "xml";
            String eventsCheck = "events count=";
            while ((nextLine = reader.readLine()) != null) {
                if (offset == 0) {
                    sb.append(nextLine);
                } else if (nextLine.contains(xmlCheck)) {
                    System.out.println("Testing: xml declaration removed from string");
                } else if (nextLine.contains(eventsCheck)) {
                    System.out.println("Testing: events declaration removed from string");
                } else {
                    sb.append(nextLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
