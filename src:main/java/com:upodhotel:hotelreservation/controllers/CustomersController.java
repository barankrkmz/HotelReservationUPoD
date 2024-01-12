package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import com.upodotel.hotelreservation.entities.Customer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {
    @FXML
    private TableView<Customer> tv_customer_table;
    @FXML
    private TableColumn<Customer, String> tc_full_name;
    @FXML
    private TableColumn<Customer, String> tc_identity_number;
    @FXML
    private TableColumn<Customer, String> tc_phone_number;
    @FXML
    private TableColumn<Customer, Date> tc_birth_date;
    @FXML
    private TableColumn<Customer, String> tc_description;
    @FXML
    private Button btn_new_customer;
    @FXML
    private Button btn_edit_customer;
    @FXML
    private Button btn_delete_customer;
    @FXML
    private Button btn_return_reservations;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        showCustomers();
        setupListeners();
    }

    private void setupTableColumns() {
        tc_full_name.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tc_identity_number.setCellValueFactory(new PropertyValueFactory<>("identityNumber"));
        tc_phone_number.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        tc_birth_date.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        tc_description.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void showCustomers() {
        ObservableList<Customer> observableCustomers = DBUtils.getCustomers();
        tv_customer_table.setItems(observableCustomers);
    }

    private void setupListeners() {
        tv_customer_table.getSelectionModel().getSelectedItems().addListener((change) -> {
            boolean isSelectionEmpty = change.getList().isEmpty();
            btn_edit_customer.setDisable(isSelectionEmpty);
            btn_delete_customer.setDisable(isSelectionEmpty);
        });

        btn_delete_customer.setOnAction(event -> deleteSelectedCustomer());
        btn_edit_customer.setOnAction(event -> editSelectedCustomer(event));
        btn_new_customer.setOnAction(event -> DBUtils.changeScene(event, "new_customer.fxml", "Create New Customer", null));
        btn_return_reservations.setOnAction(event -> DBUtils.changeScene(event, "reservations.fxml", "UPOD HOTEL!", null));
    }

    private void deleteSelectedCustomer() {
        Customer selectedCustomer = tv_customer_table.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this customer?", ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure to delete this customer?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    DBUtils.deleteRow("customers", "id", selectedCustomer.getId());
                    showCustomers();
                }
            });
        }
    }

    private void editSelectedCustomer(javafx.event.ActionEvent event) {
        Customer selectedCustomer = tv_customer_table.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            DBUtils.changeScene(event, "edit_customer.fxml", "Edit Customer", selectedCustomer);
        }
    }
}
