package com.pma.smstransfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

public class HomeActivity extends Activity {

    private static final String DEFAULT_SMS_FILE_PATH = Environment.getExternalStorageDirectory() + "/sms.db";

    private EditText testMessage, smsFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        testMessage = (EditText) findViewById(R.id.testMessage);
        smsFilePath = (EditText) findViewById(R.id.smsFilePath);
        smsFilePath.setText(DEFAULT_SMS_FILE_PATH);
    }

    public void insertTestSms(View v) {
        Intent insertTestSms = new Intent(this, InsertTestSmsService.class);
        insertTestSms.putExtra(InsertTestSmsService.TEST_MESSAGE, testMessage.getText().toString());
        startService(insertTestSms);
    }

    public void importIos6Sms(View v) {
        //TODO: add file chooser
        Intent importIos6Sms = new Intent(this, ImportIos6SmsFromSqliteFileService.class);
        importIos6Sms.putExtra(ImportIos6SmsFromSqliteFileService.DB_FILE_PATH, smsFilePath.getText().toString());
        startService(importIos6Sms);
    }
}
