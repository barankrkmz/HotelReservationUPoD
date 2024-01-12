package com.upodotel.hotelreservation.controllers;

import com.upodotel.hotelreservation.DBUtils;
import com.upodotel.hotelreservation.entities.Feature;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class FeaturesController implements Initializable {
    @FXML
    private TableView<Feature> tv_feature_table;
    @FXML
    private TableColumn<Feature, String> tc_feature_name;
    @FXML
    private Button btn_new_feature;
    @FXML
    private Button btn_edit_feature;
    @FXML
    private Button btn_delete_feature;
    @FXML
    private Button btn_return_reservations;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showFeatures();

        // Disable edit and delete buttons if no row is selected
        tv_feature_table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Feature>) c -> {
            btn_edit_feature.setDisable(c.getList().isEmpty());
            btn_delete_feature.setDisable(c.getList().isEmpty());
        });

        btn_edit_feature.setOnAction(event -> handleEditFeatureClick());

        btn_delete_feature.setOnAction(event -> handleDeleteFeatureClick());

        btn_new_feature.setOnAction(event -> DBUtils.changeScene(event, "new_feature.fxml", "Create New Feature", null));

        btn_return_reservations.setOnAction(event -> DBUtils.changeScene(event, "reservations.fxml", "UPOD OTEL", null));
    }

    private void showFeatures() {
        tc_feature_name.setCellValueFactory(new PropertyValueFactory<>("featureName"));
        ObservableList<Feature> observableFeatures = DBUtils.getFeatures();
        tv_feature_table.setItems(observableFeatures);
    }

    private void handleEditFeatureClick() {
        Feature selectedFeature = tv_feature_table.getSelectionModel().getSelectedItem();
        if (selectedFeature != null) {
            DBUtils.changeScene(event, "edit_feature.fxml", "Edit Feature", selectedFeature);
        }
    }

    private void handleDeleteFeatureClick() {
        Feature selectedFeature = tv_feature_table.getSelectionModel().getSelectedItem();
        if (selectedFeature != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Do you want to delete this feature?");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    DBUtils.deleteRow("features", "feature_id", selectedFeature.getFeatureId());
                    showFeatures();
                }
            });
        }
    }
}
