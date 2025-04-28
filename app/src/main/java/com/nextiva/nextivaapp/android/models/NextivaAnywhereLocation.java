/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;

import java.io.Serializable;

public class NextivaAnywhereLocation implements Serializable {

    @NonNull
    private String mPhoneNumber;
    @Nullable
    private String mDescription;
    @Nullable
    private Boolean mActive;
    @Nullable
    private Boolean mCallControlEnabled;
    @Nullable
    private Boolean mPreventDivertingCalls;
    @Nullable
    private Boolean mAnswerConfirmationRequired;

    public NextivaAnywhereLocation(@NonNull NextivaAnywhereLocation nextivaAnywhereLocation) {
        mPhoneNumber = nextivaAnywhereLocation.getPhoneNumber();
        mDescription = nextivaAnywhereLocation.getDescription();
        mActive = nextivaAnywhereLocation.getActiveRaw();
        mCallControlEnabled = nextivaAnywhereLocation.getCallControlEnabledRaw();
        mPreventDivertingCalls = nextivaAnywhereLocation.getPreventDivertingCallsRaw();
        mAnswerConfirmationRequired = nextivaAnywhereLocation.getAnswerConfirmationRequiredRaw();
    }

    public NextivaAnywhereLocation(
            @NonNull String phoneNumber,
            @Nullable String description,
            @Nullable Boolean active,
            @Nullable Boolean callControlEnabled,
            @Nullable Boolean preventDivertingCalls,
            @Nullable Boolean answerConfirmationRequired) {

        mPhoneNumber = phoneNumber;
        mDescription = description;
        mActive = active;
        mCallControlEnabled = callControlEnabled;
        mPreventDivertingCalls = preventDivertingCalls;
        mAnswerConfirmationRequired = answerConfirmationRequired;
    }

    @NonNull
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(@Nullable String description) {
        mDescription = description;
    }

    public boolean getActive() {
        return mActive == null ? false : mActive;
    }

    @Nullable
    public Boolean getActiveRaw() {
        return mActive;
    }

    public void setActive(@Nullable Boolean active) {
        mActive = active;
    }

    public boolean getCallControlEnabled() {
        return mCallControlEnabled == null ? false : mCallControlEnabled;
    }

    @Nullable
    public Boolean getCallControlEnabledRaw() {
        return mCallControlEnabled;
    }

    public void setCallControlEnabled(@Nullable Boolean callControlEnabled) {
        mCallControlEnabled = callControlEnabled;
    }

    public boolean getPreventDivertingCalls() {
        return mPreventDivertingCalls == null ? false : mPreventDivertingCalls;
    }

    @Nullable
    public Boolean getPreventDivertingCallsRaw() {
        return mPreventDivertingCalls;
    }

    public void setPreventDivertingCalls(@Nullable Boolean preventDivertingCalls) {
        mPreventDivertingCalls = preventDivertingCalls;
    }

    public boolean getAnswerConfirmationRequired() {
        return mAnswerConfirmationRequired == null ? false : mAnswerConfirmationRequired;
    }

    @Nullable
    public Boolean getAnswerConfirmationRequiredRaw() {
        return mAnswerConfirmationRequired;
    }

    public void setAnswerConfirmationRequired(@Nullable Boolean answerConfirmationRequired) {
        mAnswerConfirmationRequired = answerConfirmationRequired;
    }

    /**
     * Standard equality check, comparing the uniqeily important fields inside the NextivaAnywhereLocation
     * in order to determine if two NextivaAnywhereLocations are the same objects, when the data
     * is taken into consideration
     *
     * @param obj The object to compare against
     * @return Whether this object and the object compared against would be the same underlying object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof NextivaAnywhereLocation) {
            NextivaAnywhereLocation that = (NextivaAnywhereLocation) obj;
            return TextUtils.equals(CallUtil.cleanForTextWatcher(mPhoneNumber), CallUtil.cleanForTextWatcher(that.getPhoneNumber())) &&
                    StringUtil.equalsWithNullsAndBlanks(mDescription, that.getDescription()) &&
                    getActive() == that.getActive() &&
                    getCallControlEnabled() == that.getCallControlEnabled() &&
                    getPreventDivertingCalls() == that.getPreventDivertingCalls() &&
                    getAnswerConfirmationRequired() == that.getAnswerConfirmationRequired();
        }

        return false;
    }
}
