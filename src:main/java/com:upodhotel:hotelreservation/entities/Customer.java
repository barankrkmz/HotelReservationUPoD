package com.upodotel.hotelreservation.entities;

import java.util.Date;

public class Customer{
    private final int id;
    private final String name;
    private final String identityNumber;
    private final String phoneNumber;
    private final Date birthDate;
    private final String description;

    public Customer(int id, String name, String identityNumber, String phoneNumber, Date birthDate, String description) {
        this.id = id;
        this.name = name;
        this.identityNumber = identityNumber;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getDescription() {
        return description;
    }


}