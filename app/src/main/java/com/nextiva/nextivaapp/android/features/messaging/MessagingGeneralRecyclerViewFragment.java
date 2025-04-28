/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.messaging;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem;
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment;

public abstract class MessagingGeneralRecyclerViewFragment extends GeneralRecyclerViewFragment implements MessagingMasterListListener {

    @Override
    public void onSmsConversationItemClicked(@NonNull MessageListItem listItem) {
        if (mMasterListListener != null) {
            ((MessagingGeneralRecyclerViewFragment) mMasterListListener).onSmsConversationItemClicked(listItem);
        }
    }

    @Override
    public void onSmsConversationLongItemClicked(@NonNull MessageListItem listItem) {
        if (mMasterListListener != null) {
            ((MessagingGeneralRecyclerViewFragment)mMasterListListener).onSmsConversationLongItemClicked(listItem);
        }
    }

    @Override
    public void onShortSwipe(@NonNull MessageListItem listItem) {
        if (mMasterListListener != null) {
            ((MessagingGeneralRecyclerViewFragment)mMasterListListener).onShortSwipe(listItem);
        }
    }
    @Override
    public void onSwipedSmsConversationItemDelete(@NonNull MessageListItem listItem) {
        if (mMasterListListener != null) {
            ((MessagingGeneralRecyclerViewFragment)mMasterListListener).onSwipedSmsConversationItemDelete(listItem);
        }
    }
    @Override
    public void onSwipedSmsConversationItemMarkAsReadOrUnread(@NonNull MessageListItem listItem) {
        if (mMasterListListener != null) {
            ((MessagingGeneralRecyclerViewFragment)mMasterListListener).onSwipedSmsConversationItemMarkAsReadOrUnread(listItem);
        }
    }
}
