/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.di.modules;

import com.nextiva.nextivaapp.android.xmpp.managers.NextivaXMPPChatManager;
import com.nextiva.nextivaapp.android.xmpp.managers.NextivaXMPPPresenceManager;
import com.nextiva.nextivaapp.android.xmpp.managers.NextivaXMPPPubSubManager;
import com.nextiva.nextivaapp.android.xmpp.managers.NextivaXMPPRosterManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPChatManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPresenceManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPubSubManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPRosterManager;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.android.scopes.ServiceScoped;

/**
 * Created by joedephillipo on 3/30/18.
 */

@Module
@InstallIn(ServiceComponent.class)
public class XmppModule {

    @Provides
    @ServiceScoped
    XMPPRosterManager providesXMPPRosterManager(NextivaXMPPRosterManager xmppRosterManager) {
        return xmppRosterManager;
    }

    @Provides
    @ServiceScoped
    XMPPPresenceManager providesXMPPPresenceManager(NextivaXMPPPresenceManager xmppPresenceManager) {
        return xmppPresenceManager;
    }

    @Provides
    @ServiceScoped
    XMPPPubSubManager providesXMPPPubSubManager(NextivaXMPPPubSubManager xmppPubSubManager) {
        return xmppPubSubManager;
    }

    @Provides
    @ServiceScoped
    XMPPChatManager providesXMPPChatManager(NextivaXMPPChatManager xmppChatManager) {
        return xmppChatManager;
    }
}
