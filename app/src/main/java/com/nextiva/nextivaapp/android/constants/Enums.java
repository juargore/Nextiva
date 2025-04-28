/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.constants;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.media.ToneGenerator;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by adammacdonald on 2/9/18.
 */

public class Enums {

    public static class AccessDeviceTypes {

        public static final String TABLET = "Business Communicator - Tablet";
        public static final String MOBILE = "Business Communicator - Mobile";
        public static final String PC = "Business Communicator - PC";

        @Retention(SOURCE)
        @StringDef( {
                TABLET,
                MOBILE,
                PC
        })
        public @interface AccessDeviceType {
        }
    }

    // --------------------------------------------------------------------------------------------
    //region Patterns Enums
    // --------------------------------------------------------------------------------------------
    public static class TextPatterns {

        public static class Sip {
            public static class Messages {
                public static final String ASSERTED_IDENTITY = "(?<=Asserted-Identity: )(.*?)(?=>)";
                public static final String ASSERTED_IDENTITY_NAME = "(?<=Asserted-Identity: \")(.*?)(?=\")";
                public static final String ASSERTED_IDENTITY_PHONE = "(?<=sip:)(.*?)(?=\\@)";
                public static final String ATTRIBUTE = "(?<=a=)(.*?).*";
                public static final String CONTACT = "(?<=Contact: <sip:)(.*?)(?=@)";
                public static final String TO = "(?<=To: <sip:)(.*?)(?=\\@)";
                public static final String FROM = "(?<=From: \")(.*?)(?=\\\")";
                public static final String VIDEO = "m=video ";
                public static final String VIDEO_PORT = "(?<=m=video )[0-9]*";

                @Retention(SOURCE)
                @StringDef( {
                        ASSERTED_IDENTITY,
                        ASSERTED_IDENTITY_NAME,
                        ASSERTED_IDENTITY_PHONE,
                        ATTRIBUTE,
                        CONTACT,
                        TO,
                        FROM,
                        VIDEO,
                        VIDEO_PORT
                })
                public @interface SipMessage {
                }
            }

        }

    }

    // --------------------------------------------------------------------------------------------
    //endregion End Patterns Enums
    // --------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------
    //region Calls Enums
    // --------------------------------------------------------------------------------------------
    public static class Calls {
        public static class CallTypes {

            public static final String PLACED = "placed";
            public static final String RECEIVED = "received";
            public static final String MISSED = "missed";

            @Retention(SOURCE)
            @StringDef( {
                    PLACED,
                    RECEIVED,
                    MISSED
            })
            public @interface Type {
            }
        }

        public static class DetailViewTypes {

            public static final int DATETIME = 0;
            public static final int PHONE_NUMBER = 1;
            public static final int ADD_TO_CONTACTS = 2;
            public static final int ADD_TO_LOCAL_CONTACT = 3;
            public static final int SEND_PERSONAL_SMS = 4;

            @Retention(SOURCE)
            @IntDef( {
                    DATETIME,
                    PHONE_NUMBER,
                    ADD_TO_CONTACTS,
                    ADD_TO_LOCAL_CONTACT,
                    SEND_PERSONAL_SMS,
            })
            public @interface Type {
            }
        }
    }
    // --------------------------------------------------------------------------------------------
    //endregion Calls Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Call Settings Enums
    // -------------------------------------------------------------------------------------------
    public static class CallSettings {

        public static final String FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION = "FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION";
        public static final String FORM_TYPE_SIMULTANEOUS_RING_LOCATION = "FORM_TYPE_SIMULTANEOUS_RING_LOCATION";

        @Retention(SOURCE)
        @StringDef( {
                Service.TYPE_DO_NOT_DISTURB,
                Service.TYPE_CALL_FORWARDING_NOT_REACHABLE,
                Service.TYPE_CALL_FORWARDING_ALWAYS,
                Service.TYPE_CALL_FORWARDING_NO_ANSWER,
                Service.TYPE_CALL_FORWARDING_BUSY,
                Service.TYPE_REMOTE_OFFICE,
                Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING,
                SharedPreferencesManager.DIALING_SERVICE,
                SharedPreferencesManager.THIS_PHONE_NUMBER,
                FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION,
                FORM_TYPE_SIMULTANEOUS_RING_LOCATION
        })
        public @interface FormType {
        }

        public static class ErrorCodes {
            public static final String DUPLICATE_SIMULTANEOUS_RING_LOCATION_EXISTS = "8210";
            public static final String DUPLICATE_NEXTIVA_ANYWHERE_LOCATION_EXISTS = "8251";

            @Retention(SOURCE)
            @StringDef( {
                    DUPLICATE_SIMULTANEOUS_RING_LOCATION_EXISTS,
                    DUPLICATE_NEXTIVA_ANYWHERE_LOCATION_EXISTS
            })
            public @interface Type {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Call Settings Enums
    // -------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------
    //region AudioDevices Enums
    // --------------------------------------------------------------------------------------------
    public static class AudioDevices {
        public static enum AudioDevice {
            SPEAKER_PHONE,
            WIRED_HEADSET,
            EARPIECE,
            BLUETOOTH,
            NONE;

            private AudioDevice() {
            }
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface AUDIOCODEC {
        }
    }

    // --------------------------------------------------------------------------------------------
    //endregion AudioDevices Enums
    // --------------------------------------------------------------------------------------------



    // -------------------------------------------------------------------------------------------
    //region Contacts Enums
    // -------------------------------------------------------------------------------------------
    public static class Contacts {

        public static class DateType {
            public static final int BIRTH = 0;
            public static final int SIGN_UP = 1;
            public static final int NEXT_CONTACT = 2;
            public static final int CANCEL = 3;
            public static final int OTHER = 4;

            @Retention(SOURCE)
            @IntDef( {
                    BIRTH,
                    SIGN_UP,
                    NEXT_CONTACT,
                    CANCEL,
                    OTHER
            })
            public @interface Type {
            }
        }

        public static class SocialMediaType {
            public static final int LINKEDIN = 0;
            public static final int TWITTER = 1;
            public static final int FACEBOOK = 2;
            public static final int INSTAGRAM = 3;
            public static final int TELEGRAM = 4;
            public static final int OTHER = 5;

            @Retention(SOURCE)
            @IntDef( {
                    LINKEDIN,
                    TWITTER,
                    FACEBOOK,
                    INSTAGRAM,
                    TELEGRAM,
                    OTHER
            })
            public @interface Type {
            }
        }

        public static class AddressType {
            public static final int WORK = 0;
            public static final int HOME = 1;
            public static final int OTHER = 2;
            public static final int SHIPPING = 3;
            public static final int BILLING = 4;

            @Retention(SOURCE)
            @IntDef( {
                    WORK,
                    HOME,
                    OTHER,
                    SHIPPING,
                    BILLING
            })
            public @interface Type {
            }
        }

        public static class FilterTypes {

            public static final int ROSTER_ALL = 0;
            public static final int ROSTER_ONLINE = 1;
            public static final int LOCAL_ADDRESS_BOOK = 2;
            public static final int DIRECTORY = 3;

            @Retention(SOURCE)
            @IntDef( {
                    ROSTER_ALL,
                    ROSTER_ONLINE,
                    LOCAL_ADDRESS_BOOK,
                    DIRECTORY
            })
            public @interface Type {
            }
        }

        public static class ContactTypesValue {
            public static final String CONNECT_PERSONAL = "personal";
            public static final String CONNECT_SHARED = "business";
            public static final String CONNECT_CALL_CENTER = "callCenter";
            public static final String CONNECT_USER = "corporate";
            public static final String CONNECT_CALL_FLOW = "callFlow";
            public static final String CONNECT_TEAM = "team";
            public static final String CONNECT_UNKNOWN = "unknown";

            @Retention(SOURCE)
            @StringDef( {
                    CONNECT_PERSONAL,
                    CONNECT_SHARED,
                    CONNECT_CALL_CENTER,
                    CONNECT_USER,
                    CONNECT_CALL_FLOW,
                    CONNECT_TEAM,
                    CONNECT_UNKNOWN
            })
            public @interface Type {
            }
        }

        public static class ContactTypes {
            public static final int NONE = -1;
            public static final int ENTERPRISE = 0;
            public static final int LOCAL = 1;
            public static final int PERSONAL = 2;
            public static final int CONFERENCE = 3;
            public static final int UNKNOWN = 4;
            public static final int CONNECT_SHARED = 6;
            public static final int CONNECT_PERSONAL = 5;
            public static final int CONNECT_USER = 7;
            public static final int CONNECT_UNKNOWN = 8;
            public static final int CONNECT_CALL_FLOW = 9;
            public static final int CONNECT_TEAM = 10;
            public static final int CONNECT_CALL_CENTERS = 11;

            @Retention(SOURCE)
            @IntDef( {
                    NONE,
                    ENTERPRISE,
                    LOCAL,
                    PERSONAL,
                    CONFERENCE,
                    UNKNOWN,
                    CONNECT_PERSONAL,
                    CONNECT_SHARED,
                    CONNECT_USER,
                    CONNECT_UNKNOWN,
                    CONNECT_CALL_FLOW,
                    CONNECT_TEAM,
                    CONNECT_CALL_CENTERS
            })
            public @interface Type {
            }
        }

        public static class EmailTypes {
            public static final int EMAIL = -1;
            public static final int WORK_EMAIL = 0;
            public static final int HOME_EMAIL = 1;
            public static final int MOBILE_EMAIL = 2;
            public static final int ICLOUD_EMAIL = 3;
            public static final int OTHER_EMAIL = 4;
            public static final int CUSTOM_EMAIL = 5;
            public static final int CONNECT_PRIMARY_EMAIL = 6;
            public static final int CONNECT_SECONDARY_EMAIL = 7;

            @Retention(SOURCE)
            @IntDef( {
                    EMAIL,
                    WORK_EMAIL,
                    HOME_EMAIL,
                    MOBILE_EMAIL,
                    ICLOUD_EMAIL,
                    OTHER_EMAIL,
                    CUSTOM_EMAIL,
                    CONNECT_PRIMARY_EMAIL,
                    CONNECT_SECONDARY_EMAIL
            })
            public @interface Type {
            }
        }

        public static class PhoneTypes {

            public static final int PHONE = -1;
            public static final int WORK_PHONE = 0;
            public static final int WORK_EXTENSION = 1;
            public static final int WORK_MOBILE_PHONE = 2;
            public static final int WORK_PAGER = 3;
            public static final int WORK_FAX = 4;
            public static final int HOME_PHONE = 5;
            public static final int MOBILE_PHONE = 6;
            public static final int PAGER = 7;
            public static final int HOME_FAX = 8;
            public static final int MAIN_PHONE = 9;
            public static final int CONFERENCE_PHONE = 10;
            public static final int IPHONE = 11; //Not Used
            public static final int CUSTOM_PHONE = 12;
            public static final int OTHER_PHONE = 13;
            public static final int OTHER_FAX = 14;
            public static final int COMPANY_MAIN = 15;
            public static final int ASSISTANT = 16;
            public static final int CAR = 17;
            public static final int RADIO = 18;
            public static final int CALLBACK = 19;
            public static final int ISDN = 20;
            public static final int TELEX = 21;
            public static final int TTY_TDD = 22;
            public static final int MMS = 23;
            public static final int FAX = 24;
            public static final int ASSISTANT_PHONE = 25;

            @Retention(SOURCE)
            @IntDef( {
                    PHONE,
                    WORK_PHONE,
                    WORK_EXTENSION,
                    WORK_MOBILE_PHONE,
                    WORK_PAGER,
                    WORK_FAX,
                    HOME_PHONE,
                    MOBILE_PHONE,
                    PAGER,
                    HOME_FAX,
                    MAIN_PHONE,
                    CONFERENCE_PHONE,
                    IPHONE,
                    CUSTOM_PHONE,
                    OTHER_PHONE,
                    OTHER_FAX,
                    COMPANY_MAIN,
                    ASSISTANT,
                    CAR,
                    RADIO,
                    CALLBACK,
                    ISDN,
                    TELEX,
                    TTY_TDD,
                    MMS,
                    FAX,
                    ASSISTANT_PHONE
            })
            public @interface Type {
            }
        }

        public static class DetailViewTypes {
            public static final int FIRST_NAME = 0;
            public static final int LAST_NAME = 1;
            public static final int COMPANY = 2;
            public static final int PHONE_NUMBER = 3;
            public static final int EMAIL_ADDRESS = 4;
            public static final int IM_ADDRESS = 5;
            public static final int ADD_TO_CONTACTS = 6;
            public static final int ADD_TO_LOCAL_CONTACT = 7;
            public static final int SEND_PERSONAL_SMS = 8;
            public static final int EXTENSION = 9;
            public static final int CONFERENCE_PHONE_NUMBER = 10;
            public static final int FAVORITE = 11;
            public static final int ADD_TO_GROUP = 12;
            public static final int REMOVE_FROM_GROUP = 13;

            @Retention(SOURCE)
            @IntDef( {
                    FIRST_NAME,
                    LAST_NAME,
                    COMPANY,
                    PHONE_NUMBER,
                    EMAIL_ADDRESS,
                    IM_ADDRESS,
                    ADD_TO_CONTACTS,
                    ADD_TO_LOCAL_CONTACT,
                    SEND_PERSONAL_SMS,
                    EXTENSION,
                    CONFERENCE_PHONE_NUMBER,
                    FAVORITE,
                    ADD_TO_GROUP,
                    REMOVE_FROM_GROUP
            })
            public @interface Type {
            }
        }

        public static class ConnectPresenceStates {
            public static final int AUTOMATIC = 0;
            public static final int ONLINE = 1;
            public static final int ACTIVE = 2;
            public static final int DND = 3;
            public static final int AWAY = 4;
            public static final int BUSY = 5;
            public static final int BE_RIGHT_BACK = 6;
            public static final int OFFLINE = 7;
            public static final int OUT_OF_OFFICE = 8;

            @Retention(SOURCE)
            @IntDef( {
                    AUTOMATIC,
                    ONLINE,
                    ACTIVE,
                    DND,
                    AWAY,
                    BUSY,
                    BE_RIGHT_BACK,
                    OFFLINE,
                    OUT_OF_OFFICE
            })
            public @interface PresenceState {
            }
        }

        public static class PresenceStates {
            public static final int NONE = -1;
            public static final int AVAILABLE = 0;
            public static final int AWAY = 1;
            public static final int BUSY = 2;
            public static final int PENDING = 3;
            public static final int OFFLINE = 4;
            public static final int CONNECT_AUTOMATIC = 5;
            public static final int CONNECT_ONLINE = 6;
            public static final int CONNECT_ACTIVE = 7;
            public static final int CONNECT_DND = 8;
            public static final int CONNECT_AWAY = 9;
            public static final int CONNECT_BUSY = 10;
            public static final int CONNECT_BE_RIGHT_BACK = 11;
            public static final int CONNECT_OFFLINE = 12;
            public static final int CONNECT_OUT_OF_OFFICE = 13;

            @Retention(SOURCE)
            @IntDef( {
                    NONE,
                    AVAILABLE,
                    AWAY,
                    BUSY,
                    PENDING,
                    OFFLINE,
                    CONNECT_AUTOMATIC,
                    CONNECT_ONLINE,
                    CONNECT_ACTIVE,
                    CONNECT_DND,
                    CONNECT_AWAY,
                    CONNECT_BUSY,
                    CONNECT_BE_RIGHT_BACK,
                    CONNECT_OFFLINE,
                    CONNECT_OUT_OF_OFFICE
            })
            public @interface PresenceState {
            }
        }

        public static class BroadsoftPresenceState {
            public static final String AVAILABLE = "available";
            public static final String AWAY = "away";
            public static final String BUSY = "dnd";
            public static final String OFFLINE = "offline";

            @Retention(SOURCE)
            @StringDef( {
                    AVAILABLE,
                    AWAY,
                    BUSY,
                    OFFLINE
            })
            public @interface PresenceState {
            }
        }

        public static class PresenceStateText {
            public static final String ONLINE = "Online";
            public static final String AVAILABLE = "Available";
            public static final String AWAY = "Away";
            public static final String BUSY = "Busy";
            public static final String CALL = "Call";
            public static final String PENDING = "Pending";
            public static final String OFFLINE = "Offline";
            public static final String DND = "Do not disturb";
            public static final String ON_A_CALL = "On a call";
            public static final String ON_CALL = "onCall";

            @Retention(SOURCE)
            @StringDef( {
                    ONLINE,
                    AVAILABLE,
                    AWAY,
                    BUSY,
                    CALL,
                    PENDING,
                    OFFLINE,
                    DND,
                    ON_A_CALL
            })
            public @interface PresenceState {
            }
        }

        public static class PresenceTypes {
            public static final int NONE = -1;
            public static final int UNAVAILABLE = 0;
            public static final int AVAILABLE = 1;

            @Retention(SOURCE)
            @IntDef( {
                    NONE,
                    AVAILABLE,
                    UNAVAILABLE
            })
            public @interface Type {
            }
        }

        public static class UpdateActions {
            public static final int ADD_EXISTING_CONTACT = 0;
            public static final int ADD_NEW_CONTACT = 1;
            public static final int REMOVE = 2;
            public static final int EDIT = 3;

            @Retention(SOURCE)
            @IntDef( {
                    ADD_EXISTING_CONTACT,
                    ADD_NEW_CONTACT,
                    REMOVE,
                    EDIT
            })
            public @interface UpdateAction {
            }
        }

        public static class SubscriptionStates {
            public static final int SUBSCRIBED = 0;
            public static final int PENDING = 1;
            public static final int UNSUBSCRIBED = 2;

            @Retention(SOURCE)
            @IntDef( {
                    SUBSCRIBED,
                    PENDING,
                    UNSUBSCRIBED
            })
            public @interface SubscriptionState {
            }
        }

        public static class CacheTypes {
            public static final int ALL_ROSTER = 0;
            public static final int ONLINE_ROSTER = 1;
            public static final int LOCAL = 2;
            public static final int ENTERPRISE = 3;
            public static final int CONNECT = 4;

            @Retention(SOURCE)
            @IntDef( {
                    ALL_ROSTER,
                    ONLINE_ROSTER,
                    LOCAL,
                    ENTERPRISE,
                    CONNECT
            })
            public @interface Type {
            }
        }

        public static class PhoneCacheTypes {

            public static final String PHONE = "phone";
            public static final String EXTENSION = "extension";
            public static final String CONFERENCE = "conference";

            @Retention(SOURCE)
            @StringDef( {
                    PHONE,
                    EXTENSION,
                    CONFERENCE
            })
            public @interface Type {
            }
        }

        public static class BundleContactConstants {

            public static final String IS_SHARED = "is_shared";

            @Retention(SOURCE)
            @StringDef( {
                    IS_SHARED
            })
            public @interface Type {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Contacts Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Chats Enums
    // -------------------------------------------------------------------------------------------
    public static class Chats {

        public static class ConversationTypes {
            public static final String CHAT = "chat";
            public static final String GROUP_CHAT = "groupchat";
            public static final String GROUP_ALIAS = "groupalias";
            public static final String GROUP_BROADCAST = "groupbroadcast";
            public static final String GROUP_ALERT = "groupalert";
            public static final String SMS = "sms";

            @Retention(SOURCE)
            @StringDef( {
                    CHAT,
                    GROUP_CHAT,
                    GROUP_ALIAS,
                    GROUP_BROADCAST,
                    GROUP_ALERT,
                    SMS
            })
            public @interface Type {
            }
        }

        public static class MessageBubbleTypes {

            public static final int START = 0;
            public static final int MIDDLE = 1;
            public static final int END = 2;
            public static final int SINGLE = 3;

            @Retention(SOURCE)
            @IntDef( {
                    START,
                    MIDDLE,
                    END,
                    SINGLE
            })
            public @interface Type {
            }
        }

        public static class States {
            public static final String ACTIVE = "active";
            public static final String COMPOSING = "composing";
            public static final String PAUSED = "paused";
            public static final String INACTIVE = "inactive";
            public static final String GONE = "gone";

            @Retention(SOURCE)
            @StringDef( {
                    ACTIVE,
                    COMPOSING,
                    PAUSED,
                    INACTIVE,
                    GONE
            })
            public @interface State {
            }

        }

        public static class SentStatus {
            public static final int PENDING = 0;
            public static final int FAILED = 1;
            public static final int SUCCESSFUL = 2;

            @Retention(SOURCE)
            @IntDef( {
                    PENDING,
                    FAILED,
                    SUCCESSFUL
            })

            public @interface Status {
            }

        }

        public static class ChatScreens {
            public static final String PARTICIPANT = "participant";
            public static final String CONVERSATION = "conversation";

            @Retention(SOURCE)
            @StringDef( {
                    PARTICIPANT,
                    CONVERSATION
            })
            public @interface ChatScreen {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Chats Enums
    // -------------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------------
    //region SMSMessage Enums
    // -------------------------------------------------------------------------------------------
    public static class SMSMessages {

        public static class ConversationTypes {
            public static final int SELF_MESSAGE_PARTICIPANT_COUNT = 0;
            public static final int MESSAGE_CONVERSATION_SINGLE_PARTICIPANT_COUNT = 1;
            public static final int MESSAGE_GROUP_CONVERSATION_MINIMUM_PARTICIPANT_COUNT = 2;


            @Retention(SOURCE)
            @IntDef( {
                    SELF_MESSAGE_PARTICIPANT_COUNT,
                    MESSAGE_CONVERSATION_SINGLE_PARTICIPANT_COUNT,
                    MESSAGE_GROUP_CONVERSATION_MINIMUM_PARTICIPANT_COUNT
            })
            public @interface Type {
            }
        }


        public static class SentStatus {
            public static final int PENDING = 0;
            public static final int FAILED = 1;
            public static final int SUCCESSFUL = 2;
            public static final int DRAFT = 3;

            @Retention(SOURCE)
            @IntDef( {
                    PENDING,
                    FAILED,
                    SUCCESSFUL,
                    DRAFT
            })

            public @interface Status {
            }
        }

        public static class ReadStatus {
            public static final String READ = "READ";
            public static final String UNREAD = "UNREAD";

            @Retention(SOURCE)
            @StringDef( {
                    READ,
                    UNREAD
            })

            public @interface Status {
            }
        }

        public static class FooterMessageType {
            public static final String ADMIN = "ADMIN";
            public static final String USER = "USER";
            public static final String ADMIN_AND_USER = "ADMIN_AND_USER";
            public static final String UNKNOWN = "UNKNOWN";

            @Retention(SOURCE)
            @StringDef( {
                    ADMIN,
                    USER,
                    ADMIN_AND_USER,
                    UNKNOWN
            })

            public @interface FooterType {
            }
        }

        public static class ExternalSourceType {
            public static final String BROADSOFT = "broadsoft";
            public static final String CALL_CENTER = "callCenter";
            public static final String CALL_FLOW = "callFlow";
            public static final String CSV = "csv";
            public static final String GMAIL = "gmail";
            public static final String MOBILE = "mobile";
            public static final String NATIVE_GOOGLE = "native-google";
            public static final String NATIVE_MS365 = "native-ms36";
            public static final String NEXTOS_CRM = "nextos_crm";
            public static final String O365 = "o365";
            public static final String TEAM = "team";
            public static final String USER = "user";

            @Retention(SOURCE)
            @StringDef( {
                    BROADSOFT,
                    CALL_CENTER,
                    CALL_FLOW,
                    CSV,
                    GMAIL,
                    MOBILE,
                    NATIVE_GOOGLE,
                    NATIVE_MS365,
                    NEXTOS_CRM,
                    O365,
                    TEAM,
                    USER
            })

            public @interface ExternalSrcType {
            }
        }

        public enum SMSCampaignStatus {
            ACCEPTED,
            BANNED,
            DECLINED,
            DELETED,
            NOT_STARTED,
            NOT_SUBMITTED,
            PENDING,
            PENDING_BRAND_RETRY,
            PENDING_DCA_COMPLETE,
            SOLE_PROPRIETOR,
            UNKNOWN;

            public static boolean isPending(String status) {
                if (status == null) return false;

                String normalizedStatus = status.trim().toUpperCase().replace("\"", "");
                return normalizedStatus.equals(PENDING.name()) ||
                        normalizedStatus.equals(PENDING_BRAND_RETRY.name()) ||
                        normalizedStatus.equals(PENDING_DCA_COMPLETE.name()) ||
                        normalizedStatus.equals(SOLE_PROPRIETOR.name());
            }

            public static boolean isNotStarted(String status) {
                if (status == null) return false;

                String normalizedStatus = status.trim().toUpperCase().replace("\"", "");
                return normalizedStatus.equals(NOT_STARTED.name()) ||
                        normalizedStatus.equals(NOT_SUBMITTED.name()) ||
                        normalizedStatus.equals(DECLINED.name());
            }

            public static boolean isAccepted(String status) {
                if (status == null) return false;

                String normalizedStatus = status.trim().toUpperCase().replace("\"", "");
                return normalizedStatus.equals(ACCEPTED.name());
            }
        }

    }

    // -------------------------------------------------------------------------------------------
    //endregion End SMSMessage Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region XMPP Enums
    // -------------------------------------------------------------------------------------------
    public static class Xmpp {

        public static final int XMPP_ERROR_NOT_CONNECTED = 0;

        @Retention(SOURCE)
        @IntDef( {
                XMPP_ERROR_NOT_CONNECTED
        })
        public @interface XmppErrorEvents {
        }

        public static class SessionStates {

            public static final int NONE = -1;
            public static final int LOGGED_IN = 0;
            public static final int LOGGED_OUT = 1;

            @Retention(SOURCE)
            @IntDef( {
                    NONE,
                    LOGGED_IN,
                    LOGGED_OUT
            })
            public @interface SessionState {
            }
        }

        public static class ConnectionStates {
            public static final int NONE = -1;
            public static final int CONNECTED = 0;
            public static final int CONNECTING = 1;
            public static final int DISCONNECTING = 2;
            public static final int DISCONNECTED = 3;

            @Retention(SOURCE)
            @IntDef( {
                    NONE,
                    CONNECTED,
                    CONNECTING,
                    DISCONNECTING,
                    DISCONNECTED
            })
            public @interface ConnectionState {
            }
        }

        public static class RosterEntries {

            public static final int ADDED_EVENT = 0;
            public static final int UPDATED_EVENT = 1;
            public static final int DELETED_EVENT = 2;

            @Retention(SOURCE)
            @IntDef( {
                    ADDED_EVENT,
                    UPDATED_EVENT,
                    DELETED_EVENT
            })
            public @interface RosterEntriesEvent {
            }
        }
    }

    // -------------------------------------------------------------------------------------------
    //endregion End XMPP Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region SIP Enums
    // -------------------------------------------------------------------------------------------
    public static class Sip {
        public static class CallTypes {
            public static final int NONE = 0;
            public static final int VOICE = 1;
            public static final int VIDEO = 2;

            @Retention(SOURCE)
            @IntDef( {
                    NONE,
                    VOICE,
                    VIDEO
            })
            public @interface Type {
            }
        }

        public static class CameraTypes {
            public static final int FRONT_CAMERA = 1;
            public static final int REAR_CAMERA = 2;

            @Retention(SOURCE)
            @IntDef( {
                    FRONT_CAMERA,
                    REAR_CAMERA
            })
            public @interface Type {
            }
        }
//
//        public static class ResponseCodes
//        {
//                static final int TRYING = 100;
//                static final int RINGING = 180;
//                static final int CALL_IS_BEING_FORWARDED = 181;
//                static final int QUEUED = 182;
//                static final int SESSION_PROGRESS = 183;
//                static final int EARLY_DIALOG_TERMINATED = 199;
//
//                @Retention(SOURCE)
//                @IntDef( {
//                        TRYING,
//                        RINGING,
//                        CALL_IS_BEING_FORWARDED,
//                        QUEUED,
//                        SESSION_PROGRESS,
//                        EARLY_DIALOG_TERMINATED
//                })
//                public @interface ProvisionalResponses {
//                }
//
//
//            static final int TRYING = 200;
//
//            @Retention(SOURCE)
//            @IntDef( {
//                    TRYING
//            })
//            public @interface SuccessfulResponses {
//            }
//        }

        public static class CallTones {
            public static final int RINGBACK = ToneGenerator.TONE_SUP_RINGTONE;
            public static final int CALL_WAITING = ToneGenerator.TONE_SUP_CALL_WAITING;
            public static final int BUSY = ToneGenerator.TONE_SUP_BUSY;

