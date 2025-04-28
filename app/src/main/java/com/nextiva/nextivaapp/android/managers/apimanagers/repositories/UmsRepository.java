/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers.repositories;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbGroup;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.models.ChatMessage;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.RegisterDeviceResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.UnregisterDeviceResponseEvent;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by adammacdonald on 3/7/18.
 */

public interface UmsRepository {

    boolean isClientSetup();

    void setDeviceFinishedRegistering();

    Single<RegisterDeviceResponseEvent> registerDevice();

    Single<UnregisterDeviceResponseEvent> unregisterDevice();

    Single<ChatConversationsResponseEvent> getChatConversations(long timeStamp);

    Single<Boolean> getVCards(@NonNull ArrayList<NextivaContact> nextivaContacts, @NonNull final CompositeDisposable compositeDisposable);

    Single<Boolean> markAllMessagesRead(@NonNull final CompositeDisposable compositeDisposable);

    Single<Boolean> markMessagesReadFromSender(@NonNull final CompositeDisposable compositeDisposable, @NonNull String jid);

    Single<Boolean> getVCard(@NonNull String jid, @NonNull final CompositeDisposable compositeDisposable);

    Single<Boolean> setVCard(byte[] vcardData);

    Single<ArrayList<NextivaContact>> getContactStorage();

    Single<Boolean> setContactStorageWithContacts(ArrayList<NextivaContact> contacts);

    Single<Boolean> setContactStorageWithGroups(ArrayList<DbGroup> groups);

    Single<Boolean> setContactStorage(ArrayList<NextivaContact> contacts, ArrayList<DbGroup> groups);

    Single<ArrayList<DbPresence>> getOnDemandPresences(ArrayList<String> jids);

    Single<Boolean> getSuperPresence(CompositeDisposable compositeDisposable);

    Single<Boolean> sendPresenceAvailability(DbPresence dbPresence, CompositeDisposable compositeDisposable);

    Single<Boolean> deletePresenceAvailability(CompositeDisposable compositeDisposable);

    Single<Boolean> sendPresenceStatus(String status, CompositeDisposable compositeDisposable);

    Single<Boolean> deletePresenceStatus(CompositeDisposable compositeDisposable);

    void resendChatMessage(ChatMessage chatMessage);

    void sendChatMessage(String jid, String chatMessage, @Enums.Chats.ConversationTypes.Type String chatType);

    void sendChatMessage(String jid, String chatMessage, @Enums.Chats.ConversationTypes.Type String chatType, String threadId, ArrayList<String> jidList);
}
