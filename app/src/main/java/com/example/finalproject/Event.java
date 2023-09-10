package com.example.finalproject;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    private String eventId;
    private String userId;
    private String eventType;
    private String location;
    private String description;
    private String riskLevel;
    private String region;
    private Bitmap eventImage;
    private Date date;

    private List<String> comments;


    public Event(String eventId, String userId, String eventType, String location, String description,
                 String riskLevel, String region, Bitmap eventImage, Date date) {
        this.eventId = eventId;
        this.userId = userId;
        this.eventType = eventType;
        this.location = location;
        this.description = description;
        this.riskLevel = riskLevel;
        this.region = region;
        this.eventImage = eventImage;
        this.date = date;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Bitmap getEventImage() {
        return eventImage;
    }

    public void setEventImage(Bitmap eventImage) {
        this.eventImage = eventImage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }

    public void clearComments() {
        if (comments != null) {
            comments.clear();
        }
    }
}
