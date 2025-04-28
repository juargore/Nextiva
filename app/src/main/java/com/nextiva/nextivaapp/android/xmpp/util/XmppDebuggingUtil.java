package com.nextiva.nextivaapp.android.xmpp.util;

import com.nextiva.nextivaapp.android.util.LogUtil;

import org.jivesoftware.smack.SmackException;
import org.jxmpp.stringprep.XmppStringprepException;

public class XmppDebuggingUtil {
    public static void displayDebugLogMessage(Exception e, StackTraceElement element) {

        String messageToDisplay;

        if (e instanceof SmackException.NotConnectedException) {
            messageToDisplay = createLogMessage(element, "A connection to XMPP is not available.");
        } else if (e instanceof SmackException.NotLoggedInException) {
            messageToDisplay = createLogMessage(element, "An XMPP session could not be found.");
        } else if (e instanceof SmackException.NoResponseException) {
            messageToDisplay = createLogMessage(element, "Unable to get a response from the XMPP server.");
        } else if (e instanceof XmppStringprepException) {
            messageToDisplay = createLogMessage(element, "Error prepping string for XMPP.");
        } else if (e instanceof InterruptedException) {
            messageToDisplay = createLogMessage(element, "A connection to XMPP has been interrupted.");
        } else {
            messageToDisplay = createLogMessage(element, "XMPP error exception.");
        }

        LogUtil.e(messageToDisplay);
    }

    private static String createLogMessage(StackTraceElement element, String message) {
        return element.getClassName() +
                ", " +
                element.getMethodName() +
                " " +
                element.getLineNumber() +
                ": " +
                message;
    }

}
