package com.nextiva.nextivaapp.android.xmpp.iqpackets;

import org.jivesoftware.smack.packet.IQ;

public class UniqueResultIQ extends IQ {
    private String mUniqueId;

    public UniqueResultIQ(String childElementName, String uniqueId) {
        super(childElementName);
        mUniqueId = uniqueId;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

    public String getUniqueId() {
        return mUniqueId;
    }

    public void setUniqueId(String uniqueId) {
        mUniqueId = uniqueId;
    }
}