            @Retention(SOURCE)
            @IntDef( {
                    RINGBACK,
                    BUSY,
                    CALL_WAITING
            })
            public @interface CallTone {
            }

            public static class ToneTypes {
                public static final int RING = 0;
                public static final int NOTIFICATION = 1;

                @Retention(SOURCE)
                @IntDef( {
                        RING,
                        NOTIFICATION
                })
                public @interface Type {
                }
            }

        }

        public static class ReliableProvisional {
            // Disable PRACK,By default the PRACK is disabled.
            public static final int NEVER = 0;
            // Only send reliable provisionals if sending a body and far end supports.
            public static final int SUPPORTED_ESSENTIAL = 1;
            // Always send reliable provisionals if far end supports.
            public static final int SUPPORTED = 2;
            // Always send reliable provisionals.
            public static final int REQUIRED = 3;
            @Retention(SOURCE)
            @IntDef({
                    NEVER,
                    SUPPORTED_ESSENTIAL,
                    SUPPORTED,
                    REQUIRED
            })
            public @interface Supported {}
        }

        public static class SipMessages {

            public static class SipMessageStates {
                public static final String SENDRECV = "sendrecv";

                @Retention(SOURCE)
                @StringDef( {
                        SENDRECV
                })
                public @interface MessageStates {
                }
            }

        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End SIP Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Media Enums
    // -------------------------------------------------------------------------------------------
    public static class Media {

        public static class Video {
            public static class Resolutions {

                public static final String QCIF = "QCIF";
                public static final String VGA = "VGA";
                public static final String v720P = "720P";
                public static final String v1080P = "1080P";
                public static final String CIF = "CIF";

                @Retention(SOURCE)
                @StringDef( {
                        QCIF,
                        VGA,
                        v720P,
                        v1080P,
                        CIF
                })
                public @interface Resolution {
                }
            }

            public static class Codecs {

                public static final String H264 = "H264";
                public static final String I420 = "I420";
                public static final String VP8 = "VP8";
                public static final String VP9 = "VP9";

                @Retention(SOURCE)
                @StringDef( {
                        H264,
                        I420,
                        VP8,
                        VP9
                })
                public @interface Codec {
                }
            }
        }

        public static class Audio {
            public static class Codecs {

                public static final String G722 = "G722";
                public static final String PCMU = "PCMU";
                public static final String PCMA = "PCMA";
                public static final String G729 = "G729";
                public static final String OPUS = "OPUS";
                public static final String AMR = "AMR";
                public static final String AMRWB = "AMRWB";
                public static final String DTMF = "DTMF";
                public static final String GSM = "GSM";
                public static final String ILBC = "ILBC";
                public static final String ISACSWB = "ISACSWB";
                public static final String ISACWB = "ISACWB";
                public static final String SPEEX = "SPEEX";
                public static final String SPEEXWB = "SPEEXWB";

                @Retention(SOURCE)
                @StringDef( {
                        G722,
                        PCMU,
                        PCMA,
                        G729,
                        OPUS,
                        AMR,
                        AMRWB,
                        DTMF,
                        GSM,
                        ILBC,
                        ISACSWB,
                        ISACWB,
                        SPEEX,
                        SPEEXWB
                })
                public @interface Codec {
                }
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Media Enums
    // -------------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------------
    //region Notifications Enums
    // -------------------------------------------------------------------------------------------
    public static class Notification {
        @Retention(SOURCE)
        @StringDef( {
                GroupIDs.SIP_ERRORS
        })
        public @interface GroupID {
        }

        public static class TypeIDs {
            public static final int ON_CALL = 1;
            public static final int INCOMING_CALL = 2;
            public static final int SIP_TEST = 3;
            public static final int SIP_TEST_ERRORS = 4;
            public static final int RING_SPLASH = 5;
            public static final int ONGOING_LOCAL_CALL = 6;
            public static final int TOTAL_UNREAD = 7;
            public static final int MWI_NOTIFICATION_ID = 65780;
            public static final int NEW_CHAT_MESSAGE_NOTIFICATION_ID = 65781;
            public static final int MISSED_CALL_NOTIFICATION_ID = 65782;
            public static final int FAILED_CHAT_MESSAGE_NOTIFICATION_ID = 65783;
            public static final int NEW_SMS_NOTIFICATION_ID = 65784;
            public static final int PRESENCE_STATUS_UPDATED_NOTIFICATION_ID = 65785;
            public static final int SIP_AUDIO_STATS = 100;
            public static final int SIP_VIDEO_STATS = 101;
            public static final int CALL_ISSUE_NOTIFICATION = 102;

            @Retention(SOURCE)
            @IntDef( {
                    ON_CALL,
                    INCOMING_CALL,
                    SIP_TEST,
                    SIP_TEST_ERRORS,
                    RING_SPLASH,
                    ONGOING_LOCAL_CALL,
                    MWI_NOTIFICATION_ID,
                    NEW_CHAT_MESSAGE_NOTIFICATION_ID,
                    MISSED_CALL_NOTIFICATION_ID,
                    FAILED_CHAT_MESSAGE_NOTIFICATION_ID,
                    SIP_AUDIO_STATS,
                    SIP_VIDEO_STATS,
                    NEW_SMS_NOTIFICATION_ID,
                    CALL_ISSUE_NOTIFICATION

            })
            public @interface Type {

            }
        }

        public static class ChannelIDs {
            public static final String CALL = "Call";
            public static final String CALL_QUALITY = "Call Quality";
            public static final String INCOMING_CALLS = "Incoming Calls";
            public static final String RING_SPLASH = "Ring Splash";
            public static final String SIP_TEST = "Sip Test";
            public static final String SIP_TEST_ERROR = "Sip Test Error";
            public static final String CHAT = "Chat Messages";
            public static final String VOICEMAIL = "Voicemail";
            public static final String FAILED_CHAT_MESSAGE = "Failed Chat Message";
            public static final String SMS = "SMS";
            public static final String PRESENCE = "Presence";
            public static final String CALL_QUALITY_SILENT = "Call Quality Silent";
            public static final String RING_SPLASH_SILENT = "Ring Splash Silent";
            public static final String INCOMING_CALLS_SILENT = "Incoming Calls Silent";
            public static final String CHAT_SILENT = "Chat Silent";
            public static final String VOICEMAIL_SILENT = "Voicemail Silent";
            public static final String SMS_SILENT = "Sms Silent";

            @Retention(SOURCE)
            @StringDef( {
                    CALL,
                    CALL_QUALITY,
                    INCOMING_CALLS,
                    RING_SPLASH,
                    SIP_TEST,
                    SIP_TEST_ERROR,
                    CHAT,
                    VOICEMAIL,
                    FAILED_CHAT_MESSAGE,
                    SMS,
                    PRESENCE,
                    CALL_QUALITY_SILENT,
                    RING_SPLASH_SILENT,
                    INCOMING_CALLS_SILENT,
                    CHAT_SILENT,
                    VOICEMAIL_SILENT,
                    SMS_SILENT
            })
            public @interface Type {

            }
        }

        public static class GroupIDs {
            public static final String SIP_ERRORS = "Sip_Errors";
            public static final String GROUPING_KEY = "Grouping_Key";
        }

    }
    // -------------------------------------------------------------------------------------------
    //endregion End Notifications Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region UMS Enums
    // -------------------------------------------------------------------------------------------
    public static class Ums {

        public static final String SUCCESS_PUT = "2000001";
        public static final String SUCCESS_DELETE = "0000003";
        public static final String SUCCESS_GET = "0000004";
        public static final String SUCCESS_GET_NO_RESULT = "0000005";
        public static final String FAILURE_GENERAL = "1000001";
        public static final String FAILURE_UNAUTHENTICATED = "2000004";
        public static final String FAILURE_USER_CREATION_ERROR = "2000006";

        @Retention(SOURCE)
        @StringDef( {
                SUCCESS_PUT,
                SUCCESS_DELETE,
                SUCCESS_GET,
                SUCCESS_GET_NO_RESULT,
                FAILURE_GENERAL,
                FAILURE_UNAUTHENTICATED,
                FAILURE_USER_CREATION_ERROR
        })
        public @interface StatusCodeType {
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End UMS Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Services Enums
    // -------------------------------------------------------------------------------------------
    public static class Service {


        public static final String TYPE_ANONYMOUS_CALL_REJECTION = "Anonymous Call Rejection";
        public static final String TYPE_AUTHENTICATION = "Authentication";
        public static final String TYPE_CALL_FORWARDING_ALWAYS = "Call Forwarding Always";
        public static final String TYPE_CALL_FORWARDING_BUSY = "Call Forwarding Busy";
        public static final String TYPE_CALL_FORWARDING_NO_ANSWER = "Call Forwarding No Answer";
        public static final String TYPE_CALL_NOTIFY = "Call Notify";
        public static final String TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING = "Calling Line ID Delivery Blocking";
        public static final String TYPE_ALLOW_TERMINATION = "Allow Termination to this Location";
        public static final String TYPE_COMMPILOT_EXPRESS = "CommPilot Express";
        public static final String TYPE_COMMPILOT_CALL_MANAGER = "CommPilot Call Manager";
        public static final String TYPE_DO_NOT_DISTURB = "Do Not Disturb";
        public static final String TYPE_INTERCEPT_USER = "Intercept User";
        public static final String TYPE_LAST_NUMBER_REDIAL = "Last Number Redial";
        public static final String TYPE_OUTLOOK_INTEGRATION = "Outlook Integration";
        public static final String TYPE_PRIORITY_ALERT = "Priority Alert";
        public static final String TYPE_CALL_RETURN = "Call Return";
        public static final String TYPE_REMOTE_OFFICE = "Remote Office";
        public static final String TYPE_SELECTIVE_CALL_ACCEPTANCE = "Selective Call Acceptance";
        public static final String TYPE_CALL_FORWARDING_SELECTIVE = "Call Forwarding Selective";
        public static final String TYPE_SELECTIVE_CALL_REJECTION = "Selective Call Rejection";
        public static final String TYPE_SIMULTANEOUS_RING_PERSONAL = "Simultaneous Ring Personal";
        public static final String TYPE_VOICE_MESSAGING_USER = "Voice Messaging User";
        public static final String TYPE_ALTERNATE_NUMBERS = "Alternate Numbers";
        public static final String TYPE_SPEED_DIAL_8 = "Speed Dial 8";
        public static final String TYPE_CUSTOMER_ORIGINATED_TRACE = "Customer Originated Trace";
        public static final String TYPE_ATTENDANT_CONSOLE = "Attendant Console";
        public static final String TYPE_CLIENT_CALL_CONTROL = "Client Call Control";
        public static final String TYPE_SHARED_CALL_APPEARANCE_5 = "Shared Call Appearance 5";
        public static final String TYPE_SHARED_CALL_APPEARANCE_35 = "Shared Call Appearance 35";
        public static final String TYPE_CALLING_NAME_RETRIEVAL = "Calling Name Retrieval";
        public static final String TYPE_FLASH_CALL_HOLD = "Flash Call Hold";
        public static final String TYPE_SPEED_DIAL_100 = "Speed Dial 100";
        public static final String TYPE_DIRECTED_CALL_PICKUP = "Directed Call Pickup";
        public static final String TYPE_DIRECTED_CALL_PICKUP_WITH_BARGE_IN = "Directed Call Pickup with Barge-in";
        public static final String TYPE_VOICE_PORTAL_CALLING = "Voice Portal Calling";
        public static final String TYPE_EXTERNAL_CALLING_LINE_ID_DELIVERY = "External Calling Line ID Delivery";
        public static final String TYPE_INTERNAL_CALLING_LINE_ID_DELIVERY = "Internal Calling Line ID Delivery";
        public static final String TYPE_AUTOMATIC_CALLBACK = "Automatic Callback";
        public static final String TYPE_CALL_WAITING = "Call Waiting";
        public static final String TYPE_CALLING_PARTY_CATEGORY = "Calling Party Category";
        public static final String TYPE_PUSH_TO_TALK = "Push to Talk";
        public static final String TYPE_BASIC_CALL_LOGS = "Basic Call Logs";
        public static final String TYPE_ENHANCED_CALL_LOGS = "Enhanced Call Logs";
        public static final String TYPE_HOTELING_HOST = "Hoteling Host";
        public static final String TYPE_HOTELING_GUEST = "Hoteling Guest";
        public static final String TYPE_VOICE_MESSAGING_USER_VIDEO = "Voice Messaging User - Video";
        public static final String TYPE_DIVERSION_INHIBITOR = "Diversion Inhibitor";
        public static final String TYPE_MULTIPLE_CALL_ARRANGEMENT = "Multiple Call Arrangement";
        public static final String TYPE_CUSTOM_RINGBACK_USER = "Custom Ringback User";
        public static final String TYPE_CUSTOM_RINGBACK_USER_VIDEO = "Custom Ringback User - Video";
        public static final String TYPE_AUTOMATIC_HOLD_RETRIEVE = "Automatic Hold/Retrieve";
        public static final String TYPE_BUSY_LAMP_FIELD = "Busy Lamp Field";
        public static final String TYPE_THREE_WAY_CALL = "Three-Way Call";
        public static final String TYPE_CALL_TRANSFER = "Call Transfer";
        public static final String TYPE_PRIVACY = "Privacy";
        public static final String TYPE_CHARGE_NUMBER = "Charge Number";
        public static final String TYPE_N_WAY_CALL = "N-Way Call";
        public static final String TYPE_TWO_STAGE_DIALING = "Two-Stage Dialing";
        public static final String TYPE_CALL_FORWARDING_NOT_REACHABLE = "Call Forwarding Not Reachable";
        public static final String TYPE_MWI_DELIVERY_TO_MOBILE_ENDPOINT = "MWI Delivery to Mobile Endpoint";
        public static final String TYPE_EXTERNAL_CUSTOM_RINGBACK = "External Custom Ringback";
        public static final String TYPE_IN_CALL_SERVICE_ACTIVATION = "In-Call Service Activation";
        public static final String TYPE_CONNECTED_LINE_IDENTIFICATION_PRESENTATION = "Connected Line Identification Presentation";
        public static final String TYPE_CONNECTED_LINE_IDENTIFICATION_RESTRICTION = "Connected Line Identification Restriction";
        public static final String TYPE_BROADWORKS_ANYWHERE = "BroadWorks Anywhere";
        public static final String TYPE_POLYCOM_PHONE_SERVICES = "Polycom Phone Services";
        public static final String TYPE_CUSTOM_RINGBACK_USER_CALL_WAITING = "Custom Ringback User - Call Waiting";
        public static final String TYPE_VIDEO_ON_HOLD_USER = "Video On Hold User";
        public static final String TYPE_COMMUNICATION_BARRING_USER_CONTROL = "Communication Barring User-Control";
        public static final String TYPE_CLASSMARK = "Classmark";
        public static final String TYPE_CALLING_NAME_DELIVERY = "Calling Name Delivery";
        public static final String TYPE_CALLING_NUMBER_DELIVERY = "Calling Number Delivery";
        public static final String TYPE_VIRTUAL_ON_NET_ENTERPRISE_EXTENSIONS = "Virtual On-Net Enterprise Extensions";
        public static final String TYPE_PRE_ALERTING_ANNOUNCEMENT = "Pre-alerting Announcement";
        public static final String TYPE_CALL_CENTER_MONITORING = "Call Center Monitoring";
        public static final String TYPE_LOCATION_BASED_CALLING_RESTRICTIONS = "Location-Based Calling Restrictions";
        public static final String TYPE_CALL_ME_NOW = "Call Me Now";
        public static final String TYPE_INTEGRATED_IMP = "Integrated IMP";
        public static final String TYPE_GROUP_NIGHT_FORWARDING = "Group Night Forwarding";
        public static final String TYPE_BROADTOUCH_BUSINESS_COMMUNICATOR_TABLET_VIDEO = "BroadTouch Business Communicator Tablet - Video";
        public static final String TYPE_CLIENT_LICENSE_17 = "Client License 17";
        public static final String TYPE_CLIENT_LICENSE_18 = "Client License 18";
        public static final String TYPE_SEQUENTIAL_RING = "Sequential Ring";

        @Retention(SOURCE)
        @StringDef( {
                TYPE_ANONYMOUS_CALL_REJECTION,
                TYPE_AUTHENTICATION,
                TYPE_CALL_FORWARDING_ALWAYS,
                TYPE_CALL_FORWARDING_BUSY,
                TYPE_CALL_FORWARDING_NO_ANSWER,
                TYPE_CALL_NOTIFY,
                TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING,
                TYPE_COMMPILOT_EXPRESS,
                TYPE_COMMPILOT_CALL_MANAGER,
                TYPE_DO_NOT_DISTURB,
                TYPE_INTERCEPT_USER,
                TYPE_LAST_NUMBER_REDIAL,
                TYPE_OUTLOOK_INTEGRATION,
                TYPE_PRIORITY_ALERT,
                TYPE_CALL_RETURN,
                TYPE_REMOTE_OFFICE,
                TYPE_SELECTIVE_CALL_ACCEPTANCE,
                TYPE_CALL_FORWARDING_SELECTIVE,
                TYPE_SELECTIVE_CALL_REJECTION,
                TYPE_SIMULTANEOUS_RING_PERSONAL,
                TYPE_VOICE_MESSAGING_USER,
                TYPE_ALTERNATE_NUMBERS,
                TYPE_SPEED_DIAL_8,
                TYPE_CUSTOMER_ORIGINATED_TRACE,
                TYPE_ATTENDANT_CONSOLE,
                TYPE_CLIENT_CALL_CONTROL,
                TYPE_SHARED_CALL_APPEARANCE_5,
                TYPE_SHARED_CALL_APPEARANCE_35,
                TYPE_CALLING_NAME_RETRIEVAL,
                TYPE_FLASH_CALL_HOLD,
                TYPE_SPEED_DIAL_100,
                TYPE_DIRECTED_CALL_PICKUP,
                TYPE_DIRECTED_CALL_PICKUP_WITH_BARGE_IN,
                TYPE_VOICE_PORTAL_CALLING,
                TYPE_EXTERNAL_CALLING_LINE_ID_DELIVERY,
                TYPE_INTERNAL_CALLING_LINE_ID_DELIVERY,
                TYPE_AUTOMATIC_CALLBACK,
                TYPE_CALL_WAITING,
                TYPE_CALLING_PARTY_CATEGORY,
                TYPE_PUSH_TO_TALK,
                TYPE_BASIC_CALL_LOGS,
                TYPE_ENHANCED_CALL_LOGS,
                TYPE_HOTELING_HOST,
                TYPE_HOTELING_GUEST,
                TYPE_VOICE_MESSAGING_USER_VIDEO,
                TYPE_DIVERSION_INHIBITOR,
                TYPE_MULTIPLE_CALL_ARRANGEMENT,
                TYPE_CUSTOM_RINGBACK_USER,
                TYPE_CUSTOM_RINGBACK_USER_VIDEO,
                TYPE_AUTOMATIC_HOLD_RETRIEVE,
                TYPE_BUSY_LAMP_FIELD,
                TYPE_THREE_WAY_CALL,
                TYPE_CALL_TRANSFER,
                TYPE_PRIVACY,
                TYPE_CHARGE_NUMBER,
                TYPE_N_WAY_CALL,
                TYPE_TWO_STAGE_DIALING,
                TYPE_CALL_FORWARDING_NOT_REACHABLE,
                TYPE_MWI_DELIVERY_TO_MOBILE_ENDPOINT,
                TYPE_EXTERNAL_CUSTOM_RINGBACK,
                TYPE_IN_CALL_SERVICE_ACTIVATION,
                TYPE_CONNECTED_LINE_IDENTIFICATION_PRESENTATION,
                TYPE_CONNECTED_LINE_IDENTIFICATION_RESTRICTION,
                TYPE_BROADWORKS_ANYWHERE,
                TYPE_POLYCOM_PHONE_SERVICES,
                TYPE_CUSTOM_RINGBACK_USER_CALL_WAITING,
                TYPE_VIDEO_ON_HOLD_USER,
                TYPE_COMMUNICATION_BARRING_USER_CONTROL,
                TYPE_CLASSMARK,
                TYPE_ALLOW_TERMINATION,
                TYPE_CALLING_NAME_DELIVERY,
                TYPE_CALLING_NUMBER_DELIVERY,
                TYPE_VIRTUAL_ON_NET_ENTERPRISE_EXTENSIONS,
                TYPE_PRE_ALERTING_ANNOUNCEMENT,
                TYPE_CALL_CENTER_MONITORING,
                TYPE_LOCATION_BASED_CALLING_RESTRICTIONS,
                TYPE_CALL_ME_NOW,
                TYPE_INTEGRATED_IMP,
                TYPE_GROUP_NIGHT_FORWARDING,
                TYPE_BROADTOUCH_BUSINESS_COMMUNICATOR_TABLET_VIDEO,
                TYPE_CLIENT_LICENSE_17,
                TYPE_CLIENT_LICENSE_18,
                TYPE_SEQUENTIAL_RING
        })
        public @interface Type {
        }

        public static class FeatureAccessCodes {
            public static final String DIRECT_VOICE_MAIL_TRANSFER = "Direct Voice Mail Transfer";
            public static final String CALL_FORWARDING_NOT_REACHABLE_DEACTIVATION = "Call Forwarding Not Reachable Deactivation";
            public static final String CALL_BRIDGE = "Call Bridge";
            public static final String CALLING_LINE_ID_DELIVERY_PER_CALL = "Calling Line ID Delivery per Call";
            public static final String CALL_PARK = "Call Park";
            public static final String GROUP_CALL_PARK = "Group Call Park";
            public static final String CONNECTED_LINE_IDENTIFICATION_RESTRICTION_INTERROGATION = "Connected Line Identification Restriction Interrogation";
            public static final String VOICE_MAIL_CLEAR_MWI = "Voice Mail Clear MWI";
            public static final String SILENT_MONITORING = "Silent Monitoring";
            public static final String SPEED_DIAL_100 = "Speed Dial 100";
            public static final String CANCEL_CALL_WAITING = "Cancel Call Waiting";
            public static final String CALL_RETURN = "Call Return";
            public static final String CALL_FORWARDING_NO_ANSWER_TO_VOICE_MAIL_DEACTIVATION = "Call Forwarding No Answer To Voice Mail Deactivation";
            public static final String MONITORING_NEXT_CALL = "Monitoring Next Call";
            public static final String CALL_FORWARDING_BUSY_ACTIVATION = "Call Forwarding Busy Activation";
            public static final String DO_NOT_DISTURB_ACTIVATION = "Do Not Disturb Activation";
            public static final String SPEED_DIAL_8 = "Speed Dial 8";
            public static final String ANONYMOUS_CALL_REJECTION_DEACTIVATION = "Anonymous Call Rejection Deactivation";
            public static final String LOCATION_CONTROL_DEACTIVATION = "Location Control Deactivation";
            public static final String CALLING_LINE_ID_DELIVERY_BLOCKING_INTERROGATION = "Calling Line ID Delivery Blocking Interrogation";
            public static final String CALLING_LINE_ID_DELIVERY_BLOCKING_PERSISTENT_ACTIVATION = "Calling Line ID Delivery Blocking Persistent Activation";
            public static final String COMMUNICATION_BARRING_USER_CONTROL_ACTIVATION = "Communication Barring User-Control Activation";
            public static final String CALL_WAITING_PERSISTENT_DEACTIVATION = "Call Waiting Persistent Deactivation";
            public static final String CALLING_LINE_ID_DELIVERY_BLOCKING_PERSISTENT_DEACTIVATION = "Calling Line ID Delivery Blocking Persistent Deactivation";
            public static final String NO_ANSWER_TIMER = "No Answer Timer";
            public static final String CALL_FORWARDING_ALWAYS_TO_VOICE_MAIL_ACTIVATION = "Call Forwarding Always To Voice Mail Activation";
            public static final String CALL_FORWARDING_NO_ANSWER_INTERROGATION = "Call Forwarding No Answer Interrogation";
            public static final String LAST_NUMBER_REDIAL = "Last Number Redial";
            public static final String CALL_FORWARDING_BUSY_INTERROGATION = "Call Forwarding Busy Interrogation";
            public static final String PER_CALL_ACCOUNT_CODE = "Per-Call Account Code";
            public static final String CALL_FORWARDING_NO_ANSWER_ACTIVATION = "Call Forwarding No Answer Activation";
            public static final String CALL_FORWARDING_BUSY_TO_VOICEMAIL_ACTIVATION = "Call Forwarding Busy To Voicemail Activation";
            public static final String AUTOMATIC_CALLBACK_MENU_ACCESS = "Automatic Callback Menu Access";
            public static final String ANONYMOUS_CALL_REJECTION_INTERROGATION = "Anonymous Call Rejection Interrogation";
            public static final String CALLING_LINE_ID_DELIVERY_BLOCKING_PER_CALL = "Calling Line ID Delivery Blocking per Call";
            public static final String COMMUNICATION_BARRING_USER_CONTROL_DEACTIVATION = "Communication Barring User-Control Deactivation";
            public static final String CALL_RETURN_NUMBER_DELETION = "Call Return Number Deletion";
            public static final String CALL_FORWARDING_ALWAYS_ACTIVATION = "Call Forwarding Always Activation";
            public static final String CALL_FORWARDING_NOT_REACHABLE_INTERROGATION = "Call Forwarding Not Reachable Interrogation";
            public static final String HUNT_GROUP_BUSY_INTERROGATION = "Hunt Group Busy Interrogation";
            public static final String CUSTOMER_ORIGINATED_TRACE = "Customer Originated Trace";
            public static final String VOICE_MAIL_RETRIEVAL = "Voice Mail Retrieval";
            public static final String DIRECTED_CALL_PICKUP = "Directed Call Pickup";
            public static final String CALL_FORWARDING_BUSY_TO_VOICE_MAIL_DEACTIVATION = "Call Forwarding Busy To Voice Mail Deactivation";
            public static final String CALL_FORWARDING_NO_ANSWER_DEACTIVATION = "Call Forwarding No Answer Deactivation";
            public static final String SELECTIVE_CALL_FORWARDING_ACTIVATION = "Selective Call Forwarding Activation";
            public static final String SELECTIVE_CALL_REJECTION_INTERROGATION = "Selective Call Rejection Interrogation";
            public static final String ANONYMOUS_CALL_REJECTION_ACTIVATION = "Anonymous Call Rejection Activation";
            public static final String CALL_FORWARDING_ALWAYS_INTERROGATION = "Call Forwarding Always Interrogation";
            public static final String HUNT_GROUP_BUSY_DEACTIVATION = "Hunt Group Busy Deactivation";
            public static final String CALL_RETRIEVE = "Call Retrieve";
            public static final String HUNT_GROUP_BUSY_ACTIVATION = "Hunt Group Busy Activation";
            public static final String LOCATION_CONTROL_ACTIVATION = "Location Control Activation";
            public static final String SELECTIVE_CALL_FORWARDING_DEACTIVATION = "Selective Call Forwarding Deactivation";
            public static final String FLASH_CALL_HOLD = "Flash Call Hold";
            public static final String CALL_WAITING_INTERROGATION = "Call Waiting Interrogation";
            public static final String CALL_FORWARDING_ALWAYS_TO_VOICE_MAIL_DEACTIVATION = "Call Forwarding Always To Voice Mail Deactivation";
            public static final String DIVERSION_INHIBITOR = "Diversion Inhibitor";
            public static final String VOICE_PORTAL_ACCESS = "Voice Portal Access";
            public static final String CALL_PARK_RETRIEVE = "Call Park Retrieve";
            public static final String CALL_WAITING_PERSISTENT_ACTIVATION = "Call Waiting Persistent Activation";
            public static final String DO_NOT_DISTURB_DEACTIVATION = "Do Not Disturb Deactivation";
            public static final String PUSH_NOTIFICATION_RETRIEVAL = "Push Notification Retrieval";
            public static final String AUTOMATIC_CALLBACK_DEACTIVATION = "Automatic Callback Deactivation";
            public static final String PUSH_TO_TALK = "Push to Talk";
            public static final String MUSIC_ON_HOLD_PER_CALL_DEACTIVATION = "Music On Hold Per-Call Deactivation";
            public static final String CALL_FORWARDING_ALWAYS_DEACTIVATION = "Call Forwarding Always Deactivation";
            public static final String CALL_PICKUP = "Call Pickup";
            public static final String CALL_FORWARDING_BUSY_DEACTIVATION = "Call Forwarding Busy Deactivation";
            public static final String COMMUNICATION_BARRING_USER_CONTROL_QUERY = "Communication Barring User-Control Query";
            public static final String DIRECTED_CALL_PICKUP_WITH_BARGE_IN = "Directed Call Pickup with Barge-in";
            public static final String BROADWORKS_ANYWHERE_E164_DIALING = "Broadworks Anywhere E164 Dialing";
            public static final String CALL_FORWARDING_NOT_REACHABLE_ACTIVATION = "Call Forwarding Not Reachable Activation";
            public static final String CALL_FORWARDING_NO_ANSWER_TO_VOICE_MAIL_ACTIVATION = "Call Forwarding No Answer To Voice Mail Activation";

