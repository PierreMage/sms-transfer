package smstransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IosDbChecker {

    private static final String IOS6_CREATE_MESSAGE_TABLE_SQL = "CREATE TABLE message (ROWID INTEGER PRIMARY KEY AUTOINCREMENT, guid TEXT UNIQUE NOT NULL, text TEXT, replace INTEGER DEFAULT 0, service_center TEXT, handle_id INTEGER DEFAULT 0, subject TEXT, country TEXT, attributedBody BLOB, version INTEGER DEFAULT 0, type INTEGER DEFAULT 0, service TEXT, account TEXT, account_guid TEXT, error INTEGER DEFAULT 0, date INTEGER, date_read INTEGER, date_delivered INTEGER, is_delivered INTEGER DEFAULT 0, is_finished INTEGER DEFAULT 0, is_emote INTEGER DEFAULT 0, is_from_me INTEGER DEFAULT 0, is_empty INTEGER DEFAULT 0, is_delayed INTEGER DEFAULT 0, is_auto_reply INTEGER DEFAULT 0, is_prepared INTEGER DEFAULT 0, is_read INTEGER DEFAULT 0, is_system_message INTEGER DEFAULT 0, is_sent INTEGER DEFAULT 0, has_dd_results INTEGER DEFAULT 0, is_service_message INTEGER DEFAULT 0, is_forward INTEGER DEFAULT 0, was_downgraded INTEGER DEFAULT 0, is_archive INTEGER DEFAULT 0, cache_has_attachments INTEGER DEFAULT 0, cache_roomnames TEXT, was_data_detected INTEGER DEFAULT 0, was_deduplicated INTEGER DEFAULT 0)";

    public QueryExecutor check(final String dbFilePath) throws ClassNotFoundException {
        QueryExecutor queryExecutor = new QueryExecutor(dbFilePath);
        queryExecutor.execute("SELECT sql FROM sqlite_master WHERE tbl_name = 'message' AND type = 'table'", new ResultSetProcessor() {
            public void process(ResultSet resultSet) throws SQLException {
                if (!IOS6_CREATE_MESSAGE_TABLE_SQL.equals(resultSet.getString(1))) {
                    throw new IllegalArgumentException(String.format("%s does not seem to be an iOS 6 SMS db", dbFilePath));
                }
            }
        });
        return queryExecutor;
    }
}
