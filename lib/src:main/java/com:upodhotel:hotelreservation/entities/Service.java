package com.upodotel.hotelreservation.entities;

public class Service {
    private final int serviceId;
    private final String serviceName;
    private final float unitPrice;

    public Service(int serviceId, String serviceName, float unitPrice) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.unitPrice = unitPrice;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

}