package com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.OneActiveCallActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
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
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.ConnectContactListAdapter
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Service.DialingServiceTypes
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.databinding.BottomSheetViewLocalContactsBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.dialogs.ContactActionDialog
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactDetailsFragment
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.ChatMessage
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.setUnderlinedText
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetImportWizardViewModel
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetViewLocalContactsViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetViewLocalContacts : Fragment(), MasterListListener, SearchView.OnQueryTextListener,
    CallManager.ProcessParticipantInfoCallBack {

    @Inject
    lateinit var callManager: CallManager
    @Inject
    lateinit var logManager: LogManager
    @Inject
    lateinit var permissionManager: PermissionManager
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var settingsManager: SettingsManager

    private lateinit var viewModel: BottomSheetViewLocalContactsViewModel
    private var parentViewModel: BottomSheetImportWizardViewModel? = null

    private lateinit var noContactsLayout: LinearLayout
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var searchContainer: FrameLayout
    private lateinit var searchView: SearchView
    private lateinit var importText: TextView
    private lateinit var privacyText: TextView
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var importingLayout: LinearLayout
    private lateinit var importContact: TextView
    private lateinit var cancelButton: TextView
    private lateinit var activeCallBar: ComposeView
    private var hasLocalContacts: Boolean = false
    private var startImport: Boolean = false
    private var shouldSelectAll: Boolean = false
    private var enableImport: Boolean = false

    private var newCallType: Int = RequestCodes.NewCall.NEW_CALL_NONE
    private lateinit var adapter: ConnectContactListAdapter

    companion object {
        const val TAG = "BottomSheetWizardViewLocalContacts"
    }

    private var showFullHeight = false

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null) {
            MainScope().launch(Dispatchers.IO) {
                val contact = sipCall.participantInfoList.firstOrNull()?.numberToCall?.let { viewModel.getContactFromPhoneNumber(it) }

                withContext(Dispatchers.Main) {
                    activeCallBar.setContent {
                        ActiveCallBanner(sipCall, contact, !isNightModeEnabled(requireActivity(), settingsManager), viewModel.sipManager.activeCallDurationLiveData) {
                            startActivity(OneActiveCallActivity.newIntent(requireActivity(), sipCall.participantInfoList.firstOrNull(), null))
                        }
                    }
                    activeCallBar.visibility = View.VISIBLE
                }
            }

        } else {
            activeCallBar.disposeComposition()
            activeCallBar.visibility = View.GONE
        }
    }

    private val baseListItemObserver = Observer<ArrayList<BaseListItem>> { listItems ->
        if (listItems.size > 0 || viewModel.searchTerm?.isNotEmpty() == true) {
            setState(empty = false)
            adapter.updateList(listItems)

            if (viewModel.searchTerm?.isNotEmpty() == true) {
                contactsRecyclerView.scrollToPosition(0)
            }

            if (!showFullHeight) {
                (parentFragment as? BottomSheetImportWizard)?.showFullHeight()
            }

            if (viewModel.shouldSelectAll == BottomSheetViewLocalContactsViewModel.SelectAllState.Jump) {
                viewModel.shouldSelectAll = BottomSheetViewLocalContactsViewModel.SelectAllState.None
                moveToNextFragment()
                parentViewModel?.selectedContacts = viewModel.getSelectedContacts()
            }

        } else if (viewModel.searchTerm.isNullOrBlank()) {
            setState(empty = true)
        }

        if (viewModel.isImporting) {
            updateImportCountText()
            privacyText.text = getString(R.string.connect_view_local_contacts_you_have_unimported_message, viewModel.getUnimportedContactsSize())
        }

        if (!viewModel.isImporting && importingLayout.visibility == View.VISIBLE) {
            importingLayout.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hasLocalContacts = (parentFragment as? BottomSheetImportWizard)?.hasLocalContacts().orFalse()
        startImport = (parentFragment as? BottomSheetImportWizard)?.shouldStartImport().orFalse()
        shouldSelectAll = (parentFragment as? BottomSheetImportWizard)?.shouldSelectAll().orFalse()
        enableImport = (parentFragment as? BottomSheetImportWizard)?.enableImport().orFalse()

        if (hasLocalContacts) {
            showFullHeight = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_view_local_contacts, container, false)
        view?.let { bindViews(view) }
        if (!hasLocalContacts) {
            setState(true)
        }

        adapter = ConnectContactListAdapter(requireActivity(), this, dbManager, sessionManager)

        contactsRecyclerView.adapter = adapter

        newCallType = requireActivity().intent.getIntExtra(
            Constants.Calls.PARAMS_NEW_CALL_TYPE,
            RequestCodes.NewCall.NEW_CALL_NONE
        )

        if (isAdded) {
            parentFragmentManager.setFragmentResultListener(
                ContactActionDialog.CONTACT_ACTION_DIALOG_RESULT,
                viewLifecycleOwner,
                resultListener
            )
        }

        contactsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    searchView.clearFocus()
                }
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        if (hasLocalContacts) {
            (parentFragment as? BottomSheetImportWizard)?.showFullHeight()
        }
    }

    private val resultListener : ((String, Bundle) -> Unit ) = { requestKey, bundle ->
        if (requestKey == ContactActionDialog.CONTACT_ACTION_DIALOG_RESULT) {
            (bundle.getSerializable(ContactActionDialog.NEXTIVA_CONTACT) as? NextivaContact?)?.let { nextivaContact ->
                viewModel.updateContactImportStates(
                    nextivaContact = nextivaContact,
                    Enums.Platform.ConnectContactListItemImportState.SELECTED
                )
                startImporting(nextivaContact)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[BottomSheetViewLocalContactsViewModel::class.java]
        parentFragment?.let { parent ->
            parentViewModel = ViewModelProvider(
                parent
            )[BottomSheetImportWizardViewModel::class.java].apply {
                if(selectedContacts.isEmpty()) {
                    viewModel.resetImportStates()
                }
                viewModel.setInitialSelectedContacts(this.selectedContacts)
                startImport = startImport or viewModel.isImporting
            }
        }

        if(shouldSelectAll) {
            viewModel.shouldSelectAll = BottomSheetViewLocalContactsViewModel.SelectAllState.SelectAll
        }

        viewModel.getBaseListItemsLiveData().observe(viewLifecycleOwner, baseListItemObserver)
        viewModel.activeCallLiveData.observe(viewLifecycleOwner, activeCallObserver)

        if (startImport) {
            viewModel.refreshContacts(isImporting = true, preselected = null)
        } else {
            viewModel.refreshContacts()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearCompositeDisposable()
    }

    override fun onStop() {
        super.onStop()
        parentViewModel?.selectedContacts = viewModel.getSelectedContacts()
        viewModel.clearCompositeDisposable()
    }


    override fun onDestroy() {
        super.onDestroy()
        contactsRecyclerView.removeAllViews()
        viewModel.clearCompositeDisposable()
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetViewLocalContactsBinding.bind(view)

        contactsRecyclerView = binding.bottomSheetViewLocalList
        noContactsLayout = binding.bottomSheetViewLocalNoContactsLayout
        privacyText = binding.bottomSheetViewLocalPrivacyText
        importText = binding.bottomSheetViewLocalImport
        searchContainer = binding.bottomSheetViewLocalSearchContainer
        searchView = binding.bottomSheetViewLocalSearchView
        cancelIcon = binding.cancelIconInclude.closeIconView
        importingLayout = binding.bottomSheetViewLocalImportingLayout
        importContact = binding.bottomSheetViewLocalImportButton
        cancelButton = binding.bottomSheetViewLocalCancel
        activeCallBar = binding.activeCallToolbar

        importText.setUnderlinedText(getString(R.string.general_import))

        val icon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        val drawable = icon.drawable
        drawable.setTint(ContextCompat.getColor(requireActivity(), R.color.connectGrey09))
        icon.setImageDrawable(drawable)

        val searchEditText = searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.connectSecondaryDarkBlue))
        searchEditText.setHintTextColor(ContextCompat.getColor(requireActivity(), R.color.connectGrey09))

        searchView.setOnQueryTextListener(this)
        cancelIcon.setOnClickListener { (parentFragment as? BottomSheetImportWizard)?.dismiss() }
        importText.setOnClickListener { startImporting(null) }

        cancelButton.setOnClickListener {
            viewModel.resetImportStates()
            viewModel.refreshContacts()
            updateActionLabel()
            parentViewModel?.resetStrategy()
            privacyText.text = getString(R.string.connect_view_local_contacts_privacy_message)
        }

        importContact.setOnClickListener { moveToNextFragment() }
    }

    private fun moveToNextFragment() {
        (parentFragment as? BottomSheetImportWizard)?.setFragment(
            BottomSheetWizardDuplicatesStrategy(),
            BottomSheetWizardDuplicatesStrategy.TAG
        )
    }

    private fun startImporting(preselected: NextivaContact?) {
        if (viewModel.isImporting) {
            viewModel.refreshContacts(isImporting = true, null)
            viewModel.toggleAllImportStates()
            updateImportCountText()
        } else {
            viewModel.refreshContacts(isImporting = true, preselected = preselected)
        }
    }

    private fun setState(empty: Boolean) {
        noContactsLayout.visibility = if (empty) View.VISIBLE else View.GONE
        contactsRecyclerView.visibility = if (empty) View.GONE else View.VISIBLE
        searchContainer.visibility = if (empty) View.GONE else View.VISIBLE
        privacyText.visibility = if (empty) View.GONE else View.VISIBLE
        importText.visibility = if (empty || !enableImport) View.GONE else View.VISIBLE
    }

    private fun updateImportCountText() {
        updateActionLabel()
        val selectedContactSize = viewModel.getSelectedContactsSize()
        when {
            selectedContactSize > 1 -> {
                importingLayout.visibility = View.VISIBLE
                importContact.visibility = View.VISIBLE
                importContact.text = getString(R.string.connect_view_local_contacts_import_contacts, selectedContactSize)
            }
            selectedContactSize == 1 -> {
                importingLayout.visibility = View.VISIBLE
                importContact.visibility = View.VISIBLE
                importContact.text = getString(R.string.connect_view_local_contacts_import_contact)
            }
            else -> {
                importingLayout.visibility = View.VISIBLE
                importContact.visibility = View.GONE
            }
        }
    }

    private fun updateActionLabel() {
        importText.setUnderlinedText(when(viewModel.getActionLabel()) {
            BottomSheetViewLocalContactsViewModel.ActionLabel.Import -> {
                getString(R.string.general_import)
            }
            BottomSheetViewLocalContactsViewModel.ActionLabel.SelectAll -> {
                getString(R.string.connect_view_local_contacts_select_all)
            }
            BottomSheetViewLocalContactsViewModel.ActionLabel.DeselectAll -> {
                getString(R.string.connect_view_local_contacts_deselect_all)
            }
            BottomSheetViewLocalContactsViewModel.ActionLabel.None -> {
                ""
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { viewModel.onSearchTermUpdated(it) }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { viewModel.onSearchTermUpdated(newText) }
        return false
    }

    override fun onConnectContactListItemClicked(listItem: ConnectContactListItem) {
        if (viewModel.isImporting) {
            listItem.nextivaContact?.let { contact ->
                listItem.importState?.let { importState ->
                    viewModel.updateContactImportStates(contact, importState)
                    updateImportCountText()
                }
            }

        } else {
            listItem.nextivaContact?.let {
                val copiedContact = NextivaContact(it)
                copiedContact.convertToConnect(Enums.Contacts.ContactTypes.LOCAL)

                if(newCallType == RequestCodes.NewCall.NEW_CALL_NONE) {
                    BottomSheetContactDetailsFragment(copiedContact) {
                        startImporting(it)
                    }.show(requireActivity().supportFragmentManager, null)
                }
                else {
                    val participantInfo = copiedContact.getParticipantInfo(null)
                    participantInfo.dialingServiceType = DialingServiceTypes.VOIP

                    if (newCallType == RequestCodes.NewCall.TRANSFER_REQUEST_CODE || newCallType == RequestCodes.NewCall.CONFERENCE_REQUEST_CODE) {
                        participantInfo.callType = Enums.Sip.CallTypes.VOICE
                    }

                    processParticipantInfo(participantInfo, Enums.Analytics.ScreenName.CONNECT_NEW_CALL_LOCAL_CONTACTS_LIST)
                }
            }
        }
    }

    override fun onConnectContactListItemLongClicked(listItem: ConnectContactListItem) {
        if (!viewModel.isImporting) {
            listItem.nextivaContact?.let {

                ContactActionDialog.newInstance(it, isImporting = true)
                    .show(requireActivity().supportFragmentManager, null)
            }

        }
    }

    private fun processParticipantInfo(
        participantInfo: ParticipantInfo,
        @Enums.Analytics.ScreenName.Screen analyticsScreenName: String
    ) {
        callManager.processParticipantInfo(
            requireActivity(),
            analyticsScreenName,
            participantInfo,
            null,
            (parentFragment as BottomSheetImportWizard).compositeDisposable,
            this
        )
    }


    // --------------------------------------------------------------------------------------------
    // CallManager.ProcessCallInfoCallBack Methods
    // --------------------------------------------------------------------------------------------
    override fun onParticipantInfoProcessed(
        activity: Activity,
        @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
        participantInfo: ParticipantInfo,
        retrievalNumber: String?,
        compositeDisposable: CompositeDisposable
    ) {
        logManager.logToFile(
            Enums.Logging.STATE_INFO,
            R.string.log_message_success_with_message,
            activity.getString(R.string.log_message_processing_call, participantInfo.toString())
        )
        val callback = PermissionManager.PermissionGrantedCallback {
            val data = Intent()
            data.putExtra(
                Constants.EXTRA_PARTICIPANT_INFO,
                participantInfo
            )
            data.putExtra(
                Constants.EXTRA_RETRIEVAL_NUMBER,
                retrievalNumber
            )
            activity.setResult(Activity.RESULT_OK, data)
            activity.finish()
        }
        if (participantInfo.callType == Enums.Sip.CallTypes.VIDEO) {
            permissionManager.requestVideoCallPermission(
                activity,
                analyticsScreenName,
                callback,
                null
            )
        } else if (participantInfo.callType == Enums.Sip.CallTypes.VOICE) {
            permissionManager.requestVoiceCallPermission(
                activity,
                analyticsScreenName,
                callback
            )
        }
    }

    // --------------------------------------------------------------------------------------------

    override fun onConnectContactDetailHeaderListItemClicked(listItem: ConnectContactDetailHeaderListItem) {
    }

    override fun onConnectContactDetailListItemClicked(listItem: ConnectContactDetailListItem) {
    }

    override fun onConnectContactCategoryItemClicked(listItem: ConnectContactCategoryListItem) {
    }

    override fun onConnectHomeListItemClicked(listItem: ConnectHomeListItem) {
    }

    override fun onDialogContactActionHeaderListItemClicked(listItem: DialogContactActionHeaderListItem) {
    }

    override fun onDialogContactActionListItemClicked(listItem: DialogContactActionListItem) {
    }

    override fun onDialogContactActionDetailListItemClicked(listItem: DialogContactActionDetailListItem) {
    }

    override fun onFeatureFlagListItemChecked(listItem: FeatureFlagListItem) {
    }

    override fun onCallHistoryListItemClicked(listItem: CallHistoryListItem) {
    }

    override fun onCallHistoryListItemLongClicked(listItem: CallHistoryListItem) {
    }

    override fun onCallHistoryCallButtonClicked(listItem: CallHistoryListItem) {
    }

    override fun onContactHeaderListItemClicked(listItem: HeaderListItem) {
    }

    override fun onContactHeaderListItemLongClicked(listItem: HeaderListItem) {
    }

    override fun onContactListItemClicked(listItem: ContactListItem) {
    }

    override fun onContactListItemLongClicked(listItem: ContactListItem) {
    }

    override fun onDetailItemViewListItemClicked(listItem: DetailItemViewListItem) {
    }

    override fun onDetailItemViewListItemLongClicked(listItem: DetailItemViewListItem) {
    }

    override fun onDetailItemViewListItemAction1ButtonClicked(listItem: DetailItemViewListItem) {
    }

    override fun onDetailItemViewListItemAction2ButtonClicked(listItem: DetailItemViewListItem) {
    }

    override fun onChatConversationItemClicked(listItem: ChatConversationListItem) {
    }

    override fun onChatConversationItemLongClicked(listItem: ChatConversationListItem) {
    }

    override fun onResendFailedChatMessageClicked(listItem: SimpleBaseListItem<ChatMessage>) {
    }

    override fun onResendFailedSmsMessageClicked(listItem: SimpleBaseListItem<SmsMessage>) {
    }

    override fun onChatMessageListItemDatetimeVisibilityToggled(listItem: SimpleBaseListItem<ChatMessage>) {
    }

    override fun onVoicemailCallButtonClicked(listItem: VoicemailListItem) {
    }

    override fun onVoicemailReadButtonClicked(listItem: VoicemailListItem) {
    }

    override fun onVoicemailDeleteButtonClicked(listItem: VoicemailListItem) {
    }

    override fun onVoicemailContactButtonClicked(listItem: VoicemailListItem) {
    }

    override fun onVoicemailSmsButtonClicked(listItem: VoicemailListItem) {
    }

    override fun onSmsConversationItemClicked(listItem: MessageListItem) {
    }

    override fun onSmsMessageListItemDatetimeVisibilityToggled(listItem: SmsMessageListItem) {
    }

    override fun onConnectContactHeaderListItemClicked(listItem: ConnectContactHeaderListItem) {
    }

    override fun onConnectContactFavoriteIconClicked(listItem: ConnectContactListItem) {
    }

    override fun onPositiveRatingItemClicked(voicemailListItem: VoicemailListItem) {
    }

    override fun onNegativeRatingItemClicked(voicemailListItem: VoicemailListItem) {
    }


}