            @Retention(SOURCE)
            @StringDef( {
                    DIRECT_VOICE_MAIL_TRANSFER,
                    CALL_FORWARDING_NOT_REACHABLE_DEACTIVATION,
                    CALL_BRIDGE,
                    CALLING_LINE_ID_DELIVERY_PER_CALL,
                    CALL_PARK,
                    GROUP_CALL_PARK,
                    CONNECTED_LINE_IDENTIFICATION_RESTRICTION_INTERROGATION,
                    VOICE_MAIL_CLEAR_MWI,
                    SILENT_MONITORING,
                    SPEED_DIAL_100,
                    CANCEL_CALL_WAITING,
                    CALL_RETURN,
                    CALL_FORWARDING_NO_ANSWER_TO_VOICE_MAIL_DEACTIVATION,
                    MONITORING_NEXT_CALL,
                    CALL_FORWARDING_BUSY_ACTIVATION,
                    DO_NOT_DISTURB_ACTIVATION,
                    SPEED_DIAL_8,
                    ANONYMOUS_CALL_REJECTION_DEACTIVATION,
                    LOCATION_CONTROL_DEACTIVATION,
                    CALLING_LINE_ID_DELIVERY_BLOCKING_INTERROGATION,
                    CALLING_LINE_ID_DELIVERY_BLOCKING_PERSISTENT_ACTIVATION,
                    COMMUNICATION_BARRING_USER_CONTROL_ACTIVATION,
                    CALL_WAITING_PERSISTENT_DEACTIVATION,
                    CALLING_LINE_ID_DELIVERY_BLOCKING_PERSISTENT_DEACTIVATION,
                    NO_ANSWER_TIMER,
                    CALL_FORWARDING_ALWAYS_TO_VOICE_MAIL_ACTIVATION,
                    CALL_FORWARDING_NO_ANSWER_INTERROGATION,
                    LAST_NUMBER_REDIAL,
                    CALL_FORWARDING_BUSY_INTERROGATION,
                    PER_CALL_ACCOUNT_CODE,
                    CALL_FORWARDING_NO_ANSWER_ACTIVATION,
                    CALL_FORWARDING_BUSY_TO_VOICEMAIL_ACTIVATION,
                    AUTOMATIC_CALLBACK_MENU_ACCESS,
                    ANONYMOUS_CALL_REJECTION_INTERROGATION,
                    CALLING_LINE_ID_DELIVERY_BLOCKING_PER_CALL,
                    COMMUNICATION_BARRING_USER_CONTROL_DEACTIVATION,
                    CALL_RETURN_NUMBER_DELETION,
                    CALL_FORWARDING_ALWAYS_ACTIVATION,
                    CALL_FORWARDING_NOT_REACHABLE_INTERROGATION,
                    HUNT_GROUP_BUSY_INTERROGATION,
                    CUSTOMER_ORIGINATED_TRACE,
                    VOICE_MAIL_RETRIEVAL,
                    DIRECTED_CALL_PICKUP,
                    CALL_FORWARDING_BUSY_TO_VOICE_MAIL_DEACTIVATION,
                    CALL_FORWARDING_NO_ANSWER_DEACTIVATION,
                    SELECTIVE_CALL_FORWARDING_ACTIVATION,
                    SELECTIVE_CALL_REJECTION_INTERROGATION,
                    ANONYMOUS_CALL_REJECTION_ACTIVATION,
                    CALL_FORWARDING_ALWAYS_INTERROGATION,
                    HUNT_GROUP_BUSY_DEACTIVATION,
                    CALL_RETRIEVE,
                    HUNT_GROUP_BUSY_ACTIVATION,
                    LOCATION_CONTROL_ACTIVATION,
                    SELECTIVE_CALL_FORWARDING_DEACTIVATION,
                    FLASH_CALL_HOLD,
                    CALL_WAITING_INTERROGATION,
                    CALL_FORWARDING_ALWAYS_TO_VOICE_MAIL_DEACTIVATION,
                    DIVERSION_INHIBITOR,
                    VOICE_PORTAL_ACCESS,
                    CALL_PARK_RETRIEVE,
                    CALL_WAITING_PERSISTENT_ACTIVATION,
                    DO_NOT_DISTURB_DEACTIVATION,
                    PUSH_NOTIFICATION_RETRIEVAL,
                    AUTOMATIC_CALLBACK_DEACTIVATION,
                    PUSH_TO_TALK,
                    MUSIC_ON_HOLD_PER_CALL_DEACTIVATION,
                    CALL_FORWARDING_ALWAYS_DEACTIVATION,
                    CALL_PICKUP,
                    CALL_FORWARDING_BUSY_DEACTIVATION,
                    COMMUNICATION_BARRING_USER_CONTROL_QUERY,
                    DIRECTED_CALL_PICKUP_WITH_BARGE_IN,
                    BROADWORKS_ANYWHERE_E164_DIALING,
                    CALL_FORWARDING_NOT_REACHABLE_ACTIVATION,
                    CALL_FORWARDING_NO_ANSWER_TO_VOICE_MAIL_ACTIVATION
            })
            public @interface FeatureAccessCode {
            }
        }


        public static class DialingServiceTypes {
            public static final int NONE = -1;
            public static final int VOIP = 0;
            public static final int CALL_BACK = 1;
            public static final int CALL_THROUGH = 2;
            public static final int THIS_PHONE = 3;
            public static final int ALWAYS_ASK = 4;

            @Retention(SOURCE)
            @IntDef( {
                    NONE,
                    VOIP,
                    CALL_BACK,
                    CALL_THROUGH,
                    THIS_PHONE,
                    ALWAYS_ASK
            })
            public @interface DialingServiceType {
            }
        }

        public static class CallCenterServiceStatuses {
            public static final String NONE = "";
            public static final String SIGN_IN = "Sign-In";
            public static final String AVAILABLE = "Available";
            public static final String UNAVAILABLE = "Unavailable";
            public static final String WRAP_UP = "Wrap-Up";
            public static final String SIGN_OUT = "Sign-Out";

            @Retention(SOURCE)
            @StringDef( {
                    NONE,
                    SIGN_IN,
                    AVAILABLE,
                    UNAVAILABLE,
                    WRAP_UP,
                    SIGN_OUT
            })
            public @interface CallCenterServiceStatus {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Services Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Logging Enums
    // -------------------------------------------------------------------------------------------
    public static class Logging {

        public final static String STATE_INFO = "INFO";
        public final static String STATE_FAILURE = "FAILURE";
        public final static String STATE_ERROR = "ERROR";

        @Retention(SOURCE)
        @StringDef( {
                STATE_INFO,
                STATE_FAILURE,
                STATE_ERROR
        })
        public @interface StateType {
        }

        public static class KibanaStateTypes {

            public final static String STATE_TRACE = "TRACE";
            public final static String STATE_DEBUG = "DEBUG";
            public final static String STATE_INFO = "INFO";
            public final static String STATE_WARN = "WARN";
            public final static String STATE_ERROR = "ERROR";
            public final static String STATE_FATAL = "FATAL";

            @Retention(SOURCE)
            @StringDef( {
                    STATE_TRACE,
                    STATE_DEBUG,
                    STATE_INFO,
                    STATE_WARN,
                    STATE_ERROR,
                    STATE_FATAL
            })
            public @interface KibanaStateType {
            }

        }

        public static class UserDatas {

            public final static String LOGGED_IN = "LOGGED IN";
            public final static String DEVICE_URL = "DEVICE URL";
            public final static String MOBILE_CONFIG_URL = "MOBILE CONFIG URL";
            public final static String IS_AUTH_USERNAME = "IS_AUTH_USERNAME";
            public final static String IS_AUTH_PASSWORD = "IS AUTH PASSWORD";
            public final static String IS_AUTH_DEVICE = "IS AUTH DEVICE";
            public final static String IN_CALL = "IN CALL";
            public final static String CONFERENCE_CALL = "CONFERENCE CALL";
            public final static String SMS_ENABLED = "SMS ENABLED";
            public final static String VOICE_TRANSCRIPT_ENABLED = "VOICE TRANSCRIPT ENABLED";
            public final static String USER_PRESENCE_AUTOMATIC = "USER PRESENCE AUTOMATIC";
            public final static String INTERNET_CONNECTED = "INTERNET CONNECTED";
            public final static String LAST_SIP_REGISTRATION_SUCCESSFUL = "LAST SIP REGISTRATION SUCCESSFUL";
            public final static String SIP_CONNECTED = "SIP CONNECTED";
            public final static String UMS_CONNECTED = "UMS CONNECTED";
            public final static String XMPP_CONNECTED = "XMPP CONNECTED";
            public final static String CALL_CENTER_STATUS = "CALL CENTER STATUS";
            public final static String CRASH_REPORTING = "CRASH REPORTING";
            public final static String ENABLE_LOGGING = "ENABLE LOGGING";
            public final static String FILE_LOGGING = "FILE LOGGING";
            public final static String SIP_LOGGING = "SIP LOGGING";
            public final static String XMPP_LOGGING = "XMPP LOGGING";
            public final static String NIGHT_MODE = "NIGHT MODE";
            public final static String CORP_ACCOUNT_NUMBER = "CORP ACCOUNT NUMBER";
            public final static String OKTA_USER_UUID = "OKTA USER UUID";
            public final static String IS_PLATFORM_USER = "IS PLATFORM USER";
            public final static String IS_SHOW_SMS = "IS SHOW SMS";
            public final static String IS_SMS_ENABLED = "IS SMS ENABLED";
            public final static String IS_MOBILE_TEAMCHAT_ENABLED = "IS MOBILE TEAMCHAT ENABLED";
            public final static String IS_SMS_LICENSE_ENABLED = "IS SMS LICENSE ENABLED";
            public final static String IS_SMS_PROVISIONING_ENABLED = "IS SMS PROVISIONING ENABLED";
            public final static String IS_CONNECT_USER_PRESENCE_AUTOMATIC = "IS CONNECT USER PRESENCE AUTOMATIC";
            public final static String IS_NEXTIVA_CONNECT_ENABLED = "IS NEXTIVA CONNECT ENABLED";
            public final static String BATTERY_OPTIMIZATION_ENABLED = "BATTERY OPTIMIZATION ENABLED";
            public final static String POWER_SAVING_MODE_ENABLED = "POWER SAVING MODE ENABLED";
            public final static String DEVICE_IDLE_MODE = "DEVICE IDLE MODE";
            public final static String THERMAL_STATUS = "THERMAL STATUS";
            public final static String INTERNET_TYPE = "INTERNET TYPE";
            public final static String XMPP_STATE = "XMPP STATE";

            //NOT ADDED
            public final static String CALL_BACK_ENABLED = "CALL BACK ENABLED";
            public final static String CALL_THROUGH_ENABLED = "CALL THROUGH ENABLED";

            public final static String PORT_SIP_VERSION = "PORT SIP VERSION";

            @Retention(SOURCE)
            @StringDef( {
                    LOGGED_IN,
                    DEVICE_URL,
                    MOBILE_CONFIG_URL,
                    IS_AUTH_USERNAME,
                    IS_AUTH_PASSWORD,
                    IN_CALL,
                    CONFERENCE_CALL,
                    SMS_ENABLED,
                    VOICE_TRANSCRIPT_ENABLED,
                    USER_PRESENCE_AUTOMATIC,
                    INTERNET_CONNECTED,
                    LAST_SIP_REGISTRATION_SUCCESSFUL,
                    SIP_CONNECTED,
                    UMS_CONNECTED,
                    XMPP_CONNECTED,
                    CALL_CENTER_STATUS,
                    CRASH_REPORTING,
                    ENABLE_LOGGING,
                    FILE_LOGGING,
                    SIP_LOGGING,
                    PORT_SIP_VERSION,
                    XMPP_LOGGING,
                    NIGHT_MODE,
                    CORP_ACCOUNT_NUMBER,
                    OKTA_USER_UUID,
                    IS_PLATFORM_USER,
                    IS_SHOW_SMS,
                    IS_SMS_ENABLED,
                    IS_MOBILE_TEAMCHAT_ENABLED,
                    IS_SMS_LICENSE_ENABLED,
                    IS_SMS_PROVISIONING_ENABLED,
                    IS_CONNECT_USER_PRESENCE_AUTOMATIC,
                    IS_NEXTIVA_CONNECT_ENABLED,
                    CALL_BACK_ENABLED,
                    CALL_THROUGH_ENABLED,
                    BATTERY_OPTIMIZATION_ENABLED,
                    POWER_SAVING_MODE_ENABLED,
                    DEVICE_IDLE_MODE,
                    THERMAL_STATUS,
                    INTERNET_TYPE,
                    XMPP_STATE
            })
            public @interface UserData {
            }

            public static class PermissionStates {
                public final static String RECORD_AUDIO = "PERMISSION RECORD AUDIO";
                public final static String CALL_PHONE = "PERMISSION CALL PHONE";
                public final static String WRITE_EXTERNAL_STORAGE = "PERMISSION WRITE EXTERNAL STORAGE";
                public final static String READ_CONTACTS = "PERMISSION READ CONTACTS";
                public final static String CAMERA = "PERMISSION CAMERA";
                public final static String POST_NOTIFICATIONS = "POST NOTIFICATIONS";

                @Retention(SOURCE)
                @StringDef( {
                        RECORD_AUDIO,
                        CALL_PHONE,
                        WRITE_EXTERNAL_STORAGE,
                        READ_CONTACTS,
                        CAMERA,
                        POST_NOTIFICATIONS
                })
                public @interface PermissionState {
                }
            }
        }

        public static class PendoUserDatas {
            public final static String PENDO_OKTA_USER_UUID = "OKTA_USER_UUID";
            public final static String PENDO_IS_PLATFORM_USER = "IS_PLATFORM_USER";
            public final static String PENDO_CORP_ACCOUNT_NUMBER = "corpAccountNumber";
            public final static String PENDO_MOBILE_VERSION_ANDROID = "mobile_version_android";
            public final static String PENDO_FIRST_VISIT_ANDROID = "first_visit_android";

            @Retention(SOURCE)
            @StringDef( {
                    PENDO_OKTA_USER_UUID,
                    PENDO_IS_PLATFORM_USER,
                    PENDO_CORP_ACCOUNT_NUMBER,
                    PENDO_MOBILE_VERSION_ANDROID,
                    PENDO_FIRST_VISIT_ANDROID
            })
            public @interface PendoUserData {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Logging Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Avatar State Enums
    // -------------------------------------------------------------------------------------------
    public static class AvatarState {

        public final static String STATE_NOT_SELECTED = "NOT_SELECTED";
        public final static String STATE_SELECTED = "SELECTED";
        public final static String STATE_LOCKED = "LOCKED";

        @Retention(SOURCE)
        @StringDef( {
                STATE_NOT_SELECTED,
                STATE_SELECTED,
                STATE_LOCKED
        })
        public @interface StateType {
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Avatar State Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Net Enums
    // -------------------------------------------------------------------------------------------
    public static class Net {
        public static final String flavor = com.nextiva.nextivaapp.android.BuildConfig.FLAVOR;
        public static final String versionName = com.nextiva.nextivaapp.android.BuildConfig.VERSION_NAME;
        public static final String versionNumber = android.os.Build.VERSION.RELEASE;
        public static final String USER_AGENT = "nextiva-mobile-android/" + flavor + ": " + versionName + "/Android: " + versionNumber;

        public static class StatusTypes {
            public static final String SUCCESS = "STATUS_SUCCESS";
            public static final String ERROR = "STATUS_ERROR";
            public static final String LOADING = "STATUS_LOADING";

            @Retention(SOURCE)
            @StringDef( {
                    SUCCESS,
                    ERROR,
                    LOADING
            })
            public @interface StatusType {
            }
        }

        public static class InternetConnectionTypes {
            public static final int NOT_CONNECTED = 0;
            public static final int WIFI = 1;
            public static final int MOBILE = 2;
            public static final int ETHERNET = 9;

            @Retention(SOURCE)
            @IntDef( {
                    NOT_CONNECTED,
                    WIFI,
                    MOBILE
            })
            public @interface InternetConnectionType {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Net Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region View Enums
    // -------------------------------------------------------------------------------------------
    public static class View {
        public static class ViewPagerActionTypes {
            public static final int RESET = 0;
            public static final int SELECT = 1;
            public static final int SWIPE = 2;

            @Retention(SOURCE)
            @IntDef( {
                    RESET,
                    SELECT,
                    SWIPE
            })
            public @interface ActionType {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Net Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Push Notification Event Enums
    // -------------------------------------------------------------------------------------------
    public static class PushNotifications {
        public static class Events {
            public static final String NEW_TEXT_MESSAGE = "NEW_TEXT_MSG";
            public static final String MESSAGE_WAITING_INDICATOR = "MWI";
            public static final String MESSAGE_READ = "MSG_READ";
            public static final String VCARD_MODIFIED = "VCARD_MOD";
            public static final String PRIVATE_STORAGE_MODIFIED = "PS_MOD";
            public static final String SELF_PRESENCE_UPDATE = "SELF_PRESENCE_UPDATE";
            public static final String MUC_INVITE = "MUC_INVITE";
            public static final String NEW_CALL = "NEW_CALL";
            public static final String RING_SPLASH = "RNG_SPLSH";
            public static final String CALL_UPDATED = "CALL_UPD";
            public static final String DEREGISTERED = "DEREG";
            public static final String GROUP_CHAT_JOINED = "GC_JOIN";
            public static final String MEDIACALL_STARTED = "MEDIACALL_STARTED";
            public static final String MEDIACALL_COMPLETED = "MEDIACALL_COMPLETED";
            public static final String MEDIACALL_CANCELLED = "MEDIACALL_CANCELLED";
            public static final String ATTENDEE_JOIN = "ATTENDEE_JOIN";
            public static final String ATTENDEE_JOINED = "ATTENDEE_JOINED";
            public static final String ATTENDEE_DECLINED = "ATTENDEE_DECLINED";
            public static final String ATTENDEE_DROPPED = "ATTENDEE_DROPPED";
            public static final String ATTENDEE_REMOVED = "ATTENDEE_REMOVED";

            @Retention(SOURCE)
            @StringDef( {
                    NEW_TEXT_MESSAGE,
                    MESSAGE_WAITING_INDICATOR,
                    MESSAGE_READ,
                    VCARD_MODIFIED,
                    PRIVATE_STORAGE_MODIFIED,
                    SELF_PRESENCE_UPDATE,
                    MUC_INVITE,
                    NEW_CALL,
                    RING_SPLASH,
                    CALL_UPDATED,
                    DEREGISTERED,
                    GROUP_CHAT_JOINED,
                    MEDIACALL_STARTED,
                    MEDIACALL_COMPLETED,
                    MEDIACALL_CANCELLED,
                    ATTENDEE_JOIN,
                    ATTENDEE_JOINED,
                    ATTENDEE_DECLINED,
                    ATTENDEE_DROPPED,
                    ATTENDEE_REMOVED
            })
            public @interface Event {
            }
        }

        public static class CallUpdatedEventTypes {
            public static final String CALL_ANSWERED_ELSEWHERE = "call_answered_alt_location";
            public static final String CALL_ABANDONED = "abandoned";
            public static final String CALL_UNANSWERED = "ring_no_answer";

            @Retention(SOURCE)
            @StringDef( {
                    CALL_ANSWERED_ELSEWHERE,
                    CALL_ABANDONED,
                    CALL_UNANSWERED
            })
            public @interface CallUpdatedEventType {
            }
        }

        public static class EventTypes {
            public static final String NEW_TEXT = "NewText";
            public static final String MUC_INVITE = "MucInvite";
            public static final String NEW_CALL = "NewCall";
            public static final String CALL_UPDATED = "CallUpd";
            public static final String RING_SPLASH = "RngSplsh";
            public static final String MESSAGE_WAITING_INDICATOR = "MWI";
            public static final String MESSAGE_READ = "MsgRead";
            public static final String GROUP_CHAT_JOINED = "GcJoin";
            public static final String VCARD_MODIFIED = "vCard";
            public static final String CONTACT_STORAGE_MODIFIED = "ContactStore";
            public static final String SELF_PRESENCE_MODIFIED = "SelfPresence";
            public static final String DEREGISTERED = "Dereg";
            public static final String SMS = "SMS";
            public static final String PRESENCE = "Presence";
            public static final String CHAT = "Chat";

            @Retention(SOURCE)
            @StringDef( {
                    NEW_TEXT,
                    MUC_INVITE,
                    NEW_CALL,
                    CALL_UPDATED,
                    RING_SPLASH,
                    MESSAGE_WAITING_INDICATOR,
                    MESSAGE_READ,
                    GROUP_CHAT_JOINED,
                    VCARD_MODIFIED,
                    CONTACT_STORAGE_MODIFIED,
                    SELF_PRESENCE_MODIFIED,
                    DEREGISTERED,
                    SMS,
                    PRESENCE,
                    CHAT
            })
            public @interface EventType {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Push Notification Event Enums
    // -------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    //region Analytics Enums
    // -------------------------------------------------------------------------------------------
    public static class Analytics {

        public static class ScreenName {

            public static final String ABOUT = "about_screen";
            public static final String ABOUT_INFO = "about_info_screen";
            public static final String ABOUT_LEGAL_NOTICES = "about_legal_notices_screen";
            public static final String ABOUT_LICENSE = "about_license_screen";
            public static final String ACTIVE_CALL = "active_call_screen";
            public static final String ADD_CONFERENCE_CONTACT = "add_conference_contact_screen";
            public static final String ADD_ENTERPRISE_CONTACT = "add_enterprise_contact_screen";
            public static final String ADD_NEXTIVA_ANYWHERE_LOCATION = "add_nextiva_anywhere_location_screen";
            public static final String ADD_SIMULTANEOUS_RING_LOCATION = "add_simultaneous_ring_location_screen";
            public static final String APP_PREFERENCES = "app_preferences_screen";
            public static final String BLOCK_MY_CALLER_ID_SCREEN = "block_my_caller_id_screen";
            public static final String ALLOW_TERMINATION_SCREEN = "allow_termination";
            public static final String BOTTOM_SHEET_CONTACT_DETAILS = "bottom_sheet_contact_details";
            public static final String BOTTOM_SHEET_DIALER = "bottom_sheet_dialer";
            public static final String BOTTOM_SHEET_SMS_DETAILS = "bottom_sheet_sms_details";
            public static final String CALL_DETAILS = "call_details_screen";
            public static final String CALL_FORWARDING_ALWAYS_SCREEN = "call_forwarding_always_screen";
            public static final String CALL_FORWARDING_WHEN_BUSY_SCREEN = "call_forwarding_when_busy_screen";
            public static final String CALL_FORWARDING_WHEN_UNANSWERED_SCREEN = "call_forwarding_when_unanswered_screen";
            public static final String CALL_FORWARDING_WHEN_UNREACHABLE_SCREEN = "call_forwarding_when_unreachable_screen";
            public static final String CALL_HISTORY_LIST = "call_history_list_screen";
            public static final String CALL_SETTINGS_LIST = "call_settings_list_screen";
            public static final String CHAT_SMS_ACTIVITY = "chat_sms_activity_screen";
            public static final String CHAT_DETAILS = "chat_details_screen";
            public static final String CHATS_LIST = "chats_list_screen";
            public static final String CONNECT_CALLS_LIST = "connect_calls_list";
            public static final String CONNECT_MAIN = "connect_main";
            public static final String CONNECT_NEW_CALL_LOCAL_CONTACTS_LIST = "connect_new_call_local_contacts_list_screen";
            public static final String CONNECT_NEW_CALL_DIAL = "connect_new_call_dial_screen";
            public static final String CONNECT_NEW_CALL_MAIN = "connect_new_call_main_screen";
            public static final String CONNECT_CALL_DETAILS = "connect_call_details";
            public static final String CONNECT_CONTACTS_LIST = "connect_contacts_list";
            public static final String CONNECT_CONTACT_ACTION_DIALOG = "connect_contact_action_dialog";
            public static final String CONNECT_CONTACT_DETAILS = "connect_contact_details";

            public static final String CONNECT_USER_DETAILS = "connect_user_details";
            public static final String CONNECT_MEETINGS_LIST = "connect_meetings_list";
            public static final String CONNECT_MEETINGS_JOIN_USING_PHONE_BOTTOM_SHEET = "connect_meetings_join_using_phone_bottom_sheet";
            public static final String SMS_MESSAGES_LIST = "sms_messages_list";
            public static final String CONTACT_DETAILS = "contact_details_screen";
            public static final String CONTACTS_LIST = "contacts_list_screen";
            public static final String CONNECT_ROOMS_LIST = "connect_rooms_list";
            public static final String CONNECT_ROOMS_CHAT = "connect_rooms_chat";
            public static final String CONNECT_TEAM_CHAT_LIST = "connect_team_chat_list";
            public static final String DESIGN_SYSTEM_UTILITY = "design_system_utißœlity";
            public static final String DATABASE_COUNT_UTILITY = "database_count_utility_screen";
            public static final String DIALER = "dialer_screen";
            public static final String DIALING_SERVICE_SCREEN = "dialing_service_screen";
            public static final String DO_NOT_DISTURB = "do_not_disturb_screen";
            public static final String EDIT_CONFERENCE_CONTACT = "edit_conference_contact_screen";
            public static final String EDIT_ENTERPRISE_CONTACT = "edit_enterprise_contact_screen";
            public static final String EDIT_NEXTIVA_ANYWHERE_LOCATION = "edit_nextiva_anywhere_location_screen";
            public static final String EDIT_SIMULTANEOUS_RING_LOCATION = "edit_simultaneous_ring_location_screen";
            public static final String FEATURE_FLAGS = "feature_flags";
            public static final String FONT_AWESOME_UTILITY = "font_awesome_utility";
            public static final String HEALTH_CHECK = "health_check";
            public static final String INCOMING_CALL = "incoming_call_screen";
            public static final String LICENSE_AGREEMENT = "license_agreement_screen";
            public static final String LOGIN = "login_screen";
            public static final String LOGIN_PREFERENCES = "login_preferences_screen";
            public static final String MAIN = "main_screen";
            public static final String MY_ROOM = "my_room_screen";
            public static final String MY_STATUS = "my_status_screen";
            public static final String NEW_CALL_CALL_HISTORY_LIST = "new_call_call_history_list_screen";
            public static final String NEW_CALL_CONTACTS_LIST = "new_call_contacts_list_screen";
            public static final String NEW_CALL_DIALER = "new_call_dialer_screen";
            public static final String NEW_CALL_MAIN = "new_call_main_screen";
            public static final String NEW_CALL_UNKNOWN = "unknown_new_call_screen";
            public static final String NEW_CHAT = "new_chat_screen";
            public static final String NEXTIVA_ANYWHERE_LOCATIONS_LIST_SCREEN = "nextiva_anywhere_locations_list_screen";
            public static final String ONBOARDING_THIS_PHONE_NUMBER = "onboarding_this_phone_number_screen";
            public static final String REMOTE_OFFICE = "remote_office_screen";
            public static final String SIDE_NAV = "side_nav_screen";
            public static final String SIMULTANEOUS_RING_LOCATIONS_LIST = "simultaneous_ring_locations_list_screen";
            public static final String THIS_PHONE_NUMBER = "this_phone_number_screen";
            public static final String UNKNOWN_CALL_SETTINGS = "unknown_call_settings_screen";
            public static final String UNKNOWN_DASHBOARD = "unknown_dashboard_screen";
            public static final String VOICEMAIL_LIST = "voicemail_list_screen";
            public static final String SETTINGS_CALL_CENTER_SCREEN = "settings_call_center_screen";
            public static final String SIP_CONFIGURATION = "sip_configuration";
            public static final String SMS_REQUEST_SCREEN = "sms_request_screen";
            public static final String MMS_REQUEST_SCREEN = "mms_request_screen";
            public static final String MEETING_ACTIVITY_SCREEN = "meeting_activity_screen";
            public static final String MEETING_PREVIEW_FRAGMENT_SCREEN = "meeting_preview_fragment_screen";
            public static final String MEETING_ACTIVE_FRAGMENT_SCREEN = "meeting_active_fragment_screen";
            public static final String MEETING_LIST_FRAGMENT_SCREEN = "meeting_list_fragment_screen";
            public static final String FULL_IMAGE_ACTIVITY_SCREEN = "full_image_activity_screen";

