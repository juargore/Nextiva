/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.ConnectMainActivity;
import com.nextiva.nextivaapp.android.IncomingCallActivity;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.TrampolineRestrictingActivity;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.fcm.NextivaFirebaseMessagingService;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;
import com.nextiva.nextivaapp.android.models.IncomingCall;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.receivers.IncomingCallNotificationBroadcastReceiver;
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager;
import com.nextiva.nextivaapp.android.util.GsonUtil;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo;

import static android.app.Notification.CATEGORY_CALL;
import static com.nextiva.nextivaapp.android.constants.Constants.ChromeOS.CHROME_OS_DEVICE_MANAGEMENT_FEATURE;
import static com.nextiva.nextivaapp.android.constants.Enums.Notification.TypeIDs.MISSED_CALL_NOTIFICATION_ID;
import static com.nextiva.nextivaapp.android.constants.Enums.Notification.TypeIDs.NEW_CHAT_MESSAGE_NOTIFICATION_ID;
import static com.nextiva.nextivaapp.android.constants.Enums.Notification.TypeIDs.TOTAL_UNREAD;

/**
 * Created by Thaddeus Dannar on 6/11/18.
 */

@Singleton
public class NextivaNotificationManager implements NotificationManager {

    public static final String LAUNCHED_FROM_NOTIFICATION = "LAUNCHED_FROM_NOTIFICATION";
    public static final long[] VIBRATION_PATTERN_SIMPLE = {0, 500, 500};//0 to start now, 500 to vibrate 500 ms, 500 to sleep for 500 ms.
    @NonNull
    private final NotificationManagerCompat mNotificationManager;
    @NonNull
    private final android.app.NotificationManager mNotificationManagerFromService;
    @NonNull
    private final AvatarManager mAvatarManager;
    @NonNull
    private final LogManager mLogManager;
    @NonNull
    private final SessionManager mSessionManager;
    @NonNull
    private final SettingsManager mSettingsManager;
    @NonNull
    private final DbManager mDbManager;
    @NonNull
    private final SharedPreferencesManager mSharedPreferenceManager;
    LiveData<Integer> totalUnreadNotificationsLiveData;
    MediatorLiveData<Integer> totalUnreadNotificationsMediatorLiveData = new MediatorLiveData<>();
    @NonNull
    private Application mApplication;
    private int portSipServiceNotificationErrorID = 200;
    private IncomingCall mCurrentIncomingCall = null;
    private int requestCode = 1;
    private boolean isCurrentlyDoNotDisturb = false;
    Observer<Integer> totalUnreadNotificationsObserver = unread -> {
        if (mApplication.getBaseContext() != null) {
            try {
                ShortcutBadger.applyCountOrThrow(mApplication.getBaseContext(), unread);
            } catch (ShortcutBadgeException e) {
                LogUtil.e("ShortcutBadger Notification Count Error: " + e);
            }

            showNotification(mApplication,
                             Enums.Notification.ChannelIDs.CALL,
                             TOTAL_UNREAD,
                             0,
                             mApplication.getString(R.string.notification_count_message, unread),
                             mApplication.getString(R.string.notification_count_message, unread),
                             null,
                             null);
        }
    };
    private final int voiceBadgeCount = 0;
    private final int messageBadgeCount = 0;

