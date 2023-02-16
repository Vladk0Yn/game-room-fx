package com.yanovych.repositories.interfaces;

import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;

import java.util.List;

public interface ToyRepository {
    Toy getToyById(Long id);
    List<Toy> getAllToys();
    void addToy(Toy toy);
    void updateToy(Toy toy);
    void deleteToy(Toy toy);
    void addToyToRoom(Toy toy, Room room);
}
