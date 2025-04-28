/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.IncomingCall;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager;

import java.util.ArrayList;

/**
 * <p>
 * The Notification Manager should be used for all notifications displayed in the notification shade.
 * </p>
 * <p>
 * This class should be <b>injected</b>.
 * </p>
 * Created by Thaddeus Dannar on 6/11/18.
 */
public interface NotificationManager {

    /**
     * <p>
     * This will create a new notification channel for the given channel id.
     * </p>
     *
     * @param channelId The <b>NotificationChannelId</b> to create.
     * @see Enums.Notification.ChannelIDs
     */
    void replaceNotificationChannelForNewSound(@Enums.Notification.ChannelIDs.Type @NonNull String channelId, @Nullable Uri soundUri);

    /**
     * <p>
     * This will get the correct notification channel for the given enum channel id.
     * Some Channel Ids such as <b>CHAT</b> and <b>SMS</b> require the ablity to replace the original channel to get the present channel ids for these please use {@link #getPresentChannelIdFor(String)}.
     * </p>
     *
     * @param channelId The <b>NotificationChannelId</b> to create.
     * @see Enums.Notification.ChannelIDs
     */
    String getPresentChannelIdFor(@Enums.Notification.ChannelIDs.Type @NonNull String originalChannelId);

    /**
     * <p>
     * This will generate a basic notification with sound and vibration enabled.
     * </p>
     *
     * @param title     Title on the notification.
     * @param text      Main text of the notification.
     * @param channelId The <b>NotificationChannelId</b> this notification belongs too. @See Enums.Notification.NotificationChannelId
     * @return A notification object that can be modified or displayed with showNotification.
     */
    Notification simpleNotification(@NonNull String title, @NonNull String text, @NonNull @Enums.Notification.ChannelIDs.Type String channelId);

    /**
     * <p>
     * This will generate a basic notification with sound and vibration enabled.
     * </p>
     *
     * @param title         Title on the notification.
     * @param text          Main text of the notification.
     * @param channelId     The <b>NotificationChannelId</b> this notification belongs too. @See Enums.Notification.NotificationChannelId
     * @param pendingIntent The pending intent to return to when the notification is selected. Set to <b>Null</b> to make it non-selectable.
     * @return A notification object that can be modified or displayed with showNotification.
     */
    Notification simpleNotification(@NonNull String title, @NonNull String text, @NonNull @Enums.Notification.ChannelIDs.Type String channelId, @Nullable PendingIntent pendingIntent);

    /**
     * <p>
     * This will generate a basic notification with sound and vibration enabled.
     * </p>
     *
     * @param title         Title on the notification.
     * @param text          Main text of the notification.
     * @param channelId     The <b>NotificationChannelId</b> this notification belongs too. @See Enums.Notification.NotificationChannelId
     * @param pendingIntent The pending intent to return to when the notification is selected. Set to <b>Null</b> to make it non-selectable.
     * @param hasSound      By default sound is set to <B>true</B>.
     * @return A notification object that can be modified or displayed with showNotification.
     */
    Notification simpleNotification(@NonNull String title, @NonNull String text, @NonNull @Enums.Notification.ChannelIDs.Type String channelId, @Nullable PendingIntent pendingIntent, boolean hasSound);


    /**
     * <p>
     * This will generate a basic notification with sound and vibration enabled.
     * </p>
     *
     * @param title         Title on the notification.
     * @param text          Main text of the notification.
     * @param channelId     The <b>NotificationChannelId</b> this notification belongs too. @See Enums.Notification.NotificationChannelId
     * @param pendingIntent The pending intent to return to when the notification is selected. Set to <b>Null</b> to make it non-selectable.
     * @param hasSound      By default sound is set to <B>true</B>.
     * @param isBigNotification    If your text needs more room to display set to <B>true</B> to make an expanding notification.
     * @return A notification object that can be modified or displayed with showNotification.
     */
    Notification simpleNotification(@NonNull String title, @NonNull String text, @NonNull @Enums.Notification.ChannelIDs.Type String channelId, @Nullable PendingIntent pendingIntent, boolean hasSound, boolean isBigNotification);


