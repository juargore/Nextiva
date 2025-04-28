package com.nextiva.nextivaapp.android.xmpp.iqpackets.providers;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.xmpp.iqpackets.PresenceStatusTextResultIQ;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;

import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class PresenceStatusTextIQProvider extends IQProvider<PresenceStatusTextResultIQ> {

    final private DbManager mDbManager;

    public PresenceStatusTextIQProvider(DbManager dbManager) {
        mDbManager = dbManager;
    }

    @Override
    public PresenceStatusTextResultIQ parse(XmlPullParser parser, int initialDepth) throws Exception {

        PresenceStatusTextResultIQ presenceStatusTextSetIQ = new PresenceStatusTextResultIQ(NextivaXMPPConstants.IQ_CHILD_ELEMENT_PUBSUB, null);

        String text = "";
        boolean isFinished = false;
        int eventType = parser.getEventType();

        while (!isFinished) {

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (TextUtils.equals(NextivaXMPPConstants.PUBSUB_IQ_IQ_TAGNAME, parser.getName())) {
                        if (parser.getAttributeValue(null, NextivaXMPPConstants.PUBSUB_IQ_TYPE_TAGNAME) != null &&
                                TextUtils.equals(parser.getAttributeValue(null, NextivaXMPPConstants.PUBSUB_IQ_TYPE_TAGNAME),
                                                 NextivaXMPPConstants.PUBSUB_IQ_ERROR_TAGNAME)) {
                            isFinished = true;
                        }
                    }
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    switch (parser.getName()) {
                        case NextivaXMPPConstants.PRESENCE_IQ_STATUS_TAGNAME:
                            presenceStatusTextSetIQ.setPresenceStatusText(text);
                            mDbManager.updateCurrentUserStatus(text);
                            break;
                        case NextivaXMPPConstants.IQ_CHILD_ELEMENT_PUBSUB:
                            isFinished = true;
                            break;
                    }

                    break;
            }

            eventType = parser.next();
        }

        return presenceStatusTextSetIQ;
    }
}
