/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by adammacdonald on 3/21/18.
 */

public interface CallManager {

    void makeCall(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull ParticipantInfo participantInfo,
            @NonNull CompositeDisposable compositeDisposable);

    void makeCall(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull ParticipantInfo participantInfo,
            @Nullable String retrievalNumber,
            @NonNull CompositeDisposable compositeDisposable);

    void processParticipantInfo(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull ParticipantInfo participantInfo,
            @Nullable String retrievalNumber,
            @NonNull CompositeDisposable compositeDisposable,
            @NonNull ProcessParticipantInfoCallBack processCallInfoCallBack);

    void processParticipantInfo(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull ParticipantInfo participantInfo,
            @NonNull CompositeDisposable compositeDisposable,
            @NonNull ProcessParticipantInfoCallBack processCallInfoCallBack);

    interface ProcessParticipantInfoCallBack {
        void onParticipantInfoProcessed(
                @NonNull Activity activity,
                @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
                @NonNull ParticipantInfo participantInfo,
                @Nullable String retrievalNumber,
                @NonNull CompositeDisposable compositeDisposable);

    }
}
