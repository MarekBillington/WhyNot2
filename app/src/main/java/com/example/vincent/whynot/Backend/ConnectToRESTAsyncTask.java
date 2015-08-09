package com.example.vincent.whynot.Backend;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.vincent.whynot.App;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectToRESTAsyncTask extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private App myApp;


    public ConnectToRESTAsyncTask(Context context, App app) {
        mContext = context;
        myApp = app;

    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {

        String results = "";
        try {
            URL url = new URL("http://api.eventfinda.co.nz/v2/events.xml?rows=20&end_date=" + myApp.getEndDateTimeString());
            //URL url = new URL("http://api.eventfinda.co.nz/v2/events.xml?rows=20&end_date=2015-08-12%2023:00:00");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                results = readStream(in);

            } finally {
                urlConnection.disconnect();
            }

        } catch (MalformedURLException e) {
            //do nothing
        } catch (IOException e) {
            // do nothing
        }
        return results;
    }

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

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println(result);
        myApp.setEventsString(result);
        myApp.getEventsNodeList(result);
    }
}
