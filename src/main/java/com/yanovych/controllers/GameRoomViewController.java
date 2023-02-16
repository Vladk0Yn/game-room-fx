package com.yanovych.controllers;

import com.yanovych.Main;
import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.services.implementations.ChildServiceImplementation;
import com.yanovych.services.implementations.RoomServiceImplementation;
import com.yanovych.services.implementations.ToyServiceImplementation;
import com.yanovych.services.interfaces.ChildService;
import com.yanovych.services.interfaces.RoomService;
import com.yanovych.services.interfaces.ToyService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GameRoomViewController implements Initializable {
    public TableView<Room> roomTable;

    public TableView<Child> childTable;

    public TableView<Toy> toyTable;

    public TableColumn<Room, Long> roomId;

    public TableColumn<Room, String> roomName;

    public TableColumn<Room, Integer> roomMinimumAge;

    public TableColumn<Room, Integer> roomMaximumAge;

    public TableColumn<Room, Double>  roomBudget;

    public TableColumn<Room, Double> roomCapacity;

    public TableColumn<Room, String> roomAction;

    public TableColumn<Child, Long> childId;

    public TableColumn<Child, String> childName;

    public TableColumn<Child, Integer> childAge;

    public TableColumn<Child, Long> childRoom;

    public TableColumn<Child, String> childSex;

    public TableColumn<Child, String> childAction;

    public TableColumn<Toy, Long> toyId;

    public TableColumn<Toy, String> toyName;

    public TableColumn<Toy, Integer> toyMinimumAge;

    public TableColumn<Toy, Double> toyPrice;

    public TableColumn<Toy, String> toyType;

    public TableColumn<Toy, String> toySize;

    public TableColumn<Toy, String> toyColor;

    public TableColumn<Toy, String> toyMaterial;

    public TableColumn<Toy, Long> toyRoom;

    public TableColumn<Toy, String> toyAction;
    public Button childrenBtn;
    public Button toysBtn;
    public Button roomsBtn;
    public Button addChildToRoomBtn;
    public Button addToyToRoomBtn;
    public ComboBox<Room> availableRoomsBox;
    public Label childrenInRoomCountLabel;
    public Label toysInRoomCountLabel;
    private final ChildService childService = ChildServiceImplementation.getInstance();
    private final RoomService roomService = RoomServiceImplementation.getInstance();
    private final ToyService toyService = ToyServiceImplementation.getInstance();
    public Button searchToysBtn;
    private Child child = null;
    private Room room = null;
    private Toy toy = null;

    List<Toy> toys = this.toyService.getAllToys();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetUiElements();
        loadDateChildTable();
        onShowChildrenClick();
    }
    private void resetUiElements() {
        room = null;
        toy = null;
        child = null;
        childrenInRoomCountLabel.setVisible(false);
        toysInRoomCountLabel.setVisible(false);
        addChildToRoomBtn.setVisible(false);
        availableRoomsBox.setVisible(false);
        addToyToRoomBtn.setVisible(false);
        searchToysBtn.setVisible(false);
        addToyToRoomBtn.setDisable(true);
        addChildToRoomBtn.setDisable(true);
        availableRoomsBox.setItems(null);
        roomsBtn.getStyleClass().setAll("btn","btn-default","btn-lg");
        toysBtn.getStyleClass().setAll("btn","btn-default","btn-lg");
        childrenBtn.getStyleClass().setAll("btn","btn-default","btn-lg");
        addChildToRoomBtn.getStyleClass().setAll("btn","btn-default","btn-lg");
        addToyToRoomBtn.getStyleClass().setAll("btn","btn-default","btn-lg");
        searchToysBtn.getStyleClass().setAll("btn","btn-primary","btn-lg");
    }
    private void loadDateChildTable() {
        List<Child> children = this.childService.getAllChildren();

        childId.setCellValueFactory(new PropertyValueFactory<>("id"));
        childName.setCellValueFactory(new PropertyValueFactory<>("name"));
        childAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        childSex.setCellValueFactory(new PropertyValueFactory<>("sex"));
        childRoom.setCellValueFactory(new PropertyValueFactory<>("roomId"));

        Callback<TableColumn<Child, Long>, TableCell<Child, Long>> childRoomCellFactory = (TableColumn<Child, Long> param) -> new TableCell<>() {
            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    if (item != null) {
                       setText(roomService.getRoomById(item).getName());
                    } else {
                        setText("No room");
                    }
                }
            }
        };
        childRoom.setCellFactory(childRoomCellFactory);

        Callback<TableColumn<Child, String>, TableCell<Child, String>> cellFactory = (TableColumn<Child, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Button deleteIcon = new Button("Delete");
                    deleteIcon.getStyleClass().setAll("btn","btn-danger","btn-sm");
                    deleteIcon.setPrefWidth(10);
                    Button editIcon = new Button("Edit");
                    editIcon.getStyleClass().setAll("btn","btn-success","btn-sm");
                    editIcon.setPrefWidth(10);
                    deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                        child = getTableView().getItems().get(getIndex());
                        childService.deleteChild(child);
                        onShowChildrenClick();
                    });
                    editIcon.setOnMouseClicked((MouseEvent event) -> {
                        child = getTableView().getItems().get(getIndex());
                        URL url = Main.class.getResource("AddChildView.fxml");
                        FXMLLoader loader = new FXMLLoader(url);
                        try {
                            loader.load();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException();
                        }
                        AddChildViewController addChildViewController = loader.getController();
                        addChildViewController.setUpdatable(true);
                        addChildViewController.fillInputsForUpdate(child);
                        Parent parent = loader.getRoot();
                        Stage stage = new Stage();
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                        stage.setScene(scene);
                        stage.initStyle(StageStyle.UTILITY);
                        stage.showAndWait();
                        loadDateChildTable();
                    });
                    VBox manageBtn = new VBox(editIcon, deleteIcon);
                    manageBtn.setStyle("-fx-alignment:center");
                    setGraphic(manageBtn);
                    setText(null);
                }
            }
        };

        childAction.setCellFactory(cellFactory);
        childTable.setItems(FXCollections.observableArrayList(children));
    }
    private void loadDateRoomTable() {
        List<Room> rooms = roomService.getAllRooms();

        roomId.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomName.setCellValueFactory(new PropertyValueFactory<>("name"));
        roomMinimumAge.setCellValueFactory(new PropertyValueFactory<>("minimumChildAge"));
        roomMaximumAge.setCellValueFactory(new PropertyValueFactory<>("maximumChildAge"));
        roomBudget.setCellValueFactory(new PropertyValueFactory<>("budget"));
        roomCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        Callback<TableColumn<Room, String>, TableCell<Room, String>> cellFactory = (TableColumn<Room, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Button deleteIcon = new Button("Delete");
                    deleteIcon.getStyleClass().setAll("btn","btn-danger","btn-sm");
                    deleteIcon.setPrefWidth(10);
                    Button editIcon = new Button("Edit");
                    editIcon.getStyleClass().setAll("btn","btn-success","btn-sm");
                    editIcon.setPrefWidth(10);
                    deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                        room = getTableView().getItems().get(getIndex());
                        roomService.deleteRoom(room);
                        onShowRoomsClick();
                    });
                    editIcon.setOnMouseClicked((MouseEvent event) -> {
                        room = getTableView().getItems().get(getIndex());
                        URL url = Main.class.getResource("AddRoomView.fxml");
                        FXMLLoader loader = new FXMLLoader(url);
                        try {
                            loader.load();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException();
                        }
                        AddRoomViewController addRoomViewController = loader.getController();
                        addRoomViewController.setUpdatable(true);
                        addRoomViewController.fillInputsForUpdate(room);
                        Parent parent = loader.getRoot();
                        Stage stage = new Stage();
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                        stage.setScene(scene);
                        stage.initStyle(StageStyle.UTILITY);
                        stage.showAndWait();
                        loadDateRoomTable();
                    });

                    VBox manageBtn = new VBox(editIcon, deleteIcon);
                    manageBtn.setStyle("-fx-alignment:center");
                    setGraphic(manageBtn);
                    setText(null);
                }
            }

        };
        roomAction.setCellFactory(cellFactory);

        roomTable.setItems(FXCollections.observableArrayList(rooms));
    }
    private void loadDateToyTable() {

        toyId.setCellValueFactory(new PropertyValueFactory<>("id"));
        toyName.setCellValueFactory(new PropertyValueFactory<>("name"));
        toyMinimumAge.setCellValueFactory(new PropertyValueFactory<>("minimumAge"));
        toyPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        toyType.setCellValueFactory(new PropertyValueFactory<>("type"));
        toyColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        toySize.setCellValueFactory(new PropertyValueFactory<>("size"));
        toyMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));

        Callback<TableColumn<Toy, Long>, TableCell<Toy, Long>> toyRoomCellFactory = (TableColumn<Toy, Long> param) -> new TableCell<>() {
            private final RoomService roomService = RoomServiceImplementation.getInstance();

            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    if (item != null) {
                        setText(roomService.getRoomById(item).getName());
                    } else {
                        setText("No room");
                    }
                }
            }
        };
        toyRoom.setCellValueFactory(new PropertyValueFactory<>("toyRoomId"));
        toyRoom.setCellFactory(toyRoomCellFactory);

        Callback<TableColumn<Toy, String>, TableCell<Toy, String>> cellFactory = (TableColumn<Toy, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Button deleteIcon = new Button("Delete");
                    deleteIcon.getStyleClass().setAll("btn","btn-danger","btn-sm");
                    deleteIcon.setPrefWidth(10);
                    Button editIcon = new Button("Edit");
                    editIcon.getStyleClass().setAll("btn","btn-success","btn-sm");
                    editIcon.setPrefWidth(10);
                    deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                        toy = getTableView().getItems().get(getIndex());
                        toyService.deleteToy(toy);
                        onShowToysClick();
                    });
                    editIcon.setOnMouseClicked((MouseEvent event) -> {
                        toy = getTableView().getItems().get(getIndex());
                        URL url = Main.class.getResource("AddToyView.fxml");
                        FXMLLoader loader = new FXMLLoader(url);
                        try {
                            loader.load();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException();
                        }
                        AddToyViewController addToyViewController = loader.getController();
                        addToyViewController.setUpdatable(true);
                        addToyViewController.fillInputsForUpdate(toy);
                        Parent parent = loader.getRoot();
                        Stage stage = new Stage();
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                        stage.setScene(scene);
                        stage.initStyle(StageStyle.UTILITY);
                        stage.showAndWait();
                        loadDateToyTable();
                    });
                    VBox manageBtn = new VBox(editIcon, deleteIcon);
                    manageBtn.setStyle("-fx-alignment:center");
                    setGraphic(manageBtn);
                    setText(null);
                }
            }

        };
        toyAction.setCellFactory(cellFactory);

        toyTable.setItems(FXCollections.observableArrayList(toys));
    }
    @FXML
    public void onNewChildClicked() {
        URL url = Main.class.getResource("AddChildView.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
        AddChildViewController addChildViewController = loader.getController();
        addChildViewController.setUpdatable(false);
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
        loadDateChildTable();
    }
    @FXML
    public void onNewToyClicked() {
        URL url = Main.class.getResource("AddToyView.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
        AddToyViewController addToyViewController = loader.getController();
        addToyViewController.setUpdatable(false);
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
        loadDateToyTable();
    }
    @FXML
    public void onNewRoomClicked() {
        URL url = Main.class.getResource("AddRoomView.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
        AddRoomViewController addRoomViewController = loader.getController();
        addRoomViewController.setUpdatable(false);
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
        loadDateRoomTable();
    }
    @FXML
    public void onSearchToysClick() {
        URL url = Main.class.getResource("SearchToysInRoomView.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
        SearchToysInRoomViewController searchToysInRoomViewController = loader.getController();
        if (this.roomTable.isVisible()) {
            searchToysInRoomViewController.setRoom(roomTable.getSelectionModel().getSelectedItem());
        }
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
        Room roomFromForm = searchToysInRoomViewController.getRoom();
        if (roomFromForm != null) {
            this.toys = toyService.findToysInRoomByDiapasonOfPrice(
                    roomFromForm,
                    searchToysInRoomViewController.getMinPrice(),
                    searchToysInRoomViewController.getMaxPrice());
            loadDateToyTable();
            resetUiElements();
            toysBtn.getStyleClass().setAll("btn","btn-primary","btn-lg");
            this.searchToysBtn.setVisible(true);
            this.childTable.setVisible(false);
            this.roomTable.setVisible(false);
            this.toyTable.setVisible(true);
        }
    }
    @FXML
    public void onChildTableCellClicked() {
        availableRoomsBox.setItems(null);
        child = childTable.getSelectionModel().getSelectedItem();
        if (child != null) {
            List<Room> availableRooms = roomService.getAvailableRoomsForAge(child.getAge());
            availableRoomsBox.setVisible(true);
            addChildToRoomBtn.setVisible(true);
            availableRoomsBox.setItems(FXCollections.observableArrayList(availableRooms));
        }
    }
    @FXML
    public void onToyTableCellClicked() {
        availableRoomsBox.setItems(null);
        toy = toyTable.getSelectionModel().getSelectedItem();
        if (toy != null) {
            List<Room> availableRooms = roomService.getAvailableRoomsForToy(toy);
            availableRoomsBox.setVisible(true);
            addToyToRoomBtn.setVisible(true);
            availableRoomsBox.setItems(FXCollections.observableArrayList(availableRooms));
        }
    }
    @FXML
    public void onRoomTableCellClicked() {
        availableRoomsBox.setItems(null);
        room = roomTable.getSelectionModel().getSelectedItem();
        if (room != null) {
            childrenInRoomCountLabel.setVisible(true);
            toysInRoomCountLabel.setVisible(true);
            searchToysBtn.setVisible(true);
            childrenInRoomCountLabel.setText(room.getChildrenInRoom().size() + " children in room");
            toysInRoomCountLabel.setText(room.getToysInRoom().size() + " toys in room");
        }
    }
    @FXML void onAvailableRoomsBoxClick() {
        if (childTable.isVisible()) {
            room = availableRoomsBox.getSelectionModel().getSelectedItem();
            if (room != null) {
                addChildToRoomBtn.setDisable(false);
                addChildToRoomBtn.getStyleClass().setAll("btn","btn-primary","btn-lg");
            }
        }
        if (toyTable.isVisible()) {
            room = availableRoomsBox.getSelectionModel().getSelectedItem();
            if (room != null) {
                addToyToRoomBtn.setDisable(false);
                addToyToRoomBtn.getStyleClass().setAll("btn","btn-primary","btn-lg");
            }
        }
    }
    @FXML
    public void onAddChildClick() {
        this.roomService.addChildToRoom(child, room);
        resetUiElements();
        onShowChildrenClick();
    }
    @FXML
    public void onAddToyClick() {
        this.roomService.addToyToRoom(toy, room);
        resetUiElements();
        onShowToysClick();
    }
    @FXML
    public void onShowChildrenClick() {
        this.loadDateChildTable();

        resetUiElements();

        childrenBtn.getStyleClass().setAll("btn","btn-primary","btn-lg");

        this.childTable.setVisible(true);
        this.roomTable.setVisible(false);
        this.toyTable.setVisible(false);
    }
    @FXML
    public void onShowRoomsClick() {
        this.loadDateRoomTable();

        resetUiElements();

        roomsBtn.getStyleClass().setAll("btn","btn-primary","btn-lg");

        this.childTable.setVisible(false);
        this.roomTable.setVisible(true);
        this.toyTable.setVisible(false);
    }
    @FXML
    public void onShowToysClick() {
        toys = this.toyService.getAllToys();
        this.loadDateToyTable();

        resetUiElements();

        toysBtn.getStyleClass().setAll("btn","btn-primary","btn-lg");
        this.searchToysBtn.setVisible(true);
        this.childTable.setVisible(false);
        this.roomTable.setVisible(false);
        this.toyTable.setVisible(true);
    }
}
