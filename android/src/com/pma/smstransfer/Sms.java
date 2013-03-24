package com.pma.smstransfer;

public interface Sms {

    // _id, thread_id, person, date_sent, protocol (GSM?), reply_path_present (anonymous?), subject (MMS?), service_center, locked, error_code

    /**
     * Contact phone number.
     */
    String ADDRESS = "address";

    /**
     * Date received or date created?
     */
    String DATE = "date";

    String READ = "read";

    /**
     * Default seems to be -1.
     */
    String STATUS = "status";

    String TYPE = "type";

    /**
     * Text of the SMS.
     */
    String BODY = "body";

    String SEEN = "seen";

    int MESSAGE_TYPE_INBOX = 1;

    int MESSAGE_TYPE_SENT = 2;

    /*
    int MESSAGE_TYPE_ALL = 0;

    int MESSAGE_TYPE_DRAFT = 3;

    int MESSAGE_TYPE_OUTBOX = 4;

    int MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages

    int MESSAGE_TYPE_QUEUED = 6; // for messages to send later
    */
}
