/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.EmailAddress;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

public class FakeIntentManager implements IntentManager {

    @Inject
    public FakeIntentManager() {
    }

    @Override
    public void callPhone(@NotNull Activity activity, @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName, @NotNull String numberToCall) {

    }

    @Override
    public void sendPersonalSMS(@NotNull Activity activity, @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName, @NotNull String numberToSMS, @NotNull String name, @NotNull String body, @NotNull String subject) {

    }

    @Override
    public void sendEmail(@NotNull Activity activity, @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName, @NotNull String email, @NotNull String name, @NotNull String body, @NotNull String subject, @Nullable File attachment) {

    }

    @Override
    public void showUrl(@NotNull Activity activity, @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName, @NotNull String url) {

    }

    @Override
    public void addToLocalContacts(@NotNull Activity activity, @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName, @NotNull String name, @Nullable ArrayList<PhoneNumber> numbers, @Nullable ArrayList<EmailAddress> emails) {

    }

    @Override
    public void addToLocalContacts(@NotNull Activity activity, @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName, @NotNull String name, @Nullable String phone, @Nullable String phoneType, @Nullable String email, @Nullable String emailType) {

    }

    @Override
    public boolean isAbleToMakePhoneCall(@NotNull Context context) {
        return false;
    }

    @NotNull
    @Override
    public Intent getInitialIntent(@NotNull Context context) {
        return new Intent();
    }

    @NotNull
    @Override
    public Intent newActiveCallActivityIntent(@NotNull Context context, @NotNull ParticipantInfo callInfo, @Nullable String retrievalNumber) {
        return new Intent();
    }

    @NotNull
    @Override
    public Intent newActiveCallActivityIntent(@NotNull Context context, @NotNull ParticipantInfo callInfo) {
        return newActiveCallActivityIntent(context, callInfo, null);
    }

    @Override
    public void navigateToInternetSettings(@NotNull Context context) {

    }

    @Override
    public void navigateToPermissionSettings(@NotNull Context context) {

    }
}