    /**
     * <p>
     * This will generate a basic notification with sound and vibration enabled.
     * </p>
     *
     * @param title         Title on the notification.
     * @param text          Main text of the notification.
     * @param channelId     The <b>NotificationChannelId</b> this notification belongs too. @See Enums.Notification.NotificationChannelId
     * @param pendingIntent The pending intent to return to when the notification is selected. Set to <b>Null</b> to make it non-selectable.
     * @param hasSound      By default sound is set to <B>true</B>.
     * @param hasVibration      By default vibration is set to sound value.
     * @param isBigNotification    If your text needs more room to display set to <B>true</B> to make an expanding notification.
     * @return A notification object that can be modified or displayed with showNotification.
     */
    Notification simpleNotification(@NonNull String title, @NonNull String text, @NonNull @Enums.Notification.ChannelIDs.Type String channelId, @Nullable PendingIntent pendingIntent, boolean hasSound, boolean hasVibration, boolean isBigNotification);


    /**
     * <p>
     * This will generate a ongoing call notification.
     * </p>
     *
     * @param callerName    Callers name on the notification.
     * @param state         Present state of the call
     * @param pendingIntent The pending intent to return to when the notification is selected. Set to <b>Null</b> to make it non-selectable.
     * @return A notification object that can be modified or displayed with showNotification.
     */
    Notification callNotification(@NonNull String callerName, @NonNull String state, PendingIntent pendingIntent);

    /**
     * <p>
     * This will generate a ongoing call notification.
     * </p>
     *
     * @param pendingIntent The intent to return to when the notification is selected.
     * @param channelId     The <b>NotificationChannelId</b> this notification belongs too.
     * @return A notification object that can be modified or displayed with showNotification.
     * @see Enums.Notification.ChannelIDs
     */
    Notification callNotification(@NonNull String callerName, @NonNull String state, PendingIntent pendingIntent, @Nullable @Enums.Notification.ChannelIDs.Type String channelId);

    /**
     * <p>
     * This will generate a ongoing call notification.
     * </p>
     *
     * @param avatarImage   Image to display in the notification.
     * @param pendingIntent The intent to return to when the notification is selected.
     * @param channelId     The <b>NotificationChannelId</b> this notification belongs too.
     * @return A notification object that can be modified or displayed with showNotification.
     * @see Enums.Notification.ChannelIDs
     */
    Notification callNotification(@Nullable Bitmap avatarImage, @NonNull String callerName, @NonNull String state, PendingIntent pendingIntent, @Nullable @Enums.Notification.ChannelIDs.Type String channelId);

    /**
     * <p>
     * Pass audio stats in a string to put in a notification containing audio stats for the call.
     * </p>
     *
     * @param audioStats The audio stats to display.
     * @return A notification object that can be modified or displayed with showNotification.
     * @see Enums.Notification.ChannelIDs
     */
    Notification callAudioStatNotification(@NonNull String audioStats);

    /**
     * <p>
     * Pass video stats in a string to put in a notification containing video stats for the call.
     * </p>
     *
     * @param videoStats The audio stats to display.
     * @return A notification object that can be modified or displayed with showNotification.
     * @see Enums.Notification.ChannelIDs
     */
    Notification callVideoStatNotification(@NonNull String videoStats);


    /**
     * <p>
     * Show sip state for push, sip registration, if a call is active.
     * </p>
     *
     * @return A notification object that can be modified or displayed with showNotification.
     * @see Enums.Notification.ChannelIDs
     */
    void showSIPStateNotification(@NonNull PJSipManager sipManager);