            @Retention(SOURCE)
            @StringDef( {
                    ABOUT,
                    ABOUT_INFO,
                    ABOUT_LEGAL_NOTICES,
                    ABOUT_LICENSE,
                    ACTIVE_CALL,
                    ADD_CONFERENCE_CONTACT,
                    ADD_ENTERPRISE_CONTACT,
                    ADD_NEXTIVA_ANYWHERE_LOCATION,
                    ADD_SIMULTANEOUS_RING_LOCATION,
                    ALLOW_TERMINATION_SCREEN,
                    APP_PREFERENCES,
                    BLOCK_MY_CALLER_ID_SCREEN,
                    BOTTOM_SHEET_CONTACT_DETAILS,
                    BOTTOM_SHEET_DIALER,
                    BOTTOM_SHEET_SMS_DETAILS,
                    CALL_DETAILS,
                    CALL_FORWARDING_ALWAYS_SCREEN,
                    CALL_FORWARDING_WHEN_BUSY_SCREEN,
                    CALL_FORWARDING_WHEN_UNANSWERED_SCREEN,
                    CALL_FORWARDING_WHEN_UNREACHABLE_SCREEN,
                    CALL_HISTORY_LIST,
                    CALL_SETTINGS_LIST,
                    CHAT_DETAILS,
                    CHATS_LIST,
                    CONNECT_CALLS_LIST,
                    CONNECT_CONTACT_ACTION_DIALOG,
                    CONNECT_MAIN,
                    CONNECT_NEW_CALL_MAIN,
                    CONNECT_NEW_CALL_LOCAL_CONTACTS_LIST,
                    CONNECT_NEW_CALL_DIAL,
                    CONNECT_CALL_DETAILS,
                    CONNECT_CONTACTS_LIST,
                    CONNECT_ROOMS_LIST,
                    CONNECT_ROOMS_CHAT,
                    CONNECT_TEAM_CHAT_LIST,
                    CONNECT_CONTACT_DETAILS,
                    CONNECT_USER_DETAILS,
                    CONNECT_MEETINGS_LIST,
                    CONNECT_MEETINGS_JOIN_USING_PHONE_BOTTOM_SHEET,
                    CONTACT_DETAILS,
                    CONTACTS_LIST,
                    DESIGN_SYSTEM_UTILITY,
                    DATABASE_COUNT_UTILITY,
                    DIALER,
                    DIALING_SERVICE_SCREEN,
                    DO_NOT_DISTURB,
                    EDIT_CONFERENCE_CONTACT,
                    EDIT_ENTERPRISE_CONTACT,
                    EDIT_NEXTIVA_ANYWHERE_LOCATION,
                    EDIT_SIMULTANEOUS_RING_LOCATION,
                    FEATURE_FLAGS,
                    FONT_AWESOME_UTILITY,
                    HEALTH_CHECK,
                    INCOMING_CALL,
                    LICENSE_AGREEMENT,
                    LOGIN,
                    LOGIN_PREFERENCES,
                    MAIN,
                    MY_ROOM,
                    MY_STATUS,
                    NEW_CALL_CALL_HISTORY_LIST,
                    NEW_CALL_CONTACTS_LIST,
                    NEW_CALL_DIALER,
                    NEW_CALL_MAIN,
                    NEW_CALL_UNKNOWN,
                    NEW_CHAT,
                    NEXTIVA_ANYWHERE_LOCATIONS_LIST_SCREEN,
                    ONBOARDING_THIS_PHONE_NUMBER,
                    REMOTE_OFFICE,
                    SIDE_NAV,
                    SIMULTANEOUS_RING_LOCATIONS_LIST,
                    SIP_CONFIGURATION,
                    SMS_MESSAGES_LIST,
                    THIS_PHONE_NUMBER,
                    UNKNOWN_CALL_SETTINGS,
                    UNKNOWN_DASHBOARD,
                    VOICEMAIL_LIST,
                    SETTINGS_CALL_CENTER_SCREEN,
                    SMS_REQUEST_SCREEN,
                    MMS_REQUEST_SCREEN,
                    MEETING_ACTIVITY_SCREEN,
                    MEETING_ACTIVE_FRAGMENT_SCREEN,
                    MEETING_PREVIEW_FRAGMENT_SCREEN,
                    MEETING_LIST_FRAGMENT_SCREEN,
                    FULL_IMAGE_ACTIVITY_SCREEN
            })
            public @interface Screen {
            }
        }


        //TODO: MAKE SUB_CLASSES
        public static class EventName {
            public static final String ABOUT_LIST_ITEM_PRESSED = "about_list_item_pressed";
            public static final String ABOUT_MENU_ITEM_PRESSED = "about_menu_item_pressed";
            public static final String ACCEPT_BUTTON_PRESSED = "accept_button_pressed";
            public static final String ACCEPT_LICENSE_AGREEMENT_DIALOG_ACCEPT_BUTTON_PRESSED = "accept_license_agreement_dialog_accept_button_pressed";
            public static final String ACCEPT_LICENSE_AGREEMENT_DIALOG_CANCEL_BUTTON_PRESSED = "accept_license_agreement_dialog_cancel_button_pressed";
            public static final String ACCEPT_LICENSE_AGREEMENT_DIALOG_SHOWN = "accept_license_agreement_dialog_shown";
            public static final String ACCEPT_VIDEO_CALL_BUTTON_PRESSED = "accept_video_call_button_pressed";
            public static final String ACCEPT_VOICE_CALL_BUTTON_PRESSED = "accept_voice_call_button_pressed";
            public static final String ACCESS_DEVICE_NOT_FOUND_DIALOG_SHOWN = "access_device_not_found_dialog_shown";
            public static final String ACTIVE_CALL_TRANSFER_OPTIONS_LIST_SHOWN = "active_call_transfer_option_list_shown";
            public static final String ADD_CHAT_BUTTON_PRESSED = "add_chat_button_pressed";
            public static final String ADD_CONFERENCE_CONTACT_BUTTON_PRESSED = "add_conference_contact_button_pressed";
            public static final String ADD_CONTACT_BUTTON_PRESSED = "add_contact_button_pressed";
            public static final String ADD_ENTERPRISE_CONTACT_BUTTON_PRESSED = "add_enterprise_contact_button_pressed";
            public static final String ADD_GROUP_BUTTON_PRESSED = "add_group_button_pressed";
            public static final String ADD_LOCATION_BUTTON_PRESSED = "add_location_button_pressed";
            public static final String ADD_TO_CONTACTS_LIST_ITEM_COPIED = "add+to_contacts_list_item_copied";
            public static final String ADD_TO_CONTACTS_LIST_ITEM_ADD_BUTTON_PRESSED = "add_to_contacts_list_item_add_button_pressed";
            public static final String ADD_TO_FAVORITES_LIST_ITEM_FAVORITE_BUTTON_PRESSED = "add_to_favorites_list_item_favorite_button_pressed";
            public static final String ADD_TO_FAVORITES_LIST_ITEM_UNFAVORITE_BUTTON_PRESSED = "add_to_favorites_list_item_unfavorite_button_pressed";
            public static final String ADD_TO_LOCAL_CONTACT_LIST_ITEM_COPIED = "add_to_local_contacts_list_item_copied";
            public static final String ADD_TO_LOCAL_CONTACTS_LIST_ITEM_ADD_BUTTON_PRESSED = "add_to_local_contacts_list_item_add_button_pressed";
            public static final String ADD_VIDEO_TO_CALL_DIALOG_ACCEPT_BUTTON_PRESSED = "add_video_to_call_dialog_accept_button_pressed";
            public static final String ADD_VIDEO_TO_CALL_DIALOG_DECLINE_BUTTON_PRESSED = "add_video_to_call_dialog_decline_button_pressed";
            public static final String ADD_VIDEO_TO_CALL_DIALOG_SHOWN = "add_video_to_call_dialog_shown";
            public static final String ALERT_ALL_LOCATIONS_SWITCH_CHECKED = "alert_all_locations_switch_checked";
            public static final String ALERT_ALL_LOCATIONS_SWITCH_UNCHECKED = "alert_all_locations_switch_unchecked";
            public static final String ALL_CALL_LOGS_FILTER_SELECTED = "all_call_logs_filter_selected";
            public static final String ALLOW_TERMINATION_LIST_ITEM_CLICKED = "allow_termination_list_item_clicked";
            public static final String ALWAYS_ASK_RADIO_BUTTON_CHECKED = "always_ask_radio_button_checked";
            public static final String ANSWER_CONFIRMATION_SWITCH_CHECKED = "answer_confirmation_switch_checked";
            public static final String ANSWER_CONFIRMATION_SWITCH_UNCHECKED = "answer_confirmation_switch_unchecked";
            public static final String APP_PREFERENCES_MENU_ITEM_PRESSED = "app_preferences_menu_item_pressed";
            public static final String AUTOMATIC_RADIO_BUTTON_CHECKED = "automatic_radio_button_checked";
            public static final String AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED = "avatar_camera_permission_rationale_dialog_ok_button_pressed";
            public static final String AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED = "avatar_camera_permission_rationale_dialog_settings_button_pressed";
            public static final String AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_SHOWN = "avatar_camera_permission_rationale_dialog_shown";
            public static final String AVATAR_IMAGE_PRESSED = "avatar_image_pressed";
            public static final String AWAY_RADIO_BUTTON_CHECKED = "away_radio_button_checked";
            public static final String BACK_BUTTON_PRESSED = "back_button_pressed";
            public static final String BLOCK_MY_CALLER_ID_LIST_ITEM_PRESSED = "block_my_caller_id_list_item_pressed";
            public static final String BUSY_RADIO_BUTTON_CHECKED = "busy_radio_button_checked";
            public static final String CALL_BACK_CONFLICT_DIALOG_NO_BUTTON_PRESSED = "call_back_conflict_dialog_no_button_pressed";
            public static final String CALL_BACK_CONFLICT_DIALOG_SHOWN = "call_back_conflict_dialog_shown";
            public static final String CALL_BACK_CONFLICT_DIALOG_YES_BUTTON_PRESSED = "call_back_conflict_dialog_yes_button_pressed";
            public static final String CALL_BACK_INITIATED_DIALOG_OK_BUTTON_PRESSED = "call_back_initiated_dialog_ok_button_pressed";
            public static final String CALL_BACK_INITIATED_DIALOG_SHOWN = "call_back_initiated_dialog_shown";
            public static final String CALL_BACK_RADIO_BUTTON_CHECKED = "call_back_radio_button_checked";
            public static final String CALL_CONTROL_SWITCH_CHECKED = "call_control_switch_checked";
            public static final String CALL_CONTROL_SWITCH_UNCHECKED = "call_control_switch_unchecked";
            public static final String CALL_FORWARDING_ALWAYS_LIST_ITEM_PRESSED = "call_forwarding_always_list_item_pressed";
            public static final String CALL_FORWARDING_WHEN_BUSY_LIST_ITEM_PRESSED = "call_forwarding_when_busy_list_item_pressed";
            public static final String CALL_FORWARDING_WHEN_UNANSWERED_LIST_ITEM_PRESSED = "call_forwarding_when_unanswered_list_item_pressed";
            public static final String CALL_FORWARDING_WHEN_UNREACHABLE_LIST_ITEM_PRESSED = "call_forwarding_when_unreachable_list_item_pressed";
            public static final String CALL_HISTORY_LIST_ITEM_LONG_PRESSED = "call_history_list_item_long_pressed";
            public static final String CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_CALL_DETAILS_BUTTON_PRESSED = "call_history_list_item_options_dialog_call_details_button_pressed";
            public static final String CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_CANCEL_BUTTON_PRESSED = "call_history_list_item_options_dialog_cancel_button_pressed";
            public static final String CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_DELETE_CALL_BUTTON_PRESSED = "call_history_list_item_options_dialog_delete_call_button_pressed";
            public static final String CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_SHOWN = "call_history_list_item_options_dialog_shown";
            public static final String CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_VIDEO_CALL_BUTTON_PRESSED = "call_history_list_item_options_dialog_video_call_button_pressed";
            public static final String CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_VOICE_CALL_BUTTON_PRESSED = "call_history_list_item_options_dialog_voice_call_button_pressed";
            public static final String CALL_HISTORY_LIST_ITEM_PRESSED = "call_history_list_item_pressed";
            public static final String CALL_HISTORY_LIST_ITEM_SWIPED = "call_history_list_item_swiped";
            public static final String CALL_HISTORY_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED = "call_history_list_item_voice_call_button_pressed";
            public static final String CALL_HISTORY_TAB_PRESSED = "call_history_tab_pressed";
            public static final String CALL_HISTORY_TAB_SWIPED_TO = "call_history_tab_swiped_to";
            public static final String CALL_QUALITY_ISSUES_DIALOG_SHOWN = "call_quality_issues_dialog_shown";
            public static final String CALL_QUICK_ACTION_BUTTON_PRESSED = "call_quick_action_button_pressed";
            public static final String CALL_SETTINGS_HELP_DIALOG_OK_BUTTON_PRESSED = "call_settings_help_dialog_ok_button_pressed";
            public static final String CALL_SETTINGS_HELP_DIALOG_SHOWN = "call_settings_help_dialog_shown";
            public static final String CALL_SETTINGS_MENU_ITEM_PRESSED = "call_settings_menu_item_pressed";
            public static final String CALL_THROUGH_CONFLICT_DIALOG_NO_BUTTON_PRESSED = "call_through_conflict_dialog_no_button_pressed";
            public static final String CALL_THROUGH_CONFLICT_DIALOG_SHOWN = "call_through_conflict_dialog_shown";
            public static final String CALL_THROUGH_CONFLICT_DIALOG_YES_BUTTON_PRESSED = "call_through_conflict_dialog_yes_button_pressed";
            public static final String CALL_THROUGH_RADIO_BUTTON_CHECKED = "call_through_radio_button_checked";
            public static final String CALL_TYPE_SELECTION_DIALOG_CALL_TYPE_SELECTED = "call_type_selection_dialog_call_type_selected";
            public static final String CALL_TYPE_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED = "call_type_selection_dialog_cancel_button_pressed";
            public static final String CALL_TYPE_SELECTION_DIALOG_SHOWN = "call_type_selection_dialog_shown";
            public static final String CHANGE_PROFILE_PHOTO_DIALOG_CANCEL_BUTTON_PRESSED = "change_profile_photo_dialog_cancel_button_pressed";
            public static final String CHANGE_PROFILE_PHOTO_DIALOG_CLEAR_PHOTO_BUTTON_PRESSED = "change_profile_photo_dialog_clear_photo_button_pressed";
            public static final String CHANGE_PROFILE_PHOTO_DIALOG_NEW_PHOTO_BUTTON_PRESSED = "change_profile_photo_dialog_new_photo_button_pressed";
            public static final String CHANGE_PROFILE_PHOTO_DIALOG_SHOWN = "change_profile_photo_dialog_shown";
            public static final String BAD_SMS_NUMBER_INITIATED_DIALOG_OK_BUTTON_PRESSED = "bad_sms_number_initiated_dialog_ok_button_pressed";
            public static final String CHAT_LIST_ITEM_PRESSED = "chat_list_item_pressed";
            public static final String CHAT_QUICK_ACTION_BUTTON_PRESSED = "chat_quick_action_button_pressed";
            public static final String CHAT_TAB_PRESSED = "chat_tab_pressed";
            public static final String CHAT_TAB_SWIPED_TO = "chat_tab_swiped_to";
            public static final String CLEAR_ALL_LOGS_BUTTON_PRESSED = "clear_all_logs_button_pressed";
            public static final String CLEAR_ALL_LOGS_DIALOG_CANCEL_BUTTON_PRESSED = "clear_all_logs_dialog_cancel_button_pressed";
            public static final String CLEAR_ALL_LOGS_DIALOG_CLEAR_LOGS_BUTTON_PRESSED = "clear_all_logs_dialog_clear_logs_button_pressed";
            public static final String CLEAR_ALL_LOGS_DIALOG_SHOWN = "clear_all_logs_dialog_shown";
            public static final String CONFERENCE_PHONE_NUMBER_LIST_ITEM_COPIED = "conference_phone_number_list_item_copied";
            public static final String COMPANY_LIST_ITEM_COPIED = "company_list_item_copied";
            public static final String CONTACT_LIST_ITEM_DELETE_PRESSED = "contact_list_item_delete_pressed";
            public static final String CONTACT_LIST_ITEM_FAVORITE_PRESSED = "contact_list_item_favorite_pressed";
            public static final String CONTACT_LIST_ITEM_LONG_PRESSED = "contact_list_item_long_pressed";
            public static final String CONTACT_LIST_ITEM_PRESSED = "contact_list_item_pressed";
            public static final String CONTACT_LIST_ITEM_SWIPED = "contact_list_item_swiped";
            public static final String CONTACT_LIST_TAB_PRESSED = "contact_list_tab_pressed";
            public static final String CONTACT_LIST_TAB_SWIPED_TO = "contact_list_tab_swiped_to";
            public static final String CONTACTS_LIST_ITEM_OPTIONS_DIALOG_CANCEL_BUTTON_PRESSED = "contacts_list_item_options_dialog_cancel_button_pressed";
            public static final String CONTACTS_LIST_ITEM_OPTIONS_DIALOG_EDIT_CONTACT_BUTTON_PRESSED = "contacts_list_item_options_dialog_edit_contact_button_pressed";
            public static final String CONTACTS_LIST_ITEM_OPTIONS_DIALOG_SHOWN = "contacts_list_item_options_dialog_shown";
            public static final String CONTINUE_BUTTON_PRESSED = "continue_button_pressed";
            public static final String COMPLETE_CALL_TRANSFER_BUTTON_PRESSED = "complete_call_transfer_button_pressed";
            public static final String DECLINE_BUTTON_PRESSED = "decline_button_pressed";
            public static final String DECLINE_CALL_BUTTON_PRESSED = "decline_call_button_pressed";
            public static final String DECLINE_LICENSE_AGREEMENT_DIALOG_DECLINE_BUTTON_PRESSED = "decline_license_agreement_dialog_decline_button_pressed";
            public static final String DECLINE_LICENSE_AGREEMENT_DIALOG_REVIEW_BUTTON_PRESSED = "decline_license_agreement_dialog_review_button_pressed";
            public static final String DECLINE_LICENSE_AGREEMENT_DIALOG_SHOWN = "decline_license_agreement_dialog_shown";
            public static final String DELETE_ALL_CALL_HISTORY_BUTTON_PRESSED = "delete_all_call_history_button_pressed";
            public static final String DELETE_BUTTON_PRESSED = "delete_button_pressed";
            public static final String DELETE_CALL_HISTORY_DIALOG_CANCEL_BUTTON_PRESSED = "delete_call_history_dialog_cancel_button_pressed";
            public static final String DELETE_CALL_HISTORY_DIALOG_DELETE_BUTTON_PRESSED = "delete_call_history_dialog_delete_button_pressed";
            public static final String DELETE_CALL_HISTORY_DIALOG_SHOWN = "delete_call_history_dialog_shown";
            public static final String DELETE_CONTACT_DIALOG_CANCEL_BUTTON_PRESSED = "delete_contact_dialog_cancel_button_pressed";
            public static final String DELETE_CONTACT_DIALOG_DELETE_BUTTON_PRESSED = "delete_contact_dialog_delete_button_pressed";
            public static final String DELETE_CONTACT_DIALOG_SHOWN = "delete_contact_dialog_shown";
            public static final String DELETE_LOCATION_DIALOG_CANCEL_BUTTON_PRESSED = "delete_location_dialog_cancel_button_pressed";
            public static final String DELETE_LOCATION_DIALOG_DELETE_BUTTON_PRESSED = "delete_location_dialog_delete_button_pressed";
            public static final String DELETE_LOCATION_DIALOG_SHOWN = "delete_location_dialog_shown";
            public static final String DIALER_TAB_PRESSED = "dialer_tab_pressed";
            public static final String DIALER_TAB_SWIPED_TO = "dialer_tab_swiped_to";
            public static final String DIALING_SERVICE_LIST_ITEM_PRESSED = "dialing_service_list_item_pressed";
            public static final String DIALING_SERVICE_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED = "dialing_service_selection_dialog_cancel_button_pressed";
            public static final String DIALING_SERVICE_SELECTION_DIALOG_DIALING_SERVICE_SELECTED = "dialing_service_selection_dialog_dialing_service_selected";
            public static final String DIALING_SERVICE_SELECTION_DIALOG_SHOWN = "dialing_service_selection_dialog_shown";
            public static final String DIRECTORY_FILTER_SELECTED = "directory_filter_selected";
            public static final String DO_NOT_DISTURB_LIST_ITEM_PRESSED = "do_not_disturb_list_item_pressed";
            public static final String DO_NOT_RING_WHILE_ON_CALL_SWITCH_CHECKED = "do_not_ring_while_on_call_switch_checked";
            public static final String DO_NOT_RING_WHILE_ON_CALL_SWITCH_UNCHECKED = "do_not_ring_while_on_call_switch_unchecked";
            public static final String DUPLICATE_NEXTIVA_ANYWHERE_LOCATION_EXISTS_DIALOG_SHOWN = "duplicate_nextiva_anywhere_location_exists_dialog_shown";
            public static final String DUPLICATE_SIMULTANEOUS_RING_LOCATION_EXISTS_DIALOG_SHOWN = "duplicate_simultaneous_ring_location_exists_dialog_shown";
            public static final String DUPLICATE_LOCAL_CONTACT_EXISTS_DIALOG_SHOWN = "duplicate_local_contact_exists_dialog_shown";
            public static final String EDIT_BUTTON_PRESSED = "edit_button_pressed";
            public static final String EMAIL_BUTTON_PRESSED = "email_button_pressed";
            public static final String EMAIL_LIST_ITEM_COPIED = "email_list_item_copied";
            public static final String EMAIL_LIST_ITEM_EMAIL_BUTTON_PRESSED = "email_list_item_email_button_pressed";
            public static final String EMAIL_LOGS_TO_SUPPORT_BUTTON_PRESSED = "email_logs_to_support_button_pressed";
            public static final String ENABLE_LOGGING_SWITCH_CHECKED = "enable_logging_switch_checked";
            public static final String ENABLE_LOGGING_SWITCH_UNCHECKED = "enable_logging_switch_unchecked";
            public static final String ENABLED_SWITCH_CHECKED = "enabled_switch_checked";
            public static final String ENABLED_SWITCH_UNCHECKED = "enabled_switch_unchecked";
            public static final String END_CALL_BUTTON_PRESSED = "end_call_button_pressed";
            public static final String END_CALL_TRANSFER_BUTTON_PRESSED = "end_call_transfer_button_pressed";
            public static final String FAVORITE_LIST_ITEM_COPIED = "favorite_list_item_copied";
            public static final String FILE_LOGGING_SWITCH_CHECKED = "file_logging_switch_checked";
            public static final String FILE_LOGGING_SWITCH_UNCHECKED = "file_logging_switch_unchecked";
            public static final String FILTER_BUTTON_PRESSED = "filter_button_pressed";
            public static final String FIRST_NAME_LIST_ITEM_COPIED = "first_name_list_item_copied";
            public static final String GENERIC_ERROR_DIALOG_OK_BUTTON_PRESSED = "generic_error_dialog_ok_button_pressed";
            public static final String GENERIC_ERROR_DIALOG_SHOWN = "generic_error_dialog_shown";
            public static final String HAMBURGER_MENU_PRESSED = "hamburger_menu_pressed";
            public static final String HELP_BUTTON_PRESSED = "help_button_pressed";
            public static final String HELP_LIST_ITEM_PRESSED = "help_list_item_pressed";
            public static final String HELP_MENU_ITEM_PRESSED = "help_menu_item_pressed";
            public static final String HOLD_BUTTON_DESELECTED = "hold_button_deselected";
            public static final String HOLD_BUTTON_SELECTED = "hold_button_selected";
            public static final String IM_ADDRESS_LIST_ITEM_CHAT_BUTTON_PRESSED = "im_address_list_item_chat_button_pressed";
            public static final String IM_ADDRESS_LIST_ITEM_COPIED = "im_address_list_item_copied";
            public static final String INFO_TAB_PRESSED = "info_tab_pressed";
            public static final String INFO_TAB_SWIPED_TO = "info_tab_swiped_to";
            public static final String JOIN_ROOM_QUICK_ACTION_BUTTON_PRESSED = "join_room_quick_action_button_pressed";
            public static final String KEYPAD_BUTTON_PRESSED = "keypad_button_pressed";
            public static final String LAST_NAME_LIST_ITEM_COPIED = "last_name_list_item_copied";
            public static final String LEGAL_NOTICES_TAB_PRESSED = "legal_notices_tab_pressed";
            public static final String LEGAL_NOTICES_TAB_SWIPED_TO = "legal_notices_tab_swiped_to";
            public static final String LICENSE_TAB_PRESSED = "license_tab_pressed";
            public static final String LICENSE_TAB_SWIPED_TO = "license_tab_swiped_to";
            public static final String PRIVACY_TAB_PRESSED = "privacy_tab_pressed";
            public static final String PRIVACY_TAB_SWIPED_TO = "privacy_tab_swiped_to";
            public static final String LOGIN_FAILED_DIALOG_SHOWN = "login_failed_dialog_shown";
            public static final String LOCAL_ADDRESS_BOOK_FILTER_SELECTED = "local_address_book_filter_selected";
            public static final String LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED = "local_contacts_permission_rationale_dialog_ok_button_pressed";
            public static final String LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_SHOWN = "local_contacts_permission_rationale_dialog_shown";
            public static final String LOCAL_CONTACTS_REQUEST_PERMISSION_BUTTON_PRESSED = "local_contacts_request_permission_button_pressed";
            public static final String LOCATION_LIST_ITEM_PRESSED = "location_list_item_pressed";
            public static final String MARK_ALL_READ_BUTTON_PRESSED = "mark_all_read_button_pressed";
            public static final String MISSED_CALL_LOGS_FILTER_SELECTED = "missed_call_logs_filter_selected";
            public static final String MOBILE_RADIO_BUTTON_CHECKED = "mobile_radio_button_checked";
            public static final String MORE_BUTTON_PRESSED = "more_button_pressed";
            public static final String MULTI_NUMBER_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED = "multi_number_selection_dialog_cancel_button_pressed";
            public static final String MULTI_NUMBER_SELECTION_DIALOG_NUMBER_SELECTED = "multi_number_selection_dialog_number_selected";
            public static final String MULTI_NUMBER_SELECTION_DIALOG_SHOWN = "multi_number_selection_dialog_shown";
            public static final String MUTE_BUTTON_DESELECTED = "mute_button_deselected";
            public static final String MUTE_BUTTON_SELECTED = "mute_button_selected";
            public static final String MY_CONTACTS_FILTER_SELECTED = "my_contacts_filter_selected";
            public static final String MY_ONLINE_CONTACTS_FILTER_SELECTED = "my_online_contacts_filter_selected";
            public static final String NEW_CALL_BUTTON_PRESSED = "new_call_button_pressed";
            public static final String NEXTIVA_ANYWHERE_LIST_ITEM_PRESSED = "nextiva_anywhere_list_item_pressed";
            public static final String NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED = "no_app_found_dialog_ok_button_pressed";
            public static final String NO_APP_FOUND_DIALOG_SHOWN = "no_app_found_dialog_shown";
            public static final String NO_COMPATIBLE_BROWSER_FOUND_DIALOG_SHOWN = "no_compatible_browser_found_dialog_shown";
            public static final String NO_CONTACT_FOUND_DIALOG_OK_BUTTON_PRESSED = "no_contact_found_dialog_ok_button_pressed";
            public static final String NO_CONTACT_FOUND_DIALOG_SHOWN = "no_contact_found_dialog_shown";
            public static final String NO_INTERNET_DIALOG_OK_BUTTON_PRESSED = "no_internet_dialog_ok_button_pressed";
            public static final String NO_INTERNET_DIALOG_SETTINGS_BUTTON_PRESSED = "no_internet_dialog_settings_button_pressed";
            public static final String NO_INTERNET_DIALOG_SHOWN = "no_internet_dialog_shown";
            public static final String NO_LOG_FILE_DIALOG_OK_BUTTON_PRESSED = "no_log_file_dialog_ok_button_pressed";
            public static final String NO_LOG_FILE_DIALOG_SHOWN = "no_log_file_dialog_shown";
            public static final String NO_NUMBER_FOUND_DIALOG_OK_BUTTON_PRESSED = "no_number_found_dialog_ok_button_pressed";
            public static final String NO_NUMBER_FOUND_DIALOG_SHOWN = "no_log_file_dialog_shown";
            public static final String NUMBER_OF_RINGS_BUTTON_PRESSED = "number_of_rings_button_pressed";
            public static final String ON_HOLD_CALL_HEADER_PRESSED = "on_hold_call_header_pressed";
            public static final String PHONE_NUMBER_LIST_ITEM_COPIED = "phone_number_list_item_copied";
            public static final String PHONE_NUMBER_LIST_ITEM_VIDEO_CALL_BUTTON_PRESSED = "phone_number_list_item_video_call_button_pressed";
            public static final String PHONE_NUMBER_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED = "phone_number_list_item_voice_call_button_pressed";
            public static final String POOR_CONNECTION_DIALOG_CLOSE_BUTTON_PRESSED = "poor_connection_dialog_close_button_pressed";
            public static final String POOR_CONNECTION_DIALOG_SHOWN = "poor_connection_dialog_shown";
            public static final String POOR_CONNECTION_DIALOG_REMOVED = "poor_connection_dialog_removed";
            public static final String POOR_CONNECTION_DIALOG_TRANSFER_TO_MOBILE_BUTTON_PRESSED = "poor_connection_dialog_transfer_to_mobile_button_pressed";
            public static final String POST_NOTIFICATIONS_PERMISSION_RATIONALE_DIALOG_SHOWN = "post_notifications_permission_rationale_dialog_shown";
            public static final String POST_NOTIFICATIONS_REQUEST_PERMISSION_BUTTON_PRESSED = "post_notifications_request_permission_button_pressed";
            public static final String PREVENT_DIVERTING_CALLS_SWITCH_CHECKED = "prevent_diverting_calls_switch_checked";
            public static final String PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED = "prevent_diverting_calls_switch_unchecked";
            public static final String PROGRESS_DIALOG_SHOWN = "progress_dialog_shown";
            public static final String PULL_CALL_BUTTON_PRESSED = "pull_call_button_pressed";
            public static final String PULL_TO_REFRESH = "pull_to_refresh";
            public static final String REMEMBER_PASSWORD_SWITCH_CHECKED = "remember_password_switch_checked";
            public static final String REMEMBER_PASSWORD_SWITCH_UNCHECKED = "remember_password_switch_unchecked";
            public static final String REMOTE_OFFICE_LIST_ITEM_PRESSED = "remote_office_list_item_pressed";
            public static final String RETURN_TO_ACTIVE_CALL_PRESSED = "return_to_active_call_pressed";
            public static final String RING_SPLASH_SWITCH_CHECKED = "ring_splash_switch_checked";
            public static final String RING_SPLASH_SWITCH_UNCHECKED = "ring_splash_switch_unchecked";
            public static final String SAVE_BUTTON_PRESSED = "save_button_pressed";
            public static final String SCREEN_VIEW = "screen_view";
            public static final String SEARCH_BUTTON_PRESSED = "search_button_pressed";
            public static final String SECTION_HEADER_COLLAPSED = "section_header_collapsed";
            public static final String SECTION_HEADER_EXPANDED = "section_header_expanded";
            public static final String SEND_PERSONAL_SMS_LIST_ITEM_COPIED = "send_personal_sms_list_item_copied";
            public static final String SEND_SMS_LIST_ITEM_SEND_SMS_BUTTON_PRESSED = "send_sms_list_item_send_sms_button_pressed";
            public static final String SETTINGS_BUTTON_PRESSED = "settings_button_pressed";
            public static final String SIDE_NAV_CLOSED = "side_nav_closed";
            public static final String SIDE_NAV_OPENED = "side_nav_opened";
            public static final String SIGN_IN_BUTTON_PRESSED = "sign_in_button_pressed";
            public static final String SIGN_OUT_MENU_ITEM_PRESSED = "sign_out_menu_item_pressed";
            public static final String SIMULTANEOUS_RING_LIST_ITEM_PRESSED = "simultaneous_ring_list_item_pressed";
            public static final String SIP_LOGGING_SWITCH_CHECKED = "sip_logging_switch_checked";
            public static final String SIP_LOGGING_SWITCH_UNCHECKED = "sip_logging_switch_unchecked";
            public static final String SPEAKER_BUTTON_DESELECTED = "speaker_button_deselected";
            public static final String SPEAKER_BUTTON_SELECTED = "speaker_button_selected";
            public static final String SWAP_CALL_BUTTON_PRESSED = "swap_call_button_pressed";
            public static final String SWITCH_CAMERA_BUTTON_PRESSED = "switch_camera_button_pressed";
            public static final String THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED = "this_phone_call_permission_rationale_dialog_ok_button_pressed";
            public static final String THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED = "this_phone_call_permission_rationale_dialog_settings_button_pressed";
            public static final String THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN = "this_phone_call_permission_rationale_dialog_shown";
            public static final String THIS_PHONE_NUMBER_LIST_ITEM_PRESSED = "this_phone_number_list_item_pressed";
            public static final String THIS_PHONE_RADIO_BUTTON_CHECKED = "this_phone_radio_button_checked";
            public static final String TRANSFER_TO_MOBILE_CALL_DIALOG_ACCEPT_BUTTON_PRESSED = "transfer_to_mobile_call_dialog_accept_button_pressed";
            public static final String TRANSFER_TO_MOBILE_CALL_DIALOG_CANCEL_BUTTON_PRESSED = "transfer_to_mobile_call_dialog_cancel_button_pressed";
            public static final String TROUBLESHOOTING_LIST_ITEM_PRESSED = "troubleshooting_list_item_pressed";
            public static final String UNABLE_TO_SAVE_CONTACT_DIALOG_OK_BUTTON_PRESSED = "unable_to_save_contact_dialog_ok_button_pressed";
            public static final String UNABLE_TO_SAVE_CONTACT_DIALOG_SHOWN = "unable_to_save_contact_dialog_shown";
            public static final String UNSAVED_CHANGES_DIALOG_CANCEL_BUTTON_PRESSED = "unsaved_changes_dialog_cancel_button_pressed";
            public static final String UNSAVED_CHANGES_DIALOG_DISCARD_BUTTON_PRESSED = "unsaved_changes_dialog_discard_button_pressed";
            public static final String UNSAVED_CHANGES_DIALOG_SHOWN = "unsaved_changes_dialog_shown";
            public static final String USER_HEADER_PRESSED = "user_header_pressed";
            public static final String VIDEO_BUTTON_DESELECTED = "video_button_deselected";
            public static final String VIDEO_BUTTON_SELECTED = "video_button_selected";
            public static final String VIDEO_CALL_BUTTON_PRESSED = "video_call_button_pressed";
            public static final String VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED = "video_call_permission_rationale_dialog_ok_button_pressed";
            public static final String VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED = "video_call_permission_rationale_dialog_settings_button_pressed";
            public static final String VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN = "video_call_permission_rationale_dialog_shown";
            public static final String INITIAL_PERMISSION_RATIONALE_DIALOG_SHOWN = "initial_permission_rationale_dialog_shown";
            public static final String INITIAL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED = "initial_permission_rationale_dialog_ok_button_pressed";
            public static final String INITIAL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED = "initial_permission_rationale_dialog_settings_button_pressed";
            public static final String VIEW_PASSWORD_BUTTON_PRESSED = "view_password_button_pressed";
            public static final String VOICE_CALL_BUTTON_PRESSED = "voice_call_button_pressed";
            public static final String VOICE_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED = "voice_call_permission_rationale_dialog_ok_button_pressed";
            public static final String VOICE_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED = "voice_call_permission_rationale_dialog_settings_button_pressed";
            public static final String VOICE_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN = "voice_call_permission_rationale_dialog_shown";
            public static final String VOICEMAIL_CALL_BUTTON_LONG_PRESSED = "voicemail_call_button_long_pressed";
            public static final String VISUAL_VOICEMAIL_TAB_PRESSED = "visual_voicemail_tab_pressed";
            public static final String VISUAL_VOICEMAIL_TAB_SWIPED_TO = "visual_voicemail_tab_swiped_to";
            public static final String VOIP_RADIO_BUTTON_CHECKED = "voip_radio_button_checked";
            public static final String WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED = "write_external_storage_permission_rationale_dialog_ok_button_pressed";
            public static final String WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED = "write_external_storage_permission_rationale_dialog_settings_button_pressed";
            public static final String WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_SHOWN = "write_external_storage_permission_rationale_dialog_shown";
            public static final String XMPP_LOGGING_SWITCH_CHECKED = "xmpp_logging_switch_checked";
            public static final String XMPP_LOGGING_SWITCH_UNCHECKED = "xmpp_logging_switch_unchecked";
            public static final String DISPLAY_AUDIO_VIDEO_STATS_CHECKED = "display_audio_video_stats_checked";
            public static final String DISPLAY_AUDIO_VIDEO_STATS_UNCHECKED = "display_audio_video_stats_unchecked";
            public static final String DISPLAY_SIP_STATE_CHECKED = "sip_state_checked";
            public static final String DISPLAY_SIP_STATE_UNCHECKED = "display_sip_state_unchecked";
            public static final String DISPLAY_SIP_ERROR_CHECKED = "sip_error_checked";
            public static final String DISPLAY_SIP_ERROR_UNCHECKED = "display_sip_error_unchecked";
            public static final String CALL_CENTER_SIGN_IN_BUTTON_SELECTED = "call_center_sign_in_button_selected";
            public static final String CALL_CENTER_AVAILABLE_BUTTON_SELECTED = "call_center_available_button_selected";
            public static final String CALL_CENTER_UNAVAILABLE_BUTTON_SELECTED = "call_center_unavailable_button_selected";
            public static final String CALL_CENTER_WRAP_UP_BUTTON_SELECTED = "call_center_wrap_up_button_selected";
            public static final String CALL_CENTER_SIGN_OUT_BUTTON_SELECTED = "call_center_sign_out_button_selected";
            public static final String MEETING_CAMERA_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED = "meeting_camera_permission_rationale_dialog_settings_button_pressed";
            public static final String MEETING_CAMERA_PERMISSION_RATIONALE_DIALOG_SHOWN = "meeting_camera_permission_rationale_dialog_shown";
            public static final String MEETING_AUDIO_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED = "meeting_audio_permission_rationale_dialog_settings_button_pressed";
            public static final String MEETING_AUDIO_PERMISSION_RATIONALE_DIALOG_SHOWN = "meeting_audio_permission_rationale_dialog_shown";
            public static final String MEETING_PERMISSION_ERROR_DIALOG_SETTINGS_BUTTON_PRESSED = "meeting_permission_error_dialog_settings_button_pressed";

