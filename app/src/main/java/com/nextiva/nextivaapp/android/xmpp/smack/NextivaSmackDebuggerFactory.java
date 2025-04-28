package com.nextiva.nextivaapp.android.xmpp.smack;

import android.app.Application;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;

public class NextivaSmackDebuggerFactory implements SmackDebuggerFactory {

    private final Application mApplication;

    public NextivaSmackDebuggerFactory(Application application) {
        mApplication = application;
    }

    // --------------------------------------------------------------------------------------------
    // SmackDebuggerFactory Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public SmackDebugger create(XMPPConnection connection) throws IllegalArgumentException {
        return new NextivaSmackDebugger(mApplication, connection);
    }
    // --------------------------------------------------------------------------------------------
}
