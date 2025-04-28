package com.nextiva.nextivaapp.android.dialogs

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FeatureFlagListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SimpleBaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.models.ChatMessage
import com.nextiva.nextivaapp.android.models.SmsMessage

open class BaseDialog: DialogFragment(), MasterListListener {

    var adapter: MasterListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ConnectDialog)
        adapter = MasterListAdapter(requireContext(), ArrayList(), this, null, null, null, null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            dialog.window?.let {
                dialog.window?.setLayout((getWindowWidth() * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }

        return dialog
    }

    private fun getWindowWidth(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            windowMetrics.bounds.width()

        } else {
            val displayMetrics = DisplayMetrics()
            val display = requireActivity().windowManager.defaultDisplay
            display.getRealMetrics(displayMetrics)

            displayMetrics.widthPixels
        }
    }

    override fun onCallHistoryListItemClicked(listItem: CallHistoryListItem) {}
    override fun onCallHistoryListItemLongClicked(listItem: CallHistoryListItem) {}
    override fun onCallHistoryCallButtonClicked(listItem: CallHistoryListItem) {}
    override fun onContactHeaderListItemClicked(listItem: HeaderListItem) {}
    override fun onContactHeaderListItemLongClicked(listItem: HeaderListItem) {}
    override fun onContactListItemClicked(listItem: ContactListItem) {}
    override fun onContactListItemLongClicked(listItem: ContactListItem) {}
    override fun onDetailItemViewListItemClicked(listItem: DetailItemViewListItem) {}
    override fun onDetailItemViewListItemLongClicked(listItem: DetailItemViewListItem) {}
    override fun onDetailItemViewListItemAction1ButtonClicked(listItem: DetailItemViewListItem) {}
    override fun onDetailItemViewListItemAction2ButtonClicked(listItem: DetailItemViewListItem) {}
    override fun onChatConversationItemClicked(listItem: ChatConversationListItem) {}
    override fun onChatConversationItemLongClicked(listItem: ChatConversationListItem) {}
    override fun onResendFailedChatMessageClicked(listItem: SimpleBaseListItem<ChatMessage>) {}
    override fun onResendFailedSmsMessageClicked(listItem: SimpleBaseListItem<SmsMessage>) {}
    override fun onChatMessageListItemDatetimeVisibilityToggled(listItem: SimpleBaseListItem<ChatMessage>) {}
    override fun onVoicemailCallButtonClicked(listItem: VoicemailListItem) {}
    override fun onVoicemailReadButtonClicked(listItem: VoicemailListItem) {}
    override fun onVoicemailDeleteButtonClicked(listItem: VoicemailListItem) {}
    override fun onVoicemailContactButtonClicked(listItem: VoicemailListItem) {}
    override fun onVoicemailSmsButtonClicked(listItem: VoicemailListItem) {}
    override fun onSmsConversationItemClicked(listItem: MessageListItem) {}
    override fun onSmsMessageListItemDatetimeVisibilityToggled(listItem: SmsMessageListItem) {}
    override fun onConnectContactHeaderListItemClicked(listItem: ConnectContactHeaderListItem) {}
    override fun onConnectContactFavoriteIconClicked(listItem: ConnectContactListItem) {}
    override fun onPositiveRatingItemClicked(voicemailListItem: VoicemailListItem) {}
    override fun onNegativeRatingItemClicked(voicemailListItem: VoicemailListItem) {}
    override fun onConnectContactDetailHeaderListItemClicked(listItem: ConnectContactDetailHeaderListItem) {}
    override fun onConnectContactDetailListItemClicked(listItem: ConnectContactDetailListItem) {}
    override fun onConnectContactCategoryItemClicked(listItem: ConnectContactCategoryListItem) {}
    override fun onConnectContactListItemClicked(listItem: ConnectContactListItem) {}
    override fun onConnectContactListItemLongClicked(listItem: ConnectContactListItem) {}
    override fun onDialogContactActionHeaderListItemClicked(listItem: DialogContactActionHeaderListItem) {}
    override fun onConnectHomeListItemClicked(listItem: ConnectHomeListItem) {}
    override fun onDialogContactActionListItemClicked(listItem: DialogContactActionListItem) {}
    override fun onDialogContactActionDetailListItemClicked(listItem: DialogContactActionDetailListItem) {}
    override fun onFeatureFlagListItemChecked(listItem: FeatureFlagListItem) {}
}