package com.yanovych.repositories.interfaces;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;

import java.util.List;

public interface RoomRepository {
    Room getRoomById(Long id);
    List<Room> getAllRooms();
    void addRoom(Room room);
    void addChildToRoom(Child child, Room room);
    void removeChildFromRoom(Child child, Room room);
    void addToyToRoom(Toy toy, Room room);
    void removeToyFromRoom(Toy toy, Room room);
    void updateRoom(Room room);
    void deleteRoom(Room room);
}
