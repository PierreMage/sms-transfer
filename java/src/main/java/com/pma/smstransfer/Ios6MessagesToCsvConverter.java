package com.pma.smstransfer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class Ios6MessagesToCsvConverter {

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

    private final QueryExecutor queryExecutor;
    private final String outputFilePath;
    private final String separator;
    private final String newlineReplacement;

    public Ios6MessagesToCsvConverter(String[] args) throws ClassNotFoundException {
        if (args.length != 4) {
            throw new IllegalArgumentException("Expecting 4 arguments: dbFilePath, outputFilePath, separator and newlineReplacement");
        }
        this.queryExecutor = new QueryExecutor(args[0]);
        this.outputFilePath = args[1];
        this.separator = args[2];
        this.newlineReplacement = args[3];
    }

    public void writeCsv() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFilePath)));
        try {
            writer.write(metadata());
            writer.newLine();
            Set<String> messages = messages();
            for (String message : messages) {
                writer.write(message);
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    private String metadata() {
        DbCounter dbCounter = new DbCounter(queryExecutor);
        int numberOfMessages = dbCounter.count("message");
        return String.format("separator:%s newline:%s numberOfMessages:%d", separator, newlineReplacement, numberOfMessages);
    }

    private Set<String> messages() {
        MessagesResultSetProcessor resultSetProcessor = new MessagesResultSetProcessor();
        queryExecutor.execute(QUERY, resultSetProcessor);
        return resultSetProcessor.getMessages();
    }

    private class MessagesResultSetProcessor implements ResultSetProcessor {
        private Set<String> messages;

        private MessagesResultSetProcessor() {
            this.messages = new HashSet<String>();
        }

        public void process(ResultSet resultSet) throws Exception {
            while (resultSet.next()) {
                String text = resultSet.getString("text");
                if (text != null) {
                    if (text.contains(separator)) {
                        System.err.println(format("%s won't be a good separator!", separator));
                        break;
                    }
                    if (text.contains(newlineReplacement)) {
                        System.err.println(format("%s won't be a good newline replacement!", newlineReplacement));
                        break;
                    }
                    text = text.replaceAll("\n", newlineReplacement);
                }
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append(resultSet.getString("contact_number")).append(separator)
                        .append(resultSet.getString("date")).append(separator)
                        .append(resultSet.getString("is_from_me")).append(separator)
                        .append(text).append(separator)
                        .append(resultSet.getString("has_attachments"));
                messages.add(messageBuilder.toString());
            }
        }

        public Set<String> getMessages() {
            return messages;
        }
    }
}
