package smstransfer;

import java.sql.*;

public class Sample {

    public static void main(String[] args) throws ClassNotFoundException {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:sms.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery("select * from message");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("text"));
            }
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
}
