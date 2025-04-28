package com.nextiva.nextivaapp.android.sip;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;

import java.util.regex.Pattern;

/**
 * Created by Thaddeus Dannar on 8/13/18.
 */
public class SipMessage {

    private String mContact;
    private String mTo;
    private String mFrom;
    private String mAssertedIdentity;
    private String mAssertedName;
    private String mAssertedPhone;
    private String mVideo;
    private String mCallId;

    public SipMessage() {
    }

    public SipMessage parseSipMessage(String sipMessage) {
        mAssertedIdentity = StringUtil.getStringFromPattern(Pattern.compile(Enums.TextPatterns.Sip.Messages.ASSERTED_IDENTITY), sipMessage);
        mAssertedName = StringUtil.getStringFromPattern(Pattern.compile(Enums.TextPatterns.Sip.Messages.ASSERTED_IDENTITY_NAME), sipMessage);
        mAssertedPhone = splitPhoneNumberFromName(mAssertedIdentity);
        mContact = StringUtil.getStringFromPattern(Pattern.compile(Enums.TextPatterns.Sip.Messages.CONTACT), sipMessage);
        mTo = StringUtil.getStringFromPattern(Pattern.compile(Enums.TextPatterns.Sip.Messages.TO), sipMessage);
        mFrom = StringUtil.getStringFromPattern(Pattern.compile(Enums.TextPatterns.Sip.Messages.FROM), sipMessage);
        mCallId = StringUtil.getStringBetween(sipMessage, "Call-ID: ", "..");

        try {
            mVideo = getVideoFromMessage(sipMessage);
        } catch (Exception x) {
            LogUtil.e(x.toString());
            FirebaseCrashlytics.getInstance().recordException(x);
        }

        return this;
    }

    private String splitPhoneNumberFromName(String fullAssert) {
        return StringUtil.getStringFromPattern(getSipNumberOrIpPattern(), fullAssert);
    }

    private String getVideoFromMessage(String sipMessage) {
        String[] sipMessageSplit = sipMessage.split(Enums.TextPatterns.Sip.Messages.VIDEO);

        if (sipMessageSplit.length > 1) {
            return Enums.TextPatterns.Sip.Messages.VIDEO + sipMessageSplit[1];
        }

        return "";
    }

    private Pattern getSipNumberOrIpPattern() {
        return Pattern.compile(Enums.TextPatterns.Sip.Messages.ASSERTED_IDENTITY_PHONE);
    }

    public String getContact() {
        return mContact;
    }

    public String getTo() {
        return mTo;
    }

    public String getFrom() {
        return mFrom;
    }

    public String getAssertedIdentity() {
        return mAssertedIdentity;
    }

    public String getAssertedName() {
        return mAssertedName;
    }

    public String getAssertedPhone() {
        return mAssertedPhone;
    }

    public String getVideo() {
        return mVideo;
    }

    public String getCallId() {
        return mCallId;
    }

}
