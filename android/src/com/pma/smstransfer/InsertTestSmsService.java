package com.pma.smstransfer;

import android.app.IntentService;
import android.content.Intent;

import java.util.Date;

import static com.pma.smstransfer.Transitions.openSmsInbox;

public class InsertTestSmsService extends IntentService {

    public static final String TEST_MESSAGE = "test_message";

    private final SmsRepository smsRepository;

    public InsertTestSmsService() {
        super(InsertTestSmsService.class.getName());
        this.smsRepository = new SmsRepository(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        smsRepository.insertSms("0123456789", new Date().getTime(), Sms.MESSAGE_TYPE_INBOX, intent.getStringExtra(TEST_MESSAGE), 0);
        openSmsInbox(this);
    }
}
