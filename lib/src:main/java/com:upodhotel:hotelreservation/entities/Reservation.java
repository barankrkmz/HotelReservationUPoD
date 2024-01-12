package com.upodotel.hotelreservation.entities;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Reservation {
    private final int reservationId;
    private final int roomId;
    private final String roomName;
    private final List<Customer> customers;
    private final Date checkInDate;
    private final Date checkOutDate;
    private final String checkedIn;
    private final String checkedOut;
    private final Date checkedInDate;
    private final Date checkedOutDate;
    private final Map<Service, Integer> services;
    private String customerNames = "";

    public Reservation(int reservationId, int roomId, String roomName, List<Customer> customers, Date checkInDate, Date checkOutDate,
                       boolean checkedIn, boolean checkedOut, Date checkedInDate, Date checkedOutDate, Map<Service, Integer> services) {
        this.reservationId = reservationId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.customers = customers;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.checkedIn = checkedIn ? "YES" : "NO";
        this.checkedOut = checkedOut ? "YES" : "NO";
        this.checkedInDate = checkedInDate;
        this.checkedOutDate = checkedOutDate;
        this.services = services;

        
        for (Customer customer : customers) {
            customerNames += person.getName() + ", ";
        }
        customerNames = customerNames.substring(0, customerNames.length() - 2);
    }

    public int getReservationId() {
        return reservationId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public String getCustomerNames() {
        return customerNames;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public String isCheckedIn() {
        return checkedIn;
    }

    public String isCheckedOut() {
        return checkedOut;
    }

    public Date getCheckedInDate() {
        return checkedInDate;
    }

    public Date getCheckedOutDate() {
        return checkedOutDate;
    }

    public Map<Service, Integer> getServices() {
        return services;
    }
}