package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import com.upodotel.hotelreservation.entities.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class EditCustomerController implements Initializable {
    @FXML
    public TextField tf_customer_name;
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
    private Customer customer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatFields();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        btn_save.setOnAction(event -> handleSave());
        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "customers.fxml", "Customers", null));
    }

    private void handleSave() {
        if (areInputsValid()) {
            DBUtils.editCustomer(
                    customer.getId(),
                    tf_customer_name.getText(),
                    tf_identity_number.getText(),
                    tf_phone_number.getText(),
                    java.sql.Date.valueOf(dp_birth_date.getValue()),
                    ta_customer_desc.getText());

            DBUtils.changeScene(event, "customers.fxml", "Customers", null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields correctly.");
        }
    }

    private boolean areInputsValid() {
        return !tf_customer_name.getText().isEmpty() &&
               !tf_identity_number.getText().isEmpty() &&
               tf_identity_number.getText().length() == 11 &&
               !tf_phone_number.getText().isEmpty() &&
               tf_phone_number.getText().length() == 10 &&
               dp_birth_date.getValue() != null;
    }

    private void formatFields() {
        tf_identity_number.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d{0,11}") ? change : null)));

        tf_phone_number.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d{0,10}") ? change : null)));
    }

    public void setData(Object customerObj) {
        this.customer = (Customer) customerObj;
        tf_customer_name.setText(customer.getFullName());
        tf_identity_number.setText(customer.getIdentityNumber());
        tf_phone_number.setText(customer.getPhoneNumber());
        dp_birth_date.setValue(new java.sql.Date(customer.getBirthDate().getTime()).toLocalDate());
        ta_customer_desc.setText(customer.getDescription());
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