            @Retention(SOURCE)
            @StringDef( {
                    ABOUT_LIST_ITEM_PRESSED,
                    ABOUT_MENU_ITEM_PRESSED,
                    ACCEPT_BUTTON_PRESSED,
                    ACCEPT_LICENSE_AGREEMENT_DIALOG_ACCEPT_BUTTON_PRESSED,
                    ACCEPT_LICENSE_AGREEMENT_DIALOG_CANCEL_BUTTON_PRESSED,
                    ACCEPT_LICENSE_AGREEMENT_DIALOG_SHOWN,
                    ACCEPT_VIDEO_CALL_BUTTON_PRESSED,
                    ACCEPT_VOICE_CALL_BUTTON_PRESSED,
                    ACCESS_DEVICE_NOT_FOUND_DIALOG_SHOWN,
                    ACTIVE_CALL_TRANSFER_OPTIONS_LIST_SHOWN,
                    ADD_CHAT_BUTTON_PRESSED,
                    ADD_CONFERENCE_CONTACT_BUTTON_PRESSED,
                    ADD_CONTACT_BUTTON_PRESSED,
                    ADD_ENTERPRISE_CONTACT_BUTTON_PRESSED,
                    ADD_GROUP_BUTTON_PRESSED,
                    ADD_LOCATION_BUTTON_PRESSED,
                    ADD_TO_CONTACTS_LIST_ITEM_ADD_BUTTON_PRESSED,
                    ADD_TO_CONTACTS_LIST_ITEM_COPIED,
                    ADD_TO_FAVORITES_LIST_ITEM_FAVORITE_BUTTON_PRESSED,
                    ADD_TO_FAVORITES_LIST_ITEM_UNFAVORITE_BUTTON_PRESSED,
                    ADD_TO_LOCAL_CONTACT_LIST_ITEM_COPIED,
                    ADD_TO_LOCAL_CONTACTS_LIST_ITEM_ADD_BUTTON_PRESSED,
                    ADD_VIDEO_TO_CALL_DIALOG_ACCEPT_BUTTON_PRESSED,
                    ADD_VIDEO_TO_CALL_DIALOG_DECLINE_BUTTON_PRESSED,
                    ADD_VIDEO_TO_CALL_DIALOG_SHOWN,
                    ALERT_ALL_LOCATIONS_SWITCH_CHECKED,
                    ALERT_ALL_LOCATIONS_SWITCH_UNCHECKED,
                    ALL_CALL_LOGS_FILTER_SELECTED,
                    ALLOW_TERMINATION_LIST_ITEM_CLICKED,
                    ALWAYS_ASK_RADIO_BUTTON_CHECKED,
                    ANSWER_CONFIRMATION_SWITCH_CHECKED,
                    ANSWER_CONFIRMATION_SWITCH_UNCHECKED,
                    APP_PREFERENCES_MENU_ITEM_PRESSED,
                    AUTOMATIC_RADIO_BUTTON_CHECKED,
                    AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED,
                    AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED,
                    AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    AVATAR_IMAGE_PRESSED,
                    AWAY_RADIO_BUTTON_CHECKED,
                    BACK_BUTTON_PRESSED,
                    BAD_SMS_NUMBER_INITIATED_DIALOG_OK_BUTTON_PRESSED,
                    BLOCK_MY_CALLER_ID_LIST_ITEM_PRESSED,
                    BUSY_RADIO_BUTTON_CHECKED,
                    CALL_BACK_CONFLICT_DIALOG_NO_BUTTON_PRESSED,
                    CALL_BACK_CONFLICT_DIALOG_SHOWN,
                    CALL_BACK_CONFLICT_DIALOG_YES_BUTTON_PRESSED,
                    CALL_BACK_INITIATED_DIALOG_OK_BUTTON_PRESSED,
                    CALL_BACK_INITIATED_DIALOG_SHOWN,
                    CALL_BACK_RADIO_BUTTON_CHECKED,
                    CALL_CONTROL_SWITCH_CHECKED,
                    CALL_CONTROL_SWITCH_UNCHECKED,
                    CALL_FORWARDING_ALWAYS_LIST_ITEM_PRESSED,
                    CALL_FORWARDING_WHEN_BUSY_LIST_ITEM_PRESSED,
                    CALL_FORWARDING_WHEN_UNANSWERED_LIST_ITEM_PRESSED,
                    CALL_FORWARDING_WHEN_UNREACHABLE_LIST_ITEM_PRESSED,
                    CALL_HISTORY_LIST_ITEM_LONG_PRESSED,
                    CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_CALL_DETAILS_BUTTON_PRESSED,
                    CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_CANCEL_BUTTON_PRESSED,
                    CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_DELETE_CALL_BUTTON_PRESSED,
                    CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_SHOWN,
                    CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_VIDEO_CALL_BUTTON_PRESSED,
                    CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_VOICE_CALL_BUTTON_PRESSED,
                    CALL_HISTORY_LIST_ITEM_PRESSED,
                    CALL_HISTORY_LIST_ITEM_SWIPED,
                    CALL_HISTORY_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED,
                    CALL_HISTORY_TAB_PRESSED,
                    CALL_HISTORY_TAB_SWIPED_TO,
                    CALL_QUALITY_ISSUES_DIALOG_SHOWN,
                    CALL_QUICK_ACTION_BUTTON_PRESSED,
                    CALL_SETTINGS_HELP_DIALOG_OK_BUTTON_PRESSED,
                    CALL_SETTINGS_HELP_DIALOG_SHOWN,
                    CALL_SETTINGS_MENU_ITEM_PRESSED,
                    CALL_THROUGH_CONFLICT_DIALOG_NO_BUTTON_PRESSED,
                    CALL_THROUGH_CONFLICT_DIALOG_SHOWN,
                    CALL_THROUGH_CONFLICT_DIALOG_YES_BUTTON_PRESSED,
                    CALL_THROUGH_RADIO_BUTTON_CHECKED,
                    CALL_TYPE_SELECTION_DIALOG_CALL_TYPE_SELECTED,
                    CALL_TYPE_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED,
                    CALL_TYPE_SELECTION_DIALOG_SHOWN,
                    CHANGE_PROFILE_PHOTO_DIALOG_CANCEL_BUTTON_PRESSED,
                    CHANGE_PROFILE_PHOTO_DIALOG_CLEAR_PHOTO_BUTTON_PRESSED,
                    CHANGE_PROFILE_PHOTO_DIALOG_NEW_PHOTO_BUTTON_PRESSED,
                    CHANGE_PROFILE_PHOTO_DIALOG_SHOWN,
                    CHAT_LIST_ITEM_PRESSED,
                    CHAT_QUICK_ACTION_BUTTON_PRESSED,
                    CHAT_TAB_PRESSED,
                    CHAT_TAB_SWIPED_TO,
                    CLEAR_ALL_LOGS_BUTTON_PRESSED,
                    CLEAR_ALL_LOGS_DIALOG_CANCEL_BUTTON_PRESSED,
                    CLEAR_ALL_LOGS_DIALOG_CLEAR_LOGS_BUTTON_PRESSED,
                    CLEAR_ALL_LOGS_DIALOG_SHOWN,
                    CONFERENCE_PHONE_NUMBER_LIST_ITEM_COPIED,
                    COMPANY_LIST_ITEM_COPIED,
                    CONTACT_LIST_ITEM_DELETE_PRESSED,
                    CONTACT_LIST_ITEM_FAVORITE_PRESSED,
                    CONTACT_LIST_ITEM_LONG_PRESSED,
                    CONTACT_LIST_ITEM_PRESSED,
                    CONTACT_LIST_ITEM_SWIPED,
                    CONTACT_LIST_TAB_PRESSED,
                    CONTACT_LIST_TAB_SWIPED_TO,
                    CONTACTS_LIST_ITEM_OPTIONS_DIALOG_CANCEL_BUTTON_PRESSED,
                    CONTACTS_LIST_ITEM_OPTIONS_DIALOG_EDIT_CONTACT_BUTTON_PRESSED,
                    CONTACTS_LIST_ITEM_OPTIONS_DIALOG_SHOWN,
                    CONTINUE_BUTTON_PRESSED,
                    COMPLETE_CALL_TRANSFER_BUTTON_PRESSED,
                    DECLINE_BUTTON_PRESSED,
                    DECLINE_CALL_BUTTON_PRESSED,
                    DECLINE_LICENSE_AGREEMENT_DIALOG_DECLINE_BUTTON_PRESSED,
                    DECLINE_LICENSE_AGREEMENT_DIALOG_REVIEW_BUTTON_PRESSED,
                    DECLINE_LICENSE_AGREEMENT_DIALOG_SHOWN,
                    DELETE_ALL_CALL_HISTORY_BUTTON_PRESSED,
                    DELETE_BUTTON_PRESSED,
                    DELETE_CALL_HISTORY_DIALOG_CANCEL_BUTTON_PRESSED,
                    DELETE_CALL_HISTORY_DIALOG_DELETE_BUTTON_PRESSED,
                    DELETE_CALL_HISTORY_DIALOG_SHOWN,
                    DELETE_CONTACT_DIALOG_CANCEL_BUTTON_PRESSED,
                    DELETE_CONTACT_DIALOG_DELETE_BUTTON_PRESSED,
                    DELETE_CONTACT_DIALOG_SHOWN,
                    DELETE_LOCATION_DIALOG_CANCEL_BUTTON_PRESSED,
                    DELETE_LOCATION_DIALOG_DELETE_BUTTON_PRESSED,
                    DELETE_LOCATION_DIALOG_SHOWN,
                    DIALER_TAB_PRESSED,
                    DIALER_TAB_SWIPED_TO,
                    DIALING_SERVICE_LIST_ITEM_PRESSED,
                    DIALING_SERVICE_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED,
                    DIALING_SERVICE_SELECTION_DIALOG_DIALING_SERVICE_SELECTED,
                    DIALING_SERVICE_SELECTION_DIALOG_SHOWN,
                    DIRECTORY_FILTER_SELECTED,
                    DO_NOT_DISTURB_LIST_ITEM_PRESSED,
                    DO_NOT_RING_WHILE_ON_CALL_SWITCH_CHECKED,
                    DO_NOT_RING_WHILE_ON_CALL_SWITCH_UNCHECKED,
                    DUPLICATE_LOCAL_CONTACT_EXISTS_DIALOG_SHOWN,
                    DUPLICATE_NEXTIVA_ANYWHERE_LOCATION_EXISTS_DIALOG_SHOWN,
                    DUPLICATE_SIMULTANEOUS_RING_LOCATION_EXISTS_DIALOG_SHOWN,
                    EDIT_BUTTON_PRESSED,
                    EMAIL_BUTTON_PRESSED,
                    EMAIL_LIST_ITEM_COPIED,
                    EMAIL_LIST_ITEM_EMAIL_BUTTON_PRESSED,
                    EMAIL_LOGS_TO_SUPPORT_BUTTON_PRESSED,
                    ENABLE_LOGGING_SWITCH_CHECKED,
                    ENABLE_LOGGING_SWITCH_UNCHECKED,
                    ENABLED_SWITCH_CHECKED,
                    ENABLED_SWITCH_UNCHECKED,
                    END_CALL_BUTTON_PRESSED,
                    END_CALL_TRANSFER_BUTTON_PRESSED,
                    FAVORITE_LIST_ITEM_COPIED,
                    FILE_LOGGING_SWITCH_CHECKED,
                    FILE_LOGGING_SWITCH_UNCHECKED,
                    FILTER_BUTTON_PRESSED,
                    FIRST_NAME_LIST_ITEM_COPIED,
                    GENERIC_ERROR_DIALOG_OK_BUTTON_PRESSED,
                    GENERIC_ERROR_DIALOG_SHOWN,
                    HAMBURGER_MENU_PRESSED,
                    HELP_BUTTON_PRESSED,
                    HELP_LIST_ITEM_PRESSED,
                    HELP_MENU_ITEM_PRESSED,
                    HOLD_BUTTON_DESELECTED,
                    HOLD_BUTTON_SELECTED,
                    IM_ADDRESS_LIST_ITEM_CHAT_BUTTON_PRESSED,
                    IM_ADDRESS_LIST_ITEM_COPIED,
                    INFO_TAB_PRESSED,
                    INFO_TAB_SWIPED_TO,
                    JOIN_ROOM_QUICK_ACTION_BUTTON_PRESSED,
                    KEYPAD_BUTTON_PRESSED,
                    LAST_NAME_LIST_ITEM_COPIED,
                    LEGAL_NOTICES_TAB_PRESSED,
                    LEGAL_NOTICES_TAB_SWIPED_TO,
                    LICENSE_TAB_PRESSED,
                    LICENSE_TAB_SWIPED_TO,
                    PRIVACY_TAB_PRESSED,
                    PRIVACY_TAB_SWIPED_TO,
                    LOCAL_ADDRESS_BOOK_FILTER_SELECTED,
                    LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED,
                    LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    LOCAL_CONTACTS_REQUEST_PERMISSION_BUTTON_PRESSED,
                    LOCATION_LIST_ITEM_PRESSED,
                    MARK_ALL_READ_BUTTON_PRESSED,
                    MISSED_CALL_LOGS_FILTER_SELECTED,
                    MOBILE_RADIO_BUTTON_CHECKED,
                    MORE_BUTTON_PRESSED,
                    MULTI_NUMBER_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED,
                    MULTI_NUMBER_SELECTION_DIALOG_NUMBER_SELECTED,
                    MULTI_NUMBER_SELECTION_DIALOG_SHOWN,
                    MUTE_BUTTON_DESELECTED,
                    MUTE_BUTTON_SELECTED,
                    MY_CONTACTS_FILTER_SELECTED,
                    MY_ONLINE_CONTACTS_FILTER_SELECTED,
                    NEW_CALL_BUTTON_PRESSED,
                    NEXTIVA_ANYWHERE_LIST_ITEM_PRESSED,
                    LOGIN_FAILED_DIALOG_SHOWN,
                    NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED,
                    NO_APP_FOUND_DIALOG_SHOWN,
                    NO_COMPATIBLE_BROWSER_FOUND_DIALOG_SHOWN,
                    NO_CONTACT_FOUND_DIALOG_OK_BUTTON_PRESSED,
                    NO_CONTACT_FOUND_DIALOG_SHOWN,
                    NO_INTERNET_DIALOG_OK_BUTTON_PRESSED,
                    NO_INTERNET_DIALOG_SETTINGS_BUTTON_PRESSED,
                    NO_INTERNET_DIALOG_SHOWN,
                    NO_LOG_FILE_DIALOG_OK_BUTTON_PRESSED,
                    NO_LOG_FILE_DIALOG_SHOWN,
                    NO_NUMBER_FOUND_DIALOG_OK_BUTTON_PRESSED,
                    NO_NUMBER_FOUND_DIALOG_SHOWN,
                    NUMBER_OF_RINGS_BUTTON_PRESSED,
                    ON_HOLD_CALL_HEADER_PRESSED,
                    PHONE_NUMBER_LIST_ITEM_COPIED,
                    PHONE_NUMBER_LIST_ITEM_VIDEO_CALL_BUTTON_PRESSED,
                    PHONE_NUMBER_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED,
                    POOR_CONNECTION_DIALOG_CLOSE_BUTTON_PRESSED,
                    POOR_CONNECTION_DIALOG_REMOVED,
                    POOR_CONNECTION_DIALOG_SHOWN,
                    POOR_CONNECTION_DIALOG_TRANSFER_TO_MOBILE_BUTTON_PRESSED,
                    POST_NOTIFICATIONS_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    POST_NOTIFICATIONS_REQUEST_PERMISSION_BUTTON_PRESSED,
                    PREVENT_DIVERTING_CALLS_SWITCH_CHECKED,
                    PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED,
                    PROGRESS_DIALOG_SHOWN,
                    PULL_CALL_BUTTON_PRESSED,
                    PULL_TO_REFRESH,
                    REMEMBER_PASSWORD_SWITCH_CHECKED,
                    REMEMBER_PASSWORD_SWITCH_UNCHECKED,
                    REMOTE_OFFICE_LIST_ITEM_PRESSED,
                    RETURN_TO_ACTIVE_CALL_PRESSED,
                    RING_SPLASH_SWITCH_CHECKED,
                    RING_SPLASH_SWITCH_UNCHECKED,
                    SAVE_BUTTON_PRESSED,
                    SCREEN_VIEW,
                    SEARCH_BUTTON_PRESSED,
                    SECTION_HEADER_COLLAPSED,
                    SECTION_HEADER_EXPANDED,
                    SEND_PERSONAL_SMS_LIST_ITEM_COPIED,
                    SEND_SMS_LIST_ITEM_SEND_SMS_BUTTON_PRESSED,
                    SETTINGS_BUTTON_PRESSED,
                    SIDE_NAV_CLOSED,
                    SIDE_NAV_OPENED,
                    SIGN_IN_BUTTON_PRESSED,
                    SIGN_OUT_MENU_ITEM_PRESSED,
                    SIMULTANEOUS_RING_LIST_ITEM_PRESSED,
                    SIP_LOGGING_SWITCH_CHECKED,
                    SIP_LOGGING_SWITCH_UNCHECKED,
                    SPEAKER_BUTTON_DESELECTED,
                    SPEAKER_BUTTON_SELECTED,
                    SWAP_CALL_BUTTON_PRESSED,
                    SWITCH_CAMERA_BUTTON_PRESSED,
                    THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED,
                    THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED,
                    THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    THIS_PHONE_NUMBER_LIST_ITEM_PRESSED,
                    THIS_PHONE_RADIO_BUTTON_CHECKED,
                    TRANSFER_TO_MOBILE_CALL_DIALOG_ACCEPT_BUTTON_PRESSED,
                    TRANSFER_TO_MOBILE_CALL_DIALOG_CANCEL_BUTTON_PRESSED,
                    TROUBLESHOOTING_LIST_ITEM_PRESSED,
                    UNABLE_TO_SAVE_CONTACT_DIALOG_OK_BUTTON_PRESSED,
                    UNABLE_TO_SAVE_CONTACT_DIALOG_SHOWN,
                    UNSAVED_CHANGES_DIALOG_CANCEL_BUTTON_PRESSED,
                    UNSAVED_CHANGES_DIALOG_DISCARD_BUTTON_PRESSED,
                    UNSAVED_CHANGES_DIALOG_SHOWN,
                    USER_HEADER_PRESSED,
                    VIDEO_BUTTON_DESELECTED,
                    VIDEO_BUTTON_SELECTED,
                    VIDEO_CALL_BUTTON_PRESSED,
                    VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED,
                    VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED,
                    VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    INITIAL_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    INITIAL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED,
                    INITIAL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED,
                    VIEW_PASSWORD_BUTTON_PRESSED,
                    VOICE_CALL_BUTTON_PRESSED,
                    VOICE_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED,
                    VOICE_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED,
                    VOICE_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    VOICEMAIL_CALL_BUTTON_LONG_PRESSED,
                    VISUAL_VOICEMAIL_TAB_PRESSED,
                    VISUAL_VOICEMAIL_TAB_SWIPED_TO,
                    VOIP_RADIO_BUTTON_CHECKED,
                    WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED,
                    WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED,
                    WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    XMPP_LOGGING_SWITCH_CHECKED,
                    XMPP_LOGGING_SWITCH_UNCHECKED,
                    DISPLAY_AUDIO_VIDEO_STATS_CHECKED,
                    DISPLAY_AUDIO_VIDEO_STATS_UNCHECKED,
                    DISPLAY_SIP_STATE_CHECKED,
                    DISPLAY_SIP_STATE_UNCHECKED,
                    DISPLAY_SIP_ERROR_CHECKED,
                    DISPLAY_SIP_ERROR_UNCHECKED,
                    CALL_CENTER_SIGN_IN_BUTTON_SELECTED,
                    CALL_CENTER_AVAILABLE_BUTTON_SELECTED,
                    CALL_CENTER_UNAVAILABLE_BUTTON_SELECTED,
                    CALL_CENTER_WRAP_UP_BUTTON_SELECTED,
                    CALL_CENTER_SIGN_OUT_BUTTON_SELECTED,
                    MEETING_CAMERA_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED,
                    MEETING_CAMERA_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    MEETING_AUDIO_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED,
                    MEETING_AUDIO_PERMISSION_RATIONALE_DIALOG_SHOWN,
                    MEETING_PERMISSION_ERROR_DIALOG_SETTINGS_BUTTON_PRESSED
            })
            public @interface Event {
            }
        }

