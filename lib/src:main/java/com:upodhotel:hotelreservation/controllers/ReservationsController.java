package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import com.upodotel.hotelreservation.entities.Reservation;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class ReservationsController implements Initializable {
    @FXML
    private Button btn_rooms;
    @FXML
    private Button btn_features;
    @FXML
    private Button btn_services;
    @FXML
    private Button btn_customers;
    @FXML
    private TableView<Reservation> tv_reservation_table;
    @FXML
    private TableColumn<Reservation, Integer> tc_reservation_id;
    @FXML
    private TableColumn<Reservation, String> tc_reservation_room_name;
    @FXML
    private TableColumn<Reservation, Date> tc_reservation_check_in_date;
    @FXML
    private TableColumn<Reservation, Date> tc_reservation_check_out_date;
    @FXML
    private TableColumn<Reservation, String> tc_reservation_is_checked_in;
    @FXML
    private TableColumn<Reservation, String> tc_reservation_is_checked_out;
    @FXML
    private TableColumn<Reservation, String> tc_reservation_customer_names;
    @FXML
    private Button btn_new_reservation;
    @FXML
    private Button btn_edit_reservation;
    @FXML
    private Button btn_delete_reservation;
    @FXML
    private DatePicker dp_start_date;
    @FXML
    private DatePicker dp_end_date;
    @FXML
    private TextField tf_search;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the table and filters
        initializeTableAndFilters();

        // Handle button actions
        handleButtonActions();
    }

    private void initializeTableAndFilters() {
        showReservations();

        // Filter the table based on date and search criteria
        dp_start_date.valueProperty().addListener((observable, oldValue, newValue) -> filterTable(dp_start_date.getValue(), dp_end_date.getValue(), tf_search.getText()));
        dp_end_date.valueProperty().addListener((observable, oldValue, newValue) -> filterTable(dp_start_date.getValue(), dp_end_date.getValue(), tf_search.getText()));
        tf_search.textProperty().addListener((observable, oldValue, newValue) -> filterTable(dp_start_date.getValue(), dp_end_date.getValue(), tf_search.getText()));

        // Enable/disable edit and delete buttons based on selection
        tv_reservation_table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Reservation>) c -> {
            btn_edit_reservation.setDisable(c.getList().isEmpty());
            btn_delete_reservation.setDisable(c.getList().isEmpty());
        });
    }

    private void showReservations() {
        tc_reservation_id.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        tc_reservation_room_name.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        tc_reservation_check_in_date.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        tc_reservation_check_out_date.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        tc_reservation_is_checked_in.setCellValueFactory(new PropertyValueFactory<>("checkedIn"));
        tc_reservation_is_checked_out.setCellValueFactory(new PropertyValueFactory<>("checkedOut"));
        tc_reservation_customer_names.setCellValueFactory(new PropertyValueFactory<>("customerNames"));

        ObservableList<Reservation> observableReservations = DBUtils.getReservations();
        tv_reservation_table.setItems(observableReservations);
    }

    private void filterTable(LocalDate startDate, LocalDate endDate, String text) {
        showReservations();

        if (startDate != null) {
            FilteredList<Reservation> filteredList = new FilteredList<>(tv_reservation_table.getItems(), item -> {
                LocalDate checkInDate = new java.sql.Timestamp(item.getCheckInDate().getTime()).toLocalDateTime().toLocalDate();
                return checkInDate.isAfter(startDate.minusDays(1));
            });
            tv_reservation_table.setItems(filteredList);
        }
        if (endDate != null) {
            FilteredList<Reservation> filteredList = new FilteredList<>(tv_reservation_table.getItems(), item -> {
                LocalDate checkOutDate = new java.sql.Timestamp(item.getCheckOutDate().getTime()).toLocalDateTime().toLocalDate();
                return checkOutDate.isBefore(endDate.plusDays(1));
            });
            tv_reservation_table.setItems(filteredList);
        }
        if (!text.isEmpty()) {
            FilteredList<Reservation> filteredList = new FilteredList<>(tv_reservation_table.getItems(), item ->
                    item.getCustomerNames().toLowerCase().contains(text.toLowerCase()) ||
                            item.getRoomName().toLowerCase().contains(text.toLowerCase()));
            tv_reservation_table.setItems(filteredList);
        } else {
            // Show all items if dates are not selected
            tv_reservation_table.setItems(tv_reservation_table.getItems());
        }
    }

    private void handleButtonActions() {
        // Handle button actions here
        btn_delete_reservation.setOnAction(event -> handleDeleteReservation(event));
        btn_rooms.setOnAction(event -> DBUtils.changeScene(event, "rooms.fxml", "Rooms", null));
        btn_features.setOnAction(event -> DBUtils.changeScene(event, "features.fxml", "Features", null));
        btn_services.setOnAction(event -> DBUtils.changeScene(event, "services.fxml", "Services", null));
        btn_customers.setOnAction(event -> DBUtils.changeScene(event, "customers.fxml", "Customers", null));
        btn_new_reservation.setOnAction(event -> DBUtils.changeScene(event, "new_reservation.fxml", "New Reservation", null));
        btn_edit_reservation.setOnAction(event -> {
            Reservation selectedReservation = tv_reservation_table.getSelectionModel().getSelectedItem();
            DBUtils.changeScene(event, "edit_reservation.fxml", "Edit Reservation", selectedReservation);
        });
    }

    private void handleDeleteReservation(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to delete this reservation?");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                Reservation selectedReservation = tv_reservation_table.getSelectionModel().getSelectedItem();
                if (selectedReservation != null) {
                    DBUtils.deleteRow("reservations", "reservation_id", selectedReservation.getReservationId());
                    showReservations();
                }
            }
        });
    }
}
