package com.pma.smstransfer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public static void main(final String[] args) throws ClassNotFoundException {
        QueryExecutor queryExecutor = new IosDbChecker().check(args[0]);
        queryExecutor.execute(QUERY, new ResultSetProcessor() {
            public void process(ResultSet resultSet) throws SQLException, IOException {
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[1])));
                String separator = args[2];
                String newline = args[3];
                while (resultSet.next()) {
                    String text = resultSet.getString("text");
                    if (text != null) {
                        if (text.contains(separator)) {
                            System.err.println(String.format("%s won't be a good separator!", separator)); //TODO: add test
                            break;
                        }
                        if (text.contains(newline)) {
                            System.err.println(String.format("%s won't be a good newline replacement!", newline)); //TODO: add test
                            break;
                        }
                    }
                    if (text != null) {
                        text = text.replaceAll("\n", newline);
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append(resultSet.getString("contact_number")).append("|");
                    builder.append(resultSet.getString("date")).append("|");
                    builder.append(resultSet.getString("is_from_me")).append("|");
                    builder.append(text).append("|");
                    builder.append(resultSet.getString("has_attachments"));
                    writer.write(builder.toString());
                    writer.newLine();
                }
                writer.close();
            }
        });
    }
}
