/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.constants;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

/**
 * Created by adammacdonald on 2/14/18.
 */

public class RequestCodes {

    public static final int CALL_PERMISSION_REQUEST_CODE = 1;
    public static final int CALL_DETAILS_REQUEST_CODE = 2;
    public static final int CONTACT_DETAILS_REQUEST_CODE = 3;
    public static final int ADD_CONTACT_REQUEST_CODE = 4;
    public static final int ADD_NEXTIVA_ANYWHERE_LOCATION_REQUEST_CODE = 5;
    public static final int SET_SERVICE_SETTINGS_REQUEST_CODE = 6;
    public static final int ADD_SIMULTANEOUS_RING_LOCATION_REQUEST_CODE = 8;
    public static final int EDIT_CONTACT_REQUEST_CODE = 9;
    public static final int CHAT_CONVERSATION_REQUEST_CODE = 10;
    public static final int PLACE_CALL_REQUEST_CODE = 11;
    public static final int CHAT_CONVERSATION_SHOULD_DISMISS_REQUEST_CODE = 12;
    public static final int APP_PREFERENCE_REQUEST_CODE = 13;
    public static final int DEVELOPER_MODE_ENABLED_REQUEST_CODE = 14;

    //Call Modification Request Codes
    public static class NewCall {
        public static final int NEW_CALL_NONE = 0;
        public static final int NEW_CALL_REQUEST_CODE = 101;
        public static final int TRANSFER_REQUEST_CODE = 102;
        public static final int CONFERENCE_REQUEST_CODE = 103;

        @Retention(SOURCE)
        @IntDef( {
                NEW_CALL_NONE,
                NEW_CALL_REQUEST_CODE,
                TRANSFER_REQUEST_CODE,
                CONFERENCE_REQUEST_CODE
        })
        public @interface NewCallType {
        }
    }

    public static final int ONGOING_CALL_NOTIFICATION = 1000;

    public static class OpenIdAuth{
        public static final int OPEN_ID_REQUEST_CODE_AUTH = 10000;
        public static final int OPEN_ID_REQUEST_CODE_AUTH_END_SESSION = 10001;
    }

    public static class AuthLoginRequest {
        public static final int AUTH_LOGIN_REQUEST_CODE = 10100;
        public static final int AUTH_LOGIN_REQUEST_CODE_END_SESSION = 10101;
    }

    //Message Attachment request Codes

    public static final int SMS_MESSAGE_IMAGE_ATTACHMENT = 2000;
    public static final int SMS_MESSAGE_AUDIO_ATTACHMENT = 2001;

}
