/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.util;

/**
 * Created by joedephillipo on 3/8/18.
 */

public class NextivaXMPPConstants {

    public static final String IQ_CHILD_ELEMENT_QUERY = "query";
    public static final String IQ_CHILD_ELEMENT_BIND = "bind";
    public static final String IQ_CHILD_ELEMENT_PUBSUB = "pubsub";
    public static final String IQ_CHILD_ELEMENT_PING = "ping";
    public static final String IQ_NAMESPACE = "xmlns";

    public static final String JID_SUFFIX = ".im";
    public static final String HILLBILLY_SMS_JID_RESOURCE = "@smsnextiva.com";

    public static final String PUBSUB_IQ_NAMESPACE_PROTOCOLS_PUBSUB = "http://jabber.org/protocol/pubsub";

    public static final String CHAT_STATES_NAMESPACE = "http://jabber.org/protocol/chatstates";

    public static final String PING_NAMESPACE = "urn:xmpp:ping";

    public static final String BIND_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-bind";
    public static final String BIND_RESOURCE_TAGNAME = "resource";

    public static final String VCARD_UPDATE_VALUE = "vcard_update";
    public static final String VCARD_MIME_TYPE = "image/png";

    public static final String CONTACTS_IQ_NAMESPACE_BSFT_PRESENCE_ON_DEMAND = "urn:xmpp:broadsoft:presenceondemand";

    public static final String PUBSUB_IQ_PRESENCE_STATUS_TEXT = "presence-status-text-";
    public static final String PUBSUB_IQ_CONTACT_STORAGE_TEXT = "contact-storage-update-";
    public static final String PUBSUB_IQ_DATA_ITEM_TEXT = "data-item";
    public static final String PUBSUB_IQ_PUBLISH_TAGNAME = "publish";
    public static final String PUBSUB_IQ_SUBSCRIBE_TAGNAME = "subscribe";
    public static final String PUBSUB_IQ_ID_TAGNAME = "id";
    public static final String PUBSUB_IQ_NODE_TAGNAME = "node";
    public static final String PUBSUB_IQ_PRESENCE_STORAGE_TAGNAME = "presencestorage";
    public static final String PUBSUB_IQ_PRESENCE_TAGNAME = "presence";
    public static final String PUBSUB_IQ_ITEM_TAGNAME = "item";
    public static final String PUBSUB_IQ_IQ_TAGNAME = "iq";
    public static final String PUBSUB_IQ_TYPE_TAGNAME = "type";
    public static final String PUBSUB_IQ_ERROR_TAGNAME = "error";
    public static final String PUBSUB_IQ_ITEMS_TAGNAME = "items";
    public static final String PUBSUB_IQ_JID_TAGNAME = "jid";
    public static final String PUBSUB_IQ_LAST_UPDATE_TAGNAME = "last-update";

    public static final String CONTACTS_IQ_BW_USERID_TAGNAME = "bw_userid";
    public static final String CONTACTS_IQ_JID_TYPE = "jid";
    public static final String CONTACTS_IQ_EXTENSION_TYPE = "extension_number";
    public static final String CONTACTS_IQ_HOME_PHONE_TYPE = "home_phone";
    public static final String CONTACTS_IQ_MOBILE_PHONE_TYPE = "mobile_phone";
    public static final String CONTACTS_IQ_PERSONAL_PHONE_TYPE = "personal_phone";
    public static final String CONTACTS_IQ_WORK_PHONE_TYPE = "work_phone";
    public static final String CONTACTS_IQ_CUSTOM_PHONE_TYPE = "custom_phone";
    public static final String CONTACTS_IQ_PAGER_TYPE = "pager";
    public static final String CONTACTS_IQ_CONFERENCE_NUMBER_TYPE = "conference_number";

    public static final String PRESENCE_IQ_STATUS_TAGNAME = "status";
    public static final String PRESENCE_IQ_PROPERTIES_ELEMENT = "<properties xmlns=\"http://www.jivesoftware.com/xmlns/xmpp/properties\"><property><name>manual</name><value type=\"string\">true</value></property></properties>";

    public static final String PRESENCE_IQ_PRIORITY_OPEN_ELEMENT = "<priority xmlns:stream='http://etherx.jabber.org/streams'>";
    public static final String PRESENCE_IQ_SHOW_OPEN_ELEMENT = "<show xmlns:stream='http://etherx.jabber.org/streams'>";
    public static final String PRESENCE_IQ_FREETEXT_OPEN_ELEMENT = "<freeText xmlns:stream='http://etherx.jabber.org/streams'>";
    public static final String PRESENCE_IQ_PRIORITY_CLOSE_ELEMENT = "</priority>";
    public static final String PRESENCE_IQ_SHOW_CLOSE_ELEMENT = "</show>";
    public static final String PRESENCE_IQ_FREETEXT_CLOSE_ELEMENT = "</freeText>";

    public static final String CHAT_UNIQUE_ID_TAGNAME = "unique";
    public static final String CHAT_MUC_UNIQUE_SEPARATOR = "-unique-";
    public static final String CHAT_UNIQUE_ID_NAMESPACE = "http://jabber.org/protocol/muc#unique";
    public static final String CHAT_MUC_PREFIX = "muc.";
    public static final String CHAT_MUC_RESOURCE_PREFIX = "@muc.";
    public static final String CHAT_MUC_DISCO_NAMESPACE = "http://jabber.org/protocol/disco#info";
    public static final String CHAT_MUC_NODE_TAGNAME = "node";
    public static final String CHAT_MUC_INVITATION_NAMESPACE = "jabber:x:conference";
    public static final String CHAT_MUC_USS_SHARE = "<uss-share>";

    public static final String PUBSUB_FEATURE_JABBER_PUBSUB = "http://jabber.org/protocol/pubsub";
    public static final String PUBSUB_FEATURE_JABBER_PROTOCOL = "http://jabber.org/protocol/cap";
    public static final String PUBSUB_FEATURE_URN_PING = "urn:xmpp:ping";
    public static final String PUBSUB_FEATURE_VCARD_TEMP = "vcard-temp";
    public static final String PUBSUB_FEATURE_JABBER_IQ_VERSION = "jabber:iq:version";
    public static final String PUBSUB_FEATURE_URN_TIME = "urn:xmpp:time";
    public static final String PUBSUB_FEATURE_JABBER_PROTOCOL_DISCO = "http://jabber.org/protocol/disco#info";

    public static final String PRESENCE_VCARD_UPDATE_PROPERTIES = "<properties xmlns=\"http://www.jivesoftware.com/xmlns/xmpp/properties\"><property><name>event</name><value type=\"string\">vcard_update</value></property></properties>";

    public static final String CHAT_STATE_COMPOSING_EXTENSION_ELEMENT = "<composing xmlns=\"http://jabber.org/protocol/chatstates\" />";
    public static final String CHAT_STATE_ACTIVE_EXTENSION_ELEMENT = "<active xmlns=\"http://jabber.org/protocol/chatstates\" />";
    public static final String CHAT_STATE_GONE_EXTENSION_ELEMENT = "<gone xmlns=\"http://jabber.org/protocol/chatstates\" />";
}
