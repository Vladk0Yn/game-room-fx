package com.yanovych.services.implementations;

import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.helpers.PropertiesManager;
import com.yanovych.repositories.implementations.RoomFromDbRepository;
import com.yanovych.repositories.implementations.RoomFromFileRepository;
import com.yanovych.repositories.implementations.ToyFromDbRepository;
import com.yanovych.repositories.implementations.ToyFromFileRepository;
import com.yanovych.repositories.interfaces.RoomRepository;
import com.yanovych.repositories.interfaces.ToyRepository;
import com.yanovych.services.interfaces.ToyService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ToyServiceImplementation implements ToyService {
    private static ToyServiceImplementation instance = null;
    private ToyRepository toyRepository = null;
    private RoomRepository roomRepository = null;
    private ToyServiceImplementation() {
        String dataSource;
        try {
            Properties properties = PropertiesManager.getProperties("project.properties");
            dataSource = properties.getProperty("datasource");
            if (dataSource == null) {
                throw new IOException();
            }
        } catch (IOException e) {
            log.error("Properties file not found");
            throw new RuntimeException(e);
        }
        switch (dataSource) {
            case "file" -> {
                toyRepository = ToyFromFileRepository.getInstance();
                roomRepository = RoomFromFileRepository.getInstance();
            }
            case "db" -> {
                toyRepository = ToyFromDbRepository.getInstance();
                roomRepository = RoomFromDbRepository.getInstance();
            }
            default -> {
                log.error("Error at reading datasource property, default data source is file");
                toyRepository = ToyFromFileRepository.getInstance();
                roomRepository = RoomFromFileRepository.getInstance();
            }
        }
    }

    public static ToyServiceImplementation getInstance() {
        if (instance == null) {
            instance = new ToyServiceImplementation();
        }
        return instance;
    }

    @Override
    public Toy getToyById(Long id) {
        Toy toy = this.toyRepository.getToyById(id);
        if (toy == null) {
            log.warn("IN getToyById - no toy with id: {}", id);
            return null;
        }
        log.info("IN getToyById - toy: {} successfully found", toy.getName());
        return toy;
    }

    @Override
    public void createToy(Toy toy) {
        toyRepository.addToy(toy);
        log.info("IN createToy - toy: {} successfully created", toy.getName());
    }

    @Override
    public List<Toy> getAllToys() {
        List<Toy> toys = toyRepository.getAllToys();
        log.info("IN getAllToys - toys: {} successfully received", toys.size());
        return toys;
    }

    @Override
    public List<Toy> sortToysInRoomByType(Room room) {
        List<Toy> sortedListOfToysInRoom = new ArrayList<>();
        if (room.getToysInRoom() != null) {
            sortedListOfToysInRoom = room.getToysInRoom().stream()
                    .sorted(Comparator.comparing(Toy::getType))
                    .toList();
            log.info("IN sortToysInRoomByType - toys in room: {} successfully sorted", room.getName());
        } else {
            log.warn("IN sortToysInRoomByType - no toys in room: {}", room.getName());
        }
        return sortedListOfToysInRoom;
    }

    @Override
    public List<Toy> findToysInRoomByDiapasonOfPrice(Room room, Double priceMin, Double priceMax) {
        List<Toy> foundedListOfToysInRoom = new ArrayList<>();
        if (room.getToysInRoom() != null) {
            foundedListOfToysInRoom = room.getToysInRoom().stream()
                    .filter(toy -> toy.getPrice() >= priceMin && toy.getPrice() <= priceMax)
                    .toList();
            if (foundedListOfToysInRoom.isEmpty()) {
                log.warn("IN findToysInRoomByDiapasonOfPrice - toys in room: {} with minimum price: {}, maximum price: = {} not found", room.getName(), priceMin, priceMax);
            } else {
                log.info("IN findToysInRoomByDiapasonOfPrice - {} toys in room: {} successfully found", foundedListOfToysInRoom.size(), room.getName());
            }
        } else {
            log.warn("IN findToysInRoomByDiapasonOfPrice - no toys in room: {}", room.getName());
        }
        return foundedListOfToysInRoom;
    }

    @Override
    public void updateToy(Toy toy) {
        if (toy != null) {
            this.toyRepository.updateToy(toy);
            log.info("IN updateToy - toy: {} successfully updated", toy.getName());
        } else {
            log.warn("IN updateToy - toy has not updated, because toy is null");
        }
    }

    @Override
    public void deleteToy(Toy toy) {
        if (toy.getToyRoomId() != null) {
            Room toyRoom = this.roomRepository.getRoomById(toy.getToyRoomId());
            this.roomRepository.removeToyFromRoom(toy, toyRoom);
        }
        this.toyRepository.deleteToy(toy);
        log.info("IN deleteToy - toy: {} successfully deleted", toy.getName());
    }
}
