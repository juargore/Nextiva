/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.messaging;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem;

public interface MessagingMasterListListener extends MasterListListener {

    void onSmsConversationItemClicked(@NonNull MessageListItem listItem);

    void onSmsConversationLongItemClicked(@NonNull MessageListItem listItem);

    void onShortSwipe(@NonNull MessageListItem listItem);

    void onSwipedSmsConversationItemDelete(@NonNull MessageListItem itemSwipedState);

    void onSwipedSmsConversationItemMarkAsReadOrUnread(@NonNull MessageListItem listItem);
}
