package com.yanovych.services.interfaces;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;

import java.util.List;

public interface RoomService {
    Room getRoomById(Long id);
    Room getRoomForChildByRoomId(Long id, Child child);
    Room getRoomForToyByRoomId(Long id, Toy toy);
    void createRoom(Room room);
    List<Room> getAllRooms();
    void addChildToRoom(Child child, Room room);
    void removeChildFromRoom(Child child, Room room);
    void addToyToRoom(Toy toy, Room room);
    void removeToyFromRoom(Toy toy, Room room);
    List<Room> getAvailableRoomsForAge(Integer age);
    List<Room> getAvailableRoomsForToy(Toy toy);
    void updateRoom(Room room);
    void deleteRoom(Room room);
}
