package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class NewServiceController implements Initializable {
    @FXML
    private TextField tf_service_name;
    @FXML
    private TextField tf_unit_price;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatFields();
        btn_save.setOnAction(event -> handleSaveButtonClick(event));
        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "services.fxml", "Services", null));
    }

    private void formatFields() {
        tf_unit_price.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9.]")) {
                event.consume(); // Accepts only digits and points
            }
        });
    }

    private void handleSaveButtonClick(ActionEvent event) {
        if (tf_service_name.getText().isEmpty() || tf_unit_price.getText().isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
        } else {
            DBUtils.createService(tf_service_name.getText(), Float.parseFloat(tf_unit_price.getText()));
            DBUtils.changeScene(event, "services.fxml", "Services", null);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
