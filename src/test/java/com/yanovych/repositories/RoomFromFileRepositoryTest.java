package com.yanovych.repositories;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.entities.enums.*;
import com.yanovych.helpers.ObjectFileReader;
import com.yanovych.helpers.ObjectFileWriter;
import com.yanovych.repositories.implementations.ChildFromFileRepository;
import com.yanovych.repositories.implementations.RoomFromFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomFromFileRepositoryTest {
    @Mock
    private ObjectFileReader<Room> reader;
    @Mock
    private ObjectFileWriter<Room> writer;
    @InjectMocks
    private RoomFromFileRepository roomFromFileRepository;

    @Test
    void getRoomById() {
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));
        expectedRooms.add(new Room(2L,"Test2", 15, 100.0, 3, 8, null, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(expectedRooms);

        Room actualRoom = roomFromFileRepository.getRoomById(2L);

        assertEquals(expectedRooms.get(1), actualRoom);
    }
    @Test
    void getAllRooms() {
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));
        expectedRooms.add(new Room(2L,"Test2", 15, 100.0, 3, 8, null, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(expectedRooms);

        List<Room> actualRooms = roomFromFileRepository.getAllRooms();

        assertEquals(expectedRooms, actualRooms);
    }
    @Test
    void addRoomIfListNotEmpty() {
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));
        expectedRooms.add(new Room(2L,"Test2", 15, 100.0, 3, 8, null, null));

        List<Room> actualRooms = new ArrayList<>();
        actualRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualRooms);
        roomFromFileRepository.addRoom(new Room("Test2", 15, 100.0, 3, 8));
        assertEquals(expectedRooms, actualRooms);
        verify(writer).writeListOfObjects("rooms.json", actualRooms, false);
    }
    @Test
    void addRoomIfListEmpty() {
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));

        List<Room> actualRooms = new ArrayList<>();

        when(reader.readListOfObjects(any(), any())).thenReturn(actualRooms);
        roomFromFileRepository.addRoom(new Room("Test1", 10, 100.0, 2, 7));
        assertEquals(expectedRooms, actualRooms);
        verify(writer).writeListOfObjects("rooms.json", actualRooms, false);
    }
    @Test
    void updateRoom() {
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));
        expectedRooms.add(new Room(2L,"Test2upd", 15, 100.0, 3, 8, null, null));

        List<Room> actualRooms = new ArrayList<>();
        actualRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));
        actualRooms.add(new Room(2L,"Test2", 15, 100.0, 3, 8, null, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualRooms);

        roomFromFileRepository.updateRoom(new Room(2L,"Test2upd", 15, 100.0, 3, 8, null, null));
        assertEquals(expectedRooms, actualRooms);
        verify(writer).writeListOfObjects("rooms.json", actualRooms, false);
    }
    @Test
    void deleteRoom() {
        List<Child> childrenFromRoom = new ArrayList<>();
        childrenFromRoom.add(new Child(5L, "Test1", 1, Sex.MALE, 2L));
        List<Toy> toysFromRoom = new ArrayList<>();
        toysFromRoom.add(
                new Toy(1L, "Test1", 1, 1.0, 2L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));

        List<Room> actualRooms = new ArrayList<>();
        actualRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));
        actualRooms.add(new Room(2L,"Test2", 15, 100.0, 3, 8, childrenFromRoom, toysFromRoom));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualRooms);
        roomFromFileRepository.deleteRoom(actualRooms.get(1));
        assertEquals(expectedRooms, actualRooms);
        assertNull(childrenFromRoom.get(0).getRoomId());
        assertNull(toysFromRoom.get(0).getToyRoomId());

        verify(writer).writeListOfObjects("rooms.json", actualRooms, false);
    }
    @Test
    void addChildToRoom() {
        List<Child> expectedChildrenFromRoom = new ArrayList<>();
        expectedChildrenFromRoom.add(new Child(5L, "Test1", 1, Sex.MALE, 1L));

        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, expectedChildrenFromRoom, null));

        List<Room> actualRooms = new ArrayList<>();
        actualRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualRooms);
        roomFromFileRepository.addChildToRoom(expectedChildrenFromRoom.get(0), actualRooms.get(0));
        assertEquals(expectedRooms, actualRooms);
    }
    @Test
    void removeChildFromRoom() {
        List<Child> expectedChildrenFromRoom = new ArrayList<>();
        expectedChildrenFromRoom.add(new Child(5L, "Test1", 2, Sex.MALE, 1L));

        List<Child> actualChildrenFromRoom = new ArrayList<>();
        actualChildrenFromRoom.add(new Child(5L, "Test1", 1, Sex.MALE, 1L));
        actualChildrenFromRoom.add(new Child(6L, "Test2", 1, Sex.MALE, 1L));

        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, expectedChildrenFromRoom, null));

        List<Room> actualRooms = new ArrayList<>();
        actualRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, actualChildrenFromRoom, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualRooms);
        roomFromFileRepository.removeChildFromRoom(actualChildrenFromRoom.get(1), actualRooms.get(0));
        assertEquals(expectedRooms, actualRooms);
    }
    @Test
    void addToyToRoom() {
        List<Toy> toysFromRoom = new ArrayList<>();
        toysFromRoom.add(
                new Toy(1L, "Test1", 2, 1.0, 1L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 99.0, 2, 7, null, toysFromRoom));

        List<Room> actualRooms = new ArrayList<>();
        actualRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualRooms);
        roomFromFileRepository.addToyToRoom(toysFromRoom.get(0), actualRooms.get(0));
        assertEquals(expectedRooms, actualRooms);
    }
    @Test
    void removeToyFromRoom() {
        List<Toy> expectedToysFromRoom = new ArrayList<>();
        expectedToysFromRoom.add(
                new Toy(1L, "Test1", 2, 1.0, 1L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Toy> actualToysFromRoom = new ArrayList<>();
        actualToysFromRoom.add(
                new Toy(1L, "Test1", 2, 1.0, 1L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToysFromRoom.add(
                new Toy(2L, "Test2", 2, 1.0, 1L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(new Room(1L,"Test1", 10, 100.0, 2, 7, null, expectedToysFromRoom));

        List<Room> actualRooms = new ArrayList<>();
        actualRooms.add(new Room(1L,"Test1", 10, 99.0, 2, 7, null, actualToysFromRoom));
        when(reader.readListOfObjects(any(), any())).thenReturn(actualRooms);
        roomFromFileRepository.removeToyFromRoom(actualToysFromRoom.get(1), actualRooms.get(0));

        assertEquals(expectedRooms, actualRooms);

    }


}