        public static class FirebasePerformance {
            public static final String LOGIN = "login";
            public static final String OPEN_ID_REQUEST_CODE_AUTH = "open_id_request_code_auth";
            public static final String SIP_REGISTER = "sip_register";
            public static final String SIP_SERVICE_ON_CREATE = "sip_service_on_create";
            public static final String SIP_SERVICE_ON_REGISTER_SUCCESS = "sip_service_on_register_success";
            public static final String SIP_SERVICE_ON_REGISTER_FAILURE = "sip_service_on_register_failure";
            public static final String SIP_SERVICE_ON_INVITE_INCOMING = "sip_service_on_invite_incoming";
            public static final String SIP_SERVICE_ON_INVITE_TRYING = "sip_service_on_trying";
            public static final String SIP_SERVICE_ON_INVITE_SESSION_PROGRESS = "sip_service_on_invite_session_progress";
            public static final String SIP_SERVICE_ON_INVITE_RINGING = "sip_service_on_invite_ringing";
            public static final String SIP_SERVICE_ON_INVITE_ANSWERED = "sip_service_on_invite_answered";
            public static final String SIP_SERVICE_ON_INVITE_FAILURE = "sip_service_on_invite_failure";
            public static final String SIP_SERVICE_ON_INVITE_UPDATED = "sip_service_on_invite_updated";
            public static final String SIP_SERVICE_ON_INVITE_CONNECTED = "sip_service_on_invite_connected";
            public static final String SIP_SERVICE_ON_INVITE_CLOSED = "sip_service_on_invite_closed";
            public static final String SIP_SERVICE_ON_INVITE_BEGINING_FORWARD = "sip_service_on_invite_begining_forward";
            public static final String SIP_SERVICE_ON_REMOTE_UNHOLD = "sip_service_on_remote_unhold";
            public static final String SIP_SERVICE_ON_REMOTE_HOLD = "sip_service_on_remote_hold";
            public static final String SIP_SERVICE_ON_RECEIVED_REFER = "sip_service_on_received_refer";
            public static final String SIP_SERVICE_ON_REFER_ACCEPTED = "sip_service_on_refer_accepted";
            public static final String SIP_SERVICE_ON_REFER_REJECTED = "sip_service_on_refer_rejected";
            public static final String SIP_SERVICE_ON_TRANSFER_TRYING = "sip_service_on_transfer_ringing";
            public static final String SIP_SERVICE_ON_TRANSFER_RINGING = "sip_service_on_transfer_ringing";
            public static final String SIP_SERVICE_ON_ACTV_TRANSFER_SUCCESS = "sip_service_on_actv_transfer_success";
            public static final String SIP_SERVICE_ON_ACTV_TRANSFER_FAILURE = "sip_service_on_actv_transfer_failure";
            public static final String SIP_SERVICE_ON_RECEIVED_SIGNLING = "sip_service_on_received_signling";
            public static final String SIP_SERVICE_ON_SENDING_SIGNALING = "sip_service_on_sending_signaling";
            public static final String SIP_INCOMING_CALL_DISPLAY = "sip_register";

            @Retention(SOURCE)
            @StringDef( {
                    LOGIN,
                    SIP_REGISTER,
                    OPEN_ID_REQUEST_CODE_AUTH,
                    SIP_SERVICE_ON_CREATE,
                    SIP_SERVICE_ON_REGISTER_SUCCESS,
                    SIP_SERVICE_ON_REGISTER_FAILURE,
                    SIP_SERVICE_ON_INVITE_INCOMING,
                    SIP_SERVICE_ON_INVITE_TRYING,
                    SIP_SERVICE_ON_INVITE_SESSION_PROGRESS,
                    SIP_SERVICE_ON_INVITE_RINGING,
                    SIP_SERVICE_ON_INVITE_ANSWERED,
                    SIP_SERVICE_ON_INVITE_FAILURE,
                    SIP_SERVICE_ON_INVITE_UPDATED,
                    SIP_SERVICE_ON_INVITE_CONNECTED,
                    SIP_SERVICE_ON_INVITE_CLOSED,
                    SIP_SERVICE_ON_INVITE_BEGINING_FORWARD,
                    SIP_SERVICE_ON_REMOTE_UNHOLD,
                    SIP_SERVICE_ON_REMOTE_HOLD,
                    SIP_SERVICE_ON_REFER_ACCEPTED,
                    SIP_SERVICE_ON_RECEIVED_REFER,
                    SIP_SERVICE_ON_REFER_REJECTED,
                    SIP_SERVICE_ON_TRANSFER_TRYING,
                    SIP_SERVICE_ON_TRANSFER_RINGING,
                    SIP_SERVICE_ON_ACTV_TRANSFER_SUCCESS,
                    SIP_SERVICE_ON_ACTV_TRANSFER_FAILURE,
                    SIP_SERVICE_ON_RECEIVED_SIGNLING,
                    SIP_SERVICE_ON_SENDING_SIGNALING,
                    SIP_INCOMING_CALL_DISPLAY
            })
            public @interface CustomEvent {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End Net Enums
    // -------------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------------
    //region Session Enums
    // -------------------------------------------------------------------------------------------
    public static class Session {
        public static class NightModeState {

            public static final String NIGHT_MODE_STATE_LIGHT = "NIGHT_MODE_STATE_LIGHT";
            public static final String NIGHT_MODE_STATE_DARK = "NIGHT_MODE_STATE_DARK";
            public static final String NIGHT_MODE_STATE_AUTO = "NIGHT_MODE_STATE_AUTO";
            public static final String NIGHT_MODE_STATE_SYSTEM_DEFAULT = "NIGHT_MODE_STATE_SYSTEM_DEFAULT";

            @Retention(SOURCE)
            @StringDef( {
                    NIGHT_MODE_STATE_LIGHT,
                    NIGHT_MODE_STATE_DARK,
                    NIGHT_MODE_STATE_AUTO,
                    NIGHT_MODE_STATE_SYSTEM_DEFAULT
            })
            public @interface State {
            }
        }

        public static class DatabaseKey {

            public static final String ACCESS_DEVICE_PASSWORD = "ACCESS_DEVICE_PASSWORD";
            public static final String ACCESS_DEVICE_USERNAME = "ACCESS_DEVICE_USERNAME";
            public static final String ACCESS_DEVICE_TYPE_URL = "ACCESS_DEVICE_TYPE_URL";
            public static final String ACCESS_DEVICE_LINE_PORT = "ACCESS_DEVICE_LINE_PORT";
            public static final String ACCESS_DEVICE_VERSION = "ACCESS_DEVICE_VERSION";
            public static final String FEATURE_ACCESS_CODES = "FEATURE_ACCESS_CODES";
            public static final String LAST_DIALED_PHONE_NUMBER = "LAST_DIALED_PHONE_NUMBER";
            public static final String NEW_VOICEMAIL_MESSAGES_COUNT = "NEW_VOICEMAIL_MESSAGES_COUNT";
            public static final String NEW_VOICE_CALL_MESSAGES_COUNT = "NEW_VOICE_CALL_MESSAGES_COUNT";
            public static final String NEW_CHAT_MESSAGES_COUNT = "NEW_CHAT_MESSAGES_COUNT";
            public static final String NEW_SMS_MESSAGES_COUNT = "NEW_SMS_MESSAGES_COUNT";
            public static final String NEXTIVA_ANYWHERE_ENABLED = "NEXTIVA_ANYWHERE_ENABLED";
            public static final String NEXTIVA_ANYWHERE_SERVICE_SETTINGS = "NEXTIVA_ANYWHERE_SERVICE_SETTINGS";
            public static final String PASSWORD = "PASSWORD";
            public static final String REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
            public static final String REMOTE_OFFICE_ENABLED = "REMOTE_OFFICE_ENABLED";
            public static final String REMOTE_OFFICE_SERVICE_SETTINGS = "REMOTE_OFFICE_SERVICE_SETTINGS";
            public static final String SESSION_ID = "SESSION_ID";
            public static final String SELECTED_TENANT = "SELECTED_TENANT";
            public static final String USER_INFO = "USER_INFO";
            public static final String IDENTITY_VOICE = "IDENTITY_VOICE";
            public static final String TOKEN = "TOKEN";
            public static final String PUSH_NOTIFICATION_REGISTRATION_ID = "PUSH_NOTIFICATION_REGISTRATION_ID";
            public static final String USER_AVATAR = "USER_AVATAR";
            public static final String USER_DETAILS = "USER_DETAILS";
            public static final String USER_PRESENCE = "USER_PRESENCE";
            public static final String USER_PRESENCE_CONNECT = "USER_PRESENCE_CONNECT";
            public static final String IS_CONNECT_USER_PRESENCE_AUTOMATIC = "IS_CONNECT_USER_PRESENCE_AUTOMATIC";
            public static final String IS_USER_PRESENCE_AUTOMATIC = "IS_USER_PRESENCE_AUTOMATIC";
            public static final String USERNAME = "USERNAME";
            public static final String CALL_PULL = "CALL_PULL";
            public static final String MOBILE_CONFIG = "MOBILE_CONFIG";
            public static final String COPY_OF_MOBILE_CONFIG_FOR_TESTING = "COPY_OF_MOBILE_CONFIG_FOR_TESTING";
            public static final String UMS_HOST = "UMS_HOST";
            public static final String FEATURE_FLAGS = "FEATURE_FLAGS";
            public static final String ACCOUNT_INFORMATION = "ACCOUNT_INFORMATION";
            public static final String PHONE_NUMBER_INFORMATION = "PHONE_NUMBER_INFORMATION";
            public static final String PRODUCTS = "PRODUCTS";
            public static final String USERS_TEAMS = "USERS_TEAMS";
            public static final String ALL_TEAMS = "ALL_TEAMS";
            public static final String CONTACT_MANAGEMENT_PRIVILEGE = "CONTACT_MANAGEMENT_PRIVILEGE";
            public static final String CURRENT_USER = "CURRENT_USER";
            public static final String ENABLED_AUDIO_CODECS = "ENABLED_AUDIO_CODECS";
            public static final String ENABLED_VIDEO_CODECS = "ENABLED_VIDEO_CODECS";
            public static final String ECHO_CANCELLATION = "ECHO_CANCELLATION";
            public static final String AEC_AGGRESSIVENESS = "AEC_AGGRESSIVENESS";
            public static final String NOISE_SUPPRESSION = "NOISE_SUPPRESSION";
            public static final String ALLOW_TERMINATION = "ALLOW_TERMINATION";

            @Retention(SOURCE)
            @StringDef( {
                    ACCESS_DEVICE_PASSWORD,
                    ACCESS_DEVICE_USERNAME,
                    ACCESS_DEVICE_TYPE_URL,
                    ACCESS_DEVICE_LINE_PORT,
                    ACCESS_DEVICE_VERSION,
                    FEATURE_ACCESS_CODES,
                    LAST_DIALED_PHONE_NUMBER,
                    NEW_VOICEMAIL_MESSAGES_COUNT,
                    NEW_VOICE_CALL_MESSAGES_COUNT,
                    NEW_CHAT_MESSAGES_COUNT,
                    NEW_SMS_MESSAGES_COUNT,
                    NEXTIVA_ANYWHERE_ENABLED,
                    NEXTIVA_ANYWHERE_SERVICE_SETTINGS,
                    PASSWORD,
                    REMEMBER_PASSWORD,
                    REMOTE_OFFICE_ENABLED,
                    REMOTE_OFFICE_SERVICE_SETTINGS,
                    SESSION_ID,
                    SELECTED_TENANT,
                    USER_INFO,
                    IDENTITY_VOICE,
                    TOKEN,
                    PUSH_NOTIFICATION_REGISTRATION_ID,
                    USER_AVATAR,
                    USER_DETAILS,
                    USER_PRESENCE,
                    USER_PRESENCE_CONNECT,
                    IS_CONNECT_USER_PRESENCE_AUTOMATIC,
                    IS_USER_PRESENCE_AUTOMATIC,
                    USERNAME,
                    CALL_PULL,
                    MOBILE_CONFIG,
                    COPY_OF_MOBILE_CONFIG_FOR_TESTING,
                    UMS_HOST,
                    FEATURE_FLAGS,
                    ACCOUNT_INFORMATION,
                    PHONE_NUMBER_INFORMATION,
                    PRODUCTS,
                    USERS_TEAMS,
                    ALL_TEAMS,
                    CONTACT_MANAGEMENT_PRIVILEGE,
                    CURRENT_USER,
                    ENABLED_AUDIO_CODECS,
                    ENABLED_VIDEO_CODECS,
                    ECHO_CANCELLATION,
                    AEC_AGGRESSIVENESS,
                    NOISE_SUPPRESSION,
                    ALLOW_TERMINATION
            })
            public @interface Key {
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    //endregion End User Details Enums
    // -------------------------------------------------------------------------------------------

    public static class AppStates {
        public static final int ACTIVE_FORGROUND = 1;
        public static final int PAUSED = 2;
        public static final int STOPPED = 3;
        public static final int DESTORYED = 4;


        @Retention(SOURCE)
        @IntDef( {
                ACTIVE_FORGROUND,
                PAUSED,
                STOPPED,
                DESTORYED
        })
        public @interface AppState {
        }
    }

    public static class BrowserPackages {
        public static final String CHROME = "com.android.chrome";
        public static final String CHROME_APPS = "com.google.android.apps.chrome";
        public static final String CHROME_BETA = "com.android.chrome.beta";
        public static final String CHROME_CANARY = "com.chrome.canary";
        public static final String CHROME_DEV = "com.chrome.dev";
        public static final String CHROME_CUSTOM = "com.sec.android.app.chromecustomizations";
        public static final String SAMSUNG = "com.sec.android.app.sbrowser";
        public static final String FIREFOX = "org.mozilla.firefox";

        @Retention(SOURCE)
        @StringDef( {
                CHROME,
                CHROME_APPS,
                CHROME_BETA,
                CHROME_CANARY,
                CHROME_DEV,
                CHROME_CUSTOM,
                SAMSUNG,
                FIREFOX
        })
        public @interface BrowserPackage {
        }
    }

    public static class VoicemailRating {
        public static final String POSITIVE = "POSITIVE";
        public static final String NEGATIVE = "NEGATIVE";

        @Retention(SOURCE)
        @StringDef( {
                POSITIVE,
                NEGATIVE
        })
        public @interface Rating {
        }
    }

    // --------------------------------------------------------------------------------------------
    //region Platform Enums
    // --------------------------------------------------------------------------------------------
    public static class Platform {
        public static class FeatureFlags {

            public static final String VISUAL_VOICE_MAIL = "Mobile.VisualVoicemail";
            public static final String LOGGING = "Mobile.Logging";
            public static final String CONNECT = "Mobile.Connect";
            public static final String SMS = "Mobile.SMS";
            public static final String TEAM_CHAT = "Mobile.TeamChat";
            public static final String VIDEO_CALLS = "Mobile.VideoCalls";
            public static final String PERSONAL_SCHEDULES = "Mobile.Personal.Schedules";
            public static final String VOICEMAIL_COUNT = "Voice.SeparateVoicemailChannel";
            public static final String CUSTOM_TONE = "Mobile.CustomTone";
            public static final String VOICE_LARGE_PAGE = "Mobile.Voice.LargePageSize";
            public static final String COMMUNICATIONS_BULK_DELETES = "Mobile.Communications.BulkDeletes";
            public static final String COMMUNICATIONS_BULK_UPDATES = "Mobile.Communications.BulkUpdates";
            public static final String COMMUNICATIONS_BULK_ACTIONS_UPDATE = "Mobile.Communications.BulkActionsUpdate";
            public static final String SMS_CAMPAIGN_VALIDATION = "Sms.CampaignValidation";
            public static final String BLOCK_NUMBER_FOR_CALLING = "Mobile.BlockNumberForCalling";

            @Retention(SOURCE)
            @StringDef( {
                    VISUAL_VOICE_MAIL,
                    LOGGING,
                    CONNECT,
                    LOGGING,
                    SMS,
                    TEAM_CHAT,
                    VIDEO_CALLS,
                    PERSONAL_SCHEDULES,
                    VOICEMAIL_COUNT,
                    CUSTOM_TONE,
                    VOICE_LARGE_PAGE,
                    COMMUNICATIONS_BULK_DELETES,
                    COMMUNICATIONS_BULK_UPDATES,
                    COMMUNICATIONS_BULK_ACTIONS_UPDATE,
                    SMS_CAMPAIGN_VALIDATION,
                    BLOCK_NUMBER_FOR_CALLING
            })
            public @interface Type {
            }
        }


        public static class FeatureFlagState {
            public static final String ENABLED = "ENABLED";
            public static final String DISABLED = "DISABLED";
            public static final String NOT_FOUND = "NOT_FOUND";

            @Retention(SOURCE)
            @StringDef( {
                    ENABLED,
                    DISABLED,
                    NOT_FOUND
            })
            public @interface State {
            }
        }

        public static class ConnectContactLongClickAction {
            public static final int MAKE_A_CALL = 0;
            public static final int SEND_A_TEXT = 1;
            public static final int COPY_TO_CLIPBOARD = 2;
            public static final int DIAL_EXTENSION = 3;
            public static final int BLOCK_NUMBER = 4;

            @Retention(SOURCE)
            @IntDef( {
                    MAKE_A_CALL,
                    SEND_A_TEXT,
                    DIAL_EXTENSION,
                    BLOCK_NUMBER
            })
            public @interface Action {
            }
        }

        public static class ConnectContactGroups {
            public static final String FAVORITES = "FAVORITES";
            public static final String TEAMMATES = "TEAMMATES";
            public static final String BUSINESS = "BUSINESS";
            public static final String ALL_CONTACTS = "ALL_CONTACTS";

            @Retention(SOURCE)
            @StringDef( {
                    FAVORITES,
                    TEAMMATES,
                    BUSINESS,
                    ALL_CONTACTS
            })
            public @interface GroupType {
            }
        }

        public static class ConnectContactDetailSections {
            public static final String PRIMARY = "PRIMARY";
            public static final String ADDITIONAL = "ADDITIONAL";

            @Retention(SOURCE)
            @StringDef( {
                    PRIMARY,
                    ADDITIONAL
            })
            public @interface SectionType {
            }
        }

        public static class ConnectCallsFilter {
            public static final int ALL = 0;
            public static final int MISSED = 1;
            public static final int VOICEMAIL = 2;

            @Retention(SOURCE)
            @IntDef( {
                    ALL,
                    MISSED,
                    VOICEMAIL
            })
            public @interface Filter {
            }
        }

        public static class ConnectSmsFilter {
            public static final String  ALL = "ALL";

            @Retention(SOURCE)
            @StringDef( {
                    ALL
            })
            public @interface Filter {
            }
        }

        public static class ConnectHomeChannels {
            public static final int CALLS = 0;
            public static final int MESSAGES = 1;
            public static final int TEAM_CHATS = 2;

            @Retention(SOURCE)
            @IntDef( {
                    CALLS,
                    MESSAGES,
                    TEAM_CHATS
            })
            public @interface Channel {
            }
        }

        public static class ConnectContactDetailClickAction {
            public static final int NONE = -1;
            public static final int LINK = 0;
            public static final int ADDRESS = 1;
            public static final int EMAIL = 2;
            public static final int PHONE = 3;
            public static final int ROOM_DETAILS = 4;
            public static final int ROOM_CONVERSATION = 5;

            @Retention(SOURCE)
            @IntDef( {
                    NONE,
                    LINK,
                    ADDRESS,
                    EMAIL,
                    PHONE,
                    ROOM_DETAILS,
                    ROOM_CONVERSATION
            })
            public @interface Action {
            }
        }

        public static class ConnectContactListItemImportState {
            public static final int UNSELECTED = 0;
            public static final int SELECTED = 1;
            public static final int IMPORTED = 2;

            @Retention(SOURCE)
            @IntDef( {
                    UNSELECTED,
                    SELECTED,
                    IMPORTED
            })
            public @interface State {
            }
        }

        public static class WebSocketApplications {
            public static final String APPLICATION_UNKNOWN = "APPLICATION_UNKNOWN";
            public static final String APPLICATION_VOICE_CALL = "APPLICATION_VOICE_CALL";
            public static final String APPLICATION_VOICE_SETTINGS = "APPLICATION_VOICE_SETTINGS";
            public static final String APPLICATION_SMS = "APPLICATION_SMS";
            public static final String APPLICATION_CHAT = "APPLICATION_CHAT";
            public static final String APPLICATION_EMAIL = "APPLICATION_EMAIL";
            public static final String APPLICATION_MEDIA_CALL = "APPLICATION_MEDIA_CALL";
            public static final String APPLICATION_CONVERSATION = "APPLICATION_CONVERSATION";
            public static final String APPLICATION_CALL_CENTER = "APPLICATION_CALL_CENTER";
            public static final String APPLICATION_CALENDAR = "APPLICATION_CALENDAR";
            public static final String APPLICATION_CONNECT = "APPLICATION_CONNECT";
            public static final String APPLICATION_CONTACT = "APPLICATION_CONTACT";
            public static final String APPLICATION_GLOBAL_STORE = "APPLICATION_GLOBAL_STORE";

            @Retention(SOURCE)
            @StringDef( {
                    APPLICATION_UNKNOWN,
                    APPLICATION_VOICE_CALL,
                    APPLICATION_VOICE_SETTINGS,
                    APPLICATION_SMS,
                    APPLICATION_CHAT,
                    APPLICATION_EMAIL,
                    APPLICATION_MEDIA_CALL,
                    APPLICATION_CONVERSATION,
                    APPLICATION_CALL_CENTER,
                    APPLICATION_CALENDAR,
                    APPLICATION_CONNECT,
                    APPLICATION_CONTACT,
                    APPLICATION_GLOBAL_STORE
            })
            public @interface SectionType {
            }
        }

        public static class WebSocketMessageEvents {
            public static final String MESSAGE_CREATED = "MESSAGE_CREATED";
            public static final String MESSAGE_UPDATED = "MESSAGE_UPDATED";
            public static final String MESSAGE_DELETED = "MESSAGE_DELETED";

            @Retention(SOURCE)
            @StringDef( {
                    MESSAGE_CREATED,
                    MESSAGE_UPDATED,
                    MESSAGE_DELETED
            })
            public @interface EventType {}
        }

        public static class WebSocketMessageTypes {
            public static final String MESSAGE = "message";
            public static final String MESSAGE_STATUS = "message_status";
            public static final String MESSAGE_BULK_ACTION = "bulk_action";
            public static final String MESSAGE_CHAT_REGULAR ="CHAT_REGULAR";
            public static final String MESSAGE_ROOM_ADD_MEMBER = "ROOM_ADD_MEMBER";
            public static final String MESSAGE_ROOM_REMOVE_MEMBER = "ROOM_REMOVE_MEMBER";
            public static final String MESSAGE_ROOM_CREATE = "ROOM_CREATE";

            @Retention(SOURCE)
            @StringDef({
                    MESSAGE,
                    MESSAGE_STATUS,
                    MESSAGE_CHAT_REGULAR,
                    MESSAGE_ROOM_ADD_MEMBER,
                    MESSAGE_ROOM_REMOVE_MEMBER,
                    MESSAGE_ROOM_CREATE
            })
            public @interface MessageType {}
        }

        public static class ConversationChannels {
            public static final String VOICE = "VOICE";
            public static final String SMS = "SMS";
            public static final String VOICEMAIL = "VOICEMAIL";

            @Retention(SOURCE)
            @StringDef({
                    VOICE,
                    SMS,
                    VOICEMAIL
            })
            public @interface Channel {}
        }

        public static class ViewsToShow{
            public static final int NO_VIEW = -1;
            public static final int CALLS = 0;
            public static final int CALLS_MISSED = 1;
            public static final int CALLS_VOICEMAIL = 2;
            public static final int MESSAGING = 3;
            public static final int MEETINGS = 4;
            public static final int ROOMS = 5;
            public static final int CONTACTS = 6;
            public static final int CHAT = 7;
            public static final int CALENDAR = 8;
            public static final int MORE = 100;

            @Retention(SOURCE)
            @IntDef({
                    CALLS,
                    CALLS_MISSED,
                    CALLS_VOICEMAIL,
                    MESSAGING,
                    MEETINGS,
                    ROOMS,
                    CONTACTS,
                    CHAT,
                    CALENDAR,
                    MORE
            })
            public @interface ViewToShow {}
        }
    }
    // --------------------------------------------------------------------------------------------
    //endregion End Platform Enums
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Font Awesome Enums
    // --------------------------------------------------------------------------------------------
    public static class FontAwesomeIconType {
        public static final int REGULAR = 0;
        public static final int SOLID = 1;
        public static final int BRAND = 2;
        public static final int CUSTOM = 3;
        public static final int DUOTONE = 4;

        @Retention(SOURCE)
        @IntDef( {
                REGULAR,
                SOLID,
                BRAND,
                CUSTOM,
                DUOTONE
        })
        public @interface Type {
        }
    }

    public static class FontAwesomeVersion{
        public static final int FA_V5 = 0;
        public static final int FA_V6 = 1;


