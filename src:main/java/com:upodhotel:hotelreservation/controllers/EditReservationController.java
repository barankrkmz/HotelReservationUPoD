package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import com.upodotel.hotelreservation.entities.Customer;
import com.upodotel.hotelreservation.entities.Reservation;
import com.upodotel.hotelreservation.entities.Room;
import com.upodotel.hotelreservation.entities.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class EditReservationController implements Initializable {
    String selectedRoomName = "";
    @FXML
    private ChoiceBox<String> cb_select_room;
    @FXML
    private MenuButton mb_select_customers;
    @FXML
    private DatePicker dp_check_in_date;
    @FXML
    private DatePicker dp_check_out_date;
    @FXML
    private ChoiceBox<String> cb_checked_in;
    @FXML
    private ChoiceBox<String> cb_checked_out;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;
    @FXML
    private Pane pane_services;
    private int roomCapacity = -1;
    private GridPane gridPane;
    private Reservation reservation;
    private Map<String, Service> serviceMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        arrangeRoomNames();
        arrangeCustomerNames();
        arrangeChoiceBoxes();
        createServiceFields();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        cb_select_room.setOnAction(event -> handleRoomSelection());
        btn_save.setOnAction(event -> handleSave(event));
        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "reservations.fxml", "UPOD OTEL", null));
    }

    private void handleRoomSelection() {
        String selectedRoom = cb_select_room.getSelectionModel().getSelectedItem();
        roomCapacity = Integer.parseInt(String.valueOf(selectedRoom.charAt(selectedRoom.indexOf("customer") - 2)));
        selectedRoomName = selectedRoom.substring(0, selectedRoom.indexOf(" -"));
        mb_select_customers.getItems().forEach(checkMenuItem -> {
            ((CheckMenuItem) checkMenuItem).setSelected(false);
            checkMenuItem.setDisable(false);
        });
    }

    private void handleSave(javafx.event.ActionEvent event) {
        if (isInputValid()) {
            DBUtils.editReservation(
                    reservation.getReservationId(),
                    selectedRoomName,
                    java.sql.Date.valueOf(dp_check_in_date.getValue()),
                    java.sql.Date.valueOf(dp_check_out_date.getValue()),
                    cb_checked_in.getValue(),
                    cb_checked_out.getValue(),
                    getSelectedCustomerIdentityNumbers(),
                    getServiceQuantities());
            DBUtils.changeScene(event, "reservations.fxml", "UPOD OTEL", null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please ensure all fields are correctly filled.");
        }
    }

    private void arrangeRoomNames() {
        for (Room room : DBUtils.getRooms()) {
            cb_select_room.getItems().add(room.getName() + " - capacity: " + room.getCapacity() + " customer(s)");
        }
        cb_select_room.setValue("Select Room");
    }

    private void arrangeCustomerNames() {
        for (Customer customer : DBUtils.getCustomers()) {
            CheckMenuItem checkMenuItem = new CheckMenuItem(customer.getFullName() + " - " + customer.getIdentityNumber());
            mb_select_customers.getItems().add(checkMenuItem);
        }
    }

    private void arrangeChoiceBoxes() {
        cb_checked_in.getItems().addAll("YES", "NO");
        cb_checked_in.setValue("NO");
        cb_checked_out.getItems().addAll("YES", "NO");
        cb_checked_out.setValue("NO");
    }

    private boolean isInputValid() {
        return !cb_select_room.getValue().equals("Select Room") &&
               dp_check_in_date.getValue() != null &&
               dp_check_out_date.getValue() != null &&
               !dp_check_in_date.getValue().isAfter(dp_check_out_date.getValue()) &&
               !dp_check_in_date.getValue().isBefore(LocalDate.now()) &&
               getSelectedCustomerIdentityNumbers().size() == roomCapacity;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setData(Object reservationObj) {
    this.reservation = (Reservation) reservationObj;
    cb_select_room.setValue(reservation.getRoomName());
    dp_check_in_date.setValue(reservation.getCheckInDate().toLocalDate());
    dp_check_out_date.setValue(reservation.getCheckOutDate().toLocalDate());
    cb_checked_in.setValue(reservation.isCheckedIn());
    cb_checked_out.setValue(reservation.isCheckedOut());

    // Set selected customers
    for (MenuItem menuItem : mb_select_customers.getItems()) {
        CheckMenuItem checkMenuItem = (CheckMenuItem) menuItem;
        for (Customer customer : reservation.getCustomers()) {
            if (checkMenuItem.getText().equals(customer.getFullName() + " - " + customer.getIdentityNumber())) {
                checkMenuItem.setSelected(true);
            }
        }
    }

    // Set selected services and quantities
    for (Map.Entry<Service, Integer> entry : reservation.getServices().entrySet()) {
        Service service = entry.getKey();
        int quantity = entry.getValue();
        String serviceName = service.getServiceName();

        for (int i = 0; i < gridPane.getChildren().size(); i += 4) {
            if (gridPane.getChildren().get(i).getId().startsWith("cb_service_") && ((CheckBox) gridPane.getChildren().get(i)).getText().equals(serviceName)) {
                ((CheckBox) gridPane.getChildren().get(i)).setSelected(true);
                Label quantityLabel = (Label) gridPane.getChildren().get(i + 1);
                quantityLabel.setText(String.valueOf(quantity));
            }
        }
    }
}

private List<String> getSelectedCustomerIdentityNumbers() {
    List<String> selectedCustomers = new ArrayList<>();
    for (MenuItem menuItem : mb_select_customers.getItems()) {
        CheckMenuItem checkMenuItem = (CheckMenuItem) menuItem;
        if (checkMenuItem.isSelected()) {
            String identityNumber = checkMenuItem.getText().split(" - ")[1];
            selectedCustomers.add(identityNumber);
        }
    }
    return selectedCustomers;
}

private void createServiceFields() {
    gridPane = new GridPane();
    int row = 0;
    for (Service service : DBUtils.getServices()) {
        CheckBox checkbox = new CheckBox(service.getServiceName());
        checkbox.setTextFill(Color.WHITE);
        checkbox.setStyle("-fx-padding: 10 10 10 10;");
        checkbox.setFont(Font.font("Rockwell", 14));
        checkbox.setId("cb_service_" + row);
        gridPane.add(checkbox, 0, row);

        Label quantity = new Label("0");
        quantity.setTextFill(Color.WHITE);
        quantity.setStyle("-fx-padding: 10 10 10 10;");
        quantity.setFont(Font.font("Rockwell", 14));
        quantity.setAlignment(Pos.CENTER);
        quantity.setId("lbl_quantity_" + row);
        gridPane.add(quantity, 2, row);

        Button btn_minus = new Button("-");
        btn_minus.setStyle("-fx-text-fill: #4f0184; -fx-padding: 10 10 10 10;");
        btn_minus.setFont(Font.font("Rockwell", 14));
        btn_minus.setOnAction(event -> {
            int currentQuantity = Integer.parseInt(quantity.getText());
            if (currentQuantity > 0) {
                currentQuantity--;
                quantity.setText(String.valueOf(currentQuantity));
            }
        });
        gridPane.add(btn_minus, 1, row);

        Button btn_plus = new Button("+");
        btn_plus.setStyle("-fx-text-fill: #4f0184; -fx-padding: 10 10 10 10;");
        btn_plus.setFont(Font.font("Rockwell", 14));
        btn_plus.setOnAction(event -> {
            int currentQuantity = Integer.parseInt(quantity.getText());
            currentQuantity++;
            quantity.setText(String.valueOf(currentQuantity));
        });
        gridPane.add(btn_plus, 3, row);

        row++;
    }
    pane_services.getChildren().add(gridPane);
}

private Map<Service, Integer> getServiceQuantities() {
    Map<Service, Integer> selectedServices = new HashMap<>();
    int i = 0;
    for (Service service : DBUtils.getServices()) {
        CheckBox checkBox = (CheckBox) gridPane.lookup("#cb_service_" + i);
        int quantity = Integer.parseInt(((Label) gridPane.lookup("#lbl_quantity_" + i)).getText());
        if (checkBox.isSelected() && quantity > 0) {
            selectedServices.put(service, quantity);
        }
        i++;
    }
    return selectedServices;
}

}
