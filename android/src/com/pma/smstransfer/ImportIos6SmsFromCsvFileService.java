package com.pma.smstransfer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.*;

import static com.pma.smstransfer.Transitions.openSmsInbox;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.regex.Pattern.quote;

@Deprecated
public class ImportIos6SmsFromCsvFileService extends IntentService {

    public static final String FILE_PATH = "file_path";

    private static final String TAG = ImportIos6SmsFromCsvFileService.class.getSimpleName();
    private static final long IOS_YEAR_0 = 978307200000L; // iPhone timestamps are in seconds starting from January 1 2001.

    private final SmsRepository smsRepository;
    private final Handler handler;

    public ImportIos6SmsFromCsvFileService() {
        super(ImportIos6SmsFromCsvFileService.class.getName());
        this.smsRepository = new SmsRepository(this);
        this.handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        final String filePath = extras.getString(FILE_PATH);
        try {
            importIos6Sms(filePath);
            openSmsInbox(this);
        } catch (FileNotFoundException e) {
            toast(format("Could not find: %s", filePath));
            logIOException(e);
        } catch (IOException e) {
            logIOException(e);
        }
    }

    private void toast(final String text) {
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(ImportIos6SmsFromCsvFileService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logIOException(IOException e) {
        Log.e(TAG, format("Error while importing IOS 6 SMS: %s", e.getMessage()));
    }

    private void importIos6Sms(String filePath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
        String line;
        try {
            line = bufferedReader.readLine();
            if (line == null) {
                Log.e(TAG, format("%s is empty", filePath));
                return;
            }
            Metadata metadata = parseMetadata(line);
            while ((line = bufferedReader.readLine()) != null) {
                insertSms(line, metadata.getSeparator(), metadata.getNewlineReplacement());
            }
        } finally {
            bufferedReader.close();
        }
    }

    private Metadata parseMetadata(String line) {
        String[] metadataArray = line.split(" ");
        String separator = quote(metadataArray[0].split(":")[1]);
        String newlineReplacement = quote(metadataArray[1].split(":")[1]);
        String numberOfMessages = quote(metadataArray[2].split(":")[1]); //TODO: show progress
        return new Metadata(separator, newlineReplacement, numberOfMessages);
    }

    private void insertSms(String line, String separator, String newlineReplacement) {
        String[] smsFields = line.split(separator);
        try {
            String address = smsFields[0];
            long date = parseLong(smsFields[1]) * 1000 + IOS_YEAR_0;
            int type = parseInt(smsFields[2]) == 1 ? Sms.MESSAGE_TYPE_SENT : Sms.MESSAGE_TYPE_INBOX;
            String body = smsFields[3].replaceAll(newlineReplacement, "\n");
            smsRepository.insertSms(address, date, type, body);
        } catch (NumberFormatException e) {
            logInsertException(line, e);
        } catch (ArrayIndexOutOfBoundsException e) { //TODO: some newline characters are not handled properly
            logInsertException(line, e);
        }
    }

    private void logInsertException(String line, Exception e) {
        Log.e(TAG, format("Could not insert \"%s\": %s", line, e.getMessage()));
    }

    private class Metadata {
        private final String separator;
        private final String newlineReplacement;
        private final String numberOfMessages;

        public Metadata(String separator, String newlineReplacement, String numberOfMessages) {
            this.separator = separator;
            this.newlineReplacement = newlineReplacement;
            this.numberOfMessages = numberOfMessages;
        }

        public String getSeparator() {
            return separator;
        }

        public String getNewlineReplacement() {
            return newlineReplacement;
        }

        public String getNumberOfMessages() {
            return numberOfMessages;
        }
    }
}
