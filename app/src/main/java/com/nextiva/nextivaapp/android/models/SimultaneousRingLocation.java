/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class SimultaneousRingLocation implements Serializable {

    @Nullable
    private String mPhoneNumber;
    @Nullable
    private Boolean mAnswerConfirmationRequired;

    public SimultaneousRingLocation() {
    }

    @SuppressLint("UseValueOf")
    public SimultaneousRingLocation(
            @Nullable String phoneNumber,
            @Nullable Boolean answerConfirmationRequired) {

        if (phoneNumber != null) {
            //noinspection StringOperationCanBeSimplified
            mPhoneNumber = new String(phoneNumber);
        }

        // noinspection UseValueOf,BoxingBoxedValue,BooleanConstructorCall
        mAnswerConfirmationRequired = answerConfirmationRequired != null ? new Boolean(answerConfirmationRequired) : null;

    }

    public SimultaneousRingLocation(@NonNull final SimultaneousRingLocation simultaneousRingLocation) {
        this(simultaneousRingLocation.mPhoneNumber,
             simultaneousRingLocation.mAnswerConfirmationRequired);
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        mPhoneNumber = phoneNumber;
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
}
