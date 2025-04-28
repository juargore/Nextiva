package com.nextiva.nextivaapp.android.models;

import java.util.ArrayList;

public class ChatIntentModel {
    String chatType;
    ArrayList<String> participants;
    String groupValue;
    Boolean isNewChat;
    String toJid;
    ArrayList<String> jidList;
    Boolean isShouldRefresh;

    public ChatIntentModel(String chatType, String toJid, ArrayList<String> jidList, ArrayList<String> participants, String groupValue, Boolean isNewChat, Boolean isShouldRefresh) {
        this.chatType = chatType;
        this.participants = participants;
        this.groupValue = groupValue;
        this.isNewChat = isNewChat;
        this.toJid = toJid;
        this.jidList = jidList;
        this.isShouldRefresh = isShouldRefresh;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public String getGroupValue() {
        return groupValue;
    }

    public void setGroupValue(String groupValue) {
        this.groupValue = groupValue;
    }

    public Boolean getNewChat() {
        return isNewChat;
    }

    public void setNewChat(Boolean newChat) {
        isNewChat = newChat;
    }

    public String getToJid() {
        return toJid;
    }

    public void setToJid(String toJid) {
        this.toJid = toJid;
    }

    public ArrayList<String> getJidList() {
        return jidList;
    }

    public void setJidList(ArrayList<String> jidList) {
        this.jidList = jidList;
    }

    public Boolean getShouldRefresh() {
        return isShouldRefresh;
    }

    public void setShouldRefresh(Boolean shouldRefresh) {
        isShouldRefresh = shouldRefresh;
    }
}
