/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.net.interceptors.UmsAuthenticator;
import com.nextiva.nextivaapp.android.net.interceptors.UmsHostInterceptor;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPChatManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPresenceManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPubSubManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPRosterManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by joedephillipo on 3/1/18.
 */


@AndroidEntryPoint
public class NextivaXMPPConnectionService extends Service {

    @Inject
    ConfigManager mConfigManager;
    @Inject
    SessionManager mSessionManager;
    @Inject
    SettingsManager mSettingsManager;
    @Inject
    LogManager mLogManager;
    @Inject
    XMPPConnectionActionManager mXmppConnectionActionManager;
    @Inject
    DbManager mDbManager;
    @Inject
    UmsRepository mUmsRepository;
    @Inject
    UmsHostInterceptor mUmsHostInterceptor;
    @Inject
    UmsAuthenticator mUmsAuthenticator;
    @Inject
    XMPPRosterManager mRosterManager;
    @Inject
    XMPPPresenceManager mPresenceManager;
    @Inject
    XMPPChatManager mChatManager;
    @Inject
    XMPPPubSubManager mPubSubManager;
    @Inject
    ConnectionStateManager mConnectionStateManager;
    @Inject
    XMPPConnectionActionManager mXMPPConnectionActionManager;
    @Inject
    SharedPreferencesManager mSharedPreferencesManager;
    @Inject
    UserRepository mUserRepository;
    @Inject
    SchedulerProvider mSchedulerProvider;

    private boolean mIsThreadActive;
    private Thread mThread;
    private Handler mThreadHandler;
    private NextivaXMPPConnection mConnection;
    private XMPPUser mXMPPUser;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, NextivaXMPPConnectionService.class);
    }

    public NextivaXMPPConnectionService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initConnection() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mConnection == null) {
            mConnection = new NextivaXMPPConnection(getApplication(),
                                                    mXMPPUser,
                                                    mCompositeDisposable,
                                                    mSessionManager,
                                                    mDbManager,
                                                    mUmsRepository,
                                                    mUmsHostInterceptor,
                                                    mUmsAuthenticator,
                                                    mRosterManager,
                                                    mPresenceManager,
                                                    mChatManager,
                                                    mPubSubManager,
                                                    mLogManager,
                                                    mConfigManager,
                                                    mConnectionStateManager,
                                                    mXMPPConnectionActionManager,
                                                    mSharedPreferencesManager,
                                                    mUserRepository,
                                                    mSchedulerProvider);
        }

        try {
            mConnection.connect();
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
        } catch (IOException | SmackException | XMPPException exception) {
            exception.printStackTrace();
            stopSelf();
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, exception.getClass().getSimpleName());
        }
    }

    private void start() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!mIsThreadActive) {
            mIsThreadActive = true;

            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(() -> {
                    Looper.prepare();
                    mThreadHandler = new Handler();
                    initConnection();
                    Looper.loop();
                });

                mThread.start();
            }
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    private void stop() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        mIsThreadActive = false;
        mThread = null;

        if (mThreadHandler != null) {
            mThreadHandler.post(() -> {
                if (mConnection != null) {
                    mXmppConnectionActionManager.disconnectConnection();
                }
            });
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mConfigManager.getMobileConfig() != null &&
                mConfigManager.getMobileConfig().getXmpp() != null &&
                mConfigManager.getMobileConfig().getXmpp().getUsername() != null &&
                !TextUtils.isEmpty(mConfigManager.getMobileConfig().getXmpp().getUsername()) &&
                mConfigManager.getMobileConfig().getXmpp().getUsername().split("@").length > 0 &&
                !TextUtils.isEmpty(mConfigManager.getMobileConfig().getXmpp().getPassword())) {

            mXMPPUser = new XMPPUser(
                    mConfigManager.getMobileConfig().getXmpp().getUsername().split("@")[0],
                    mConfigManager.getMobileConfig().getXmpp().getPassword(),
                    mConfigManager.getMobileConfig().getXmpp().getDomain(),
                    mConfigManager.getMobileConfig().getXmpp().getKeepAliveTimeOut(),
                    getString(R.string.xmpp_resource_format, getString(R.string.app_name), BuildConfig.VERSION_NAME)
            );

            start();
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
            return Service.START_STICKY;
        } else {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_FAILURE, R.string.log_message_required_data_unavailable);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mConnection != null) {
            mXmppConnectionActionManager.disconnectConnection();
        }

        mCompositeDisposable.clear();
        stop();
        super.onDestroy();
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}