        @Retention(SOURCE)
        @IntDef({
                FA_V5,
                FA_V6
        })
        public @interface Type {
        }
    }
    // --------------------------------------------------------------------------------------------
    //endregion End Font Awesome Enums
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------

    public static class DesignSystemResourceType {
        public static final int SECTION = 0;
        public static final int COLOR = 1;
        public static final int FONT = 2;
        public static final int CUSTOM = 3;

        @Retention(SOURCE)
        @IntDef( {
                SECTION,
                COLOR,
                FONT,
                CUSTOM
        })
        public @interface Type {
        }
    }

    public static class Workers {
        public static final String LOG_POST = "LOG_PORT_WORKER";

        @Retention(SOURCE)
        @StringDef( {
                LOG_POST
        })
        public @interface Type {
        }

    }


    // --------------------------------------------------------------------------------------------
    //region Internet Connect Types Enums
    // --------------------------------------------------------------------------------------------
    public static class InternetConnectTypes {
        public static final String UNKNOWN = "UNKNOWN";
        public static final String WIFI = "WIFI";
        public static final String MOBILE = "MOBILE";
        public static final String ETHERNET = "ETHERNET";
        public static final String BLUETOOTH = "BLUETOOTH";
        public static final String DUMMY = "DUMMY";
        public static final String MOBILE_DUN = "MOBILE DUN";
        public static final String MOBILE_HIPRI = "MOBILE HIPRI";
        public static final String MOBILE_SUPL = "MOBILE SUPL";
        public static final String VPN = "VPN";
        public static final String WIMAX = "WIMAX";

        @Retention(SOURCE)
        @StringDef( {
                WIFI
        })

        public @interface Type {
        }
    }
    // --------------------------------------------------------------------------------------------
    //endregion End Internet Connect Types Enums
    // --------------------------------------------------------------------------------------------

    public static class License {
        public static class Products {
            public static final String SURVEYS = "Surveys";
            public static final String VOICE_GLOBAL = "VOICE_GLOBAL";
            public static final String CALENDAR = "CALENDAR";
            public static final String DATA_MIGRATION = "Data-Migration";
            public static final String RULES_ENGINE = "RULES_ENGINE";
            public static final String NEXTIVA_CONNECT = "NEXTIVA_CONNECT";
            public static final String SMS = "SMS";
            public static final String EMAIL = "EMAIL";
            public static final String COLLABORATION = "COLLABORATION";
            public static final String VOICE_ANALYTICS = "VOICE_ANALYTICS";
            public static final String CRM = "CRM";
            public static final String VOICE = "Voice";

            @Retention(SOURCE)
            @StringDef( {
                    SURVEYS,
                    VOICE_GLOBAL,
                    CALENDAR,
                    DATA_MIGRATION,
                    RULES_ENGINE,
                    NEXTIVA_CONNECT,
                    SMS,
                    EMAIL,
                    COLLABORATION,
                    VOICE_ANALYTICS,
                    CRM,
                    VOICE
            })
            public @interface Product {
            }
        }

        public static final class Features {
            public static final String SURVERYS_BASE_FEATURE = "SURVEYS_BASE_FEATURE";
            public static final String PLATFORM_VOICE_ACCESS = "PLATFORM_VOICE_ACCESS";
            public static final String MEETING_SCHEDULING = "MEETING_SCHEDULING";
            public static final String CALENDAR = "CALENDAR";
            public static final String CALENDAR_BASE_FEATURE = "CALENDAR_BASE_FEATURE";
            public static final String INTEGRATIONS = "INTEGRATIONS";
            public static final String BASE_DATA_MIGRATION = "Base-Data-Migration";
            public static final String RULES_ENGINE_BASE_FEATURE = "RULES_ENGINE_BASE_FEATURE";
            public static final String BASE_NEXTIVA_CONNECT = "BASE_NEXTIVA_CONNECT";
            public static final String BUSINESS_CONTACTS = "BUSINESS_CONTACTS";
            public static final String CONVERSATIONS = "CONVERSATIONS";
            public static final String SMS_BASE = "SMS_BASE";
            public static final String EMAIL = "EMAIL";
            public static final String VIDEO_CALL_ME = "VIDEO_CALL_ME";
            public static final String VIDEO_GUEST_PARTICIPANTS = "VIDEO_GUEST_PARTICIPANTS";
            public static final String VIDEO_RECORDING_EXT_STORAGE = "VIDEO_RECORDING_EXT_STORAGE";
            public static final String VIDEO = "VIDEO";
            public static final String VIDEO_RECORDING = "VIDEO_RECORDING";
            public static final String CHAT = "CHAT";
            public static final String ATTACHMENTS = "ATTACHMENTS";
            public static final String ATTACHMENTS_EXTENDED = "ATTACHMENTS_EXTENDED";
            public static final String VOICE_ANALYTICS = "VOICE_ANALYTICS";
            public static final String CRM_BASE_FEATURE = "CRM_BASE_FEATURE";
            public static final String VOICE_PORTAL_CALLING = "Voice Portal Calling";
            public static final String DO_NOT_DISTURB = "Do Not Disturb";
            public static final String CHARGE_NUMBER = "Charge Number";
            public static final String VOICE_MESSAGING_USER_VIDEO = "Voice Messaging User - Video";
            public static final String CALL_FORWARDING_NO_ANSWER = "Call Forwarding No Answer";
            public static final String SELECTIVE_CALL_REJECTION = "Selective Call Rejection";
            public static final String POLYCOM_PHONE_SERVICES = "Polycom Phone Services";
            public static final String CALL_NOTIFY = "Call Notify";
            public static final String AUTOMATIC_CALLBACK = "Automatic Callback";
            public static final String IN_CALL_SERVICE_ACTIVATION = "In-Call Service Activation";
            public static final String CALL_FORWARDING_SELECTIVE = "Call Forwarding Selective";
            public static final String HOTELING_HOST = "Hoteling Host";
            public static final String PRIVACY = "Privacy";
            public static final String DIVERSION_INHIBITOR = "Diversion Inhibitor";
            public static final String SIMULTANEOUS_RING_PERSONAL = "Simultaneous Ring Personal";
            public static final String SHARED_CALL_APPEARANCE_5 = "Shared Call Appearance 5";
            public static final String CALL_FORWARDING_ALWAYS = "Call Forwarding Always";
            public static final String SPEED_DIAL_8 = "Speed Dial 8";
            public static final String BASIC_CALL_LOGS = "Basic Call Logs";
            public static final String CALL_FORWARDING_NOT_REACHABLE = "Call Forwarding Not Reachable";
            public static final String INTEGRATED_IMP = "Integrated IMP";
            public static final String DIRECTED_CALL_PICKUP = "Directed Call Pickup";
            public static final String BARGE_IN_EXEMPT = "Barge-in Exempt";
            public static final String CALLING_LINE_ID_DELIVERY_BLOCKING = "Calling Line ID Delivery Blocking";
            public static final String CONNECTED_LINE_IDENTIFICATION_RESTRICTION = "Connected Line Identification Restriction";
            public static final String CUSTOM_RINGBACK_USER = "Custom Ringback User";
            public static final String ALTERNATE_NUMBERS = "Alternate Numbers";
            public static final String CALL_RETURN = "Call Return";
            public static final String CONNECTED_LINE_IDENTIFICATION_PRESENTATION = "Connected Line Identification Presentation";
            public static final String SPEED_DIAL_100 = "Speed Dial 100";
            public static final String GROUP_NIGHT_FORWARDING = "Group Night Forwarding";
            public static final String SELECTIVE_CALL_ACCEPTANCE = "Selective Call Acceptance";
            public static final String CLIENT_LICENSE_17 = "Client License 17";
            public static final String CLIENT_LICENSE_18 = "Client License 18";
            public static final String CALLING_NAME_RETRIEVAL = "Calling Name Retrieval";
            public static final String BROADWORKS_ANYWHERE = "BroadWorks Anywhere";
            public static final String CALL_TRANSFER = "Call Transfer";
            public static final String CUSTOM_RINGBACK_USER_CALL_WAITING = "Custom Ringback User - Call Waiting";
            public static final String INTERNAL_CALLING_LINE_ID_DELIVERY = "Internal Calling Line ID Delivery";
            public static final String MWI_DELIVERY_TO_MOBILE_ENDPOINT = "MWI Delivery to Mobile Endpoint";
            public static final String ANONYMOUS_CALL_REJECTION = "Anonymous Call Rejection";
            public static final String N_WAY_CALL = "N-Way Call";
            public static final String CUSTOMER_ORIGINATED_TRACE = "Customer Originated Trace";
            public static final String PUSH_TO_TALK = "Push to Talk";
            public static final String VIDEO_ON_HOLD_USER = "Video On Hold User";
            public static final String CLASSMARK = "Classmark";
            public static final String BUSY_LAMP_FIELD = "Busy Lamp Field";
            public static final String VIRTUAL_ON_NET_ENTERPRISE_EXTENSIONS = "Virtual On-Net Enterprise Extensions";
            public static final String DIRECTED_CALL_PICKUP_WITH_BARGE_IN = "Directed Call Pickup with Barge-in";
            public static final String MALICIOUS_CALL_TRACE = "Malicious Call Trace";
            public static final String PRIORITY_ALERT = "Priority Alert";
            public static final String LOCATION_BASED_CALLING_RESTRICTIONS = "Location-Based Calling Restrictions";
            public static final String REMOTE_OFFICE = "Remote Office";
            public static final String FLASH_CALL_HOLD = "Flash Call Hold";
            public static final String CALLING_NAME_DELIVERY = "Calling Name Delivery";
            public static final String LAST_NUMBER_REDIAL = "Last Number Redial";
            public static final String MULTIPLE_CALL_ARRANGEMENT = "Multiple Call Arrangement";
            public static final String CALL_WAITING = "Call Waiting";
            public static final String THREE_WAY_CALL = "Three-Way Call";
            public static final String CALLING_NUMBER_DELIVERY = "Calling Number Delivery";
            public static final String CALL_ME_NOW = "Call Me Now";
            public static final String AUTOMATIC_HOLD_RETRIEVE = "Automatic Hold/Retrieve";
            public static final String DIRECTORY_NUMBER_HUNTING = "Directory Number Hunting";
            public static final String CALL_CENTER_MONITORING = "Call Center Monitoring";
            public static final String BROADTOUCH_BUSINESS_COMMUNICATOR_TABLET_VIDEO = "BroadTouch Business Communicator Tablet - Video";
            public static final String PRE_ALERTING_ANNOUNCEMENT = "Pre-alerting Announcement";
            public static final String CALLING_PARTY_CATEGORY = "Calling Party Category";
            public static final String CALL_FORWARDING_BUSY = "Call Forwarding Busy";
            public static final String EXTERNAL_CALLING_LINE_ID_DELIVERY = "External Calling Line ID Delivery";
            public static final String HOTELING_GUEST = "Hoteling Guest";
            public static final String ENHANCED_CALL_LOGS = "Enhanced Call Logs";
            public static final String SEQUENTIAL_RING = "Sequential Ring";
            public static final String TWO_STAGE_DIALING = "Two-Stage Dialing";
            public static final String COLLABORATE_SHARING = "Collaborate - Sharing";
            public static final String VOICE_MESSAGING_USER = "Voice Messaging User";
            public static final String AUTHENTICATION = "Authentication";
            public static final String CUSTOM_RINGBACK_USER_VIDEO = "Custom Ringback User - Video";
            public static final String INTERCEPT_USER = "Intercept User";
            public static final String COMMUNICATION_BARRING_USER_CONTROL = "Communication Barring User-Control";
            public static final String SHARED_CALL_APPEARANCE_35 = "Shared Call Appearance 35";
            public static final String EXTERNAL_CUSTOM_RINGBACK = "External Custom Ringback";
            public static final String VOICEMAIL_TRANSCRIPTION = "VOICEMAIL_TRANSCRIPTION";
            public static final String TEAM_SMS = "TEAM_SMS";

            @Retention(SOURCE)
            @StringDef( {
                    SURVERYS_BASE_FEATURE,
                    PLATFORM_VOICE_ACCESS,
                    MEETING_SCHEDULING,
                    CALENDAR,
                    CALENDAR_BASE_FEATURE,
                    INTEGRATIONS,
                    BASE_DATA_MIGRATION,
                    RULES_ENGINE_BASE_FEATURE,
                    BASE_NEXTIVA_CONNECT,
                    BUSINESS_CONTACTS,
                    CONVERSATIONS,
                    SMS_BASE,
                    EMAIL,
                    VIDEO_CALL_ME,
                    VIDEO_GUEST_PARTICIPANTS,
                    VIDEO_RECORDING_EXT_STORAGE,
                    VIDEO,
                    VIDEO_RECORDING,
                    CHAT,
                    ATTACHMENTS,
                    ATTACHMENTS_EXTENDED,
                    VOICE_ANALYTICS,
                    CRM_BASE_FEATURE,
                    VOICE_PORTAL_CALLING,
                    DO_NOT_DISTURB,
                    CHARGE_NUMBER,
                    VOICE_MESSAGING_USER_VIDEO,
                    CALL_FORWARDING_NO_ANSWER,
                    SELECTIVE_CALL_REJECTION,
                    POLYCOM_PHONE_SERVICES,
                    CALL_NOTIFY,
                    AUTOMATIC_CALLBACK,
                    IN_CALL_SERVICE_ACTIVATION,
                    CALL_FORWARDING_SELECTIVE,
                    HOTELING_HOST,
                    PRIVACY,
                    DIVERSION_INHIBITOR,
                    SIMULTANEOUS_RING_PERSONAL,
                    SHARED_CALL_APPEARANCE_5,
                    CALL_FORWARDING_ALWAYS,
                    SPEED_DIAL_8,
                    BASIC_CALL_LOGS,
                    CALL_FORWARDING_NOT_REACHABLE,
                    INTEGRATED_IMP,
                    DIRECTED_CALL_PICKUP,
                    BARGE_IN_EXEMPT,
                    CALLING_LINE_ID_DELIVERY_BLOCKING,
                    CONNECTED_LINE_IDENTIFICATION_RESTRICTION,
                    CUSTOM_RINGBACK_USER,
                    ALTERNATE_NUMBERS,
                    CALL_RETURN,
                    CONNECTED_LINE_IDENTIFICATION_PRESENTATION,
                    SPEED_DIAL_100,
                    GROUP_NIGHT_FORWARDING,
                    SELECTIVE_CALL_ACCEPTANCE,
                    CLIENT_LICENSE_17,
                    CLIENT_LICENSE_18,
                    CALLING_NAME_RETRIEVAL,
                    BROADWORKS_ANYWHERE,
                    CALL_TRANSFER,
                    CUSTOM_RINGBACK_USER_CALL_WAITING,
                    INTERNAL_CALLING_LINE_ID_DELIVERY,
                    MWI_DELIVERY_TO_MOBILE_ENDPOINT,
                    ANONYMOUS_CALL_REJECTION,
                    N_WAY_CALL,
                    CUSTOMER_ORIGINATED_TRACE,
                    PUSH_TO_TALK,
                    VIDEO_ON_HOLD_USER,
                    CLASSMARK,
                    BUSY_LAMP_FIELD,
                    VIRTUAL_ON_NET_ENTERPRISE_EXTENSIONS,
                    DIRECTED_CALL_PICKUP_WITH_BARGE_IN,
                    MALICIOUS_CALL_TRACE,
                    PRIORITY_ALERT,
                    LOCATION_BASED_CALLING_RESTRICTIONS,
                    REMOTE_OFFICE,
                    FLASH_CALL_HOLD,
                    CALLING_NAME_DELIVERY,
                    LAST_NUMBER_REDIAL,
                    MULTIPLE_CALL_ARRANGEMENT,
                    CALL_WAITING,
                    THREE_WAY_CALL,
                    CALLING_NUMBER_DELIVERY,
                    CALL_ME_NOW,
                    AUTOMATIC_HOLD_RETRIEVE,
                    DIRECTORY_NUMBER_HUNTING,
                    CALL_CENTER_MONITORING,
                    BROADTOUCH_BUSINESS_COMMUNICATOR_TABLET_VIDEO,
                    PRE_ALERTING_ANNOUNCEMENT,
                    CALLING_PARTY_CATEGORY,
                    CALL_FORWARDING_BUSY,
                    EXTERNAL_CALLING_LINE_ID_DELIVERY,
                    HOTELING_GUEST,
                    ENHANCED_CALL_LOGS,
                    SEQUENTIAL_RING,
                    TWO_STAGE_DIALING,
                    COLLABORATE_SHARING,
                    VOICE_MESSAGING_USER,
                    AUTHENTICATION,
                    CUSTOM_RINGBACK_USER_VIDEO,
                    INTERCEPT_USER,
                    COMMUNICATION_BARRING_USER_CONTROL,
                    SHARED_CALL_APPEARANCE_35,
                    EXTERNAL_CUSTOM_RINGBACK,
                    VOICEMAIL_TRANSCRIPTION,
                    TEAM_SMS
            })

            public @interface Feature {
            }
        }
    }

    // -------------------------------------------------------------------------------------------
    public static class AvatarDisplays {
        public static final String COMPANY = "C";

        @Retention(SOURCE)
        @StringDef( {
                COMPANY
        })

        public @interface Type {
        }
    }

    // -------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region MediaCall Enums
    // --------------------------------------------------------------------------------------------
    public static class MediaCall {
        public static class AttendeeTypes {
            public static final String DIAL_IN = "DIAL_IN";
            public static final String GUEST = "GUEST";
            public static final String HOST = "HOST";
            public static final String MODERATOR = "MODERATOR";
            public static final String RECORDING_BOT = "RECORDING_BOT";
            public static final String REGULAR = "REGULAR";

            @Retention(SOURCE)
            @StringDef( {
                    DIAL_IN,
                    GUEST,
                    HOST,
                    MODERATOR,
                    RECORDING_BOT,
                    REGULAR
            })
            public @interface AttendeeType {
            }
        }

        //TODO:Check on the difference in Category and meetingType. Make sure these are properly set
        public static class CallCategories {
            public static final String INSTANT = "INSTANT";
            public static final String SCHEDULED = "SCHEDULED";

            @Retention(SOURCE)
            @StringDef( {
                    INSTANT,
                    SCHEDULED
            })
            public @interface CallCatagory {
            }
        }

        public static class CallStatuses {
            public static final String ACTIVE = "ACTIVE";
            public static final String COMPLETED = "COMPLETED";
            public static final String INACTIVE = "INACTIVE";
            public static final String NOT_STARTED = "NOT_STARTED";

            @Retention(SOURCE)
            @StringDef( {
                    ACTIVE,
                    COMPLETED,
                    INACTIVE,
                    NOT_STARTED
            })
            public @interface CallStatus {

            }
        }

        public static class CallTypes {
            public static final String REGULAR = "REGULAR";
            public static final String DUO = "DUO";
            public static final String ROOM = "ROOM";
            public static final String GROUP = "GROUP";
            public static final String SCHEDULED = "SCHEDULED";

            @Retention(SOURCE)
            @StringDef( {
                    REGULAR,
                    DUO,
                    ROOM,
                    GROUP,
                    SCHEDULED
            })
            public @interface CallType {
            }
        }

        public static class AccessTypes {
            public static final String HOST = "HOST";
            public static final String MODERATOR = "MODERATOR";
            public static final String REGULAR = "REGULAR";
            public static final String GUEST = "GUEST";

            @Retention(SOURCE)
            @StringDef( {
                    REGULAR,
                    MODERATOR,
                    HOST,
                    GUEST
            })
            public @interface AccessType {
            }
        }

        public static class JoinStatuses {
            public static final String JOINED = "JOINED";
            public static final String CREATED = "CREATED";
            public static final String INVITED = "INVITED";
            public static final String DECLINED = "DECLINED";
            public static final String REMOVED = "REMOVED";

            @Retention(SOURCE)
            @StringDef( {
                    JOINED,
                    CREATED,
                    INVITED,
                    DECLINED,
                    REMOVED
            })
            public @interface JoinStatus {

            }
        }

        public static class LockedStates {
            public static final String LOCKED = "LOCKED";
            public static final String UNLOCKED = "UNLOCKED";

            @Retention(SOURCE)
            @StringDef( {
                    LOCKED,
                    UNLOCKED
            })
            public @interface LockedState {

            }
        }
        public static class SessionStatus {

            public static class Names{
                public static final String MEETING_START_REQUESTED = "meetingStartRequested";
                public static final String MEETING_START_SUCCEEDED = "meetingStartSucceeded";
                public static final String MEETING_START_FAILED = "meetingStartFailed";
                public static final String MEETING_END = "meetingEnded";
                public static final String MEETING_FAILED = "meetingFailed";


                @Retention(SOURCE)
                @StringDef( {
                        MEETING_START_REQUESTED,
                        MEETING_START_SUCCEEDED,
                        MEETING_START_FAILED,
                        MEETING_END,
                        MEETING_FAILED
                })
                public @interface Name {

                }
            }

            public static class Attributes{
                public static final String MEETING_STATUS = "meetingStatus";
                public static final String MEETING_ERROR_MESSAGE = "meetingErrorMessage";
                public static final String MEETING_TIMESTAMP_MS = "timestampMs";
                public static final String MAX_VIDEO_TILE_COUNT = "maxVideoTileCount";
                public static final String RETRY_COUNT = "retryCount";
                public static final String POOR_CONNECTION_COUNT = "poorConnectionCount";
                public static final String MEETING_DURATION_MS = "meetingDurationMs";
                public static final String MEETING_START_DURATION_MS = "meetingStartDurationMs";

                @Retention(SOURCE)
                @StringDef( {
                        MEETING_STATUS,
                        MEETING_ERROR_MESSAGE,
                        MEETING_TIMESTAMP_MS,
                        MAX_VIDEO_TILE_COUNT,
                        RETRY_COUNT,
                        POOR_CONNECTION_COUNT,
                        MEETING_DURATION_MS,
                        MEETING_START_DURATION_MS
                })
                public @interface Attribute {

                }
            }
        }

    }
    // --------------------------------------------------------------------------------------------
    //endregion End MediaCall Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region CalendarOrchestration Enums
    // --------------------------------------------------------------------------------------------

    public static class CalendarOrchestration {
        public static final String EVENT_ID = "EVENT_ID";
        public static final String CALENDAR_ID = "calendarId";
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
        public static final String NEXTIVA_URL_FILTER = "nextiva-connect";
        public static final String PAGE_SIZE = "1000";

        @Retention(SOURCE)
        @StringDef( {
                EVENT_ID,
                CALENDAR_ID,
                START_DATE,
                END_DATE,
                DATE_FORMAT,
                PAGE_SIZE
        })
        public @interface Type {
        }
    }

    // -------------------------------------------------------------------------------------------
    // endregion CalendarOrchestration Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region MeetingRecurrenceType Enums
    // --------------------------------------------------------------------------------------------

    public static class MeetingRecurrenceType {
        public static final String DAILY = "DAILY";
        public static final String WEEKLY = "WEEKLY";
        public static final String MONTHLY = "MONTHLY";
        public static final String YEARLY = "YEARLY";

        @Retention(SOURCE)
        @StringDef( {
                DAILY,
                WEEKLY,
                MONTHLY,
                YEARLY
        })
        public @interface Type {
        }
    }

    // -------------------------------------------------------------------------------------------
    // endregion MeetingRecurrenceType Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region MeetingRecurrenceEndByType Enums
    // --------------------------------------------------------------------------------------------

    public static class MeetingRecurrenceEndByType{
        public static final String UNTIL = "UNTIL";
        public static final String COUNT = "COUNT";

        @Retention(SOURCE)
        @StringDef({
                UNTIL,
                COUNT
        })
        public @interface Type {}
    }

    // -------------------------------------------------------------------------------------------
    // endregion MeetingRecurrenceEndByType Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region MeetingDayType Enums
    // --------------------------------------------------------------------------------------------

    public static class MeetingDayType{
        public static final String SUNDAY = "SUNDAY";
        public static final String MONDAY = "MONDAY";
        public static final String TUESDAY = "TUESDAY";
        public static final String WEDNESDAY = "WEDNESDAY";
        public static final String THURSDAY = "THURSDAY";
        public static final String FRIDAY = "FRIDAY";
        public static final String SATURDAY = "SATURDAY";


        @Retention(SOURCE)
        @StringDef({
                SUNDAY,
                MONDAY,
                TUESDAY,
                WEDNESDAY,
                THURSDAY,
                FRIDAY,
                SATURDAY
        })
        public @interface Type {}
    }

    // -------------------------------------------------------------------------------------------
    // endregion MeetingDayType Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region AttendeeResponseType Enums
    // --------------------------------------------------------------------------------------------

    public static class AttendeeResponseType{
        public static final String ACCEPTED = "ACCEPTED";
        public static final String DECLINED = "DECLINED";
        public static final String NO_RESPONSE = "NO_RESPONSE";
        public static final String PROPOSED_NEW_TIMINGS = "PROPOSED_NEW_TIMINGS";
        public static final String TENTATIVE = "TENTATIVE";


        @Retention(SOURCE)
        @StringDef({
                ACCEPTED,
                DECLINED,
                NO_RESPONSE,
                PROPOSED_NEW_TIMINGS,
                TENTATIVE
        })
        public @interface Type {}
    }

    // -------------------------------------------------------------------------------------------
    // endregion AttendeeResponseType Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region AssignCoHost Enums
    // --------------------------------------------------------------------------------------------

    public static class AssignCoHost{
        public static final String NO_COHOST = "NO_COHOST";


        @Retention(SOURCE)
        @StringDef({
                NO_COHOST
        })
        public @interface Type {}
    }

    // -------------------------------------------------------------------------------------------
    // endregion AssignCoHost Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region Attachment Enums
    // --------------------------------------------------------------------------------------------

    public static class Attachment {
        // Attachment file types and extensions can be found here https://nextiva.atlassian.net/wiki/spaces/PRDDEV/pages/287802083/Product+Brief+SMS+Channel#Appendix

        public static class AttachmentContentType {
            public static final String AUDIO_3GPP = "audio/3gpp";
            public static final String AUDIO_MP3 = "audio/mp3";
            public static final String AUDIO_AMR = "audio/amr";
            public static final String AUDIO_MP4 = "audio/mp4";
            public static final String AUDIO_M4P = "audio/m4p";
            public static final String AUDIO_MPEG = "audio/mpeg";
            public static final String AUDIO_WAV = "audio/wav";
            public static final String AUDIO_WAVE = "audio/wave";
            public static final String AUDIO_XWAV = "audio/x-wav";
            public static final String AUDIO_M4A = "audio/m4a";
            public static final String AUDIO_XM4R = "audio/x-m4r";
            public static final String AUDIO_M4R = "audio/m4r";
            public static final String AUDIO_M4B = "audio/m4b";
            public static final String IMAGE_BMP = "image/bmp";
            public static final String IMAGE_GIF = "image/gif";
            public static final String IMAGE_JPEG = "image/jpeg";
            public static final String IMAGE_PNG = "image/png";
            public static final String IMAGE_HEIC = "image/heic";
            public static final String IMAGE_HEIF = "image/heif";
            public static final String VIDEO_3GPP = "video/3gpp";
            public static final String VIDEO_H263 = "video/h263";
            public static final String VIDEO_H264 = "video/h264";
            public static final String VIDEO_MP4 = "video/mp4";

            @Retention(SOURCE)
            @StringDef( {
                    AUDIO_3GPP,
                    AUDIO_AMR,
                    AUDIO_MP4,
                    AUDIO_MP3,
                    AUDIO_MPEG,
                    AUDIO_WAV,
                    AUDIO_WAVE,
                    AUDIO_XWAV,
                    AUDIO_M4A,
                    AUDIO_XM4R,
                    AUDIO_M4R,
                    IMAGE_BMP,
                    IMAGE_GIF,
                    IMAGE_JPEG,
                    IMAGE_PNG,
                    IMAGE_HEIC,
                    IMAGE_HEIF,
                    VIDEO_3GPP,
                    VIDEO_H263,
                    VIDEO_H264,
                    VIDEO_MP4
            })
            public @interface ContentType {
            }
        }

        public static class ContentMajorType {
            public static final String AUDIO = "audio";
            public static final String IMAGE = "image";
            public static final String VIDEO = "video";

            @Retention(SOURCE)
            @StringDef( {
                    AUDIO,
                    IMAGE,
                    VIDEO
            })
            public @interface MajorType {
            }
        }

