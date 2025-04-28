/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The {@code CallInfo} class represents an instance of a call, either about to be made
 * or in an active state.  This object contains all the info needed in order to start a
 * call, as well as see the info which is associated to the call.
 * <p>
 * The {@code DisplayName} field should be used as the source of truth and should trump
 * the Display Name as returned by the possible {@code NextivaContact} object.
 */
public class CallInfo implements Serializable {

    @Nullable
    private NextivaContact mNextivaContact;
    @Nullable
    private String mDisplayName;
    @Nullable
    private String mCountryCode;
    @Nullable
    private String mNumberToCall;
    @Nullable
    private ChatConversation mChatConversation;
    @Enums.Sip.CallTypes.Type
    private int mCallType;
    @Enums.Service.DialingServiceTypes.DialingServiceType
    private int mDialingServiceType;
    @Nullable
    @Enums.Service.DialingServiceTypes.DialingServiceType
    private int[] mDisallowDialingServiceTypes;
    private long mSessionID;
    private String mTrackingId;
    private boolean mHasLeftNWay;

    private CallInfo(@Nullable NextivaContact nextivaContact,
                     @Nullable String displayName,
                     @Nullable String countryCode,
                     @Nullable String numberToCall,
                     @Nullable ChatConversation chatConversation,
                     @Enums.Sip.CallTypes.Type int callType,
                     @Enums.Service.DialingServiceTypes.DialingServiceType int dialingServiceType,
                     @Nullable @Enums.Service.DialingServiceTypes.DialingServiceType int[] disallowDialingServiceTypes,
                     long sessionID,
                     String trackingId,
                     boolean hasLeftNWay) {

        mNextivaContact = nextivaContact;
        mDisplayName = displayName;
        mCountryCode = countryCode;
        mNumberToCall = numberToCall;
        mChatConversation = chatConversation;
        mCallType = callType;
        mDialingServiceType = dialingServiceType;
        mDisallowDialingServiceTypes = disallowDialingServiceTypes;
        mSessionID = sessionID;
        mTrackingId = trackingId;
        mHasLeftNWay = hasLeftNWay;
    }

    @Nullable
    public NextivaContact getNextivaContact() {
        return mNextivaContact;
    }

    public void setNextivaContact(@Nullable NextivaContact nextivaContact) {
        mNextivaContact = nextivaContact;
    }

