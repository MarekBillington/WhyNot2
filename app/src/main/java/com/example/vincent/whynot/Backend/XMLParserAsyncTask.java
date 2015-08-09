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

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        buildXMLFile(eventsString);
        return "";
    }



    public void buildXMLFile(String xmlString) {

        System.out.println("Starting tyo build xml doc");
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            NodeList events = doc.getElementsByTagName("event");
            Event eventobj;
            for(int i = 0; i < events.getLength(); i++)
            {
                eventobj = new Event(myApp);
                System.out.println("Starting for loop");
                Element event = (Element) events.item(i);
                Node id = event.getElementsByTagName("id").item(0);
                eventobj.setId(id.getTextContent());
                Node name = event.getElementsByTagName("name").item(0);
                eventobj.setName(name.getTextContent());
                System.out.println("Name node = " + name);

                System.out.println("Name = " + name.getTextContent());
                Node desc = event.getElementsByTagName("description").item(0);
                eventobj.setDescription(desc.getTextContent());
                Node dt_start = event.getElementsByTagName("datetime_start").item(0);
                eventobj.setDt_start(dt_start.getTextContent());
                Node web = event.getElementsByTagName("url").item(0);
                eventobj.setWebpage(web.getTextContent());
                Node loc = event.getElementsByTagName("location_summary").item(0);
                eventobj.setLocation(loc.getTextContent());
                Element point = (Element)event.getElementsByTagName("point").item(0);
                Node lat = point.getElementsByTagName("lat").item(0);
                eventobj.setLatitude(Float.parseFloat(lat.getTextContent()));
                Node lng = point.getElementsByTagName("lng").item(0);
                eventobj.setLongitude(Float.parseFloat(lng.getTextContent()));
                Node free = event.getElementsByTagName("is_free").item(0);

                if(free.getTextContent() == "0")
                {
                    ArrayList<String> prices = new ArrayList<>();
                    Element ticket_types = (Element)event.getElementsByTagName("ticket_types").item(0);
                    NodeList t_types = ticket_types.getElementsByTagName("ticket_type");
                    for(int j = 0; j < t_types.getLength(); j++)
                    {
                        int pricePointer = 0;
                        Element ticket_type = (Element) t_types.item(j);
                        Node price = ticket_type.getElementsByTagName("price").item(0);
                        prices.add(price.getTextContent());
                    }
                }

                Element all_img = (Element)event.getElementsByTagName("images").item(0);
                NodeList images = all_img.getElementsByTagName("image");
                for (int j = 0; j < images.getLength(); j++) {
                    Element img = (Element) images.item(j);
                    NodeList trans = img.getElementsByTagName("transform");
                    int imgpointer = 0;
                    for (int k = 0; k < trans.getLength(); k++) {
                        Element tran = (Element) trans.item(k);
                        Node width = tran.getElementsByTagName("width").item(0);
                        if(width.getTextContent() == "350") {
                            Node url = tran.getElementsByTagName("url").item(0);
                            eventobj.setImgUrl(url.getTextContent());
                        }
                    }
                }
                if (eventobj.verifyEvent()) {
                    myEvents.add(eventobj);
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
        myApp.setEventsArray(myEvents);
        System.out.println("testing size" + myEvents.size());
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        myApp.myActivity.updateFromEvents(myApp);
    }

}