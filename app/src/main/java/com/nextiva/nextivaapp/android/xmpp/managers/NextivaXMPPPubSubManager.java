/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.FormatterManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.xmpp.iqpackets.PresenceStatusTextGetIQ;
import com.nextiva.nextivaapp.android.xmpp.iqpackets.PresenceStatusTextResultIQ;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPubSubManager;
import com.nextiva.nextivaapp.android.xmpp.util.IQBuilderUtil;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PubSubException;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by joedephillipo on 4/2/18.
 */

public class NextivaXMPPPubSubManager implements XMPPPubSubManager {

    private XMPPTCPConnection mConnection;

    private final LogManager mLogManager;
    private final CalendarManager mCalendarManager;
    private final DbManager mDbManager;

    @Inject
    public NextivaXMPPPubSubManager(
            LogManager logManager,
            CalendarManager calendarManager,
            DbManager dbManager) {

        mLogManager = logManager;
        mCalendarManager = calendarManager;
        mDbManager = dbManager;
    }

    private void initFeatures() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(mConnection);
        manager.addFeature(NextivaXMPPConstants.PUBSUB_FEATURE_JABBER_PUBSUB);
        manager.addFeature(NextivaXMPPConstants.PUBSUB_FEATURE_JABBER_PROTOCOL);
        manager.addFeature(NextivaXMPPConstants.PUBSUB_FEATURE_URN_PING);
        manager.addFeature(NextivaXMPPConstants.PUBSUB_FEATURE_VCARD_TEMP);
        manager.addFeature(NextivaXMPPConstants.PUBSUB_FEATURE_JABBER_IQ_VERSION);
        manager.addFeature(NextivaXMPPConstants.PUBSUB_FEATURE_URN_TIME);
        manager.addFeature(NextivaXMPPConstants.PUBSUB_FEATURE_JABBER_PROTOCOL_DISCO);

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    private void initPubSubListeners(String username, final String serviceName) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            PubSubManager pubSubManager = PubSubManager.getInstance(mConnection, JidCreate.bareFrom("pubsub." + serviceName));

            try {
                pubSubManager.getNode(NextivaXMPPConstants.PUBSUB_IQ_PRESENCE_STATUS_TEXT + username.toLowerCase() + "-" + serviceName);
            } catch (PubSubException.NotAPubSubNodeException e) {
                mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
                pubSubManager.createNode(NextivaXMPPConstants.PUBSUB_IQ_PRESENCE_STATUS_TEXT + username.toLowerCase() + "-" + serviceName);
            }

            final Node presenceStatusTextNode = pubSubManager.getNode(NextivaXMPPConstants.PUBSUB_IQ_PRESENCE_STATUS_TEXT + username.toLowerCase() + "-" + serviceName);

            presenceStatusTextNode.addItemEventListener(items -> {
                String status = null;
                Pattern pattern = Pattern.compile("<status xmlns='http://jabber.org/protocol/pubsub#event'>(.*?)</status>");
                Matcher matcher = pattern.matcher(items.getItems().toString());
                while (matcher.find()) {
                    status = matcher.group(1);
                    mDbManager.updateCurrentUserStatus(status);
                }

                if (TextUtils.isEmpty(status)) {
                    mDbManager.updateCurrentUserStatus(null);
                }

                mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
            });

        } catch (XmppStringprepException |
                SmackException.NoResponseException |
                PubSubException.NotAPubSubNodeException |
                SmackException.NotConnectedException |
                XMPPException.XMPPErrorException |
                InterruptedException exception) {

            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());
        }

        // Pubsub implementation of contact storage updates.  Since these have been inconsistent in the past we are moving this to push notifications.

//        mConnection.addAsyncStanzaListener(
//                packet -> {
//                        Message pubSubMessage = (Message) packet;
//
//                        if(pubSubMessage.getExtension(NextivaXMPPConstants.PUBSUB_EVENT_NAMESPACE) != null) {
//                            EventElement eventElement = (EventElement) pubSubMessage.getExtension(NextivaXMPPConstants.PUBSUB_EVENT_NAMESPACE);
//
//                            if (eventElement.getEvent().getNode().contains(NextivaXMPPConstants.PUBSUB_IQ_CONTACT_STORAGE_TEXT)) {
//                                mXMPPConnectionActionManager.refreshRoster(true);
//                            }
//                        }
//                }, stanza -> stanza instanceof Message &&
//                                stanza.getFrom() != null &&
//                                stanza.getFrom().toString().equals(NextivaXMPPConstants.PUBSUB_FROM_PREFIX + mConnection.getUser().getDomain().toString()));
    }

    @NonNull
    private String getNewIQTimestamp(String resource) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        return FormatterManager.getInstance().getDateFormatter_8601ExtendedDatetimeTimeZoneNoMs()
                .format(mCalendarManager.getNowInstant()) + " " + resource;
    }

    // --------------------------------------------------------------------------------------------
    // XMPPPubSubManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void init(XMPPTCPConnection connection) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start_with_message, R.string.log_message_init);


        mConnection = connection;

        initFeatures();

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void setPubSubPresenceStatusText(String username, String serviceName, String status) {

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            mConnection.sendStanza(IQBuilderUtil.setPubSubStatusTextIQ(username.toLowerCase(), serviceName, getNewIQTimestamp(username + "@" + serviceName), status));

            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);

        } catch (SmackException.NotConnectedException | InterruptedException exception) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());
        }
    }

    @Override
    public void getPubSubPresenceStatusText(final String username, final String serviceName, final String fullJid) {

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            mConnection.sendIqWithResponseCallback(
                    new PresenceStatusTextGetIQ(username, serviceName, fullJid),
                    packet -> {
                        if (packet instanceof PresenceStatusTextResultIQ) {
                            mDbManager.updateCurrentUserStatus(((PresenceStatusTextResultIQ) packet).getPresenceStatusText());
                        }
                    });

            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);

        } catch (SmackException.NotConnectedException | InterruptedException exception) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());
        }
    }

    @Override
    public void subscribeToPubSub(String username, String serviceName, String fullJid) {

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            mConnection.sendStanza(IQBuilderUtil.subscribeToPubSubStatusTextIQ(username.toLowerCase(), serviceName, fullJid));
            mConnection.sendStanza(IQBuilderUtil.subscribeToPubSubContactStorageIQ(username.toLowerCase(), serviceName, fullJid));

            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);

        } catch (SmackException.NotConnectedException | InterruptedException exception) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());
        }

        initPubSubListeners(username, serviceName);
    }
    // --------------------------------------------------------------------------------------------
}