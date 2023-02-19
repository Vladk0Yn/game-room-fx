package com.yanovych.services;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.entities.enums.*;
import com.yanovych.repositories.interfaces.RoomRepository;
import com.yanovych.repositories.interfaces.ToyRepository;
import com.yanovych.services.implementations.ToyServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToyServiceImplementationTest {
    @Mock
    private ToyRepository toyRepository;
    @Mock
    private RoomRepository roomRepository;
    @InjectMocks
    private ToyServiceImplementation toyServiceImplementation;

    @Test
    void getToyByIdIfToyExist() {
        Toy toyExpected = new Toy(1L, "Test1", 1, 1.0, null,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        when(toyRepository.getToyById(1L)).thenReturn(toyExpected);
        Toy actualToy = toyServiceImplementation.getToyById(1L);
        assertEquals(toyExpected, actualToy);
        verify(toyRepository, times(1)).getToyById(1L);
    }
    @Test
    void getToyByIdIfToyNotExist() {
        when(toyRepository.getToyById(1L)).thenReturn(null);
        Toy actualToy = toyServiceImplementation.getToyById(1L);
        assertNull(actualToy);
        verify(toyRepository, times(1)).getToyById(1L);
    }
    @Test
    void getAllToys() {
        List<Toy> expectedToys = new ArrayList<>();
        expectedToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(2L, "Test2", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        when(toyRepository.getAllToys()).thenReturn(expectedToys);

        List<Toy> actualToys = toyServiceImplementation.getAllToys();

        assertEquals(expectedToys, actualToys);
        verify(toyRepository, times(1)).getAllToys();
    }
    @Test
    void createToy() {
        Toy toy = new Toy("Test1", 1, 1.0,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        toyServiceImplementation.createToy(toy);
        verify(toyRepository, times(1)).addToy(toy);
    }
    @Test
    void updateToyIfToyExist() {
        Toy toy = new Toy(1L, "Test1", 1, 1.0, null,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        toyServiceImplementation.updateToy(toy);
        verify(toyRepository, times(1)).updateToy(toy);
    }
    @Test
    void updateToyIfToyNotExist() {
        toyServiceImplementation.updateToy(null);
        verify(toyRepository, times(0)).updateToy(null);
    }
    @Test
    void deleteToyIfRoomIdNull() {
        Toy toy = new Toy(1L, "Test1", 1, 1.0, null,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        toyServiceImplementation.deleteToy(toy);
        verify(toyRepository, times(1)).deleteToy(toy);
    }
    @Test
    void deleteToyIfRoomIdNotNull() {
        Toy toy = new Toy(1L, "Test1", 1, 1.0, 1L,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, null);
        when(roomRepository.getRoomById(room.getId())).thenReturn(room);
        toyServiceImplementation.deleteToy(toy);
        verify(roomRepository, times(1)).removeToyFromRoom(toy, room);
        verify(toyRepository, times(1)).deleteToy(toy);
    }
    @Test
    void sortToysInRoomByType() {
        List<Toy> actualToysInRoom = new ArrayList<>();
        actualToysInRoom.add(
                new Toy(1L, "Test1", 1, 1.0, 1L,
                        ToyType.DOLL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToysInRoom.add(
                new Toy(2L, "Test2", 1, 1.0, 1L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToysInRoom.add(
                new Toy(3L, "Test3", 1, 1.0, 1L,
                        ToyType.CAR, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Toy> expectedToysInRoom = new ArrayList<>();
        expectedToysInRoom.add(
                new Toy(2L, "Test2", 1, 1.0, 1L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToysInRoom.add(
                new Toy(3L, "Test3", 1, 1.0, 1L,
                        ToyType.CAR, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToysInRoom.add(
                new Toy(1L, "Test1", 1, 1.0, 1L,
                        ToyType.DOLL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, actualToysInRoom);

        actualToysInRoom = toyServiceImplementation.sortToysInRoomByType(room);
        assertEquals(expectedToysInRoom, actualToysInRoom);
    }
    @Test
    void findToysInRoomByDiapasonOfPrice() {
        List<Toy> actualToysInRoom = new ArrayList<>();
        actualToysInRoom.add(
                new Toy(1L, "Test1", 1, 1.0, 1L,
                        ToyType.DOLL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToysInRoom.add(
                new Toy(2L, "Test2", 2, 2.0, 1L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToysInRoom.add(
                new Toy(3L, "Test3", 3, 3.0, 1L,
                        ToyType.CAR, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToysInRoom.add(
                new Toy(3L, "Test4", 4, 4.0, 1L,
                        ToyType.CAR, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Toy> expectedToysInRoom = new ArrayList<>();
        expectedToysInRoom.add(
                new Toy(2L, "Test2", 2, 2.0, 1L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToysInRoom.add(
                new Toy(3L, "Test3", 3, 3.0, 1L,
                        ToyType.CAR, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, actualToysInRoom);

        actualToysInRoom = toyServiceImplementation.findToysInRoomByDiapasonOfPrice(room, 2.0, 3.0);
        assertEquals(expectedToysInRoom, actualToysInRoom);
    }
}
