package com.yanovych.services.implementations;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.helpers.PropertiesManager;
import com.yanovych.repositories.implementations.*;
import com.yanovych.repositories.interfaces.ChildRepository;
import com.yanovych.repositories.interfaces.RoomRepository;
import com.yanovych.repositories.interfaces.ToyRepository;
import com.yanovych.services.interfaces.RoomService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class RoomServiceImplementation implements RoomService {

    private static RoomServiceImplementation instance = null;
    private RoomRepository roomRepository = null;
    private ChildRepository childRepository = null;
    private ToyRepository toyRepository = null;

    private RoomServiceImplementation() {
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
                roomRepository = RoomFromFileRepository.getInstance();
                childRepository = ChildFromFileRepository.getInstance();
                toyRepository = ToyFromFileRepository.getInstance();
            }
            case "db" -> {
                roomRepository = RoomFromDbRepository.getInstance();
                childRepository = ChildFromDbRepository.getInstance();
                toyRepository = ToyFromDbRepository.getInstance();
            }
            default -> {
                log.error("Error at reading datasource property, default data source is file");
                childRepository = ChildFromFileRepository.getInstance();
                roomRepository = RoomFromFileRepository.getInstance();
                toyRepository = ToyFromFileRepository.getInstance();
            }
        }
    }

    public static RoomServiceImplementation getInstance() {
        if (instance == null) {
            instance = new RoomServiceImplementation();
        }
        return instance;
    }

    @Override
    public Room getRoomById(Long id) {
        Room room = this.roomRepository.getRoomById(id);
        if (room == null) {
            log.warn("IN getRoomById - no room with id: {}", id);
            return null;
        }
        log.info("IN getRoomById - room: {} successfully found", room.getName());
        return room;
    }

    @Override
    public Room getRoomForChildByRoomId(Long id, Child child) {
        Room roomForChildAge = this.getAvailableRoomsForAge(child.getAge())
                .stream()
                .filter(room -> Objects.equals(room.getId(), id))
                .findAny().orElse(null);
        if (roomForChildAge == null) {
            log.warn("IN getRoomForChildAgeById - no room with id: {}", id);
            return null;
        }
        log.info("IN getRoomForChildAgeById - room: {} successfully found", roomForChildAge.getName());
        return roomForChildAge;
    }

    @Override
    public Room getRoomForToyByRoomId(Long id, Toy toy) {
        Room roomForToy = this.getAvailableRoomsForToy(toy)
                .stream()
                .filter(room -> Objects.equals(room.getId(), id))
                .findAny().orElse(null);
        if (roomForToy == null) {
            log.warn("IN getRoomForToyByRoomId - no room with id: {}", id);
            return null;
        }
        log.info("IN getRoomForToyByRoomId - room: {} successfully found", roomForToy.getName());
        return roomForToy;
    }

    @Override
    public void createRoom(Room room) {
        this.roomRepository.addRoom(room);
        log.info("IN create - room: {} successfully created", room.getName());
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = this.roomRepository.getAllRooms();
        log.info("IN getAll - rooms: {} successfully received", rooms.size());
        return rooms;
    }

    @Override
    public void addChildToRoom(Child child, Room room) {
        if (room.getChildrenInRoom() != null && room.getChildrenInRoom().contains(child)) {
            log.warn("IN addChildToRoom - child: {} already in room", child.getName());
            return;
        }
        if (child.getRoomId() != null) {
            this.removeChildFromRoom(child, getRoomById(child.getRoomId()));
        }
        this.childRepository.addChildToRoom(child, room);
        this.roomRepository.addChildToRoom(child, room);
        log.info("IN addChildToRoom - child: {} successfully added to room: {}", child.getName(), room.getName());
    }

    @Override
    public void removeChildFromRoom(Child child, Room room) {
        this.roomRepository.removeChildFromRoom(child, room);
        log.info("IN removeChildFromRoom - child: {} successfully removed from room: {}", child.getName(), room.getName());
    }

    @Override
    public void addToyToRoom(Toy toy, Room room) {
        if (room.getToysInRoom() != null && room.getToysInRoom().contains(toy)) {
            log.warn("IN addToyToRoom - toy: {} already in room", toy.getName());
            return;
        }
        if (room.getBudget() < toy.getPrice()) {
            log.warn("IN addToyToRoom - room: {} budget is not enough for toy: {}", room.getName(), toy.getName());
            return;
        }
        if (room.getToysInRoom() != null && !room.getToysInRoom().isEmpty() && room.getCapacity() < room.getToysInRoom().size() + 1) {
            log.warn("IN addToyToRoom - room: {} is full of toys", room.getName());
            return;
        }
        if (toy.getToyRoomId() != null) {
            this.removeToyFromRoom(toy, getRoomById(toy.getToyRoomId()));
        }
        this.toyRepository.addToyToRoom(toy, room);
        this.roomRepository.addToyToRoom(toy, room);
        log.info("IN addToyToRoom - toy: {} successfully added to room: {}", toy.getName(), room.getName());
    }

    @Override
    public void removeToyFromRoom(Toy toy, Room room) {
        this.roomRepository.removeToyFromRoom(toy, room);
        log.info("IN removeToyFromRoom - toy: {} successfully removed from room {}", toy.getName(), room.getName());
    }

    @Override
    public List<Room> getAvailableRoomsForAge(Integer age) {
        List<Room> availableRooms;
        availableRooms = this.getAllRooms().stream()
                .filter(room -> room.getMinimumChildAge() <= age && room.getMaximumChildAge() >= age)
                .toList();
        if (availableRooms.size() > 0) {
            log.info("IN getAvailableRoomsForAge - {} rooms is available", availableRooms.size());
            return availableRooms;
        }
        log.warn("IN getAvailableRoomsForAge - no available rooms");
        return availableRooms;
    }

    @Override
    public List<Room> getAvailableRoomsForToy(Toy toy) {
        List<Room> availableRoomByAge = this.getAllRooms().stream()
                .filter(room -> room.getMinimumChildAge() <= toy.getMinimumAge())
                .toList();
        List<Room> availableRoomsByBudget = availableRoomByAge.stream()
                .filter(room -> room.getBudget() >= toy.getPrice())
                .toList();
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : availableRoomsByBudget) {
            if (room.getToysInRoom() != null && !room.getToysInRoom().isEmpty()) {
                if (room.getCapacity() >= room.getToysInRoom().size() + 1) {
                    availableRooms.add(room);
                }
            } else {
                availableRooms.add(room);
            }
        }
        if (availableRooms.size() > 0) {
            log.info("IN getAvailableRoomsForToy - {} rooms is available", availableRooms.size());
            return availableRooms;
        }
        log.warn("IN getAvailableRoomsForToy - no available rooms");
        return availableRooms;
    }

    @Override
    public void updateRoom(Room room) {
        if (room != null) {
            this.roomRepository.updateRoom(room);
            log.info("IN updateRoom - room: {} successfully updated", room.getName());
        } else {
            log.warn("IN updateRoom - room has not updated, because room is null");
        }
    }

    @Override
    public void deleteRoom(Room room) {
        this.roomRepository.deleteRoom(room);
        log.info("IN deleteRoom - room: {} successfully deleted", room.getName());
    }
}
