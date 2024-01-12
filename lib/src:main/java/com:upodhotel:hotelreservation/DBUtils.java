package com.upodotel.hotelreservation;

import com.upodotel.hotelreservation.controllers.*;
import com.upodotel.hotelreservation.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, Object editableObj) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            Parent root = loader.load();

            if (editableObj != null) {
                switch (fxmlFile) {
                    case "edit_customer.fxml":
                        ((EditCustomerController) loader.getController()).setData(editableObj);
                        break;
                    case "edit_room.fxml":
                        ((EditRoomController) loader.getController()).setData(editableObj);
                        break;
                    case "edit_feature.fxml":
                        ((EditFeatureController) loader.getController()).setData(editableObj);
                        break;
                    case "edit_service.fxml":
                        ((EditServiceController) loader.getController()).setData(editableObj);
                        break;
                    case "edit_reservation.fxml":
                        ((EditReservationController) loader.getController()).setData(editableObj);
                        break;
                }
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 800, 500));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createCustomer(String fullName, String identityNumber, String phoneNumber, Date birthDate, String customerDesc) {
        String checkQuery = "SELECT * FROM customers WHERE identity_number = ?";
        String insertQuery = "INSERT INTO customers (full_name, identity_number, phone_number, birth_date, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
             PreparedStatement psCheckCustomerExist = connection.prepareStatement(checkQuery);
             PreparedStatement psInsertCustomer = connection.prepareStatement(insertQuery)) {

            psCheckCustomerExist.setString(1, identityNumber);
            try (ResultSet resultSet = psCheckCustomerExist.executeQuery()) {
                if (resultSet.isBeforeFirst()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Customer already exists!");
                } else {
                    psInsertCustomer.setString(1, fullName);
                    psInsertCustomer.setString(2, identityNumber);
                    psInsertCustomer.setString(3, phoneNumber);
                    psInsertCustomer.setDate(4, birthDate);
                    psInsertCustomer.setString(5, customerDesc);
                    psInsertCustomer.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Customer created successfully");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createRoom(String roomName, int roomCapacity, float roomPrice, List<String> roomFeatures) {
    String checkRoomQuery = "SELECT * FROM rooms WHERE room_name = ?";
    String insertRoomQuery = "INSERT INTO rooms (room_name, room_capacity, room_price) VALUES (?, ?, ?)";
    String selectRoomIdQuery = "SELECT room_id FROM rooms WHERE room_name = ?";
    String selectFeatureIdQuery = "SELECT feature_id FROM features WHERE feature_name = ?";
    String insertRoomFeatureQuery = "INSERT INTO room_features (room_id, feature_id) VALUES (?, ?)";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psCheckRoomExist = connection.prepareStatement(checkRoomQuery);
         PreparedStatement psInsertRoom = connection.prepareStatement(insertRoomQuery, Statement.RETURN_GENERATED_KEYS)) {

        psCheckRoomExist.setString(1, roomName);
        try (ResultSet resultSet = psCheckRoomExist.executeQuery()) {
            if (resultSet.isBeforeFirst()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Room already exists!");
                return;
            }
        }

        psInsertRoom.setString(1, roomName);
        psInsertRoom.setInt(2, roomCapacity);
        psInsertRoom.setFloat(3, roomPrice);
        psInsertRoom.executeUpdate();

        try (ResultSet generatedKeys = psInsertRoom.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int roomId = generatedKeys.getInt(1);

                for (String featureName : roomFeatures) {
                    try (PreparedStatement psSelectFeatureId = connection.prepareStatement(selectFeatureIdQuery);
                         PreparedStatement psInsertRoomFeature = connection.prepareStatement(insertRoomFeatureQuery)) {

                        psSelectFeatureId.setString(1, featureName);
                        ResultSet featureSet = psSelectFeatureId.executeQuery();

                        if (featureSet.next()) {
                            int featureId = featureSet.getInt("feature_id");
                            psInsertRoomFeature.setInt(1, roomId);
                            psInsertRoomFeature.setInt(2, featureId);
                            psInsertRoomFeature.executeUpdate();
                        }
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room created successfully");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void createFeature(String featureName) {
    String checkFeatureQuery = "SELECT * FROM features WHERE feature_name = ?";
    String insertFeatureQuery = "INSERT INTO features(feature_name) VALUES (?)";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psCheckFeatureExist = connection.prepareStatement(checkFeatureQuery);
         PreparedStatement psInsertFeature = connection.prepareStatement(insertFeatureQuery)) {

        psCheckFeatureExist.setString(1, featureName);
        try (ResultSet resultSet = psCheckFeatureExist.executeQuery()) {
            if (resultSet.isBeforeFirst()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Feature already exists!");
            } else {
                psInsertFeature.setString(1, featureName);
                psInsertFeature.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Feature created successfully");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void createService(String serviceName, float servicePrice) {
    String checkServiceQuery = "SELECT * FROM services WHERE service_name = ?";
    String insertServiceQuery = "INSERT INTO services(service_name, unit_price) VALUES (?, ?)";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psCheckServiceExist = connection.prepareStatement(checkServiceQuery);
         PreparedStatement psInsertService = connection.prepareStatement(insertServiceQuery)) {

        psCheckServiceExist.setString(1, serviceName);
        try (ResultSet resultSet = psCheckServiceExist.executeQuery()) {
            if (resultSet.isBeforeFirst()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Service already exists!");
            } else {
                psInsertService.setString(1, serviceName);
                psInsertService.setFloat(2, servicePrice);
                psInsertService.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Service created successfully");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void createReservation(String selectedRoomName, Date checkInDate, Date checkOutDate,
                                     String checkedIn, String checkedOut, List<String> selectedCustomerIdentityNumbers,
                                     Map<Service, Integer> selectedServices) {
    String selectRoomIdQuery = "SELECT room_id FROM rooms WHERE room_name = ?";
    String checkReservationQuery = "SELECT * FROM reservations WHERE room_id = ?";
    String insertReservationQuery = "INSERT INTO reservations (room_id, check_in_date, check_out_date, checked_in_date, checked_out_date) VALUES (?, ?, ?, ?, ?)";
    String selectCustomerIdQuery = "SELECT id FROM customers WHERE identity_number = ?";
    String insertReservationCustomerQuery = "INSERT INTO reservation_customers (reservation_id, customer_id) VALUES (?, ?)";
    String selectServiceIdQuery = "SELECT service_id FROM services WHERE service_name = ?";
    String insertReservationServiceQuery = "INSERT INTO reservation_services (reservation_id, service_id, quantity) VALUES (?, ?, ?)";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS)) {
        int roomId = getRoomId(connection, selectRoomIdQuery, selectedRoomName);

        if (isRoomReserved(connection, checkReservationQuery, roomId)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Room already reserved!");
            return;
        }

        int reservationId = insertReservation(connection, insertReservationQuery, roomId, checkInDate, checkOutDate, checkedIn, checkedOut);
        insertReservationCustomers(connection, selectCustomerIdQuery, insertReservationCustomerQuery, reservationId, selectedCustomerIdentityNumbers);
        insertReservationServices(connection, selectServiceIdQuery, insertReservationServiceQuery, reservationId, selectedServices);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation created successfully");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private static int getRoomId(Connection connection, String query, String roomName) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setString(1, roomName);
        try (ResultSet resultSet = ps.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("room_id");
            }
        }
    }
    return -1; // or throw an exception
}

private static boolean isRoomReserved(Connection connection, String query, int roomId) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, roomId);
        try (ResultSet resultSet = ps.executeQuery()) {
            return resultSet.isBeforeFirst();
        }
    }
}

private static int insertReservation(Connection connection, String query, int roomId, Date checkInDate, Date checkOutDate, String checkedIn, String checkedOut) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, roomId);
        ps.setDate(2, checkInDate);
        ps.setDate(3, checkOutDate);
        ps.setDate(4, checkedIn.equals("YES") ? java.sql.Date.valueOf(LocalDate.now()) : null);
        ps.setDate(5, checkedOut.equals("YES") ? java.sql.Date.valueOf(LocalDate.now()) : null);
        ps.executeUpdate();

        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
    }
    return -1; // or throw an exception
}

