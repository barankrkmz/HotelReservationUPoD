package com.upodotel.hotelreservation.entities;

public class Feature {
    private final int featureId;
    private final String featureName;

    public Feature(int featureId, String featureName) {
        this.featureId = featureId;
        this.featureName = featureName;
    }

    public int getFeatureId() {
        return featureId;
    }

    public String getFeatureName() {
        return featureName;
    }

}