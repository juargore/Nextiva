/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.util;

import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.BIND_NAMESPACE;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.BIND_RESOURCE_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.CHAT_MUC_DISCO_NAMESPACE;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.CHAT_MUC_NODE_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.IQ_CHILD_ELEMENT_PING;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.IQ_CHILD_ELEMENT_PUBSUB;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.IQ_CHILD_ELEMENT_QUERY;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.IQ_NAMESPACE;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PING_NAMESPACE;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PRESENCE_IQ_STATUS_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PRESENCE_VCARD_UPDATE_PROPERTIES;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_CONTACT_STORAGE_TEXT;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_DATA_ITEM_TEXT;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_ID_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_ITEM_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_JID_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_LAST_UPDATE_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_NAMESPACE_PROTOCOLS_PUBSUB;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_NODE_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_PRESENCE_STATUS_TEXT;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_PRESENCE_STORAGE_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_PRESENCE_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_PUBLISH_TAGNAME;
import static com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants.PUBSUB_IQ_SUBSCRIBE_TAGNAME;

import com.nextiva.nextivaapp.android.util.GuidUtil;
import com.nextiva.nextivaapp.android.util.LogUtil;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * Created by joedephillipo on 3/21/18.
 */

public class IQBuilderUtil {

    public static IQ subscribeToPubSubStatusTextIQ(final String username,
                                                   final String serviceName,
                                                   final String fullJid) {

        final IQ iq = new IQ(IQ_CHILD_ELEMENT_PUBSUB, PUBSUB_IQ_NAMESPACE_PROTOCOLS_PUBSUB) {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                xml.rightAngleBracket();
                xml.halfOpenElement(PUBSUB_IQ_SUBSCRIBE_TAGNAME);
                xml.attribute(PUBSUB_IQ_NODE_TAGNAME, PUBSUB_IQ_PRESENCE_STATUS_TEXT + username + "-" + serviceName);
                xml.attribute(PUBSUB_IQ_JID_TAGNAME, fullJid);
                xml.rightAngleBracket();
                xml.closeElement(PUBSUB_IQ_SUBSCRIBE_TAGNAME);

                return xml;
            }
        };

        try {
            iq.setTo(JidCreate.bareFrom(IQ_CHILD_ELEMENT_PUBSUB + "." + serviceName));

        } catch (XmppStringprepException e) {
            LogUtil.e(e.getLocalizedMessage());
        }