private static void insertReservationCustomers(Connection connection, String selectQuery, String insertQuery, int reservationId, List<String> customerIds) throws SQLException {
    for (String customerId : customerIds) {
        try (PreparedStatement psSelect = connection.prepareStatement(selectQuery);
             PreparedStatement psInsert = connection.prepareStatement(insertQuery)) {

            psSelect.setString(1, customerId);
            try (ResultSet resultSet = psSelect.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    psInsert.setInt(1, reservationId);
                    psInsert.setInt(2, id);
                    psInsert.executeUpdate();
                }
            }
        }
    }
}

private static void insertReservationServices(Connection connection, String selectQuery, String insertQuery, int reservationId, Map<Service, Integer> services) throws SQLException {
    for (Map.Entry<Service, Integer> entry : services.entrySet()) {
        try (PreparedStatement psSelect = connection.prepareStatement(selectQuery);
             PreparedStatement psInsert = connection.prepareStatement(insertQuery)) {

            psSelect.setString(1, entry.getKey().getServiceName());
            try (ResultSet resultSet = psSelect.executeQuery()) {
                if (resultSet.next()) {
                    int serviceId = resultSet.getInt("service_id");
                    psInsert.setInt(1, reservationId);
                    psInsert.setInt(2, serviceId);
                    psInsert.setInt(3, entry.getValue());
                    psInsert.executeUpdate();
                }
            }
        }
    }
}

    public static ObservableList<Customer> getCustomers() {
    String query = "SELECT * FROM customers";
    ObservableList<Customer> observableCustomers = FXCollections.observableArrayList();

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psSelect = connection.prepareStatement(query);
         ResultSet resultSet = psSelect.executeQuery()) {

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String fullName = resultSet.getString("full_name");
            String identityNumber = resultSet.getString("identity_number");
            String phoneNumber = resultSet.getString("phone_number");
            Date birthDate = resultSet.getDate("birth_date");
            String description = resultSet.getString("description");

            observableCustomers.add(new Customer(id, fullName, identityNumber, phoneNumber, birthDate, description));
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Customer cannot be retrieved!");
        e.printStackTrace();
    }
    return observableCustomers;
}

    public static ObservableList<Feature> getFeatures() {
    String query = "SELECT * FROM features";
    ObservableList<Feature> observableFeatures = FXCollections.observableArrayList();

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psSelect = connection.prepareStatement(query);
         ResultSet resultSet = psSelect.executeQuery()) {

        while (resultSet.next()) {
            int id = resultSet.getInt("feature_id");
            String featureName = resultSet.getString("feature_name");
            observableFeatures.add(new Feature(id, featureName));
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Feature cannot be retrieved!");
        e.printStackTrace();
    }
    return observableFeatures;
}

    public static ObservableList<Service> getServices() {
    String query = "SELECT * FROM services";
    ObservableList<Service> observableServices = FXCollections.observableArrayList();

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psSelect = connection.prepareStatement(query);
         ResultSet resultSet = psSelect.executeQuery()) {

        while (resultSet.next()) {
            int id = resultSet.getInt("service_id");
            String serviceName = resultSet.getString("service_name");
            float unitPrice = resultSet.getFloat("unit_price");
            observableServices.add(new Service(id, serviceName, unitPrice));
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Service cannot be retrieved!");
        e.printStackTrace();
    }
    return observableServices;
}

    public static ObservableList<Room> getRooms() {
    String selectRoomsQuery = "SELECT * FROM rooms";
    String selectFeaturesQuery = "SELECT * FROM features INNER JOIN room_features ON features.feature_id = room_features.feature_id WHERE room_id = ?";
    ObservableList<Room> observableRooms = FXCollections.observableArrayList();

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psSelectRooms = connection.prepareStatement(selectRoomsQuery);
         ResultSet resultSet = psSelectRooms.executeQuery()) {

        while (resultSet.next()) {
            int id = resultSet.getInt("room_id");
            String name = resultSet.getString("room_name");
            int capacity = resultSet.getInt("room_capacity");
            float price = resultSet.getFloat("room_price");
            List<Feature> features = getRoomFeatures(connection, selectFeaturesQuery, id);
            observableRooms.add(new Room(id, name, capacity, price, features));
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Room cannot be retrieved!");
        e.printStackTrace();
    }
    return observableRooms;
}