    @Nullable
    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        mDisplayName = displayName;
    }

    @Nullable
    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(@Nullable String countryCode) {
        mCountryCode = countryCode;
    }

    @Nullable
    public String getNumberToCall() {
        return mNumberToCall;
    }

    public void setNumberToCall(@Nullable String numberToCall) {
        mNumberToCall = numberToCall;
    }

    @Nullable
    public ChatConversation getChatConversation() {
        return mChatConversation;
    }

    public void setChatConversation(@Nullable ChatConversation chatConversation) {
        mChatConversation = chatConversation;
    }

    @Enums.Sip.CallTypes.Type
    public int getCallType() {
        return mCallType;
    }

    public void setCallType(@Enums.Sip.CallTypes.Type int callType) {
        mCallType = callType;
    }

    @Enums.Service.DialingServiceTypes.DialingServiceType
    public int getDialingServiceType() {
        return mDialingServiceType;
    }

    public void setDialingServiceType(@Enums.Service.DialingServiceTypes.DialingServiceType int dialingServiceType) {
        mDialingServiceType = dialingServiceType;
    }

    @Nullable
    @Enums.Service.DialingServiceTypes.DialingServiceType
    public int[] getDisallowDialingServiceTypes() {
        return mDisallowDialingServiceTypes;
    }

    public void setDisallowDialingServiceTypes(@Nullable @Enums.Service.DialingServiceTypes.DialingServiceType int[] disallowDialingServiceTypes) {
        mDisallowDialingServiceTypes = disallowDialingServiceTypes;
    }

    public long getSessionID() {
        return mSessionID;
    }

    public void setSessionID(final long sessionID) {
        mSessionID = sessionID;
    }

    public String getTrackingId() {
        return mTrackingId;
    }

    public void setTrackingId(String trackingId) {
        mTrackingId = trackingId;
    }

    public boolean hasLeftNWay() {
        return mHasLeftNWay;
    }

    public void setHasLeftNWay(boolean hasLeftNWay) {
        mHasLeftNWay = hasLeftNWay;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (mNextivaContact != null) {
            stringBuilder.append("Nextiva Contact: ").append(mNextivaContact.getUiName()).append("; ");
        }

        if (mDisplayName != null) {
            stringBuilder.append("Display Name: ").append(mDisplayName).append("; ");
        }

        if (mCountryCode != null) {
            stringBuilder.append("Country Code: ").append(mCountryCode).append("; ");
        }

        if (mNumberToCall != null) {
            stringBuilder.append("Number to Call: ").append(mNumberToCall).append("; ");
        }

        if (mChatConversation != null) {
            stringBuilder.append("Chat Conversation Type: ").append(mChatConversation.getConversationType()).append("; ");
        }

        if (mCallType == Enums.Sip.CallTypes.NONE) {
            stringBuilder.append("Call Type: None; ");
        } else if (mCallType == Enums.Sip.CallTypes.VOICE) {
            stringBuilder.append("Call Type: Voice; ");
        } else if (mCallType == Enums.Sip.CallTypes.VIDEO) {
            stringBuilder.append("Call Type: Video; ");
        }

        if (mDialingServiceType == Enums.Service.DialingServiceTypes.NONE) {
            stringBuilder.append("Dialing Service: None; ");
        } else if (mDialingServiceType == Enums.Service.DialingServiceTypes.VOIP) {
            stringBuilder.append("Dialing Service: VoIP; ");
        } else if (mDialingServiceType == Enums.Service.DialingServiceTypes.CALL_BACK) {
            stringBuilder.append("Dialing Service: Call Back; ");
        } else if (mDialingServiceType == Enums.Service.DialingServiceTypes.CALL_THROUGH) {
            stringBuilder.append("Dialing Service: Call Through; ");
        } else if (mDialingServiceType == Enums.Service.DialingServiceTypes.THIS_PHONE) {
            stringBuilder.append("Dialing Service: This Phone; ");
        } else if (mDialingServiceType == Enums.Service.DialingServiceTypes.ALWAYS_ASK) {
            stringBuilder.append("Dialing Service: Always Ask; ");
        }

        if (mDisallowDialingServiceTypes != null) {
            stringBuilder.append("Disallow Dialing Service Types: ").append(Arrays.toString(mDisallowDialingServiceTypes)).append("; ");
        }

        if (!TextUtils.isEmpty(mTrackingId)) {
            stringBuilder.append("Tracking Id: ").append(mTrackingId);
        }

        stringBuilder.append("Has Left NWay: ").append(mHasLeftNWay ? "True" : "False");

        return stringBuilder.toString();
    }

    public static class Builder {

        @Nullable
        private NextivaContact mNextivaContact;
        @Nullable
        private String mDisplayName;
        @Nullable
        private String mCountryCode;
        @Nullable
        private String mNumberToCall;
        @Nullable
        private ChatConversation mChatConversation;
        @Enums.Sip.CallTypes.Type
        private int mCallType = Enums.Sip.CallTypes.NONE;
        @Enums.Service.DialingServiceTypes.DialingServiceType
        private int mDialingServiceType = Enums.Service.DialingServiceTypes.NONE;
        @Nullable
        @Enums.Service.DialingServiceTypes.DialingServiceType
        private int[] mDisallowDialingServiceTypes;
        private long mSessionID;
        private String mTrackingId;
        private boolean mHasLeftNWay = false;

        public Builder() {
        }

        public Builder setNextivaContact(@Nullable NextivaContact nextivaContact) {
            mNextivaContact = nextivaContact;
            return this;
        }

        public Builder setDisplayName(@Nullable String displayName) {
            mDisplayName = displayName;
            return this;
        }

        public Builder setCountryCode(@Nullable String countryCode) {
            mCountryCode = countryCode;
            return this;
        }

        public Builder setNumberToCall(@NonNull String numberToCall) {
            mNumberToCall = numberToCall;
            return this;
        }

        public Builder setChatConversation(@Nullable ChatConversation chatConversation) {
            mChatConversation = chatConversation;
            return this;
        }

        public Builder setCallType(@Enums.Sip.CallTypes.Type int callType) {
            mCallType = callType;
            return this;
        }

        public Builder setDialingServiceType(@Enums.Service.DialingServiceTypes.DialingServiceType int dialingServiceType) {
            mDialingServiceType = dialingServiceType;
            return this;
        }

        public Builder setDisallowDialingServiceTypes(@Nullable @Enums.Service.DialingServiceTypes.DialingServiceType int[] disallowDialingServiceTypes) {
            mDisallowDialingServiceTypes = disallowDialingServiceTypes;
            return this;
        }

        public Builder setSessionID(final long sessionID) {
            mSessionID = sessionID;
            return this;
        }

        public Builder setTrackingId(String trackingId) {
            mTrackingId = trackingId;
            return this;
        }

        public Builder setHasLeftNWay(boolean hasLeftNWay) {
            mHasLeftNWay = hasLeftNWay;
            return this;
        }

        public CallInfo build() {
            return new CallInfo(mNextivaContact,
                                mDisplayName,
                                mCountryCode,
                                mNumberToCall,
                                mChatConversation,
                                mCallType,
                                mDialingServiceType,
                                mDisallowDialingServiceTypes,
                                mSessionID,
                                mTrackingId,
                                mHasLeftNWay);
        }
    }
}
