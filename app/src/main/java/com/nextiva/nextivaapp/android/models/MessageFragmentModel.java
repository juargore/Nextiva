package com.nextiva.nextivaapp.android.models;

public class MessageFragmentModel {

    public boolean isChatListRefreshStopped() {
        return chatListRefreshStopped;
    }

    public void setChatListRefreshStopped(boolean chatListRefreshStopped) {
        this.chatListRefreshStopped = chatListRefreshStopped;
    }

    public boolean isSmsListRefreshStopped() {
        return smsListRefreshStopped;
    }

    public void setSmsListRefreshStopped(boolean smsListRefreshStopped) {
        this.smsListRefreshStopped = smsListRefreshStopped;
    }

    boolean chatListRefreshStopped;
    boolean smsListRefreshStopped;
}
