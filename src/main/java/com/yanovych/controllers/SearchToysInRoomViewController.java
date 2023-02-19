package com.yanovych.controllers;

import com.yanovych.entities.Room;
import com.yanovych.entities.enums.ToyType;
import com.yanovych.services.implementations.RoomServiceImplementation;
import com.yanovych.services.interfaces.RoomService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchToysInRoomViewController implements Initializable {
    public ComboBox<Room> roomBox;
    public TextField toyMinimumPriceInput;
    public TextField toyMaximumPriceInput;
    public Button searchToysBtn;

    private Room room;
    private Double minPrice;
    private Double maxPrice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchToysBtn.getStyleClass().setAll("btn","btn-success","btn-lg");
        RoomService roomService = RoomServiceImplementation.getInstance();
        List<Room> rooms = roomService.getAllRooms();
        roomBox.setItems(FXCollections.observableArrayList(rooms));
        toyMinimumPriceInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("[0-9](\\.[0-9]*)?")) {
                toyMinimumPriceInput.setText(newValue.replaceAll("[^\\d.]", ""));
                StringBuilder aus = new StringBuilder(newValue);
                boolean firstPointFound = false;
                for (int i = 0; i < aus.length(); i++){
                    if(aus.charAt(i) == '.') {
                        if(!firstPointFound)
                            firstPointFound = true;
                        else
                            aus.deleteCharAt(i);
                    }
                }
            }
        });
        toyMaximumPriceInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("[0-9](\\.[0-9]*)?")) {
                toyMaximumPriceInput.setText(newValue.replaceAll("[^\\d.]", ""));
                StringBuilder aus = new StringBuilder(newValue);
                boolean firstPointFound = false;
                for (int i = 0; i < aus.length(); i++){
                    if(aus.charAt(i) == '.') {
                        if(!firstPointFound)
                            firstPointFound = true;
                        else
                            aus.deleteCharAt(i);
                    }
                }
            }
        });
    }

    @FXML
    public void onSearchToysClicked() {
        this.room = roomBox.getValue();
        if (!toyMinimumPriceInput.getText().isEmpty()) {
            this.minPrice = Double.valueOf(toyMinimumPriceInput.getText());
        } else {
            this.minPrice = 0d;
        }
        if (!toyMaximumPriceInput.getText().isEmpty()) {
            this.maxPrice = Double.valueOf(toyMaximumPriceInput.getText());
        } else {
            this.maxPrice = Double.MAX_VALUE;
        }
        closeView();
    }

    private void closeView() {
        Stage stage = (Stage) searchToysBtn.getScene().getWindow();
        stage.close();
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        roomBox.setValue(room);
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }
}
