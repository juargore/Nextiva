/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.di.modules;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;

@Module
@InstallIn(ServiceComponent.class)
public class TestXmppModule {

   /* @Provides
    @ServiceScoped
    XMPPRosterManager providesXMPPRosterManager() {
        return Mockito.mock(XMPPRosterManager.class);
    }

    @Provides
    @ServiceScoped
    XMPPPresenceManager providesXMPPPresenceManager() {
        return Mockito.mock(XMPPPresenceManager.class);
    }

    @Provides
    @ServiceScoped
    XMPPPubSubManager providesXMPPPubSubManager() {
        return Mockito.mock(XMPPPubSubManager.class);
    }

    @Provides
    @ServiceScoped
    XMPPChatManager providesXMPPChatManager() {
        return Mockito.mock(XMPPChatManager.class);
    }*/
}