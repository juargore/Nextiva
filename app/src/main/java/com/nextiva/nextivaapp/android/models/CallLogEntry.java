/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.util.DbConstants;
import com.nextiva.nextivaapp.android.util.StringUtil;
import com.nextiva.nextivaapp.android.util.extensions.PhoneCustomFormat;
import com.nextiva.nextivaapp.android.util.extensions.StringExtensionsKt;

import org.threeten.bp.Instant;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by adammacdonald on 2/12/18.
 */

public class CallLogEntry implements Serializable {

    @Nullable
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_LOG_ID)
    private String mCallLogId;
    @Nullable
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_DISPLAY_NAME)
    private String mDisplayName;
    @Nullable
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_DATE_TIME)
    private Long mCallTime;
    @Nullable
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_COUNTRY_CODE)
    private String mCountryCode;
    @Nullable
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_PHONE_NUMBER)
    private String mPhoneNumber;
    @Nullable
    @Enums.Calls.CallTypes.Type
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_TYPE)
    private String mCallType;
    @Nullable
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_IS_READ)
    private Boolean mIsRead;
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_DURATION)
    private int mCallDuration;
    @Nullable
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_START_TIME)
    private String mCallStartTime;
    @Nullable
    @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_PHOTO_DATA)
    private byte[] mAvatar;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_UI_NAME)
    private String mUiName;
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE)
    private int mContactType;
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_STATE)
    private int mPresenceState;
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRIORITY)
    private int mPresencePriority;
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_STATUS_TEXT)
    private String mStatusText;
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_JID)
    private String mJid;
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_TYPE)
    private int mPresenceType;


    /**
     * @param callLogId     The call log id
     * @param displayName   The display name
     * @param callTime      The call time in epoch
     * @param countryCode   The country code
     * @param phoneNumber   The phone number
     * @param callType      The call type
     * @param avatar        The avatar byte array (if applicable)
     * @param uiName        The UI name (if applicable)
     * @param presenceState The presence state (if applicable)
     * @param statusText    The users status text (if applicable)
     * @param contactType   The contact type (if applicable)
     * @param jid           The jid of the contact (if applicable)
     * @param presenceType  The presence type (if applicable)
     */
    public CallLogEntry(@Nullable String callLogId,
                        @Nullable String displayName,
                        @Nullable Long callTime,
                        @Nullable String countryCode,
                        @Nullable String phoneNumber,
                        @Nullable @Enums.Calls.CallTypes.Type String callType,
                        @Nullable byte[] avatar,
                        @Nullable String uiName,
                        @Enums.Contacts.PresenceStates.PresenceState int presenceState,
                        int presencePriority,
                        @Nullable String statusText,
                        int contactType,
                        String jid,
                        @Enums.Contacts.PresenceTypes.Type int presenceType,
                        int callDuration,
                        @Nullable String callStartTime
    ) {

        mCallLogId = callLogId;
        mDisplayName = displayName;
        mCallTime = callTime;
        mCountryCode = countryCode;
        mPhoneNumber = phoneNumber;
        mCallType = callType;
        mAvatar = avatar;
        mUiName = uiName;
        mPresenceState = presenceState;
        mPresencePriority = presencePriority;
        mStatusText = statusText;
        mContactType = contactType;
        mJid = jid;
        mPresenceType = presenceType;
        mCallDuration = callDuration;
        mCallStartTime = callStartTime;
    }

    /**
     * Determines the human readable name, based on either the UI Name or the Display Name (in that order)
     *
     * @return The human readable name
     */
    @Nullable
    public String getHumanReadableName() {
        if (!TextUtils.isEmpty(mUiName)) {
            return mUiName;

        } else if (!TextUtils.isEmpty(mDisplayName)) {
            return mDisplayName;

        } else if (!TextUtils.isEmpty(mPhoneNumber)){
            String numberToDial = StringExtensionsKt.extractFirstNumber(mPhoneNumber);
            numberToDial = numberToDial != null ? numberToDial : "";
            numberToDial = StringExtensionsKt.formatPhoneNumber(numberToDial, PhoneCustomFormat.Start, false);
            return numberToDial;
        }

        return null;
    }

    @Nullable
    public String getCallLogId() {
        return mCallLogId;
    }

    public void setCallLogId(@Nullable String callLogId) {
        mCallLogId = callLogId;
    }

    @Nullable
    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        mDisplayName = displayName;
    }

    public Instant getCallInstant() {
        if (mCallTime != null) {
            return Instant.ofEpochMilli(mCallTime);
        }

        return null;
    }

    /**
     * @return The call time in epoch
     */
    @Nullable
    public Long getCallTime() {
        return mCallTime;
    }

    /**
     * @param callTime The call time in epoch
     */
    public void setCallTime(@Nullable Long callTime) {
        mCallTime = callTime;
    }

    @Nullable
    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(@Nullable String countryCode) {
        mCountryCode = countryCode;
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @Nullable
    public String getFormattedPhoneNumber() {
        return TextUtils.isEmpty(mPhoneNumber) ? null : PhoneNumberUtils.formatNumber(mPhoneNumber, Locale.getDefault().getCountry());
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    @Nullable
    @Enums.Calls.CallTypes.Type
    public String getCallType() {
        return mCallType;
    }

    public void setCallType(@Nullable @Enums.Calls.CallTypes.Type String callType) {
        mCallType = callType;
    }

    public boolean getIsRead() {
        return mIsRead == null ? false : mIsRead;
    }

    public void setIsRead(@Nullable Boolean read) {
        mIsRead = read;
    }

    @Nullable
    public byte[] getAvatar() {
        return mAvatar;
    }

    public void setAvatar(@Nullable byte[] avatarString) {
        mAvatar = avatarString;
    }

    @Nullable
    public String getUiName() {
        return mUiName;
    }

    public void setUiName(@Nullable String uiName) {
        mUiName = uiName;
    }

    @Enums.Contacts.PresenceStates.PresenceState
    public int getPresenceState() {
        return mPresenceState;
    }

    public void setPresenceState(@Enums.Contacts.PresenceStates.PresenceState int presenceState) {
        mPresenceState = presenceState;
    }

    public int getPresencePriority() {
        return mPresencePriority;
    }

    public void setPresencePriority(int presencePriority) {
        mPresencePriority = presencePriority;
    }

    @Nullable
    public String getStatusText() {
        return TextUtils.isEmpty(mStatusText) ? "" : mStatusText;
    }

    public void setStatusText(@Nullable String statusText) {
        mStatusText = statusText;
    }

    public int getContactType() {
        return mContactType;
    }

    public void setContactType(final int contactType) {
        mContactType = contactType;
    }

    public String getJid() {
        return mJid;
    }

    public void setJid(String mJid) {
        this.mJid = mJid;
    }

    public int getPresenceType() {
        return mPresenceType;
    }

    public void setPresenceType(int mPresenceType) {
        this.mPresenceType = mPresenceType;
    }

    public String getCreateContactFirstName() {
        if (!TextUtils.isEmpty(getUiName())) {
            String[] split = getUiName().split("\\s+", 2);

            if (split.length > 0) {
                return split[0];
            }

        } else if (!TextUtils.isEmpty(getDisplayName())) {
            String[] split = getDisplayName().split("\\s+", 2);

            if (split.length > 0) {
                return split[0];
            }

        }

        return null;
    }

    public String getCreateContactLastName() {
        if (!TextUtils.isEmpty(getUiName())) {
            String[] split = getUiName().split("\\s+", 2);

            if (split.length > 1) {
                return split[1];
            }

        } else if (!TextUtils.isEmpty(getDisplayName())) {
            String[] split = getDisplayName().split("\\s+", 2);

            if (split.length > 1) {
                return split[1];
            }

        }

        return null;
    }

    public int getCallDuration() {
        return mCallDuration;
    }

    public String getCallStartTime() {
        return mCallStartTime;
    }

    public void setCallDuration(int mCallDuration) {
        this.mCallDuration = mCallDuration;
    }

    public void setCallStartTime(String startTime) {
        this.mCallStartTime = startTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof CallLogEntry) {
            CallLogEntry that = (CallLogEntry) obj;
            return StringUtil.equalsWithNullsAndBlanks(mCallLogId, that.getCallLogId()) &&
                    StringUtil.equalsWithNullsAndBlanks(mDisplayName, that.getDisplayName()) &&
                    (mCallTime != null && mCallTime.equals(that.getCallTime())) &&
                    StringUtil.equalsWithNullsAndBlanks(mCountryCode, that.getCountryCode()) &&
                    StringUtil.equalsWithNullsAndBlanks(mPhoneNumber, that.getPhoneNumber()) &&
                    StringUtil.equalsWithNullsAndBlanks(mCallType, that.getCallType()) &&
                    Arrays.equals(mAvatar, that.mAvatar) &&
                    StringUtil.equalsWithNullsAndBlanks(mUiName, that.getUiName()) &&
                    mPresenceState == that.getPresenceState() &&
                    StringUtil.equalsWithNullsAndBlanks(mStatusText, that.getStatusText()) &&
                    mContactType == that.getContactType() &&
                    getIsRead() == that.getIsRead();
        }

        return false;
    }
}
