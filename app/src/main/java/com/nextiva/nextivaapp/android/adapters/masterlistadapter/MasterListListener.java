/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter;

import androidx.annotation.NonNull;

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

/**
 * Created by adammacdonald on 2/8/18.
 */

public interface MasterListListener {

    void onCallHistoryListItemClicked(@NonNull CallHistoryListItem listItem);

    void onCallHistoryListItemLongClicked(@NonNull CallHistoryListItem listItem);

    void onCallHistoryCallButtonClicked(@NonNull CallHistoryListItem listItem);

    void onContactHeaderListItemClicked(@NonNull HeaderListItem listItem);

    void onContactHeaderListItemLongClicked(@NonNull HeaderListItem listItem);

    void onContactListItemClicked(@NonNull ContactListItem listItem);

    void onContactListItemLongClicked(@NonNull ContactListItem listItem);

    void onDetailItemViewListItemClicked(@NonNull DetailItemViewListItem listItem);

    void onDetailItemViewListItemLongClicked(@NonNull DetailItemViewListItem listItem);

    void onDetailItemViewListItemAction1ButtonClicked(@NonNull DetailItemViewListItem listItem);

    void onDetailItemViewListItemAction2ButtonClicked(@NonNull DetailItemViewListItem listItem);

    void onChatConversationItemClicked(@NonNull ChatConversationListItem listItem);

    void onChatConversationItemLongClicked(@NonNull ChatConversationListItem listItem);

    void onResendFailedChatMessageClicked(@NonNull SimpleBaseListItem<ChatMessage> listItem);

    void onResendFailedSmsMessageClicked(@NonNull SimpleBaseListItem<SmsMessage> listItem);

    void onChatMessageListItemDatetimeVisibilityToggled(@NonNull SimpleBaseListItem<ChatMessage> listItem);

    void onVoicemailCallButtonClicked(@NonNull VoicemailListItem listItem);

    void onVoicemailReadButtonClicked(@NonNull VoicemailListItem listItem);

    void onVoicemailDeleteButtonClicked(@NonNull VoicemailListItem listItem);

    void onVoicemailContactButtonClicked(@NonNull VoicemailListItem listItem);

    void onVoicemailSmsButtonClicked(@NonNull VoicemailListItem listItem);

    void onSmsConversationItemClicked(@NonNull MessageListItem listItem);

    void onSmsMessageListItemDatetimeVisibilityToggled(@NonNull SmsMessageListItem listItem);

    void onConnectContactHeaderListItemClicked(@NonNull ConnectContactHeaderListItem listItem);

    void onConnectContactFavoriteIconClicked(@NonNull ConnectContactListItem listItem);

    void onPositiveRatingItemClicked(@NonNull VoicemailListItem voicemailListItem);

    void onNegativeRatingItemClicked(@NonNull VoicemailListItem voicemailListItem);

    void onConnectContactListItemClicked(@NonNull ConnectContactListItem listItem);

    void onConnectContactDetailHeaderListItemClicked(@NonNull ConnectContactDetailHeaderListItem listItem);

    void onConnectContactCategoryItemClicked(@NonNull ConnectContactCategoryListItem listItem);

    void onConnectContactDetailListItemClicked(@NonNull ConnectContactDetailListItem listItem);

    void onConnectHomeListItemClicked(@NonNull ConnectHomeListItem listItem);

    void onFeatureFlagListItemChecked(@NonNull FeatureFlagListItem listItem);

    void onConnectContactListItemLongClicked(@NonNull ConnectContactListItem listItem);

    void onDialogContactActionHeaderListItemClicked(@NonNull DialogContactActionHeaderListItem listItem);

    void onDialogContactActionListItemClicked(@NonNull DialogContactActionListItem listItem);

    void onDialogContactActionDetailListItemClicked(@NonNull DialogContactActionDetailListItem listItem);
}
