/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.constants;

/**
 * Created by adammacdonald on 2/12/18.
 */

public class Constants {

    public static final String VALID_PHONE_SPECIAL_CHARACTERS = "+#;,.*";
    public static final String CALL_SETTINGS_INVALID_PHONE_SPECIAL_CHARACTERS = "#;,.*";

    public static final String EXTRA_CALL_LOG_ENTRY = "EXTRA_CALL_LOG_ENTRY";
    public static final String EXTRA_VOICEMAIL = "EXTRA_VOICEMAIL";
    public static final String EXTRA_CALL_DELETED = "EXTRA_CALL_DELETED";

    public static final String EXTRA_SERVICE_SETTINGS = "EXTRA_SERVICE_SETTINGS";
    public static final String EXTRA_OLD_PHONE_NUMBER = "EXTRA_OLD_PHONE_NUMBER";
    public static final String EXTRA_PHONE_NUMBER = "EXTRA_PHONE_NUMBER";
    public static final String EXTRA_CALL_SETTINGS_KEY = "EXTRA_CALL_SETTINGS_KEY";
    public static final String EXTRA_CALL_SETTINGS_VALUE = "EXTRA_CALL_SETTINGS_VALUE";
    public static final String EXTRA_NEXTIVA_CONTACT_JSON = "EXTRA_NEXTIVA_CONTACT_JSON";
    public static final String EXTRA_JID_LIST = "EXTRA_JID_LIST";
    public static final String EXTRA_SMS_LIST = "EXTRA_SMS_LIST";
    public static final String EXTRA_JID = "EXTRA_JID";
    public static final String EXTRA_VCARD = "EXTRA_VCARD";
    public static final String EXTRA_CALLED_NUMBER = "EXTRA_CALLED_NUMBER";
    public static final String EXTRA_OPENED_FROM_NOTIFICATION = "EXTRA_OPENED_FROM_NOTIFICATION";
    public static final String EXTRA_PREDIALED_PHONENUMBER = "EXTRA_PREDIALED_PHONE_NUMBER";
    public static final String EXTRA_INCOMING_CALL_INFO = "EXTRA_INCOMING_CALL_INFO";
    public static final String EXTRA_PUSH_NOTIFICATION_CALL_INFO = "EXTRA_PUSH_NOTIFICATION_CALL_INFO";
    public static final String EXTRA_MESSAGE_ID = "EXTRA_MESSAGE_ID";
    public static final String EXTRA_CHAT_SMS_INTENT = "EXTRA_CHAT_SMS_INTENT";

    public static final String EXTRA_ACTION = "EXTRA_ACTION";
    public static final String ACTION_TYPE_SAVED = "EXTRA_ACTION_SAVED";
    public static final String ACTION_TYPE_DELETED = "EXTRA_ACTION_DELETED";

    public static final String EXTRA_PARTICIPANT_INFO = "EXTRA_PARTICIPANT_INFO";
    public static final String EXTRA_RETRIEVAL_NUMBER = "EXTRA_RETRIEVAL_NUMBER";

    public static final String EXTRA_IS_INSTANT_MEETING = "EXTRA_IS_INSTANT_MEETING";
    public static final String EXTRA_MEETING_TITLE = "EXTRA_MEETING_TITLE";
    public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";
    public static final String EXTRA_MEETING_EVENT_ID = "EXTRA_MEETING_EVENT_ID";
    public static final String EXTRA_MEETING_START_TIME = "EXTRA_MEETING_START_TIME";
    public static final String EXTRA_MEETING_EMAIL = "EXTRA_MEETING_EMAIL";
    public static final String EXTRA_MEETING_FULL_NAME = "EXTRA_MEETING_FULL_NAME";
    public static final String EXTRA_MEETING_MEETING_DETAILS = "EXTRA_MEETING_MEETING_DETAILS";

    public static final String EXTRA_SCHEDULE = "EXTRA_SCHEDULE";

    public static final long ONE_SECOND_IN_MILLIS = 1000L;
    public static final long ONE_MINUTE_IN_MILLIS = ONE_SECOND_IN_MILLIS * 60L;
    public static final long ONE_HOUR_IN_MILLIS = ONE_MINUTE_IN_MILLIS * 60L;
    public static final long ONE_DAY_IN_MILLIS = ONE_HOUR_IN_MILLIS * 24L;

    public static final long DEFAULT_PHONE_NUMBER_LENGTH = 7;

    public static final long DEFAULT_API_TIMEOUT_MILLIS = 60 * ONE_MINUTE_IN_MILLIS;
    public static final long DEFAULT_XMPP_TIMEOUT_MILLIS = 10 * ONE_SECOND_IN_MILLIS;
    public static final long XMPP_REFRESH_ROSTER_TIMEOUT_MILLIS = 10 * ONE_SECOND_IN_MILLIS;
    public static final long DEFAULT_WAKE_LOCK_TIMEOUT = 8 * ONE_HOUR_IN_MILLIS;


