package com.pma.smstransfer;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.regex.Pattern.quote;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private static final Uri SMS_PROVIDER = Uri.parse("content://sms");
    private static final long IOS_YEAR_0 = 978307200000L; // iPhone timestamps are in seconds starting from January 1 2001.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    //TODO: move this in a service?
    public void test(View v) {
        insertSms("0123456789", new Date().getTime(), Sms.MESSAGE_TYPE_SENT, "Java Insert Sent");
    }

    //TODO: move this in a service?
    public void importIos6Sms(View v) throws IOException {
        String filename = "sms.csv";
        File smsFile = new File(Environment.getExternalStorageDirectory(), filename);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(smsFile));
        String line;
        try {
            line = bufferedReader.readLine();
            if (line == null) {
                Log.e(TAG, format("%s is empty", filename));
                return;
            }
            String[] metadata = line.split(" ");
            String separator = quote(metadata[0].split(":")[1]);
            String newline = quote(metadata[1].split(":")[1]);
            //TODO: show progress
            //String numberOfMessages = quote(metadata[2].split(":")[1]);
            while ((line = bufferedReader.readLine()) != null) {
                String[] smsFields = line.split(separator);
                try {
                    String address = smsFields[0];
                    long date = parseLong(smsFields[1]) * 1000 + IOS_YEAR_0;
                    int type = parseInt(smsFields[2]) == 1 ? Sms.MESSAGE_TYPE_SENT : Sms.MESSAGE_TYPE_INBOX;
                    String body = smsFields[3].replaceAll(newline, "\n");
                    insertSms(address, date, type, body);
                } catch (NumberFormatException e) {
                    Log.e(TAG, format("Could not insert %s: %s", line, e.getMessage()));
                }
            }
        } finally {
            bufferedReader.close();
        }
    }

    private void insertSms(String address, long date, int type, String body) {
        ContentValues values = new ContentValues();
        values.put(Sms.ADDRESS, address);
        values.put(Sms.DATE, date);
        values.put(Sms.READ, 1);
        values.put(Sms.STATUS, "-1");
        values.put(Sms.TYPE, type);
        values.put(Sms.BODY, body);
        values.put(Sms.SEEN, 1);
        // only restore inbox messages and sent messages - otherwise sms might get sent on restore
        if ((type == Sms.MESSAGE_TYPE_INBOX || type == Sms.MESSAGE_TYPE_SENT) && !smsExists(values)) {
            Uri uri = getContentResolver().insert(SMS_PROVIDER, values);
            if (uri != null) {
                Log.v(TAG, "inserted " + uri);
            }
        } else {
            Log.d(TAG, "ignoring sms");
        }
    }

    private boolean smsExists(ContentValues values) {
        // just assume equality on date+address+type
        Cursor c = getContentResolver().query(SMS_PROVIDER,
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