    /**
     * <p>
     * Pass SIP Error information in string to put in a notification.
     * </p>
     *
     * @param errorMessage SIP Error Message.
     * @return A notification object that can be modified or displayed with showNotification.
     * @see Enums.Notification.ChannelIDs
     */
    Notification sipErrorNotification(@NonNull String errorMessage);
    /**
     * <p>
     * This will display the notification in the notification shade.
     * </p>
     *
     * @param notificationId The <b>NotificationTypeId</b> of the notification to display.
     * @param notification   The notification object to display.
     * @see Enums.Notification.ChannelIDs
     */
    void showNotification(@Enums.Notification.TypeIDs.Type int notificationId, @Nullable Notification notification);

    /**
     * <p>
     * This will display the notification in the notification shade.
     * </p>
     *
     * @param channelId      The <b>NotificationChannelId</b> this notification belongs too.
     * @param notificationId The <b>NotificationTypeId</b> of the notification to display.
     * @param notification   The notification object to display.
     * @see Enums.Notification.ChannelIDs
     */
    void showNotification(@Nullable @Enums.Notification.ChannelIDs.Type String channelId, @Enums.Notification.TypeIDs.Type int notificationId, Notification notification);


    /**
     * <p>
     * This will display the notification in the notification shade.
     * </p>
     *
     * @param channelId      The <b>NotificationChannelId</b> this notification belongs too.
     * @param notificationId The <b>Notification Id</b> of the notification to display.
     * @param notification   The notification object to display.
     * @see Enums.Notification.ChannelIDs
     */
    void showNotification(int notificationId, @Nullable @Enums.Notification.ChannelIDs.Type String channelId, Notification notification);

    /**
     * <p>
     * This will display the notification in the notification shade.
     * </p>
     *
     * @param context        Used to get color for notification
     * @param channelId      The <b>NotificationChannelId</b> this notification belongs too.
     * @param title          Title on the notification.
     * @param body           Main text of the notification.
     * @param notificationId The <b>Notification Id</b> of the notification to display.
     * @param requestCode    Request Code used for the Pending Intent
     * @param intent         Intent used to create Pending Intent for Notification
     * @param icon           The icon shown on the notification
     * @see Enums.Notification.ChannelIDs
     */
    void showNotification(Context context, String channelId, int notificationId, int requestCode, String body, String title, Intent intent, @Nullable Bitmap icon);

    /**
     * <p>
     * This will display the notification in the notification shade.
     * </p>
     *
     * @param context        Used to get color for notification
     * @param channelId      The <b>NotificationChannelId</b> this notification belongs too.
     * @param category      The <b>NotificationCompat</b> category this notification belongs too.
     * @param title          Title on the notification.
     * @param body           Main text of the notification.
     * @param notificationId The <b>Notification Id</b> of the notification to display.
     * @param requestCode    Request Code used for the Pending Intent
     * @param intent         Intent used to create Pending Intent for Notification
     * @param icon           The icon shown on the notification
     * @see Enums.Notification.ChannelIDs
     */
    void showNotification(Context context, String channelId, String category, int notificationId, int requestCode, String body, String title, Intent intent, @Nullable Bitmap icon);

    /**
     * <p>
     * This will display the notification in the notification shade.
     * </p>
     *
     * @param context        Used to get color for notification
     * @param channelId      The <b>NotificationChannelId</b> this notification belongs too.
     * @param title          Title on the notification.
     * @param body           Main text of the notification.
     * @param notificationId The <b>Notification Id</b> of the notification to display.
     * @param requestCode    Request Code used for the Pending Intent
     * @param intent         Intent used to create Pending Intent for Notification
     * @param extras         Information to further identify the notification
     * @param icon           The icon shown on the notification
     * @see Enums.Notification.ChannelIDs
     */
    void showNotification(Context context, String channelId, int notificationId, int requestCode, String body, String title, Intent intent, String extras, @Nullable Bitmap icon);

