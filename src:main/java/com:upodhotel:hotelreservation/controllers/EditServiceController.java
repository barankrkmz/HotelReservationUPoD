package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import com.upodotel.hotelreservation.entities.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class EditServiceController implements Initializable {
    @FXML
    private TextField tf_service_name;
    @FXML
    private TextField tf_unit_price;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;
    private Service service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatFields();
        btn_save.setOnAction(event -> handleSaveButtonClick(event));

        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "services.fxml", "Services", null));
    }

    private void formatFields() {
        tf_unit_price.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9.]")) {
                event.consume(); // Accepts only digits
            }
        });
    }

    private void handleSaveButtonClick(javafx.event.ActionEvent event) {
        if (areRequiredFieldsEmpty()) {
            showAlert("Please fill in all required fields.");
        } else {
            DBUtils.editService(
                    service.getServiceId(),
                    tf_service_name.getText(),
                    Float.parseFloat(tf_unit_price.getText()));

            DBUtils.changeScene(event, "services.fxml", "Services", null);
        }
    }

    private boolean areRequiredFieldsEmpty() {
        return tf_service_name.getText().isEmpty() || tf_unit_price.getText().isEmpty();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    public void setData(Object serviceObj) {
        this.service = (Service) serviceObj;
        tf_service_name.setText(service.getServiceName());
        tf_unit_price.setText(String.valueOf(service.getUnitPrice()));
    }
}