private static List<Feature> getRoomFeatures(Connection connection, String query, int roomId) throws SQLException {
    List<Feature> features = new ArrayList<>();
    try (PreparedStatement psSelectFeatures = connection.prepareStatement(query)) {
        psSelectFeatures.setInt(1, roomId);
        try (ResultSet resultSetFeatures = psSelectFeatures.executeQuery()) {
            while (resultSetFeatures.next()) {
                features.add(new Feature(resultSetFeatures.getInt("feature_id"), resultSetFeatures.getString("feature_name")));
            }
        }
    }
    return features;
}

    public static ObservableList<Reservation> getReservations() {
    String selectReservationsQuery = "SELECT * FROM reservations";
    ObservableList<Reservation> observableReservations = FXCollections.observableArrayList();

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psSelectReservations = connection.prepareStatement(selectReservationsQuery);
         ResultSet resultSet = psSelectReservations.executeQuery()) {

        while (resultSet.next()) {
            int reservationId = resultSet.getInt("reservation_id");
            int roomId = resultSet.getInt("room_id");
            Date checkInDate = resultSet.getDate("check_in_date");
            Date checkOutDate = resultSet.getDate("check_out_date");
            Date checkedInDate = resultSet.getDate("checked_in_date");
            Date checkedOutDate = resultSet.getDate("checked_out_date");

            String roomName = getRoomName(connection, roomId);
            List<Customer> customers = getReservationCustomers(connection, reservationId);
            Map<Service, Integer> services = getReservationServices(connection, reservationId);

            observableReservations.add(new Reservation(reservationId, roomId, roomName, customers, checkInDate, checkOutDate, checkedInDate != null, checkedOutDate != null, checkedInDate, checkedOutDate, services));
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Reservation cannot be retrieved!");
        e.printStackTrace();
    }
    return observableReservations;
}

