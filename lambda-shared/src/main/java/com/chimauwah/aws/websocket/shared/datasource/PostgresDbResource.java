package com.chimauwah.aws.websocket.shared.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Access Object for database queries.
 */
public class PostgresDbResource implements DatasourceResource {

    @Override
    public Set<String> getAll() throws Exception {
        Set<String> connections = new HashSet<>();
        String query = "SELECT connection_id FROM ws_demo.connections";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                connections.add(resultSet.getString("connection_id"));
            }
        }
        return connections;
    }

    @Override
    public void insert(String connectionId) throws Exception {
        String query = "INSERT INTO ws_demo.connections (connection_id) VALUES (?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, connectionId);
            preparedStatement.execute();
        }
    }

    @Override
    public void delete(String connectionId) throws Exception {
        String query = "DELETE FROM ws_demo.connections WHERE connection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, connectionId);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Get a database connection, added to allow spying during tests
     *
     * @return Database connection
     * @throws SQLException throws exception if error establishing a connection
     */
    public Connection getConnection() throws SQLException {
        return Database.connection();
    }
}

