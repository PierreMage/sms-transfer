package com.pma.smstransfer;

import java.io.File;
import java.sql.*;

public class QueryExecutor {

    private final String jdbcUrl;

    public QueryExecutor(String dbFilePath) throws ClassNotFoundException {
        this.jdbcUrl = getJdbcUrl(dbFilePath);

        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");
    }

    public void execute(String query, ResultSetProcessor resultSetProcessor) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery(query);
            resultSetProcessor.process(resultSet);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private String getJdbcUrl(String dbFilePath) {
        if (dbFilePath == null) {
            throw new IllegalArgumentException("dbFilePath cannot be null");
        }
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            throw new IllegalArgumentException(String.format("%s does not exist", dbFilePath));
        }
        return String.format("jdbc:sqlite:%s", dbFilePath);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }
}
