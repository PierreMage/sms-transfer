package com.pma.smstransfer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SmsRepository {

    private static final String TAG = SmsRepository.class.getSimpleName();

    private final Context context;

    public SmsRepository(Context context) {
        this.context = context;
    }

    public void insertSms(String address, long date, int type, String body) {
        insertSms(address, date, type, body, 1);
    }

    public void insertSms(String address, long date, int type, String body, int read) {
        ContentValues values = new ContentValues();
        values.put(Sms.ADDRESS, address);
        values.put(Sms.DATE, date);
        values.put(Sms.READ, read);
        values.put(Sms.STATUS, "-1");
        values.put(Sms.TYPE, type);
        values.put(Sms.BODY, body);
        values.put(Sms.SEEN, 1);
        // only restore inbox messages and sent messages - otherwise sms might get sent on restore
        if ((type == Sms.MESSAGE_TYPE_INBOX || type == Sms.MESSAGE_TYPE_SENT) && !smsExists(values)) {
            Uri uri = context.getContentResolver().insert(Sms.PROVIDER, values);
            if (uri != null) {
                Log.v(TAG, "inserted " + uri);
            }
        } else {
            Log.v(TAG, "ignoring sms");
        }
    }

    private boolean smsExists(ContentValues values) {
        // just assume equality on date + address + type
        Cursor c = context.getContentResolver().query(Sms.PROVIDER,
                new String[]{"_id"},
                "date = ? AND address = ? AND type = ?",
                new String[]{values.getAsString(Sms.DATE),
                        values.getAsString(Sms.ADDRESS),
                        values.getAsString(Sms.TYPE)},
                null
        );
        boolean exists = false;
        if (c != null) {
            exists = c.getCount() > 0;
            c.close();
        }
        return exists;
    }
}
