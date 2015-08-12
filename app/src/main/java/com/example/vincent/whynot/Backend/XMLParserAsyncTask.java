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
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class XMLParserAsyncTask extends AsyncTask<Void, Void, String> {

    App myApp;
    String eventsString;
    ArrayList<Event> myEvents = new ArrayList<Event>();

    public XMLParserAsyncTask(App app, String eventsString) {
        myApp = app;
        this.eventsString = eventsString;
    }

    // Overridden class functions

    @Override
    protected String doInBackground(Void... params) {
        buildXMLFile(eventsString);
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        myApp.setOffset(myApp.getOffset() + 20);
        if (myApp.getOffset() < myApp.getEventsCount()) {
            myApp.getEventsStringHTTPRequest();
        } else {
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

            for(int i = 0; i < events.getLength(); i++) {

                Event eventObject = new Event(myApp);
                Element event = (Element) events.item(i);

                Node id = event.getElementsByTagName("id").item(0);
                eventObject.setId(id.getTextContent());

                Node name = event.getElementsByTagName("name").item(0);
                eventObject.setName(name.getTextContent());

                Node desc = event.getElementsByTagName("description").item(0);
                eventObject.setDescription(desc.getTextContent());

                Node dt_start = event.getElementsByTagName("datetime_start").item(0);
                eventObject.setDt_start(dt_start.getTextContent());

                Node endDate = event.getElementsByTagName("datetime_end").item(0);
                eventObject.setEndDate(endDate.getTextContent());

                Node web = event.getElementsByTagName("url").item(0);
                eventObject.setWebpage(web.getTextContent());

                Node loc = event.getElementsByTagName("location_summary").item(0);
                eventObject.setLocation(loc.getTextContent());

                Element point = (Element)event.getElementsByTagName("point").item(0);
                Node lat = point.getElementsByTagName("lat").item(0);
                eventObject.setLatitude(Float.parseFloat(lat.getTextContent()));

                Node lng = point.getElementsByTagName("lng").item(0);
                eventObject.setLongitude(Float.parseFloat(lng.getTextContent()));

                eventObject.setDistance();

                Node free = event.getElementsByTagName("is_free").item(0);
                if (free.getTextContent() == "0") {
                    ArrayList<String> prices = new ArrayList<>();
                    Element ticket_types = (Element)event.getElementsByTagName("ticket_types").item(0);
                    NodeList t_types = ticket_types.getElementsByTagName("ticket_type");
                    for(int j = 0; j < t_types.getLength(); j++) {
                        int pricePointer = 0;
                        Element ticket_type = (Element) t_types.item(j);
                        Node price = ticket_type.getElementsByTagName("price").item(0);
                        prices.add(price.getTextContent());
                    }
                }
                
                //Get the best image for the event item background
                //This could probably be placed in its own method
                Element all_img = (Element)event.getElementsByTagName("images").item(0);

                NodeList images = all_img.getElementsByTagName("image");
                for (int j = 0; j < images.getLength(); j++) {
                    Element img = (Element) images.item(j);
                    // Check if this image is the primary image, if not, continue
                    Node isPrimary = img.getElementsByTagName("is_primary").item(0);
                    if(Integer.parseInt(isPrimary.getTextContent()) != 1) continue;

                    NodeList trans = img.getElementsByTagName("transform");
                    int imgpointer = 0;
                    for (int k = 0; k < trans.getLength(); k++) {
                        //Element tran = (Element) trans.item(k);
                        //Node width = tran.getElementsByTagName("width").item(0);

                        //Find the optimal transformation of the image, ideally 650x280 (id = 7)
                        //Otherwise, 350x350 (id = 27)
                        Element tran = (Element) trans.item(k);
                        Node width = tran.getElementsByTagName("transformation_id").item(0);
                        if(Integer.parseInt(width.getTextContent()) == 7) {
                            Node url = tran.getElementsByTagName("url").item(0);
                            eventObject.setImgUrl(url.getTextContent());
                            break;
                        }else if(Integer.parseInt(width.getTextContent()) == 27) {
                            Node url = tran.getElementsByTagName("url").item(0);
                            eventObject.setImgUrl(url.getTextContent());
                        }
                    }
                }
                eventObject.setDistance();
                if (eventObject.verifySelf()) {
                    myEvents.add(eventObject);
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
        } else {
            myApp.appendEvents(myEvents);
        }
        System.out.println("Testing: Events array size =  " + myApp.getEventsArray().size());
    }

}
