package com.example.vincent.whynot.Backend;

import android.os.AsyncTask;

import com.example.vincent.whynot.App;
import com.example.vincent.whynot.UI.Event;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class XMLParserAsyncTask extends AsyncTask<Void, Void, String> {

    private App myApp;
    private String eventsString;
    private int offset;
    CopyOnWriteArrayList<Event> myEvents = new CopyOnWriteArrayList<Event>();

    public XMLParserAsyncTask(App app, String eventsString, int asyncTaskOffset) {
        myApp = app;
        this.eventsString = eventsString;
        offset = asyncTaskOffset;
    }

    // Overridden class functions

    @Override
    protected String doInBackground(Void... params) {
        if (myApp.getUserLocation() != null) {
            buildXMLFile(eventsString);
        } else {
            buildXMLFileWithoutLocationData(eventsString);
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if ((offset + 20) >= myApp.getEventsCount()) {
            System.out.println("Testing: Thread" + offset / 20 + " has offset + 20 larger than " +
                    "or equal to the number of events (offset = " + offset + ", events count = " +
                    myApp.getEventsCount() + "). Calling update from events :)");
            myApp.myActivity.updateFromEvents(myApp);
            myApp.setOffset(0);
        }
    }

    // Helper functions

    private void buildXMLFile(String xmlString) {

        try {

            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            NodeList events = doc.getElementsByTagName("event");

            for (int i = 0; i < events.getLength(); i++) {

                Element event = (Element) events.item(i);
                String startDate = event.getElementsByTagName("datetime_start").item(0).getTextContent();

                if (verifyDate(startDate)) {

                    Event eventObject = new Event(myApp);

                    Node id = event.getElementsByTagName("id").item(0);
                    eventObject.setId(id.getTextContent());

                    Node name = event.getElementsByTagName("name").item(0);
                    eventObject.setName(name.getTextContent());


                    // Get the category
                    Node category = event.getElementsByTagName("category").item(0);
                    NodeList categoryNodeList = category.getChildNodes();
                    for (int j = 0; j < categoryNodeList.getLength(); j++) {
                        if (categoryNodeList.item(j).getNodeName().equals("parent_id"))
                            eventObject.setCategory(Integer.parseInt(categoryNodeList.item(j).getTextContent()));

                       // if (categoryNodeList.item(j).getNodeName().equals("name"))
                        //   eventObject.setCategory(categoryNodeList.item(j).getTextContent());
                    }

                    Node dt_start = event.getElementsByTagName("datetime_start").item(0);
                    eventObject.setDt_start(dt_start.getTextContent());

                    Node desc = event.getElementsByTagName("description").item(0);
                    eventObject.setDescription(desc.getTextContent());


                    eventObject.setDt_start(startDate);

                    String endDate = event.getElementsByTagName("datetime_end").item(0).getTextContent();
                    eventObject.setEndDate(endDate);

                    Node web = event.getElementsByTagName("url").item(0);
                    eventObject.setWebpage(web.getTextContent());

                    Node loc = event.getElementsByTagName("location_summary").item(0);
                    eventObject.setLocation(loc.getTextContent());

                    Element point = (Element) event.getElementsByTagName("point").item(0);
                    Node lat = point.getElementsByTagName("lat").item(0);
                    eventObject.setLatitude(Float.parseFloat(lat.getTextContent()));

                    Node lng = point.getElementsByTagName("lng").item(0);
                    eventObject.setLongitude(Float.parseFloat(lng.getTextContent()));

                    eventObject.setDistance();

                    Node free = event.getElementsByTagName("is_free").item(0);
                    if (free.getTextContent().equals("1")) eventObject.setCheapest("Free");
                    else eventObject.setCheapest("Paid");

                    if (free.getTextContent() == "0") {
                        ArrayList<String> prices = new ArrayList<>();
                        Element ticket_types = (Element) event.getElementsByTagName("ticket_types").item(0);
                        NodeList t_types = ticket_types.getElementsByTagName("ticket_type");
                        for (int j = 0; j < t_types.getLength(); j++) {
                            int pricePointer = 0;
                            Element ticket_type = (Element) t_types.item(j);
                            Node price = ticket_type.getElementsByTagName("price").item(0);
                            prices.add(price.getTextContent());
                        }
                    }


                //Get the best image for the event item background
                //This could probably be placed in its own method
                Element all_img = (Element) event.getElementsByTagName("images").item(0);
                eventObject.setImg_url("");
                NodeList images = all_img.getElementsByTagName("image");
                for (int j = 0; j < images.getLength(); j++) {
                    Element img = (Element) images.item(j);
                    // Check if an image has already been assigned
                    Node isPrimary = img.getElementsByTagName("is_primary").item(0);
                    if (eventObject.getImg_url() != "") break;

                    NodeList trans = img.getElementsByTagName("transform");

                    for (int k = 0; k < trans.getLength(); k++) {
                        //Find the optimal transformation of the image, ideally 650x280 (id = 7)
                        //Otherwise, 350x350 (id = 27)
                        Element tran = (Element) trans.item(k);
                        //System.out.println("Testing images : " + eventObject.getName() + " " + tran.getElementsByTagName("url").item(0).getTextContent());

                        Node transformationId = tran.getElementsByTagName("transformation_id").item(0);
                        if (Integer.parseInt(transformationId.getTextContent()) == 7) {
                            Node url = tran.getElementsByTagName("url").item(0);
                            eventObject.setImgUrl(url.getTextContent());
                            break;
                        } else if (Integer.parseInt(transformationId.getTextContent()) == 27) {
                            Node url = tran.getElementsByTagName("url").item(0);
                            eventObject.setImgUrl(url.getTextContent());

                        }
                    }
                }
                    eventObject.setDistance();
                    myEvents.add(eventObject);
                    System.out.println("Testing: Thread " + offset / 20 +
                            " built event " + eventObject.getName());

                }
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (UnsupportedEncodingException uce) {
            uce.printStackTrace();
        } catch (SAXException sex) {
            sex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (myApp.getEventsArray() == null) {
            myApp.setEventsArray(myEvents);
            System.out.println("Testing: Thread " + offset / 20 + " adding " +
                    myEvents.size() + " events to the events array");
        } else {
            myApp.appendEvents(myEvents);
            System.out.println("Testing: Thread " + offset / 20 + " appending " +
                    myEvents.size() + " events to the events array");
        }
    }

    private void buildXMLFileWithoutLocationData(String xmlString) {
        try {

            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            NodeList events = doc.getElementsByTagName("event");

            for (int i = 0; i < events.getLength(); i++) {

                Element event = (Element) events.item(i);
                String startDate = event.getElementsByTagName("datetime_start").item(0).getTextContent();

                if (verifyDate(startDate)) {

                    Event eventObject = new Event(myApp);

                    Node id = event.getElementsByTagName("id").item(0);
                    eventObject.setId(id.getTextContent());

                    Node name = event.getElementsByTagName("name").item(0);
                    eventObject.setName(name.getTextContent());

                    Node desc = event.getElementsByTagName("description").item(0);
                    eventObject.setDescription(desc.getTextContent());

                    eventObject.setDt_start(startDate);
                    String endDate = event.getElementsByTagName("datetime_end").item(0).getTextContent();
                    eventObject.setEndDate(endDate);

                    Node web = event.getElementsByTagName("url").item(0);
                    eventObject.setWebpage(web.getTextContent());

                    Node loc = event.getElementsByTagName("location_summary").item(0);
                    eventObject.setLocation(loc.getTextContent());

                    eventObject.setDistance();

                    Node free = event.getElementsByTagName("is_free").item(0);
                    if (free.getTextContent() == "0") {
                        ArrayList<String> prices = new ArrayList<>();
                        Element ticket_types = (Element) event.getElementsByTagName("ticket_types").item(0);
                        NodeList t_types = ticket_types.getElementsByTagName("ticket_type");
                        for (int j = 0; j < t_types.getLength(); j++) {
                            int pricePointer = 0;
                            Element ticket_type = (Element) t_types.item(j);
                            Node price = ticket_type.getElementsByTagName("price").item(0);
                            prices.add(price.getTextContent());
                        }
                    }

                    Element all_img = (Element) event.getElementsByTagName("images").item(0);
                    NodeList images = all_img.getElementsByTagName("image");
                    for (int j = 0; j < images.getLength(); j++) {
                        Element img = (Element) images.item(j);
                        NodeList trans = img.getElementsByTagName("transform");
                        int imgpointer = 0;
                        for (int k = 0; k < trans.getLength(); k++) {
                            Element tran = (Element) trans.item(k);
                            Node width = tran.getElementsByTagName("width").item(0);
                            if (width.getTextContent() == "350") {
                                Node url = tran.getElementsByTagName("url").item(0);
                                eventObject.setImgUrl(url.getTextContent());
                            }
                        }
                    }

                    myEvents.add(eventObject);
                    System.out.println("Testing: Thread " + offset / 20 +
                            " built event " + eventObject.getName());

                }
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (UnsupportedEncodingException uce) {
            uce.printStackTrace();
        } catch (SAXException sex) {
            sex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (myApp.getEventsArray() == null) {
            myApp.setEventsArray(myEvents);
            System.out.println("Testing: Appending " + myEvents.size() + " events to the events array");
        } else {
            myApp.appendEvents(myEvents);
            System.out.println("Testing: Appending " + myEvents.size() + " events to the events array");
        }
    }

    // verify that the event falls into the relevant date parameters
    public boolean verifyDate(String startDate) {
        GregorianCalendar eventStartDate = new GregorianCalendar();
        GregorianCalendar endOfToday = new GregorianCalendar();
        endOfToday.set(Calendar.HOUR_OF_DAY, 23);
        endOfToday.set(Calendar.MINUTE, 59);
        endOfToday.set(Calendar.SECOND, 59);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(startDate);
            eventStartDate.setTime(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        boolean startsBeforeTomorrow = eventStartDate.getTimeInMillis() < endOfToday.getTimeInMillis();
        if (startsBeforeTomorrow) {
            return true;
        } else {
            return false;
        }

    }


}
