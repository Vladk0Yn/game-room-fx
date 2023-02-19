package com.yanovych.services;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.entities.enums.*;
import com.yanovych.repositories.interfaces.ChildRepository;
import com.yanovych.repositories.interfaces.RoomRepository;
import com.yanovych.repositories.interfaces.ToyRepository;
import com.yanovych.services.implementations.RoomServiceImplementation;
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
public class RoomServiceImplementationTest
{
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ChildRepository childRepository;
    @Mock
    private ToyRepository toyRepository;
    @InjectMocks
    private RoomServiceImplementation roomServiceImplementation;

    @Test
    void getRoomByIdIfRoomExist() {
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, null);

        when(roomRepository.getRoomById(1L)).thenReturn(room);

        Room actualRoom = roomServiceImplementation.getRoomById(1L);

        assertEquals(room, actualRoom);
        verify(roomRepository, times(1)).getRoomById(1L);
    }
    @Test
    void getRoomByIdIfRoomNotExist() {
        when(roomRepository.getRoomById(1L)).thenReturn(null);

        Room actualRoom = roomServiceImplementation.getRoomById(1L);

        assertNull(actualRoom);
        verify(roomRepository, times(1)).getRoomById(1L);
    }
    @Test
    void getAllRooms() {
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));
        expectedRooms.add(new Room(2L,"Test2", 15, 100.0, 3, 8, null, null));

        when(roomRepository.getAllRooms()).thenReturn(expectedRooms);

        List<Room> actualRooms = roomServiceImplementation.getAllRooms();

        assertEquals(expectedRooms, actualRooms);
        verify(roomRepository, times(1)).getAllRooms();
    }
    @Test
    void createRoom() {
        Room room = new Room("Test2", 15, 100.0, 3, 8);
        roomServiceImplementation.createRoom(room);
        verify(roomRepository, times(1)).addRoom(room);
    }
    @Test
    void updateRoomIfRoomExist() {
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, null);
        roomServiceImplementation.updateRoom(room);
        verify(roomRepository, times(1)).updateRoom(room);
    }
    @Test
    void updateRoomIfRoomNotExist() {
        roomServiceImplementation.updateRoom(null);
        verify(roomRepository, times(0)).updateRoom(any());
    }
    @Test
    void deleteRoom() {
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, null);
        roomServiceImplementation.deleteRoom(room);
        verify(roomRepository, times(1)).deleteRoom(room);
    }
    @Test
    void getRoomForChildByRoomIdIfRoomExist() {
        Child child = new Child(1L, "Test", 4, Sex.MALE, 1L);
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1L,"Test1", 10, 100.0, 3, 7, null, null));
        rooms.add(new Room(2L,"Test2", 15, 100.0, 1, 3, null, null));
        when(roomRepository.getAllRooms()).thenReturn(rooms);
        Room room = roomServiceImplementation.getRoomForChildByRoomId(1L, child);
        assertEquals(rooms.get(0), room);
    }
    @Test
    void getRoomForChildByRoomIdIfRoomNotExist() {
        Child child = new Child(1L, "Test", 12, Sex.MALE, 1L);
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1L,"Test1", 10, 100.0, 3, 7, null, null));
        rooms.add(new Room(2L,"Test2", 15, 100.0, 1, 3, null, null));
        when(roomRepository.getAllRooms()).thenReturn(rooms);
        Room room = roomServiceImplementation.getRoomForChildByRoomId(1L, child);
        assertNull(room);
    }
    @Test
    void getRoomForToyByRoomIdIfRoomExist() {
        Toy toy = new Toy(1L, "Test1", 1, 1.0, 1L,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1L,"Test1", 10, 100.0, 1, 7, null, null));
        rooms.add(new Room(2L,"Test2", 15, 100.0, 5, 9, null, null));
        when(roomRepository.getAllRooms()).thenReturn(rooms);
        Room room = roomServiceImplementation.getRoomForToyByRoomId(1L, toy);
        assertEquals(rooms.get(0), room);
    }
    @Test
    void getRoomForToyByRoomIdIfRoomNotExist() {
        Toy toy = new Toy(1L, "Test1", 1, 100.0, 1L,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1L,"Test1", 10, 10.0, 3, 7, null, null));
        rooms.add(new Room(2L,"Test2", 15, 10.0, 1, 3, null, null));
        when(roomRepository.getAllRooms()).thenReturn(rooms);
        Room room = roomServiceImplementation.getRoomForToyByRoomId(1L, toy);
        assertNull(room);
    }
    @Test
    void addChildToRoomIfChildRoomIdNotNull() {
        Child child = new Child(1L, "Test", 1, Sex.MALE, 3L);
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, null);
        when(roomRepository.getRoomById(3L)).thenReturn(room);
        roomServiceImplementation.addChildToRoom(child, room);
        verify(roomRepository, times(1)).removeChildFromRoom(child, room);
        verify(roomRepository, times(1)).addChildToRoom(child, room);
        verify(childRepository, times(1)).addChildToRoom(child, room);
    }
    @Test
    void addChildToRoomIfChildAlreadyInRoom() {
        Child child = new Child(1L, "Test", 1, Sex.MALE, 1L);
        List<Child> childrenFromRoom = new ArrayList<>();
        childrenFromRoom.add(child);
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, childrenFromRoom, null);
        roomServiceImplementation.addChildToRoom(child, room);
        verify(roomRepository, times(0)).removeChildFromRoom(child, room);
        verify(roomRepository, times(0)).addChildToRoom(child, room);
        verify(childRepository, times(0)).addChildToRoom(child, room);
    }
    @Test
    void addToyToRoomIfToyRoomIdNotNull() {
        Toy toy = new Toy(1L, "Test1", 1, 1.0, 2L,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, null);
        when(roomRepository.getRoomById(2L)).thenReturn(room);
        roomServiceImplementation.addToyToRoom(toy, room);
        verify(roomRepository, times(1)).removeToyFromRoom(toy, room);
        verify(roomRepository, times(1)).addToyToRoom(toy, room);
        verify(toyRepository, times(1)).addToyToRoom(toy, room);
    }
    @Test
    void addToyToRoomIfToyAlreadyInRoom() {
        Toy toy = new Toy(1L, "Test1", 1, 1.0, 1L,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        List<Toy> toysFromRoom = new ArrayList<>();
        toysFromRoom.add(toy);
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, toysFromRoom);
        roomServiceImplementation.addToyToRoom(toy, room);
        verify(roomRepository, times(0)).removeToyFromRoom(toy, room);
        verify(roomRepository, times(0)).addToyToRoom(toy, room);
        verify(toyRepository, times(0)).addToyToRoom(toy, room);
    }
    @Test
    void addToyToRoomIfBudgetNotEnough() {
        Toy toy = new Toy(1L, "Test1", 1, 2.0, 1L,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        Room room = new Room(1L,"Test1", 10, 1.0, 2, 7, null, null);
        roomServiceImplementation.addToyToRoom(toy, room);
        verify(roomRepository, times(0)).removeToyFromRoom(toy, room);
        verify(roomRepository, times(0)).addToyToRoom(toy, room);
        verify(toyRepository, times(0)).addToyToRoom(toy, room);
    }
    @Test
    void addToyToRoomIfRoomFull() {
        Toy toy = new Toy(1L, "Test1", 1, 2.0, 1L,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        Toy toy2 = new Toy(2L, "Test2", 1, 1.0, 1L,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD);
        List<Toy> toysFromRoom = new ArrayList<>();
        toysFromRoom.add(toy2);
        Room room = new Room(1L,"Test1", 1, 100.0, 2, 7, null, toysFromRoom);
        roomServiceImplementation.addToyToRoom(toy, room);
        verify(roomRepository, times(0)).removeToyFromRoom(toy, room);
        verify(roomRepository, times(0)).addToyToRoom(toy, room);
        verify(toyRepository, times(0)).addToyToRoom(toy, room);
    }
}
