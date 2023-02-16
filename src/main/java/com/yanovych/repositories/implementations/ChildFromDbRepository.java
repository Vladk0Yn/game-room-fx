package com.yanovych.repositories.implementations;

import com.yanovych.entities.Child;
import com.yanovych.entities.Room;
import com.yanovych.entities.enums.Sex;
import com.yanovych.exceptions.DaoOperationException;
import com.yanovych.helpers.ConnectionManager;
import com.yanovych.repositories.interfaces.ChildRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChildFromDbRepository implements ChildRepository {
    private final static String INSERT_CHILD_SQL = "INSERT INTO child(child_name, child_age, child_sex) VALUES(?,?,?);";
    private final static String INSERT_ROOM_ID_INTO_CHILD_SQL = "UPDATE child SET child_room_id = ? WHERE child_id = ?;";
    private final static String SELECT_CHILD_BY_ID_SQL = "SELECT * FROM child WHERE child_id = ?;";
    private final static String SELECT_ALL_CHILDREN_SQL = "SELECT * FROM child;";
    private final static String UPDATE_CHILD_SQL = "UPDATE child SET child_name = ?, child_age = ?, child_sex = ? WHERE child_id = ?;";
    private final static String DELETE_CHILD_SQL = "DELETE FROM child WHERE child_id = ?;";
    private static ChildFromDbRepository instance = null;
    private final ConnectionManager connectionManager;

    private ChildFromDbRepository() {
        this.connectionManager = ConnectionManager.getConnectionManager();
    }
    public static ChildFromDbRepository getInstance() {
        if (instance == null) {
            instance = new ChildFromDbRepository();
        }
        return instance;
    }
    @Override
    public Child getChildById(Long id) {
        try (Connection connection = connectionManager.getConnection()) {
            return findRoomById(id, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot find child by id = %d", id), e);
        }
    }

    @Override
    public List<Child> getAllChildren() {
        try (Connection connection = connectionManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_CHILDREN_SQL);
            return collectToList(rs);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    @Override
    public void addChild(Child child) {
        try (Connection connection = connectionManager.getConnection()) {
            saveChild(child, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage(), e);
        }
    }

    @Override
    public void updateChild(Child child) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement updateStatement = prepareUpdateStatement(child, connection);
            executeUpdate(updateStatement, "Child was not updated");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot update child with id = %d", child.getId()), e);
        }
    }

    @Override
    public void deleteChild(Child child) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE_CHILD_SQL);
            deleteStatement.setLong(1, child.getId());
            executeUpdate(deleteStatement, "Child was not deleted");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot delete child with id = %d", child.getId()), e);
        }
    }

    @Override
    public void addChildToRoom(Child child, Room room) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement updateStatement = connection.prepareStatement(INSERT_ROOM_ID_INTO_CHILD_SQL);
            updateStatement.setLong(1, room.getId());
            updateStatement.setLong(2, child.getId());
            executeUpdate(updateStatement, "Child was not added to room");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot add child to room with id = %d", room.getId()), e);
        }
    }

    private Child findRoomById(Long id, Connection connection) throws SQLException {
        PreparedStatement selectByIdStatement = prepareSelectByIdStatement(id, connection);
        ResultSet resultSet = selectByIdStatement.executeQuery();
        resultSet.next();
        return parseRow(resultSet);
    }

    private PreparedStatement prepareSelectByIdStatement(Long id, Connection connection) {
        try {
            PreparedStatement selectByIdStatement = connection.prepareStatement(SELECT_CHILD_BY_ID_SQL);
            selectByIdStatement.setLong(1, id);
            return selectByIdStatement;
        } catch (SQLException e) {
            throw new DaoOperationException("Cannot prepare statement to select child by id", e);
        }
    }

    private Child parseRow(ResultSet rs) throws SQLException {
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

    private List<Child> collectToList(ResultSet rs) throws SQLException {
        List<Child> childList = new ArrayList<>();
        while (rs.next()) {
            Child child = parseRow(rs);
            childList.add(child);
        }

        return childList;
    }

    private void saveChild(Child child, Connection connection) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(connection, child);
        executeUpdate(insertStatement, "Child was not created");
        Long id = fetchGeneratedId(insertStatement);
        child.setId(id);
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Child child) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_CHILD_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillStatementWithChildData(insertStatement, child);
        } catch (SQLException e) {
            throw new DaoOperationException("Cannot prepare statement to insert child", e);
        }
    }

    private PreparedStatement fillStatementWithChildData(PreparedStatement insertStatement, Child child)
            throws SQLException {
        insertStatement.setString(1, child.getName());
        insertStatement.setInt(2, child.getAge());
        insertStatement.setString(3, child.getSex().toString());
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
            throw new DaoOperationException("Can not obtain an child ID");
        }
    }

    private PreparedStatement prepareUpdateStatement(Child child, Connection connection) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_CHILD_SQL);
            fillStatementWithChildData(updateStatement, child);
            updateStatement.setLong(4, child.getId());
            return updateStatement;
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot prepare update statement for child id = %d", child.getId()), e);
        }
    }
}
