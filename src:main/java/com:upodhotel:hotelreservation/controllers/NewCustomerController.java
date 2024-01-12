package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class NewCustomerController implements Initializable {
    @FXML
    private TextField tf_customer_name;
    @FXML
    private TextField tf_identity_number;
    @FXML
    private TextField tf_phone_number;
    @FXML
    private DatePicker dp_birth_date;
    @FXML
    private TextArea ta_customer_desc;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatFields();

        btn_save.setOnAction(event -> handleSaveButtonClick(event));

        btn_cancel.setOnAction(event -> handleCancelButtonClick(event));
    }

    private void formatFields() {
        int characterLimit = 11;
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() <= characterLimit) {
                return change;
            }
            return null;
        });
        tf_identity_number.setTextFormatter(textFormatter);

        tf_phone_number.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9]")) {
                event.consume(); // Accepts only digits
            }
        });

        tf_identity_number.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9]")) {
                event.consume(); // Accepts only digits
            }
        });
    }

    private void handleSaveButtonClick(ActionEvent event) {
        if (areRequiredFieldsEmpty() || !isIdentityNumberValid() || !isPhoneNumberValid()) {
            showValidationError("Please fill in all required fields.");
        } else {
            createNewCustomer(event);
        }
    }

    private void handleCancelButtonClick(ActionEvent event) {
        DBUtils.changeScene(event, "customers.fxml", "Customers", null);
    }

    private boolean areRequiredFieldsEmpty() {
        return tf_customer_name.getText().isEmpty() || tf_identity_number.getText().isEmpty() || tf_phone_number.getText().isEmpty() || dp_birth_date.getValue() == null;
    }

    private boolean isIdentityNumberValid() {
        return tf_identity_number.getText().length() == 11;
    }

    private boolean isPhoneNumberValid() {
        return tf_phone_number.getText().length() == 10;
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    private void createNewCustomer(ActionEvent event) {
        DBUtils.createCustomer(
                tf_customer_name.getText(),
                tf_identity_number.getText(),
                tf_phone_number.getText(),
                java.sql.Date.valueOf(dp_birth_date.getValue()),
                ta_customer_desc.getText());

        DBUtils.changeScene(event, "customers.fxml", "Customers", null);
    }
}
