package com.yanovych.repositories.implementations;

import com.yanovych.entities.Room;
import com.yanovych.entities.Toy;
import com.yanovych.entities.enums.Color;
import com.yanovych.entities.enums.ToyMaterial;
import com.yanovych.entities.enums.ToySize;
import com.yanovych.entities.enums.ToyType;
import com.yanovych.exceptions.DaoOperationException;
import com.yanovych.helpers.ConnectionManager;
import com.yanovych.repositories.interfaces.ToyRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToyFromDbRepository implements ToyRepository {
    private final static String INSERT_TOY_SQL = "INSERT INTO toy(toy_name, toy_min_age, toy_price, toy_type, toy_size, toy_color, toy_material) VALUES(?,?,?,?,?,?,?);";
    private final static String INSERT_ROOM_ID_INTO_TOY_SQL = "UPDATE toy SET toy_room_id = ? WHERE toy_id = ?;";
    private final static String SELECT_TOY_BY_ID_SQL = "SELECT * FROM toy WHERE toy_id = ?;";
    private final static String SELECT_ALL_TOYS_SQL = "SELECT * FROM toy;";
    private final static String UPDATE_TOY_SQL = "UPDATE toy SET toy_name = ?, toy_min_age = ?, toy_price = ?,  toy_type = ?, toy_size = ?, toy_color = ?,  toy_material = ? WHERE toy_id = ?;";
    private final static String DELETE_TOY_SQL = "DELETE FROM toy WHERE toy_id = ?;";
    private static ToyFromDbRepository instance = null;
    private final ConnectionManager connectionManager;
    private ToyFromDbRepository() {
        this.connectionManager = ConnectionManager.getConnectionManager();
    }
    public static ToyFromDbRepository getInstance() {
        if (instance == null) {
            instance = new ToyFromDbRepository();
        }
        return instance;
    }


    @Override
    public Toy getToyById(Long id) {
        try (Connection connection = connectionManager.getConnection()) {
            return findToyById(id, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot find child by id = %d", id), e);
        }
    }

    @Override
    public List<Toy> getAllToys() {
        try (Connection connection = connectionManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_TOYS_SQL);
            return collectToList(rs);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    @Override
    public void addToy(Toy toy) {
        try (Connection connection = connectionManager.getConnection()) {
            saveToy(toy, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage(), e);
        }
    }

    @Override
    public void addToyToRoom(Toy toy, Room room) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement updateStatement = connection.prepareStatement(INSERT_ROOM_ID_INTO_TOY_SQL);
            updateStatement.setLong(1, room.getId());
            updateStatement.setLong(2, toy.getId());
            executeUpdate(updateStatement, "Toy was not added to room");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot add toy to room with id = %d", room.getId()), e);
        }
    }

    @Override
    public void updateToy(Toy toy) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement updateStatement = prepareUpdateStatement(toy, connection);
            executeUpdate(updateStatement, "Toy was not updated");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot update toy with id = %d", toy.getId()), e);
        }
    }

    @Override
    public void deleteToy(Toy toy) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE_TOY_SQL);
            deleteStatement.setLong(1, toy.getId());
            executeUpdate(deleteStatement, "Toy was not deleted");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot delete toy with id = %d", toy.getId()), e);
        }
    }

    private Toy findToyById(Long id, Connection connection) throws SQLException {
        PreparedStatement selectByIdStatement = prepareSelectByIdStatement(id, connection);
        ResultSet resultSet = selectByIdStatement.executeQuery();
        resultSet.next();
        return parseRow(resultSet);
    }

    private PreparedStatement prepareSelectByIdStatement(Long id, Connection connection) {
        try {
            PreparedStatement selectByIdStatement = connection.prepareStatement(SELECT_TOY_BY_ID_SQL);
            selectByIdStatement.setLong(1, id);
            return selectByIdStatement;
        } catch (SQLException e) {
            throw new DaoOperationException("Cannot prepare statement to select toy by id", e);
        }
    }

    private Toy parseRow(ResultSet rs) throws SQLException {
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

    private List<Toy> collectToList(ResultSet rs) throws SQLException {
        List<Toy> toyList = new ArrayList<>();
        while (rs.next()) {
            Toy toy = parseRow(rs);
            toyList.add(toy);
        }

        return toyList;
    }

    private void saveToy(Toy toy, Connection connection) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(connection, toy);
        executeUpdate(insertStatement, "Toy was not created");
        Long id = fetchGeneratedId(insertStatement);
        toy.setId(id);
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Toy toy) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_TOY_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillStatementWithToyData(insertStatement, toy);
        } catch (SQLException e) {
            throw new DaoOperationException("Cannot prepare statement to insert toy", e);
        }
    }

    private PreparedStatement fillStatementWithToyData(PreparedStatement insertStatement, Toy toy)
            throws SQLException {
        insertStatement.setString(1, toy.getName());
        insertStatement.setInt(2, toy.getMinimumAge());
        insertStatement.setDouble(3, toy.getPrice());
        insertStatement.setString(4, toy.getType().toString());
        insertStatement.setString(5, toy.getSize().toString());
        insertStatement.setString(6, toy.getColor().toString());
        insertStatement.setString(7, toy.getMaterial().toString());

        return insertStatement;
    }

    private PreparedStatement prepareUpdateStatement(Toy toy, Connection connection) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TOY_SQL);
            fillStatementWithToyData(updateStatement, toy);
            updateStatement.setLong(8, toy.getId());
            return updateStatement;
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot prepare update statement for toy id = %d", toy.getId()), e);
        }
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
            throw new DaoOperationException("Can not obtain an toy ID");
        }
    }
}
