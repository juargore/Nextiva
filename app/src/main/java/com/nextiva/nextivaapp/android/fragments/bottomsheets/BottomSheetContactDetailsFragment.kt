package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback
import com.nextiva.nextivaapp.android.ConnectContactDetailsActivity
import com.nextiva.nextivaapp.android.CreateBusinessContactActivity
import com.nextiva.nextivaapp.android.OneActiveCallActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Contacts.ContactTypes
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.databinding.BottomSheetContactDetailBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.dialogs.ConnectMenuDialog
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationActivity
import com.nextiva.nextivaapp.android.fragments.ConnectCallDetailsFragment
import com.nextiva.nextivaapp.android.fragments.ConnectContactDetailsFragment
import com.nextiva.nextivaapp.android.managers.BlockingState
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.models.Voicemail
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.CallUtil.isExtensionNumber
import com.nextiva.nextivaapp.android.util.Event
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.util.extensions.extractDtfmTone
import com.nextiva.nextivaapp.android.util.extensions.extractFirstNumber
import com.nextiva.nextivaapp.android.util.extensions.notNull
import com.nextiva.nextivaapp.android.util.extensions.setBlockedContactTypeLabel
import com.nextiva.nextivaapp.android.util.extensions.setContactTypeLabel
import com.nextiva.nextivaapp.android.view.ConnectContactHeaderView
import com.nextiva.nextivaapp.android.view.ConnectQuickActionDetailButton
import com.nextiva.nextivaapp.android.view.CustomSnackbar
import com.nextiva.nextivaapp.android.view.SnackStyle
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.nextivaapp.android.viewmodels.ConnectContactDetailsViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class BottomSheetContactDetailsFragment(): BaseBottomSheetDialogFragment(), AppBarLayout.OnOffsetChangedListener,
    CallManager.ProcessParticipantInfoCallBack, ContentViewCallback,
    ConnectCallDetailsFragment.BlockingNumberListener {

    constructor(contact: NextivaContact, importAction: (() -> Unit)?): this() {
        this.contact = contact
        this.importAction = importAction
    }

    constructor(callLogEntry: CallLogEntry, contact: NextivaContact?): this() {
        this.callLogEntry = callLogEntry
        this.contact = contact
    }

    private val percentageToShowTitleAtToolbar = 0.8f
    private var isTheTitleVisible = false

    var contact: NextivaContact? = null
    var callLogEntry: CallLogEntry? = null
    var voicemail: Voicemail? = null
    private var importAction: (() -> Unit)? = null

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var intentManager: IntentManager
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var dbManagerKt: DbManagerKt
    @Inject
    lateinit var dialogManager: DialogManager
    @Inject
    lateinit var blockingManager: BlockingNumberManager

    private lateinit var contactHeaderView: ConnectContactHeaderView
    private lateinit var quickActionCallButton: ConnectQuickActionDetailButton
    private lateinit var quickActionSmsButton: ConnectQuickActionDetailButton
    private lateinit var quickActionVideoButton: ConnectQuickActionDetailButton
    private lateinit var quickActionChatButton: ConnectQuickActionDetailButton
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var collapsedTitleTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var toolbarImage: FontTextView
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var addContactSubMenuLayout: LinearLayout
    private lateinit var addToContactSubMenuButton: TextView
    private lateinit var blockingFeatureButton: TextView
    private lateinit var backIcon: RelativeLayout
    private lateinit var activeCallBar: ComposeView

    private var firstAddSubMenuOnClick: (() -> Unit)? = null
    private var secondAddSubMenuOnClick: (() -> Unit)? = null
    private var thirdAddSubMenuOnClick: (() -> Unit)? = null

    private lateinit var viewModel: ConnectContactDetailsViewModel

    private val contactValuesChangedObserver = Observer<NextivaContact?> { contact ->
        contact?.let {
            loadContactViews(it)
            loadButtons()
            viewModel.getDetailListItems()
        }
    }

    private val presenceChangedObserver = Observer<DbPresence?> {
        it?.let { contactHeaderView.updatePresence(it) }
    }

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null) {
            MainScope().launch(Dispatchers.IO) {
                val contact = sipCall.participantInfoList.firstOrNull()?.numberToCall?.let { dbManagerKt.getContactFromPhoneNumberInThread(it).value }

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

    private val blockingFeatureObserver = Observer<BlockingState?> { newState ->
        newState?.let { nState ->
            val message = nState.messageResId
            when (nState) {
                BlockingState.Blocked, BlockingState.Unblocked -> {
                    showSnackBar(null, resources.getString(message)) {
                        callLogEntry?.phoneNumber?.let {
                            viewModel.updateBlockingFeature(it)
                        }
                    }
                    updateTitleTextView()
                }
                BlockingState.FailureBlocking, BlockingState.FailureUnblocking -> {
                    showSnackBar(R.string.fa_times_circle, resources.getString(message), SnackStyle.Error)
                    viewModel.clearBlockingFeatureEvent()
                }
            }
        }
    }

    private val unacceptedQuickActionContactTypesList = listOf(
            ContactTypes.CONNECT_USER,
            ContactTypes.CONNECT_SHARED,
            ContactTypes.CONNECT_PERSONAL,
            ContactTypes.LOCAL,
            ContactTypes.CONNECT_TEAM,
            ContactTypes.CONNECT_CALL_FLOW,
            ContactTypes.CONNECT_CALL_CENTERS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
        viewModel = ViewModelProvider(requireActivity())[ConnectContactDetailsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_contact_detail, container, false)
        view?.let { bindViews(view) }

        toolbar.setTitleTextColor(ContextCompat.getColor(requireActivity(), R.color.black))

        ViewUtil.startAlphaAnimation(collapsedTitleTextView, 0, View.GONE)
        appBarLayout.addOnOffsetChangedListener(this)
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

        when {
            voicemail != null -> voicemail?.let { loadVoicemailViews(it) }
            callLogEntry != null -> callLogEntry?.let { loadCallLogEntryViews(it) }
            contact != null -> contact?.let {
                viewModel.setContact(it)
                loadContactViews(it)
            }
        }

        when (contact?.contactType) {
            ContactTypes.CONNECT_USER -> {
                if (sessionManager.featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.TEAM_CHAT)) {

                    if (sessionManager.isTeamchatEnabled(requireContext())) {
                        quickActionChatButton.visibility = View.VISIBLE
                        quickActionChatButton.isEnabled = sessionManager.isSmsEnabled
                    }
                }

                if (sessionManager.isMeetingEnabled(requireContext())) {
                    quickActionVideoButton.visibility = View.VISIBLE
                }
            }
            ContactTypes.CONNECT_SHARED,
            ContactTypes.CONNECT_PERSONAL -> {
                if (sessionManager.isMeetingEnabled(requireContext())) {
                    quickActionVideoButton.visibility = View.VISIBLE
                }
            }
            else -> {}
        }

        contact?.contactType.let {
            // show sms icon for unsaved contact if feature flag is enabled
            if (it !in unacceptedQuickActionContactTypesList && !viewModel.isXbertContact()) {
                if (sessionManager.featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.SMS)) {
                    quickActionSmsButton.visibility = View.VISIBLE
                    quickActionChatButton.isEnabled = sessionManager.isSmsEnabled
                }
            }
        }

        viewModel.groupId.observe(viewLifecycleOwner, Event.EventObserver { groupId ->
            startActivity(viewModel.getSmsIntent(groupId.first))
        })
        viewModel.activeCallLiveData.observe(viewLifecycleOwner, activeCallObserver)

        loadButtons()

        viewModel.contactLiveData.observe(this, contactValuesChangedObserver)
        contact?.userId?.let { userId ->
            dbManager.getPresenceLiveDataFromContactTypeId(userId).observe(this, presenceChangedObserver)
        }

        viewModel.addContactPressed.observe(viewLifecycleOwner, Event.EventObserver { index ->
            when(index) {
                0 -> firstAddSubMenuOnClick?.invoke()
                1 -> secondAddSubMenuOnClick?.invoke()
                2 -> thirdAddSubMenuOnClick?.invoke()
            }
        })

        viewModel.blockingFeatureEvent.observe(viewLifecycleOwner, blockingFeatureObserver)

        updateTitleTextView()

        when (contact?.contactType) {
            ContactTypes.CONNECT_PERSONAL -> {
                toolbarImage.setIcon(R.string.fa_lock, Enums.FontAwesomeIconType.SOLID)
                toolbarImage.visibility = View.VISIBLE
            }
            ContactTypes.CONNECT_SHARED -> {
                toolbarImage.setIcon(R.string.fa_user_friends, Enums.FontAwesomeIconType.SOLID)
                toolbarImage.visibility = View.VISIBLE
            }
            else -> {
                toolbarImage.visibility = View.GONE
            }
        }
        
        return view
    }

    private fun updateTitleTextView() {
        titleTextView.setContactTypeLabel(contact)
        callLogEntry?.phoneNumber?.let { phoneNumber ->
            if (viewModel.isNumberBlocked(phoneNumber)) {
                titleTextView.setBlockedContactTypeLabel()
            }
        }
        addOrUpdateBlockingFeatureButton()
        callLogEntry?.let { addOrUpdateContactDetails(it) }
    }

    private fun addOrUpdateBlockingFeatureButton() {
        if (!viewModel.isBlockingFeatureEnabled() || isExtensionNumber(callLogEntry?.phoneNumber)) {
            blockingFeatureButton.visibility = View.GONE
            return
        }

        var buttonText = getString(R.string.connect_call_details_block_number)
        var buttonTextColor = ContextCompat.getColor(requireContext(), R.color.connectSecondaryRed)
        var bottomSheetTitle = resources.getString(R.string.connect_call_details_block_number_title)
        var bottomSheetSubtitle = resources.getString(R.string.connect_call_details_block_number_message)
        var bottomSheetPrimaryButtonText = resources.getString(R.string.connect_call_details_block_number)

        callLogEntry?.phoneNumber?.let { phoneNumber ->
            if (viewModel.isNumberBlocked(phoneNumber)) {
                buttonTextColor = ContextCompat.getColor(requireContext(), R.color.connectPrimaryBlue)
                buttonText = resources.getString(R.string.connect_call_details_unblock_number)
                bottomSheetTitle = resources.getString(R.string.connect_call_details_unblock_number_title)
                bottomSheetSubtitle = resources.getString(R.string.connect_call_details_unblock_number_message)
                bottomSheetPrimaryButtonText = resources.getString(R.string.connect_call_details_unblock_number)
            }
        }

        blockingFeatureButton.visibility = View.VISIBLE
        blockingFeatureButton.text = buttonText
        blockingFeatureButton.setTextColor(buttonTextColor)
        blockingFeatureButton.setOnClickListener {
            showBottomSheetDialog(null, bottomSheetTitle, bottomSheetSubtitle, bottomSheetPrimaryButtonText)
        }

        viewModel.clearBlockingFeatureEvent()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.resetContactLiveData()
    }

    private fun loadContactViews(contact: NextivaContact) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.bottom_sheet_contact_detail_list_fragment_container_layout, ConnectContactDetailsFragment())
        transaction.commit()

        viewModel.getDetailListItems()

        contactHeaderView.setNameText(contact)
        contactHeaderView.setAvatar(contact.getAvatarInfo(true))

        collapsedTitleTextView.text = contact.uiName

        quickActionCallButton.visibility = View.VISIBLE
        val callingPhoneNumbers = contact.allPhoneNumbers?.filter { it.type != Enums.Contacts.PhoneTypes.FAX }

        quickActionCallButton.setEnabled(callingPhoneNumbers?.isNotEmpty() == true) {
            callingPhoneNumbers?.let { phoneNumbers ->
                if (phoneNumbers.size > 1) {
                    ConnectMenuDialog(viewModel.getPhoneNumberListItems(phoneNumbers)) { phoneNumber ->
                        (phoneNumber as? PhoneNumber)?.number?.let { number ->
                            viewModel.processCallInfo(
                                requireActivity(),
                                Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS,
                                number.extractFirstNumber(),
                                Enums.Sip.CallTypes.VOICE,
                                metadata = number.extractDtfmTone(),
                                this
                            )
                        }
                    }.show(childFragmentManager, null)

                } else {
                    phoneNumbers.firstOrNull()?.number?.let { number ->
                        viewModel.processCallInfo(
                            requireActivity(),
                            Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS,
                            number.extractFirstNumber(),
                            Enums.Sip.CallTypes.VOICE,
                            metadata = number.extractDtfmTone(),
                            processCallInfoCallBack = this
                        )
                    }
                }
            }
        }

        if (viewModel.isShowSms()) {
            setupQuickActionSms(contact)
        }
    }

    private fun addOrUpdateContactDetails(callLogEntry: CallLogEntry) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.bottom_sheet_contact_detail_list_fragment_container_layout, ConnectCallDetailsFragment(callLogEntry, contact))
        transaction.commit()
    }

    private fun loadCallLogEntryViews(callLogEntry: CallLogEntry) {
        addOrUpdateContactDetails(callLogEntry)

        collapsedTitleTextView.text = contact?.uiName ?: callLogEntry.humanReadableName ?: requireActivity().getString(R.string.connect_contact_details_unknown_contact)

        when {
            contact != null -> {
                contact?.let { contact ->
                    contactHeaderView.setNameText(contact, true)
                    contactHeaderView.setAvatar(contact.getAvatarInfo(true))
                    contactHeaderView.showProfileTextView.setOnClickListener {
                        dismiss()
                        requireActivity().startActivity(ConnectContactDetailsActivity.newIntent(requireActivity(), contact))
                    }
                }
            }
            else -> {
                contactHeaderView.setNameText(callLogEntry)
                contactHeaderView.setAvatar(AvatarInfo.Builder()
                        .setPhotoData(callLogEntry.avatar)
                        .isConnect(true)
                        .setDisplayName(callLogEntry.humanReadableName)
                        .setFontAwesomeIconResId(R.string.fa_user)
                        .build())
            }
        }

        quickActionCallButton.visibility = View.VISIBLE
        quickActionCallButton.setEnabled(callLogEntry.phoneNumber?.let {
            CallUtil.getStrippedPhoneNumber(
                it
            ).isNotEmpty()
        } ?: false) {
            callLogEntry.phoneNumber?.let { formattedNumber ->
                val numberToDial = formattedNumber.extractFirstNumber().orEmpty()
                val callingNumber = if (callLogEntry.contactType == ContactTypes.CONNECT_USER) {
                    CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(numberToDial)
                } else {
                    numberToDial
                }
                viewModel.processCallInfo(
                    requireActivity(),
                    Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS,
                    callingNumber,
                    Enums.Sip.CallTypes.VOICE,
                    metadata = callingNumber?.extractDtfmTone(),
                    processCallInfoCallBack = this
                )
            }
        }

        contact?.let { contact ->
            setupQuickActionSms(contact)

        } ?: kotlin.run {
            quickActionSmsButton.setEnabled(CallUtil.isValidSMSNumber(callLogEntry.phoneNumber) && sessionManager.canSendSms()) {
                callLogEntry.phoneNumber?.let { sendSms(it) }
            }
        }
    }

    private fun loadVoicemailViews(voicemail: Voicemail) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.bottom_sheet_contact_detail_list_fragment_container_layout, ConnectCallDetailsFragment(voicemail, contact))
        transaction.commit()

        val voicemailUiName = contact?.uiName ?: voicemail.uiName ?: voicemail.name ?: requireActivity().getString(R.string.connect_contact_details_unknown_contact)

        collapsedTitleTextView.text = voicemailUiName

        when {
            contact != null -> {
                contact?.let { contact ->
                    contactHeaderView.setNameText(contact, true)
                    contactHeaderView.setAvatar(contact.getAvatarInfo(true))
                    contactHeaderView.showProfileTextView.setOnClickListener {
                        dismiss()
                        requireActivity().startActivity(ConnectContactDetailsActivity.newIntent(requireActivity(), contact))
                    }
                }
            }
            else -> {
                contactHeaderView.setNameText(voicemail)
                contactHeaderView.setAvatar(AvatarInfo.Builder()
                        .setPhotoData(voicemail.avatar)
                        .isConnect(true)
                        .setDisplayName(voicemailUiName)
                        .setFontAwesomeIconResId(R.string.fa_user)
                        .build())
            }
        }

        quickActionCallButton.visibility = View.VISIBLE
        quickActionCallButton.setEnabled(voicemail.address?.let { CallUtil.getStrippedPhoneNumber(it).isNotEmpty() } ?: false) {
            voicemail.address?.let {
                viewModel.processCallInfo(
                    requireActivity(),
                    Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS,
                    it,
                    Enums.Sip.CallTypes.VOICE,
                    processCallInfoCallBack = this
                )
            }
        }

        quickActionSmsButton.setEnabled(CallUtil.isValidSMSNumber(voicemail.address) && sessionManager.canSendSms()) {
            voicemail.address?.let { sendSms(it) }
        }
    }

    private fun loadButtons() {

        firstAddSubMenuOnClick = null
        secondAddSubMenuOnClick = null
        thirdAddSubMenuOnClick = null

        var firstButtonText: String? = null
        var secondButtonText: String? = null
        var thirdButtonText: String? = null

        when {
            (voicemail != null || callLogEntry != null) && contact != null -> {
                firstButtonText = requireActivity().getString(R.string.connect_call_details_add_to_local_contacts)
                firstAddSubMenuOnClick = { importCallToLocalContacts() }
            }
            (voicemail != null || callLogEntry != null) -> {
                firstButtonText = requireActivity().getString(R.string.connect_call_details_add_to_contacts)
                secondButtonText = requireActivity().getString(R.string.connect_call_details_add_to_existing_contact)
                thirdButtonText = requireActivity().getString(R.string.connect_call_details_add_to_local_contacts)

                firstAddSubMenuOnClick = {
                    callLogEntry?.let { requireActivity().startActivity(CreateBusinessContactActivity.newIntent(requireActivity(), it)) }
                    voicemail?.let { requireActivity().startActivity(CreateBusinessContactActivity.newIntent(requireActivity(), it)) }
                    dismiss()
                }

                secondAddSubMenuOnClick = {
                    BottomSheetSelectContactList(intArrayOf(ContactTypes.CONNECT_SHARED,
                            ContactTypes.CONNECT_PERSONAL),
                            callLogEntry?.phoneNumber ?: voicemail?.address,
                            this)
                            .show(requireActivity().supportFragmentManager, null)
                }

                thirdAddSubMenuOnClick = {
                    importCallToLocalContacts()
                }
            }
            importAction != null && !viewModel.isContactImported() -> {
                firstButtonText = requireActivity().getString(R.string.general_import)
                firstAddSubMenuOnClick = {
                    importAction?.let { it() }
                    dismiss()
                }

            }
            else -> {
                addToContactSubMenuButton.visibility = View.GONE
            }
        }

        firstAddSubMenuOnClick?.let {
            addToContactSubMenuButton.setOnClickListener {
                BottomSheetAddContactMenu.newInstance(
                    firstButtonText, secondButtonText, thirdButtonText
                ).show(childFragmentManager, "AddContactMenu")
            }
        }

        addToContactSubMenuButton.visibility = if(firstAddSubMenuOnClick.notNull()) View.VISIBLE else View.GONE

        addOrUpdateBlockingFeatureButton()
    }

    private fun showSnackBar(
        icon: Int?,
        message: String,
        snackStyle: SnackStyle = SnackStyle.Standard,
        undoAction: (() -> Unit)? = null,
    ) {
        val duration = BaseTransientBottomBar.LENGTH_LONG
        CustomSnackbar.make(requireView(), duration, this, false, snackStyle).apply {
            setText(message)
            icon?.let { setFontAwesomeIcon(getString(icon)) }
            undoAction?.let { enableUndoAction { undoAction.invoke() } }
            show()
        }
    }

    private fun setupQuickActionSms(contact: NextivaContact) {
        quickActionSmsButton.visibility = View.VISIBLE

        val smsPhoneNumbers = contact.allPhoneNumbers?.filter { CallUtil.isValidSMSNumber(it.strippedNumber) }
        quickActionSmsButton.setEnabled(smsPhoneNumbers?.isNotEmpty() == true) {
            smsPhoneNumbers?.let { smsNumbers ->
                if (sessionManager.isSmsLicenseEnabled && (sessionManager.isSmsProvisioningEnabled || viewModel.sessionManager.isTeamSmsEnabled)) {
                    if (contact.contactType == ContactTypes.CONNECT_TEAM) {
                        if (viewModel.isTeamSmsEnabled(contact)) {
                            sendSms(smsNumbers.filter { CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(it.strippedNumber ?: "").length > 9 })

                        } else {
                            dialogManager.showDialog(
                                requireContext(),
                                "",
                                getString(R.string.contact_detail_team_sms_not_supported),
                                getString(R.string.general_ok)
                            ) { _, _ -> }
                        }

                    } else {
                        sendSms(smsNumbers)
                    }

                } else {
                    val isSmsLicenseEnabled = sessionManager.isSmsLicenseEnabled
                    val isSmsEnabled = sessionManager.isSmsEnabled

                    val dialogTitle = when {
                        !isSmsEnabled || !isSmsLicenseEnabled -> getString(R.string.error_general_error_title)
                        else -> getString(R.string.invalid_provisioning_dialog_title)
                    }
                    val dialogBody = when {
                        !isSmsEnabled || !isSmsLicenseEnabled -> getString(R.string.invalid_provisioning_dialog_body)
                        else -> getString(R.string.invalid_license_dialog_body)
                    }

                    dialogManager.showDialog(
                        requireContext(),
                        dialogTitle,
                        dialogBody,
                        getString(R.string.general_ok)
                    ) { _, _ -> }
                }
            }
        }
    }

    private fun sendSms(smsNumbers: List<PhoneNumber>) {
        if (smsNumbers.size > 1) {
            ConnectMenuDialog(viewModel.getPhoneNumberListItems(smsNumbers)) { phoneNumber ->
                (phoneNumber as? PhoneNumber)?.strippedNumber?.let { strippedNumber ->
                    viewModel.fetchGroupId(strippedNumber)
                }

            }.show(childFragmentManager, null)

        } else {
            smsNumbers.firstOrNull()?.strippedNumber?.let { //startActivity(viewModel.getSmsIntent(it)) }
                viewModel.fetchGroupId(it)
            }
        }
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetContactDetailBinding.bind(view)

        contactHeaderView = binding.bottomSheetContactDetailsContactHeaderView
        quickActionCallButton = binding.bottomSheetContactDetailsCallTextView
        quickActionSmsButton = binding.bottomSheetContactDetailsSmsTextView
        quickActionVideoButton = binding.bottomSheetContactDetailsVideoTextView
        quickActionChatButton = binding.bottomSheetContactDetailsChatTextView
        toolbar = binding.bottomSheetContactDetailsToolbar
        fragmentContainer = binding.bottomSheetContactDetailListFragmentContainerLayout
        appBarLayout = binding.bottomSheetContactDetailAppBarLayout
        collapsingToolbarLayout = binding.bottomSheetContactDetailsCollapsingToolbar
        collapsedTitleTextView = binding.bottomSheetContactDetailsToolbarCollapsedTitleTextView
        titleTextView = binding.bottomSheetContactDetailsToolbarTitleTextView
        toolbarImage = binding.bottomSheetContactDetailsToolbarLock
        cancelIcon = binding.cancelIconInclude.closeIconView
        addContactSubMenuLayout = binding.connectContactDetailsSubmenuLayout
        addToContactSubMenuButton = binding.connectContactDetailsSubmenu
        blockingFeatureButton = binding.connectContactDetailsBlockingButton
        backIcon = binding.backArrowInclude.backArrowView
        activeCallBar = binding.activeCallToolbar

        cancelIcon.setOnClickListener { dismiss() }
        backIcon.setOnClickListener { dismiss() }
    }

    private fun importCallToLocalContacts() {
        val displayName = callLogEntry?.humanReadableName ?: voicemail?.humanReadableName ?: ""
        val phoneNumber = callLogEntry?.phoneNumber ?: voicemail?.address

        intentManager.addToLocalContacts(requireActivity(),
                Enums.Analytics.ScreenName.BOTTOM_SHEET_CONTACT_DETAILS,
                displayName,
                phoneNumber,
                null,
                null,
                null)
    }

    private fun sendSms(number: String) {
        var groupValue: String? = CallUtil.getFormattedNumber(CallUtil.getStrippedPhoneNumber(number))
        var ourNumber = ""

        viewModel.sessionManager.userDetails?.let { userDetails ->
            userDetails.telephoneNumber?.let { telephoneNumber ->
                ourNumber = CallUtil.getCountryCode() + CallUtil.getStrippedPhoneNumber(telephoneNumber)
                groupValue = "$groupValue,$ourNumber"
            }
        }

        groupValue?.let {
            val startChat = ConversationActivity.newIntent(activity,
                    SmsConversationDetails(getSortedGroupValue(it),
                        listOf(SmsParticipant(CallUtil.getFormattedNumber(CallUtil.getStrippedPhoneNumber(number)),
                        viewModel.contactLiveData.value?.userId)),
                        ourNumber,
                        sessionManager.currentUser?.userUuid ?: ""),
                    false,
                    Enums.Chats.ConversationTypes.SMS,
                    Enums.Chats.ChatScreens.CONVERSATION)
            requireActivity().startActivity(startChat)
        }
    }

    private fun getSortedGroupValue(groupValue: String): String {
        val sortingGroupValueList = java.util.ArrayList(listOf(*groupValue.trim { it <= ' ' }.replace("\\s".toRegex(), "").split(",".toRegex()).toTypedArray()))
        sortingGroupValueList.sortWith { s, t1 -> s.trim { it <= ' ' }.toLong().compareTo(t1.trim { it <= ' ' }.toLong()) }
        return TextUtils.join(",", sortingGroupValueList).trim { it <= ' ' }
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= percentageToShowTitleAtToolbar) {
            if (!isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                ViewUtil.startAlphaAnimation(titleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                ViewUtil.startAlphaAnimation(toolbarImage, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                isTheTitleVisible = true
            }
        } else {
            if (isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                ViewUtil.startAlphaAnimation(titleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                ViewUtil.startAlphaAnimation(toolbarImage, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                isTheTitleVisible = false
            }
        }

        contactHeaderView.visibility = if (isTheTitleVisible) { View.INVISIBLE } else { View.VISIBLE }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.totalScrollRange?.let { maxScroll ->
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
            handleToolbarTitleVisibility(percentage)
        }
    }

    override fun onParticipantInfoProcessed(activity: Activity, analyticsScreenName: String, participantInfo: ParticipantInfo, retrievalNumber: String?, compositeDisposable: CompositeDisposable) {
        viewModel.makeCall(activity, analyticsScreenName, participantInfo)
        dismiss()
    }

    override fun animateContentIn(delay: Int, duration: Int) { }

    override fun animateContentOut(delay: Int, duration: Int) { }

    override fun onBlockUnblockAction(phoneNumber: String) {
        var bottomSheetTitle = resources.getString(R.string.connect_call_details_block_number_title)
        var bottomSheetSubtitle = resources.getString(R.string.connect_call_details_block_number_message)
        var bottomSheetPrimaryButtonText = resources.getString(R.string.connect_call_details_block_number)

        if (viewModel.isNumberBlocked(phoneNumber)) {
            bottomSheetTitle = resources.getString(R.string.connect_call_details_unblock_number_title)
            bottomSheetSubtitle = resources.getString(R.string.connect_call_details_unblock_number_message)
            bottomSheetPrimaryButtonText = resources.getString(R.string.connect_call_details_unblock_number)
        }

        showBottomSheetDialog(phoneNumber, bottomSheetTitle, bottomSheetSubtitle, bottomSheetPrimaryButtonText)
    }

    private fun showBottomSheetDialog(
        phoneNumber: String?,
        bottomSheetTitle: String,
        bottomSheetSubtitle: String,
        bottomSheetPrimaryButtonText: String
    ) {
        parentFragmentManager.let { fm ->
            BottomSheetDeleteConfirmation.newInstance(
                title = bottomSheetTitle,
                subtitle = bottomSheetSubtitle,
                primaryButtonText = bottomSheetPrimaryButtonText,
                showShowAgainCheckbox = false,
                showCloseButton = false,
                deleteAction = {
                    val phone = phoneNumber ?: callLogEntry?.phoneNumber
                    phone?.let { viewModel.updateBlockingFeature(it) }
                },
                cancelAction = { }
            ).show(fm, null)
        }
    }
}
