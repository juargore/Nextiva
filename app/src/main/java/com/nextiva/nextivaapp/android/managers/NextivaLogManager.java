/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbLogging;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.net.platform.featureFlags.FeatureFlag;
import com.nextiva.nextivaapp.android.models.net.platform.featureFlags.FeatureFlags;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import io.reactivex.disposables.CompositeDisposable;

@Singleton
public class NextivaLogManager implements LogManager {

    private final int MINIMUM_LOGS_TO_POST = 250;


    private final Application mApplication;
    private final SettingsManager mSettingsManager;
    private final Long mLogAPIBufferTimer = Constants.ONE_SECOND_IN_MILLIS * 10;
    private Logger mLogger = LoggerFactory.getLogger(NextivaLogManager.class);
    private DbManager mDbManager;
    private CompositeDisposable mCompositeDisposable;
    private PlatformRepository mPlatformRepository;
    private ConnectionStateManager mConnectionStateManager;
    private SessionManager mSessionManager;
    private Long mLogAPILastPostTime = 0L;
    private FeatureFlags mFeatureFlags = null;


    @Inject
    public NextivaLogManager(@NonNull Application application, @NonNull SettingsManager settingsManager) {
        mApplication = application;
        mSettingsManager = settingsManager;

    }

    // --------------------------------------------------------------------------------------------
    // LogManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void setupLogger() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        ContextInitializer ci = new ContextInitializer(loggerContext);
        try {
            ci.autoConfig();
        } catch (JoranException e) {
            e.printStackTrace();
        }

