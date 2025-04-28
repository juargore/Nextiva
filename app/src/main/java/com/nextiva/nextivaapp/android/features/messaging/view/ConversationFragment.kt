package com.nextiva.nextivaapp.android.features.messaging.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.ConnectNewTextActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.SMSMessages.FooterMessageType.ADMIN
import com.nextiva.nextivaapp.android.constants.Enums.SMSMessages.FooterMessageType.ADMIN_AND_USER
import com.nextiva.nextivaapp.android.constants.Enums.SMSMessages.FooterMessageType.USER
import com.nextiva.nextivaapp.android.core.common.FileUtil
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentAudioFilePlayer
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentInfo
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentMenuItems
import com.nextiva.nextivaapp.android.core.common.ui.MessageTextField
import com.nextiva.nextivaapp.android.core.common.ui.MessageTextFieldInterface
import com.nextiva.nextivaapp.android.core.common.ui.PendingMessageData
import com.nextiva.nextivaapp.android.core.common.ui.SendingViaBanner
import com.nextiva.nextivaapp.android.databinding.FragmentConnectChatConversationBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.messaging.MessagingGeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleHelper
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.AudioAttachmentInterface
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.CommonSmsViewModel
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.ConversationViewModel
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.PagedConversationListAdapter
import com.nextiva.nextivaapp.android.features.rooms.view.AttachmentDetailsActivity
import com.nextiva.nextivaapp.android.features.rooms.view.BottomSheetMessageMenuDialog
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDeleteConfirmation
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetSwitchConversation
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.MMSData
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SendToFromItem
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.Event
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.MessageUtil
import com.nextiva.nextivaapp.android.util.MessageUtil.Companion.getFileNameWithOutExtension
import com.nextiva.nextivaapp.android.util.MessageUtil.Companion.isAttachmentAPhotoFromCamera
import com.nextiva.nextivaapp.android.util.RecyclerViewFastScroller
import com.nextiva.nextivaapp.android.util.extensions.downloadFileAsByteArray
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.CustomSnackbar
import com.nextiva.nextivaapp.android.view.SnackStyle
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ConversationFragment : MessagingGeneralRecyclerViewFragment(),
    CallManager.ProcessParticipantInfoCallBack, MessageTextFieldInterface, AudioAttachmentInterface, ContentViewCallback {

    companion object {
        const val PARTICIPANT_TEXTVIEW_MARGINS_AND_PADDING = 80
        const val MAX_ATTACHMENT_SIZE = 1000000
        const val MAX_ATTACHMENTS = 1

        private const val PARAMS_PARTICIPANTS = "PARAMS_PARTICIPANTS"
        private const val PARAMS_CHAT_TYPE = "CHAT_TYPE"
        private const val PARAMS_SMS_CONVERSATION_DETAILS = "PARAMS_SMS_CONVERSATION_DETAILS"
        private const val PARAMS_PENDING_MESSAGE_DATA = "PARAMS_PENDING_MESSAGE_DATA"
        private const val PARAMS_IS_CALL_OPTIONS_DISABLED = "PARAMS_IS_CALL_OPTIONS_DISABLED"
        private const val PARAMS_IS_NEW_CHAT = "PARAMS_IS_NEW_CHAT"
        private const val PARAMS_ATTACHMENT_URI = "PARAMS_ATTACHMENT_URI"

        private const val VIEW_CACHE_SIZE = 50
        private const val SNACK_BAR_DURATION = 1500L

        fun newInstance(
            @Enums.Chats.ConversationTypes.Type chatType: String,
            participantList: ArrayList<String>?,
            conversationDetails: SmsConversationDetails?,
            isCallOptionsDisabled: Boolean,
            isNewChat: Boolean,
            attachmentUri: String? = null
        ): ConversationFragment {

            val fragment = ConversationFragment()
            val args = Bundle()
            args.putString(PARAMS_CHAT_TYPE, chatType)
            args.putString(PARAMS_SMS_CONVERSATION_DETAILS, GsonUtil.getJSON(conversationDetails))
            args.putStringArrayList(PARAMS_PARTICIPANTS, participantList)
            args.putBoolean(PARAMS_IS_CALL_OPTIONS_DISABLED, isCallOptionsDisabled)
            args.putBoolean(PARAMS_IS_NEW_CHAT, isNewChat)
            args.putString(PARAMS_ATTACHMENT_URI, attachmentUri)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(
            @Enums.Chats.ConversationTypes.Type chatType: String,
            participantList: ArrayList<String>?,
            conversationDetails: SmsConversationDetails?,
            pendingMessage: PendingMessageData?,
            isCallOptionsDisabled: Boolean,
            isNewChat: Boolean,
            attachmentUri: String? = null
        ): ConversationFragment {

            val fragment = ConversationFragment()
            val args = Bundle()
            args.putString(PARAMS_CHAT_TYPE, chatType)
            args.putString(PARAMS_SMS_CONVERSATION_DETAILS, GsonUtil.getJSON(conversationDetails))
            args.putSerializable(PARAMS_PENDING_MESSAGE_DATA, pendingMessage)
            args.putStringArrayList(PARAMS_PARTICIPANTS, participantList)
            args.putBoolean(PARAMS_IS_CALL_OPTIONS_DISABLED, isCallOptionsDisabled)
            args.putBoolean(PARAMS_IS_NEW_CHAT, isNewChat)
            args.putString(PARAMS_ATTACHMENT_URI, attachmentUri)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var messageLayout: LinearLayout

    private lateinit var mContactCardView: CardView
    private lateinit var mContactCardAvatarView: AvatarView
    private lateinit var mContactChipCardNameAppCompatTextView: AppCompatTextView
    private lateinit var mContactChipCardPhoneAppCompatTextView: AppCompatTextView
    private lateinit var mCloseChipCardImageView: ImageView
    private lateinit var mChatConversationErrorBanner: LinearLayout
    private lateinit var mChatConversationErrorBannerText: TextView
    private lateinit var mChatConversationMessageLayout: ComposeView
    private lateinit var mLoadingLayout: View
    lateinit var mToolbar: Toolbar

    @Inject
    lateinit var callManager: CallManager
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var settingsManager: SettingsManager
    @Inject
    lateinit var avatarManager: AvatarManager
    @Inject
    lateinit var logManager: LogManager
    @Inject
    lateinit var permissionManager: PermissionManager
    @Inject
    lateinit var nextivaMediaPlayer: NextivaMediaPlayer
    @Inject
    lateinit var mDbManager: DbManager
    @Inject
    lateinit var dialogManager: DialogManager
    @Inject
    lateinit var schedulerProvider: SchedulerProvider
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var smsTitleHelper: SmsTitleHelper
    @Inject
    lateinit var nextivaApplication: Application
    @Inject
    lateinit var mConnectionStateManager: ConnectionStateManager

    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var chatSMSViewModel: CommonSmsViewModel

    private var pagedAdapter: PagedConversationListAdapter? = null
    private var teamBannerIsShown = false
    private var autoScrollAfterSending = false
    private var isSharingImageViaIntent = false
    private var alreadyUpdatedDraftMessage = false
    private var attachmentAudioFilePlayer = AttachmentAudioFilePlayer()
    var textFromMessageTextField = ""

    private val uiNameTextChangedObserver = Observer<NextivaContact?> { contact ->
        when {
            viewModel.isNewChat -> setTitle(getString(R.string.new_sms_title))
            !contact?.uiName.isNullOrEmpty() && viewModel.conversationDetails?.getAllTeams()?.isEmpty() == true -> contact?.uiName?.let { setTitle(it) }
            else -> { setToolbarTitleToRecipients() }
        }

        conversationDetails.postValue(viewModel.conversationDetails)
    }

    private val draftMessageObserver = Observer<SmsMessage?> { draftMessage ->

        // --------------------------------------------------------------------------------------------
        // Load draft messages|attachments via normal Conversation
        // --------------------------------------------------------------------------------------------

        if (!viewModel.isNewChat && draftMessage != null && !alreadyUpdatedDraftMessage && !viewModel.comesFromNewMessage) {
            alreadyUpdatedDraftMessage = true
            if (draftMessage.attachments.isNullOrEmpty()) {
                textFromMessageTextField = draftMessage.body.orEmpty()
                setDraftMessage.postValue(draftMessage.body)
            } else {
                val attachmentText = draftMessage.body.orEmpty()
                addAttachments(draftMessage.attachments!!.map { Uri.parse(it.link) })
                textFromMessageTextField = attachmentText
                setDraftMessage.postValue(attachmentText)
            }
        }
    }

    private fun updateChatMessages(pagingData: PagingData<BaseListItem>) {
        pagedAdapter?.submitData(viewLifecycleOwner.lifecycle, pagingData)

        // --------------------------------------------------------------------------------------------
        // Load draft messages|attachments via bottom sheet new message
        // --------------------------------------------------------------------------------------------

        if (viewModel.isNewChat && (parentFragment is BottomSheetNewMessage) && !alreadyUpdatedDraftMessage) {
            alreadyUpdatedDraftMessage = true
            val tempAttachment = (parentFragment as BottomSheetNewMessage).temporalAttachmentFromMessage
            val tempText = (parentFragment as BottomSheetNewMessage).temporalTextFromMessageTextFields
            if (tempAttachment != null) { addAttachments(listOf(tempAttachment.uri)) }
            textFromMessageTextField = tempText
            setDraftMessage.postValue(tempText)
        }
    }

    private val onMessageDeletedObserver = Observer<Boolean> { success ->
        showSnackBar(success)
    }

    private fun notifyItemRemoved(message: SmsMessageListItem?) {
        getIndexForListItem(message)?.let { index ->
            pagedAdapter?.notifyItemRemoved(index)
            pagedAdapter?.refresh()
        }
    }

    private fun getIndexForListItem(message: SmsMessageListItem?): Int? {
        message ?: return null
        pagedAdapter?.itemCount?.let { itemCount ->
            for (index in 0 until itemCount) {
                val mMessage = pagedAdapter?.peek(index) as? SmsMessageListItem
                if (mMessage?.data?.messageId == message.data.messageId) {
                    return index
                }
            }
        }
        return null
    }

    private fun showSnackBar(isDeleteSuccessful: Boolean) {
        dialogManager.dismissProgressDialog()
        val customSnackBar = CustomSnackbar.make(
            view = requireView(),
            duration = BaseTransientBottomBar.LENGTH_LONG,
            contentViewCallBack = this,
            snackStyle = SnackStyle.fromBoolean(isDeleteSuccessful)
        )
        if (isDeleteSuccessful) {
            notifyItemRemoved(viewModel.smsMessageListItemDeleted.value)
            customSnackBar.setFontAwesomeIcon(getString(R.string.fa_trash_alt))
            customSnackBar.setText(getString(R.string.connect_sms_message_deleted_success), Gravity.CENTER)
            customSnackBar.setCloseAction { customSnackBar.dismiss() }
            closeScreenIfConversationIsEmpty()
        } else {
            customSnackBar.setFontAwesomeIcon(getString(R.string.fa_times_circle))
            customSnackBar.setText(getString(R.string.connect_sms_message_deleted_failed),  Gravity.START)
        }
        customSnackBar.show()
    }

    private fun closeScreenIfConversationIsEmpty() {
        val totalMessages = pagedAdapter?.snapshot()?.count { it is SmsMessageListItem } ?: 0
        if (totalMessages <= 1) {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(SNACK_BAR_DURATION) // wait 1.5 seconds until the customBar is gone
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    fun saveMessageAsDraftIfNeeded(isBottomSheet: Boolean) {
        if (!isBottomSheet && !viewModel.isNewChat && !viewModel.comesFromNewMessage) {
            saveMessageAsDraft()
            return
        }
        if (isBottomSheet && (pagedAdapter?.itemCount ?: 0) > 1 && isSending.value == false) {
            val attachment = selectedAttachments.value?.firstOrNull()
            if (attachment != null || textFromMessageTextField.isNotEmpty()) {
                saveMessageAsDraft()
            }
        }
    }

    private fun saveMessageAsDraft() {
        val attachmentUri = selectedAttachments.value?.firstOrNull()?.uri
        val attachmentName = getFileNameWithOutExtension(attachmentUri, requireContext()).orEmpty()
        if (attachmentUri != null && !isAttachmentAPhotoFromCamera(attachmentName)) {
            saveAttachmentAsDraft(attachmentUri, textFromMessageTextField)
        } else {
            viewModel.saveOrUpdateDraftMessage(textFromMessageTextField)
        }
    }

    private fun saveAttachmentAsDraft(uri: Uri, text: String) {
        getAttachmentData(
                text = text,
                attachmentUri = uri,
                onFinished = { imageUri, _, message, fileName, contentType, contentData, audioDuration ->
                    viewModel.saveOrUpdateDraftAttachment(
                            imageUri = imageUri,
                            message = message,
                            fileName = fileName,
                            contentType = contentType,
                            contentData = contentData,
                            audioDuration = audioDuration
                    )
                    selectedAttachments.postValue(null)
                }
        )
    }

    private fun rebuildTextField(showComposer: Boolean = true) {
        if (showComposer) {
            val isSendingAllowed = viewModel.participantsList.size <= Constants.SMS.MAX_SMS_RECIPIENT_COUNT
            if (isSendingAllowed) {
                mChatConversationErrorBanner.visibility = View.GONE
                mChatConversationMessageLayout.visibility = View.VISIBLE
                (activity as? AppCompatActivity)?.let {
                    mChatConversationMessageLayout.setContent {
                        MessageTextField(
                            activity = it,
                            messageTextFieldInterface = this
                        )
                    }
                }
            } else {
                mChatConversationErrorBanner.visibility = View.VISIBLE
                mChatConversationMessageLayout.visibility = View.GONE
                mChatConversationErrorBannerText.text = getString(R.string.chat_conversation_max_participants_error, (Constants.SMS.MAX_SMS_RECIPIENT_COUNT).toString())
            }
        } else {
            mChatConversationMessageLayout.visibility = View.GONE
        }
    }

    private val showNoInternetObserver = Observer<Void?> {
        activity?.let { activity ->
            mDialogManager.showDialog(
                activity,
                R.string.error_no_internet_title,
                R.string.error_no_internet_chat_message,
                R.string.general_ok
            ) { _, _ -> }
        }
    }

    private val showProgressBarObserver = Observer<Void?> {
        isSending.postValue(true)
    }

    private val dismissProgressBarObserver = Observer<Void?> {
        isSending.postValue(false)
    }

    private val processedCallInfoObserver = Observer<ParticipantInfo> { participantInfo ->
        activity?.let {
            callManager.processParticipantInfo(
                it,
                analyticScreenName,
                participantInfo,
                null,
                mCompositeDisposable,
                this
            )
        }
    }

    private val maxRateExceededObserver = Observer<Boolean> { exceeded ->
        if (exceeded) {
            activity?.let { activity ->
                mDialogManager.showDialog(
                    activity,
                    R.string.error_max_sms_rate_exceeded_title,
                    R.string.error_max_sms_rate_exceeded_message,
                    R.string.general_ok
                ) { _, _ -> }
            }
        }
    }

    private val sendingPhoneNumberObserver = Observer<ConversationViewModel.SendingPhoneNumber> { sendingPhoneNumber ->
        var sendingViaBannerValue: SendingViaBanner? = null

        if(sendingPhoneNumber.phoneNumber.isEmpty() &&
            (parentFragment is BottomSheetNewMessage)) {
            val name = (parentFragment as BottomSheetNewMessage).getDefaultTeamName()
            viewModel.selectTeamNumberAsSender(name)

        } else {
            if (sendingPhoneNumber.team != null) {
                sendingPhoneNumber.team.let { team ->
                    sendingPhoneNumber.phoneNumber.let {
                        sendingViaBannerValue = SendingViaBanner(getString(
                            R.string.chat_conversation_sending_via,
                            team.teamName,
                            PhoneNumberUtils.formatNumber(
                                team.teamPhoneNumber,
                                Locale.getDefault().country
                            )
                        ), true)
                    }
                }
            } else if (viewModel.getUserSmsEnabledNumbers().size > 1 && !sendingPhoneNumber.enabled) {
                sendingViaBannerValue = SendingViaBanner(getString(R.string.chat_conversation_select_a_number), true)
            }
        }

        sendingViaBanner.postValue(sendingViaBannerValue)
        validateSMSCampaignBanner()
    }

    fun validateSMSCampaignBanner() {
        viewModel.shouldDisplayRibbonAboveComposer { shouldDisplayRibbonAboveComposer ->
            if (shouldDisplayRibbonAboveComposer) {
                val textToDisplay = if (viewModel.isUserAdmin) {
                    getString(R.string.chat_conversation_campaign_validation_ribbon_admin)
                } else {
                    getString(R.string.chat_conversation_campaign_validation_ribbon_user)
                }

                sendingViaBanner.postValue(SendingViaBanner(textToDisplay, true))
            }
        }

        viewModel.shouldDisplayFooterAndHideComposer { shouldDisplayFooterAndHideComposer ->
            if (shouldDisplayFooterAndHideComposer) {
                viewModel.getFooterType { footerType ->
                    val textToDisplay = when (footerType) {
                        ADMIN -> getString(R.string.chat_conversation_campaign_validation_footer_admin)
                        USER -> getString(R.string.chat_conversation_campaign_validation_footer_user)
                        ADMIN_AND_USER -> getString(R.string.chat_conversation_campaign_validation_footer_admin_and_user)
                        else -> getString(R.string.chat_conversation_campaign_validation_footer_unknown)
                    }

                    sendingViaBanner.postValue(SendingViaBanner(textToDisplay, false))
                    rebuildTextField(false)
                }
            }
        }

        refreshRecyclerViewPadding()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity == null) {
            return
        }

        shouldReverseLayout = true
        shouldAddDivider = false

        setHasOptionsMenu(true)
        EmojiManager.install(GoogleEmojiProvider())

        nextivaMediaPlayer.getCurrentPlayingProgressChangedLiveData().observeForever {
            attachmentAudioFilePlayer.updateProgress(it)
        }
    }

    override fun onResume() {
        super.onResume()
        analyticsManager.logScreenView(analyticScreenName)
        viewModel.getUiNameToDisplay()
    }

    override fun onPause() {
        super.onPause()
        nextivaMediaPlayer.pausePlaying()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = super.onCreateView(inflater, container, savedInstanceState)
        if (view != null) {
            arguments?.let { arguments -> viewModel.setup(arguments, true) }
            bindViews(view)

            val recyclerViewFastScroller = RecyclerViewFastScroller(requireActivity(), settingsManager, true)
            recyclerViewFastScroller.setRecyclerView(mRecyclerView)

            arguments?.getString(PARAMS_ATTACHMENT_URI)?.let { attachmentUri ->
                Uri.parse(attachmentUri)?.let {
                    isSharingImageViaIntent = true
                    addAttachments(listOf(it))
                }
            }
        }

        if (activity == null || arguments == null) {
            return view
        }

        savedInstanceState?.let { viewModel.setup(it) }
        viewModel.uiNameTextChangedLiveData.observe(viewLifecycleOwner, uiNameTextChangedObserver)
        viewModel.draftMessageLiveData.observe(viewLifecycleOwner, draftMessageObserver)
        viewModel.showNoInternetLiveData.observe(viewLifecycleOwner, showNoInternetObserver)
        viewModel.showProgressDialog.observe(viewLifecycleOwner, showProgressBarObserver)
        viewModel.dismissProgressBarDialog.observe(viewLifecycleOwner, dismissProgressBarObserver)
        viewModel.processedCallInfoMutableLiveData.observe(viewLifecycleOwner, processedCallInfoObserver)
        viewModel.maxRateExceededLiveData.observe(viewLifecycleOwner, maxRateExceededObserver)
        viewModel.sendingPhoneNumber.observe(viewLifecycleOwner, sendingPhoneNumberObserver)
        viewModel.messageDeleteResultLiveData.observe(viewLifecycleOwner, onMessageDeletedObserver)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch { viewModel.smsListItemsLiveData.collectLatest { updateChatMessages(it) } }
                launch { viewModel.groupId.collectLatest {
                    sendingPhoneNumbers.postValue(viewModel.getUserSmsEnabledNumbers()) }
                    resetView()
                }
                launch {
                    viewModel.loading.collectLatest {
                        mLoadingLayout.visibility = if(it) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        // Handle PendingMessage Event
        viewModel.pendingMessage.observe(viewLifecycleOwner, Event.EventObserver { pendingMessage ->
            flushPendingMessage(pendingMessage)
        })

        sendingPhoneNumbers.observe(viewLifecycleOwner) { phoneNumbers ->
            if ((phoneNumbers?.size ?: 0) == 2) {
                val hasEmptyNumber = phoneNumbers?.any { it.phoneNumber.isNullOrEmpty() }
                if ((parentFragment is BottomSheetNewMessage) && hasEmptyNumber == true && !teamBannerIsShown) {
                    teamBannerIsShown = true
                    showDefaultTeamSMSBanner()
                }
            }
        }

        pagedAdapter = PagedConversationListAdapter(requireActivity(), this@ConversationFragment, this@ConversationFragment)
        mRecyclerView.adapter = pagedAdapter
        mRecyclerView.setItemViewCacheSize(VIEW_CACHE_SIZE)
        mRecyclerView.animation = null
        mRecyclerView.itemAnimator = null

        pagedAdapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                pagedAdapter?.snapshot()?.items?.let { viewModel.groupMessagesByTime(it) }
            }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                pagedAdapter?.snapshot()?.items?.let { viewModel.groupMessagesByTime(it) }
            }
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                pagedAdapter?.snapshot()?.items?.let { viewModel.groupMessagesByTime(it) }
            }
        })

        pagedAdapter?.addLoadStateListener {
            pagedAdapter?.let { adapter ->

                if (it.append.endOfPaginationReached || it.prepend.endOfPaginationReached) {
                    if (adapter.itemCount > 0) {
                        showRecyclerView()
                        refreshRecyclerViewPadding()

                    } else {
                        showEmptyState()
                    }

                    if (autoScrollAfterSending || autoScrollForIncomingMessage()) {
                        mRecyclerView.scrollToPosition(0)
                        autoScrollAfterSending = false
                    }
                }
            }
        }

        conversationDetails.postValue(viewModel.conversationDetails)

        return view
    }

    private fun flushPendingMessage(pendingMessage: PendingMessageData) {
        selectedAttachments.value = pendingMessage.selectedAttachments?.map { it.toAttachmentInfo() }
        onSend(pendingMessage.editMessage)
    }

    private fun showErrorDialog() {
        val isTeamSmsLicenseEnabled = viewModel.isTeamSmsLicenseEnabled
        val isSmsLicenseEnabled = viewModel.isSmsLicenseEnabled
        val isSmsEnabled = viewModel.isSmsEnabled
        val isTeamConversation = viewModel.isTeamConversation
        val isOurTeamSelected = conversationDetails.value?.isOurTeamSelected() == true

        val dialogTitle = when {
            isTeamConversation && !isTeamSmsLicenseEnabled && isOurTeamSelected -> getString(R.string.invalid_team_sms_license_dialog_title)
            (!isTeamSmsLicenseEnabled && !isSmsEnabled) || (!isTeamSmsLicenseEnabled && !isSmsLicenseEnabled) -> getString(R.string.error_general_error_title)
            else -> getString(R.string.invalid_provisioning_dialog_title)
        }
        val dialogBody = when {
            isTeamConversation && !isTeamSmsLicenseEnabled && isOurTeamSelected -> getString(R.string.invalid_team_sms_license_dialog_body)
            (!isTeamSmsLicenseEnabled && !isSmsEnabled) || (!isTeamSmsLicenseEnabled && !isSmsLicenseEnabled) -> getString(R.string.invalid_provisioning_dialog_body)
            else -> getString(R.string.invalid_license_dialog_body)
        }

        dialogManager.showDialog(requireContext(),
                dialogTitle,
                dialogBody,
                getString(R.string.general_ok)
        ) { _, _ -> }
    }

    private fun refreshRecyclerViewPadding() {
        mRecyclerView.setPadding(mRecyclerView.paddingLeft,
            mRecyclerView.paddingTop,
            mRecyclerView.paddingRight,
            resources.getDimension(R.dimen.general_vertical_margin_xsmall).toInt()
        )
    }

    private fun bindViews(view: View) {
        val binding = FragmentConnectChatConversationBinding.bind(view)

        mLoadingLayout = binding.loadingLayout
        messageLayout = binding.chatConversationFragmentLayout

        mContactCardView = binding.chipCardView
        mContactCardAvatarView = binding.listItemChatConversationAvatarView
        mContactChipCardNameAppCompatTextView = binding.chipCardNameTextView
        mContactChipCardPhoneAppCompatTextView = binding.chipCardPhoneTextView
        mCloseChipCardImageView = binding.closeChipCardImageView
        mChatConversationErrorBanner = binding.chatConversationErrorBanner
        mChatConversationErrorBannerText = binding.chatConversationErrorBannerText
        mChatConversationMessageLayout = binding.chatConversationMessageLayout

        rebuildTextField()
    }

    private fun setToolbarTitleToRecipients() {
        if (!viewModel.isNewChat) {
            viewModel.conversationDetails?.let { conversationDetails ->
                val smsTitleInfo = smsTitleHelper.getSMSConversationParticipantInfo(
                    conversationDetails = conversationDetails,
                    width = getTitleWidth(),
                    paint = (activity as? ConversationActivity)?.titleTextView?.paint,
                    context = requireContext()
                )
                setTitle(smsTitleInfo.smsTitleName)
            }
        }
    }

    private fun moveToActivity(pendingMessage: PendingMessageData) {
        viewModel.conversationDetails?.let { conversationDetails ->
            viewModel.sendingPhoneNumber.value?.team?.let { team ->
                if (conversationDetails.teams?.contains(team) != true) {
                    val conversationTeams = conversationDetails.teams?.toMutableList() ?: mutableListOf()
                    conversationTeams.add(team)
                    conversationDetails.teams = conversationTeams
                }
            }

            val startChat = ConversationActivity.newIntent(
                activity,
                conversationDetails,
                pendingMessage,
                false,
                Enums.Chats.ConversationTypes.SMS,
                Enums.Chats.ChatScreens.CONVERSATION
            )
            startActivity(startChat)

            if (parentFragment != null && !requireParentFragment().isDetached) {
                if (isSharingImageViaIntent) {
                    (activity as? ConnectNewTextActivity)?.finish()
                } else {
                    (parentFragment as? BottomSheetNewMessage)?.dismiss()
                }
            }
        }
    }

    private fun autoScrollForIncomingMessage(): Boolean {
        val layoutManager: LinearLayoutManager? =
            LinearLayoutManager::class.java.cast(mRecyclerView.layoutManager)
        val firstVisible: Int = layoutManager?.findFirstVisibleItemPosition() ?: 0
        return firstVisible <= 0
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_chat_conversation
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }

    override fun getRecyclerViewId(): Int {
        return R.id.chat_conversation_recycler_view
    }

    override fun getEmptyStateViewId(): Int {
        return R.id.chat_conversation_empty_state_view
    }

    override fun getAnalyticScreenName(): String {
        return ScreenName.CHAT_DETAILS
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)

        (activity as? ConversationActivity)?.let { activity ->
            val isTeamConversation = viewModel.isTeamConversation
            val isSingle = !(viewModel.membersUiNames.size > 1 || viewModel.participantsList.size > 1)
            val isOwnNumber = if (isSingle) viewModel.participantsList.firstOrNull()?.phoneNumber == null else false
            activity.mToolbarCallButton?.visibility = if (isSingle && !viewModel.isNewChat && !isTeamConversation && !isOwnNumber) View.VISIBLE else View.GONE
            activity.mToolbarCallButton?.setOnClickListener {
                viewModel.contactAction(Enums.Sip.CallTypes.VOICE)
            }

            setToolbarTitleToRecipients()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(viewModel.getOutStateBundle(outState))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatSMSViewModel = ViewModelProvider(requireActivity())[CommonSmsViewModel::class.java]
    }

    private fun setTitle(title: String) {
        chatSMSViewModel.updateToolbarTitle(title)
    }

    fun getCurrentConversationDetails(): SmsConversationDetails? {
        return viewModel.conversationDetails
    }

    fun updateReadStatus() {
        viewModel.updateReadStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateReadStatus()
    }

    private fun getTitleWidth(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)

        val callButtonWidth = (activity as? ConversationActivity)?.let {
            if(it.mToolbarCallButton?.isVisible.orFalse())
                resources.getDimensionPixelOffset(R.dimen.general_view_xxlarge)
            else
                0
        }.orZero()

        val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                PARTICIPANT_TEXTVIEW_MARGINS_AND_PADDING.toFloat(),
                requireActivity().resources.displayMetrics) + callButtonWidth

        return displayMetrics.widthPixels - px.toInt()
    }

    override fun onParticipantInfoProcessed(
        activity: Activity,
        analyticsScreenName: String,
        participantInfo: ParticipantInfo,
        retrievalNumber: String?,
        compositeDisposable: CompositeDisposable
    ) {

        logManager.logToFile(
            Enums.Logging.STATE_INFO,
            R.string.log_message_success_with_message,
            activity.getString(R.string.log_message_processing_call, participantInfo.toString())
        )

        mDialogManager.dismissProgressDialog()

        callManager.makeCall(activity, ScreenName.DIALER, participantInfo, mCompositeDisposable)
    }


    // --------------------------------------------------------------------------------------------
    // MessageTextFieldInterface
    // --------------------------------------------------------------------------------------------

    override val isSending: MutableLiveData<Boolean> = MutableLiveData(false)
    override val editMessage: MutableLiveData<String?> = MutableLiveData(null)
    override val setDraftMessage: MutableLiveData<String?> = MutableLiveData(null)
    override val errorMessages: MutableLiveData<List<String>?> = MutableLiveData(null)
    override val selectedAttachments: MutableLiveData<List<AttachmentInfo>?> = MutableLiveData(null)
    override val sendingPhoneNumbers: MutableLiveData<ArrayList<ConversationViewModel.SendingPhoneNumber>?> = MutableLiveData(null)
    override val conversationDetails: MutableLiveData<SmsConversationDetails?> = MutableLiveData(null)
    override val sendingViaBanner: MutableLiveData<SendingViaBanner?> = MutableLiveData(null)

    override fun onEditMessageCancel() {
        editMessage.postValue(null)
    }

    override fun onSendingViaBannerClosed() {
        sendingViaBanner.postValue(null)
    }

    override fun onSend(text: String?) {
        if (viewModel.sendingPhoneNumber.value == null ||
            viewModel.sendingPhoneNumber.value?.enabled != true ||
            (!viewModel.isTeamSmsLicenseEnabled && conversationDetails.value?.isOurTeamSelected() == true)
        ) {
            showErrorDialog()
            setDraftMessage.postValue(text)
            return
        }

        isSending.postValue(true)

        // If we are in BottomSheetNewMessage, move user to ConversationActivity to send the message
        if (parentFragment is BottomSheetNewMessage &&
                (selectedAttachments.value?.firstOrNull()?.uri != null ||
                        !text.isNullOrBlank())) {
            sendMessageThroughActivity(text)
            return
        }

        sendingViaBanner.postValue(null)
        val attachmentUri = selectedAttachments.value?.firstOrNull()?.uri
        if (attachmentUri != null) {
            getAttachmentData(
                    attachmentUri = attachmentUri,
                    text = text,
                    onFinished = { imageUri, fBody, message, fileName, contentType, contentData, audioDuration ->
                        viewModel.sendMmsMessage(
                                imageUri = imageUri,
                                fbody = fBody,
                                message = message,
                                fileName = fileName,
                                contentType = contentType,
                                contentData = contentData,
                                audioDuration = audioDuration,
                                finishedCallback = { code ->
                                    code?.takeIf { code < 200 || code > 299 }?.let {
                                        showErrorDialog()
                                    }
                                    sendComplete()
                                }
                        )
                        selectedAttachments.postValue(null)
                    }
            )
        } else if (!text.isNullOrBlank()) {
            autoScrollAfterSending = true
            viewModel.sendSmsMessage(text) { code ->
                code?.takeIf { code < 200 || code > 299 }?.let {
                    showErrorDialog()
                }
                sendComplete()
            }
        }

        //  remove draft message
        textFromMessageTextField = ""
        setDraftMessage.postValue("")
        viewModel.draftMessage = null
        viewModel.removeDraftMessages()
    }

    private fun getAttachmentData(
            attachmentUri: Uri,
            text: String?,
            onFinished: (
                    imageUri: Uri,
                    fBody: MultipartBody.Part,
                    message: String,
                    fileName: String,
                    contentType: String,
                    contentData: ByteArray?,
                    audioDuration: Long
            ) -> Unit
    ) {
        val context = context ?: return
        val activity = activity ?: return

        var mimeType = MessageUtil.getMimeType(attachmentUri, context)
        val extension = mimeType?.let {
            MessageUtil.getFileExtension(attachmentUri, it, activity)
        }

        if (!extension.isNullOrEmpty() && !mimeType.isNullOrEmpty()) {
            mimeType?.let { mimeType ->
                isSending.postValue(true)
                selectedAttachments.postValue(null)
                Single.fromCallable {
                    var tempFilename = FileUtil.getFileNameFromUri(attachmentUri, nextivaApplication)
                    if (tempFilename == null) {
                        attachmentUri.path?.let { attachmentPath ->
                            tempFilename = attachmentPath.substring(attachmentPath.lastIndexOf('/') + 1)
                        }
                    }
                    var fileName = tempFilename ?: (System.currentTimeMillis().toString() + "." + extension)
                    val serverAllowedCharacters = Regex("[^A-Za-z0-9._]")
                    fileName = serverAllowedCharacters.replace(fileName, "")
                    try {
                        val fileNameExtension = FilenameUtils.getExtension(fileName)
                        if (Enums.Attachment.AttachmentContentType.IMAGE_BMP.contains(fileNameExtension) &&
                                mimeType == Enums.Attachment.AttachmentContentType.IMAGE_PNG) {
                            // the server rejects the attachment if the filename extension doesn't match mimeType
                            // (for .bmp we are converting to .png before sending)
                            fileName = FilenameUtils.removeExtension(fileName) + "." + extension
                        }
                    } catch (e: IllegalArgumentException) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        LogUtil.e(e.toString())
                    }

                    val file = File(context.cacheDir, fileName)
                    lateinit var bitmapData: ByteArray
                    file.createNewFile()

                    if (MessageUtil.isAudioFile(mimeType)) {
                        bitmapData = viewModel.getFileBytes(context, file, attachmentUri)

                    } else if (MessageUtil.isImageFile(mimeType)) {
                        bitmapData = if (Enums.Attachment.AttachmentContentType.IMAGE_GIF.contains(extension)) {
                            viewModel.getFileBytes(activity, file, attachmentUri)
                        } else {
                            val inputStream = context.contentResolver.openInputStream(attachmentUri)
                            val selectedBitMap = viewModel.scaleBitmapForMMS(context, BitmapFactory.decodeStream(inputStream))
                            viewModel.getBitmapData(extension, file, selectedBitMap)
                        }
                    }

                    MMSData(fileName,
                            file.toUri(),
                            bitmapData,
                            MultipartBody.Part.createFormData("file",
                                    fileName,
                                    RequestBody.create(mimeType.toMediaTypeOrNull(), file)),
                            mimeType.split("/")[0] + "/" + extension
                    )
                }.subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribe(object : DisposableSingleObserver<MMSData>() {
                            override fun onSuccess(mmsData: MMSData) {
                                autoScrollAfterSending = true
                                onFinished(
                                        mmsData.fileUri,
                                        mmsData.body,
                                        if (!text.isNullOrBlank()) text.toString() else "",
                                        mmsData.fileName,
                                        mmsData.contentType,
                                        // since Glide is managing image caching, no need to store bitmap in db
                                        if (MessageUtil.isAudioFile(mmsData.contentType)) mmsData.bitmapData else null,
                                        0
                                )
                            }

                            override fun onError(e: Throwable) {
                                FirebaseCrashlytics.getInstance().recordException(e)
                                Toast.makeText(activity, R.string.new_sms_valid_file_type, Toast.LENGTH_SHORT).show()
                            }
                        })
            }
        } else {
            Toast.makeText(activity, R.string.new_sms_valid_file_type, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onValueChanged(text: String) {
        textFromMessageTextField = text
    }

    private fun sendMessageThroughActivity(text: String?) {
        val pendingMessage = PendingMessageData(
            editMessage = text,
            selectedAttachments = selectedAttachments.value?.map {
                PendingMessageData.PendingAttachmentInfo(
                    it
                )
            }
        )
        viewModel.isNewChat = false
        setToolbarTitleToRecipients()
        moveToActivity(pendingMessage)
        return
    }

    private fun sendComplete() {
        if (viewModel.isNewChat) {
            viewModel.isNewChat = false
            setToolbarTitleToRecipients()
        }

        viewModel.tempMessageIdSent = ""
    }

    override fun addAttachments(attachments: List<Uri>) {
        val context = context ?: return
        val updatedAttachmentList = ArrayList<AttachmentInfo>()
        selectedAttachments.value?.let { updatedAttachmentList.addAll(it) }

        attachments.forEach {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                updatedAttachmentList += attachmentInfo(context, it)

                val postList = ArrayList<AttachmentInfo>()
                updatedAttachmentList.forEach { postList.add(it) }

                selectedAttachments.postValue(postList)
                refreshErrors(updatedAttachmentList)
            }
        }
    }

    override fun removeAttachment(info: AttachmentInfo) {
        val updatedAttachmentList = ArrayList<AttachmentInfo>()
        selectedAttachments.value?.forEach {
            if (it.uri != info.uri) {
                updatedAttachmentList.add(it)
            }
        }
        selectedAttachments.postValue(updatedAttachmentList)
        refreshErrors(updatedAttachmentList)
    }

    private fun refreshErrors(attachments: ArrayList<AttachmentInfo>) {
        val context = context ?: return
        var sizeError = false
        var typeError = false
        val countError = attachments.size > MAX_ATTACHMENTS

        attachments.forEach { attachment ->
            if (attachment.byteSize > MAX_ATTACHMENT_SIZE && !attachment.resizeableType) {
                sizeError = true
            }
            if (attachment.excludedType) {
                typeError = true
            }
        }

        val errorList = ArrayList<String>()
        if (sizeError) {
            errorList += context.getString(R.string.connect_sms_file_size_error)
        }
        if (typeError) {
            errorList += context.getString(R.string.room_conversation_file_type_error)
        }
        if (countError) {
            errorList += context.getString(R.string.connect_sms_file_count_error)
        }

        errorMessages.postValue(errorList)
    }

    override fun isExcludedType( uri: Uri) : Boolean {
        val context = context ?: return true
        MessageUtil.getMimeType(uri, context)?. let { mimeType ->
            return (!MessageUtil.isAudioFile(mimeType)) && (!MessageUtil.isImageFile(mimeType))
        }
        return true
    }

    override fun isImageError(uri: Uri, byteSize: Long): Boolean {
        val context = context ?: return false
        if (byteSize > MAX_ATTACHMENT_SIZE &&
            !FileUtil.hasExtension(context, uri, FileUtil.RESIZEABLE_FILE_TYPES)) {
            return true
        }
        return isExcludedType(uri)
    }

    override fun hasThumbnail(uri: Uri): Boolean {
        val context = context ?: return false
        return FileUtil.hasExtension(context, uri, FileUtil.THUMBNAIL_FILE_TYPES)
    }

    override fun menuItems() : List<AttachmentMenuItems> {
        return listOf(AttachmentMenuItems.TAKE_PICTURE, AttachmentMenuItems.CHOOSE_PHOTO, AttachmentMenuItems.ATTACH_FILE)
    }

    private fun showDefaultTeamSMSBanner() {
        val participantList = viewModel.conversationDetails?.getParticipantsList()
        val fromItems = viewModel.getFromItemsForTeamSMSBanner()
        val toItems = viewModel.getToItemsForTeamSMSBanner(participantList)

        toItems.firstOrNull()?.let {
            viewModel.conversationDetails?.getParticipantsList()
                    ?.firstOrNull()?.phoneNumber =
                    CallUtil.getStrippedNumberWithCountryCode(it.text)
        }

        fromItems.firstOrNull()?.let {
            viewModel.setSenderPhoneNumber(
                    CallUtil.getStrippedPhoneNumber(it.text),
                    fromItems.first().label,
                    getSelectedParticipants(),
                    true
            )
        }

        sendingPhoneNumbers.postValue(viewModel.getUserSmsEnabledNumbers())
        resetView()
    }

    private fun resetView() {
        setToolbarTitleToRecipients()
        rebuildTextField()
        requireActivity().setResult(Activity.RESULT_OK)
        (activity as? ConversationActivity)?.let { activity ->
            activity.mToolbarCallButton?.visibility = View.GONE
        }
    }

    override fun onSelectPhoneNumberClicked() {
        val participantList = viewModel.conversationDetails?.getParticipantsList()
        val fromItems = viewModel.getFromItemsForTeamSMSBanner()
        val toItems = viewModel.getToItemsForTeamSMSBanner(participantList)

        val sendToFromItem = SendToFromItem(
            toItems,
            fromItems,
            viewModel.getUserSmsEnabledNumbers().firstOrNull { it.team == null }?.enabled != true,
            (participantList?.size ?: 0) > 1 && !viewModel.isNewChat
        ) { to, from ->
            from?.let {
                // save team name to use in case there is no default personal number
                it.label?.let { name ->
                    (parentFragment as? BottomSheetNewMessage)?.setDefaultSendTeamName(name)
                }
                val shouldSwitchConversation = (to != null && !CallUtil.arePhoneNumbersEqual(
                    to.text,
                    participantList?.firstOrNull()?.phoneNumber
                )) ||
                        !CallUtil.arePhoneNumbersEqual(viewModel.getSendingPhoneNumber(), from.text)

                if (shouldSwitchConversation) {
                    BottomSheetSwitchConversation {
                        to?.let {
                            viewModel.conversationDetails?.getParticipantsList()
                                ?.firstOrNull()?.phoneNumber =
                                CallUtil.getStrippedNumberWithCountryCode(to.text)
                        }

                        if (it) {
                            viewModel.fetchGroupId(from.text, from.label, getSelectedParticipants())
                        }
                    }.show(requireActivity().supportFragmentManager, null)

                } else {
                    if (viewModel.conversationDetails?.getAllTeams()
                            .isNullOrEmpty() && viewModel.isNewChat
                    ) {
                        viewModel.fetchGroupId(from.text, from.label, getSelectedParticipants())
                    }
                }
            }
        }

        BottomSheetSendToFrom.newInstance(sendToFromItem).show(childFragmentManager, null)
    }

    override fun onAlertError() {
        showErrorDialog()
    }

    private fun getSelectedParticipants(): ArrayList<SmsParticipant>? {
        return (parentFragment as? BottomSheetNewMessage)?.getSelectedContacts()
            ?.map { SmsParticipant(it.uiName,
                null,
                CallUtil.getStrippedNumberWithCountryCode(it.allPhoneNumbers?.firstOrNull()?.strippedNumber),
                it.userId,
                null,
                null) } as ArrayList<SmsParticipant>?
    }

    fun getParticipantsList(): List<String> {
        return viewModel.participantsList.map { it.phoneNumber ?: "" }
    }

    // --------------------------------------------------------------------------------------------
    // AudioAttachmentInterface
    // --------------------------------------------------------------------------------------------

    override fun playAudioAttachment(id: String, filename: String, url: String) {
        if (mConnectionStateManager.isInternetConnected) {
            attachmentAudioFilePlayer.activeItemId = id
            attachmentAudioFilePlayer.activeItemFilename = filename
            attachmentAudioFilePlayer.activeItemUrl = url

            getAudioFile()?.also { audioFile ->
                initializeAudioPlayer(audioFile)
            } ?: run {
                with(attachmentAudioFilePlayer) {
                    downloadAudioAttachment(
                            link = activeItemUrl,
                            messageId = activeItemId.padStart(3, '0'),
                            contentType = FileUtil.getContentTypeFromFileName(activeItemFilename)
                    )
                }
            }
        } else {
            showNoInternetDialog(R.string.error_no_internet_play_audio)
        }
    }

    private fun getAudioFile(): File? {
        val messageId = attachmentAudioFilePlayer.activeItemId
        val fileName = attachmentAudioFilePlayer.activeItemFilename
        return if (messageId.isEmpty()) {
            nextivaMediaPlayer.getAudioFileFromCacheByName(nextivaApplication, fileName)
        } else {
            nextivaMediaPlayer.getAudioFileFromCache(nextivaApplication, messageId.padStart(3, '0'))
        }
    }

    private fun initializeAudioPlayer(audioFile: File) {
        val messageId = attachmentAudioFilePlayer.activeItemId

        if (nextivaMediaPlayer.getCurrentActiveAudioFileMessageId() != messageId) {
            nextivaMediaPlayer.finishPlayingAudioFile()
            nextivaMediaPlayer.setCurrentActiveAudioFileMessageId(messageId)
            attachmentAudioFilePlayer.updateDuration(MessageUtil.getAudioFileDuration(audioFile.toUri().toString(), nextivaApplication))
        }

        if (nextivaMediaPlayer.isPlaying()) {
            nextivaMediaPlayer.pausePlaying()
            attachmentAudioFilePlayer.isPlaying.value = false
        } else {
            val speakerEnabled = attachmentAudioFilePlayer.speakerEnabledLiveData(messageId).value ?: false
            val playerSpeakerEnabled = nextivaMediaPlayer.isSpeakerPhoneEnabled()
            if (speakerEnabled != playerSpeakerEnabled) {
                nextivaMediaPlayer.toggleSpeakerPhone(nextivaApplication)
            }

            nextivaMediaPlayer.playAudioFile(nextivaApplication, audioFile)
            attachmentAudioFilePlayer.isPlaying.value = true
        }
    }

    private fun downloadAudioAttachment(link: String, contentType: String, messageId: String) {
        val context = context ?: return
        mDbManager.saveContentDataFromLinkWithReturn(link, contentType)
            .flatMap { contentData -> MessageUtil.createAudioCacheFile(context, messageId, contentType, contentData) }
            .map { file ->
                MessageUtil.getAudioFileDuration(file.path, context).div(Constants.ONE_SECOND_IN_MILLIS.toInt())
            }
            .flatMap { duration -> mDbManager.saveFileDuration(link, duration) }
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<Long>() {
                override fun onSuccess(duration: Long) {
                    attachmentAudioFilePlayer.updateDuration(duration)
                    getAudioFile()?.let { audioFile ->
                        initializeAudioPlayer(audioFile)
                    }
                }

                override fun onError(e: Throwable) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            })
    }

    override fun audioProgressDragged(progress: Int) {
        val progressMilliSeconds = attachmentAudioFilePlayer.duration * (progress.toDouble() / 100)
        nextivaMediaPlayer.setProgress(progressMilliSeconds.toInt(), true)
    }

    override fun toggleSpeaker(attachmentId: String) {
        val enabled = attachmentAudioFilePlayer.speakerEnabledLiveData(attachmentId).value ?: false
        attachmentAudioFilePlayer.updateSpeakerEnabled(attachmentId, !enabled)

        val playerSpeakerEnabled = nextivaMediaPlayer.isSpeakerPhoneEnabled()
        if (!enabled != playerSpeakerEnabled) {
            nextivaMediaPlayer.toggleSpeakerPhone(nextivaApplication)
        }
    }

    override fun getAttachmentAudioFilePlayer(): AttachmentAudioFilePlayer {
        return attachmentAudioFilePlayer
    }

    override fun getSessionId(): String {
        return sessionManager.sessionId ?: ""
    }

    override fun getCorpAcctNumber(): String {
        return sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
    }

    override fun senderUiName(listItem: SmsMessageListItem): String {
        var senderUiName = ""

        listItem.data.sender?.get(0)?.uiName?.let { uiName ->
            senderUiName = uiName
        }

        if (senderUiName.isEmpty()) {
            listItem.data.sender?.get(0)?.phoneNumber?.let { senderPhoneNumber ->
                senderUiName = mDbManager.getConnectUiNameFromPhoneNumber(CallUtil.getStrippedPhoneNumber(senderPhoneNumber)) ?:
                        CallUtil.phoneNumberFormatNumberDefaultCountry(listItem.data.sender?.get(0)?.phoneNumber)
            }
        }
        return senderUiName
    }

    override fun onClicked(filename: String?, url: String?) {
        if (mConnectionStateManager.isInternetConnected) {
            activity?.let { activity ->
                if (filename != null && url != null && FileUtil.hasExtension(filename, FileUtil.ALLOWED_FILE_IMAGE_TYPES)) {
                    startActivity(AttachmentDetailsActivity.newIntent(activity, filename, url))
                }
            }
        }
    }

    override fun onResendSmsMessage(message: SmsMessage) {
        if (!message.attachments.isNullOrEmpty()) {
            viewModel.onResendMmsMessage(message, activity)
        } else {
            viewModel.onResendSmsMessage(message)
        }
    }

    override fun onLongClicked(drawable: Drawable?, filename: String?, url: String?, message: SmsMessageListItem) {
        context?.let { context ->
            if (filename != null && url != null &&
               (FileUtil.hasExtension(filename, FileUtil.ALLOWED_FILE_IMAGE_TYPES) || FileUtil.hasExtension(filename, FileUtil.AUDIO_FILE_TYPES))
            ) {
                val textToDisplayBottomSheet = context.getString(
                    if (isTheFileAnImageOrVideo(filename)) {
                        R.string.chat_details_download_image
                    } else {
                        R.string.chat_details_download_file
                    }
                )
                BottomSheetMessageMenuDialog(
                    editAction = null,
                    deleteAction = { showConfirmationDialogToDeleteMessage(message) },
                    cancelAction = { },
                    downloadAction = {
                        lifecycleScope.launch {
                            val attachment = when {
                                mConnectionStateManager.isInternetConnected -> sessionManager.sessionId?.let {
                                    url.downloadFileAsByteArray(
                                        it, sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
                                    )
                                }
                                isTheFileAnImage(filename) -> FileUtil.drawableToByteArray(drawable)
                                else -> getAudioFile()?.takeIf { it.exists() }?.let { FileUtil.fileToByteArray(it) }
                            }

                            if (attachment != null) {
                                validateStoragePermissions(attachment, filename)
                            } else {
                                showNoInternetDialog(R.string.error_no_internet_download_file)
                            }
                        }
                    },
                    downloadTextString = textToDisplayBottomSheet
                ).show(childFragmentManager, null)
            }
        }
    }

    override fun onDeleteMessage(message: SmsMessageListItem) {
        showConfirmationDialogToDeleteMessage(message)
    }

    private fun showConfirmationDialogToDeleteMessage(message: SmsMessageListItem) {
        activity?.supportFragmentManager?.let { fm ->
            BottomSheetDeleteConfirmation.newInstance(
                    title = context?.getString(R.string.connect_sms_delete_single_message_title),
                    subtitle = context?.getString(R.string.connect_sms_delete_single_message_subtitle),
                    deleteAction = {
                        dialogManager.showProgressDialog(requireContext(), analyticScreenName, R.string.progress_deleting)
                        viewModel.deleteMessage(message)
                    },
                    cancelAction = { }
            ).show(fm, null)
        }
    }

    private fun validateStoragePermissions(attachment: ByteArray, filename: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveDataToStorage(attachment, filename)
        } else {
            permissionManager.requestStorageToDownloadPermission(
                context as Activity,
                Enums.Analytics.ScreenName.APP_PREFERENCES,
                { saveDataToStorage(attachment, filename) },
                { showSimpleToast(R.string.permission_required_title) }
            )
        }
    }

    private fun showNoInternetDialog(messageResource: Int) {
        mDialogManager.showDialog(
            requireContext(),
            R.string.error_no_internet_title,
            messageResource,
            R.string.general_ok
        ) { _, _ -> }
    }

    private fun isTheFileAnImage(filename: String): Boolean {
        return FileUtil.hasExtension(filename, FileUtil.ALLOWED_FILE_IMAGE_TYPES)
    }

    private fun isTheFileAnImageOrVideo(filename: String): Boolean {
        val isVideo = FileUtil.hasExtension(filename, FileUtil.VIDEO_FILE_TYPES)
        return isTheFileAnImage(filename) || isVideo
    }

    private fun saveDataToStorage(attachment: ByteArray, filename: String) {
        val fos: OutputStream?

        if (isTheFileAnImage(filename)) {
            fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveMediaToStorageQ(
                    filename,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    Environment.DIRECTORY_PICTURES
                )
            } else {
                saveMediaToStorageLegacy(
                    filename, Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    )
                )
            }

            fos?.use {
                it.write(attachment)
                (context as? Activity ?: (this as Fragment).requireActivity()).runOnUiThread {
                    showCustomToastWhenFinished(Enums.Attachment.ContentMajorType.IMAGE)
                }
            }
        } else {
            fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveMediaToStorageQ(filename, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Environment.DIRECTORY_MUSIC)
            } else {
                saveMediaToStorageLegacy(filename, Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC))
            }

            fos?.use {
                it.write(attachment)
                (context as? Activity ?: (this as Fragment).requireActivity()).runOnUiThread {
                    showCustomToastWhenFinished(Enums.Attachment.ContentMajorType.AUDIO)
                }
            }
        }
    }

    private fun saveMediaToStorageQ(filename: String, mediaUri: Uri, directory: String): OutputStream? {
        val resolver = context?.contentResolver ?: return null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
        }
        val uri = resolver.insert(mediaUri, contentValues) ?: return null
        return resolver.openOutputStream(uri)
    }

    private fun saveMediaToStorageLegacy(filename: String, directory: File): OutputStream {
        val file = File(directory, filename)
        return FileOutputStream(file)
    }

    private fun showSimpleToast(message: Int) {
        Toast.makeText(context, context?.getString(message), Toast.LENGTH_LONG).show()
    }

    @SuppressLint("InflateParams")
    private fun showCustomToastWhenFinished(type: String) {
        val messageSuccess = when (type) {
            Enums.Attachment.ContentMajorType.IMAGE -> R.string.chat_details_image_saved
            else -> R.string.chat_details_file_saved
        }

        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_toast, null)
        val textView = view.findViewById<TextView>(R.id.custom_toast_message)
        textView.text = context?.getString(messageSuccess)
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }

    override fun animateContentIn(delay: Int, duration: Int) { }

    override fun animateContentOut(delay: Int, duration: Int) { }
}
