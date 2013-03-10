# Transfering iPhone SMS to Android

Only works for iOS 6 SMS.

## Tables: Colums
* attachment: ROWID, guid, created_date, start_date, filename, uti, mime_type, transfer_date, is_outgoing
* chat: ROWID, guid, style, state, account_id, properties, chat_identifier (phone number), service_name (SMS or iMessage), room_name, account_login, is_archived, last_address_handle 
* chat_handle_join: chat_id, handle_id
* chat_message_join: chat_id, message_id
* handle: ROWID, id (phone number), country, service (SMS or iMessage), uncanonicalized_id
* message: ROWID, guid, text, replace, service_center, handle_id, subject, country, attributedBody, version, type, service, account, account_guid, error, date, date_read, date_delivered, is_delivered, is_finished, is_emote, is_from_me, is_empty, is_delayed, is_auto_reply, is_prepared, is_read, is_system_message, is_forward, was_downgraded, is_archive, cache_has_attachments, cache_roomnames, was_data_detected, was_deduplicated
* message_attachment_join: message_id, attachment_id

## Useful links
* http://faked.org/isms2droid/
* https://github.com/toffer/iphone-sms-backup
* https://github.com/jberkel/sms-backup-plus