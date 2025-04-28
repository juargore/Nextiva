/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/16/18.
 */

public class ServiceSettings implements Serializable, Cloneable {

    @NonNull
    @Enums.Service.Type
    private String mType;
    @NonNull
    private String mUri;
    @Nullable
    private Boolean mActive;
    @Nullable
    private Boolean mRingSplashEnabled;
    @Nullable
    private Integer mNumberOfRings;
    @Nullable
    private String mRemoteOfficeNumber;
    @Nullable
    private String mForwardToPhoneNumber;
    @Nullable
    private Boolean mAlertAllLocationsForClickToDialCalls;
    @Nullable
    private Boolean mAlertAllLocationsForGroupPagingCalls;
    @Nullable
    private ArrayList<NextivaAnywhereLocation> mNextivaAnywhereLocationsList;
    @Nullable
    private Boolean mDontRingWhileOnCall;
    @Nullable
    private ArrayList<SimultaneousRingLocation> mSimultaneousRingLocationsList;

    public ServiceSettings(@NonNull String type, @NonNull String uri) {
        mType = type;
        mUri = uri;
    }

    @SuppressLint("UseValueOf")
    @SuppressWarnings( {"StringOperationCanBeSimplified", "BoxingBoxedValue", "BooleanConstructorCall", "UnnecessaryBoxing"})
    public ServiceSettings(@NonNull ServiceSettings serviceSettings) {
        mType = new String(serviceSettings.getType());
        mUri = new String(serviceSettings.getUri());
        mActive = serviceSettings.getActiveRaw() != null ? new Boolean(serviceSettings.getActive()) : null;
        mRingSplashEnabled = serviceSettings.getRingSplashEnabledRaw() != null ? new Boolean(serviceSettings.getRingSplashEnabled()) : null;
        mNumberOfRings = serviceSettings.getNumberOfRings() != null ? new Integer(serviceSettings.getNumberOfRings()) : null;
        mRemoteOfficeNumber = serviceSettings.getRemoteOfficeNumber() != null ? new String(serviceSettings.getRemoteOfficeNumber()) : null;
        mForwardToPhoneNumber = serviceSettings.getForwardToPhoneNumber() != null ? new String(serviceSettings.getForwardToPhoneNumber()) : null;
        mAlertAllLocationsForClickToDialCalls = serviceSettings.getAlertAllLocationsForClickToDialCallsRaw() != null ? new Boolean(serviceSettings.getAlertAllLocationsForClickToDialCalls()) : null;
        mAlertAllLocationsForGroupPagingCalls = serviceSettings.getAlertAllLocationsForGroupPagingCallsRaw() != null ? new Boolean(serviceSettings.getAlertAllLocationsForGroupPagingCalls()) : null;
        mNextivaAnywhereLocationsList = serviceSettings.getNextivaAnywhereLocationsList() != null ? new ArrayList<>(serviceSettings.getNextivaAnywhereLocationsList()) : null;
        mDontRingWhileOnCall = serviceSettings.getDontRingWhileOnCallRaw() != null ? new Boolean(serviceSettings.getDontRingWhileOnCall()) : null;
        mSimultaneousRingLocationsList = serviceSettings.getSimultaneousRingLocationsList() != null ? new ArrayList<>(serviceSettings.getSimultaneousRingLocationsList()) : null;
    }

    public ServiceSettings(
            @NonNull @Enums.Service.Type String type,
            @NonNull String uri,
            @Nullable Boolean active,
            @Nullable Boolean ringSplashEnabled,
            @Nullable Integer numberOfRings,
            @Nullable String remoteOfficeNumber,
            @Nullable String forwardToPhoneNumber,
            @Nullable Boolean alertAllLocationsForClickToDialCalls,
            @Nullable Boolean alertAllLocationsForGroupPagingCalls,
            @Nullable ArrayList<NextivaAnywhereLocation> nextivaAnywhereLocationsList,
            @Nullable Boolean dontRingWhileOnCall,
            @Nullable ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList) {

        mType = type;
        mUri = uri;
        mActive = active;
        mRingSplashEnabled = ringSplashEnabled;
        mNumberOfRings = numberOfRings;
        mRemoteOfficeNumber = remoteOfficeNumber;
        mForwardToPhoneNumber = forwardToPhoneNumber;
        mAlertAllLocationsForClickToDialCalls = alertAllLocationsForClickToDialCalls;
        mAlertAllLocationsForGroupPagingCalls = alertAllLocationsForGroupPagingCalls;
        mNextivaAnywhereLocationsList = nextivaAnywhereLocationsList;
        mDontRingWhileOnCall = dontRingWhileOnCall;
        mSimultaneousRingLocationsList = simultaneousRingLocationsList;
    }

