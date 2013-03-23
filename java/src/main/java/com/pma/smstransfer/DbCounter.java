package com.pma.smstransfer;

import java.sql.ResultSet;

import static java.lang.String.format;

public class DbCounter {

    public static final String QUERY = "select count(0) from %s";

    private final QueryExecutor queryExecutor;

    public DbCounter(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public int count(String tableName) {
        DbCountResultSetProcessor resultSetProcessor = new DbCountResultSetProcessor();
        queryExecutor.execute(format(QUERY, tableName), resultSetProcessor);
        return resultSetProcessor.getCount();
    }

    private class DbCountResultSetProcessor implements ResultSetProcessor {
        private int count;

        public DbCountResultSetProcessor() {
            this.count = -1;
        }

        public void process(ResultSet resultSet) throws Exception {
            this.count = resultSet.getInt(1);
        }

        public int getCount() {
            return count;
        }
    }
}
