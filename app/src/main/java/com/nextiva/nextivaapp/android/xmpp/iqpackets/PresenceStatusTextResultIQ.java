package com.nextiva.nextivaapp.android.xmpp.iqpackets;

import org.jivesoftware.smack.packet.IQ;

public class PresenceStatusTextResultIQ extends IQ {

    private String mPresenceStatusText;

    public PresenceStatusTextResultIQ(String childElementName, String presenceStatusText) {
        super(childElementName);
        mPresenceStatusText = presenceStatusText;
    }

    public String getPresenceStatusText() {
        return mPresenceStatusText;
    }

    public void setPresenceStatusText(String presenceStatusText) {
        mPresenceStatusText = presenceStatusText;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

}
