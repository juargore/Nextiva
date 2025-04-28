package com.nextiva.nextivaapp.android.models.mobileConfig;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 3/6/19.
 */
public class MobileConfig {
    @Nullable
    private Calls mCalls;
    @Nullable
    private Sip mSip;
    @Nullable
    private String mVoicemailPhoneNumber;
    @Nullable
    private Xmpp mXmpp;
    @Nullable
    private Xsi mXsi;
    @Nullable
    private ArrayList<String> mEmergencyNumbersArrayList;

    public MobileConfig() {
    }

    @Nullable
    public Calls getCalls() {
        return mCalls;
    }

    public void setCalls(@Nullable final Calls calls) {
        mCalls = calls;
    }

    @Nullable
    public Sip getSip() {
        return mSip;
    }

    public void setSip(@Nullable final Sip sip) {
        mSip = sip;
    }

    @Nullable
    public String getVoicemailPhoneNumber() {
        return mVoicemailPhoneNumber;
    }

    public void setVoicemailPhoneNumber(@Nullable final String voicemailPhoneNumber) {
        mVoicemailPhoneNumber = voicemailPhoneNumber;
    }

    @Nullable
    public Xmpp getXmpp() {
        return mXmpp;
    }

    public void setXmpp(@Nullable final Xmpp xmpp) {
        mXmpp = xmpp;
    }

    @Nullable
    public Xsi getXsi() {
        return mXsi;
    }

    public void setXsi(@Nullable final Xsi xsi) {
        mXsi = xsi;
    }

    public void setEmergencyNumbers(final ArrayList<String> numbers) {
        mEmergencyNumbersArrayList = numbers;
    }

    public ArrayList<String> getEmergencyNumbers() {
        return mEmergencyNumbersArrayList;
    }
}