private static String getRoomName(Connection connection, int roomId) throws SQLException {
    String query = "SELECT room_name FROM rooms WHERE room_id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, roomId);
        try (ResultSet resultSet = ps.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString("room_name");
            }
        }
    }
    return ""; // or handle this case appropriately
}

private static List<Customer> getReservationCustomers(Connection connection, int reservationId) throws SQLException {
    List<Customer> customers = new ArrayList<>();
    String query = "SELECT * FROM customers INNER JOIN reservation_customers ON customers.id = reservation_customers.customer_id WHERE reservation_id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, reservationId);
        try (ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                customers.add(new Customer(
                    resultSet.getInt("id"),
                    resultSet.getString("full_name"),
                    resultSet.getString("identity_number"),
                    resultSet.getString("phone_number"),
                    resultSet.getDate("birth_date"),
                    resultSet.getString("description")));
            }
        }
    }
    return customers;
}

private static Map<Service, Integer> getReservationServices(Connection connection, int reservationId) throws SQLException {
    Map<Service, Integer> services = new HashMap<>();
    String query = "SELECT * FROM services INNER JOIN reservation_services ON services.service_id = reservation_services.service_id WHERE reservation_id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, reservationId);
        try (ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                services.put(
                    new Service(
                        resultSet.getInt("service_id"),
                        resultSet.getString("service_name"),
                        resultSet.getFloat("unit_price")),
                    resultSet.getInt("quantity"));
            }
        }
    }
    return services;
}

    public static void deleteRow(String tableName, String attributeName, int attributeId) {
    String deleteQuery = "DELETE FROM " + tableName + " WHERE " + attributeName + " = ?";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psDelete = connection.prepareStatement(deleteQuery)) {

        psDelete.setInt(1, attributeId);
        int affectedRows = psDelete.executeUpdate();

        if (affectedRows > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Deletion from " + tableName + " is successful!");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", "No rows affected. Nothing to delete.");
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Row cannot be deleted!");
        e.printStackTrace();
    }
}

