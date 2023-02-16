package com.yanovych.services.interfaces;

import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;

import java.util.List;

public interface ToyService {
    Toy getToyById(Long id);
    void createToy(Toy toy);
    List<Toy> getAllToys();
    List<Toy> sortToysInRoomByType(Room room);
    List<Toy> findToysInRoomByDiapasonOfPrice(Room room, Double priceMin, Double priceMax);
    void updateToy(Toy toy);
    void deleteToy(Toy toy);
}
