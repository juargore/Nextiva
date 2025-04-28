package com.nextiva.nextivaapp.android.managers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidTest;

/**
 * Created by Thaddeus Dannar on 9/5/18.
 */

@HiltAndroidTest
public class NextivaNotificationManagerTest extends BaseRobolectricTest {

    @Inject
    protected AvatarManager mAvatarManager;

    private NextivaNotificationManager mNextivaNotificationManager;
    private NotificationManager mNotificationManager;
    private Application mApplication = ApplicationProvider.getApplicationContext();
    private LogManager mLogManager;
    @Mock
    private SessionManager mSessionManager = Mockito.mock(SessionManager.class);
    private SettingsManager mSettingsManager;
    @Mock
    private DbManager mDbManager = Mockito.mock(DbManager.class);
    @Mock
    private SharedPreferencesManager mSharedPreferencesManager = Mockito.mock(SharedPreferencesManager.class);

    private String testTitle = "Test Title";
    private String testText = "Test Text";
    private String testChannel = "Test Channel";
    private String testState = "Test State";
    private String notificationTitleKey = "android.title";
    private String notificationTextKey = "android.text";
    private String notificationsChannelCallsDescription = "Information about ongoing calls.";
    private String notificationsChannelCallsName = "Call";

    @Override
    public void setup() throws IOException {
        super.setup();
        MockitoAnnotations.initMocks(this);

        when(mSessionManager.getConnectUserPresence()).thenReturn(new DbPresence());
        when(mDbManager.getOwnConnectPresenceLiveData()).thenReturn(new MutableLiveData<>());

        mNextivaNotificationManager = new NextivaNotificationManager(mApplication, mAvatarManager, mLogManager, mSessionManager, mSettingsManager, mDbManager, mSharedPreferencesManager);
        mNotificationManager = mApplication.getSystemService(NotificationManager.class);
        mLogManager = mApplication.getSystemService(LogManager.class);
    }

    @Test
    public void setup_call_channel_description() {
        assertEquals(notificationsChannelCallsDescription, mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.CALL).getDescription());
    }

    @Test
    public void setup_call_channel_name() {
        assertEquals(notificationsChannelCallsName, mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.CALL).getName());
    }

    @Test
    public void setup_call_channel_importance() {
        assertEquals(android.app.NotificationManager.IMPORTANCE_LOW, mNotificationManager.getNotificationChannel(Enums.Notification.ChannelIDs.CALL).getImportance());
    }

    @Test
    public void simpleNotification_set_channel() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);
        assertEquals(testChannel, notification.getChannelId());
    }

    @Test
    public void simpleNotification_set_channel_with_pending_intent() {
        PendingIntent intent = PendingIntent.getActivity(mApplication, 0, new Intent(), 0);

        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel, intent);
        assertEquals(notification.getChannelId(), testChannel);
        assertEquals(notification.contentIntent, intent);
        assertEquals(testTitle, notification.extras.getString(notificationTitleKey));
        assertEquals(testText, notification.extras.getString(notificationTextKey));
    }

    @Test
    public void simpleNotification_set_channel_with_sound() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel, null, true);
        assertEquals(notification.getChannelId(), testChannel);
        assertEquals(testTitle, notification.extras.getString(notificationTitleKey));
        assertEquals(testText, notification.extras.getString(notificationTextKey));
    }

    @Test
    public void simpleNotification_set_channel_without_sound() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel, null, false);
        assertEquals(notification.getChannelId(), testChannel);
        assertEquals(testTitle, notification.extras.getString(notificationTitleKey));
        assertEquals(testText, notification.extras.getString(notificationTextKey));
    }

    @Test
    public void showNotification() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(0, notification);
        assertEquals(1, mNotificationManager.getActiveNotifications().length);
    }

    @Test
    public void showNotification_Multiple() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(0, notification);
        mNextivaNotificationManager.showNotification(1, notification);
        assertEquals(2, mNotificationManager.getActiveNotifications().length);
    }
/*

    @Test
    public void showNotification_with_custom_id() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);
        mNextivaNotificationManager.showNotification(100, Enums.Notification.ChannelIDs.SIP_TEST_ERROR, notification);
        assertEquals(1, mNotificationManager.getActiveNotifications().length);
    }

    @Test
    public void showNotification_with_custom_id_Multiple() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);
        mNextivaNotificationManager.showNotification(100, Enums.Notification.ChannelIDs.SIP_TEST_ERROR, notification);
        mNextivaNotificationManager.showNotification(101, Enums.Notification.ChannelIDs.SIP_TEST_ERROR, notification);
        assertEquals(2, mNotificationManager.getActiveNotifications().length);
    }
*/

    @Test
    public void cancelAllNotification() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(0, notification);
        mNextivaNotificationManager.showNotification(1, notification);
        assertEquals(2, mNotificationManager.getActiveNotifications().length);
        mNextivaNotificationManager.cancelAllNotifications();
        assertEquals(0, mNotificationManager.getActiveNotifications().length);
    }

    @Test
    public void cancelNotification_by_id() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(0, notification);
        mNextivaNotificationManager.showNotification(1, notification);
        assertEquals(2, mNotificationManager.getActiveNotifications().length);
        mNextivaNotificationManager.cancelNotification(1);
        assertEquals(1, mNotificationManager.getActiveNotifications().length);
    }

    @Test
    public void cancelNotification_with_wrong_id() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(0, notification);
        assertEquals(1, mNotificationManager.getActiveNotifications().length);
        mNextivaNotificationManager.cancelNotification(1);
        assertEquals(1, mNotificationManager.getActiveNotifications().length);
    }

    @Test
    public void cancelNotification_by_channel() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(testChannel, 0, notification);
        mNextivaNotificationManager.showNotification(testChannel, 1, notification);

        assertEquals(2, mNotificationManager.getActiveNotifications().length);
        mNextivaNotificationManager.cancelNotification(testChannel, 1);
        assertEquals(1, mNotificationManager.getActiveNotifications().length);
    }

    @Test
    public void cancelNotification_by_channel_without_channel() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(0, notification);
        mNextivaNotificationManager.showNotification(1, notification);

        assertEquals(2, mNotificationManager.getActiveNotifications().length);
        mNextivaNotificationManager.cancelNotification(testChannel, 1);
        assertEquals(2, mNotificationManager.getActiveNotifications().length);
    }

    @Test
    public void cancelNotification_by_channel_with_wrong_id() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(0, notification);
        mNextivaNotificationManager.showNotification(1, notification);

        assertEquals(2, mNotificationManager.getActiveNotifications().length);
        mNextivaNotificationManager.cancelNotification(testChannel, 3);
        assertEquals(2, mNotificationManager.getActiveNotifications().length);
    }

    @Test
    public void cancelNotification_by_channel_with_wrong_channel() {
        Notification notification = mNextivaNotificationManager.simpleNotification(testTitle, testText, testChannel);

        mNextivaNotificationManager.showNotification(0, notification);
        mNextivaNotificationManager.showNotification(1, notification);

        assertEquals(2, mNotificationManager.getActiveNotifications().length);
        mNextivaNotificationManager.cancelNotification("wrong channel", 0);
        assertEquals(2, mNotificationManager.getActiveNotifications().length);
    }
}