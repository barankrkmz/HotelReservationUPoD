package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import com.upodotel.hotelreservation.entities.Feature;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NewRoomController implements Initializable {
    @FXML
    private TextField tf_room_name;
    @FXML
    private ChoiceBox<Integer> cb_room_capacity;
    @FXML
    private MenuButton mb_room_features;
    @FXML
    private TextField tf_room_price;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatFields();
        arrangeRoomFeatures();
        cb_room_capacity.getItems().addAll(1, 2, 3, 4);
        btn_save.setOnAction(event -> handleSaveButtonClick(event));
        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "rooms.fxml", "Rooms", null));
    }

    private void arrangeRoomFeatures() {
        for (Feature feature : DBUtils.getFeatures()) {
            CheckMenuItem checkMenuItem = new CheckMenuItem(feature.getFeatureName());
            mb_room_features.getItems().add(checkMenuItem);
        }
    }

    private List<String> getSelectedFeatures() {
        List<String> featureList = new ArrayList<>();
        for (MenuItem menuItem : mb_room_features.getItems()) {
            CheckMenuItem checkMenuItem = (CheckMenuItem) menuItem;
            if (checkMenuItem.isSelected()) {
                featureList.add(checkMenuItem.getText());
            }
        }
        return featureList;
    }

    private void formatFields() {
        tf_room_price.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9.]")) {
                event.consume(); // Accepts only digits and point
            }
        });
    }

    private void handleSaveButtonClick(ActionEvent event) {
        if (tf_room_name.getText().isEmpty() || cb_room_capacity.getValue() == null || tf_room_price.getText().isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
        } else {
            List<String> featureList = getSelectedFeatures();
            DBUtils.createRoom(tf_room_name.getText(), cb_room_capacity.getValue(), Float.parseFloat(tf_room_price.getText()), featureList);
            DBUtils.changeScene(event, "rooms.fxml", "Rooms", null);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