    public static final int PRESENCE_BUSY_PRIORITY = 100;
    public static final int PRESENCE_ON_CALL_PRIORITY = 80;
    public static final int PRESENCE_AVAILABLE_PRIORITY = -5;
    public static final int PRESENCE_MOBILE_PRIORITY = -10;
    public static final int PRESENCE_AT_DESK_PRIORITY = -20;
    public static final int PRESENCE_AWAY_PRIORITY = -30;
    public static final int PRESENCE_OFFLINE_PRIORITY = -128;

    public static final String PRESENCE_STATUS_TEXT = "STATUS_TEXT";

    public static final String TCP = "TCP";
    public static final String UDP = "UDP";
    public static final String TLS = "TLS";
    public static final String TAB = "TAB";

    public static class Calls {
        public static final String PARAMS_PARTICIPANT_INFO = "PARAMS_PARTICIPANT_INFO";
        public static final String PARAMS_CONTACT = "PARAMS_CONTACT";
        public static final String PARAMS_DISPLAY_NAME = "PARAMS_DISPLAY_NAME";
        public static final String PARAMS_COUNTRY_CODE = "PARAMS_COUNTRY_CODE";
        public static final String PARAMS_PHONE_NUMBER = "PARAMS_PHONE_NUMBER";
        public static final String PARAMS_CALL_TYPE = "PARAMS_CALL_TYPE";
        public static final String PARAMS_INCOMING_CALL = "PARAMS_INCOMING_CALL";
        public static final String PARAMS_NEW_CALL_TYPE = "PARAMS_NEW_CALL_TYPE";
        public static final String PARAMS_RETRIEVAL_NUMBER = "PARAMS_RETRIEVAL_NUMBER";
        public static final String PARAMS_ANSWER_ACTION = "PARAMS_ANSWER_ACTION";
        public static final String PARAMS_CALL_STATE = "PARAMS_CALL_STATE";
        public static final String PARAMS_IS_NEXTIVA_CONNECT = "PARAMS_IS_NEXTIVA_CONNECT";
    }

    public static class Chats {
        public static final String PARAMS_CHAT_CONVERSATION = "PARAMS_CHAT_CONVERSATION";
        public static final String PARAMS_TO_JID = "PARAMS_TO_JID";
        public static final String PARAMS_PARTICIPANTS = "PARAMS_PARTICIPANTS";
        public static final String PARAMS_CHAT_TYPE = "CHAT_TYPE";
        public static final String PARAMS_JID_LIST = "PARAMS_JID_LIST";
        public static final String PARAMS_SMS_CONVERSATION_DETAILS = "PARAMS_SMS_CONVERSATION_DETAILS";
        public static final String PARAMS_PENDING_MESSAGE_DATA = "PARAMS_PENDING_MESSAGE_DATA";
        public static final String PARAMS_GROUP_VALUE = "PARAMS_GROUP_VALUE";
        public static final String PARAMS_GROUP_ID = "PARAMS_GROUP_ID";
        public static final String PARAMS_MESSAGE_ID = "PARAMS_MESSAGE_ID";
        public static final String PARAMS_PRESENCE = "PARAMS_PRESENCE";
        public static final String PARAMS_SHOULD_REFRESH_MESSAGES = "SHOULD_REFRESH_MESSAGES";
        public static final String PARAMS_DISPLAY_NAME = "PARAMS_DISPLAY_NAME";
        public static final String PARAMS_IS_CALL_OPTIONS_DISABLED = "PARAMS_IS_CALL_OPTIONS_DISABLED";
        public static final String PARAMS_IS_NEW_CHAT = "PARAMS_IS_NEW_CHAT";
        public static final String PARAMS_CHAT_SCREEN = "CHAT_SCREEN";
    }

    public static class Navigation {
        public static final String PARAMS_VIEW_TO_SHOW = "PARAMS_VIEW_TO_SHOW";
    }

    public static class MMS {
        public static final int MAX_FILE_SIZE = 1024 * 1024;
    }

    public static class SMS {
        public static final int MAX_SMS_RECIPIENT_COUNT = 10;
    }

    public static final String CONVERSATION_TYPE = "CONVERSATION_TYPE";
    public static final String CHAT_CONVERSATION_TYPE = "CHAT_TYPE";
    public static final String MESSAGE_CONVERSATION_TYPE = "MESSAGE_TYPE";

    public static final String TEMP_ATTACHMENT_MESSAGE_ID = "TEMP_MESSAGE_ID";

    public static final String CONNECT_MESSAGE_SENT_RESULT = "CONNECT_MESSAGE_SENT_RESULT";
    public static final String CONNECT_MESSAGE_SENT_BUNDLE = "CONNECT_MESSAGE_SENT_BUNDLE";


    public static class CharacterLimits {
        public static final int MINIMUM_PHONE_NUMBER_LENGTH = 10;
    }

    public static class Contacts {
        public static class Aliases {
            public static final String XBERT_ALIASES = "xbert";
        }
    }

    public static class ChromeOS{
        public static final String CHROME_OS_DEVICE_MANAGEMENT_FEATURE = "org.chromium.arc.device_management";
    }
}