    @Inject
    public NextivaNotificationManager(@NonNull Application application,
                                      @NonNull AvatarManager avatarManager,
                                      @NonNull LogManager logManager,
                                      @NonNull SessionManager sessionManager,
                                      @NonNull SettingsManager settingManger,
                                      @NonNull DbManager dbManager,
                                      @NonNull SharedPreferencesManager sharedPreferenceManager) {
        mApplication = application;
        mAvatarManager = avatarManager;
        mLogManager = logManager;
        mSessionManager = sessionManager;
        mSettingsManager = settingManger;
        mDbManager = dbManager;
        mSharedPreferenceManager = sharedPreferenceManager;

        mNotificationManager = NotificationManagerCompat.from(application);
        mNotificationManagerFromService = (android.app.NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);

        DbPresence connectPresence = mSessionManager.getConnectUserPresence();

        if (connectPresence != null) {
            isCurrentlyDoNotDisturb = connectPresence.getState() == Enums.Contacts.PresenceStates.CONNECT_DND;
        }

        createNotificationChannels();
        if (mApplication.getMainLooper().isCurrentThread()) {
            try {
                mDbManager.getOwnConnectPresenceLiveData().observeForever(dbSession -> {
                    if (dbSession != null && dbSession.getValue() != null && !TextUtils.isEmpty(dbSession.getValue())) {
                        DbPresence newPresence = GsonUtil.getObject(DbPresence.class, dbSession.getValue());

                        if (newPresence != null) {
                            boolean isNowDoNotDisturb = newPresence.getState() == Enums.Contacts.PresenceStates.CONNECT_DND;
                            if (isCurrentlyDoNotDisturb != isNowDoNotDisturb) {
                                isCurrentlyDoNotDisturb = isNowDoNotDisturb;
                            }
                        }
                    }
                });
            } catch (Exception exception) {
                FirebaseCrashlytics.getInstance().recordException(exception);
            }
        }

        if (totalUnreadNotificationsLiveData != null) {
            totalUnreadNotificationsMediatorLiveData.addSource(totalUnreadNotificationsLiveData, totalUnreadNotificationsObserver);
        }

    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.CALL) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_calls_name),
                                          mApplication.getString(R.string.notifications_channel_calls_description),
                                          Enums.Notification.ChannelIDs.CALL,
                                          android.app.NotificationManager.IMPORTANCE_LOW);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.CALL_QUALITY) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_call_quality_name),
                                          mApplication.getString(R.string.notifications_channel_call_quality_description),
                                          Enums.Notification.ChannelIDs.CALL_QUALITY,
                                          android.app.NotificationManager.IMPORTANCE_HIGH);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.RING_SPLASH) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_ring_splash_name),
                                          mApplication.getString(R.string.notifications_channel_ring_splash_description),
                                          Enums.Notification.ChannelIDs.RING_SPLASH,
                                          android.app.NotificationManager.IMPORTANCE_MAX);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.INCOMING_CALLS) == null) {
                createSoundFreeNotificationChannel(mApplication.getString(R.string.notifications_channel_incoming_calls_name),
                                                   mApplication.getString(R.string.notifications_channel_incoming_calls_description),
                                                   Enums.Notification.ChannelIDs.INCOMING_CALLS,
                                                   android.app.NotificationManager.IMPORTANCE_MAX);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.CHAT) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_chat_name),
                                          mApplication.getString(R.string.notifications_channel_chat_description),
                                          Enums.Notification.ChannelIDs.CHAT,
                                          android.app.NotificationManager.IMPORTANCE_HIGH);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.VOICEMAIL) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_voicemail_name),
                                          mApplication.getString(R.string.notifications_channel_voicemail_description),
                                          Enums.Notification.ChannelIDs.VOICEMAIL,
                                          android.app.NotificationManager.IMPORTANCE_HIGH);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.SMS) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_sms_name),
                                          mApplication.getString(R.string.notifications_channel_sms_description),
                                          Enums.Notification.ChannelIDs.SMS,
                                          android.app.NotificationManager.IMPORTANCE_HIGH);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.PRESENCE) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_presence_name),
                                          mApplication.getString(R.string.notifications_channel_presence_description),
                                          Enums.Notification.ChannelIDs.PRESENCE,
                                          android.app.NotificationManager.IMPORTANCE_HIGH);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.CALL_QUALITY_SILENT) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_call_quality_silent_name),
                                          mApplication.getString(R.string.notifications_channel_call_quality_description),
                                          Enums.Notification.ChannelIDs.CALL_QUALITY_SILENT,
                                          android.app.NotificationManager.IMPORTANCE_LOW);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.RING_SPLASH_SILENT) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_ring_splash_silent_name),
                                          mApplication.getString(R.string.notifications_channel_ring_splash_description),
                                          Enums.Notification.ChannelIDs.RING_SPLASH_SILENT,
                                          android.app.NotificationManager.IMPORTANCE_LOW);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.INCOMING_CALLS_SILENT) == null) {
                createSoundFreeNotificationChannel(mApplication.getString(R.string.notifications_channel_incoming_calls_silent_name),
                                                   mApplication.getString(R.string.notifications_channel_incoming_calls_description),
                                                   Enums.Notification.ChannelIDs.INCOMING_CALLS_SILENT,
                                                   android.app.NotificationManager.IMPORTANCE_LOW);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.CHAT_SILENT) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_chat_silent_name),
                                          mApplication.getString(R.string.notifications_channel_chat_description),
                                          Enums.Notification.ChannelIDs.CHAT_SILENT,
                                          android.app.NotificationManager.IMPORTANCE_LOW);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.VOICEMAIL_SILENT) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_voicemail_silent_name),
                                          mApplication.getString(R.string.notifications_channel_voicemail_description),
                                          Enums.Notification.ChannelIDs.VOICEMAIL_SILENT,
                                          android.app.NotificationManager.IMPORTANCE_LOW);
            }

            if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.SMS_SILENT) == null) {
                createNotificationChannel(mApplication.getString(R.string.notifications_channel_sms_silent_name),
                                          mApplication.getString(R.string.notifications_channel_sms_description),
                                          Enums.Notification.ChannelIDs.SMS_SILENT,
                                          android.app.NotificationManager.IMPORTANCE_LOW);
            }

            if (!TextUtils.equals(mApplication.getString(R.string.app_environment), mApplication.getString(R.string.environment_prod)) &&
                    !TextUtils.equals(mApplication.getString(R.string.app_environment), mApplication.getString(R.string.environment_prodBeta)) &&
                    !TextUtils.equals(mApplication.getString(R.string.app_environment), mApplication.getString(R.string.environment_rc))) {

                if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.SIP_TEST) == null) {
                    createNotificationChannel(mApplication.getString(R.string.notifications_channel_sip_test_name),
                                              mApplication.getString(R.string.notifications_channel_sip_test_description),
                                              Enums.Notification.ChannelIDs.SIP_TEST,
                                              android.app.NotificationManager.IMPORTANCE_LOW);
                }

                if (mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.SIP_TEST_ERROR) == null) {
                    createNotificationChannel(mApplication.getString(R.string.notifications_channel_sip_error_name),
                                              mApplication.getString(R.string.notifications_channel_sip_error_description),
                                              Enums.Notification.ChannelIDs.SIP_TEST_ERROR,
                                              android.app.NotificationManager.IMPORTANCE_LOW);
                }
            }
        }
    }

    private NotificationCompat.Builder baseNotificationBuilder(
            @NonNull String title,
            @NonNull String text,
            @NonNull @Enums.Notification.ChannelIDs.Type String channelId,
            @Nullable PendingIntent pendingIntent) {

        return baseNotificationBuilder(title, text, channelId, pendingIntent, 0);
    }

    private NotificationCompat.Builder baseNotificationBuilder(
            @NonNull String title,
            @NonNull String text,
            @NonNull @Enums.Notification.ChannelIDs.Type String channelId,
            @Nullable PendingIntent pendingIntent,
            int totalNotificationCount) {

        return new NotificationCompat
                .Builder(mApplication, channelId)
                .setSmallIcon(R.drawable.ic_notification_xbert)
                .setColor(ContextCompat.getColor(mApplication, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(text)
                .setChannelId(getChannelIdToUse(channelId))
                .setNumber(getCountMinusActiveNotificationCount(totalNotificationCount))
                .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                .setGroup(Enums.Notification.GroupIDs.GROUPING_KEY)
                .setContentIntent(pendingIntent);
    }


    private int getCountMinusActiveNotificationCount(int count) {
        int messageCount = count;
        StatusBarNotification[] statusBarNotification = mNotificationManagerFromService.getActiveNotifications();
        for (StatusBarNotification notification : statusBarNotification) {
            messageCount -= notification.getNotification().number;
        }
        return messageCount;
    }

    private void createNotificationChannel(
            String channelName,
            String channelDescription,
            @NonNull String channelId, int importance) {
        createNotificationChannel(channelName, channelDescription, channelId, importance, true);
    }

    private void createNotificationChannel(
            String channelName,
            String channelDescription,
            @NonNull String channelId, int importance,
            boolean showBadge) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            channel.setImportance(importance);

            if (!channelId.equals(Enums.Notification.ChannelIDs.CALL)) { // && !channelId.equals(Enums.Notification.ChannelIDs.SIP_TEST)) {
                channel.enableVibration(true);
                channel.enableLights(true);

            } else {
                channel.enableVibration(false);
                channel.enableLights(false);
            }

            channel.setShowBadge(showBadge);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManagerFromService.createNotificationChannel(channel);
        }
    }

    public void replaceNotificationChannelForNewSound(
            @Enums.Notification.ChannelIDs.Type @NonNull String channelId,
            @Nullable Uri soundUri) {
        createReplacementChannelForNewSound(getPresentChannelIdFor(channelId), createNewChannelId(channelId), soundUri);
    }

    ///This method is used to create a new channel with the same settings as the original channel but with a new sound
    ///Do not attempt to create a channel with the same id as a previous channel or it will automatically apply the previous versions settings
    private void createReplacementChannelForNewSound(String presentChannelId, String replacementChannelId, @Nullable Uri soundUri) {
        NotificationChannel originalChannel = mNotificationManagerFromService.getNotificationChannel(presentChannelId);

        if (originalChannel != null) {
            NotificationChannel replacementChannel = new NotificationChannel(replacementChannelId, originalChannel.getName(), originalChannel.getImportance());
            replacementChannel.setDescription(originalChannel.getDescription());
            replacementChannel.setShowBadge(originalChannel.canShowBadge());
            replacementChannel.setVibrationPattern(originalChannel.getVibrationPattern());
            replacementChannel.enableVibration(originalChannel.shouldVibrate());
            replacementChannel.enableLights(originalChannel.shouldShowLights());

            if (soundUri != null) {
                replacementChannel.setSound(soundUri, originalChannel.getAudioAttributes());
            }

            boolean isExtraChannel = true;
            String extraChannelId = "";
            while (isExtraChannel) {
                extraChannelId = getPresentChannelIdFor(presentChannelId);
                if (!extraChannelId.isEmpty()) {
                    mNotificationManagerFromService.deleteNotificationChannel(extraChannelId);
                } else {
                    isExtraChannel = false;
                }
            }

            //DO NOT ATTEMPT TO CREATE A CHANNEL WITH THE SAME ID AS A PREVIOUS CHANNEL OR IT WILL AUTOMATICALLY APPLY THE PREVIOUS VERSIONS SETTINGS
            mNotificationManagerFromService.createNotificationChannel(replacementChannel);
        } else {
            FirebaseCrashlytics.getInstance().recordException(new Exception("Original Channel Not Found: " + presentChannelId));
        }
    }

    public String getPresentChannelIdFor(@Enums.Notification.ChannelIDs.Type @NonNull String originalChannelId) {
        String originalChannelIdRemovedDigits = originalChannelId.replaceAll("[0-9]", "");
        String channelIdRemovedDigits = "";
        for (NotificationChannel channel : mNotificationManagerFromService.getNotificationChannels()) {
            channelIdRemovedDigits = channel.getId().replaceAll("[0-9]", "");
            if (channelIdRemovedDigits.equals(originalChannelIdRemovedDigits)) {
                return channel.getId();
            }
        }

        return "";
    }

    private String createNewChannelId(String originalChannelId) {
        String originalChannelIdRemovedDigits = originalChannelId.replaceAll("[0-9]", "");
        return originalChannelIdRemovedDigits + Instant.now().getEpochSecond();
    }


    private void createSoundFreeNotificationChannel(
            String channelName,
            String channelDescription,
            @NonNull String channelId, int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            channel.setImportance(importance);

            channel.setSound(null, null);
            channel.setShowBadge(true);

            if (!channelId.equals(Enums.Notification.ChannelIDs.CALL)) { // && !channelId.equals(Enums.Notification.ChannelIDs.SIP_TEST)) {
                channel.enableVibration(true);
                channel.enableLights(true);

            } else {
                channel.enableVibration(false);
                channel.enableLights(false);
            }

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            Objects.requireNonNull(mNotificationManagerFromService).createNotificationChannel(channel);
        }
    }

    // --------------------------------------------------------------------------------------------
    // Notification Manager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Notification simpleNotification(
            @NonNull String title,
            @NonNull String text,
            @NonNull @Enums.Notification.ChannelIDs.Type String channelId) {

        return simpleNotification(title, text, channelId, null, true);
    }

    @Override
    public Notification simpleNotification(
            @NonNull String title,
            @NonNull String text,
            @NonNull @Enums.Notification.ChannelIDs.Type String channelId,
            @Nullable PendingIntent pendingIntent) {

        return simpleNotification(title, text, channelId, pendingIntent, true);
    }

    @Override
    public Notification simpleNotification(
            @NonNull String title,
            @NonNull String text,
            @NonNull @Enums.Notification.ChannelIDs.Type String channelId,
            @Nullable PendingIntent pendingIntent,
            boolean hasSound) {
        return simpleNotification(title, text, channelId, pendingIntent, true, false);
    }

    @Override
    public Notification simpleNotification(
            @NonNull String title,
            @NonNull String text,
            @NonNull @Enums.Notification.ChannelIDs.Type String channelId,
            @Nullable PendingIntent pendingIntent,
            boolean hasSound,
            boolean hasVibration) {
        return simpleNotification(title, text, channelId, pendingIntent, hasSound, hasVibration, false);
    }

    @Override
    public Notification simpleNotification(
            @NonNull String title,
            @NonNull String text,
            @NonNull @Enums.Notification.ChannelIDs.Type String channelId,
            @Nullable PendingIntent pendingIntent,
            boolean hasSound,
            boolean hasVibration,
            boolean isBigNotification) {

        NotificationCompat.Builder builder = baseNotificationBuilder(title, text, channelId, pendingIntent, mSessionManager.getTotalUnreadNotificationsCount());

        if (hasSound && !isCurrentlyDoNotDisturb) {
            String notificationToneUriString = mSharedPreferenceManager.getString(SharedPreferencesManager.NOTIFICATION_TONE_URI, "");
            Uri notificationUri = (mSessionManager.isCustomToneEnabled() && !TextUtils.isEmpty(notificationToneUriString))? Uri.parse(notificationToneUriString) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (notificationUri.getScheme() == null) {
                notificationUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mApplication.getPackageName() + "/" + R.raw.notification);
            }

            if (mApplication.getPackageManager().hasSystemFeature(CHROME_OS_DEVICE_MANAGEMENT_FEATURE)) {
                Uri chromebookNotificationSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mApplication.getPackageName() + "/" + R.raw.notification);
                Ringtone ringtonePlayer = RingtoneManager.getRingtone(mApplication, chromebookNotificationSound);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ringtonePlayer.setLooping(false);
                }
                ringtonePlayer.play();
            } else {
                builder.setSound(notificationUri);
            }
        } else {
            builder.setSound(null);
        }

        if (isBigNotification) {
            builder.setStyle(new NotificationCompat.BigTextStyle()
                                     .bigText(text));
        }


        if (hasVibration && !isCurrentlyDoNotDisturb) {
            builder.setVibrate(VIBRATION_PATTERN_SIMPLE);
        } else {
            builder.setVibrate(null);
        }

        ShortcutBadger.applyNotification(mApplication, builder.build(), mSessionManager.getTotalUnreadNotificationsCount());

        return builder.build();
    }

    @Override
    public Notification callNotification(
            @NonNull String callerName,
            @NonNull String state,
            @NonNull PendingIntent pendingIntent) {

        return callNotification(null, callerName, state, pendingIntent, null);
    }

    @Override
    public Notification callNotification(
            @NonNull String callerName,
            @NonNull String state,
            @NonNull PendingIntent pendingIntent,
            @Nullable @Enums.Notification.ChannelIDs.Type String channelId) {

        return callNotification(null, callerName, state, pendingIntent, channelId);
    }

    @Override
    public Notification callNotification(
            @Nullable Bitmap avatarImage,
            @NonNull String callerName,
            @NonNull String state,
            @NonNull PendingIntent pendingIntent,
            @Nullable @Enums.Notification.ChannelIDs.Type String channelId) {

        channelId = (channelId == null) ? Enums.Notification.ChannelIDs.CALL : channelId;
        NotificationCompat.Builder builder = baseNotificationBuilder(callerName, state, channelId, pendingIntent);

        if (avatarImage != null) {
            builder.setLargeIcon(avatarImage);

        } else {
            AvatarInfo avatarInfo = new AvatarInfo.Builder()
                    .setDisplayName(callerName)
                    .build();
            builder.setLargeIcon(mAvatarManager.getBitmap(avatarInfo));
        }

        builder.setCategory(CATEGORY_CALL)
                .setSound(null)
                .setOngoing(true)
                .setVibrate(null)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        return builder.build();
    }


    @Override
    public Notification callAudioStatNotification(
            @NonNull String audioStats) {

        NotificationCompat.Builder builder = baseNotificationBuilder("Audio Stat", audioStats, Enums.Notification.ChannelIDs.CALL, null);

        builder.setSound(null)
                .setVibrate(null)
                .setStyle(new NotificationCompat.BigTextStyle()
                                  .bigText(audioStats));

        return builder.build();
    }


    @Override
    public Notification callVideoStatNotification(
            @NonNull String videoStats) {

        NotificationCompat.Builder builder = baseNotificationBuilder("Video Stat", videoStats, Enums.Notification.ChannelIDs.CALL, null);

        builder.setSound(null)
                .setVibrate(null)
                .setStyle(new NotificationCompat.BigTextStyle()
                                  .bigText(videoStats));

        return builder.build();
    }


    @Override
    public void showSIPStateNotification(@NonNull PJSipManager sipManager) {

        if (!TextUtils.equals(mApplication.getString(R.string.app_environment), mApplication.getString(R.string.environment_prod)) &&
                !TextUtils.equals(mApplication.getString(R.string.app_environment), mApplication.getString(R.string.environment_prodBeta)) &&
                !TextUtils.equals(mApplication.getString(R.string.app_environment), mApplication.getString(R.string.environment_rc))) {
            if (mSettingsManager.getDisplaySIPState()) {
                String sipStateString = "";

                sipStateString += "Push registered: " + !TextUtils.isEmpty(mSessionManager.getPushNotificationRegistrationId());
                //sipStateString += "\nSIP registered: " + sipManager.isRegistered();
                sipStateString += "\nSIP has call active: " + sipManager.isCallActive();


                NotificationCompat.Builder builder = baseNotificationBuilder("SIP State", sipStateString, Enums.Notification.ChannelIDs.SIP_TEST, null);

                builder.setSound(null)
                        .setVibrate(null)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                          .bigText(sipStateString));

                showNotification(Enums.Notification.TypeIDs.SIP_TEST,
                                 Enums.Notification.ChannelIDs.SIP_TEST,
                                 builder.build());
            }
        }
    }

    @Override
    public Notification sipErrorNotification(
            @NonNull String errorMessage) {

        NotificationCompat.Builder builder = baseNotificationBuilder("Sip Error", errorMessage, Enums.Notification.ChannelIDs.SIP_TEST_ERROR, null);

        builder.setSound(null)
                .setVibrate(null)
                .setGroup(Enums.Notification.GroupIDs.SIP_ERRORS);

        return builder.build();
    }


    @Override
    public void showNotification(
            @Enums.Notification.TypeIDs.Type int notificationId,
            @Nullable Notification notification) {

        if (notification != null) {
            mNotificationManager.notify(notificationId, notification);
        }
    }

    @Override
    public void showNotification(
            @Enums.Notification.ChannelIDs.Type String channelId,
            @Enums.Notification.TypeIDs.Type int notificationId,
            @NonNull Notification notification) {
        mNotificationManager.notify(channelId, notificationId, notification);
    }

    @Override
    public void showNotification(
            int notificationId,
            @Enums.Notification.ChannelIDs.Type String channelId,
            @NonNull Notification notification) {
        mNotificationManager.notify(channelId, notificationId, notification);
    }

    @Override
    public void showNotification(
            Context context,
            String channelId,
            int notificationId,
            int requestCode,
            String body,
            String title,
            Intent intent,
            @Nullable Bitmap icon) {
        showNotification(context, channelId, notificationId, requestCode, body, title, intent, null, icon);
    }

    @Override
    public void showNotification(
            Context context,
            String channelId,
            String category,
            int notificationId,
            int requestCode,
            String body,
            String title,
            Intent intent,
            @Nullable Bitmap icon) {
        showNotification(context, channelId, category, notificationId, null, requestCode, body, title, intent, null, icon);
    }

    @Override
    public void showNotification(
            Context context,
            String channelId,
            int notificationId,
            int requestCode,
            String body,
            String title,
            Intent intent,
            String extras,
            @Nullable Bitmap icon) {
        showNotification(context, channelId, null, notificationId, null, requestCode, body, title, intent, extras, icon, 0);

    }

    @Override
    public void showNotification(
            Context context,
            String channelId,
            String category,
            int notificationId,
            @Nullable String notificationTag,
            int requestCode,
            String body,
            String title,
            Intent intent,
            String extras,
            @Nullable Bitmap icon) {
        showNotification(context, channelId, category, notificationId, notificationTag, requestCode, body, title, intent, extras, icon, 0);
    }

    @Override
    public void showNotification(
            Context context,
            String channelId,
            String category,
            int notificationId,
            @Nullable String notificationTag,
            int requestCode,
            String body,
            String title,
            Intent intent,
            String extras,
            @Nullable Bitmap icon,
            int totalIconBadgeCount) {
        try {
            if (intent != null) {
                intent.putExtra(LAUNCHED_FROM_NOTIFICATION, true);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            String ringtoneUriString = mSharedPreferenceManager.getString(SharedPreferencesManager.NOTIFICATION_TONE_URI, "");
            Uri notificationUri = (mSessionManager.isCustomToneEnabled() && !ringtoneUriString.isEmpty())? Uri.parse(ringtoneUriString) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (notificationUri.getScheme() == null) {
                notificationUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mApplication.getPackageName() + "/" + R.raw.notification);
            }

            if (!isCurrentlyDoNotDisturb) {
                if (mApplication.getPackageManager().hasSystemFeature(CHROME_OS_DEVICE_MANAGEMENT_FEATURE)) {
                    notificationUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mApplication.getPackageName() + "/" + R.raw.notification);

                    Ringtone ringtonePlayer = RingtoneManager.getRingtone(mApplication, notificationUri);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ringtonePlayer.setLooping(false);
                    }

                    //maybe
                    ringtonePlayer.setStreamType(AudioManager.STREAM_RING);

                    ringtonePlayer.play();
                }
            }

            Bundle bundle = new Bundle();
            bundle.putString(Constants.EXTRA_MESSAGE_ID, extras);

            NotificationCompat.Builder notificationBuilder = baseNotificationBuilder(
                    title,
                    body,
                    getChannelIdToUse(channelId),
                    pendingIntent,
                    mSessionManager.getTotalUnreadNotificationsCount());


            notificationBuilder
                    .setExtras(bundle)
                    .setLargeIcon(icon)
                    .setAutoCancel(true);

            if (!isCurrentlyDoNotDisturb) {
                notificationBuilder.setSound(notificationUri, AudioManager.STREAM_NOTIFICATION);

            } else {
                notificationBuilder.setSound(null);
            }

            if (category != null && !category.isEmpty()) {
                notificationBuilder.setCategory(category);
            }

            ShortcutBadger.applyNotification(mApplication, notificationBuilder.build(), mSessionManager.getTotalUnreadNotificationsCount());
            mNotificationManager.cancel(notificationId);
            mNotificationManager.notify(notificationTag, notificationId, notificationBuilder.build());
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private String getChannelIdToUse(String channelId) {
        if (isCurrentlyDoNotDisturb) {
            switch (channelId) {
                case Enums.Notification.ChannelIDs.CALL_QUALITY:
                    return Enums.Notification.ChannelIDs.CALL_QUALITY_SILENT;
                case Enums.Notification.ChannelIDs.RING_SPLASH:
                    return Enums.Notification.ChannelIDs.RING_SPLASH_SILENT;
                case Enums.Notification.ChannelIDs.INCOMING_CALLS:
                    return Enums.Notification.ChannelIDs.INCOMING_CALLS_SILENT;
                case Enums.Notification.ChannelIDs.CHAT:
                    return Enums.Notification.ChannelIDs.CHAT_SILENT;
                case Enums.Notification.ChannelIDs.VOICEMAIL:
                    return Enums.Notification.ChannelIDs.VOICEMAIL_SILENT;
                case Enums.Notification.ChannelIDs.SMS:
                    return Enums.Notification.ChannelIDs.SMS_SILENT;
            }
        }

        return channelId;
    }

    @Override
    public void cancelAllNotifications() {
        mNotificationManager.cancelAll();
    }

    @Override
    public void cancelNotification(@Enums.Notification.TypeIDs.Type int notificationId) {
        mNotificationManager.cancel(notificationId);
    }

    @Override
    public void cancelNotificationByTag(String notificationTag, int notificationId) {
        mNotificationManager.cancel(notificationTag, notificationId);
    }

    @Override
    public void cancelChatNotification(int notificationId, String messageId) {
        if (mNotificationManagerFromService != null) {
            StatusBarNotification[] barNotifications = mNotificationManagerFromService.getActiveNotifications();
            for (StatusBarNotification notification : barNotifications) {
                if (notification.getId() == notificationId && notification.getNotification().extras.containsKey(Constants.EXTRA_MESSAGE_ID) &&
                        TextUtils.equals(notification.getNotification().extras.getString(Constants.EXTRA_MESSAGE_ID), messageId)) {
                    cancelNotification(notificationId);
                }
            }
        }
    }

    @Override
    public void cancelChatNotificationsFromMessageIds(ArrayList<String> messageIds) {
        if (mNotificationManagerFromService != null && mNotificationManagerFromService.getActiveNotifications() != null && messageIds != null) {
            StatusBarNotification[] barNotifications = mNotificationManagerFromService.getActiveNotifications();
            for (StatusBarNotification notification : barNotifications) {
                for (String messageId : messageIds) {
                    if (notification.getId() == NEW_CHAT_MESSAGE_NOTIFICATION_ID && notification.getNotification().extras.containsKey(Constants.EXTRA_MESSAGE_ID) &&
                            TextUtils.equals(notification.getNotification().extras.getString(Constants.EXTRA_MESSAGE_ID), messageId)) {
                        cancelNotification(NEW_CHAT_MESSAGE_NOTIFICATION_ID);
                    }
                }
            }
        }
    }

    @Override
    public void cancelNotification(
            @Enums.Notification.ChannelIDs.Type String channel,
            @Enums.Notification.TypeIDs.Type int notificationId) {

        mNotificationManager.cancel(channel, notificationId);
    }

    @Override
    public int getPortSipServiceNotificationErrorID() {
        if (portSipServiceNotificationErrorID > 299) {
            portSipServiceNotificationErrorID = 200;
        }

        return portSipServiceNotificationErrorID++;
    }

    @Override
    public void showMissedCallNotification(RxEvents.CallUpdatedEvent event, Context context) {
        if (mCurrentIncomingCall != null &&
                ((mCurrentIncomingCall.getPushNotificationCallInfo() != null && mCurrentIncomingCall.getPushNotificationCallInfo().getCallId() != null &&
                        TextUtils.equals(mCurrentIncomingCall.getPushNotificationCallInfo().getCallId(), event.getCallId())) ||
                        (!TextUtils.isEmpty(mCurrentIncomingCall.getSipMessage()) &&
                                mCurrentIncomingCall.getSipMessage().contains(event.getCallId())))) {

            if (event.getCallWasMissed()) {
                String displayName = context.getString(R.string.push_notification_missed_call_anonymous);
                ParticipantInfo participantInfo = null;

                if (mCurrentIncomingCall.getPushNotificationCallInfo() != null) {
                    participantInfo = mCurrentIncomingCall.getPushNotificationCallInfo().getParticipantInfo();
                }

                if (participantInfo != null) {
                    if (!TextUtils.isEmpty(participantInfo.getDisplayName())) {
                        displayName = participantInfo.getDisplayName();
                    } else if (!TextUtils.isEmpty(participantInfo.getNumberToCall())) {
                        displayName = participantInfo.getNumberToCall();
                    }
                }

                showNotification(context.getApplicationContext(),
                                 Enums.Notification.ChannelIDs.CALL,
                                 NotificationCompat.CATEGORY_MISSED_CALL,
                                 MISSED_CALL_NOTIFICATION_ID,
                                 NextivaFirebaseMessagingService.REQUEST_CODE,
                                 context.getString(R.string.push_notification_missed_call_body, displayName),
                                 context.getString(R.string.push_notification_missed_call_title),
                                 new Intent(ConnectMainActivity.newIntent(context, Enums.Platform.ViewsToShow.CALLS_MISSED)),
                                 null);
            }
        }

        mCurrentIncomingCall = null;
    }

    @Override
    public Notification getIncomingCallNotification(Service service, IncomingCall incomingCall) {
        mLogManager.sipLogToFile(Enums.Logging.STATE_INFO, "Incoming Call: " + GsonUtil.getJSON(incomingCall));
        if (incomingCall == null) {
            return null;
        }

        String displayName = null;
        NextivaContact contact = null;
        String phoneNumber = null;
        ParticipantInfo participantInfo = null;

        if (incomingCall.getPushNotificationCallInfo() != null) {
            participantInfo = incomingCall.getPushNotificationCallInfo().getParticipantInfo();
        } else {
            participantInfo = incomingCall.getParticipantInfo();
        }

        if (participantInfo != null && !TextUtils.isEmpty(participantInfo.getNumberToCall())) {
            contact = mDbManager.getConnectContactFromPhoneNumberInThread(participantInfo.getNumberToCall()).getValue();
        }

        if(participantInfo != null && participantInfo.getDisplayName() != null && !TextUtils.isEmpty(participantInfo.getDisplayName())){
            displayName = participantInfo.getDisplayName();
        } else if (contact != null && contact.getDisplayName() != null && !contact.getDisplayName().isEmpty()) {
            displayName = contact.getDisplayName();
        }

        if(participantInfo != null && participantInfo.getNumberToCall() != null && !TextUtils.isEmpty(participantInfo.getNumberToCall())){
            phoneNumber = participantInfo.getNumberToCall();
        }

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(service, getNewRequestCode(), incomingCallIntent(incomingCall), PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AvatarInfo avatarInfo = new AvatarInfo.Builder()
                .setIconResId(R.drawable.avatar_group)
                .setDisplayName(displayName)
                .build();


        NotificationCompat.Builder notificationBuilder =
                baseNotificationBuilder(
                        displayName,
                        phoneNumber,
                        Enums.Notification.ChannelIDs.INCOMING_CALLS,
                        fullScreenPendingIntent);


        notificationBuilder
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setLargeIcon(mAvatarManager.getBitmap(avatarInfo))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setSound(null)
                .setVibrate(null)
                .setColorized(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setAutoCancel(true);


        if (ActivityCompat.checkSelfPermission(mApplication.getBaseContext(), Manifest.permission.RECORD_AUDIO) >= PackageManager.PERMISSION_GRANTED) {
            notificationBuilder.addAction(addAction(R.drawable.ic_phone, R.string.incoming_call_notification_answer, R.color.nextivaGreen, answerAudioCallPendingIntent(incomingCall)));
            notificationBuilder.addAction(addAction(R.drawable.ic_call_end, R.string.incoming_call_notification_decline, R.color.nextivaRed, declineCallPendingIntent(incomingCall, service)));
        }

        NotificationManagerCompat compat = NotificationManagerCompat.from(mApplication);
        compat.notify(Enums.Notification.TypeIDs.INCOMING_CALL, notificationBuilder.build());

        mCurrentIncomingCall = incomingCall;
        return notificationBuilder.build();
    }

    private NotificationCompat.Action addAction(@DrawableRes int icon, @StringRes int actionText, @ColorRes int actionTextColor, PendingIntent pendingIntent) {
        return new NotificationCompat.Action.Builder(icon, getActionText(actionText, actionTextColor), pendingIntent).build();
    }

    private Spannable getActionText(@StringRes int stringRes, @ColorRes int colorRes) {
        Spannable spannable = new SpannableString(mApplication.getText(stringRes));
        spannable.setSpan(new ForegroundColorSpan(mApplication.getColor(colorRes)), 0, spannable.length(), 0);
        return spannable;
    }


    private Spannable getActionBackground(@StringRes int stringRes, @ColorRes int colorRes) {
        Spannable spannable = new SpannableString(mApplication.getText(stringRes));
        spannable.setSpan(new BackgroundColorSpan(mApplication.getColor(colorRes)), 0, spannable.length(), 0);
        return spannable;
    }

    private Intent declineCallIntent(IncomingCall incomingCall) {
        mLogManager.sipLogToFile(Enums.Logging.STATE_INFO, "Decline Call: " + GsonUtil.getJSON(incomingCall));
        return IncomingCallNotificationBroadcastReceiver.newIntent(mApplication, incomingCall)
                .setAction(IncomingCallNotificationBroadcastReceiver.DECLINE_CALL);

    }

    private PendingIntent declineCallPendingIntent(IncomingCall incomingCall, Service service) {
        return declinePendingIntent(declineCallIntent(incomingCall), service);
    }

    private PendingIntent declinePendingIntent(Intent intent, Service service) {
        return PendingIntent.getBroadcast(service, getNewRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private Intent answerAudioCallIntent(IncomingCall incomingCall) {
        mLogManager.sipLogToFile(Enums.Logging.STATE_INFO, "Answer Incoming Audio Call: " + GsonUtil.getJSON(incomingCall));
        return TrampolineRestrictingActivity.Companion.newIntent(mApplication, incomingCall, IncomingCallNotificationBroadcastReceiver.ANSWER_AUDIO_CALL);
    }

    private PendingIntent answerAudioCallPendingIntent(IncomingCall incomingCall) {
        LogUtil.d("Answer Incoming Audio Call Pending: " + GsonUtil.getJSON(incomingCall));
        mLogManager.sipLogToFile(Enums.Logging.STATE_INFO, "Answer Incoming Audio Call Pending: " + GsonUtil.getJSON(incomingCall));
        return answerAudioPendingIntent(answerAudioCallIntent(incomingCall));
    }

    private PendingIntent answerAudioPendingIntent(Intent intent) {
        return PendingIntent.getActivity(mApplication, getNewRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private Intent incomingCallIntent(IncomingCall incomingCall) {
        mLogManager.sipLogToFile(Enums.Logging.STATE_INFO, "Incoming Call Intent: " + GsonUtil.getJSON(incomingCall));
        ComponentName comp = new ComponentName(mApplication, IncomingCallActivity.class.getName());
        return IncomingCallActivity.newIntent(mApplication, incomingCall).setComponent(comp);

    }

    private int getNewRequestCode() {
        return requestCode++;
    }

    // --------------------------------------------------------------------------------------------
}
