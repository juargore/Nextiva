package com.nextiva.nextivaapp.android.xmpp.iqpackets;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.util.GuidUtil;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;

import org.jivesoftware.smack.packet.IQ;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public class PresenceStatusTextGetIQ extends IQ {

    final private String mUsername;
    final private String mServerName;
    final private String mFullJid;

    public PresenceStatusTextGetIQ(String username, String serverName, String fullJid) {
        super(NextivaXMPPConstants.IQ_CHILD_ELEMENT_PUBSUB,
              NextivaXMPPConstants.PUBSUB_IQ_NAMESPACE_PROTOCOLS_PUBSUB);

        mUsername = username;
        mServerName = serverName;
        mFullJid = fullJid;

        try {
            setTo(JidCreate.bareFrom(NextivaXMPPConstants.IQ_CHILD_ELEMENT_PUBSUB + "." + serverName));
        } catch (XmppStringprepException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        setType(IQ.Type.get);
        setStanzaId(String.valueOf(GuidUtil.getRandomId()));
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.halfOpenElement(NextivaXMPPConstants.PUBSUB_IQ_ITEMS_TAGNAME);
        xml.attribute(NextivaXMPPConstants.PUBSUB_IQ_NODE_TAGNAME, NextivaXMPPConstants.PUBSUB_IQ_PRESENCE_STATUS_TEXT + mUsername + "-" + mServerName);
        xml.attribute(NextivaXMPPConstants.PUBSUB_IQ_JID_TAGNAME, mFullJid);
        xml.closeEmptyElement();
        return xml;
    }

}
