package com.yanovych.controllers;

import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.entities.enums.*;
import com.yanovych.services.implementations.ToyServiceImplementation;
import com.yanovych.services.interfaces.ToyService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddToyViewController implements Initializable {

    public TextField toyNameInput;
    public TextField toyMinAgeInput;
    public TextField toyPriceInput;
    public ComboBox<ToyType> toyTypeBox;
    public ComboBox<ToySize> toySizeBox;
    public ComboBox<Color> toyColorBox;
    public ComboBox<ToyMaterial> toyMaterialBox;
    public Button saveToyBtn;
    public Button clearBtn;
    private Toy toy = new Toy();
    private boolean update;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveToyBtn.getStyleClass().setAll("btn","btn-success","btn-lg");
        clearBtn.getStyleClass().setAll("btn","btn-warning","btn-lg");

        toyTypeBox.setItems(FXCollections.observableArrayList(ToyType.values()));
        toySizeBox.setItems(FXCollections.observableArrayList(ToySize.values()));
        toyColorBox.setItems(FXCollections.observableArrayList(Color.values()));
        toyMaterialBox.setItems(FXCollections.observableArrayList(ToyMaterial.values()));

        toyMinAgeInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.matches("\\d*")) {
                    toyMinAgeInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        toyPriceInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("[0-9](\\.[0-9]*)?")) {
                toyPriceInput.setText(newValue.replaceAll("[^\\d.]", ""));
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
    public void onSaveToyClicked() {
        ToyService toyService = ToyServiceImplementation.getInstance();
        toy.setName(toyNameInput.getText());
        toy.setPrice(Double.valueOf(toyPriceInput.getText()));
        toy.setMinimumAge(Integer.valueOf(toyMinAgeInput.getText()));
        toy.setType(toyTypeBox.getValue());
        toy.setSize(toySizeBox.getValue());
        toy.setColor(toyColorBox.getValue());
        toy.setMaterial(toyMaterialBox.getValue());

        if (update) {
            toyService.updateToy(toy);
        } else {
            toy.setId(null);
            toy.setToyRoomId(null);
            toyService.createToy(toy);
        }
        closeView();
    }
    private void closeView() {
        Stage stage = (Stage) saveToyBtn.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onClearClicked() {
        this.toyNameInput.setText(null);
        this.toyPriceInput.setText(null);
        this.toyMinAgeInput.setText(null);
        this.toyTypeBox.setValue(null);
        this.toySizeBox.setValue(null);
        this.toyColorBox.setValue(null);
        this.toyMaterialBox.setValue(null);
    }

    public void setUpdatable(boolean update) {
        this.update = update;
    }

    public void fillInputsForUpdate(Toy toy) {
        this.toy = toy;
        this.toyNameInput.setText(toy.getName());
        this.toyPriceInput.setText(toy.getName());
        this.toyMinAgeInput.setText(toy.getMinimumAge().toString());
        this.toyTypeBox.setValue(toy.getType());
        this.toySizeBox.setValue(toy.getSize());
        this.toyColorBox.setValue(toy.getColor());
        this.toyMaterialBox.setValue(toy.getMaterial());
    }
}