    @NonNull
    @Enums.Service.Type
    public String getType() {
        return mType;
    }

    public void setType(@NonNull @Enums.Service.Type String type) {
        mType = type;
    }

    @NonNull
    public String getUri() {
        return mUri;
    }

    public void setUri(@NonNull String uri) {
        mUri = uri;
    }

    public boolean getActive() {
        return mActive == null ? false : mActive;
    }

    public void setActive(@Nullable Boolean active) {
        mActive = active;
    }

    @Nullable
    public Boolean getActiveRaw() {
        return mActive;
    }

    public boolean getRingSplashEnabled() {
        return mRingSplashEnabled == null ? false : mRingSplashEnabled;
    }

    public void setRingSplashEnabled(@Nullable Boolean ringSplashEnabled) {
        mRingSplashEnabled = ringSplashEnabled;
    }

    @Nullable
    public Boolean getRingSplashEnabledRaw() {
        return mRingSplashEnabled;
    }

    @Nullable
    public Integer getNumberOfRings() {
        return mNumberOfRings;
    }

    public void setNumberOfRings(@Nullable Integer numberOfRings) {
        mNumberOfRings = numberOfRings;
    }

    @Nullable
    public String getRemoteOfficeNumber() {
        return mRemoteOfficeNumber;
    }

    public void setRemoteOfficeNumber(@Nullable String remoteOfficeNumber) {
        mRemoteOfficeNumber = remoteOfficeNumber;
    }

    @Nullable
    public String getForwardToPhoneNumber() {
        return mForwardToPhoneNumber;
    }

    public void setForwardToPhoneNumber(@Nullable String forwardToPhoneNumber) {
        mForwardToPhoneNumber = forwardToPhoneNumber;
    }

    public boolean getAlertAllLocationsForClickToDialCalls() {
        return mAlertAllLocationsForClickToDialCalls == null ? false : mAlertAllLocationsForClickToDialCalls;
    }

    @Nullable
    public Boolean getAlertAllLocationsForClickToDialCallsRaw() {
        return mAlertAllLocationsForClickToDialCalls;
    }

    public void setAlertAllLocationsForClickToDialCalls(@Nullable Boolean alertAllLocationsForClickToDialCalls) {
        mAlertAllLocationsForClickToDialCalls = alertAllLocationsForClickToDialCalls;
    }

    public boolean getAlertAllLocationsForGroupPagingCalls() {
        return mAlertAllLocationsForGroupPagingCalls == null ? false : mAlertAllLocationsForGroupPagingCalls;
    }

    @Nullable
    public Boolean getAlertAllLocationsForGroupPagingCallsRaw() {
        return mAlertAllLocationsForGroupPagingCalls;
    }

    public void setAlertAllLocationsForGroupPagingCalls(@Nullable Boolean alertAllLocationsForGroupPagingCalls) {
        mAlertAllLocationsForGroupPagingCalls = alertAllLocationsForGroupPagingCalls;
    }

    @Nullable
    public ArrayList<NextivaAnywhereLocation> getNextivaAnywhereLocationsList() {
        return mNextivaAnywhereLocationsList;
    }

    public void setNextivaAnywhereLocationsList(@Nullable ArrayList<NextivaAnywhereLocation> nextivaAnywhereLocationsList) {
        mNextivaAnywhereLocationsList = nextivaAnywhereLocationsList;
    }

    public boolean getDontRingWhileOnCall() {
        return mDontRingWhileOnCall == null ? false : mDontRingWhileOnCall;
    }

    @Nullable
    public Boolean getDontRingWhileOnCallRaw() {
        return mDontRingWhileOnCall;
    }

    public void setDontRingWhileOnCall(@Nullable Boolean dontRingWhileOnCall) {
        mDontRingWhileOnCall = dontRingWhileOnCall;
    }

    @Nullable
    public ArrayList<SimultaneousRingLocation> getSimultaneousRingLocationsList() {
        return mSimultaneousRingLocationsList;
    }

    public void setSimultaneousRingLocationsList(@Nullable ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList) {
        mSimultaneousRingLocationsList = simultaneousRingLocationsList;
    }
}