        public static class ContentExtensionType {
            public static final String EXT_3GP = "3gp";
            public static final String EXT_AMR = "amr";
            public static final String EXT_3GA = "3ga";
            public static final String EXT_M4A = "m4a";
            public static final String EXT_XM4A = "x-m4a";
            public static final String EXT_M4B = "m4b";
            public static final String EXT_M4P = "m4p";
            public static final String EXT_M4R = "m4r";
            public static final String EXT_MP3 = "mp3";
            public static final String EXT_WAV = "wav";
            public static final String EXT_VND_WAVE = "vnd.wave";
            public static final String EXT_WAVE = "wave";
            public static final String EXT_XWAVE = "x-wav";
            public static final String EXT_MPEG = "mpeg";
            public static final String EXT_MPG = "mpg";
            public static final String EXT_BMP = "bmp";
            public static final String EXT_X_MS_BMP = "x-ms-bmp";
            public static final String EXT_DIB = "dib";
            public static final String EXT_GIF = "gif";
            public static final String EXT_JPEG = "jpeg";
            public static final String EXT_JPG = "jpg";
            public static final String EXT_PNG = "png";
            public static final String EXT_3GPP = "3gpp";
            public static final String EXT_H263 = "h263";
            public static final String EXT_H264 = "h264";
            public static final String EXT_MP4 = "mp4";
            public static final String EXT_M4V = "m4v";
            public static final String EXT_HEIC = "heic";
            public static final String EXT_HEIF = "heif";
            public static final String EXT_NONE = "";

            @Retention(SOURCE)
            @StringDef( {
                    EXT_3GP,
                    EXT_AMR,
                    EXT_3GA,
                    EXT_M4A,
                    EXT_M4B,
                    EXT_M4P,
                    EXT_M4R,
                    EXT_MP3,
                    EXT_WAV,
                    EXT_WAVE,
                    EXT_MPEG,
                    EXT_MPG,
                    EXT_BMP,
                    EXT_DIB,
                    EXT_GIF,
                    EXT_JPEG,
                    EXT_JPG,
                    EXT_PNG,
                    EXT_3GPP,
                    EXT_H263,
                    EXT_H264,
                    EXT_MP4,
                    EXT_XWAVE,
                    EXT_M4V,
                    EXT_HEIC,
                    EXT_HEIF,
                    EXT_NONE,
                    EXT_XM4A
            })
            public @interface ExtensionType {
            }
        }

        public static class EntityType {
            public static final String CHAT_UPLOAD = "CHAT_UPLOAD";
            public static final String EMAIL_MESSAGE = "EMAIL_MESSAGE";
            public static final String LINK_MESSAGE = "LINK_MESSAGE";
            public static final String MMS_MESSAGE = "MMS_MESSAGE";
            public static final String RECORDING_FILE = "RECORDING_FILE";
            public static final String SMS_MESSAGE = "SMS_MESSAGE";

            @Retention(SOURCE)
            @StringDef( {
                    CHAT_UPLOAD,
                    EMAIL_MESSAGE,
                    LINK_MESSAGE,
                    MMS_MESSAGE,
                    RECORDING_FILE,
                    SMS_MESSAGE
            })
            public @interface Type {}
        }
    }

    // -------------------------------------------------------------------------------------------
    // endregion Attachment Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region AttendeeActionType Enums
    // --------------------------------------------------------------------------------------------

    public static class AttendeeActionType{
        public static final String ACCEPTED = "ACCEPTED";
        public static final String ADMITTED = "ADMITTED";
        public static final String DECLINED = "DECLINED";
        public static final String DENIED = "DENIED";
        public static final String DROPPED = "DROPPED";
        public static final String MISSED = "MISSED";


        @Retention(SOURCE)
        @StringDef({
                ACCEPTED,
                ADMITTED,
                DECLINED,
                DENIED,
                DROPPED,
                MISSED
        })
        public @interface Type {}
    }

    // -------------------------------------------------------------------------------------------
    // endregion Attachment Enums
    // --------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // region DndDurationType Enums
    // --------------------------------------------------------------------------------------------

    public static class DndDurationType{
        public static final int DND_DURATION_UNTILL_CHANGE = 0;
        public static final int DND_DURATION_30_MINUTE = 1;
        public static final int DND_DURATION_1_HOUR = 2;
        public static final int DND_DURATION_6_HOUR = 3;
        public static final int DND_DURATION_DAY = 4;
        public static final int DND_DURATION_CUSTOM = 5;


        @Retention(SOURCE)
        @IntDef({
                DND_DURATION_UNTILL_CHANGE,
                DND_DURATION_30_MINUTE,
                DND_DURATION_1_HOUR,
                DND_DURATION_6_HOUR,
                DND_DURATION_DAY,
                DND_DURATION_CUSTOM
        })
        public @interface Type {}
    }

    // -------------------------------------------------------------------------------------------
    // endregion DndDurationType Enums
    // --------------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------------
    // region ScheduleValidationErrorType Enums
    // --------------------------------------------------------------------------------------------

    public static class ScheduleValidationErrorType {
        public static final String EMPTY_SCHEDULE_NAME = "EMPTY_SCHEDULE_NAME";
        public static final String MAX_SCHEDULE_NAME_CHAR_LIMIT_REACHED = "MAX_SCHEDULE_NAME_CHAR_LIMIT_REACHED";
        public static final String SCHEDULE_NAME_IN_USE = "SCHEDULE_NAME_IN_USE";
        public static final String END_TIME_BEFORE_START_TIME = "END_TYPE_BEFORE_START_TIME";
        public static final String BREAK_TIMES_START_TIME_NOT_IN_RANGE = "BREAK_TIMES_START_TIME_NOT_IN_RANGE";
        public static final String BREAK_TIMES_END_TIME_NOT_IN_RANGE = "BREAK_TIMES_END_TIME_NOT_IN_RANGE";
        public static final String BREAK_TIMES_START_END_TIME_NOT_IN_RANGE = "BREAK_TIMES_START_END_TIME_NOT_IN_RANGE";
        public static final String BREAK_TIMES_OVERLAPPING = "BREAK_TIMES_OVERLAPPING";
        public static final String SCHEDULE_VALIDATION_ERROR_NONE = "SCHEDULE_VALIDATION_ERROR_NONE";


        @Retention(SOURCE)
        @StringDef({
                EMPTY_SCHEDULE_NAME,
                MAX_SCHEDULE_NAME_CHAR_LIMIT_REACHED,
                SCHEDULE_NAME_IN_USE,
                SCHEDULE_VALIDATION_ERROR_NONE,
                END_TIME_BEFORE_START_TIME,
                BREAK_TIMES_START_TIME_NOT_IN_RANGE,
                BREAK_TIMES_END_TIME_NOT_IN_RANGE,
                BREAK_TIMES_OVERLAPPING
        })
        public @interface Type {
        }
    }


    // -------------------------------------------------------------------------------------------
    // endregion ScheduleValidationErrorType Enums
    // ----------------------------------------------------------


    // -------------------------------------------------------------------------------------------
    // region Messages Enums
    // --------------------------------------------------------------------------------------------
    public static class Messages {
        public static class Channels {
            public static final String CHAT = "CHAT";
            public static final String EMAIL = "EMAIL";
            public static final String MEETING = "MEETING";
            public static final String SMS = "SMS";
            public static final String SURVEY = "SURVEY";
            public static final String VOICE = "VOICE";
            public static final String VOICEMAIL = "VOICEMAIL";
            public static final String ROOMS = "ROOMS";

            @Retention(SOURCE)
            @StringDef({
                    CHAT,
                    EMAIL,
                    MEETING,
                    SMS,
                    SURVEY,
                    VOICE,
                    VOICEMAIL,
                    ROOMS
            })
            public @interface Channel {
            }
        }

        public static class ReadStatus {
            public static final String READ = "READ";
            public static final String UNREAD = "UNREAD";

            @Retention(SOURCE)
            @StringDef( {
                    READ,
                    UNREAD
            })

            public @interface Status {
            }
        }
    }

    // -------------------------------------------------------------------------------------------
    // endregion Messages Enums
    // ----------------------------------------------------------




    // -------------------------------------------------------------------------------------------
    // region ResponseCodes Enums
    // --------------------------------------------------------------------------------------------
    public static class ResponseCodes {


        @Retention(SOURCE)
        @IntDef( {
                ProvisionalResponses.TRYING,
                ProvisionalResponses.RINGING,
                ProvisionalResponses.CALL_IS_BEING_FORWARDED,
                ProvisionalResponses.QUEUED,
                ProvisionalResponses.SESSION_PROGRESS,
                ProvisionalResponses.EARLY_DIALOG_TERMINATED,
                SuccessfulResponses.OK,
                SuccessfulResponses.ACCEPTED,
                SuccessfulResponses.NO_NOTIFICATION,
                RedirectionResponsess.MULTIPLE_CHOICES,
                RedirectionResponsess.MOVED_PERMANENTLY,
                RedirectionResponsess.MOVED_TEMPORARILY,
                RedirectionResponsess.USE_PROXY,
                RedirectionResponsess.ALTERNATIVE_SERVICE,
                ClientFailureResponses.BAD_REQUEST,
                ClientFailureResponses.UNAUTHORIZED,
                ClientFailureResponses.PAYMENT_REQUIRED,
                ClientFailureResponses.FORBIDDEN,
                ClientFailureResponses.NOT_FOUND,
                ClientFailureResponses.METHOD_NOT_ALLOWED,
                ClientFailureResponses.NOT_ACCEPTABLE,
                ClientFailureResponses.PROXY_AUTHENTICATION_REQUIRED,
                ClientFailureResponses.REQUEST_TIMEOUT,
                ClientFailureResponses.CONFLICT,
                ClientFailureResponses.GONE,
                ClientFailureResponses.LENGTH_REQUIRED,
                ClientFailureResponses.CONDITIONAL_REQUEST_FAILED,
                ClientFailureResponses.REQUEST_ENTITY_TOO_LARGE,
                ClientFailureResponses.REQUEST_URI_TOO_LONG,
                ClientFailureResponses.UNSUPPORTED_MEDIA_TYPE,
                ClientFailureResponses.UNSUPPORTED_URI_SCHEME,
                ClientFailureResponses.UNKNOWN_RESOURCE_PRIORITY,
                ClientFailureResponses.BAD_EXTENSION,
                ClientFailureResponses.EXTENSION_REQUIRED,
                ClientFailureResponses.SESSION_INTERVAL_TOO_SMALL,
                ClientFailureResponses.INTERVAL_TOO_BRIEF,
                ClientFailureResponses.BAD_LOCATION_INFORMATION,
                ClientFailureResponses.USE_IDENTITY_HEADER,
                ClientFailureResponses.PROVIDE_REFERRER_IDENTITY,
                ClientFailureResponses.FLOW_FAILED,
                ClientFailureResponses.ANONYMITY_DISALLOWED,
                ClientFailureResponses.BAD_IDENTITY_INFO,
                ClientFailureResponses.UNSUPPORTED_CERTIFICATE,
                ClientFailureResponses.INVALID_IDENTITY_HEADER,
                ClientFailureResponses.FIRST_HOP_LACKS_OUTBOUND_SUPPORT,
                ClientFailureResponses.MAX_BREADTH_EXCEEDED,
                ClientFailureResponses.BAD_INFO_PACKAGE,
                ClientFailureResponses.CONSENT_NEEDED,
                ClientFailureResponses.TEMPORARILY_UNAVAILABLE,
                ClientFailureResponses.CALL_TRANSACTION_DOES_NOT_EXIST,
                ClientFailureResponses.LOOP_DETECTED,
                ClientFailureResponses.TOO_MANY_HOPS,
                ClientFailureResponses.ADDRESS_INCOMPLETE,
                ClientFailureResponses.AMBIGUOUS,
                ClientFailureResponses.BUSY_HERE,
                ClientFailureResponses.REQUEST_TERMINATED,
                ClientFailureResponses.NOT_ACCEPTABLE_HERE,
                ClientFailureResponses.BAD_EVENT,
                ClientFailureResponses.REQUEST_PENDING,
                ClientFailureResponses.UNDECIPHERABLE,
                ClientFailureResponses.SECURITY_AGREEMENT_REQUIRED,
                ServerFailureResponses.SERVER_INTERNAL_ERROR,
                ServerFailureResponses.NOT_IMPLEMENTED,
                ServerFailureResponses.BAD_GATEWAY,
                ServerFailureResponses.SERVICE_UNAVAILABLE,
                ServerFailureResponses.SERVER_TIME_OUT,
                ServerFailureResponses.VERSION_NOT_SUPPORTED,
                ServerFailureResponses.MESSAGE_TOO_LARGE,
                ServerFailureResponses.PRECONDITION_FAILURE,
                GlobalFailureResponses.BUSY_EVERYWHERE,
                GlobalFailureResponses.DECLINE,
                GlobalFailureResponses.DOES_NOT_EXIST_ANYWHERE,
                GlobalFailureResponses.NOT_ACCEPTABLE,
                GlobalFailureResponses.UNWANTED
        })
        public @interface ResponseCode {
        }

        public static class ProvisionalResponses {
            /**
             * Extended search being performed may take a significant time so a forking proxy must send a 100 Trying response.
             */
            public static final int TRYING = 100;
            /**
             * Destination user agent received INVITE, and is alerting user of call.
             */
            public static final int RINGING = 180;
            /**
             * Servers can optionally send this response to indicate a call is being forwarded.
             */
            public static final int CALL_IS_BEING_FORWARDED = 181;
            /**
             * Indicates that the destination was temporarily unavailable, so the server has queued the call until the destination is available. A server may send multiple 182 responses to update progress of the queue.
             */
            public static final int QUEUED = 182;
            /**
             * This response may be used to send extra information for a call which is still being set up.
             */
            public static final int SESSION_PROGRESS = 183;
            /**
             * Can be used by User Agent Server to indicate to upstream SIP entities (including the User Agent Client (UAC)) that an early dialog has been terminated.
             */
            public static final int EARLY_DIALOG_TERMINATED = 199;

            @Retention(SOURCE)
            @IntDef( {
                    TRYING,
                    RINGING,
                    CALL_IS_BEING_FORWARDED,
                    QUEUED,
                    SESSION_PROGRESS,
                    EARLY_DIALOG_TERMINATED
            })
            public @interface ProvisionalResponse {
            }
        }

        public static class SuccessfulResponses {

            /**
             * Indicates the request was successful.
             */
            public static final int OK = 200;
            /**
             * Indicates that the request has been accepted for processing, but the processing has not been completed.
             */
            public static final int ACCEPTED = 202;
            /**
             * Indicates the request was successful, but the corresponding response will not be received.
             */
            public static final int NO_NOTIFICATION = 204;

            @Retention(SOURCE)
            @IntDef( {
                    OK,
                    ACCEPTED,
                    NO_NOTIFICATION
            })
            public @interface SuccessfulResponse {
            }
        }

        public static class RedirectionResponsess {
            /**
             * The address resolved to one of several options for the user or client to choose between, which are listed in the message body or the message's Contact fields.
             */
            public static final int MULTIPLE_CHOICES = 300;
            /**
             * The original Request-URI is no longer valid, the new address is given in the Contact header field, and the client should update any records of the original Request-URI with the new value.
             */
            public static final int MOVED_PERMANENTLY = 301;
            /**
             * The client should try at the address in the Contact field. If an Expires field is present, the client may cache the result for that period of time.
             */
            public static final int MOVED_TEMPORARILY = 302;
            /**
             * The Contact field details a proxy that must be used to access the requested destination.
             */
            public static final int USE_PROXY = 305;
            /**
             * The call failed, but alternatives are detailed in the message body.
             */
            public static final int ALTERNATIVE_SERVICE = 380;

            @Retention(SOURCE)
            @IntDef( {
                    MULTIPLE_CHOICES,
                    MOVED_PERMANENTLY,
                    MOVED_TEMPORARILY,
                    USE_PROXY,
                    ALTERNATIVE_SERVICE
            })
            public @interface RedirectionResponse {
            }
        }

        public static class ClientFailureResponses {
            /**
             * The request could not be understood due to malformed syntax.
             */
            public static final int BAD_REQUEST = 400;
            /**
             * The request requires user authentication. This response is issued by UASs and registrars.
             */
            public static final int UNAUTHORIZED = 401;
            /**
             * Reserved for future use.
             */
            public static final int PAYMENT_REQUIRED = 402;
            /**
             * The server understood the request, but is refusing to fulfill it. Sometimes (but not always) this means the call has been rejected by the receiver.
             */
            public static final int FORBIDDEN = 403;
            /**
             * The server has definitive information that the user does not exist at the domain specified in the Request-URI. This status is also returned if the domain in the Request-URI does not match any of the domains handled by the recipient of the request.
             */
            public static final int NOT_FOUND = 404;
            /**
             * The method specified in the Request-Line is understood, but not allowed for the address identified by the Request-URI.[
             */
            public static final int METHOD_NOT_ALLOWED = 405;
            /**
             * The method specified in the Request-Line is understood, but not allowed for the address identified by the Request-URI.[
             */
            public static final int NOT_ACCEPTABLE = 406;
            /**
             * The method specified in the Request-Line is understood, but not allowed for the address identified by the Request-URI.[
             */
            public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
            /**
             * Couldn't find the user in time. The server could not produce a response within a suitable amount of time, for example, if it could not determine the location of the user in time. The client MAY repeat the request without modifications at any later time.
             */
            public static final int REQUEST_TIMEOUT = 408;
            /**
             * User already registered. Deprecated by omission from later RFCs and by non-registration with the IANA.
             */
            public static final int CONFLICT = 409;
            /**
             * The user existed once, but is not available here any more.
             */
            public static final int GONE = 410;
            /**
             * The server will not accept the request without a valid Content-Length. Deprecated by omission from later RFCs and by non-registration with the IANA.
             */
            public static final int LENGTH_REQUIRED = 411;
            /**
             * The given precondition has not been met.
             */
            public static final int CONDITIONAL_REQUEST_FAILED = 412;
            /**
             * The given precondition has not been met.
             */
            public static final int REQUEST_ENTITY_TOO_LARGE = 413;
            /**
             * The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
             */
            public static final int REQUEST_URI_TOO_LONG = 414;
            /**
             * The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
             */
            public static final int UNSUPPORTED_MEDIA_TYPE = 415;
            /**
             * The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
             */
            public static final int UNSUPPORTED_URI_SCHEME = 416;
            /**
             * There was a resource-priority option tag, but no Resource-Priority header.
             */
            public static final int UNKNOWN_RESOURCE_PRIORITY = 417;
            /**
             * Bad SIP Protocol Extension used, not understood by the server.
             */
            public static final int BAD_EXTENSION = 420;
            /**
             * The server needs a specific extension not listed in the Supported header.
             */
            public static final int EXTENSION_REQUIRED = 421;
            /**
             * The received request contains a Session-Expires header field with a duration below the minimum timer.
             */
            public static final int SESSION_INTERVAL_TOO_SMALL = 422;
            /**
             * Expiration time of the resource is too short
             */
            public static final int INTERVAL_TOO_BRIEF = 423;
            /**
             * The request's location content was malformed or otherwise unsatisfactory.
             */
            public static final int BAD_LOCATION_INFORMATION = 424;
            /**
             * The server policy requires an Identity header, and one has not been provided.
             */
            public static final int USE_IDENTITY_HEADER = 428;
            /**
             * The server did not receive a valid Referred-By token on the request.
             */
            public static final int PROVIDE_REFERRER_IDENTITY = 429;
            /**
             * A specific flow to a user agent has failed, although other flows may succeed. This response is intended for use between proxy devices, and should not be seen by an endpoint (and if it is seen by one, should be treated as a 400 Bad Request response).
             */
            public static final int FLOW_FAILED = 430;
            /**
             * The request has been rejected because it was anonymous.
             */
            public static final int ANONYMITY_DISALLOWED = 433;
            /**
             * The request has an Identity-Info header, and the URI scheme in that header cannot be dereferenced.
             */
            public static final int BAD_IDENTITY_INFO = 436;
            /**
             * The server was unable to validate a certificate for the domain that signed the request.
             */
            public static final int UNSUPPORTED_CERTIFICATE = 437;
            /**
             * The server was unable to validate a certificate for the domain that signed the request.
             */
            public static final int INVALID_IDENTITY_HEADER = 438;
            /**
             * The first outbound proxy the user is attempting to register through does not support the "outbound" feature of RFC 5626, although the registrar does.
             */
            public static final int FIRST_HOP_LACKS_OUTBOUND_SUPPORT = 439;
            /**
             * If a SIP proxy determines a response context has insufficient Incoming Max-Breadth to carry out a desired parallel fork, and the proxy is unwilling/unable to compensate by forking serially or sending a redirect, that proxy MUST return a 440 response. A client receiving a 440 response can infer that its request did not reach all possible destinations.
             */
            public static final int MAX_BREADTH_EXCEEDED = 440;
            /**
             * If a SIP UA receives an INFO request associated with an Info Package that the UA has not indicated willingness to receive, the UA MUST send a 469 response, which contains a Recv-Info header field with Info Packages for which the UA is willing to receive INFO requests.
             */
            public static final int BAD_INFO_PACKAGE = 469;
            /**
             * The source of the request did not have the permission of the recipient to make such a request.
             */
            public static final int CONSENT_NEEDED = 470;
            /**
             * Callee currently unavailable.
             */
            public static final int TEMPORARILY_UNAVAILABLE = 480;
            /**
             * Server received a request that does not match any dialog or transaction.
             */
            public static final int CALL_TRANSACTION_DOES_NOT_EXIST = 481;
            /**
             * Server has detected a loop.
             */
            public static final int LOOP_DETECTED = 482;
            /**
             * Max-Forwards header has reached the value '0'.
             */
            public static final int TOO_MANY_HOPS = 483;
            /**
             * Request-URI incomplete.
             */
            public static final int ADDRESS_INCOMPLETE = 484;
            /**
             * Request-URI is ambiguous.
             */
            public static final int AMBIGUOUS = 485;
            /**
             * Callee is busy.
             */
            public static final int BUSY_HERE = 486;
            /**
             * Request has terminated by bye or cancel.
             */
            public static final int REQUEST_TERMINATED = 487;
            /**
             * Some aspect of the session description or the Request-URI is not acceptable, or Codec issue.
             */
            public static final int NOT_ACCEPTABLE_HERE = 488;
            /**
             * The server did not understand an event package specified in an Event header field.
             */
            public static final int BAD_EVENT = 489;
            /**
             * Server has some pending request from the same dialog.
             */
            public static final int REQUEST_PENDING = 491;
            /**
             * Request contains an encrypted MIME body, which recipient can not decrypt.
             */
            public static final int UNDECIPHERABLE = 493;
            /**
             * The server has received a request that requires a negotiated security mechanism, and the response contains a list of suitable security mechanisms for the requester to choose between,[20]:Â§Â§2.3.1â€“2.3.2 or a digest authentication challenge.
             */
            public static final int SECURITY_AGREEMENT_REQUIRED = 494;

            @Retention(SOURCE)
            @IntDef(value = {
                    BAD_REQUEST,
                    UNAUTHORIZED,
                    PAYMENT_REQUIRED,
                    FORBIDDEN,
                    NOT_FOUND,
                    METHOD_NOT_ALLOWED,
                    NOT_ACCEPTABLE,
                    PROXY_AUTHENTICATION_REQUIRED,
                    REQUEST_TIMEOUT,
                    CONFLICT,
                    GONE,
                    LENGTH_REQUIRED,
                    CONDITIONAL_REQUEST_FAILED,
                    REQUEST_ENTITY_TOO_LARGE,
                    REQUEST_URI_TOO_LONG,
                    UNSUPPORTED_MEDIA_TYPE,
                    UNSUPPORTED_URI_SCHEME,
                    UNKNOWN_RESOURCE_PRIORITY,
                    BAD_EXTENSION,
                    EXTENSION_REQUIRED,
                    SESSION_INTERVAL_TOO_SMALL,
                    INTERVAL_TOO_BRIEF,
                    BAD_LOCATION_INFORMATION,
                    USE_IDENTITY_HEADER,
                    PROVIDE_REFERRER_IDENTITY,
                    FLOW_FAILED,
                    ANONYMITY_DISALLOWED,
                    BAD_IDENTITY_INFO,
                    UNSUPPORTED_CERTIFICATE,
                    INVALID_IDENTITY_HEADER,
                    FIRST_HOP_LACKS_OUTBOUND_SUPPORT,
                    MAX_BREADTH_EXCEEDED,
                    BAD_INFO_PACKAGE,
                    CONSENT_NEEDED,
                    TEMPORARILY_UNAVAILABLE,
                    CALL_TRANSACTION_DOES_NOT_EXIST,
                    LOOP_DETECTED,
                    TOO_MANY_HOPS,
                    ADDRESS_INCOMPLETE,
                    AMBIGUOUS,
                    BUSY_HERE,
                    REQUEST_TERMINATED,
                    NOT_ACCEPTABLE_HERE,
                    BAD_EVENT,
                    REQUEST_PENDING,
                    UNDECIPHERABLE,
                    SECURITY_AGREEMENT_REQUIRED

            })
            public @interface ClientFailureResponse {
            }
        }

        public static class ServerFailureResponses {

            /**
             * The server could not fulfill the request due to some unexpected condition.
             */
            public static final int SERVER_INTERNAL_ERROR = 500;

            /**
             * The server does not have the ability to fulfill the request, such as because it does not recognize the request method. (Compare with 405 Method Not Allowed, where the server recognizes the method but does not allow or support it.)
             */
            public static final int NOT_IMPLEMENTED = 501;

            /**
             * The server is acting as a gateway or proxy, and received an invalid response from a downstream server while attempting to fulfill the request.
             */
            public static final int BAD_GATEWAY = 502;

            /**
             * The server is undergoing maintenance or is temporarily overloaded and so cannot process the request. A "Retry-After" header field may specify when the client may reattempt its request.
             */
            public static final int SERVICE_UNAVAILABLE = 503;

            /**
             * The server attempted to access another server in attempting to process the request, and did not receive a prompt response.
             */
            public static final int SERVER_TIME_OUT = 504;

            /**
             * The SIP protocol version in the request is not supported by the server.
             */
            public static final int VERSION_NOT_SUPPORTED = 505;

            /**
             * The request message length is longer than the server can process.
             */
            public static final int MESSAGE_TOO_LARGE = 513;

            /**
             * The server is unable or unwilling to meet some constraints specified in the offer.
             */
            public static final int PRECONDITION_FAILURE = 580;

            @Retention(SOURCE)
            @IntDef( {
                    SERVER_INTERNAL_ERROR,
                    NOT_IMPLEMENTED,
                    BAD_GATEWAY,
                    SERVICE_UNAVAILABLE,
                    SERVER_TIME_OUT,
                    VERSION_NOT_SUPPORTED,
                    MESSAGE_TOO_LARGE,
                    PRECONDITION_FAILURE
            })
            public @interface ServerFailureResponse {
            }
        }

        public static class GlobalFailureResponses {

            /**
             * All possible destinations are busy. Unlike the 486 response, this response indicates the destination knows there are no alternative destinations (such as a voicemail server) able to accept the call
             */
            public static final int BUSY_EVERYWHERE = 600;

            /**
             * The destination does not wish to participate in the call, or cannot do so, and additionally the destination knows there are no alternative destinations (such as a voicemail server) willing to accept the call.
             */
            public static final int DECLINE = 603;

            /**
             * The server has authoritative information that the requested user does not exist anywhere.
             */
            public static final int DOES_NOT_EXIST_ANYWHERE = 604;

            /**
             * The user's agent was contacted successfully but some aspects of the session description such as the requested media, bandwidth, or addressing style were not acceptable.
             */
            public static final int NOT_ACCEPTABLE = 606;

            /**
             * The called party did not want this call from the calling party. Future attempts from the calling party are likely to be similarly rejected.
             */
            public static final int UNWANTED = 607;

            @Retention(SOURCE)
            @IntDef( {
                    BUSY_EVERYWHERE,
                    DECLINE,
                    DOES_NOT_EXIST_ANYWHERE,
                    NOT_ACCEPTABLE,
                    UNWANTED
            })
            public @interface GlobalFailureResponse {
            }
        }
    }

    // -------------------------------------------------------------------------------------------
    // endregion ResponseCodes Enums
    // ----------------------------------------------------------
}
