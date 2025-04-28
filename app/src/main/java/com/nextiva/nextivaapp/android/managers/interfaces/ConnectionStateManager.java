package com.nextiva.nextivaapp.android.managers.interfaces;

import androidx.lifecycle.LiveData;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager;

public interface ConnectionStateManager {
    LiveData<Integer> getXmppConnectionStateLiveData();

    LiveData<Integer> getInternetConnectionStateLiveData();

    LiveData<Boolean> getUMSConnectionStateLiveData();

    LiveData<Boolean> getSIPConnectionStateLiveData();

    void postXmppConnectionState(@Enums.Xmpp.ConnectionStates.ConnectionState int connectionState);

    void postIsUMSConnected(boolean connected);

    void postIsConnectWebSocketConnected(boolean connected);

    boolean isXmppConnected();

    boolean isConnectWebSocketConnected();

    boolean isInternetConnected();

    String getInternetConnectionType();

    boolean hasIpv6Address();

    boolean isUMSConnected();

    boolean isSipConnected();

    boolean isCallActive();

    void updateSipRegistration(boolean isSipRegistered);

    void setSipManager(PJSipManager sipManager);

    void setWebSocketManager(WebSocketManager webSocketManager);
}
