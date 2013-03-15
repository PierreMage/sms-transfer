package com.pma.smstransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    private static final String QUERY =
                    "SELECT h.id as contact_number\n" +
                    "     , m.date\n" +
                    "     , m.is_from_me\n" +
                    "     , m.text\n" +
                    "     , m.cache_has_attachments as has_attachments\n" +
                    "FROM handle  h\n" +
                    "   , message m\n" +
                    "WHERE m.handle_id = h.ROWID\n" +
                    "ORDER BY m.date";

    public static void main(String[] args) throws ClassNotFoundException {
        QueryExecutor queryExecutor = new IosDbChecker().check(args[0]);
        queryExecutor.execute(QUERY, new ResultSetProcessor() {
            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    String text = resultSet.getString("text");
                    if (text != null && text.contains("|")) {
                        System.err.println("| won't be a good separator!");
                        break;
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append(resultSet.getString("contact_number")).append("|");
                    builder.append(resultSet.getString("date")).append("|");
                    builder.append(resultSet.getString("is_from_me")).append("|");
                    builder.append(text).append("|");
                    builder.append(resultSet.getString("has_attachments"));
                    System.out.println(builder.toString());
                }
            }
        });
    }
}