    /**
     * <p>
     * This will display the notification in the notification shade.
     * </p>
     *
     * @param context         Used to get color for notification
     * @param channelId       The <b>NotificationChannelId</b> this notification belongs too.
     * @param category        The <b>NotificationCompat</b> category this notification belongs too.
     * @param title           Title on the notification.
     * @param body            Main text of the notification.
     * @param notificationId  The <b>Notification Id</b> of the notification to display.
     * @param notificationTag The Firebase GUID that comes from notification.
     * @param requestCode     Request Code used for the Pending Intent
     * @param intent          Intent used to create Pending Intent for Notification
     * @param extras          Information to further identify the notification
     * @param icon            The icon shown on the notification
     * @see Enums.Notification.ChannelIDs
     */
    void showNotification(Context context, String channelId, String category, int notificationId, @Nullable String notificationTag, int requestCode, String body, String title, Intent intent, String extras, @Nullable Bitmap icon);

    /**
     * <p>
     * This will display the notification in the notification shade.
     * </p>
     *
     * @param context         Used to get color for notification
     * @param channelId       The <b>NotificationChannelId</b> this notification belongs too.
     * @param category        The <b>NotificationCompat</b> category this notification belongs too.
     * @param title           Title on the notification.
     * @param body            Main text of the notification.
     * @param notificationId  The <b>Notification Id</b> of the notification to display.
     * @param notificationTag The Firebase GUID that comes from notification.
     * @param requestCode     Request Code used for the Pending Intent
     * @param intent          Intent used to create Pending Intent for Notification
     * @param extras          Information to further identify the notification
     * @param icon            The icon shown on the notification
     * @param increaseIconBadgeCountAmount  If the device supports icon badge counts by number of notifications this will increase the count on the badge by this amount
     * @see Enums.Notification.ChannelIDs
     */
    void showNotification(Context context, String channelId, String category, int notificationId, @Nullable String notificationTag, int requestCode, String body, String title, Intent intent, String extras, @Nullable Bitmap icon, int increaseIconBadgeCountAmount);


    /**
     * <p>
     * This will show the incoming call notification.
     * </p>
     *
     * @param service      The service receiving the incoming call.
     * @param incomingCall The incoming call object to display.
     */
    Notification getIncomingCallNotification(Service service, IncomingCall incomingCall);


    /**
     * <p>
     * This will remove <b>all</b> ongoing notifications in the notification shade.
     * </p>
     */
    void cancelAllNotifications();

    /**
     * <p>
     * This will remove a specific ongoing notifications in the notification shade.
     * </p>
     *
     * @param notificationId The id of the notification to remove.
     */
    void cancelNotification(@Enums.Notification.TypeIDs.Type int notificationId);

    /**
     * <p>
     * This will remove a specific ongoing notifications in the notification shade.
     * </p>
     *
     * @param notificationId The id of the notification to remove.
     * @param notificationTag The string notification tag from Firebase GUID.
     */
    void cancelNotificationByTag(String notificationTag, @Enums.Notification.TypeIDs.Type int notificationId);

    /**
     * <p>
     * This will remove a specific ongoing notification if there is a chat message notification showing and
     * that chat message has now been read.
     * </p>
     *
     * @param notificationId The id of the notification to remove.
     * @param messageId      The id of the message read
     */
    void cancelChatNotification(int notificationId, String messageId);

    void cancelChatNotificationsFromMessageIds(ArrayList<String> messageIds);

    /**
     * <p>
     * This will remove a specific ongoing notifications in the notification shade.
     * </p>
     *
     * @param channel        The channel of the notification to remove.
     * @param notificationId The id of the notification to remove.
     */
    void cancelNotification(@Nullable @Enums.Notification.ChannelIDs.Type String channel, @Enums.Notification.TypeIDs.Type int notificationId);


    /**
     * <p>
     * NOT FOR PRODUCTION. This will return a new ID for Port Sip Service Error each time it's called.
     * </p>
     *
     * @return A notification ID for Port Sip Service Error.
     */
    int getPortSipServiceNotificationErrorID();

    void showMissedCallNotification(RxEvents.CallUpdatedEvent event, Context context);
}
