package com.nextiva.nextivaapp.android.xmpp.util;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbPresence;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Presence;

public class PresenceUtil {

    public static Presence dbPresenceToSmackPresence(DbPresence nextivaPresence) {
        Presence presence = new Presence(Presence.Type.available);

        if (nextivaPresence.getType() == Enums.Contacts.PresenceTypes.UNAVAILABLE) {
            presence.setType(Presence.Type.unavailable);
        }

        switch (nextivaPresence.getState()) {
            case Enums.Contacts.PresenceStates.AWAY:
                presence.setMode(Presence.Mode.away);
                break;
            case Enums.Contacts.PresenceStates.BUSY:
                presence.setMode(Presence.Mode.dnd);
                break;
            case Enums.Contacts.PresenceStates.OFFLINE:
                presence.setMode(Presence.Mode.xa);
                break;
            case Enums.Contacts.PresenceStates.AVAILABLE:
            default:
                presence.setMode(Presence.Mode.available);
                break;
        }

        if (!TextUtils.isEmpty(nextivaPresence.getStatus())) {
            presence.setStatus(nextivaPresence.getStatus());
        }

        presence.setPriority(nextivaPresence.getPriority());

        presence.addExtension(new ExtensionElement() {
            @Override
            public String getNamespace() {
                return null;
            }

            @Override
            public String getElementName() {
                return null;
            }

            @Override
            public CharSequence toXML(String enclosingNamespace) {
                return NextivaXMPPConstants.PRESENCE_IQ_PROPERTIES_ELEMENT;
            }
        });

        return presence;
    }

    public static DbPresence getPendingPresence(String jid) {
        return new DbPresence(jid, Enums.Contacts.PresenceStates.PENDING, Constants.PRESENCE_OFFLINE_PRIORITY, null, Enums.Contacts.PresenceTypes.UNAVAILABLE);
    }

    public static DbPresence getUnsubscribedPresence(String jid) {
        return new DbPresence(jid, Enums.Contacts.PresenceStates.NONE, Constants.PRESENCE_OFFLINE_PRIORITY, null, Enums.Contacts.PresenceTypes.UNAVAILABLE);
    }

    public static DbPresence getOnCallPresence() {
        return new DbPresence(null, Enums.Contacts.PresenceStates.BUSY, Constants.PRESENCE_ON_CALL_PRIORITY, null, Enums.Contacts.PresenceTypes.AVAILABLE);
    }

    public static DbPresence getMobilePresence() {
        return new DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, Constants.PRESENCE_MOBILE_PRIORITY, null, Enums.Contacts.PresenceTypes.AVAILABLE);
    }
}
