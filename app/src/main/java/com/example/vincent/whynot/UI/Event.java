package com.example.vincent.whynot.UI;

import android.media.Image;

import java.util.Date;

/**
 * Created by Vincent on 8/08/2015.
 */
public class Event {
    private String title;
    private String description;
    private String startDate, endDate;
    private double latitude, longitude;
    private double price;
    private int ageRestriction;
    private String link;
    private Image image;

    public Event(String title, String description, String startDate, String endDate, double latitude, double longitude, double price, int ageRestriction, String link, Image image) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.ageRestriction = ageRestriction;
        this.link = link;
        this.image = image;
    }

    public String getOverview(){
        String nl = System.getProperty("line.separator");
        String output = "\n" + this.description + "\n\n";
        output += "$" + this.price + " per person.\n";

        if(ageRestriction > 0) output += "Minimum age: " + this.ageRestriction + "\n";

        output += this.link + "\n";
        return output;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(int ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
