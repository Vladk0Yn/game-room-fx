package com.yanovych.automation;

import com.yanovych.Main;
import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.entities.enums.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import javafx.scene.control.TableView;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.stage.Stage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.*;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@TestMethodOrder(OrderAnnotation.class)
public class GameRoomViewTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("GameRoomView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.show();

    }
    @Test
    @Order(1)
    public void testCreateChild() {
        clickOn("#menuBtn").clickOn("#newChildBtn");
        verifyThat("#addChildView", isVisible());
        clickOn("#childNameInput").write("NameTest");
        clickOn("#childAgeInput").write("1");
        ComboBox<Sex> comboBoxAdd = lookup("#childSexBox").queryComboBox();
        clickOn(comboBoxAdd).clickOn(String.valueOf(Sex.FEMALE));
        clickOn("#saveChildBtn");
        verifyThat("#childTable", hasTableCell("NameTest"));
    }

    @Test
    @Order(2)
    public void testChildUpdate() {
        TableView<Child> tableView = lookup("#childTable").queryTableView();
        int rowIndex = tableView.getItems().size() - 1;
        Button buttonAdd = lookup("#editChildBtn").nth(rowIndex).queryButton();
        clickOn(buttonAdd);
        verifyThat("#addChildView", isVisible());
        clickOn("#childNameInput").write("Upd");
        clickOn("#saveChildBtn");
        verifyThat("#childTable", hasTableCell("NameTestUpd"));
    }

    @Test
    @Order(3)
    public void testChildDelete() {
        TableView<Child> tableView = lookup("#childTable").queryTableView();
        int rowIndex = tableView.getItems().size() - 1;
        Button buttonDelete = lookup("#deleteChildBtn").nth(rowIndex).queryButton();
        Child lastChild = tableView.getItems().get(rowIndex);
        clickOn(buttonDelete);
        List<Child> children = tableView.getItems();
        assertFalse(children.contains(lastChild));
    }
    @Test
    @Order(4)
    public void testOpenToysTable() {
        clickOn("#toysBtn");
        verifyThat("#toyTable", isVisible());
    }
    @Test
    @Order(5)
    public void testCreateToy() {
        clickOn("#menuBtn").clickOn("#newToyBtn");
        verifyThat("#addToyView", isVisible());
        clickOn("#toyNameInput").write("NameTest");
        clickOn("#toyMinAgeInput").write("1");
        clickOn("#toyPriceInput").write("10.0");
        clickOn("#toyTypeBox").clickOn(String.valueOf(ToyType.ANIMAL));
        clickOn("#toySizeBox").clickOn(String.valueOf(ToySize.MEDIUM));
        clickOn("#toyColorBox").clickOn(String.valueOf(Color.RED));
        clickOn("#toyMaterialBox").clickOn(String.valueOf(ToyMaterial.CLOTH));
        clickOn("#saveToyBtn");
        verifyThat("#toyTable", hasTableCell("NameTest"));
    }

    @Test
    @Order(6)
    public void testToyUpdate() {
        clickOn("#toysBtn");
        TableView<Toy> tableView = lookup("#toyTable").queryTableView();
        int rowIndex = tableView.getItems().size() - 1;
        Button buttonAdd = lookup("#editToyBtn").nth(rowIndex).queryButton();
        clickOn(buttonAdd);
        verifyThat("#addToyView", isVisible());
        clickOn("#toyNameInput").write("Upd");
        clickOn("#saveToyBtn");
        verifyThat("#toyTable", hasTableCell("NameTestUpd"));
    }

    @Test
    @Order(7)
    public void testToyDelete() {
        clickOn("#toysBtn");
        TableView<Toy> tableView = lookup("#toyTable").queryTableView();
        int rowIndex = tableView.getItems().size() - 1;
        Button buttonDelete = lookup("#deleteToyBtn").nth(rowIndex).queryButton();
        Toy lastToy = tableView.getItems().get(rowIndex);
        clickOn(buttonDelete);
        List<Toy> toys = tableView.getItems();
        assertFalse(toys.contains(lastToy));
    }
    @Test
    @Order(8)
    public void testOpenRoomTable() {
        clickOn("#roomsBtn");
        verifyThat("#roomTable", isVisible());
    }
    @Test
    @Order(9)
    public void testCreateRoom() {
        clickOn("#menuBtn").clickOn("#newRoomBtn");
        verifyThat("#addRoomView", isVisible());
        clickOn("#roomNameInput").write("NameTest");
        clickOn("#roomCapacityInput").write("20");
        clickOn("#roomBudgetInput").write("40.5");
        clickOn("#roomMinAgeInput").write("1");
        clickOn("#roomMaxAgeInput").write("6");
        clickOn("#saveRoomBtn");
        verifyThat("#roomTable", hasTableCell("NameTest"));
    }

    @Test
    @Order(10)
    public void testRoomUpdate() {
        clickOn("#roomsBtn");
        TableView<Toy> tableView = lookup("#roomTable").queryTableView();
        int rowIndex = tableView.getItems().size() - 1;
        Button buttonAdd = lookup("#editRoomBtn").nth(rowIndex).queryButton();
        clickOn(buttonAdd);
        verifyThat("#addRoomView", isVisible());
        clickOn("#roomNameInput").write("Upd");
        clickOn("#saveRoomBtn");
        verifyThat("#roomTable", hasTableCell("NameTestUpd"));
    }

    @Test
    @Order(11)
    public void testRoomDelete() {
        clickOn("#roomsBtn");
        TableView<Room> tableView = lookup("#roomTable").queryTableView();
        int rowIndex = tableView.getItems().size() - 1;
        Button buttonDelete = lookup("#deleteRoomBtn").nth(rowIndex).queryButton();
        Room lastRoom = tableView.getItems().get(rowIndex);
        clickOn(buttonDelete);
        List<Room> rooms = tableView.getItems();
        assertFalse(rooms.contains(lastRoom));
    }
}
