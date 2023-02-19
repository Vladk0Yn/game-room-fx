package com.yanovych.repositories;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.enums.Sex;
import com.yanovych.helpers.ObjectFileReader;
import com.yanovych.helpers.ObjectFileWriter;
import com.yanovych.repositories.implementations.ChildFromFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChildFromFileRepositoryTest {
    @Mock
    private ObjectFileReader<Child> reader;
    @Mock
    private ObjectFileWriter<Child> writer;
    @InjectMocks
    private ChildFromFileRepository childFromFileRepository;

    @Test
    void getChildById() {
        List<Child> expectedChildren = new ArrayList<>();

        expectedChildren.add(new Child(1L, "Test1", 1, Sex.MALE, 1L));
        expectedChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));

        when(reader.readListOfObjects(any(), any())).thenReturn(expectedChildren);

        Child actualChild = childFromFileRepository.getChildById(2L);

        assertEquals(expectedChildren.get(1), actualChild);
    }
    @Test
    void getAllChildren() {
        List<Child> expectedChildren = new ArrayList<>();

        expectedChildren.add(new Child(1L, "Test1", 1, Sex.MALE, 1L));
        expectedChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));

        when(reader.readListOfObjects(any(), any())).thenReturn(expectedChildren);

        List<Child> actualChildren = childFromFileRepository.getAllChildren();

        assertEquals(expectedChildren, actualChildren);
    }
    @Test
    void addChildIfListNotEmpty() {
        List<Child> expectedChildren = new ArrayList<>();

        expectedChildren.add(new Child(5L, "Test1", 1, Sex.MALE, 1L));
        expectedChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));
        expectedChildren.add(new Child(6L, "Test3", 3, Sex.FEMALE, null));

        List<Child> actualChildren = new ArrayList<>();

        actualChildren.add(new Child(5L, "Test1", 1, Sex.MALE, 1L));
        actualChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualChildren);

        childFromFileRepository.addChild(new Child("Test3", 3, Sex.FEMALE));

        assertEquals(expectedChildren, actualChildren);
        verify(writer).writeListOfObjects("children.json", actualChildren, false);
    }
    @Test
    void addChildIfListEmpty() {
        List<Child> expectedChildren = new ArrayList<>();

        expectedChildren.add(new Child(1L, "Test1", 3, Sex.FEMALE, null));

        List<Child> actualChildren = new ArrayList<>();

        when(reader.readListOfObjects(any(), any())).thenReturn(actualChildren);

        childFromFileRepository.addChild(new Child("Test1", 3, Sex.FEMALE));

        assertEquals(expectedChildren, actualChildren);

        verify(writer).writeListOfObjects("children.json", actualChildren, false);
    }
    @Test
    void updateChild() {
        List<Child> expectedChildren = new ArrayList<>();

        expectedChildren.add(new Child(1L, "Test1", 1, Sex.MALE, 1L));
        expectedChildren.add(new Child(2L, "Test2upt", 2, Sex.FEMALE, null));
        expectedChildren.add(new Child(3L, "Test3", 3, Sex.FEMALE, null));

        List<Child> actualChildren = new ArrayList<>();

        actualChildren.add(new Child(1L, "Test1", 1, Sex.MALE, 1L));
        actualChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));
        actualChildren.add(new Child(3L, "Test3", 3, Sex.FEMALE, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualChildren);

        childFromFileRepository.updateChild(new Child(2L, "Test2upt", 2, Sex.FEMALE, null));

        assertEquals(expectedChildren, actualChildren);
        verify(writer).writeListOfObjects("children.json", actualChildren, false);
    }
    @Test
    void addChildToRoom() {
        List<Child> expectedChildren = new ArrayList<>();
        expectedChildren.add(new Child(1L, "Test1", 1, Sex.MALE, 1L));
        expectedChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));
        expectedChildren.add(new Child(3L, "Test3", 3, Sex.FEMALE, 3L));

        List<Child> actualChildren = new ArrayList<>();
        actualChildren.add(new Child(1L, "Test1", 1, Sex.MALE, 1L));
        actualChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));
        actualChildren.add(new Child(3L, "Test3", 3, Sex.FEMALE, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualChildren);

        Child child = actualChildren.get(2);
        Room room = new Room("room", 10, 20.0, 1, 5);
        room.setId(3L);

        childFromFileRepository.addChildToRoom(child, room);

        assertEquals(expectedChildren, actualChildren);
        verify(writer).writeListOfObjects("children.json", actualChildren, false);
    }
    @Test
    void deleteChild() {
        List<Child> expectedChildren = new ArrayList<>();

        expectedChildren.add(new Child(5L, "Test1", 1, Sex.MALE, 1L));
        expectedChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));

        List<Child> actualChildren = new ArrayList<>();

        actualChildren.add(new Child(5L, "Test1", 1, Sex.MALE, 1L));
        actualChildren.add(new Child(2L, "Test2", 2, Sex.FEMALE, 2L));
        actualChildren.add(new Child(6L, "Test3", 3, Sex.FEMALE, null));

        when(reader.readListOfObjects(any(), any())).thenReturn(actualChildren);

        childFromFileRepository.deleteChild(actualChildren.get(2));

        assertEquals(expectedChildren, actualChildren);
        verify(writer).writeListOfObjects("children.json", actualChildren, false);
    }
}
