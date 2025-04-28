/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FeatureFlagListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SimpleBaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem;
import com.nextiva.nextivaapp.android.models.ChatMessage;
import com.nextiva.nextivaapp.android.models.SmsMessage;

import dagger.hilt.android.AndroidEntryPoint;

@SuppressLint("Registered")
@AndroidEntryPoint
public class BaseMasterListListenerActivity extends BaseActivity implements MasterListListener {

    // --------------------------------------------------------------------------------------------
    // MasterListListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onCallHistoryListItemClicked(@NonNull CallHistoryListItem listItem) {

    }

    @Override
    public void onCallHistoryListItemLongClicked(@NonNull CallHistoryListItem listItem) {

    }

    @Override
    public void onCallHistoryCallButtonClicked(@NonNull CallHistoryListItem listItem) {

    }

    @Override
    public void onContactHeaderListItemClicked(@NonNull HeaderListItem listItem) {

    }

    @Override
    public void onContactHeaderListItemLongClicked(@NonNull HeaderListItem listItem) {

    }

    @Override
    public void onContactListItemClicked(@NonNull ContactListItem listItem) {

    }

    @Override
    public void onContactListItemLongClicked(@NonNull ContactListItem listItem) {

    }

    @Override
    public void onDetailItemViewListItemClicked(@NonNull DetailItemViewListItem listItem) {

    }

    @Override
    public void onDetailItemViewListItemLongClicked(@NonNull DetailItemViewListItem listItem) {

    }

    @Override
    public void onDetailItemViewListItemAction1ButtonClicked(@NonNull DetailItemViewListItem listItem) {

    }

    @Override
    public void onDetailItemViewListItemAction2ButtonClicked(@NonNull DetailItemViewListItem listItem) {

    }

    @Override
    public void onChatConversationItemClicked(@NonNull ChatConversationListItem listItem) {

    }

    @Override
    public void onChatConversationItemLongClicked(@NonNull ChatConversationListItem listItem) {

    }

    @Override
    public void onResendFailedChatMessageClicked(@NonNull SimpleBaseListItem<ChatMessage> listItem) {

    }

    @Override
    public void onResendFailedSmsMessageClicked(@NonNull SimpleBaseListItem<SmsMessage> listItem) {

    }

    @Override
    public void onChatMessageListItemDatetimeVisibilityToggled(@NonNull SimpleBaseListItem<ChatMessage> listItem) {

    }

    @Override
    public void onVoicemailCallButtonClicked(@NonNull VoicemailListItem listItem) {

    }

    @Override
    public void onVoicemailReadButtonClicked(@NonNull VoicemailListItem listItem) {

    }

    @Override
    public void onVoicemailDeleteButtonClicked(@NonNull VoicemailListItem listItem) {

    }

    @Override
    public void onVoicemailContactButtonClicked(@NonNull VoicemailListItem listItem) {

    }

    @Override
    public void onVoicemailSmsButtonClicked(@NonNull VoicemailListItem listItem) {

    }

    @Override
    public void onSmsConversationItemClicked(@NonNull MessageListItem listItem) {

    }

    @Override
    public void onSmsMessageListItemDatetimeVisibilityToggled(@NonNull SmsMessageListItem listItem) {

    }

    @Override
    public void onConnectContactHeaderListItemClicked(@NonNull ConnectContactHeaderListItem listItem) {

    }

    @Override
    public void onPositiveRatingItemClicked(@NonNull VoicemailListItem voicemailListItem) {

    }

    @Override
    public void onNegativeRatingItemClicked(@NonNull VoicemailListItem voicemailListItem) {

    }


    @Override
    public void onConnectContactFavoriteIconClicked(@NonNull ConnectContactListItem listItem) {

    }

    @Override
    public void onConnectContactListItemClicked(@NonNull ConnectContactListItem listItem) {

    }

    @Override
    public void onConnectContactDetailHeaderListItemClicked(@NonNull ConnectContactDetailHeaderListItem listItem) {

    }

    @Override
    public void onConnectContactDetailListItemClicked(@NonNull ConnectContactDetailListItem listItem) {

    }

    @Override
    public void onConnectContactCategoryItemClicked(@NonNull ConnectContactCategoryListItem listItem) {

    }

    @Override
    public void onConnectHomeListItemClicked(@NonNull ConnectHomeListItem listITem) {

    }

    @Override
    public void onFeatureFlagListItemChecked(@NonNull FeatureFlagListItem listItem) {

    }


    @Override
    public void onConnectContactListItemLongClicked(@NonNull ConnectContactListItem listItem) {

    }

    @Override
    public void onDialogContactActionHeaderListItemClicked(@NonNull DialogContactActionHeaderListItem listItem) {

    }

    @Override
    public void onDialogContactActionListItemClicked(@NonNull DialogContactActionListItem listItem) {

    }

    @Override
    public void onDialogContactActionDetailListItemClicked(@NonNull DialogContactActionDetailListItem listItem) {

    }

    // --------------------------------------------------------------------------------------------
}
