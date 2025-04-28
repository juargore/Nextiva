/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers.interfaces;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by joedephillipo on 4/2/18.
 */

public interface XMPPPubSubManager {

    void init(XMPPTCPConnection connection);

    void setPubSubPresenceStatusText(String username, String serviceName, String status);

    void getPubSubPresenceStatusText(String username, String serviceName, String fullJid);

    void subscribeToPubSub(String username, String serviceName, String fullJid);

}
