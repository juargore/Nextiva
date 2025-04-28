package com.nextiva.nextivaapp.android.xmpp.smack;

import android.app.Application;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.debugger.android.AndroidDebugger;

import javax.inject.Inject;

public class NextivaSmackDebugger extends AndroidDebugger {

    @Inject
    protected LogManager mLogManager;
    @Inject
    protected SettingsManager mSettingsManager;

    public NextivaSmackDebugger(Application application, XMPPConnection connection) {
        super(connection);

    }

    // --------------------------------------------------------------------------------------------
    // AndroidDebugger Methods
    // --------------------------------------------------------------------------------------------
    @Override
    protected void log(String logMessage) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, logMessage);

    }

    @Override
    protected void log(String logMessage, Throwable throwable) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_FAILURE, logMessage + " " + throwable.getLocalizedMessage());
        FirebaseCrashlytics.getInstance().recordException(throwable);
    }
    // --------------------------------------------------------------------------------------------
}
