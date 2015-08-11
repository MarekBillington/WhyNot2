package com.example.vincent.whynot.Backend;

import com.example.vincent.whynot.App;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class ConnectToRESTAsyncTask extends AsyncTask<Void, Void, String> {

    private App myApp;

    public ConnectToRESTAsyncTask(App app) {
        myApp = app;
    }

    // Overridden class methods

    @Override
    protected String doInBackground(Void... params) {
        String results = getDataStringFromURL();
        return results;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Call the XMLParser async task and pass it the String retrieved from the url
        myApp.getEventsArrayFromString(result);
    }

    // Helper methods

    private String getDataStringFromURL() {

        // Initialise string to be returned
        String results = "";
        // Try to establish URL connection with HttpURLConnection object
        try {
            URL url = new URL("http://api.eventfinda.co.nz/v2/events.xml?rows=20&end_date=" + getEndDateTimeString());
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
        return results;
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
