/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository;

import io.reactivex.disposables.CompositeDisposable;

@SuppressWarnings("unused")
public interface LogManager {

    void setupLogger();

    void addDBManager(@NonNull DbManager dbManager);

    void addCompositeDisposable(@NonNull CompositeDisposable compositeDisposable);

    void addPlatformRepository(@NonNull PlatformRepository platformRepository);

    void addSessionManager(@NonNull SessionManager sessionManager);

    void addConnectionStateManager(@NonNull ConnectionStateManager connectionStateManager);

    void logToFile(@Enums.Logging.StateType @NonNull String state);

    void logToFile(@Enums.Logging.StateType @NonNull String state, @Nullable String message);

    void logToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId);

    void logToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId, String... args);

    void logToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId, @StringRes int... args);

    void logToFile(@NonNull @Enums.Logging.StateType String state, @Nullable String message, @Nullable StackTraceElement stackTrace);

    void sipLogToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId);

    void sipLogToFile(@Enums.Logging.StateType @NonNull String state, @Nullable String message);

    void sipLogToFile(@Enums.Logging.StateType @NonNull String state, @Nullable String message, @Nullable StackTraceElement stackTrace);

    void xmppLogToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId);

    void xmppLogToFile(@Enums.Logging.StateType @NonNull String state, @Nullable String message);

    void xmppLogToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId, @StringRes int... args);

    void xmppLogToFile(@Enums.Logging.StateType @NonNull String state, @StringRes int messageResId, String... args);
}