        mLogger = LoggerFactory.getLogger(NextivaLogManager.class);
        logToFile(Enums.Logging.STATE_INFO, R.string.log_message_logging_enabled);
    }

    @Override
    public void addDBManager(@NonNull DbManager dbManager) {
        mDbManager = dbManager;
    }

    @Override
    public void addCompositeDisposable(@NonNull CompositeDisposable compositeDisposable) {
        mCompositeDisposable = compositeDisposable;
    }

    @Override
    public void addPlatformRepository(@NonNull PlatformRepository platformRepository) {
        mPlatformRepository = platformRepository;
    }

    @Override
    public void addSessionManager(@NonNull SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    @Override
    public void addConnectionStateManager(@NonNull ConnectionStateManager connectionStateManager) {
        mConnectionStateManager = connectionStateManager;
    }

    @Override
    public void logToFile(@NonNull @Enums.Logging.StateType String state) {
        logToFile(state, null);
    }

    @Override
    public void logToFile(@NonNull @Enums.Logging.StateType String state, @Nullable String message) {
        logToFile(state, message, null);
    }

    @Override
    public void logToFile(@NonNull @Enums.Logging.StateType String state, @StringRes int messageResId) {
        if (messageResId != 0) {
            logToFile(state, mApplication.getString(messageResId));
        }
    }

    @Override
    public void logToFile(@NonNull String state, @StringRes int messageResId, @NonNull String... args) {
        if (messageResId != 0) {
            logToFile(state, mApplication.getString(messageResId, (Object) args));
        }
    }

    @Override
    public void logToFile(@NonNull String state, @StringRes int messageResId, @StringRes @NonNull int... args) {
        if (messageResId != 0) {
            String[] stringArgs = new String[args.length];

            for (int i = 0; i < stringArgs.length; i++) {
                stringArgs[i] = mApplication.getString(args[i]);
            }

            logToFile(state, messageResId, stringArgs);
        }
    }

    @Override
    public void logToFile(@NonNull @Enums.Logging.StateType String state, @Nullable String message, @Nullable StackTraceElement stackTrace) {

        String timestamp = Instant.now().toString();
        String emoji = "";

        if (message == null) {
            message = "";
        }

        String threadCopyOfMessage = message;
        StackTraceElement stackTraceElement = stackTrace;

        if (stackTraceElement == null) {
            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                if (element.getClassName().contains("com.nextiva.nextivaapp.android") &&
                        !element.getClassName().contains(NextivaLogManager.class.getSimpleName())) {

                    stackTraceElement = element;
                    break;
                }
            }
        }

        StackTraceElement threadCopyOfStackTraceElement = stackTraceElement;
        if (threadCopyOfStackTraceElement != null) {
            switch (state) {
                case Enums.Logging.STATE_INFO: {
                    LogUtil.i(String.format("%1$s\t%2$s\t%3$s.%4$s:%5$s\t%6$s\t%7$s",
                                            "\uD83D\uDE00",
                                            timestamp,
                                            threadCopyOfStackTraceElement.getClassName(),
                                            threadCopyOfStackTraceElement.getMethodName(),
                                            threadCopyOfStackTraceElement.getLineNumber(),
                                            state,
                                            StringUtil.redactApiUrl(threadCopyOfMessage)));
                    break;
                }
                case Enums.Logging.STATE_FAILURE: {
                    LogUtil.e(String.format("%1$s\t%2$s\t%3$s.%4$s:%5$s\t%6$s\t%7$s",
                                            "\uD83E\uDD22",
                                            timestamp,
                                            threadCopyOfStackTraceElement.getClassName(),
                                            threadCopyOfStackTraceElement.getMethodName(),
                                            threadCopyOfStackTraceElement.getLineNumber(),
                                            state,
                                            StringUtil.redactApiUrl(threadCopyOfMessage)));
                    break;
                }
                case Enums.Logging.STATE_ERROR: {
                    LogUtil.e(String.format("%1$s\t%2$s\t%3$s.%4$s:%5$s\t%6$s\t%7$s",
                                            "\uD83D\uDE21",
                                            timestamp,
                                            threadCopyOfStackTraceElement.getClassName(),
                                            threadCopyOfStackTraceElement.getMethodName(),
                                            threadCopyOfStackTraceElement.getLineNumber(),
                                            state,
                                            StringUtil.redactApiUrl(threadCopyOfMessage)));
                    break;
                }
            }

            String finalMessage = StringUtil.redactApiUrl(threadCopyOfMessage);

            Thread dbLoggingThread = new Thread(() -> {
                if (mDbManager != null &&
                        mCompositeDisposable != null &&
                        !finalMessage.contains(mApplication.getString(R.string.log_message_start)) &&
                        mSettingsManager.getFileLogging()) {
                    mLogger.debug(String.format("%1$s\t%2$s\t%3$s.%4$s:%5$s\t%6$s\t%7$s",
                                                emoji,
                                                timestamp,
                                                threadCopyOfStackTraceElement.getClassName(),
                                                threadCopyOfStackTraceElement.getMethodName(),
                                                threadCopyOfStackTraceElement.getLineNumber(),
                                                state,
                                                StringUtil.redactApiUrl(threadCopyOfMessage)));

                    //Add Logs to Crashlytics Crashes
                    FirebaseCrashlytics.getInstance().log(String.format("%1$s\t%2$s\t%3$s.%4$s:%5$s\t%6$s\t%7$s",
                                                                        emoji,
                                                                        timestamp,
                                                                        threadCopyOfStackTraceElement.getClassName(),
                                                                        threadCopyOfStackTraceElement.getMethodName(),
                                                                        threadCopyOfStackTraceElement.getLineNumber(),
                                                                        state,
                                                                        StringUtil.redactApiUrl(threadCopyOfMessage)));


                    String location = String.format("%1$s.%2$s:%3$s",
                                                    threadCopyOfStackTraceElement.getClassName(),
                                                    threadCopyOfStackTraceElement.getMethodName(),
                                                    threadCopyOfStackTraceElement.getLineNumber());

                    ArrayList<FeatureFlag> featureFlags = new ArrayList<>();

                    try {
                        FeatureFlags featureFlagsParent = null;
                        if (mFeatureFlags != null) {
                            featureFlagsParent = mFeatureFlags;

                        } else if (mSessionManager != null) {
                            mFeatureFlags = mSessionManager.getFeatureFlags();
                        }

                        featureFlags = (featureFlagsParent != null &&
                                featureFlagsParent.getFeatureFlags() != null) ? featureFlagsParent.getFeatureFlags() : new ArrayList<>();
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }


                    if (featureFlags != null && !featureFlags.isEmpty()) {
                        boolean isLoggingFeatureFlag = false;

                        for (FeatureFlag featureFlag : featureFlags) {
                            if (featureFlag != null &&
                                    featureFlag.getName() != null &&
                                    featureFlag.getName().equals(Enums.Platform.FeatureFlags.LOGGING) &&
                                    Objects.equals(featureFlag.isEnabled(), true)) {
                                isLoggingFeatureFlag = true;
                                break;
                            }
                        }

                        if (isLoggingFeatureFlag) {
                            new Thread(() -> {
                                DbLogging dbLogging = new DbLogging(null, state, finalMessage, Instant.now().toEpochMilli(), location, (mConnectionStateManager.isInternetConnected()) ? mConnectionStateManager.getInternetConnectionType() : "No Internet");
                                mDbManager.insertLog(mCompositeDisposable, dbLogging);
                            }).start();
                            new Thread(() -> {
                                if (mDbManager.getLogsCount() >= MINIMUM_LOGS_TO_POST &&
                                        Instant.now().toEpochMilli() > (mLogAPILastPostTime + mLogAPIBufferTimer)) {
                                    mLogAPILastPostTime = Instant.now().toEpochMilli();
                                    if (mConnectionStateManager.getInternetConnectionType().equals(Enums.InternetConnectTypes.WIFI) &&
                                            !mConnectionStateManager.isCallActive()) {
                                        LogUtil.postLogsToKibana(mCompositeDisposable, mPlatformRepository, mDbManager);
                                    }
                                }
                            }).start();
                        } else {

                            new Thread(() -> {
                                if (mDbManager.getLogsCount() > 0) {
                                    mDbManager.clearAllLogs(mCompositeDisposable);
                                }
                            }).start();
                        }
                    }
                }
            });
            dbLoggingThread.setUncaughtExceptionHandler((t, e) -> FirebaseCrashlytics.getInstance().recordException(e));
            dbLoggingThread.start();
        }
    }


    @Override
    public void sipLogToFile(@NonNull @Enums.Logging.StateType String state, @StringRes int messageResId) {
        if (mSettingsManager.getSipLogging()) {
            logToFile(state, messageResId);
        }
    }

    @Override
    public void sipLogToFile(@NonNull @Enums.Logging.StateType String state, @Nullable String message) {
        if (mSettingsManager.getSipLogging()) {
            logToFile(state, message);
        }
    }

    @Override
    public void sipLogToFile(@NonNull @Enums.Logging.StateType String state, @Nullable String message, @Nullable StackTraceElement stackTrace) {
        if (mSettingsManager.getSipLogging()) {
            logToFile(state, message, stackTrace);
        }
    }

    @Override
    public void xmppLogToFile(@NonNull @Enums.Logging.StateType String state, @StringRes int messageResId) {
        if (mSettingsManager.getXMPPLogging()) {
            logToFile(state, messageResId);
        }
    }

    @Override
    public void xmppLogToFile(@NonNull @Enums.Logging.StateType String state, @Nullable String message) {
        if (mSettingsManager.getXMPPLogging()) {
            logToFile(state, message);
        }
    }

    @Override
    public void xmppLogToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId, @StringRes @NonNull int... args) {
        if (mSettingsManager.getXMPPLogging()) {
            logToFile(state, messageResId, args);
        }
    }

    @Override
    public void xmppLogToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId, @NonNull String... args) {
        if (mSettingsManager.getXMPPLogging()) {
            logToFile(state, messageResId, args);
        }
    }
    // --------------------------------------------------------------------------------------------
}