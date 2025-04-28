package com.nextiva.nextivaapp.android.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.dialogs.ContactActionDialog
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.Voicemail
import com.nextiva.nextivaapp.android.viewmodels.ConnectCallDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectCallDetailsFragment() : GeneralRecyclerViewFragment() {

    constructor(callLogEntry: CallLogEntry, contact: NextivaContact?): this() {
        this.callLogEntry = callLogEntry
        this.nextivaContact = contact
    }

    constructor(voicemail: Voicemail, contact: NextivaContact?): this() {
        this.voicemail = voicemail
        this.nextivaContact = contact
    }

    var callLogEntry: CallLogEntry? = null
    var voicemail: Voicemail? = null
    var nextivaContact: NextivaContact? = null

    private lateinit var viewModel: ConnectCallDetailsViewModel

    private var blockingListener: BlockingNumberListener? = null

    private val baseListItemObserver = Observer<ArrayList<BaseListItem>?> { listItems ->
        listItems?.let { mAdapter.updateList(it) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentFragment?.let { fragment ->
            if (fragment is BlockingNumberListener) {
                blockingListener = fragment
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ConnectCallDetailsViewModel::class.java]
        viewModel.getBaseListItemsLiveData().observe(viewLifecycleOwner, baseListItemObserver)

        viewModel.callLogEntry = callLogEntry
        viewModel.voicemail = voicemail

        viewModel.getDetailListItems()

        if (isAdded) {
            setFragmentResultListener(ContactActionDialog.CONTACT_ACTION_DIALOG_RESULT) { _, bundle ->
                val phoneNumber: String? = bundle.getString(ContactActionDialog.PHONE_NUMBER, null)
                phoneNumber?.let { number ->
                    blockingListener?.onBlockUnblockAction(number)
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_call_details
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }

    override fun getRecyclerViewId(): Int {
        return R.id.connect_call_details_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.CONNECT_CALL_DETAILS
    }

    override fun onConnectContactDetailListItemClicked(listItem: ConnectContactDetailListItem) {
        super.onConnectContactDetailListItemClicked(listItem)
        ContactActionDialog.newInstance(
            nextivaContact = nextivaContact,
            phoneId = nextivaContact?.allPhoneNumbers?.firstOrNull()?.id ?: 0L,
            phoneNumber = callLogEntry?.formattedPhoneNumber ?: voicemail?.formattedPhoneNumber
        ).show(parentFragmentManager, null)
    }

    interface BlockingNumberListener {
        fun onBlockUnblockAction(phoneNumber: String)
    }
}
