package com.nextiva.nextivaapp.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Contacts.ContactTypes
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.databinding.ActivityConnectContactDetailsBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.dialogs.ConnectMenuDialog
import com.nextiva.nextivaapp.android.fragments.ConnectContactDetailsFragment
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetAddContactMenu
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactDetailsMenu
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDeleteConfirmation
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetSelectContactList
import com.nextiva.nextivaapp.android.managers.BlockingState
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.CallUtil.isExtensionNumber
import com.nextiva.nextivaapp.android.util.ContactDeleteHelper
import com.nextiva.nextivaapp.android.util.Event
import com.nextiva.nextivaapp.android.util.MenuUtil
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.util.extensions.extractDtfmTone
import com.nextiva.nextivaapp.android.util.extensions.extractFirstNumber
import com.nextiva.nextivaapp.android.util.extensions.setBlockedContactTypeLabel
import com.nextiva.nextivaapp.android.util.extensions.setContactTypeLabel
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
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
class ConnectContactDetailsActivity : BaseActivity(), AppBarLayout.OnOffsetChangedListener,
    CallManager.ProcessParticipantInfoCallBack,
    ConnectContactDetailsFragment.BlockingNumberListener,
    ContentViewCallback {

    private val percentageToShowTitleAtToolbar = 0.8f

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var intentManager: IntentManager
    @Inject
    lateinit var dialogManager: DialogManager
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var dbManagerKt: DbManagerKt
    @Inject
    lateinit var blockingManager: BlockingNumberManager
    @Inject
    lateinit var platformContactsRepository: PlatformContactsRepository
    @Inject
    lateinit var conversationRepository: ConversationRepository
    @Inject
    lateinit var smsManagementRepository: SmsManagementRepository

    private var isTheTitleVisible = false
    private var selectedPhoneNumber: String? = null
    private var nextivaContact: NextivaContact? = null

    companion object {
        const val CONNECT_CONTACT = "CONNECT_CONTACT"

        fun newIntent(context: Context, contact: NextivaContact): Intent {
            val intent = Intent(context, ConnectContactDetailsActivity::class.java)
            intent.putExtra(CONNECT_CONTACT, contact)
            return intent
        }
    }

    private lateinit var parentLayout: CoordinatorLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var titleTextView: TextView
    private lateinit var collapsedTitleTextView: TextView
    private lateinit var contactDetailHeaderView: ConnectContactHeaderView
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var toolbarImage: FontTextView
    private lateinit var quickActionCallButton: ConnectQuickActionDetailButton
    private lateinit var quickActionSmsButton: ConnectQuickActionDetailButton
    private lateinit var quickActionVideoButton: ConnectQuickActionDetailButton
    private lateinit var quickActionChatButton: ConnectQuickActionDetailButton
    private lateinit var addContactSubMenuLayout: LinearLayout
    private lateinit var addToContactSubMenuButton: TextView
    private lateinit var blockingFeatureButton: TextView
    private lateinit var backArrowView: RelativeLayout
    private lateinit var loadingLayout: View
    private lateinit var activeCallBar: ComposeView

    private var firstAddSubMenuOnClick: (() -> Unit)? = null
    private var secondAddSubMenuOnClick: (() -> Unit)? = null
    private var thirdAddSubMenuOnClick: (() -> Unit)? = null

    private lateinit var viewModel: ConnectContactDetailsViewModel

    private val contactValuesChangedObserver = Observer<NextivaContact?> { contact ->
        contact?.let {
            setHeaderViews(it)
            loadButtons(it)
            viewModel.getDetailListItems()
            nextivaContact = it
        }

        if (contact == null) {
            finish() // contact was deleted from the Edit screen
        }
    }

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null) {
            MainScope().launch(Dispatchers.IO) {
                val contact = sipCall.participantInfoList.firstOrNull()?.numberToCall?.let { dbManagerKt.getContactFromPhoneNumberInThread(it).value }

                withContext(Dispatchers.Main) {
                    activeCallBar.setContent {
                        ActiveCallBanner(sipCall, contact, !isNightModeEnabled(applicationContext, mSettingsManager), viewModel.sipManager.activeCallDurationLiveData) {
                            startActivity(OneActiveCallActivity.newIntent(applicationContext, sipCall.participantInfoList.firstOrNull(), null))
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
                        selectedPhoneNumber?.let {
                            viewModel.updateBlockingFeature(it)
                        }
                    }
                    updateContactDetailsFragment()
                    updateTitleTextView()
                }
                BlockingState.FailureBlocking, BlockingState.FailureUnblocking -> {
                    showSnackBar(R.string.fa_times_circle, resources.getString(message), SnackStyle.Error)
                    viewModel.clearBlockingFeatureEvent()
                }
            }
        }
    }

    private fun updateContactDetailsFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        transaction.replace(R.id.connect_contact_detail_list_fragment_container_layout, ConnectContactDetailsFragment())
        transaction.commit()
    }

    private var createContactLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) { finish() }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ConnectContactDetailsViewModel::class.java]

        setContentView(bindViews())

        viewModel.contactLiveData.observe(this, contactValuesChangedObserver)
        viewModel.activeCallLiveData.observe(this, activeCallObserver)
        viewModel.blockingFeatureEvent.observe(this, blockingFeatureObserver)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.connectGrey01)

        // Setup toolbar
        ViewUtil.startAlphaAnimation(collapsedTitleTextView, 0, View.GONE)
        appBarLayout.addOnOffsetChangedListener(this)
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.connectGrey01))

        setSupportActionBar(toolbar)
        title = ""
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))

        backArrowView.setOnClickListener { onBackPressed() }

        if (intent.hasExtra(CONNECT_CONTACT)) {
            val contact = intent.getSerializableExtra(CONNECT_CONTACT) as NextivaContact
            viewModel.setContact(contact)
            nextivaContact = contact

            // Load list fragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.connect_contact_detail_list_fragment_container_layout, ConnectContactDetailsFragment())
            transaction.commit()
        }

        viewModel.groupId.observe(this, Event.EventObserver { groupId ->
            startActivity(viewModel.getSmsIntent(groupId.first))
        })

        viewModel.loading.observe(this){ loading ->
            loadingLayout.visibility = if(loading) View.VISIBLE else View.GONE
        }

        updateTitleTextView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_connect_contact_details, menu)

        MenuUtil.setMenuContentDescriptions(menu)
        menu.findItem(R.id.connect_contact_details_favorite)?.icon = FontDrawable(this, R.string.fa_star,
            if (viewModel.nextivaContact?.isFavorite == true) Enums.FontAwesomeIconType.SOLID else Enums.FontAwesomeIconType.REGULAR)
            .withColor(ContextCompat.getColor(this,
                if (viewModel.nextivaContact?.isFavorite == true) R.color.connectPrimaryYellow else R.color.connectGrey09))
            .withSize(R.dimen.font_awesome_menu_icon)
        menu.findItem(R.id.connect_contact_details_options)?.icon = FontDrawable(this, R.string.fa_ellipsis_v, Enums.FontAwesomeIconType.REGULAR)
            .withColor(ContextCompat.getColor(this, R.color.connectGrey09))
            .withSize(R.dimen.font_awesome_menu_icon)

        val contactType = viewModel.nextivaContact?.contactType
        if (contactType == ContactTypes.CONNECT_USER ||
            contactType == ContactTypes.CONNECT_TEAM ||
            contactType == ContactTypes.CONNECT_CALL_FLOW ||
            contactType == ContactTypes.CONNECT_CALL_CENTERS
        ) {
            menu.removeItem(R.id.connect_contact_details_options)
        }

        if (viewModel.nextivaContact?.contactType == ContactTypes.CONNECT_UNKNOWN) {
            menu.removeItem(R.id.connect_contact_details_favorite)
            menu.removeItem(R.id.connect_contact_details_options)
        }
        else if(viewModel.isXbertContact())
        {
            menu.removeItem(R.id.connect_contact_details_options)
        }



        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.connect_contact_details_favorite -> {
                viewModel.toggleFavorite { success ->
                    if (success) {
                        invalidateOptionsMenu()
                    }
                }
                return true
            }
            R.id.connect_contact_details_options -> {
                viewModel.nextivaContact?.let { contact ->
                    displayOptionsMenu(contact)
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun displayOptionsMenu(contact: NextivaContact) {
        BottomSheetContactDetailsMenu(
            isPrivate = (contact.contactType == ContactTypes.CONNECT_PERSONAL) ||
                    sessionManager.hasContactManagementPrivilege(),
            editAction = {
                startActivity(CreateBusinessContactActivity.newIntent(this, contact))
            },
            deleteAction = {
                deleteAction(contact)
            },
            cancelAction = {}
        ).show(
            supportFragmentManager,
            null
        )
    }

    private fun showSnackBar(
        icon: Int?,
        message: String,
        snackStyle: SnackStyle = SnackStyle.Standard,
        undoAction: (() -> Unit)? = null,
    ) {
        val duration = BaseTransientBottomBar.LENGTH_LONG
        CustomSnackbar.make(parentLayout, duration, this, false, snackStyle).apply {
            setText(message)
            icon?.let { setFontAwesomeIcon(getString(icon)) }
            undoAction?.let { enableUndoAction { undoAction.invoke() } }
            show()
        }
    }

    private fun deleteAction(contact: NextivaContact) {
        val helper = ContactDeleteHelper(
            this,
            supportFragmentManager,
            compositeDisposable,
            dbManager,
            platformContactsRepository,
            smsManagementRepository,
            conversationRepository
        )
        helper.deleteContactConfirmation(
            contact = contact,
            resetCallback = { viewModel.resetContactLiveData() },
            onSuccessCallback = { finish() }
        )
    }

    private fun setHeaderViews(contact: NextivaContact) {
        contactDetailHeaderView.setNameText(contact)
        contact.avatarInfo.let {
            when (viewModel.nextivaContact?.contactType) {
                ContactTypes.CONNECT_TEAM ->
                    it.iconResId = R.drawable.avatar_team
                ContactTypes.CONNECT_CALL_FLOW ->
                    it.iconResId = R.drawable.avatar_callflow
                ContactTypes.CONNECT_CALL_CENTERS ->
                    it.iconResId = R.drawable.avatar_callcenter
                else -> Unit
            }

            contactDetailHeaderView.setAvatar(it)
        }

        if (contact.contactType == ContactTypes.CONNECT_USER) {
            if (viewModel.nextivaContact?.contactType != ContactTypes.CONNECT_TEAM &&
                viewModel.nextivaContact?.contactType != ContactTypes.CONNECT_CALL_FLOW &&
                viewModel.nextivaContact?.contactType != ContactTypes.CONNECT_CALL_CENTERS) {
                // Doug - for the initial release we are not including Chat or Video Calls so this needs to be hidden to any customer.
                //        Outside of the release or rc we can enable the flags internally as we continue working on adding the features.
                if (sessionManager.isTeamchatEnabled(this)) {
                    quickActionChatButton.visibility = View.VISIBLE
                    quickActionChatButton.setEnabled(true) {
                        viewModel.getChatIntent()?.let { intent ->
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        quickActionCallButton.visibility = View.VISIBLE
        val callingPhoneNumbers = contact.allPhoneNumbers?.filter { it.type != Enums.Contacts.PhoneTypes.FAX }
        quickActionCallButton.setEnabled(callingPhoneNumbers?.isNotEmpty() == true) {
            callingPhoneNumbers?.let { phoneNumbers ->
                if (phoneNumbers.size > 1) {
                    ConnectMenuDialog(viewModel.getPhoneNumberListItems(phoneNumbers)) { phoneNumber ->
                        (phoneNumber as? PhoneNumber)?.let { number ->
                            number.number?.extractFirstNumber()?.let { numberToDial ->
                                val callingNumber =
                                    if (contact.contactType == ContactTypes.CONNECT_USER) {
                                        CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(numberToDial)
                                    } else {
                                        numberToDial
                                    }
                                viewModel.processCallInfo(
                                    this,
                                    Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS,
                                    callingNumber,
                                    Enums.Sip.CallTypes.VOICE,
                                    metadata = number.number?.extractDtfmTone(),
                                    processCallInfoCallBack = this
                                )
                            }
                        }
                    }.show(supportFragmentManager, null)

                } else {
                    phoneNumbers.firstOrNull()?.number.let { number ->
                        number?.extractFirstNumber()?.let { numberToDial ->
                            val callingNumber =
                                if (contact.contactType == ContactTypes.CONNECT_USER) {
                                    CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(numberToDial)
                                } else {
                                    numberToDial
                                }
                            viewModel.processCallInfo(
                                this,
                                Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS,
                                callingNumber,
                                Enums.Sip.CallTypes.VOICE,
                                number.extractDtfmTone(),
                                this
                            )
                        }
                    }
                }
            }
        }

        val showSms = viewModel.isShowSms()
        val isNotCallFlow = viewModel.nextivaContact?.contactType != ContactTypes.CONNECT_CALL_FLOW
        val isNotCallCentre = viewModel.nextivaContact?.contactType != ContactTypes.CONNECT_CALL_CENTERS
        if (showSms && isNotCallFlow && isNotCallCentre) {
            quickActionSmsButton.visibility = View.VISIBLE

            val smsPhoneNumbers = contact.allPhoneNumbers?.filter { CallUtil.isValidSMSNumber(it.strippedNumber) }
            quickActionSmsButton.setEnabled(smsPhoneNumbers?.isNotEmpty() == true) {
                smsPhoneNumbers?.let { smsNumbers ->
                    if (sessionManager.isSmsLicenseEnabled && (sessionManager.isSmsProvisioningEnabled || viewModel.sessionManager.isTeamSmsEnabled)) {
                        if (contact.contactType == ContactTypes.CONNECT_TEAM) {
                            if (viewModel.isTeamSmsEnabled(contact)) {
                                sendSms(smsNumbers.filter { CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(it.strippedNumber ?: "").length > 9 })

                            } else {
                                dialogManager.showDialog(this,
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

                        dialogManager.showDialog(this,
                            dialogTitle,
                            dialogBody,
                            getString(R.string.general_ok)
                        ) { _, _ -> }
                    }
                }
            }
        }

        if (viewModel.nextivaContact?.contactType != ContactTypes.CONNECT_TEAM &&
            viewModel.nextivaContact?.contactType != ContactTypes.CONNECT_CALL_FLOW &&
            viewModel.nextivaContact?.contactType != ContactTypes.CONNECT_CALL_CENTERS) {
            // Doug - for the initial release we are not including Chat or Video Calls so this needs to be hidden to any customer.
            //        Outside of the release or rc we can enable the flags internally as we continue working on adding the features.
            if (sessionManager.isMeetingEnabled(this) && !viewModel.isXbertContact()) {
                quickActionVideoButton.visibility = View.VISIBLE
            }
        }

        contact.contactType.let {
            if (it != ContactTypes.CONNECT_USER
                && it != ContactTypes.CONNECT_SHARED
                && it != ContactTypes.CONNECT_PERSONAL
                && it != ContactTypes.LOCAL
                && it != ContactTypes.CONNECT_TEAM
                && it != ContactTypes.CONNECT_CALL_FLOW
                && it != ContactTypes.CONNECT_CALL_CENTERS
                && !viewModel.isXbertContact()
            ) {
                // hide video icon for unsaved contact
                quickActionVideoButton.visibility = View.GONE
            }
        }

        when (contact.contactType) {
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

        collapsedTitleTextView.text = contact.uiName

        if (TextUtils.equals(getString(R.string.app_environment), getString(R.string.environment_rc)) || TextUtils.equals(getString(R.string.app_environment), getString(R.string.environment_prod))) {
            quickActionChatButton.visibility = View.GONE
            quickActionVideoButton.visibility = View.GONE
        }

        if (viewModel.isXbertContact()) {
            quickActionVideoButton.visibility = View.GONE
        }
    }

    private fun sendSms(smsNumbers: List<PhoneNumber>) {
        if (smsNumbers.size > 1) {
            ConnectMenuDialog(viewModel.getPhoneNumberListItems(smsNumbers)) { phoneNumber ->
                (phoneNumber as? PhoneNumber)?.strippedNumber?.let { strippedNumber ->
                    viewModel.fetchGroupId(strippedNumber)
                }

            }.show(supportFragmentManager, null)

        } else {
            smsNumbers.firstOrNull()?.strippedNumber?.let {
                viewModel.fetchGroupId(it)
            }
        }
    }

    fun bindViews(): View {
        val binding = ActivityConnectContactDetailsBinding.inflate(layoutInflater)

        toolbar = binding.connectContactDetailsToolbar
        titleTextView = binding.connectContactDetailsToolbarTitleTextView
        contactDetailHeaderView = binding.connectContactDetailsContactHeaderView
        collapsingToolbarLayout = binding.connectContactDetailsCollapsingToolbar
        quickActionCallButton = binding.connectContactDetailsCallTextView
        quickActionSmsButton = binding.connectContactDetailsSmsTextView
        quickActionVideoButton = binding.connectContactDetailsVideoTextView
        quickActionChatButton = binding.connectContactDetailsChatTextView
        toolbarImage = binding.connectContactDetailsToolbarLock
        appBarLayout = binding.connectContactDetailAppBarLayout
        parentLayout = binding.connectContactDetailCoordinatorLayout
        collapsedTitleTextView = binding.connectContactDetailsToolbarCollapsedTitleTextView
        addContactSubMenuLayout = binding.connectContactDetailsSubmenuLayout
        addToContactSubMenuButton = binding.connectContactDetailsSubmenu
        blockingFeatureButton = binding.connectContactDetailsBlockingButton
        backArrowView = binding.backArrowInclude.backArrowView
        loadingLayout = binding.loadingLayout
        activeCallBar = binding.activeCallToolbar

        return binding.root
    }

    private fun loadButtons(contact: NextivaContact) {

        viewModel.addContactPressed.observe(this, Event.EventObserver { index ->
            when (index) {
                0 -> firstAddSubMenuOnClick?.invoke()
                1 -> secondAddSubMenuOnClick?.invoke()
                2 -> thirdAddSubMenuOnClick?.invoke()
            }
        })

        firstAddSubMenuOnClick = {
            viewModel.nextivaContact?.let { contact ->
                contact.allPhoneNumbers?.firstOrNull()?.strippedNumber?.let { phoneNumber ->
                    createContactLauncher.launch(
                        CreateBusinessContactActivity.newIntent(
                            this,
                            phoneNumber
                        )
                    )
                }
            }
        }

        secondAddSubMenuOnClick = {
            viewModel.nextivaContact?.let { contact ->
                contact.allPhoneNumbers?.firstOrNull()?.strippedNumber?.let { phoneNumber ->
                    BottomSheetSelectContactList(
                        intArrayOf(
                            ContactTypes.CONNECT_SHARED,
                            ContactTypes.CONNECT_PERSONAL
                        ),
                        phoneNumber,
                        null
                    )
                        .show(supportFragmentManager, null)
                }
            }
        }

        thirdAddSubMenuOnClick = {
            viewModel.nextivaContact?.let { contact ->
                contact.allPhoneNumbers?.firstOrNull()?.strippedNumber?.let { phoneNumber ->
                    intentManager.addToLocalContacts(
                        this,
                        Enums.Analytics.ScreenName.BOTTOM_SHEET_CONTACT_DETAILS,
                        "",
                        phoneNumber,
                        null,
                        null,
                        null
                    )
                }
            }
        }

        addToContactSubMenuButton.setOnClickListener {
            BottomSheetAddContactMenu.newInstance(
                getString(R.string.connect_call_details_add_to_contacts),
                getString(R.string.connect_call_details_add_to_existing_contact),
                getString(R.string.connect_call_details_add_to_local_contacts)
            ).show(supportFragmentManager, "AddContactMenu")
        }

        addToContactSubMenuButton.visibility = if (contact.contactType == ContactTypes.CONNECT_UNKNOWN)
            View.VISIBLE
        else
            View.GONE
    }

    private fun updateTitleTextView() {
        titleTextView.setContactTypeLabel(nextivaContact)
        val allBlocked = nextivaContact?.phoneNumbers
            ?.map { phoneNumber ->
                val numberWithoutExtension = phoneNumber.number?.substringBefore("x")
                viewModel.isNumberBlocked(numberWithoutExtension ?: "")
            }
            ?.all { it } ?: false

        val firstAllPhoneNumber = nextivaContact?.allPhoneNumbers?.firstOrNull()?.number
        val firstPhoneNumber = nextivaContact?.phoneNumbers?.firstOrNull()?.number
        val hasNothing = firstAllPhoneNumber == null && firstPhoneNumber == null
        val isExtension = isExtensionNumber(firstAllPhoneNumber)

        if (allBlocked && !isExtension && !hasNothing) {
            titleTextView.setBlockedContactTypeLabel()
        }
        addOrUpdateBlockingFeatureButton()
    }

    private fun addOrUpdateBlockingFeatureButton() {
        if (!viewModel.isBlockingFeatureEnabled() ||
            nextivaContact?.contactType != ContactTypes.CONNECT_UNKNOWN ||
            nextivaContact?.phoneNumbers?.firstOrNull()?.type == Enums.Contacts.PhoneTypes.WORK_EXTENSION ||
            isExtensionNumber(nextivaContact?.phoneNumbers?.firstOrNull()?.number)) {
            blockingFeatureButton.visibility = View.GONE
            return
        }

        var buttonText = getString(R.string.connect_call_details_block_number)
        var buttonTextColor = ContextCompat.getColor(this, R.color.connectSecondaryRed)
        var bottomSheetTitle = resources.getString(R.string.connect_call_details_block_number_title)
        var bottomSheetSubtitle = resources.getString(R.string.connect_call_details_block_number_message)
        var bottomSheetPrimaryButtonText = resources.getString(R.string.connect_call_details_block_number)

        nextivaContact?.phoneNumbers?.firstOrNull()?.number?.let { phoneNumber ->
            if (viewModel.isNumberBlocked(phoneNumber)) {
                buttonTextColor = ContextCompat.getColor(this, R.color.connectPrimaryBlue)
                buttonText = resources.getString(R.string.connect_call_details_unblock_number)
                bottomSheetTitle = resources.getString(R.string.connect_call_details_unblock_number_title)
                bottomSheetSubtitle = resources.getString(R.string.connect_call_details_unblock_number_message)
                bottomSheetPrimaryButtonText = resources.getString(R.string.connect_call_details_unblock_number)
            }
        }

        blockingFeatureButton.visibility = View.VISIBLE
        blockingFeatureButton.text = buttonText
        blockingFeatureButton.setTextColor(buttonTextColor)
        blockingFeatureButton.setBackgroundColor(Color.TRANSPARENT)
        blockingFeatureButton.setOnClickListener {
            showBottomSheetDialog(null, bottomSheetTitle, bottomSheetSubtitle, bottomSheetPrimaryButtonText)
        }

        viewModel.clearBlockingFeatureEvent()
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= percentageToShowTitleAtToolbar) {
            if (!isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                ViewUtil.startAlphaAnimation(titleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)

                if (viewModel.nextivaContact?.contactType == ContactTypes.CONNECT_PERSONAL ||
                    viewModel.nextivaContact?.contactType == ContactTypes.CONNECT_SHARED) {
                    ViewUtil.startAlphaAnimation(toolbarImage, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                }

                isTheTitleVisible = true
            }
        } else {
            if (isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                ViewUtil.startAlphaAnimation(titleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)

                if (viewModel.nextivaContact?.contactType == ContactTypes.CONNECT_PERSONAL ||
                    viewModel.nextivaContact?.contactType == ContactTypes.CONNECT_SHARED) {
                    ViewUtil.startAlphaAnimation(toolbarImage, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                }

                isTheTitleVisible = false
            }
        }

        contactDetailHeaderView.visibility = if (isTheTitleVisible) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.totalScrollRange?.let { maxScroll ->
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
            handleToolbarTitleVisibility(percentage)
        }
    }

    override fun onParticipantInfoProcessed(activity: Activity, analyticsScreenName: String, participantInfo: ParticipantInfo, retrievalNumber: String?, compositeDisposable: CompositeDisposable) {
        viewModel.makeCall(activity, analyticsScreenName, participantInfo)
    }

    override fun onBlockUnblockAction(phoneNumber: String) {
        var bottomSheetTitle = resources.getString(R.string.connect_call_details_block_number_title)
        var bottomSheetSubtitle = resources.getString(R.string.connect_call_details_block_number_message)
        var bottomSheetPrimaryButtonText = resources.getString(R.string.connect_call_details_block_number)

        if (viewModel.isNumberBlocked(phoneNumber)) {
            bottomSheetTitle = resources.getString(R.string.connect_call_details_unblock_number_title)
            bottomSheetSubtitle = resources.getString(R.string.connect_call_details_unblock_number_message)
            bottomSheetPrimaryButtonText = resources.getString(R.string.connect_call_details_unblock_number)
        }

        selectedPhoneNumber = phoneNumber
        showBottomSheetDialog(phoneNumber, bottomSheetTitle, bottomSheetSubtitle, bottomSheetPrimaryButtonText)
    }

    private fun showBottomSheetDialog(
        phoneNumber: String?,
        bottomSheetTitle: String,
        bottomSheetSubtitle: String,
        bottomSheetPrimaryButtonText: String
    ) {
        supportFragmentManager.let { fm ->
            BottomSheetDeleteConfirmation.newInstance(
                title = bottomSheetTitle,
                subtitle = bottomSheetSubtitle,
                primaryButtonText = bottomSheetPrimaryButtonText,
                showShowAgainCheckbox = false,
                showCloseButton = false,
                deleteAction = {
                    val phone = phoneNumber ?: nextivaContact?.phoneNumbers?.firstOrNull()?.number
                    phone?.let {
                        selectedPhoneNumber = it
                        viewModel.updateBlockingFeature(it)
                    }
                },
                cancelAction = { }
            ).show(fm, null)
        }
    }

    override fun animateContentIn(delay: Int, duration: Int) { }

    override fun animateContentOut(delay: Int, duration: Int) { }
}
