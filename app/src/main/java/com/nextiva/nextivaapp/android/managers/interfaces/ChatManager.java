/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.ChatConversation;
import com.nextiva.nextivaapp.android.models.NextivaContact;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 4/30/18.
 */
public interface ChatManager {

    @Nullable
    ArrayList<ChatConversation> getChatConversationsList();

    @Nullable
    ChatConversation getChatConversation(@NonNull NextivaContact nextivaContact);

    void setChatConversationsList(@NonNull ArrayList<ChatConversation> chatConversationsList);

    byte[] getAttachmentDataByteArray(String urlString, String sessionId, String corpAcctNumber);
}
