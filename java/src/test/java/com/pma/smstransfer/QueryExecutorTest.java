package com.pma.smstransfer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class QueryExecutorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenDbFilePathIsNull() throws ClassNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("dbFilePath cannot be null");

        new QueryExecutor(null);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenDbFilePathDoesNotExist() throws ClassNotFoundException {
        String dbFilePath = "invalidPath";

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(dbFilePath + " does not exist");

        new QueryExecutor(dbFilePath);
    }

    @Test
    public void shouldExecuteQuery() throws ClassNotFoundException {
        QueryExecutor queryExecutor = new QueryExecutor("src/test/resources/iOS6-SMS.db");

        final String expectedResult = "CREATE TABLE message (ROWID INTEGER PRIMARY KEY AUTOINCREMENT, guid TEXT UNIQUE NOT NULL, text TEXT, replace INTEGER DEFAULT 0, service_center TEXT, handle_id INTEGER DEFAULT 0, subject TEXT, country TEXT, attributedBody BLOB, version INTEGER DEFAULT 0, type INTEGER DEFAULT 0, service TEXT, account TEXT, account_guid TEXT, error INTEGER DEFAULT 0, date INTEGER, date_read INTEGER, date_delivered INTEGER, is_delivered INTEGER DEFAULT 0, is_finished INTEGER DEFAULT 0, is_emote INTEGER DEFAULT 0, is_from_me INTEGER DEFAULT 0, is_empty INTEGER DEFAULT 0, is_delayed INTEGER DEFAULT 0, is_auto_reply INTEGER DEFAULT 0, is_prepared INTEGER DEFAULT 0, is_read INTEGER DEFAULT 0, is_system_message INTEGER DEFAULT 0, is_sent INTEGER DEFAULT 0, has_dd_results INTEGER DEFAULT 0, is_service_message INTEGER DEFAULT 0, is_forward INTEGER DEFAULT 0, was_downgraded INTEGER DEFAULT 0, is_archive INTEGER DEFAULT 0, cache_has_attachments INTEGER DEFAULT 0, cache_roomnames TEXT, was_data_detected INTEGER DEFAULT 0, was_deduplicated INTEGER DEFAULT 0)";
        queryExecutor.execute("SELECT sql FROM sqlite_master WHERE tbl_name = 'message' AND type = 'table'", new ResultSetProcessor() {
            public void process(ResultSet resultSet) throws SQLException {
                assertEquals(expectedResult, resultSet.getString(1));
            }
        });
    }
}
