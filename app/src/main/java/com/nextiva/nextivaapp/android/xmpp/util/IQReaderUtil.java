package com.nextiva.nextivaapp.android.xmpp.util;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbPresence;

import org.jivesoftware.smack.packet.Presence;

public class IQReaderUtil {

    public static DbPresence getPresenceFromPresenceWithBroadsoftNamespace(Presence presence) {
        String xml = presence.getExtensions().get(presence.getExtensions().size() - 1).toXML(NextivaXMPPConstants.CONTACTS_IQ_NAMESPACE_BSFT_PRESENCE_ON_DEMAND).toString();

        DbPresence nextivaPresence = new DbPresence();
        nextivaPresence.setJid(presence.getFrom().asBareJid().toString());
        nextivaPresence.setType(Enums.Contacts.PresenceTypes.AVAILABLE);

        if (xml.contains(NextivaXMPPConstants.PRESENCE_IQ_PRIORITY_OPEN_ELEMENT)) {
            nextivaPresence.setPriority(Integer.parseInt(xml.substring(xml.indexOf(NextivaXMPPConstants.PRESENCE_IQ_PRIORITY_OPEN_ELEMENT)
                                                                               + NextivaXMPPConstants.PRESENCE_IQ_PRIORITY_OPEN_ELEMENT.length(),
                                                                       xml.indexOf(NextivaXMPPConstants.PRESENCE_IQ_PRIORITY_CLOSE_ELEMENT))));
        } else {
            return null;
        }

        if (xml.contains(NextivaXMPPConstants.PRESENCE_IQ_FREETEXT_OPEN_ELEMENT)) {
            nextivaPresence.setStatus(xml.substring(xml.lastIndexOf(NextivaXMPPConstants.PRESENCE_IQ_FREETEXT_OPEN_ELEMENT)
                                                            + NextivaXMPPConstants.PRESENCE_IQ_FREETEXT_OPEN_ELEMENT.length(),
                                                    xml.lastIndexOf(NextivaXMPPConstants.PRESENCE_IQ_FREETEXT_CLOSE_ELEMENT)));
        }

        if (xml.contains(NextivaXMPPConstants.PRESENCE_IQ_SHOW_OPEN_ELEMENT)) {
            switch (xml.substring(xml.lastIndexOf(NextivaXMPPConstants.PRESENCE_IQ_SHOW_OPEN_ELEMENT)
                                          + NextivaXMPPConstants.PRESENCE_IQ_SHOW_OPEN_ELEMENT.length(),
                                  xml.lastIndexOf(NextivaXMPPConstants.PRESENCE_IQ_SHOW_CLOSE_ELEMENT))) {
                case "available":
                    nextivaPresence.setState(Enums.Contacts.PresenceStates.AVAILABLE);
                    break;
                case "away":
                    nextivaPresence.setState(Enums.Contacts.PresenceStates.AWAY);
                    break;
                case "dnd":
                    nextivaPresence.setState(Enums.Contacts.PresenceStates.BUSY);
                    break;
                case "offline":
                    nextivaPresence.setState(Enums.Contacts.PresenceStates.OFFLINE);
                    break;
            }
        } else {
            return null;
        }

        return nextivaPresence;
    }

    public static boolean isVCardUpdatePresence(Presence presence) {
        return presence.toXML("").toString().contains(NextivaXMPPConstants.VCARD_UPDATE_VALUE);
    }

    public static boolean isManualTruePresence(Presence presence) {
        return (presence.toXML("jabber:client").toString().contains("<name>manual</name>") &&
                presence.toXML("jabber:client").toString().contains("<value type='string'>true</value>")) ||
                (presence.toXML("jabber:client").toString().contains("<name xmlns:stream='http://etherx.jabber.org/streams'>manual</name>") &&
                        presence.toXML("jabber:client").toString().contains("<value xmlns:stream='http://etherx.jabber.org/streams'>true</value>")) ||
                (presence.toXML("jabber:client").toString().contains("<name xmlns:stream='http://etherx.jabber.org/streams'>manual</name>") &&
                        presence.toXML("jabber:client").toString().contains("<value xmlns:stream='http://etherx.jabber.org/streams' type='string'>true</value>")) ||
                (presence.toXML("jabber:client").toString().contains("<name>manual</name>") &&
                        presence.toXML("jabber:client").toString().contains("<value type='string'>true</value>"));
    }

    public static boolean isManualFalsePresence(Presence presence) {
        return (presence.toXML("jabber:client").toString().contains("<name>manual</name>") &&
                presence.toXML("jabber:client").toString().contains("<value type='string'>false</value>")) ||
                (presence.toXML("jabber:client").toString().contains("<name xmlns:stream='http://etherx.jabber.org/streams'>manual</name>") &&
                        presence.toXML("jabber:client").toString().contains("<value xmlns:stream='http://etherx.jabber.org/streams'>false</value>")) ||
                (presence.toXML("jabber:client").toString().contains("<name xmlns:stream='http://etherx.jabber.org/streams'>manual</name>") &&
                        presence.toXML("jabber:client").toString().contains("<value xmlns:stream='http://etherx.jabber.org/streams' type='string'>false</value>")) ||
                (presence.toXML("jabber:client").toString().contains("<name>manual</name>") &&
                        presence.toXML("jabber:client").toString().contains("<value type='string'>false</value>"));
    }
}
