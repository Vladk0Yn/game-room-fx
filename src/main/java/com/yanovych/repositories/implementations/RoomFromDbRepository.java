package com.yanovych.repositories.implementations;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.entities.enums.*;
import com.yanovych.exceptions.DaoOperationException;
import com.yanovych.helpers.ConnectionManager;
import com.yanovych.repositories.interfaces.RoomRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomFromDbRepository implements RoomRepository {
    private final static String INSERT_ROOM_SQL = "INSERT INTO room(room_name, room_capacity, room_budget, room_max_child_age, room_min_child_age) VALUES(?,?,?,?,?);";
    private final static String SELECT_ROOM_BY_ID_SQL = "SELECT * FROM room WHERE room_id = ?;";
    private final static String SELECT_ALL_ROOMS_SQL = "SELECT * FROM room;";
    private final static String UPDATE_ROOM_SQL = "UPDATE room SET room_name = ?, room_capacity = ?, room_budget = ?, room_max_child_age = ?, room_min_child_age = ? WHERE room_id = ?;";
    private final static String SELECT_CHILDREN_FROM_ROOM = "SELECT * FROM child WHERE child_room_id = ?";
    private final static String SELECT_TOYS_FROM_ROOM = "SELECT * FROM toy WHERE toy_room_id = ?";
    private final static String DELETE_ROOM_SQL = "DELETE FROM room WHERE room_id = ?;";
    private static RoomFromDbRepository instance = null;
    private final ConnectionManager connectionManager;
    private RoomFromDbRepository() {
        this.connectionManager = ConnectionManager.getConnectionManager();
    }
    public static RoomFromDbRepository getInstance() {
        if (instance == null) {
            instance = new RoomFromDbRepository();
        }
        return instance;
    }
    @Override
    public Room getRoomById(Long id) {
        try (Connection connection = connectionManager.getConnection()) {
            return findRoomById(id, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot find room by id = %d", id), e);
        }
    }

    @Override
    public List<Room> getAllRooms() {
        try (Connection connection = connectionManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_ROOMS_SQL);
            return collectToList(rs);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    @Override
    public void addRoom(Room room) {
        try (Connection connection = connectionManager.getConnection()) {
            saveRoom(room, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage(), e);
        }
    }

    @Override
    public void updateRoom(Room room) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement updateStatement = prepareUpdateStatement(room, connection);
            executeUpdate(updateStatement, "Room was not updated");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot update room with id = %d", room.getId()), e);
        }
    }

    @Override
    public void deleteRoom(Room room) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE_ROOM_SQL);
            deleteStatement.setLong(1, room.getId());
            executeUpdate(deleteStatement, "Room was not deleted");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot delete room with id = %d", room.getId()), e);
        }
    }

    @Override
    public void addChildToRoom(Child child, Room room) {

    }

    @Override
    public void removeChildFromRoom(Child child, Room room) {

    }

    @Override
    public void addToyToRoom(Toy toy, Room room) {
        room.setBudget(room.getBudget() - toy.getPrice());
        this.updateRoom(room);
    }

    @Override
    public void removeToyFromRoom(Toy toy, Room room) {
        room.setBudget(room.getBudget() + toy.getPrice());
        this.updateRoom(room);
    }

    private Room parseRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong(1));
        room.setName(rs.getString(2));
        room.setCapacity(rs.getInt(3));
        room.setBudget(rs.getDouble(4));
        room.setMaximumChildAge(rs.getInt(5));
        room.setMinimumChildAge(rs.getInt(6));
        room.setChildrenInRoom(getChildrenFromRoom(room.getId()));
        room.setToysInRoom(getToysFromRoom(room.getId()));
        return room;
    }

    private List<Room> collectToList(ResultSet rs) throws SQLException {
        List<Room> roomList = new ArrayList<>();
        while (rs.next()) {
            Room room = parseRow(rs);
            roomList.add(room);
        }

        return roomList;
    }

    private List<Child> getChildrenFromRoom(long roomId) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_CHILDREN_FROM_ROOM);
            statement.setLong(1, roomId);
            ResultSet rs = statement.executeQuery();
            return collectChildrenToList(rs);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    private List<Child> collectChildrenToList(ResultSet rs) throws SQLException {
        List<Child> childList = new ArrayList<>();
        while (rs.next()) {
            Child child = parseChildRow(rs);
            childList.add(child);
        }

        return childList;
    }

    private Child parseChildRow(ResultSet rs) throws SQLException {
        Child child = new Child();
        child.setId(rs.getLong(1));
        child.setName(rs.getString(2));
        child.setAge(rs.getInt(3));
        child.setSex(Sex.valueOf(rs.getString(4).toUpperCase()));
        if (rs.getLong(5) != 0) {
            child.setRoomId(rs.getLong(5));
        }
        return child;
    }

    private List<Toy> getToysFromRoom(long roomId) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_TOYS_FROM_ROOM);
            statement.setLong(1, roomId);
            ResultSet rs = statement.executeQuery();
            return collectToysToList(rs);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    private List<Toy> collectToysToList(ResultSet rs) throws SQLException {
        List<Toy> toyList = new ArrayList<>();
        while (rs.next()) {
            Toy toy = parseToyRow(rs);
            toyList.add(toy);
        }

        return toyList;
    }
    private Toy parseToyRow(ResultSet rs) throws SQLException {
        Toy toy = new Toy();
        toy.setId(rs.getLong(1));
        toy.setName(rs.getString(2));
        toy.setMinimumAge(rs.getInt(3));
        toy.setPrice(rs.getDouble(4));
        toy.setType(ToyType.valueOf(rs.getString(5)));
        toy.setSize(ToySize.valueOf(rs.getString(6)));
        toy.setColor(Color.valueOf(rs.getString(7)));
        toy.setMaterial(ToyMaterial.valueOf(rs.getString(8)));
        if (rs.getLong(9) != 0) {
            toy.setToyRoomId(rs.getLong(9));
        }
        return toy;
    }

    private Room findRoomById(Long id, Connection connection) throws SQLException {
        PreparedStatement selectByIdStatement = prepareSelectByIdStatement(id, connection);
        ResultSet resultSet = selectByIdStatement.executeQuery();
        resultSet.next();
        return parseRow(resultSet);
    }

    private PreparedStatement prepareSelectByIdStatement(Long id, Connection connection) {
        try {
            PreparedStatement selectByIdStatement = connection.prepareStatement(SELECT_ROOM_BY_ID_SQL);
            selectByIdStatement.setLong(1, id);
            return selectByIdStatement;
        } catch (SQLException e) {
            throw new DaoOperationException("Cannot prepare statement to select room by id", e);
        }
    }

    private void saveRoom(Room room, Connection connection) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(connection, room);
        executeUpdate(insertStatement, "Room was not created");
        Long id = fetchGeneratedId(insertStatement);
        room.setId(id);
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Room room) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_ROOM_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillStatementWithRoomData(insertStatement, room);
        } catch (SQLException e) {
            throw new DaoOperationException("Cannot prepare statement to insert room", e);
        }
    }

    private PreparedStatement fillStatementWithRoomData(PreparedStatement insertStatement, Room room)
            throws SQLException {
        insertStatement.setString(1, room.getName());
        insertStatement.setInt(2, room.getCapacity());
        insertStatement.setDouble(3, room.getBudget());
        insertStatement.setInt(4, room.getMaximumChildAge());
        insertStatement.setInt(5, room.getMinimumChildAge());
        return insertStatement;
    }

    private void executeUpdate(PreparedStatement insertStatement, String errorMessage) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 0) {
            throw new DaoOperationException(errorMessage);
        }
    }

    private Long fetchGeneratedId(PreparedStatement insertStatement) throws SQLException {
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();

        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new DaoOperationException("Can not obtain an room ID");
        }
    }

    private PreparedStatement prepareUpdateStatement(Room room, Connection connection) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_ROOM_SQL);
            fillStatementWithRoomData(updateStatement, room);
            updateStatement.setLong(6, room.getId());
            return updateStatement;
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot prepare update statement for room id = %d", room.getId()), e);
        }
    }
}
