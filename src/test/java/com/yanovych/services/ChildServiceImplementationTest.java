package com.yanovych.services;


import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.enums.Sex;
import com.yanovych.repositories.interfaces.ChildRepository;
import com.yanovych.repositories.interfaces.RoomRepository;
import com.yanovych.services.implementations.ChildServiceImplementation;
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
public class ChildServiceImplementationTest {

    @Mock
    private ChildRepository childRepository;
    @Mock
    private RoomRepository roomRepository;
    @InjectMocks
    private ChildServiceImplementation childServiceImplementation;

    @Test
    void getChildByIdIfChildExist() {
        Child childExpected = new Child(1L, "Test", 1, Sex.MALE, null);
        when(childRepository.getChildById(1L)).thenReturn(childExpected);
        Child childActual = childServiceImplementation.getChildById(1L);
        assertEquals(childExpected, childActual);
        verify(childRepository, times(1)).getChildById(1L);
    }
    @Test
    void getChildByIdIfChildNotExist() {
        when(childRepository.getChildById(1L)).thenReturn(null);
        Child childActual = childServiceImplementation.getChildById(1L);
        assertNull(childActual);
        verify(childRepository, times(1)).getChildById(1L);
    }
    @Test
    void getAllChildren() {
        List<Child> childListExpected = new ArrayList<>();

        childListExpected.add(new Child("Test1", 1, Sex.MALE));
        childListExpected.add(new Child("Test2", 2, Sex.FEMALE));

        when(childRepository.getAllChildren()).thenReturn(childListExpected);

        List<Child> childListActual = childServiceImplementation.getAllChildren();

        assertEquals(childListExpected, childListActual);
        verify(childRepository, times(1)).getAllChildren();
    }
    @Test
    void createChild() {
        Child child = new Child("Test", 1, Sex.MALE);
        childServiceImplementation.createChild(child);
        verify(childRepository, times(1)).addChild(child);
    }
    @Test
    void updateChildIfChildExist() {
        Child child = new Child(1L, "Test", 1, Sex.MALE, null);
        childServiceImplementation.updateChild(child);
        verify(childRepository, times(1)).updateChild(child);
    }
    @Test
    void updateChildIfChildNotExist() {
        childServiceImplementation.updateChild(null);
        verify(childRepository, times(0)).updateChild(any());
    }
    @Test
    void deleteChildIfRoomIdNull() {
        Child child = new Child(1L, "Test", 1, Sex.MALE, null);
        childServiceImplementation.deleteChild(child);
        verify(childRepository, times(1)).deleteChild(child);
    }
    @Test
    void deleteChildIfRoomIdNotNull() {
        Child child = new Child(1L, "Test", 1, Sex.MALE, 1L);
        Room room = new Room(1L,"Test1", 10, 100.0, 2, 7, null, null);
        when(roomRepository.getRoomById(room.getId())).thenReturn(room);
        childServiceImplementation.deleteChild(child);
        verify(roomRepository, times(1)).removeChildFromRoom(child, room);
        verify(childRepository, times(1)).deleteChild(child);
    }
}
