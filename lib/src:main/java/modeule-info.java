module com.upodotel.upodotel {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.upodotel.hotelreservation to javafx.fxml;
    exports com.upodotel.hotelreservation;
    exports com.upodotel.hotelreservation.controllers;
    opens com.upodotel.hotelreservation.controllers to javafx.fxml;
    exports com.upodotel.hotelreservation.entities;
    opens com.upodotel.hotelreservation.entities to javafx.fxml;
}