package com.nextiva.nextivaapp.android.features.messaging.view

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.nextiva.nextivaapp.android.ConnectNewTextActivity
import com.nextiva.nextivaapp.android.OneActiveCallActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentInfo
import com.nextiva.nextivaapp.android.databinding.BottomSheetNewSmsBinding
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.BottomSheetNewMessageViewModel
import com.nextiva.nextivaapp.android.fragments.ConnectSmsContactsListFragment
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.MessageUtil
import com.nextiva.nextivaapp.android.util.extensions.extractFirstNumber
import com.nextiva.nextivaapp.android.util.extensions.isNull
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.orTrue
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.util.extensions.serializable
import com.nextiva.nextivaapp.android.util.extensions.withFontAwesomeDrawable
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.ConnectMaxHeightScrollView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetNewMessage : BaseBottomSheetDialogFragment(tintedAppBar = true), TextWatcher {

    companion object {
        const val MAX_CHIP_LINES_COUNT = 2.5f
        const val CHIP_MARGINS_AND_PADDING = 14
        const val conversationType = Enums.Chats.ConversationTypes.SMS
        const val FRAGMENT_TAG_SEARCH = "search_fragment"
        const val FRAGMENT_TAG_SMS = "sms_fragment"

        private const val EXTRA_SINGLE_IMAGE_ATTACHMENT = "EXTRA_SINGLE_IMAGE_ATTACHMENT"
        private const val PRELOADED_CONTACTS = "PRELOADED_CONTACTS"
        private const val CONVERSATION_GROUP_ID = "CONVERSATION_GROUP_ID"

        fun newInstance(image: Uri? = null): BottomSheetNewMessage {
            val args = Bundle()
            image?.let {
                args.putString(EXTRA_SINGLE_IMAGE_ATTACHMENT, it.toString())
            }

            val fragment = BottomSheetNewMessage()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(preLoadedContacts: ArrayList<NextivaContact>, groupId: String?) = BottomSheetNewMessage().apply {
            dialogAnimation = dialog?.window?.attributes?.windowAnimations
            dialog?.window?.setWindowAnimations(-1)
            dialog?.window?.setWindowAnimations(-1)
            arguments = Bundle().apply {
                putSerializable(PRELOADED_CONTACTS, preLoadedContacts)
                putString(CONVERSATION_GROUP_ID, groupId)
            }
        }
    }

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var avatarManager: AvatarManager
    @Inject
    lateinit var dialogManager: DialogManager
    private lateinit var viewModel: BottomSheetNewMessageViewModel

    private lateinit var cancelIcon: RelativeLayout
    private lateinit var cancelIconFontTextView: FontTextView
    private lateinit var searchIconFontTextView: FontTextView
    private lateinit var chipGroupScrollview: ConnectMaxHeightScrollView
    private lateinit var contactsChipGroup: ChipGroup
    private lateinit var editTextSearchBox: EditText
    private lateinit var editTextSearchBoxHint: EditText
    private lateinit var toConstraintLayout: ConstraintLayout
    private lateinit var header: LinearLayout
    private lateinit var sendToLayout: ConstraintLayout
    private lateinit var sendToTitle: TextView
    private lateinit var sendToSubtitle: TextView
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var recentTextView: TextView
    private lateinit var loadingLayout: View
    private lateinit var activeCallBar: ComposeView

    private lateinit var chatConversationFragment: ConversationFragment
    private lateinit var contactsListFragment: ConnectSmsContactsListFragment

    private var selectedChip: Chip? = null
    private var lastPositionOfLastChip = 0
    private var isSearchView = false

    private var isCallOptionsDisabled = true
    private var isNewChat = true
    var temporalTextFromMessageTextFields = ""
    var temporalAttachmentFromMessage: AttachmentInfo? = null
    var dialogAnimation: Int? = null

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[BottomSheetNewMessageViewModel::class.java]

        val attachmentUri = arguments?.getString(EXTRA_SINGLE_IMAGE_ATTACHMENT)
        chatConversationFragment = ConversationFragment.newInstance(
                Enums.Chats.ConversationTypes.SMS, null, null, isCallOptionsDisabled, isNewChat, attachmentUri)

        contactsListFragment = ConnectSmsContactsListFragment()
        contactsListFragment.listenForSearchResultsOnAdapter { total ->
            recentTextView.text = requireContext().getString(R.string.connect_contacts_search_results_count, total)
        }

        viewModel.fetchRecentContacts()

        showFullHeight = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_new_sms, container, false)
        view?.let { bindViews(view) }

        editTextSearchBox.addTextChangedListener(this)

        viewModel.addSelectedContact.observe(viewLifecycleOwner) {
            nextivaContactSelected(it)
        }
        viewModel.activeCallLiveData.observe(viewLifecycleOwner, activeCallObserver)

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if(isLoading) {
                loadingLayout.visibility = View.VISIBLE
            } else {
                loadingLayout.visibility = View.GONE
            }
        }

        viewModel.groupId.observe(viewLifecycleOwner) {
            updateGroupId(it)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.currentState.collect { state ->
                    if (state == BottomSheetNewMessageViewModel.CurrentState.Sms) {
                        switchToSmsView()
                    } else {
                        if (state == BottomSheetNewMessageViewModel.CurrentState.Recent) {
                            recentTextView.text =
                                requireContext().getString(R.string.connect_sms_recent_searches)
                        } else {
                            recentTextView.text = requireContext().getString(
                                R.string.connect_contacts_search_results_count,
                                0
                            )
                        }
                        switchToSearchView()
                    }
                }
            }
        }

        cancelIcon.setOnClickListener {
            if (viewModel.selectedContactsCount() > 0 && isSearchView) {
                editTextSearchBox.setText("")
            } else {
                dismiss()
                (activity as? ConnectNewTextActivity)?.finish()
            }
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        chatConversationFragment.saveMessageAsDraftIfNeeded(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null) {
            arguments?.serializable<ArrayList<NextivaContact>>(PRELOADED_CONTACTS)?.let { preLoadedNumbers ->
                addPreloadedContact(
                    preLoadedNumbers.toList(),
                    arguments?.getString(CONVERSATION_GROUP_ID)
                )
                dialogAnimation?.let { anim -> dialog?.window?.setWindowAnimations(anim) }
            }
        } else {
            viewModel.selectedContacts.forEach { contact ->
                addMessageContact(contact)
            }
        }

        viewModel.fetchRecentContacts()
    }

    fun getDefaultTeamName() = viewModel.defaultTeamName

    fun setDefaultSendTeamName(name: String) {
        viewModel.defaultTeamName = name
    }

    private fun switchToSmsView() {
        if(viewModel.groupId.value?.isNotBlank().orFalse() && isSearchView) {
            isSearchView = false
            recentTextView.visibility = View.GONE
            childFragmentManager
                .beginTransaction()
                .replace(R.id.bottom_sheet_connect_new_sms_container, chatConversationFragment, FRAGMENT_TAG_SMS)
                .commitNow()
        }
        return
    }

    private fun updateGroupId(groupId: String?) {

        Log.d("BottomSheetNewMessage", "Updating GroupId: [$groupId]")
        if(groupId?.isNull().orTrue() || viewModel.selectedContacts.size.orZero() == 0){
            return
        }

        getInputManager()?.hideSoftInputFromWindow(editTextSearchBox.windowToken, 0)
        val attachment = chatConversationFragment.selectedAttachments.value?.firstOrNull()
        val attachmentName = MessageUtil.getFileNameWithOutExtension(attachment?.uri, requireContext()).orEmpty()
        if (attachment != null && !attachmentName.startsWith("camera-")) {
            temporalAttachmentFromMessage = attachment
        }
        if (chatConversationFragment.textFromMessageTextField.isNotEmpty()) {
            temporalTextFromMessageTextFields = chatConversationFragment.textFromMessageTextField
        }

        val selectedPhoneNumbers = ArrayList<String>()
        val participants = ArrayList<SmsParticipant>()

        viewModel.selectedContacts.forEach { contact ->
            contact.allPhoneNumbers?.firstOrNull()?.strippedNumber?.let { strippedNumber ->
                if (strippedNumber.isNotEmpty()) {
                        selectedPhoneNumbers.add(strippedNumber)
                        val participant = SmsParticipant(contact.uiName,
                            null,
                            CallUtil.getStrippedNumberWithCountryCode(strippedNumber),
                            contact.userId,
                            null,
                            null)
                        participant.representingTeam = contact.representingTeam
                        participants.add(participant)
                    }
                }
            }

            var groupValue = ""
            val ourNumber = sessionManager.userDetails?.telephoneNumber?.let { CallUtil.getCountryCode() + it }

            if (!ourNumber.isNullOrEmpty()) {
                groupValue = ourNumber
            }

            if (selectedPhoneNumbers.size >= 1) {
                val groupValueBuilder = StringBuilder(groupValue)

                for (local in selectedPhoneNumbers) {
                    if (CallUtil.isCountryCodeAdded(local)) {
                        groupValueBuilder.append(",").append(local)
                    } else {
                        groupValueBuilder.append(",").append(" 1").append(local)
                    }
                }

                groupValue = groupValueBuilder.toString()

                val sortingGroupValueList = ArrayList(groupValue.trim().replace("\\s".toRegex(), "").split(","))
                sortingGroupValueList.removeAll(Collections.singleton(""))
                sortingGroupValueList.sortWith { s: String, t1: String -> s.trim().toLong().compareTo(t1.trim().toLong()) }

                groupValue = TextUtils.join(",", sortingGroupValueList).trim()

                ourNumber?.let {
                    sortingGroupValueList.remove(it)
                }

                isCallOptionsDisabled = false

                val conversationDetails = SmsConversationDetails(groupValue, participants, ourNumber ?: "", sessionManager.currentUser?.userUuid ?: "")
                conversationDetails.groupId = groupId

                val attachmentUri = arguments?.getString(EXTRA_SINGLE_IMAGE_ATTACHMENT)
                chatConversationFragment = ConversationFragment.newInstance(
                    Enums.Chats.ConversationTypes.SMS,
                    sortingGroupValueList,
                    conversationDetails,
                    isCallOptionsDisabled,
                    isNewChat,
                    attachmentUri)

                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(
                            R.id.bottom_sheet_connect_new_sms_container,
                            chatConversationFragment,
                            FRAGMENT_TAG_SMS
                )
                transaction.commit()
                childFragmentManager.executePendingTransactions()

                isSearchView = false
                recentTextView.visibility = View.GONE

                chatConversationFragment.validateSMSCampaignBanner()
            }
    }

    private fun getInputManager() = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

    private fun switchToSearchView() {

        if(isSearchView) return

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(
            R.id.bottom_sheet_connect_new_sms_container,
            contactsListFragment,
            FRAGMENT_TAG_SEARCH
        )
        transaction.commit()
        childFragmentManager.executePendingTransactions()
        if (viewModel.selectedContacts.isEmpty()) {
            temporalAttachmentFromMessage = null
            temporalTextFromMessageTextFields = ""
            chatConversationFragment.textFromMessageTextField = ""
            chatConversationFragment.selectedAttachments.value = emptyList()
        }

        isSearchView = true
        recentTextView.visibility = View.VISIBLE

        chatConversationFragment.selectedAttachments.value?.firstOrNull()?.let {
            if (arguments == null) {
                arguments = Bundle()
            }
            arguments?.putString(EXTRA_SINGLE_IMAGE_ATTACHMENT, it.uri.toString())
        }

        if (viewModel.selectedContacts.isNotEmpty()) {
            editTextSearchBox.requestFocus()
            getInputManager()?.showSoftInput(editTextSearchBox, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetNewSmsBinding.bind(view)

        cancelIcon = binding.cancelIconInclude.closeIconView
        cancelIconFontTextView = binding.cancelIconInclude.closeIconFontTextView
        searchIconFontTextView = binding.chatSmsToTextView
        contactsChipGroup = binding.chatSmsContactsInChatChipGroup
        editTextSearchBox = binding.editTextSearchBox
        editTextSearchBoxHint = binding.editTextSearchBoxHint
        toConstraintLayout = binding.toConstraintLayout
        chipGroupScrollview = binding.chipGroupScrollview
        header = binding.header
        coordinatorLayout = binding.bottomSheetConnectNewSmsCoordinator
        sendToLayout = binding.bottomSheetConnectNewSmsSendToLayout
        sendToTitle = binding.bottomSheetConnectNewSmsSendTo.listItemConnectContactName
        sendToSubtitle = binding.bottomSheetConnectNewSmsSendTo.listItemConnectContactSearchMatch
        recentTextView = binding.recentTextView
        loadingLayout = binding.loadingLayout
        sendToSubtitle.visibility = View.VISIBLE
        activeCallBar = binding.activeCallToolbar

        sendToLayout.setOnClickListener {
            viewModel.getContactFromPhoneNumber(CallUtil.getStrippedPhoneNumber(sendToTitle.text.toString())) {
                nextivaContactSelected(it ?: NextivaContact(sendToTitle.text.toString(), Enums.Contacts.ContactTypes.CONNECT_UNKNOWN))
            }
        }

        binding.bottomSheetConnectNewSmsSendTo.listItemConnectContactAvatarView.setAvatar(AvatarInfo.Builder()
                .isConnect(true)
                .setFontAwesomeIconResId(R.string.fa_user)
                .build())

        setEdittextAndChipsProperties()
    }

    private fun setEdittextAndChipsProperties() {
        contactsChipGroup.isSingleSelection = true
        setSmallHint(editTextSearchBox, requireContext().getString(R.string.connect_new_chat_search_box_hint))

        editTextSearchBox.setOnFocusChangeListener { _, isFocused ->
            if (!isFocused) {
                contactsChipGroup.clearCheck()
                selectedChip = null
            }
            contactsChipGroup.children.forEach { chip ->
                if (chip is Chip) {
                    val newState = if (isFocused) ChipState.UNSELECTED else ChipState.INACTIVE
                    updateChipUI(chip, newState)
                }
            }
        }

        editTextSearchBox.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                selectedChip?.let { chip ->
                    (chip.tag as? NextivaContact)?.let { contact ->
                        viewModel.removeContact(contact)
                        contactsChipGroup.removeView(chip)
                        Handler(Looper.getMainLooper())
                                .postDelayed({ selectedChip = null }, 200)
                    }
                    if (contactsChipGroup.childCount < 2) {
                        searchIconFontTextView.visibility = View.VISIBLE
                    }
                    if (viewModel.selectedContacts.isEmpty()) {
                        recentTextView.text = requireContext().getString(R.string.connect_sms_recent_searches)
                    }
                    setSearchBoxHint()
                    return@setOnKeyListener true
                } ?: run {
                    val lastChip = if (contactsChipGroup.childCount > 1) {
                        contactsChipGroup.getChildAt(contactsChipGroup.childCount - 2)
                    } else null
                    if (lastChip is Chip && editTextSearchBox.text.isNullOrEmpty()) {
                        // Delay needed to avoid evaluating (keyCode == KeyEvent.KEYCODE_DEL)
                        // at the same time and avoid deleting the selected (last) chip
                        Handler(Looper.getMainLooper())
                            .postDelayed({ lastChip.isChecked = true }, 200)
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }
    }

    private fun addMessageContact(nextivaContact: NextivaContact) {
        val strippedNumber = nextivaContact.allPhoneNumbers?.firstOrNull { CallUtil.isValidSMSNumber(it.strippedNumber) }?.strippedNumber

        if (CallUtil.isValidSMSNumber(strippedNumber ?: "")) {
            val chip: Chip = getContactChip(nextivaContact)
            if (!chipGroupScrollview.isMaxHeightSet) {
                val params = chipGroupScrollview.layoutParams as ViewGroup.MarginLayoutParams
                chipGroupScrollview.setMaxHeight(chip.chipMinHeight * MAX_CHIP_LINES_COUNT +
                        params.bottomMargin + params.topMargin + CHIP_MARGINS_AND_PADDING)
            }

            if (!viewModel.isMessageContactAlreadyAdded(nextivaContact)) {
                viewModel.contactAdded(nextivaContact)
            }

            contactsChipGroup.addView(chip, contactsChipGroup.childCount - 1)

            if (contactsChipGroup.childCount > 1) {
                searchIconFontTextView.visibility = View.GONE
            }

            if (editTextSearchBox.text.toString().isNotEmpty()) {
                editTextSearchBox.setText("")
            }
            setSearchBoxHint()
            chipGroupScrollview.fullScroll(FOCUS_DOWN)
        } else {
            dialogManager.showDialog(
                    requireContext(),
                    getString(R.string.new_chat_invalid_number_dialog_title),
                    getString(R.string.new_chat_invalid_number_dialog_body),
                    getString(R.string.general_ok)
            ) { _: MaterialDialog?, _: DialogAction? ->
                //Delay needed to let the dialog finish dismissing.
                Handler(Looper.getMainLooper())
                        .postDelayed({ this.showKeyboard() }, 200)
                analyticsManager.logEvent(
                        Enums.Analytics.ScreenName.NEW_CHAT,
                        Enums.Analytics.EventName.BAD_SMS_NUMBER_INITIATED_DIALOG_OK_BUTTON_PRESSED
                )
            }
        }
    }

    private fun addPreloadedContact(contacts: List<NextivaContact>, groupId: String?) {
        viewModel.selectedContacts.addAll(contacts)
        contacts.forEach { nextivaContact ->
            val chip: Chip = getContactChip(nextivaContact)
            if (!chipGroupScrollview.isMaxHeightSet) {
                val params = chipGroupScrollview.layoutParams as ViewGroup.MarginLayoutParams
                chipGroupScrollview.setMaxHeight(
                    chip.chipMinHeight * MAX_CHIP_LINES_COUNT +
                            params.bottomMargin + params.topMargin + CHIP_MARGINS_AND_PADDING
                )
            }
            contactsChipGroup.addView(chip, contactsChipGroup.childCount - 1)

            if (contactsChipGroup.childCount > 1) {
                searchIconFontTextView.visibility = View.GONE
            }

            if (editTextSearchBox.text.toString().isNotEmpty()) {
                editTextSearchBox.setText("")
            }
        }
        setSearchBoxHint()
        chipGroupScrollview.fullScroll(FOCUS_DOWN)
        groupId?.let {
            viewModel.updateGroupId(groupId)
        } ?: run {
            viewModel.fetchGroupId()
        }
    }

    private fun showKeyboard() {
        editTextSearchBox.requestFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextSearchBox, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setSearchBoxHint() {
        if (contactsChipGroup.childCount > 1) {
            editTextSearchBox.hint = ""
            editTextSearchBox.setText("")

            contactsChipGroup.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                contactsChipGroup.children.lastOrNull()?.let {
                    val lastPosition = getXPositionOfLastChipInScreen(it)

                    if (lastPosition != lastPositionOfLastChip) {
                        lastPositionOfLastChip = lastPosition

                        val (secondaryHintVisibility, textTop, textBottom) = getVisibilityAndTextsForHints(lastPosition)
                        editTextSearchBoxHint.visibility = secondaryHintVisibility

                        setSmallHint(editTextSearchBoxHint, textBottom)
                        setSmallHint(editTextSearchBox, textTop)

                        if (contactsChipGroup.childCount == Constants.SMS.MAX_SMS_RECIPIENT_COUNT + 1) {
                            editTextSearchBoxHint.visibility = View.GONE
                            editTextSearchBoxHint.hint = ""
                            editTextSearchBox.hint = ""
                        }

                        if (secondaryHintVisibility == View.VISIBLE) {
                            chipGroupScrollview.post {
                                chipGroupScrollview.fullScroll(FOCUS_DOWN)
                            }
                        }
                    }
                }
            }

        } else {
            setSmallHint(editTextSearchBox, requireContext().getString(R.string.connect_new_chat_search_box_hint))
            editTextSearchBoxHint.visibility = View.GONE
        }
    }

    private fun getVisibilityAndTextsForHints(lastPosition: Int): Triple<Int, String, String> {
        val rangeTwoWordsTopAndOneBottom = (585..760)
        val rangeOneWordTopAndTwoBottom = (761..880)

        return when (lastPosition) {
            in rangeTwoWordsTopAndOneBottom -> {
                val (hintTop, hintBottom) = splitHintIfNecessary(1)
                Triple(View.VISIBLE, hintTop, hintBottom)
            }
            in rangeOneWordTopAndTwoBottom -> {
                val (hintTop, hintBottom) = splitHintIfNecessary(2)
                Triple(View.VISIBLE, hintTop, hintBottom)
            }
            else -> {
                val (hintTop, hintBottom) = splitHintIfNecessary(0)
                Triple(View.GONE, hintTop, hintBottom)
            }
        }
    }

    private fun setSmallHint(editText: EditText, hint: String) {
        val spannableString = SpannableString(hint)
        spannableString.setSpan(
                AbsoluteSizeSpan(16, true),
                0,
                hint.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        editText.hint = spannableString
    }

    private fun getXPositionOfLastChipInScreen(view: View): Int {
        val coordinates = IntArray(2)
        view.getLocationOnScreen(coordinates)
        return coordinates[0] // x position of last chip to know if we should split hint or not
    }

    private fun splitHintIfNecessary(case: Int): Pair<String, String> {
        val hint = requireContext().getString(R.string.connect_new_sms_add_more_contacts)
        val words = hint.split(" ")

        return when (case) {
            1 -> Pair("${words[0]} ${words[1]}", words[2]) // returns "Add more \n contacts"
            2 -> Pair(words[0], "${words[1]} ${words[2]}") // returns "Add \n more contacts"
            else -> Pair(hint, "") // returns "Add more contacts"
        }
    }

    private fun getContactChip(nextivaContact: NextivaContact?): Chip {
        val chip = Chip(context)
        nextivaContact?.let { contact ->
            if (TextUtils.isEmpty(nextivaContact.userId) && (nextivaContact.allPhoneNumbers?.size ?: 0) > 0) {
                nextivaContact.allPhoneNumbers?.firstOrNull()?.strippedNumber?.let { strippedNumber ->
                    chip.text = if (strippedNumber.length == 10 || strippedNumber.length == 11) {
                        CallUtil.phoneNumberFormattedDefaultCountry(strippedNumber)
                    } else {
                        strippedNumber
                    }
                }
            } else {
                chip.text = contact.uiName
            }
        }

        chip.setEnsureMinTouchTargetSize(false)
        chip.tag = nextivaContact
        chip.textStartPadding = 20f
        chip.textEndPadding = 24f
        chip.chipStrokeWidth = 1.0f
        chip.isCloseIconVisible = false
        chip.isCheckedIconVisible = false
        chip.isCheckable = true
        chip.isChecked = false

        updateChipUI(chip, ChipState.UNSELECTED)

        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showKeyboard()
                selectedChip = chip
                updateChipUI(chip, ChipState.SELECTED)
            } else {
                if (selectedChip == chip) { selectedChip = null }
                updateChipUI(chip, ChipState.UNSELECTED)
            }
        }

        return chip
    }

    private fun updateChipUI(chip: Chip, state: ChipState) {
        val (backgroundColor, textColor, strokeColor) = when (state) {
            ChipState.SELECTED -> Triple(
                    R.color.nextivaPrimaryBlue,
                    R.color.connectSecondaryLightBlue,
                    R.color.connectSecondaryBrightBlue
            )
            ChipState.UNSELECTED -> Triple(
                    R.color.connectPrimaryLightBlue,
                    R.color.nextivaPrimaryBlue,
                    R.color.connectSecondaryLightBlue
            )
            ChipState.INACTIVE -> Triple(
                    R.color.connectGrey01,
                    R.color.connectGrey10,
                    R.color.connectGrey03
            )
        }

        chip.setChipBackgroundColorResource(backgroundColor)
        chip.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), strokeColor))
    }

    enum class ChipState {
        SELECTED,
        UNSELECTED,
        INACTIVE,
    }

    private fun nextivaContactSelected(nextivaContact: NextivaContact) {
        if (viewModel.selectedContactsCount() >= Constants.SMS.MAX_SMS_RECIPIENT_COUNT) {
            dialogManager.showDialog(requireContext(),
                    getString(R.string.chat_details_max_recipient_title),
                    getString(R.string.chat_details_max_recipient_body, Constants.SMS.MAX_SMS_RECIPIENT_COUNT),
                    getString(R.string.general_okay)) { _, _ -> }

        } else if (!viewModel.isMessageContactAlreadyAdded(nextivaContact)) {
            val validSMSNumberCount = viewModel.smsPhoneNumberList(nextivaContact).count()
            if (validSMSNumberCount == 1) {
                addMessageContact(nextivaContact)
            } else if (validSMSNumberCount > 1) {
                showSelectPhoneNumberSheet(nextivaContact)
            } else {
                showSnackBarMessage(getString(R.string.new_chat_invalid_number_dialog_body))
            }
        } else if (isResumed) {
            showSnackBarMessage(getString(R.string.connect_sms_contact_already_added))
        }
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(coordinatorLayout,
            message,
            Snackbar.LENGTH_SHORT)
            .withFontAwesomeDrawable(FontDrawable(requireContext(),
                R.string.fa_check_circle,
                Enums.FontAwesomeIconType.SOLID)
                .withColor(ContextCompat.getColor(requireContext(),
                    R.color.connectPrimaryGreen))).show()
    }

    fun getSelectedContacts(): ArrayList<NextivaContact> {
        return viewModel.selectedContacts
    }

    private fun showSelectPhoneNumberSheet(nextivaContact: NextivaContact) {
        val phoneNumbers = nextivaContact.phoneNumbers ?: return
        BottomSheetSelectNumber(phoneNumbers) { selection ->
            selection?.let {
                val selectedContact = NextivaContact(nextivaContact)
                selectedContact.phoneNumbers = arrayListOf(it)
                addMessageContact(selectedContact)
            }
        }.show(requireActivity().supportFragmentManager, null)
    }

    // --------------------------------------------------------------------------------------------
    // TextListener Methods
    // --------------------------------------------------------------------------------------------
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {

        s.toString().extractFirstNumber()?.let { firstNumber ->
            CallUtil.phoneNumberFormattedDefaultCountry(firstNumber)
        }?.let { formattedNumber ->
            if(formattedNumber.isNotEmpty()) {
                sendToTitle.text = getString(R.string.connect_new_sms_send_to, formattedNumber)
                sendToSubtitle.text = formattedNumber
                sendToLayout.visibility = View.VISIBLE
                true
            } else {
                null
            }
        } ?: run {
            sendToLayout.visibility = View.GONE
        }

        s?.toString()?.let {
            if (it.trim().isNotEmpty()) {
                editTextSearchBoxHint.visibility = View.GONE
            } else {
                val (secondaryHintVisibility, _, textBottom) = getVisibilityAndTextsForHints(
                    lastPositionOfLastChip
                )
                editTextSearchBoxHint.visibility = secondaryHintVisibility
                setSmallHint(editTextSearchBoxHint, textBottom)
                if (secondaryHintVisibility == View.VISIBLE) {
                    chipGroupScrollview.post {
                        chipGroupScrollview.fullScroll(FOCUS_DOWN)
                    }
                }
            }
        }

        viewModel.onSearchTermUpdated(s.toString())
    }

    // --------------------------------------------------------------------------------------------

}
