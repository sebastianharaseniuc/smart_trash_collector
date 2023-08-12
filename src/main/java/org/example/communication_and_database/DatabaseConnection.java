package org.example.communication_and_database;
import org.example.enums.TrashType;
import org.example.exception.DatabaseException;
import org.example.recycling_units.Unit;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sys";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE, "Error connecting to the database", e);
        }
        return connection;
    }

    public void initializeDatabaseTables() {
        initializeRecyclingHoldersTable();
        initializeRecyclingUnitsTable();
    }

    public List<Unit> initializeDatabaseDataIntoTables() {
        List<Unit> availableUnits = new ArrayList<>();

        String firstLocationName = "Zorilor";
        String secondLocationName = "Manastur";
        String thirdLocationName = "Marasti";

        Unit zorilorUnit = new Unit(firstLocationName, 700, 800, 900);
        Unit manasturUnit = new Unit(secondLocationName, 1000, 1100, 1200);
        Unit marastiUnit = new Unit(thirdLocationName, 1300, 1400, 1500);

        availableUnits.add(zorilorUnit);
        availableUnits.add(manasturUnit);
        availableUnits.add(marastiUnit);

        for(Unit unit : availableUnits) {
            if(getUnitIdByLocation(unit.getLocationName()) == 0) {
                int firstUnitPlasticHolderId = insertDataToHolder(TrashType.PLASTIC, 0, zorilorUnit.getPlasticHolder().getMaximumCapacity());
                int firstUnitGlassHolderId = insertDataToHolder(TrashType.GLASS, 0, zorilorUnit.getGlassHolder().getMaximumCapacity());
                int firstUnitPaperHolderId = insertDataToHolder(TrashType.PAPER, 0, zorilorUnit.getPaperHolder().getMaximumCapacity());

                int secondUnitPlasticHolderId = insertDataToHolder(TrashType.PLASTIC, 0, manasturUnit.getPlasticHolder().getMaximumCapacity());
                int secondUnitGlassHolderId = insertDataToHolder(TrashType.GLASS, 0, manasturUnit.getGlassHolder().getMaximumCapacity());
                int secondUnitPaperHolderId = insertDataToHolder(TrashType.PAPER, 0, manasturUnit.getPaperHolder().getMaximumCapacity());

                int thirdUnitPlasticHolderId = insertDataToHolder(TrashType.PLASTIC, 0, marastiUnit.getPlasticHolder().getMaximumCapacity());
                int thirdUnitGlassHolderId = insertDataToHolder(TrashType.GLASS, 0, marastiUnit.getGlassHolder().getMaximumCapacity());
                int thirdUnitPaperHolderId = insertDataToHolder(TrashType.PAPER, 0, marastiUnit.getPaperHolder().getMaximumCapacity());

                insertDataToUnit(firstUnitPlasticHolderId, firstUnitGlassHolderId, firstUnitPaperHolderId, firstLocationName);
                insertDataToUnit(secondUnitPlasticHolderId, secondUnitGlassHolderId, secondUnitPaperHolderId, secondLocationName);
                insertDataToUnit(thirdUnitPlasticHolderId, thirdUnitGlassHolderId, thirdUnitPaperHolderId, thirdLocationName);
            }
        }
        return availableUnits;
    }

    public void initializeRecyclingUnitsTable() {
        String createRecyclingUnitsQuery =
                """
                        CREATE TABLE IF NOT EXISTS recycling_units (
                            unit_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                            plastic_holder_id INT(11),
                            glass_holder_id INT(11),
                            paper_holder_id INT(11),
                            location VARCHAR(255),
                            FOREIGN KEY (plastic_holder_id) REFERENCES recycling_holders(holder_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                            FOREIGN KEY (glass_holder_id) REFERENCES recycling_holders(holder_id) ON UPDATE RESTRICT ON DELETE RESTRICT,
                            FOREIGN KEY (paper_holder_id) REFERENCES recycling_holders(holder_id) ON UPDATE RESTRICT ON DELETE RESTRICT
                        );
                        """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(createRecyclingUnitsQuery)) {
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void initializeRecyclingHoldersTable() {
        String createRecyclingHoldersTable = """
                CREATE TABLE IF NOT EXISTS recycling_holders (
                    holder_id INT(11) AUTO_INCREMENT PRIMARY KEY,
                    holder_type VARCHAR(50),
                    current_capacity DOUBLE,
                    maximum_capacity DOUBLE,
                    timestamp TIMESTAMP
                );""";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(createRecyclingHoldersTable)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertDataToUnit(int plasticHolderId, int glassHolderId, int paperHolderId, String location) {
        String insertSql = "INSERT INTO recycling_units (plastic_holder_id, glass_holder_id, paper_holder_id, location) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {

            preparedStatement.setInt(1, plasticHolderId);
            preparedStatement.setInt(2, glassHolderId);
            preparedStatement.setInt(3, paperHolderId);
            preparedStatement.setString(4, location);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertDataToHolder(TrashType holderType, double currentCapacity, double maximumCapacity) {
        int generatedId = 0;
        String sql = "INSERT INTO recycling_holders (holder_type, current_capacity, maximum_capacity) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, holderType.name());
            statement.setDouble(2, currentCapacity);
            statement.setDouble(3, maximumCapacity);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error inserting data to holder table", e);
        }

        return generatedId;
    }

    public int getHolderIdBasedOnLocation(String location, TrashType holderType) {
        String holderTypeName = getDBFieldByThrashType(holderType);

        int holderId = 0;
        String selectSQL = "select holder_id from recycling_holders where holder_id in (select %s from recycling_units where location = ?)";
        try (Connection connection = getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSQL.formatted(holderTypeName));)
        {
            selectStatement.setString(1, location);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if(resultSet.next()) {
                    holderId = resultSet.getInt("holder_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return holderId;
    }

    private static String getDBFieldByThrashType(TrashType holderType) {
        return switch (holderType) {
            case GLASS -> "glass_holder_id";
            case PAPER -> "paper_holder_id";
            case PLASTIC -> "plastic_holder_id";
            default -> null;
        };
    }

    public double getHolderCurrentCapacity(int holderId) {
        double currentCapacity = 0;
        String selectSql = "SELECT current_capacity, maximum_capacity FROM recycling_holders WHERE holder_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSql);)
        {
            selectStatement.setInt(1, holderId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if(resultSet.next()) {
                    currentCapacity = resultSet.getDouble("current_capacity");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currentCapacity;
    }

    public void updateDataToHolder(int holderId, double newCapacity) {
        String selectSql = "SELECT current_capacity, maximum_capacity FROM recycling_holders WHERE holder_id = ?";
        String updateSql = "UPDATE recycling_holders SET current_capacity = ? WHERE holder_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            selectStatement.setInt(1, holderId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                        updateStatement.setDouble(1, newCapacity);
                        updateStatement.setInt(2, holderId);
                        updateStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating data in recycling_holders table", e);
            e.printStackTrace();
        }
    }
    public int getHolderUnitId(String locationName, TrashType holderType) {
        String dbField;

        dbField = getDBFieldByThrashType(holderType);

        int unitId = 0;
        String query = String.format("SELECT %s FROM recycling_units WHERE location = ?", dbField);

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, locationName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    unitId = resultSet.getInt(dbField);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving unit ID from the database", e);
        }
        return unitId;
    }

    public double getCurrentCapacityForHolder(int holderId) {
        String query = "SELECT current_capacity FROM recycling_holders WHERE holder_id = ?";
        double currentCapacity = 0;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, holderId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    currentCapacity = resultSet.getDouble("current_capacity");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving current capacity from the database", e);
        }

        return currentCapacity;
    }

    public int getUnitIdByLocation(String location) {
        String query = "SELECT unit_id FROM recycling_units WHERE location = ?";
        int unitId = 0;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, location);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    unitId = resultSet.getInt("unit_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving unit id from the database", e);
        }

        return unitId;
    }
}