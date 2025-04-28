/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.nextiva.nextivaapp.android.managers.interfaces.ChatManager;
import com.nextiva.nextivaapp.android.models.ChatConversation;
import com.nextiva.nextivaapp.android.models.NextivaContact;

/**
 * Created by Thaddeus Dannar on 4/25/18.
 */

@Singleton
public class NextivaChatManager implements ChatManager {

    private static ArrayList<ChatConversation> sChatConversationsList;

    @Inject
    public NextivaChatManager() {
    }

    // --------------------------------------------------------------------------------------------
    // ChatManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public ArrayList<ChatConversation> getChatConversationsList() {
        return sChatConversationsList;
    }

    @Override
    public ChatConversation getChatConversation(@NonNull NextivaContact nextivaContact) {
        if (!TextUtils.isEmpty(nextivaContact.getJid()) &&
                sChatConversationsList != null &&
                sChatConversationsList.size() > 0) {

            for (ChatConversation chat : sChatConversationsList) {
                for (String member : chat.getMembersList()) {
                    if (member.contains(nextivaContact.getJid())) {
                        return chat;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void setChatConversationsList(@NonNull ArrayList<ChatConversation> chatConversationsList) {
        sChatConversationsList = chatConversationsList;
    }

    @Override
    public byte[] getAttachmentDataByteArray(String urlString, String sessionId, String corpAcctNumber) {
        try {
            URL url = new URL(urlString);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("x-api-key", sessionId);
            conn.setRequestProperty("nextiva-context-corpAcctNumber", corpAcctNumber);


            try (InputStream inputStream = conn.getInputStream()) {
                int n;
                byte[] buffer = new byte[1024];
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            }
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // --------------------------------------------------------------------------------------------
}
