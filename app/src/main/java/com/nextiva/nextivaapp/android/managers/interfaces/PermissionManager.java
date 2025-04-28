/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

@SuppressWarnings("ALL")
public interface PermissionManager {
    void requestInitialPermissions(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback);

    void requestPhonePermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback);

    void requestPhonePermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable View permissionSnackbarView);

    void requestVoiceCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback);

    void requestVoiceCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    void requestVoiceCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable View permissionSnackbarView);

    void requestVoiceCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView);

    void requestVoiceIncomingCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable View permissionSnackbarView);

    void requestVoiceIncomingCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView);

    void requestVideoCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    void requestVideoCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView);

    void requestFileLoggingPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    void requestFileLoggingPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView);

    void requestStorageToDownloadPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    public void requestContactsPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            boolean isConnect,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    void requestContactsPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    void requestContactsPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView,
            boolean isConnect);

    void requestNotificationsPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView,
            boolean isConnect);

    void requestAvatarCameraPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    void requestAvatarCameraPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView);

    void requestMeetingCameraPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    void requestMeetingCameraPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView);

    void requestMeetingAudioPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    void requestMeetingAudioPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView);

    void checkMeetingPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull String permission,
            int snackbarMessage,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback);

    interface PermissionGrantedCallback {
        void onPermissionGranted();
    }

    interface PermissionDeniedCallback {
        void onPermissionDenied();
    }
}
