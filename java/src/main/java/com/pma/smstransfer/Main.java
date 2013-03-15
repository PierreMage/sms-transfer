package com.pma.smstransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException {
        QueryExecutor queryExecutor = new IosDbChecker().check(args[0]);
        queryExecutor.execute("select * from message", new ResultSetProcessor() {
            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("text"));
                }
            }
        });
    }
}
