package com.pma.smstransfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class HomeActivity extends Activity {

    private EditText testMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        testMessage = (EditText) findViewById(R.id.testMessage);
    }

    public void insertTestSms(View v) {
        Intent insertTestSms = new Intent(this, InsertTestSmsService.class);
        insertTestSms.putExtra(InsertTestSmsService.TEST_MESSAGE, testMessage.getText().toString());
        startService(insertTestSms);
        openSmsInbox();
    }

    public void importIos6Sms(View v) {
        //TODO: add file chooser
        Intent importIos6Sms = new Intent(this, ImportIos6SmsService.class);
        importIos6Sms.putExtra(ImportIos6SmsService.FILENAME, "sms.csv");
        startService(importIos6Sms);
        openSmsInbox();
    }

    private void openSmsInbox() {
        Intent openSmsInbox = new Intent(Intent.ACTION_MAIN);
        openSmsInbox.setType("vnd.android-dir/mms-sms");
        startActivity(openSmsInbox);
    }
}
