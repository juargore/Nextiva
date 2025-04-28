/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;
import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbLogging;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository;
import com.nextiva.nextivaapp.android.models.net.platform.LogEntry;
import com.nextiva.nextivaapp.android.models.net.platform.LogMetadata;
import com.nextiva.nextivaapp.android.models.net.platform.LogSubmit;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by adammacdonald on 2/20/18.
 */

@SuppressWarnings("unused")
public class LogUtil {
    private static final int MAX_LOGS_TO_POST_AT_A_TIME = 250; 

    private static final String TAG = "Nextiva Android";
    private static boolean mIsPostInProgress;

    public static void log(String message) {
        log(TAG, message);
    }

    public static void log(String message, int logType) {
        log(TAG, message, logType);
    }

    public static void log(String tag, String message) {
        log(tag, message, Log.DEBUG);
    }

    private static void log(String tag, String message, int logType) {
        if (BuildConfig.DEBUG) {
            switch (logType) {
                case Log.ASSERT:
                    Log.wtf(tag, message);
                    break;
                case Log.ERROR:
                    Log.e(tag, message);
                    break;
                case Log.INFO:
                    Log.i(tag, message);
                    break;
                case Log.VERBOSE:
                    Log.v(tag, message);
                    break;
                case Log.WARN:
                    Log.w(tag, message);
                    break;
                case Log.DEBUG:
                default:
                    Log.d(tag, message);
                    break;

            }
        }
    }

    //Show Assertive logs
    private static void wtf(String tag, String message) {
        log(tag, message, Log.ASSERT);
    }

    public static void wtf(String message) {
        wtf(TAG, message);
    }

    //Show error logs
    public static void e(String tag, String message) {
        log(tag, message, Log.ERROR);
    }

    public static void e(String message) {
        e(TAG, message);
    }

    //Show Information Logs
    private static void i(String tag, String message) {
        log(tag, message, Log.INFO);
    }

    public static void i(String message) {
        i(TAG, message);
    }

    ///Show Verbose Logs
    private static void v(String tag, String message) {
        log(tag, message, Log.VERBOSE);
    }

    public static void v(String message) {
        v(TAG, message);
    }

    ///Show Warning Logs
    private static void w(String tag, String message) {
        log(tag, message, Log.WARN);
    }

    public static void w(String message) {
        w(TAG, message);
    }

    ///Show Debug Logs
    public static void d(String tag, String message) {
        log(tag, message, Log.DEBUG);
    }

    public static void d(String message) {
        d(TAG, message);
    }

    public static void postLogsToKibana(@NonNull CompositeDisposable compositeDisposable, @NonNull PlatformRepository platformRepository, @NonNull DbManager dbManager) {

        new Thread(() -> {
            try {
                List<DbLogging> dbLogs = dbManager.getLogs(MAX_LOGS_TO_POST_AT_A_TIME);

                LogSubmit logSubmit;
                LogMetadata logMetadata = new LogMetadata();
                logMetadata.setAppName("nextivaapp-android");


                JsonObject additionalObject = new JsonObject();
                additionalObject.addProperty("Manufacturer", Build.MANUFACTURER);
                additionalObject.addProperty("Model", Build.MODEL);
                additionalObject.addProperty("Device", Build.DEVICE);
                additionalObject.addProperty("Version", BuildConfig.VERSION_NAME);
                additionalObject.addProperty("Flavor", BuildConfig.FLAVOR);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    additionalObject.addProperty("OSVersion", Build.VERSION.BASE_OS);
                    additionalObject.addProperty("RELEASE", Build.VERSION.RELEASE_OR_CODENAME);
                    additionalObject.addProperty("SECURITY_PATCH", Build.VERSION.SECURITY_PATCH);
                } else {
                    additionalObject.addProperty("RELEASE", Build.VERSION.RELEASE);
                    additionalObject.addProperty("CODENAME", Build.VERSION.CODENAME);

                }
                additionalObject.addProperty("AndroidSDK", Build.VERSION.SDK_INT);
                additionalObject.addProperty("IncrementalOSVersion", Build.VERSION.INCREMENTAL);

                logMetadata.setAdditional(additionalObject);

                ArrayList<LogEntry> logs = new ArrayList<>();
                LogEntry logEntry;
                String logLevel = Enums.Logging.KibanaStateTypes.STATE_INFO;
                for (DbLogging dbLog : dbLogs) {
                    logEntry = new LogEntry();

                    logEntry.setLevel(getKibanaLogLevel(dbLog.getLevel()));
                    logEntry.setMessage(dbLog.getMessage());
                    logEntry.setTimestamp(dbLog.getTime());

                    JsonObject additionalLogObject = new JsonObject();
                    additionalLogObject.addProperty("Location", dbLog.getLocation());
                    additionalLogObject.addProperty("Internet Status", dbLog.getInternetStatus());

                    logEntry.setAdditional(additionalLogObject);
                    logs.add(logEntry);
                }

                if (!logs.isEmpty()) {
                    logSubmit = new LogSubmit(logMetadata, logs);
                    compositeDisposable.add(
                            platformRepository.postLogs(logSubmit).subscribe(
                                    response -> {
                                        if (response.isSuccessful()) {
                                            new Thread(() -> {
                                                dbManager.clearPostedLogs(MAX_LOGS_TO_POST_AT_A_TIME);
                                                if (dbManager.getLogsCount() > MAX_LOGS_TO_POST_AT_A_TIME) {
                                                    postLogsToKibana(compositeDisposable, platformRepository, dbManager);
                                                }
                                            }).start();
                                        }
                                        //Don't send to Crashlytics, the API call handles this in postLogs
                                    })
                    );
                }
            } catch (OutOfMemoryError x) {
                FirebaseCrashlytics.getInstance().recordException(x);
            }
        }).start();
    }

    private static String getKibanaLogLevel(String logLevel) {
        if (logLevel == null) {
            return Enums.Logging.KibanaStateTypes.STATE_INFO;
        }
        switch (logLevel) {
            case Enums.Logging.KibanaStateTypes.STATE_ERROR:
            case Enums.Logging.STATE_FAILURE:
                return Enums.Logging.KibanaStateTypes.STATE_ERROR;
            case Enums.Logging.STATE_INFO:
                return Enums.Logging.KibanaStateTypes.STATE_INFO;
            case Enums.Logging.KibanaStateTypes.STATE_DEBUG:
                return Enums.Logging.KibanaStateTypes.STATE_DEBUG;
            case Enums.Logging.KibanaStateTypes.STATE_TRACE:
                return Enums.Logging.KibanaStateTypes.STATE_TRACE;
            case Enums.Logging.KibanaStateTypes.STATE_FATAL:
                return Enums.Logging.KibanaStateTypes.STATE_FATAL;
            case Enums.Logging.KibanaStateTypes.STATE_WARN:
                return Enums.Logging.KibanaStateTypes.STATE_WARN;
        }
        return Enums.Logging.KibanaStateTypes.STATE_INFO;
    }

}
