package com.pma.smstransfer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.regex.Pattern.quote;

public class ImportIos6SmsService extends IntentService {

    public static final String FILENAME = "filename";

    private static final String TAG = ImportIos6SmsService.class.getSimpleName();
    private static final long IOS_YEAR_0 = 978307200000L; // iPhone timestamps are in seconds starting from January 1 2001.

    private final SmsRepository smsRepository;
    private final ExecutorService executorService;

    public ImportIos6SmsService() {
        super(ImportIos6SmsService.class.getName());
        this.smsRepository = new SmsRepository(this);
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String filename = extras.getString(FILENAME);
        try {
            importIos6Sms(filename);
        } catch (IOException e) {
            Log.e(TAG, format("Error while importing IOS 6 SMS: %s", e.getMessage()));
        }
    }

    private void importIos6Sms(String filename) throws IOException {
        File smsFile = new File(Environment.getExternalStorageDirectory(), filename);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(smsFile));
        String line;
        try {
            line = bufferedReader.readLine();
            if (line == null) {
                Log.e(TAG, format("%s is empty", filename));
                return;
            }
            Metadata metadata = parseMetadata(line);
            while ((line = bufferedReader.readLine()) != null) {
                executorService.execute(new InsertSmsRunnable(line, metadata.getSeparator(), metadata.getNewlineReplacement()));
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
            Log.e(TAG, format("Could not insert \"%s\": %s", line, e.getMessage()));
        } catch (ArrayIndexOutOfBoundsException e) { //TODO: some newline characters are not handled properly
            Log.e(TAG, format("Could not insert \"%s\": %s", line, e.getMessage()));
        }
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

    private class InsertSmsRunnable implements Runnable {
        private final String line;
        private final String separator;
        private final String newlineReplacement;

        public InsertSmsRunnable(String line, String separator, String newlineReplacement) {
            this.line = line;
            this.separator = separator;
            this.newlineReplacement = newlineReplacement;
        }

        public void run() {
            insertSms(line, separator, newlineReplacement);
        }
    }
}
