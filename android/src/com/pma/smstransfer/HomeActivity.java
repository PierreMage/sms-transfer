package com.pma.smstransfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    public void insertTestSms(View v) {
        Intent insertTestSms = new Intent(this, InsertTestSmsService.class);
        startService(insertTestSms);
    }

    public void importIos6Sms(View v) {
        //TODO: add file chooser
        Intent importIos6Sms = new Intent(this, ImportIos6SmsService.class);
        importIos6Sms.putExtra(ImportIos6SmsService.FILENAME, "sms.csv");
        startService(importIos6Sms);
    }
}
