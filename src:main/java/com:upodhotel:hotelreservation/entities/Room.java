package com.upodotel.hotelreservation.entities;

import java.util.List;

public class Room {
    private final int id;
    private final float price;
    private final List<Feature> features;
    private String featuresString = "";
    private final String name;
    private final int capacity;
    

    public Room(String name, int capacity, float price, int id, List<Feature> features) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.price = price;
        this.features = features;
        for (Feature feature : features) {
            featuresString += feature.getFeatureName() + ", ";
        }
        if (!featuresString.equals("")) {
            featuresString = featuresString.substring(0, featuresString.length() - 2);
        }
    }

    public float getPrice() {
        return price;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public String getFeaturesString() {
        return featuresString;
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    
}