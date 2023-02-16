package com.yanovych.services.implementations;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.helpers.PropertiesManager;
import com.yanovych.repositories.implementations.ChildFromDbRepository;
import com.yanovych.repositories.implementations.ChildFromFileRepository;
import com.yanovych.repositories.implementations.RoomFromDbRepository;
import com.yanovych.repositories.implementations.RoomFromFileRepository;
import com.yanovych.repositories.interfaces.ChildRepository;
import com.yanovych.repositories.interfaces.RoomRepository;
import com.yanovych.services.interfaces.ChildService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ChildServiceImplementation implements ChildService {
    private static ChildServiceImplementation instance = null;
    private ChildRepository childRepository = null;
    private RoomRepository roomRepository = null;

    private ChildServiceImplementation() {
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
                childRepository = ChildFromFileRepository.getInstance();
                roomRepository = RoomFromFileRepository.getInstance();
            }
            case "db" -> {
                childRepository = ChildFromDbRepository.getInstance();
                roomRepository = RoomFromDbRepository.getInstance();
            }
            default -> {
                log.error("Error at reading datasource property, default data source is file");
                childRepository = ChildFromFileRepository.getInstance();
                roomRepository = RoomFromFileRepository.getInstance();
            }
        }
    }

    public static ChildServiceImplementation getInstance() {
        if (instance == null) {
            instance = new ChildServiceImplementation();
        }
        return instance;
    }

    @Override
    public Child getChildById(Long id) {
        Child child = this.childRepository.getChildById(id);
        if (child == null) {
            log.warn("IN getChildById - no child with id: {}", id);
            return null;
        }
        log.info("IN getChildById - child: {} successfully found", child.getName());
        return child;
    }

    @Override
    public void createChild(Child child) {
        childRepository.addChild(child);
        log.info("IN createChild - child: {} successfully created", child.getName());
    }

    @Override
    public List<Child> getAllChildren() {
        List<Child> children = childRepository.getAllChildren();
        log.info("IN getAllChildren - children: {} successfully received", children.size());
        return children;
    }

    @Override
    public void updateChild(Child child) {
        if (child != null) {
            this.childRepository.updateChild(child);
            log.info("IN updateChild - child: {} successfully updated", child.getName());
        } else {
            log.warn("IN updateChild - child was not updated, because child is null");
        }
    }

    @Override
    public void deleteChild(Child child) {
        if (child.getRoomId() != null) {
            Room childRoom = roomRepository.getRoomById(child.getRoomId());
            this.roomRepository.removeChildFromRoom(child, childRoom);
        }
        this.childRepository.deleteChild(child);
        log.info("IN deleteChild - child: {} successfully deleted", child.getName());
    }
}
