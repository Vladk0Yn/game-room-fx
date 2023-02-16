package com.yanovych.repositories.implementations;

import com.google.gson.reflect.TypeToken;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.helpers.ObjectFileReader;
import com.yanovych.helpers.ObjectFileWriter;
import com.yanovych.repositories.interfaces.ToyRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ToyFromFileRepository implements ToyRepository {
    private ObjectFileReader<Toy> reader = null;
    private ObjectFileWriter<Toy> writer= null;
    private List<Toy> toys = null;
    private static ToyFromFileRepository instance = null;

    private ToyFromFileRepository() {
        this.reader = new ObjectFileReader<>();
        this.writer = new ObjectFileWriter<>();
        this.toys = this.getAllToys();
    }

    public static ToyFromFileRepository getInstance() {
        if (instance == null) {
            instance = new ToyFromFileRepository();
        }
        return instance;
    }

    @Override
    public Toy getToyById(Long id) {
        return getAllToys().stream()
                .filter(toy -> Objects.equals(toy.getId(), id)).findAny().orElse(null);
    }

    @Override
    public List<Toy> getAllToys() {
        return reader.readListOfObjects("toys.json", new TypeToken<ArrayList<Toy>>(){}.getType());
    }

    @Override
    public void addToy(Toy toy) {
        this.toys = this.getAllToys();
        if (this.toys.isEmpty()) {
            toy.setId(1L);
        } else {
            Long lastToyId = this.toys.stream()
                    .max(Comparator.comparingLong(Toy::getId)).get().getId();
            toy.setId(++lastToyId);
        }
        this.toys.add(toy);
        this.writer.writeListOfObjects("toys.json", this.toys, false);
    }

    @Override
    public void addToyToRoom(Toy toy, Room room) {
        this.toys = this.getAllToys();
        toy.setToyRoomId(room.getId());
        this.updateToy(toy);
    }

    @Override
    public void updateToy(Toy toy) {
        this.toys = this.getAllToys();
        for (int i = 0; i < this.toys.size(); i++) {
            if (toy.getId().equals(this.toys.get(i).getId())) {
                this.toys.set(i, toy);
                this.writer.writeListOfObjects("toys.json", this.toys, false);
                break;
            }
        }
    }

    @Override
    public void deleteToy(Toy toy) {
        this.toys = this.getAllToys();
        this.toys.remove(toy);
        this.writer.writeListOfObjects("toys.json", this.toys, false);
    }
}