        iq.setType(IQ.Type.set);
        return iq;
    }

    public static IQ subscribeToPubSubContactStorageIQ(final String username,
                                                       final String serviceName,
                                                       final String fullJid) {

        final IQ iq = new IQ(IQ_CHILD_ELEMENT_PUBSUB, PUBSUB_IQ_NAMESPACE_PROTOCOLS_PUBSUB) {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                xml.rightAngleBracket();
                xml.halfOpenElement(PUBSUB_IQ_SUBSCRIBE_TAGNAME);
                xml.attribute(PUBSUB_IQ_JID_TAGNAME, fullJid);
                xml.attribute(PUBSUB_IQ_NODE_TAGNAME, PUBSUB_IQ_CONTACT_STORAGE_TEXT + username + "-" + serviceName);
                xml.rightAngleBracket();
                xml.closeElement(PUBSUB_IQ_SUBSCRIBE_TAGNAME);

                return xml;
            }
        };

        try {
            iq.setTo(JidCreate.bareFrom(IQ_CHILD_ELEMENT_PUBSUB + "." + serviceName));

        } catch (XmppStringprepException e) {
            LogUtil.e(e.getLocalizedMessage());
        }

        iq.setType(IQ.Type.set);
        return iq;
    }

    public static IQ setPubSubStatusTextIQ(final String username,
                                           final String serviceName,
                                           final String timestamp,
                                           final String status) {

        final IQ iq = new IQ(IQ_CHILD_ELEMENT_PUBSUB, PUBSUB_IQ_NAMESPACE_PROTOCOLS_PUBSUB) {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                xml.rightAngleBracket();
                xml.halfOpenElement(PUBSUB_IQ_PUBLISH_TAGNAME);
                xml.attribute(PUBSUB_IQ_NODE_TAGNAME, PUBSUB_IQ_PRESENCE_STATUS_TEXT + username + "-" + serviceName);
                xml.rightAngleBracket();
                xml.halfOpenElement(PUBSUB_IQ_ITEM_TAGNAME);
                xml.attribute(PUBSUB_IQ_ID_TAGNAME,
                              PUBSUB_IQ_PRESENCE_STATUS_TEXT + PUBSUB_IQ_DATA_ITEM_TEXT + "-" + username + "-" + serviceName);
                xml.rightAngleBracket();
                xml.openElement(PUBSUB_IQ_PRESENCE_STORAGE_TAGNAME);
                xml.openElement(PUBSUB_IQ_PRESENCE_TAGNAME);
                xml.openElement(PRESENCE_IQ_STATUS_TAGNAME);
                xml.append(status);
                xml.closeElement(PRESENCE_IQ_STATUS_TAGNAME);
                xml.closeElement(PUBSUB_IQ_PRESENCE_TAGNAME);
                xml.openElement(PUBSUB_IQ_LAST_UPDATE_TAGNAME);
                xml.append(timestamp);
                xml.closeElement(PUBSUB_IQ_LAST_UPDATE_TAGNAME);
                xml.closeElement(PUBSUB_IQ_PRESENCE_STORAGE_TAGNAME);
                xml.closeElement(PUBSUB_IQ_ITEM_TAGNAME);
                xml.closeElement(PUBSUB_IQ_PUBLISH_TAGNAME);

                return xml;
            }
        };

        iq.setStanzaId(String.valueOf(GuidUtil.getRandomId()));

        try {
            iq.setTo(JidCreate.bareFrom(IQ_CHILD_ELEMENT_PUBSUB + "." + serviceName));

        } catch (XmppStringprepException e) {
            LogUtil.e(e.getLocalizedMessage());
        }

        iq.setType(IQ.Type.set);
        return iq;
    }

    /**
     * Example
     * &lt;iq id="qxmpp55908" type="set"&gt;
     * &lt;bind xmlns="urn:ietf:params:xml:ns:xmpp-bind"&gt;
     * &lt;resource&gt;bc-uc - Nextiva App (22.1.0.177 Mac OS X (16)) f975a66d60e0bf6345b26cad9b32c76d1961bb1d&lt;/resource&gt;
     * &lt;/bind&gt;
     * &lt;/iq&gt;
     */

    public static IQ getBindResourceSetIQ(String resource) {
        IQ iq = new IQ(IQ_CHILD_ELEMENT_QUERY) {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                xml.attribute(IQ_NAMESPACE, BIND_NAMESPACE);
                xml.rightAngleBracket();
                xml.halfOpenElement(BIND_RESOURCE_TAGNAME);
                xml.rightAngleBracket();
                xml.append(resource);
                xml.closeElement(BIND_RESOURCE_TAGNAME);
                return xml;
            }
        };

        iq.setType(IQ.Type.set);

        return iq;
    }

    public static IQ getPingIQ() {
        return new IQ(IQ_CHILD_ELEMENT_PING) {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                xml.attribute(IQ_NAMESPACE, PING_NAMESPACE);
                xml.rightAngleBracket();
                return xml;
            }
        };
    }

    /**
     * Example
     * &lt;iq type="get" to="lmo6y69ex9xvhumansetyfxlla1sh2@muc.south.nextiva.im" id="CB95D15F-4B37-4170-853B-18764B6BCE2D"&gt;
     * &lt;query xmlns="http://jabber.org/protocol/disco#info" node="http://jabber.org/protocol/disco#info"&gt;&lt;/query&gt;
     * &lt;/iq&gt;
     */

    public static IQ getMucDiscoFeatures(String mucJid) {
        IQ iq = new IQ(IQ_CHILD_ELEMENT_QUERY) {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                xml.attribute(IQ_NAMESPACE, CHAT_MUC_DISCO_NAMESPACE);
                xml.attribute(CHAT_MUC_NODE_TAGNAME, CHAT_MUC_DISCO_NAMESPACE);
                xml.rightAngleBracket();

                return xml;
            }
        };

        iq.setType(IQ.Type.get);
        iq.setStanzaId(String.valueOf(GuidUtil.getRandomId()));

        try {
            iq.setTo(JidCreate.bareFrom(mucJid));

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        return iq;
    }

    public static Presence getVCardUpdatePresence() {

        Presence presence = new Presence(Presence.Type.available);
        presence.setPriority(10);
        ExtensionElement extensionElement = new ExtensionElement() {
            @Override
            public String getNamespace() {
                return null;
            }

            @Override
            public String getElementName() {
                return null;
            }

            @Override
            public CharSequence toXML(String namespace) {
                return PRESENCE_VCARD_UPDATE_PROPERTIES;
            }
        };
        presence.addExtension(extensionElement);
        return presence;
    }
}
