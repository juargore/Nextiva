/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.INITIAL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.INITIAL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.INITIAL_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.MEETING_AUDIO_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.MEETING_AUDIO_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.MEETING_CAMERA_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.MEETING_CAMERA_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.MEETING_PERMISSION_ERROR_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.POST_NOTIFICATIONS_REQUEST_PERMISSION_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.VOICE_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.VOICE_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.VOICE_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.INCOMING_CALL;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.dialogs.PermissionRationaleDialog;
import com.nextiva.nextivaapp.android.listeners.NextivaPermissionListener;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager;
import com.nextiva.nextivaapp.android.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class NextivaPermissionManager implements PermissionManager {

    private final DialogManager mDialogManager;
    private final AnalyticsManager mAnalyticsManager;
    private final IntentManager mIntentManager;
    private final LogManager mLogManager;

    @Inject
    public NextivaPermissionManager(@NonNull DialogManager dialogManager,
                                    @NonNull AnalyticsManager analyticsManager,
                                    @NonNull IntentManager intentManager,
                                    @NonNull LogManager logManager) {

        mDialogManager = dialogManager;
        mAnalyticsManager = analyticsManager;
        mIntentManager = intentManager;
        mLogManager = logManager;
    }

    @Nullable
    private String getVideoCallRationaleMessage(
            @NonNull Context context,
            @Nullable List<PermissionRequest> permissions) {

        if (permissions == null || permissions.isEmpty()) {
            return null;
        }

        int choosePermissionMessage = 0;

        for (PermissionRequest permissionRequest : permissions) {
            switch (permissionRequest.getName()) {
                case Manifest.permission.CAMERA:
                    choosePermissionMessage += 1;
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    choosePermissionMessage += 2;
                    break;
            }
        }

        switch (choosePermissionMessage) {
            case 1:
                return context.getString(R.string.permission_video_call_camera_rationale_message, context.getString(R.string.app_name));
            case 2:
                return context.getString(R.string.permission_video_call_record_audio_rationale_message, context.getString(R.string.app_name));
            case 3:
                return context.getString(R.string.permission_video_call_camera_record_audio_rationale_message, context.getString(R.string.app_name));
        }

        return null;
    }

    @StringRes
    private int getVideoCallDeniedMessage(
            @Nullable List<PermissionDeniedResponse> deniedPermissions,
            boolean shouldHaveShownCameraPermissionRationaleDialog,
            boolean shouldHaveShownMicrophonePermissionRationaleDialog) {

        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            return 0;
        }

        int choosePermissionMessage = 0;

        for (PermissionDeniedResponse permissionDeniedResponse : deniedPermissions) {
            switch (permissionDeniedResponse.getPermissionName()) {
                case Manifest.permission.CAMERA:
                    if (permissionDeniedResponse.isPermanentlyDenied() && !shouldHaveShownCameraPermissionRationaleDialog) {
                        choosePermissionMessage += 1;
                    }
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    if (permissionDeniedResponse.isPermanentlyDenied() && !shouldHaveShownMicrophonePermissionRationaleDialog) {
                        choosePermissionMessage += 2;
                    }
                    break;
            }
        }

        switch (choosePermissionMessage) {
            case 1:
                return R.string.permission_video_call_camera_denied_message;
            case 2:
                return R.string.permission_video_call_record_audio_denied_message;
            case 3:
                return R.string.permission_video_call_camera_record_audio_denied_message;
        }

        return 0;
    }

    @NonNull
    private MultiplePermissionsListener getVideoCallPermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View snackbarPermissionView) {

        if (snackbarPermissionView == null) {
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        boolean shouldHaveShownCameraPermissionRationaleDialog = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA);
        boolean shouldHaveShownMicrophonePermissionRationaleDialog = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO);

        final View finalSnackbarPermissionView = snackbarPermissionView;
        return new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (permissionGrantedCallback != null) {
                        permissionGrantedCallback.onPermissionGranted();
                    }

                    return;
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    @StringRes
                    int deniedMessageStringResId = getVideoCallDeniedMessage(report.getDeniedPermissionResponses(),
                                                                             shouldHaveShownCameraPermissionRationaleDialog,
                                                                             shouldHaveShownMicrophonePermissionRationaleDialog);

                    if (deniedMessageStringResId != 0) {
                        final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                                .with(finalSnackbarPermissionView, deniedMessageStringResId)
                                .withButton(R.string.general_settings,
                                            v -> {
                                                mAnalyticsManager.logEvent(analyticsScreenName, VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED);
                                                mIntentManager.navigateToPermissionSettings(activity);
                                            })
                                .build();

                        PermissionRequest request = new PermissionRequest(Manifest.permission.CAMERA);
                        PermissionDeniedResponse response = new PermissionDeniedResponse(request, true);

                        snackbarOnDeniedPermissionListener.onPermissionDenied(response);
                    }
                }

                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                //Needed for Samsungs permissions to function correctly
                token.continuePermissionRequest();


                String rationaleMessage = getVideoCallRationaleMessage(activity, permissions);

                if (!TextUtils.isEmpty(rationaleMessage) && activity.hasWindowFocus()) {
                    mAnalyticsManager.logEvent(analyticsScreenName, VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN);

                    if (!activity.isFinishing() && activity.hasWindowFocus()) {
                        mDialogManager.showDialog(activity,
                                activity.getString(R.string.permission_required_title),
                                rationaleMessage,
                                activity.getString(R.string.general_ok),
                                (dialog, which) -> {
                                    token.continuePermissionRequest();
                                    mAnalyticsManager.logEvent(analyticsScreenName, VIDEO_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED);
                                });
                    }
                }
            }
        };
    }

    @Nullable
    private String getInitialRationaleMessage(
            @NonNull Context context,
            @Nullable List<PermissionRequest> permissions) {

        if (permissions == null || permissions.isEmpty()) {
            return null;
        }

        int choosePermissionMessage = 0;

        for (PermissionRequest permissionRequest : permissions) {
            switch (permissionRequest.getName()) {
                case Manifest.permission.CAMERA:
                    choosePermissionMessage += 1;
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    choosePermissionMessage += 2;
                    break;
                case Manifest.permission.READ_CONTACTS:
                    choosePermissionMessage += 4;
                    break;
            }
        }

        switch (choosePermissionMessage) {
            case 1:
                // Video denied
                return context.getString(R.string.permission_video_call_camera_rationale_message, context.getString(R.string.app_name));
            case 2:
                // Audio denied
                return context.getString(R.string.permission_video_call_record_audio_rationale_message, context.getString(R.string.app_name));
            case 3:
                // Video and Audio denied
                return context.getString(R.string.permission_video_call_camera_record_audio_rationale_message, context.getString(R.string.app_name));
            case 4:
                // Contacts denied
                return context.getString(R.string.permission_contacts_rationale_message, context.getString(R.string.app_name));
            case 5:
                // Contacts and Video denied
                return context.getString(R.string.permission_video_call_camera_contacts_rationale_message, context.getString(R.string.app_name));
            case 6:
                // Contacts and Audio denied
                return context.getString(R.string.permission_record_audio_contacts_rationale_message, context.getString(R.string.app_name));
            case 7:
                // Contacts, Audio and Video denied
                return context.getString(R.string.permission_video_call_camera_record_audio_contacts_rationale_message, context.getString(R.string.app_name));
        }

        return null;
    }

    @StringRes
    private int getInitialDeniedMessage(
            Context context,
            @Nullable List<PermissionDeniedResponse> deniedPermissions,
            boolean shouldHaveShownCameraPermissionRationaleDialog,
            boolean shouldHaveShownMicrophonePermissionRationaleDialog,
            boolean shouldHaveShownReadContactsPermissionRationaleDialog,
            boolean shouldHaveShownCallPhonePermissionRationaleDialog,
            boolean shouldHaveShownNotificationPermissionRationaleDialog
    ) {

        int choosePermissionMessage = 0;

        for (PermissionDeniedResponse permissionDeniedResponse : deniedPermissions) {
            switch (permissionDeniedResponse.getPermissionName()) {
                case Manifest.permission.CAMERA:
                    if (permissionDeniedResponse.isPermanentlyDenied() && !shouldHaveShownCameraPermissionRationaleDialog) {
                        choosePermissionMessage += 1;
                    }
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    if (permissionDeniedResponse.isPermanentlyDenied() && !shouldHaveShownMicrophonePermissionRationaleDialog) {
                        choosePermissionMessage += 2;
                    }
                    break;
                case Manifest.permission.READ_CONTACTS:
                    if (permissionDeniedResponse.isPermanentlyDenied() && !shouldHaveShownReadContactsPermissionRationaleDialog) {
                        choosePermissionMessage += 4;
                    }
                    break;
                case Manifest.permission.CALL_PHONE:
                    if (permissionDeniedResponse.isPermanentlyDenied() && !shouldHaveShownCallPhonePermissionRationaleDialog) {
                        choosePermissionMessage += 8;
                    }
                    break;
                case Manifest.permission.POST_NOTIFICATIONS:
                    if (permissionDeniedResponse.isPermanentlyDenied() && !shouldHaveShownNotificationPermissionRationaleDialog) {
                        choosePermissionMessage += 16;
                    }
                    break;
            }

        }

        //This is for custom denied messages
            switch (choosePermissionMessage) {
                case 1:
                    // Video denied
                    return R.string.permission_video_call_camera_denied_message;
                case 2:
                    // Audio denied
                    return R.string.permission_video_call_record_audio_denied_message;
                case 3:
                    // Video and Audio denied
                    return R.string.permission_video_call_camera_record_audio_denied_message;
                case 4:
                    // Contacts denied
                    return 0;
                case 5:
                    // Contacts and Video denied
                    return R.string.permission_video_call_camera_contacts_denied_message;
                case 6:
                    // Contacts and Audio denied
                    return R.string.permission_record_audio_contacts_denied_message;
                case 7:
                    // Contacts, Audio and Video denied
                    return R.string.permission_video_call_camera_record_audio_contacts_denied_message;
                default:
                    return R.string.permission_generic_record_audio_contacts_denied_message;
            }
    }

    @NonNull
    private MultiplePermissionsListener getInitialPermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @SuppressWarnings("SameParameterValue")
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @SuppressWarnings("SameParameterValue")
            @Nullable View snackbarPermissionView) {

        if (snackbarPermissionView == null) {
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        boolean shouldHaveShownCameraPermissionRationaleDialog = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA);
        boolean shouldHaveShownMicrophonePermissionRationaleDialog = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO);
        boolean shouldHaveShownReadContactsPermissionRationaleDialog = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS);
        boolean shouldHaveShownCallPhonePermissionRationaleDialog = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE);

        boolean shouldHaveNotificationPermissionRationaleDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            shouldHaveNotificationPermissionRationaleDialog = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS);
        } else {
            NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            shouldHaveNotificationPermissionRationaleDialog = !notificationManager.areNotificationsEnabled();
        }

        final View finalSnackbarPermissionView = snackbarPermissionView;
        return new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                for (PermissionDeniedResponse response: report.getDeniedPermissionResponses()) {
                    mLogManager.logToFile(Enums.Logging.STATE_INFO, activity.getString(R.string.log_message_permission_not_granted, response.getPermissionName()));
                }

                if (!report.getGrantedPermissionResponses().isEmpty()) {
                    for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
                        if (TextUtils.equals(response.getPermissionName(), Manifest.permission.READ_CONTACTS) && permissionGrantedCallback != null) {
                            permissionGrantedCallback.onPermissionGranted();
                        }
                    }
                }

                if (report.isAnyPermissionPermanentlyDenied() || shouldHaveNotificationPermissionRationaleDialog) {
                    @StringRes
                    int deniedMessageStringResId = getInitialDeniedMessage(activity,
                                                                           report.getDeniedPermissionResponses(),
                                                                           shouldHaveShownCameraPermissionRationaleDialog,
                                                                           shouldHaveShownMicrophonePermissionRationaleDialog,
                                                                           shouldHaveShownReadContactsPermissionRationaleDialog,
                                                                           shouldHaveShownCallPhonePermissionRationaleDialog,
                                                                           shouldHaveNotificationPermissionRationaleDialog);


                    if (deniedMessageStringResId != 0) {
                    String permissionsList = (deniedMessageStringResId == R.string.permission_generic_record_audio_contacts_denied_message)?
                            activity.getString(R.string.permission_generic_record_audio_contacts_denied_message, getDeniedPermisions(activity, report.getDeniedPermissionResponses())) :
                            activity.getString(deniedMessageStringResId);

                        final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                                .with(finalSnackbarPermissionView, permissionsList)
                                .withButton(R.string.general_settings,
                                            v -> {
                                                mAnalyticsManager.logEvent(analyticsScreenName, INITIAL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED);
                                                mIntentManager.navigateToPermissionSettings(activity);
                                            })
                                .build();

                        PermissionRequest request = new PermissionRequest(Manifest.permission.CAMERA);
                        PermissionDeniedResponse response = new PermissionDeniedResponse(request, true);

                        snackbarOnDeniedPermissionListener.onPermissionDenied(response);
                    }
                }

                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                //Needed for Samsungs permissions to function correctly
                token.continuePermissionRequest();

                String rationaleMessage = getInitialRationaleMessage(activity, permissions);

                if (!TextUtils.isEmpty(rationaleMessage) && activity.hasWindowFocus()) {
                    mAnalyticsManager.logEvent(analyticsScreenName, INITIAL_PERMISSION_RATIONALE_DIALOG_SHOWN);

                    mDialogManager.showDialog(activity,
                                              activity.getString(R.string.permission_required_title),
                                              rationaleMessage,
                                              activity.getString(R.string.general_ok),
                                              (dialog, which) -> {
                                                  token.continuePermissionRequest();
                                                  mAnalyticsManager.logEvent(analyticsScreenName, INITIAL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED);
                                              });
                }
            }
        };
    }

    @NonNull
    private PermissionListener getVoiceCallPermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable View snackbarPermissionView) {
        return getVoiceCallPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, null, snackbarPermissionView);
    }

        @NonNull
        private PermissionListener getVoiceCallPermissionListener(
        @NonNull final Activity activity,
        @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
        @Nullable PermissionGrantedCallback permissionGrantedCallback,
        @Nullable PermissionDeniedCallback permissionDeniedCallback,
        @Nullable View snackbarPermissionView) {

        if (snackbarPermissionView == null) {
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                .with(snackbarPermissionView, R.string.permission_voice_call_record_audio_denied_message)
                .withButton(R.string.general_settings,
                            v -> {
                                mAnalyticsManager.logEvent(analyticsScreenName, VOICE_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED);
                                mIntentManager.navigateToPermissionSettings(activity);
                            })
                .build();

        return new NextivaPermissionListener(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.onPermissionGranted();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.RECORD_AUDIO, true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied() && !mShouldHaveShownRationaleDialog) {
                    snackbarOnDeniedPermissionListener.onPermissionDenied(response);
                }

                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.RECORD_AUDIO, false);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                mAnalyticsManager.logEvent(analyticsScreenName, VOICE_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN);

                //Needed for Samsungs permissions to function correctly
                token.continuePermissionRequest();

                if(activity.hasWindowFocus())
                    mDialogManager.showDialog(activity,
                                          activity.getString(R.string.permission_required_title),
                                          activity.getString(R.string.permission_voice_call_record_audio_rationale_message, activity.getString(R.string.app_name)),
                                          activity.getString(R.string.general_ok),
                                          (dialog, which) -> {
                                              token.continuePermissionRequest();
                                              mAnalyticsManager.logEvent(analyticsScreenName, VOICE_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED);
                                          });
            }
        };
    }

    @NonNull
    private PermissionListener getCallPhonePermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable View snackbarPermissionView) {

        if (snackbarPermissionView == null) {
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                .with(snackbarPermissionView, R.string.permission_call_denied_message)
                .withButton(R.string.general_settings,
                            v -> {
                                mAnalyticsManager.logEvent(analyticsScreenName, THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED);
                                mIntentManager.navigateToPermissionSettings(activity);
                            })
                .build();

        return new NextivaPermissionListener(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {


            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.onPermissionGranted();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.CALL_PHONE, true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied() && !mShouldHaveShownRationaleDialog) {
                    snackbarOnDeniedPermissionListener.onPermissionDenied(response);
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.CALL_PHONE, false);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                mAnalyticsManager.logEvent(analyticsScreenName, THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_SHOWN);

                //Needed for Samsungs permissions to function correctly
                //token.continuePermissionRequest();

                if (activity.hasWindowFocus()) {
                    mDialogManager.showDialog(activity,
                            activity.getString(R.string.permission_required_title),
                            activity.getString(R.string.permission_call_rationale_message, activity.getString(R.string.app_name)),
                            activity.getString(R.string.general_ok),
                            (dialog, which) -> {
                                token.continuePermissionRequest();
                                mAnalyticsManager.logEvent(analyticsScreenName, THIS_PHONE_CALL_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED);
                            });
                }
            }
        };
    }

    @NonNull
    private PermissionListener getWriteExternalStoragePermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View snackBarPermissionView,
            Integer snackBarPermissionDescription,
            Integer rationalePermissionDescription) {

        if (snackBarPermissionView == null) {
            snackBarPermissionView = activity.findViewById(android.R.id.content);
        }

        final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                .with(snackBarPermissionView, snackBarPermissionDescription)
                .withButton(R.string.general_settings,
                            v -> {
                                mAnalyticsManager.logEvent(analyticsScreenName, WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED);
                                mIntentManager.navigateToPermissionSettings(activity);
                            })
                .build();

        return new NextivaPermissionListener(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.onPermissionGranted();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.WRITE_EXTERNAL_STORAGE, true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied() && !mShouldHaveShownRationaleDialog) {
                    snackbarOnDeniedPermissionListener.onPermissionDenied(response);
                }

                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.WRITE_EXTERNAL_STORAGE, false);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                mAnalyticsManager.logEvent(analyticsScreenName, WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_SHOWN);

                if (activity.hasWindowFocus()) {
                    mDialogManager.showDialog(activity,
                            activity.getString(R.string.permission_required_title),
                            activity.getString(rationalePermissionDescription, activity.getString(R.string.app_name)),
                            activity.getString(R.string.general_ok),
                            (dialog, which) -> {
                                token.continuePermissionRequest();
                                mAnalyticsManager.logEvent(analyticsScreenName, WRITE_EXTERNAL_STORAGE_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED);
                            });
                }
            }
        };
    }

    @NonNull
    private PermissionListener getContactsPermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View snackbarPermissionView,
            boolean isConnect) {

        if (snackbarPermissionView == null) {
            //noinspection UnusedAssignment
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        return new NextivaPermissionListener(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.onPermissionGranted();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.READ_CONTACTS, true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.READ_CONTACTS, false);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                mAnalyticsManager.logEvent(analyticsScreenName, LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_SHOWN);

                if (activity.hasWindowFocus()) {
                    if (isConnect) {
                        new PermissionRationaleDialog(activity,
                                activity.getString(R.string.connect_permission_contacts_rationale_message, activity.getString(R.string.app_name)),
                                R.drawable.request_permission_contacts,
                                () -> {
                                    token.continuePermissionRequest();
                                    return null;
                                }).show();

                    } else {
                        mDialogManager.showDialog(activity,
                                activity.getString(R.string.permission_required_title),
                                activity.getString(R.string.permission_contacts_rationale_message, activity.getString(R.string.app_name)),
                                activity.getString(R.string.general_ok),
                                (dialog, which) -> {
                                    token.continuePermissionRequest();
                                    mAnalyticsManager.logEvent(analyticsScreenName, LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED);
                                });
                    }
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @NonNull
    private PermissionListener getNotificationsPermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View snackbarPermissionView,
            boolean isConnect
            ) {

        if (snackbarPermissionView == null) {
            //noinspection UnusedAssignment
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        return new NextivaPermissionListener(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)) {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.onPermissionGranted();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.POST_NOTIFICATIONS, true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.POST_NOTIFICATIONS, false);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                mAnalyticsManager.logEvent(analyticsScreenName, LOCAL_CONTACTS_PERMISSION_RATIONALE_DIALOG_SHOWN);

                if (activity.hasWindowFocus()) {
                    if (isConnect) {
                        new PermissionRationaleDialog(activity,
                                activity.getString(R.string.permission_notifications_rationale_message, activity.getString(R.string.app_name)),
                                R.drawable.ic_call_white_24dp,
                                () -> {
                                    token.continuePermissionRequest();
                                    return null;
                                }).show();

                    } else {
                        mDialogManager.showDialog(activity,
                                activity.getString(R.string.permission_required_title),
                                activity.getString(R.string.permission_notifications_rationale_message, activity.getString(R.string.app_name)),
                                activity.getString(R.string.general_ok),
                                (dialog, which) -> {
                                    token.continuePermissionRequest();
                                    mAnalyticsManager.logEvent(analyticsScreenName, POST_NOTIFICATIONS_REQUEST_PERMISSION_BUTTON_PRESSED);
                                });
                    }
                }
            }
        };
    }

    @NonNull
    private PermissionListener getAvatarCameraPermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View snackbarPermissionView) {

        if (snackbarPermissionView == null) {
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                .with(snackbarPermissionView, R.string.permission_avatar_camera_denied_message)
                .withButton(R.string.general_settings,
                            v -> {
                                mAnalyticsManager.logEvent(analyticsScreenName, AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED);
                                mIntentManager.navigateToPermissionSettings(activity);
                            })
                .build();

        return new NextivaPermissionListener(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.onPermissionGranted();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.CAMERA, true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied() && !mShouldHaveShownRationaleDialog) {
                    snackbarOnDeniedPermissionListener.onPermissionDenied(response);
                }

                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.CAMERA, false);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                mAnalyticsManager.logEvent(analyticsScreenName, AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_SHOWN);

                if (activity.hasWindowFocus()) {
                    mDialogManager.showDialog(activity,
                            activity.getString(R.string.permission_required_title),
                            activity.getString(R.string.permission_avatar_camera_rationale_message, activity.getString(R.string.app_name)),
                            activity.getString(R.string.general_ok),
                            (dialog, which) -> {
                                token.continuePermissionRequest();
                                mAnalyticsManager.logEvent(analyticsScreenName, AVATAR_CAMERA_PERMISSION_RATIONALE_DIALOG_OK_BUTTON_PRESSED);
                            });
                }
            }
        };
    }

    @NonNull
    private PermissionListener getMeetingCameraPermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View snackbarPermissionView) {

        if (snackbarPermissionView == null) {
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                .with(snackbarPermissionView, R.string.permission_video_call_camera_denied_message)
                .withButton(R.string.general_settings,
                        v -> {
                            mAnalyticsManager.logEvent(analyticsScreenName, MEETING_CAMERA_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED);
                            mIntentManager.navigateToPermissionSettings(activity);
                        })
                .build();

        return new NextivaPermissionListener(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.onPermissionGranted();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.CAMERA, true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied() && !mShouldHaveShownRationaleDialog) {
                    snackbarOnDeniedPermissionListener.onPermissionDenied(response);
                }

                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.CAMERA, false);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                mAnalyticsManager.logEvent(analyticsScreenName, MEETING_CAMERA_PERMISSION_RATIONALE_DIALOG_SHOWN);
                token.continuePermissionRequest();
            }
        };
    }

    @NonNull
    private PermissionListener getMeetingAudioPermissionListener(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View snackbarPermissionView) {

        if (snackbarPermissionView == null) {
            snackbarPermissionView = activity.findViewById(android.R.id.content);
        }

        final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                .with(snackbarPermissionView, R.string.permission_video_call_record_audio_denied_message)
                .withButton(R.string.general_settings,
                        v -> {
                            mAnalyticsManager.logEvent(analyticsScreenName, MEETING_AUDIO_PERMISSION_RATIONALE_DIALOG_SETTINGS_BUTTON_PRESSED);
                            mIntentManager.navigateToPermissionSettings(activity);
                        })
                .build();

        return new NextivaPermissionListener(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.onPermissionGranted();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.CAMERA, true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied() && !mShouldHaveShownRationaleDialog) {
                    snackbarOnDeniedPermissionListener.onPermissionDenied(response);
                }

                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.onPermissionDenied();
                }
                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.PermissionStates.RECORD_AUDIO, false);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                mAnalyticsManager.logEvent(analyticsScreenName, MEETING_AUDIO_PERMISSION_RATIONALE_DIALOG_SHOWN);
                token.continuePermissionRequest();
            }
        };
    }

    private PermissionRequestErrorListener getErrorListener(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName) {

        return error -> {
            LogUtil.log("Dexter", "There was an error: " + error.toString());
            FirebaseCrashlytics.getInstance().log("Dexter: " + "There was an error: " + error.toString());

            mDialogManager.dismissProgressDialog();

            if (error != DexterError.REQUEST_ONGOING) {
                mDialogManager.showErrorDialog(context, analyticsScreenName);
            }
        };
    }

    private PermissionRequestErrorListener getIncomingCallErrorListener(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback
            ) {

        return error -> {
            if (
                    error == DexterError.REQUEST_ONGOING &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) >= PackageManager.PERMISSION_GRANTED &&
                    analyticsScreenName.equals(INCOMING_CALL) &&
                    permissionGrantedCallback != null
            ) {
                permissionGrantedCallback.onPermissionGranted();
            } else {
                FirebaseCrashlytics.getInstance().recordException(new Exception("Dexter: " + "There was an error: " + error.toString()));
            }
        };
    }

    private String getDeniedPermisions(Context context, List<PermissionDeniedResponse> deniedResponse){
        StringBuilder permissionsList = new StringBuilder();

        for (PermissionDeniedResponse permissionDeniedResponse : deniedResponse) {
            if(permissionsList.length() > 0)
            {
                permissionsList.append(", ");
            }

            switch (permissionDeniedResponse.getPermissionName()) {
                case Manifest.permission.CAMERA:
                    permissionsList.append(context.getString(R.string.permission_notifications_type_camera));
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    permissionsList.append(context.getString(R.string.permission_notifications_type_audio));
                    break;
                case Manifest.permission.READ_CONTACTS:
                    permissionsList.append(context.getString(R.string.permission_notifications_type_contacts));
                    break;
                case Manifest.permission.CALL_PHONE:
                    permissionsList.append(context.getString(R.string.permission_notifications_type_calls));
                    break;
                case Manifest.permission.READ_PHONE_STATE:
                    permissionsList.append(context.getString(R.string.permission_notifications_type_phone_state));
                    break;
                case Manifest.permission.BLUETOOTH_CONNECT:
                    permissionsList.append(context.getString(R.string.permission_notifications_type_bluetooth));
                    break;
                case Manifest.permission.POST_NOTIFICATIONS:
                    permissionsList.append(context.getString(R.string.permission_notifications_type_notifications));
                    break;
            }

        }

        return permissionsList.toString();
    }

    // --------------------------------------------------------------------------------------------
    // PermissionManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void requestInitialPermissions(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback) {

        ArrayList<String> permissions = new ArrayList<>(Arrays.asList(Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        Dexter.withActivity(activity)
                .withPermissions(permissions)
                .withListener(getInitialPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, null, null))
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }

    @Override
    public void requestPhonePermission(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable final PermissionGrantedCallback permissionGrantedCallback) {

        requestPhonePermission(activity, analyticsScreenName, permissionGrantedCallback, null);
    }

    @Override
    public void requestPhonePermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable final View permissionSnackbarView) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(getCallPhonePermissionListener(activity, analyticsScreenName, permissionGrantedCallback, permissionSnackbarView))
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }

    @Override
    public void requestVoiceCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback) {

        requestVoiceCallPermission(activity, analyticsScreenName, permissionGrantedCallback, null, null);
    }

    @Override
    public void requestVoiceCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable View permissionSnackbarView) {

        requestVoiceCallPermission(activity, analyticsScreenName, permissionGrantedCallback, null, permissionSnackbarView);
    }

    @Override
    public void requestVoiceCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        requestVoiceCallPermission(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, null);
    }

    @Override
    public void requestVoiceCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(getVoiceCallPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, permissionSnackbarView))
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }

    @Override
    public void requestVoiceIncomingCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable View permissionSnackbarView) {

        requestVoiceIncomingCallPermission(activity, analyticsScreenName, permissionGrantedCallback, null, permissionSnackbarView);
    }

    @Override
    public void requestVoiceIncomingCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(getVoiceCallPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, permissionSnackbarView))
                .withErrorListener(getIncomingCallErrorListener(activity, analyticsScreenName,permissionGrantedCallback))
                .check();
    }

    @Override
    public void requestVideoCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        requestVideoCallPermission(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, null);
    }

    @Override
    public void requestVideoCallPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView) {

        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .withListener(getVideoCallPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, permissionSnackbarView))
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }

    @Override
    public void requestFileLoggingPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        requestFileLoggingPermission(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, null);
    }

    @Override
    public void requestFileLoggingPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(getWriteExternalStoragePermissionListener(
                        activity,
                        analyticsScreenName,
                        permissionGrantedCallback,
                        permissionDeniedCallback,
                        permissionSnackbarView,
                        R.string.permission_write_external_storage_denied_message,
                        R.string.permission_write_external_storage_rationale_message)
                )
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }

    @Override
    public void requestStorageToDownloadPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(getWriteExternalStoragePermissionListener(
                        activity,
                        analyticsScreenName,
                        permissionGrantedCallback,
                        permissionDeniedCallback,
                        null,
                        R.string.permission_write_external_storage_denied_download_message,
                        R.string.permission_write_external_storage_rationale_message_download)
                )
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }

    @Override
    public void requestContactsPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            boolean isConnect,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        requestContactsPermission(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, null, isConnect);
    }

    @Override
    public void requestContactsPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        requestContactsPermission(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, null, false);
    }

    @Override
    public void requestContactsPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView,
            boolean isConnect) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(getContactsPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, permissionSnackbarView, isConnect))
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void requestNotificationsPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView,
            boolean isConnect) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.POST_NOTIFICATIONS)
                .withListener(getNotificationsPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, permissionSnackbarView, isConnect))
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }


    @Override
    public void requestAvatarCameraPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        requestAvatarCameraPermission(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, null);
    }

    @Override
    public void requestAvatarCameraPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(getAvatarCameraPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, permissionSnackbarView))
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();
    }

    @Override
    public void requestMeetingCameraPermission(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        requestMeetingCameraPermission(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, null);
    }

    @Override
    public void requestMeetingCameraPermission(
            @NonNull Activity activity,
            @NonNull String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(getMeetingCameraPermissionListener(activity, analyticsScreenName, permissionGrantedCallback, permissionDeniedCallback, permissionSnackbarView))
                .withErrorListener(getErrorListener(activity, analyticsScreenName))
                .check();


    }

    @Override
    public void requestMeetingAudioPermission(
            @NonNull Activity activity,
            @NonNull String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback) {

        requestMeetingAudioPermission(activity,analyticsScreenName,permissionGrantedCallback,permissionDeniedCallback,null);

    }

    @Override
    public void requestMeetingAudioPermission(
            @NonNull Activity activity,
            @NonNull String analyticsScreenName,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback,
            @Nullable View permissionSnackbarView) {

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(getMeetingAudioPermissionListener(activity,analyticsScreenName,permissionGrantedCallback,permissionDeniedCallback,permissionSnackbarView))
                .withErrorListener(getErrorListener(activity,analyticsScreenName))
                .check();

    }

    @Override
    public void checkMeetingPermission(
            @NonNull Activity activity,
            @NonNull String analyticsScreenName,
            @NonNull String permission,
            int snackbarMessage,
            @Nullable PermissionGrantedCallback permissionGrantedCallback,
            @Nullable PermissionDeniedCallback permissionDeniedCallback
    ) {
        if (activity.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED){
            if(permissionGrantedCallback != null)
                permissionGrantedCallback.onPermissionGranted();
        } else{
            View snackbarPermissionView = activity.findViewById(android.R.id.content);

            final SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                    .with(snackbarPermissionView, snackbarMessage)
                    .withButton(R.string.general_settings,
                            v -> {
                                mAnalyticsManager.logEvent(analyticsScreenName, MEETING_PERMISSION_ERROR_DIALOG_SETTINGS_BUTTON_PRESSED);
                                mIntentManager.navigateToPermissionSettings(activity);
                            })
                    .build();

            PermissionDeniedResponse response = PermissionDeniedResponse.from( permission,true);
            snackbarOnDeniedPermissionListener.onPermissionDenied(response);
            if (permissionDeniedCallback != null) {
                permissionDeniedCallback.onPermissionDenied();
            }
        }
    }

    // --------------------------------------------------------------------------------------------
}
