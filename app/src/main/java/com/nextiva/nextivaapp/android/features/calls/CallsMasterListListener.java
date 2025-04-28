package com.nextiva.nextivaapp.android.features.calls;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallHistoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem;

public interface CallsMasterListListener extends MasterListListener {

    void onConnectCallHistoryListItemClicked(@NonNull ConnectCallHistoryListItem listItem, int position);

    void onVoicemailListItemClicked(@NonNull VoicemailListItem listItem, int position);

    void onCallHistorySwipedItemDelete(@NonNull ConnectCallHistoryListItem listItem);

    void onCallHistorySwipedItemMarkAsReadOrUnread(@NonNull ConnectCallHistoryListItem listItem);

    void onShortSwipe(@NonNull BaseListItem listItem);

    void onVoicemailSwipedItemMarkAsReadOrUnread(@NonNull VoicemailListItem listItem);

    void onVoicemailSwipedDeleteItem(@NonNull VoicemailListItem listItem);
}