private static void showAlert(Alert.AlertType alertType, String title, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.show();
}

    public static void editCustomer(int id, String fullName, String identityNumber, String phoneNumber, Date birthDate, String customerDesc) {
    String checkCustomerExistQuery = "SELECT * FROM customers WHERE identity_number = ? AND id != ?";
    String updateCustomerQuery = "UPDATE customers SET full_name = ?, identity_number = ?, phone_number = ?, birth_date = ?, description = ? WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psCheckCustomerExist = connection.prepareStatement(checkCustomerExistQuery)) {

        psCheckCustomerExist.setString(1, identityNumber);
        psCheckCustomerExist.setInt(2, id);
        try (ResultSet resultSet = psCheckCustomerExist.executeQuery()) {
            if (resultSet.isBeforeFirst()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Customer already exists!");
                return;
            }
        }

        try (PreparedStatement psUpdateCustomer = connection.prepareStatement(updateCustomerQuery)) {
            psUpdateCustomer.setString(1, fullName);
            psUpdateCustomer.setString(2, identityNumber);
            psUpdateCustomer.setString(3, phoneNumber);
            psUpdateCustomer.setDate(4, birthDate);
            psUpdateCustomer.setString(5, customerDesc);
            psUpdateCustomer.setInt(6, id);
            psUpdateCustomer.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer edited successfully");
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Unable to edit customer!");
        e.printStackTrace();
    }
}


   public static void editFeature(int feature_id, String featureName) {
    String checkFeatureExistQuery = "SELECT * FROM features WHERE feature_name = ? AND feature_id != ?";
    String updateFeatureQuery = "UPDATE features SET feature_name = ? WHERE feature_id = ?";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psCheckFeatureExist = connection.prepareStatement(checkFeatureExistQuery)) {

        psCheckFeatureExist.setString(1, featureName);
        psCheckFeatureExist.setInt(2, feature_id);
        try (ResultSet resultSet = psCheckFeatureExist.executeQuery()) {
            if (resultSet.isBeforeFirst()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Feature already exists!");
                return;
            }
        }

        try (PreparedStatement psUpdateFeature = connection.prepareStatement(updateFeatureQuery)) {
            psUpdateFeature.setString(1, featureName);
            psUpdateFeature.setInt(2, feature_id);
            psUpdateFeature.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Feature edited successfully");
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Unable to edit feature!");
        e.printStackTrace();
    }
}

    public static void editService(int service_id, String serviceName, float unitPrice) {
    String checkServiceExistQuery = "SELECT * FROM services WHERE service_name = ? AND service_id != ?";
    String updateServiceQuery = "UPDATE services SET service_name = ?, unit_price = ? WHERE service_id = ?";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psCheckServiceExist = connection.prepareStatement(checkServiceExistQuery)) {

        psCheckServiceExist.setString(1, serviceName);
        psCheckServiceExist.setInt(2, service_id);
        try (ResultSet resultSet = psCheckServiceExist.executeQuery()) {
            if (resultSet.isBeforeFirst()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Service already exists!");
                return;
            }
        }

        try (PreparedStatement psUpdateService = connection.prepareStatement(updateServiceQuery)) {
            psUpdateService.setString(1, serviceName);
            psUpdateService.setFloat(2, unitPrice);
            psUpdateService.setInt(3, service_id);
            psUpdateService.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Service edited successfully");
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Unable to edit service!");
        e.printStackTrace();
    }
}

    public static void editRoom(int roomId, String roomName, int roomCapacity, float roomPrice, List<String> roomFeatures) {
    String checkRoomExistQuery = "SELECT * FROM rooms WHERE room_name = ? AND room_id != ?";
    String updateRoomQuery = "UPDATE rooms SET room_name = ?, room_capacity = ?, room_price = ? WHERE room_id = ?";
    String deleteRoomFeaturesQuery = "DELETE FROM room_features WHERE room_id = ?";
    String selectFeatureIdQuery = "SELECT feature_id FROM features WHERE feature_name = ?";
    String insertRoomFeatureQuery = "INSERT INTO room_features (room_id, feature_id) VALUES (?, ?)";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
         PreparedStatement psCheckRoomExist = connection.prepareStatement(checkRoomExistQuery)) {

        psCheckRoomExist.setString(1, roomName);
        psCheckRoomExist.setInt(2, roomId);
        try (ResultSet resultSet = psCheckRoomExist.executeQuery()) {
            if (resultSet.isBeforeFirst()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Room already exists!");
                return;
            }
        }

        try (PreparedStatement psUpdateRoom = connection.prepareStatement(updateRoomQuery);
             PreparedStatement psDeleteRoomFeatures = connection.prepareStatement(deleteRoomFeaturesQuery)) {

            psUpdateRoom.setString(1, roomName);
            psUpdateRoom.setInt(2, roomCapacity);
            psUpdateRoom.setFloat(3, roomPrice);
            psUpdateRoom.setInt(4, roomId);
            psUpdateRoom.executeUpdate();

            psDeleteRoomFeatures.setInt(1, roomId);
            psDeleteRoomFeatures.executeUpdate();

            for (String featureName : roomFeatures) {
                updateRoomFeatures(connection, selectFeatureIdQuery, insertRoomFeatureQuery, roomId, featureName);
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Room edited successfully");
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Unable to edit room!");
        e.printStackTrace();
    }
}

private static void updateRoomFeatures(Connection connection, String selectQuery, String insertQuery, int roomId, String featureName) throws SQLException {
    try (PreparedStatement psSelectFeatureId = connection.prepareStatement(selectQuery);
         PreparedStatement psInsertRoomFeature = connection.prepareStatement(insertQuery)) {

        psSelectFeatureId.setString(1, featureName);
        try (ResultSet resultSet = psSelectFeatureId.executeQuery()) {
            if (resultSet.next()) {
                int featureId = resultSet.getInt("feature_id");

                psInsertRoomFeature.setInt(1, roomId);
                psInsertRoomFeature.setInt(2, featureId);
                psInsertRoomFeature.executeUpdate();
            }
        }
    }
}

    public static void editReservation(int reservationId, String selectedRoomName, Date checkInDate, Date checkOutDate,
                                   String checkedIn, String checkedOut, List<String> selectedCustomerIdentityNumbers,
                                   Map<Service, Integer> selectedServices) {
    String selectRoomIdQuery = "SELECT room_id FROM rooms WHERE room_name = ?";
    String checkReservationExistQuery = "SELECT * FROM reservations WHERE room_id = ? AND reservation_id != ?";
    String updateReservationQuery = "UPDATE reservations SET room_id = ?, check_in_date = ?, check_out_date = ?, checked_in_date = ?, checked_out_date = ? WHERE reservation_id = ?";
    String deleteCustomersQuery = "DELETE FROM reservation_customers WHERE reservation_id = ?";
    String deleteServicesQuery = "DELETE FROM reservation_services WHERE reservation_id = ?";
    String insertCustomerQuery = "INSERT INTO reservation_customers (reservation_id, customer_id) VALUES (?, ?)";
    String insertServiceQuery = "INSERT INTO reservation_services (reservation_id, service_id, quantity) VALUES (?, ?, ?)";

    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS)) {
        int roomId = getRoomId(connection, selectRoomIdQuery, selectedRoomName);
        
        if (isReservationExist(connection, checkReservationExistQuery, roomId, reservationId)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Room already reserved!");
            return;
        }

        updateReservation(connection, updateReservationQuery, roomId, checkInDate, checkOutDate, checkedIn, checkedOut, reservationId);
        updateEntityForReservation(connection, deleteCustomersQuery, insertCustomerQuery, "customer_id", "SELECT id FROM customers WHERE identity_number = ?", selectedCustomerIdentityNumbers, reservationId);
        updateEntityForReservation(connection, deleteServicesQuery, insertServiceQuery, "service_id", "SELECT service_id FROM services WHERE service_name = ?", new ArrayList<>(selectedServices.keySet()), reservationId);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation updated successfully");
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Unable to edit reservation!");
        e.printStackTrace();
    }
}

