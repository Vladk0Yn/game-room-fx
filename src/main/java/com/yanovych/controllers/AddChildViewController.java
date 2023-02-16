package com.yanovych.controllers;

import com.yanovych.entities.Child;
import com.yanovych.entities.enums.Sex;
import com.yanovych.services.implementations.ChildServiceImplementation;
import com.yanovych.services.interfaces.ChildService;
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

public class AddChildViewController implements Initializable {

    public TextField childNameInput;
    public TextField childAgeInput;
    public Button saveChildBtn;
    public Button clearBtn;
    public ComboBox<Sex> childSexBox;
    private boolean updatable = false;
    private Child child = new Child();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveChildBtn.getStyleClass().setAll("btn","btn-success","btn-lg");
        clearBtn.getStyleClass().setAll("btn","btn-warning","btn-lg");
        childSexBox.setItems(FXCollections.observableArrayList(Sex.values()));

        childAgeInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.matches("\\d*")) {
                    childAgeInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    public void onSaveChildClicked() {
        ChildService childService = ChildServiceImplementation.getInstance();
        child.setName(childNameInput.getText());
        child.setAge(Integer.parseInt(childAgeInput.getText()));
        child.setSex(childSexBox.getValue());
        if (updatable) {
            childService.updateChild(child);
        } else {
            child.setId(null);
            child.setRoomId(null);
            childService.createChild(child);
        }
        closeView();
    }

    @FXML
    public void onClearClicked() {
        childNameInput.setText(null);
        childAgeInput.setText(null);
        childSexBox.setValue(null);
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public void fillInputsForUpdate(Child child) {
        this.child = child;
        childNameInput.setText(child.getName());
        childAgeInput.setText(child.getAge().toString());
        childSexBox.setValue(child.getSex());
    }

    private void closeView() {
       Stage stage = (Stage) saveChildBtn.getScene().getWindow();
       stage.close();
    }
}
