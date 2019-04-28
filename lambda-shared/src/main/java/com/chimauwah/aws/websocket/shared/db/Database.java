package com.chimauwah.aws.websocket.shared.db;

import com.chimauwah.aws.websocket.shared.config.Configuration;
import com.chimauwah.aws.websocket.shared.config.ConfigurationHolder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * POJO used to connect to Database
 */
public final class Database {

    private Database() {
        throw new AssertionError();
    }

    private static final HikariDataSource dataSource;

    static {
        Configuration configuration = ConfigurationHolder.instance.configuration();
        Configuration.DataSourceProperties props = configuration.getDataSourceProperties();

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(props.getDriverClassName());
        config.setJdbcUrl(props.getUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());

        dataSource = new HikariDataSource(config);
    }

    /**
     * Returns a database connection object
     *
     * @return the connection to the DB
     * @throws SQLException thrown if connection fails
     */
    public static Connection connection() throws SQLException {
        return dataSource.getConnection();
    }
}
