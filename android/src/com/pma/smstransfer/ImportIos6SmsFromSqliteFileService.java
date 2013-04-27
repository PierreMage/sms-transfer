package com.pma.smstransfer;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import static com.pma.smstransfer.Transitions.openSmsInbox;

public class ImportIos6SmsFromSqliteFileService extends IntentService {

    public static final String DB_FILE_PATH = "db_file_path";

    private static final long IOS_YEAR_0 = 978307200000L; // iPhone timestamps are in seconds starting from January 1 2001.

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

    private final SmsRepository smsRepository;

    public ImportIos6SmsFromSqliteFileService() {
        super(ImportIos6SmsFromSqliteFileService.class.getName());
        this.smsRepository = new SmsRepository(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String dbFilePath = extras.getString(DB_FILE_PATH);
        importIos6Sms(dbFilePath);
        openSmsInbox(this);
    }

    private void importIos6Sms(String dbFilePath) {
        //TODO: PRAGMA journal_mode=DELETE
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor cursor = sqLiteDatabase.rawQuery(QUERY, null);
        //TODO: show progress
        while (cursor.moveToNext()) {
            String address = cursor.getString(0);
            long date = cursor.getLong(1) * 1000 + IOS_YEAR_0;
            int type = cursor.getInt(2) == 1 ? Sms.MESSAGE_TYPE_SENT : Sms.MESSAGE_TYPE_INBOX;
            String body = cursor.getString(3);
            smsRepository.insertSms(address, date, type, body);
        }
        cursor.close();
        sqLiteDatabase.close();
    }
}
