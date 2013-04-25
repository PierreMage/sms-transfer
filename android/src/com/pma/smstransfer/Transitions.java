package com.pma.smstransfer;

import android.content.Context;
import android.content.Intent;

public class Transitions {

    public static void openSmsInbox(Context context) {
        Intent openSmsInbox = new Intent(Intent.ACTION_MAIN);
        openSmsInbox.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openSmsInbox.setType("vnd.android-dir/mms-sms");
        context.startActivity(openSmsInbox);
    }
}
