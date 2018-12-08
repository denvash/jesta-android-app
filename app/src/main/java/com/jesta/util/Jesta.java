package com.jesta.util;

import java.util.HashMap;

public class Jesta {


    private String id;
    private String difficulty;
    private String description;
    private String imageUrl;
    private long payment;
    private long numOfPeople;
    private long duration;
    private String location;

    public Jesta(String id, String difficulty, String description, String imageUrl, int payment, int numOfPeople, int duration, String location) {
        this.id = difficulty;
        this.difficulty = difficulty;
        this.description = description;
        this.imageUrl = imageUrl;
        this.payment = payment;
        this.numOfPeople = numOfPeople;
        this.duration = duration;
        this.location = location;
    }

    public Jesta(HashMap dbJesta) {
        this.id = (String)dbJesta.get("id");
        this.difficulty = (String)dbJesta.get("difficulty");
        this.description = (String)dbJesta.get("description");
        this.imageUrl = (String)dbJesta.get("imageUrl");
        this.payment = (long) dbJesta.get("payment");
        this.numOfPeople = (long)dbJesta.get("numOfPeople");
        this.duration = (long)dbJesta.get("duration");
        this.location = (String)dbJesta.get("location");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public long getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }



}
