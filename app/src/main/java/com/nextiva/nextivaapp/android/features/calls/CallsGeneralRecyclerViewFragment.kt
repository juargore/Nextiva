package com.nextiva.nextivaapp.android.features.calls

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallHistoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment

abstract class CallsGeneralRecyclerViewFragment : GeneralRecyclerViewFragment(),
    CallsMasterListListener {
    override fun onConnectCallHistoryListItemClicked(
        listItem: ConnectCallHistoryListItem,
        position: Int
    ) {
        if (mMasterListListener != null) {
            (mMasterListListener as? CallsGeneralRecyclerViewFragment)?.onConnectCallHistoryListItemClicked(listItem, position)
        }
    }

    override fun onVoicemailListItemClicked(listItem: VoicemailListItem, position: Int) {
        if (mMasterListListener != null) {
            (mMasterListListener as? CallsGeneralRecyclerViewFragment)?.onVoicemailListItemClicked(listItem, position)
        }
    }

    override fun onCallHistorySwipedItemDelete(listItem: ConnectCallHistoryListItem) {
        if (mMasterListListener != null) {
            (mMasterListListener as? CallsMasterListListener)?.onCallHistorySwipedItemDelete(listItem)
        }
    }

    override fun onCallHistorySwipedItemMarkAsReadOrUnread(listItem: ConnectCallHistoryListItem) {
        if (mMasterListListener != null) {
            (mMasterListListener as? CallsMasterListListener)?.onCallHistorySwipedItemMarkAsReadOrUnread(listItem)
        }
    }

    override fun onShortSwipe(listItem: BaseListItem) {
        if (mMasterListListener != null) {
            (mMasterListListener as? CallsMasterListListener)?.onShortSwipe(listItem)
        }
    }

    override fun onVoicemailSwipedDeleteItem(listItem: VoicemailListItem) {
        if (mMasterListListener != null) {
            (mMasterListListener as? CallsMasterListListener)?.onVoicemailSwipedDeleteItem(listItem)
        }
    }

    override fun onVoicemailSwipedItemMarkAsReadOrUnread(listItem: VoicemailListItem) {
        if (mMasterListListener != null) {
            (mMasterListListener as? CallsMasterListListener)?.onVoicemailSwipedItemMarkAsReadOrUnread(listItem)
        }
    }
}