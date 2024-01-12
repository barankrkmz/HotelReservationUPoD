package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import com.upodotel.hotelreservation.entities.Feature;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditFeatureController implements Initializable {
    @FXML
    private TextField tf_feature_name;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;
    private Feature feature;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        btn_save.setOnAction(event -> handleSave(event));
        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "features.fxml", "Features", null));
    }

    private void handleSave(javafx.event.ActionEvent event) {
        if (isInputValid()) {
            DBUtils.editFeature(
                    feature.getFeatureId(),
                    tf_feature_name.getText());

            DBUtils.changeScene(event, "features.fxml", "Features", null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
        }
    }

    private boolean isInputValid() {
        return !tf_feature_name.getText().isEmpty();
    }

    public void setData(Object featureObj) {
        this.feature = (Feature) featureObj;
        tf_feature_name.setText(feature.getFeatureName());
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
