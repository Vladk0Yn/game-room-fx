package com.yanovych.controllers;

import com.yanovych.entities.Room;
import com.yanovych.services.implementations.RoomServiceImplementation;
import com.yanovych.services.interfaces.RoomService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddRoomViewController implements Initializable {
    public TextField roomNameInput;
    public TextField roomCapacityInput;
    public TextField roomBudgetInput;
    public TextField roomMinAgeInput;
    public TextField roomMaxAgeInput;
    public Button saveRoomBtn;
    public Button clearBtn;
    private boolean update;
    private Room room = new Room();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveRoomBtn.getStyleClass().setAll("btn","btn-success","btn-lg");
        clearBtn.getStyleClass().setAll("btn","btn-warning","btn-lg");

        roomCapacityInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.matches("\\d*")) {
                    roomCapacityInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        roomMinAgeInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.matches("\\d*")) {
                    roomMinAgeInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        roomMaxAgeInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.matches("\\d*")) {
                    roomMaxAgeInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        roomBudgetInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("[0-9](\\.[0-9]*)?")) {
                roomBudgetInput.setText(newValue.replaceAll("[^\\d.]", ""));
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
    public void onSaveRoomClicked() {
        RoomService roomService = RoomServiceImplementation.getInstance();
        room.setName(roomNameInput.getText());
        room.setBudget(Double.parseDouble(roomBudgetInput.getText()));
        room.setCapacity(Integer.parseInt(roomCapacityInput.getText()));
        room.setMinimumChildAge(Integer.parseInt(roomMinAgeInput.getText()));
        room.setMaximumChildAge(Integer.parseInt(roomMaxAgeInput.getText()));
        if (update) {
            roomService.updateRoom(room);
        } else {
            room.setId(null);
            room.setChildrenInRoom(null);
            room.setToysInRoom(null);
            roomService.createRoom(room);
        }
        closeView();
    }

    private void closeView() {
        Stage stage = (Stage) saveRoomBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onClearClicked() {
        roomNameInput.setText(null);
        roomBudgetInput.setText(null);
        roomCapacityInput.setText(null);
        roomMinAgeInput.setText(null);
        roomMaxAgeInput.setText(null);
    }

    public void setUpdatable(boolean update) {
        this.update = update;
    }

    public void fillInputsForUpdate(Room room) {
       this.room = room;
        roomNameInput.setText(room.getName());
        roomBudgetInput.setText(room.getBudget().toString());
        roomCapacityInput.setText(room.getCapacity().toString());
        roomMinAgeInput.setText(room.getMinimumChildAge().toString());
        roomMaxAgeInput.setText(room.getMaximumChildAge().toString());
    }
}
