import org.example.communication_and_database.DatabaseConnection;
import org.example.enums.TrashType;
import org.example.exception.DatabaseException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatabaseConnectionTest {

    @Test
    public void testInsertDataToUnit() {
        DatabaseConnection databaseConnection = new DatabaseConnection();

        // implements mock database connection
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        try {
            when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);
            doNothing().when(mockStatement).setInt(anyInt(), anyInt());
            doNothing().when(mockStatement).setString(anyInt(), anyString());
            when(mockStatement.executeUpdate()).thenReturn(1);

            Connection spyConnection = spy(databaseConnection.getConnection());
            doReturn(mockStatement).when(spyConnection).prepareStatement(any(String.class));
            doNothing().when(spyConnection).close();

            // injects the spy connection into the database connection
            databaseConnection = spy(databaseConnection);
            doReturn(spyConnection).when(databaseConnection).getConnection();

            // tests the insertDataToUnit method
            DatabaseConnection finalDatabaseConnection = databaseConnection;
            assertDoesNotThrow(() -> finalDatabaseConnection.insertDataToUnit(1, 2, 3, "Test Location"));
        } catch (SQLException e) {
            fail("Exception not expected");
        }
    }

    @Test
    public void testInsertDataToHolder() {
        DatabaseConnection databaseConnection = new DatabaseConnection();

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockGeneratedKeys = mock(ResultSet.class);

        try {
            when(mockConnection.prepareStatement(any(String.class), anyInt())).thenReturn(mockStatement);
            doNothing().when(mockStatement).setString(anyInt(), anyString());
            doNothing().when(mockStatement).setDouble(anyInt(), anyDouble());
            when(mockStatement.executeUpdate()).thenReturn(1);

            when(mockGeneratedKeys.next()).thenReturn(true);
            when(mockGeneratedKeys.getInt(1)).thenReturn(1);

            doReturn(mockGeneratedKeys).when(mockStatement).getGeneratedKeys();

            Connection spyConnection = spy(databaseConnection.getConnection());
            doReturn(mockStatement).when(spyConnection).prepareStatement(any(String.class), anyInt());
            doNothing().when(spyConnection).close();

            databaseConnection = spy(databaseConnection);
            doReturn(spyConnection).when(databaseConnection).getConnection();

            // tests the insertDataToHolder method
            DatabaseConnection finalDatabaseConnection = databaseConnection;
            assertDoesNotThrow(() -> {
                int generatedId = finalDatabaseConnection.insertDataToHolder(TrashType.PLASTIC, 50.0, 100.0);
                assertEquals(1, generatedId);
            });
        } catch (SQLException e) {
            fail("Exception not expected");
        }
    }
}