// Helper methods:

private static int getRoomId(Connection connection, String query, String roomName) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setString(1, roomName);
        try (ResultSet resultSet = ps.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("room_id");
            }
        }
    }
    return -1;
}

private static boolean isReservationExist(Connection connection, String query, int roomId, int reservationId) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, roomId);
        ps.setInt(2, reservationId);
        try (ResultSet resultSet = ps.executeQuery()) {
            return resultSet.isBeforeFirst();
        }
    }
    return false;
}

private static void updateReservation(Connection connection, String query, int roomId, Date checkInDate, Date checkOutDate, String checkedIn, String checkedOut, int reservationId) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, roomId);
        ps.setDate(2, checkInDate);
        ps.setDate(3, checkOutDate);
        ps.setDate(4, checkedIn.equals("YES") ? java.sql.Date.valueOf(LocalDate.now()) : null);
        ps.setDate(5, checkedOut.equals("YES") ? java.sql.Date.valueOf(LocalDate.now()) : null);
        ps.setInt(6, reservationId);
        ps.executeUpdate();
    }
}

private static void updateEntityForReservation(Connection connection, String deleteQuery, String insertQuery, String entityIdField, String selectIdQuery, List<?> entities, int reservationId) throws SQLException {
    try (PreparedStatement psDelete = connection.prepareStatement(deleteQuery)) {
        psDelete.setInt(1, reservationId);
        psDelete.executeUpdate();
    }

    for (Object entity : entities) {
        int entityId = -1;
        try (PreparedStatement psSelectId = connection.prepareStatement(selectIdQuery)) {
            if (entity instanceof String) {
                psSelectId.setString(1, (String) entity);
            } else if (entity instanceof Service) {
                psSelectId.setString(1, ((Service) entity).getServiceName());
            }
            try (ResultSet resultSet = psSelectId.executeQuery()) {
                if (resultSet.next()) {
                    entityId = resultSet.getInt(entityIdField);
                }
            }
        }

        if (entityId != -1) {
            try (PreparedStatement psInsert = connection.prepareStatement(insertQuery)) {
                psInsert.setInt(1, reservationId);
                psInsert.setInt(2, entityId);
                if (entity instanceof Service) {
                    psInsert.setInt(3, selectedServices.get(entity));
                }
                psInsert.executeUpdate();
            }
        }
    }
}
}