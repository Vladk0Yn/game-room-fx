package com.yanovych.repositories;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.entities.enums.Color;
import com.yanovych.entities.enums.ToyMaterial;
import com.yanovych.entities.enums.ToySize;
import com.yanovych.entities.enums.ToyType;
import com.yanovych.helpers.ObjectFileReader;
import com.yanovych.helpers.ObjectFileWriter;
import com.yanovych.repositories.implementations.ChildFromFileRepository;
import com.yanovych.repositories.implementations.ToyFromFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ToyFromFileRepositoryTest {
    @Mock
    private ObjectFileReader<Toy> reader;
    @Mock
    private ObjectFileWriter<Toy> writer;
    @InjectMocks
    private ToyFromFileRepository toyFromFileRepository;

    @Test
    void getToyById() {
        List<Toy> expectedToys = new ArrayList<>();
        expectedToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(2L, "Test2", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        when(reader.readListOfObjects(any(), any())).thenReturn(expectedToys);

        Toy actualToy = toyFromFileRepository.getToyById(2L);

        assertEquals(expectedToys.get(1), actualToy);
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

        when(reader.readListOfObjects(any(), any())).thenReturn(expectedToys);

        List<Toy> actualToys = toyFromFileRepository.getAllToys();

        assertEquals(expectedToys, actualToys);
    }
    @Test
    void addToyIfListNotEmpty() {
        List<Toy> expectedToys = new ArrayList<>();
        expectedToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(2L, "Test2", 3, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(3L, "Test3", 2, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Toy> actualToys = new ArrayList<>();
        actualToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToys.add(
                new Toy(2L, "Test2", 3, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        when(reader.readListOfObjects(any(),any())).thenReturn(actualToys);

        toyFromFileRepository.addToy(new Toy( "Test3", 2, 1.0,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        assertEquals(expectedToys, actualToys);
        verify(writer).writeListOfObjects("toys.json", actualToys, false);
    }
    @Test
    void addToyIfListEmpty() {
        List<Toy> expectedToys = new ArrayList<>();
        expectedToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Toy> actualToys = new ArrayList<>();

        when(reader.readListOfObjects(any(),any())).thenReturn(actualToys);

        toyFromFileRepository.addToy(new Toy("Test1", 1, 1.0,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        assertEquals(expectedToys, actualToys);
        verify(writer).writeListOfObjects("toys.json", actualToys, false);
    }
    @Test
    void updateToy() {
        List<Toy> expectedToys = new ArrayList<>();
        expectedToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(2L, "Test2upt", 3, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(3L, "Test3", 2, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Toy> actualToys = new ArrayList<>();
        actualToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToys.add(
                new Toy(2L, "Test2", 3, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToys.add(
                new Toy(3L, "Test3", 2, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        when(reader.readListOfObjects(any(),any())).thenReturn(actualToys);

        toyFromFileRepository.updateToy(new Toy(2L, "Test2upt", 3, 1.0, null,
                ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        assertEquals(expectedToys, actualToys);
        verify(writer).writeListOfObjects("toys.json", actualToys, false);
    }
    @Test
    void addToyToRoom() {
        List<Toy> expectedToys = new ArrayList<>();
        expectedToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(2L, "Test2", 3, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(3L, "Test3", 2, 1.0, 3L,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Toy> actualToys = new ArrayList<>();
        actualToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToys.add(
                new Toy(2L, "Test2", 3, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToys.add(
                new Toy(3L, "Test3", 2, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        when(reader.readListOfObjects(any(),any())).thenReturn(actualToys);

        Toy toy = actualToys.get(2);
        Room room = new Room("room", 10, 20.0, 1, 5);
        room.setId(3L);

        toyFromFileRepository.addToyToRoom(toy, room);

        assertEquals(expectedToys, actualToys);
        verify(writer).writeListOfObjects("toys.json", actualToys, false);
    }
    @Test
    void deleteToy() {
        List<Toy> expectedToys = new ArrayList<>();
        expectedToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        expectedToys.add(
                new Toy(2L, "Test2", 3, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        List<Toy> actualToys = new ArrayList<>();
        actualToys.add(
                new Toy(1L, "Test1", 1, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToys.add(
                new Toy(2L, "Test2", 3, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));
        actualToys.add(
                new Toy(3L, "Test3", 2, 1.0, null,
                        ToyType.ANIMAL, ToySize.MEDIUM, Color.MULTI_COLOR, ToyMaterial.WOOD));

        when(reader.readListOfObjects(any(),any())).thenReturn(actualToys);

        toyFromFileRepository.deleteToy(actualToys.get(2));

        assertEquals(expectedToys, actualToys);
        verify(writer).writeListOfObjects("toys.json", actualToys, false);
    }
}
