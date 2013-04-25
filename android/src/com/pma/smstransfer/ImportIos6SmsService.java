package com.pma.smstransfer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pma.smstransfer.Transitions.openSmsInbox;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.regex.Pattern.quote;

public class ImportIos6SmsService extends IntentService {

    public static final String FILE_PATH = "file_path";

    private static final String TAG = ImportIos6SmsService.class.getSimpleName();
    private static final long IOS_YEAR_0 = 978307200000L; // iPhone timestamps are in seconds starting from January 1 2001.

    private final SmsRepository smsRepository;
    private final ExecutorService executorService;
    private final Handler handler;

    public ImportIos6SmsService() {
        super(ImportIos6SmsService.class.getName());
        this.smsRepository = new SmsRepository(this);
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(ImportIos6SmsService.this, format("Could not find: %s", filePath), Toast.LENGTH_SHORT).show();
                }
            });
            logIOException(e);
        } catch (IOException e) {
            logIOException(e);
        }
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
