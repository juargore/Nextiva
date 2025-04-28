package com.nextiva.nextivaapp.android.xmpp.iqpackets.providers;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.xmpp.iqpackets.UniqueResultIQ;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;

import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class UniqueIQProvider extends IQProvider<UniqueResultIQ> {

    @Override
    public UniqueResultIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        UniqueResultIQ uniqueResultIQ = new UniqueResultIQ(NextivaXMPPConstants.CHAT_UNIQUE_ID_TAGNAME, null);

        String text = "";
        boolean isFinished = false;
        int eventType = parser.getEventType();

        while (!isFinished) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (TextUtils.equals(parser.getName(), NextivaXMPPConstants.CHAT_UNIQUE_ID_TAGNAME)) {
                        uniqueResultIQ.setUniqueId(text);
                        isFinished = true;
                    }
            }

            eventType = parser.next();
        }

        return uniqueResultIQ;
    }
}
