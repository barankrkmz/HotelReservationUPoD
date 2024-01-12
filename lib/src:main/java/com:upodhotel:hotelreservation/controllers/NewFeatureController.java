package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class NewFeatureController implements Initializable {

    @FXML
    private TextField tf_feature_name;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_save.setOnAction(event -> handleSaveButtonClick(event));

        btn_cancel.setOnAction(event -> handleCancelButtonClick(event));
    }

    private void handleSaveButtonClick(ActionEvent event) {
        if (tf_feature_name.getText().isEmpty()) {
            showValidationError("Please fill in all required fields.");
        } else {
            createNewFeature(event);
        }
    }

    private void handleCancelButtonClick(ActionEvent event) {
        DBUtils.changeScene(event, "features.fxml", "Features", null);
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    private void createNewFeature(ActionEvent event) {
        DBUtils.createFeature(tf_feature_name.getText());

        DBUtils.changeScene(event, "features.fxml", "Features", null);
    }
}
