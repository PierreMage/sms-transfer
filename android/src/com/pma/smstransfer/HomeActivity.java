package com.pma.smstransfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

public class HomeActivity extends Activity {

    private EditText testMessage, smsFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        testMessage = (EditText) findViewById(R.id.testMessage);
        smsFilePath = (EditText) findViewById(R.id.smsFilePath);
        smsFilePath.setText(Environment.getExternalStorageDirectory() + "/sms.csv");
    }

    public void insertTestSms(View v) {
        Intent insertTestSms = new Intent(this, InsertTestSmsService.class);
        insertTestSms.putExtra(InsertTestSmsService.TEST_MESSAGE, testMessage.getText().toString());
        startService(insertTestSms);
    }

    public void importIos6Sms(View v) {
        //TODO: add file chooser
        Intent importIos6Sms = new Intent(this, ImportIos6SmsService.class);
        importIos6Sms.putExtra(ImportIos6SmsService.FILE_PATH, smsFilePath.getText().toString());
        startService(importIos6Sms);
    }
}
