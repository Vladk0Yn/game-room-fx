package com.yanovych.repositories.interfaces;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;

import java.util.List;

public interface ChildRepository {

    Child getChildById(Long id);
    List<Child> getAllChildren();
    void addChild(Child child);
    void updateChild(Child child);
    void deleteChild(Child child);
    void addChildToRoom(Child child, Room room);
}
