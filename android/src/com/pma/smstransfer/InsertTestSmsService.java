package com.pma.smstransfer;

import android.app.IntentService;
import android.content.Intent;

import java.util.Date;

public class InsertTestSmsService extends IntentService {

    private final SmsRepository smsRepository;

    public InsertTestSmsService() {
        super(InsertTestSmsService.class.getName());
        this.smsRepository = new SmsRepository(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        smsRepository.insertSms("0123456789", new Date().getTime(), Sms.MESSAGE_TYPE_SENT, "Hello World!");
    }
}
