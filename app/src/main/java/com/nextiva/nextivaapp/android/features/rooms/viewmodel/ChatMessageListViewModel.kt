package com.nextiva.nextivaapp.android.features.rooms.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.FileUtil
import com.nextiva.nextivaapp.android.core.common.FileUtil.Companion.getContentTypeFromFileName
import com.nextiva.nextivaapp.android.core.common.UiUtil.Companion.toMutableLiveData
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentAudioFilePlayer
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentInfo
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentMenuItems
import com.nextiva.nextivaapp.android.core.common.ui.MessageTextFieldInterface
import com.nextiva.nextivaapp.android.core.common.ui.SendingViaBanner
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.dialogs.ConnectMenuDialog
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.ConversationViewModel
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessage
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessageAttachment
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbChatMessage
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.model.RoomMessageListItem
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageBubbleType
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.PlatformRoomsApiManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager.ProcessParticipantInfoCallBack
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.MessageUtil
import com.nextiva.nextivaapp.android.util.StringUtil
import com.nextiva.nextivaapp.android.util.extensions.downloadFileAsByteArray
import com.nextiva.nextivaapp.android.util.extensions.drawableToByteArray
import com.nextiva.nextivaapp.android.util.extensions.gifDrawableToByteArray
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.io.File
import java.io.OutputStream
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.*

@HiltViewModel
class ChatMessageListViewModel @Inject constructor(application: Application, val nextivaApplication: Application, val dbManager: DbManager, val roomsDbManager: RoomsDbManager,
                                                   private val platformRoomsRepository: PlatformRoomsRepository, val schedulerProvider: SchedulerProvider,
                                                   private val calendarManager: CalendarManager, private val sessionManager: SessionManager,
                                                   private val nextivaMediaPlayer: NextivaMediaPlayer, private val notificationManager: NotificationManager,
                                                   private val callManager: CallManager) :
    BaseViewModel(application), MessageTextFieldInterface,
    ProcessParticipantInfoCallBack {

    companion object {
        const val MAX_ATTACHMENT_SIZE = 100000000
        const val MAX_ATTACHMENTS = 10
    }

    var attachmentAudioFilePlayer = AttachmentAudioFilePlayer()
    var pageNumber = PlatformRoomsApiManager.START_PAGE_NUMBER
    private var nextPage = ""
    private var hasMorePages = true
    private var apiMessageCount = 0
    private var isPageLoading = false

    val memberList: HashMap<String, NextivaContact> = HashMap()
    var messages = MutableLiveData<List<RoomMessageListItem>>()
    val presenceMap = mutableMapOf<String, LiveData<DbPresence>>()

    var roomId: MutableLiveData<String?> = MutableLiveData(null)
    var singleContact: MutableLiveData<NextivaContact?> = MutableLiveData(null)
    var newRoomMemberList: List<String>? = null
    var dbRoom: LiveData<DbRoom?> = roomId.switchMap {
        roomsDbManager.getRoom(it ?: "") }
    private val allMessages: LiveData<List<RoomMessageListItem>> = dbRoom.switchMap { dbRoom ->
        updateMembers()
        updateSingleContact()
        resetMessageCount()
        fetchChatMessages(PlatformRoomsApiManager.START_PAGE_NUMBER) { }
        roomsDbManager.getAllChatMessages(roomId = dbRoom?.roomId).map { list ->
            list.map { item ->
                item.senderId?.let { userId ->
                    if (!presenceMap.contains(userId)) {
                        presenceMap[userId] = dbManager.getPresenceLiveDataFromContactTypeId(userId)
                        presenceMap[userId]?.observeForever { presence ->
                            presence?.let { updatePresence(it) }
                        }
                    }
                }

                item.attachments?.forEach { attachmentAudioFilePlayer.updateSpeakerEnabled(it.id, false) }

                notificationManager.cancelNotificationByTag(item.id, Enums.Notification.TypeIDs.NEW_CHAT_MESSAGE_NOTIFICATION_ID)

                RoomMessageListItem(item, null)
            }
        }
    }

    private fun updatePresence(presence: DbPresence) {
        var presenceChanged = (allMessages.value?.filter {
            (TextUtils.equals(it.message.senderId, presence.userId) && presence.state != it.presence?.state)
        }?.count() ?: 0) > 0

        if (presenceChanged) {
            messages.postValue(allMessages.value?.map { item ->
                RoomMessageListItem(
                    message = item.message,
                    presence = if (TextUtils.equals(item.message.senderId, presence.userId)) presence else item.presence
                )
            })
        }
    }

    var messageIndexMarkedForDeletion: MutableLiveData<Int?> = MutableLiveData(null)
    private var deleteUndoMessage: DbChatMessage? = null

    var messageIndexMarkedForEdit: MutableLiveData<Int?> = MutableLiveData(null)

    init {
        messages = allMessages.toMutableLiveData()
        dbManager.getConnectGroupCount(Enums.Platform.ConnectContactGroups.ALL_CONTACTS).observeForever { count ->
            // for the case where the user has just logged in and the contact list has not fully loaded,
            // reload the memberList after all contacts are fetched
            if (count > 0) {
                updateMembers()
            }
        }

        nextivaMediaPlayer.getCurrentPlayingProgressChangedLiveData().observeForever {
            attachmentAudioFilePlayer.updateProgress(it)
        }
    }

    fun loadRoom(roomId: String) {
        this.roomId.postValue(roomId)
    }

    private fun updateMembers() {
        dbRoom.value?.let { room ->
            room.members?.forEach { member ->
                member.userUuid?.let { userUuid ->
                    mCompositeDisposable.add(
                        dbManager.getContactFromContactTypeId(userUuid).subscribe { contact ->
                            memberList[userUuid] = contact
                        }
                    )
                }
            }
        }
    }

    fun isSent(item: RoomMessageListItem): Boolean {
        return item.message.senderId == sessionManager.phoneNumberInformation?.userUuid
    }

    fun isAllowedImageType(filename: String): Boolean {
        return FileUtil.hasExtension(filename, FileUtil.ALLOWED_FILE_IMAGE_TYPES)
    }

    fun isTheFileAnImageOrVideo(filename: String): Boolean {
        val isImage = isAllowedImageType(filename)
        val isVideo = FileUtil.hasExtension(filename, FileUtil.VIDEO_FILE_TYPES)
        return isImage || isVideo
    }

    fun isAllowedFileType(filename: String): Boolean {
        val isExcluded = FileUtil.hasExtension(filename, FileUtil.EXCLUDED_TYPES)
        return !isExcluded
    }

    fun getImageFullSizeIfNeeded(context: Context, thumbnail: Drawable?, contentType: String, link: String, onResourceReady: (Drawable?) -> Unit) {
        if (contentType.contains(Enums.Attachment.ContentExtensionType.EXT_GIF) || contentType.contains(Enums.Attachment.ContentMajorType.IMAGE)) {
            // attachment is image -> get full size image and discard thumbnail
            Glide.with(context).asDrawable().load(link).into(object: CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    onResourceReady(resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) { }
            })
        } else {
            // attachment is not image -> return the thumbnail since it is not necessary to get the full image
            onResourceReady(thumbnail)
        }
    }

    fun downloadImageOrFile(accessToken: String, contentType: String, drawable: Drawable?, fullLink: String, onFileDownloaded: (ByteArray?) -> Unit) {
        viewModelScope.launch {
            if (contentType.contains(Enums.Attachment.ContentExtensionType.EXT_GIF)) {
                // attachment is gif image
                onFileDownloaded((drawable as GifDrawable).gifDrawableToByteArray())
            } else if (contentType.contains(Enums.Attachment.ContentMajorType.IMAGE)) {
                // attachment is image any type except gif
                onFileDownloaded(drawable?.drawableToByteArray())
            } else {
                // attachment is not image -> download as file
                onFileDownloaded(sessionManager.sessionId?.let {
                    fullLink.downloadFileAsByteArray(it, sessionManager.userInfo?.comNextivaCorpAccountNumber.toString())
                })
            }
        }
    }

    fun saveMediaToStorage(context: Context, filename: String): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtil.saveMediaToStorageQ(context, filename, Environment.DIRECTORY_DOWNLOADS)
        } else {
            FileUtil.saveMediaToStorageLegacy(filename, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        }
    }

    fun avatarInfo(item: RoomMessageListItem, index: Int): AvatarInfo? {
        if (displayBubbleTitle(index)) {
            val displayName = memberList[item.message.senderId]?.displayName ?: ""
            val presence = item.message.senderId?.let { presenceMap[it]?.value }

            return AvatarInfo.Builder()
                .setDisplayName(displayName)
                .setPresence(presence)
                .isConnect(true)
                .build()
        }

        return null
    }

    fun attachmentAvatarName(item: RoomMessageListItem, index: Int, attachmentIndex: Int): String? {
        if (displayBubbleTitle(index) && TextUtils.isEmpty(item.message.text) && attachmentIndex == 0) {
            val displayName = memberList[item.message.senderId]?.displayName ?: ""
            return displayName
        }

        return null
    }

    fun displayTime(item: RoomMessageListItem): String {
        item.message.timestamp?.let {
            return FormatterManager.getInstance().format_connectHourMinuteTimeStamp(nextivaApplication, it)
        }
        return ""
    }

    fun sentDisplayTime(item: RoomMessageListItem, index: Int): String? {
        return if (displayBubbleTitle(index)) displayTime(item) else null
    }

    fun attachmentSentDisplayTime(item: RoomMessageListItem, index: Int, attachmentIndex: Int): String? {
        return if (displayBubbleTitle(index) && attachmentIndex == 0 && TextUtils.isEmpty(item.message.text)) displayTime(item) else null
    }

    fun formattedTextMessage(item: RoomMessageListItem): String {
        val editedResource = nextivaApplication.getString(R.string.room_conversation_message_edited)
        val edited = if (item.message.edited == true) " ($editedResource)" else ""
        return "${item.message.text}$edited"
    }

    private fun displayBubbleTitle(index: Int): Boolean {
        val message = allMessages.value?.get(index)?.message ?: return true
        val timestamp = message.timestamp ?: Instant.MIN
        val userId = message.senderId ?: ""

        var previousTimestamp = Instant.MIN
        var previousUserId = ""
        val previousIndex = index + 1

        val messageCount = allMessages.value?.count() ?: 0
        if (previousIndex < messageCount) {
            allMessages.value?.get(previousIndex)?.message?.let { previousMessage ->
                previousMessage.timestamp?.let {
                    previousTimestamp = it
                }
                previousUserId = previousMessage.senderId ?: previousUserId
            }
        }

        return (userId != previousUserId || !sameTimestamp(timestamp, previousTimestamp))
    }

    // Bubble type of text message ignoring attachments
    private fun bubbleTypeRaw(index: Int): MessageBubbleType {
        val message = allMessages.value?.get(index)?.message ?: return MessageBubbleType.NONE
        val timestamp = message.timestamp ?: Instant.MIN
        val userId = message.senderId ?: ""

        var previousTimestamp = Instant.MIN
        var previousUserId = ""
        val previousIndex = index+1

        var nextTimestamp = Instant.MIN
        var nextUserId = ""
        val nextIndex = index-1

        val messageCount = allMessages.value?.count() ?: 0
        if (previousIndex < messageCount) {
            allMessages.value?.get(previousIndex)?.message?.let { previousMessage ->
                previousMessage.timestamp?.let {
                    previousTimestamp = it
                }
                previousUserId = previousMessage.senderId ?: previousUserId
            }
        }
        if (nextIndex >= 0) {
            allMessages.value?.get(nextIndex)?.message?.let { nextMessage ->
                nextMessage.timestamp?.let {
                    nextTimestamp = it
                }
                nextUserId = nextMessage.senderId ?: nextUserId
            }
        }

        if (userId != previousUserId && userId != nextUserId) {
            return MessageBubbleType.NONE
        } else if (userId == previousUserId && userId != nextUserId) {
            if (sameTimestamp(timestamp, previousTimestamp)) {
                return MessageBubbleType.BOTTOM
            }
        } else if (userId != previousUserId && userId == nextUserId) {
            if (sameTimestamp(timestamp, nextTimestamp)) {
                return MessageBubbleType.TOP
            }
        } else if (userId == previousUserId && userId == nextUserId) {
            if (sameTimestamp(timestamp, previousTimestamp) && sameTimestamp(timestamp, nextTimestamp)) {
                return MessageBubbleType.MIDDLE
            } else if (sameTimestamp(timestamp, previousTimestamp)) {
                return MessageBubbleType.BOTTOM
            } else if (sameTimestamp(timestamp, nextTimestamp)) {
                return MessageBubbleType.TOP
            }
        }

        return MessageBubbleType.NONE
    }

    fun bubbleType(index: Int): MessageBubbleType {
        val message = allMessages.value?.get(index)?.message ?: return MessageBubbleType.NONE
        val attachmentCount = message.attachments?.count() ?: 0

        val rawBubbleType = bubbleTypeRaw(index)
        if (attachmentCount > 0) {
            if (rawBubbleType == MessageBubbleType.BOTTOM) {
                return MessageBubbleType.MIDDLE
            }
            if (rawBubbleType == MessageBubbleType.NONE) {
                return MessageBubbleType.TOP
            }
        }

        return rawBubbleType
    }

    fun attachmentBubbleType(index: Int, attachmentIndex: Int): MessageBubbleType {
        val rawBubbleType = bubbleTypeRaw(index)
        val message = allMessages.value?.get(index)?.message ?: return MessageBubbleType.NONE
        val attachmentCount = message.attachments?.count() ?: return MessageBubbleType.NONE
        if (attachmentCount > 1) {
            if (attachmentIndex == 0) {
                return if (TextUtils.isEmpty(message.text) && rawBubbleType == MessageBubbleType.TOP) MessageBubbleType.TOP else MessageBubbleType.MIDDLE
            } else if (attachmentIndex == (attachmentCount - 1)) {
                if (rawBubbleType == MessageBubbleType.TOP || rawBubbleType == MessageBubbleType.MIDDLE) {
                    return MessageBubbleType.MIDDLE
                }
                return MessageBubbleType.BOTTOM
            }
            return MessageBubbleType.MIDDLE
        }

        if (TextUtils.isEmpty(message.text) || rawBubbleType == MessageBubbleType.BOTTOM) {
            return rawBubbleType
        }

        if (rawBubbleType == MessageBubbleType.TOP || rawBubbleType == MessageBubbleType.MIDDLE) {
            return MessageBubbleType.MIDDLE
        }

        return MessageBubbleType.NONE
    }

    fun markForDeletion(messageIndex: Int) {
        messageIndexMarkedForDeletion.postValue(messageIndex)
    }

    fun unmarkForDeletion() {
        messageIndexMarkedForDeletion.postValue(null)
    }

    fun markForEdit(messageIndex: Int) {
        unmarkForDeletion()
        messageIndexMarkedForEdit.postValue(messageIndex)
        allMessages.value?.get(messageIndex)?.message?.let {
            editMessage.postValue(it.text)
        }
    }

    private fun sameTimestamp(instantA: Instant, instantB: Instant): Boolean {
        val localDateA = instantA.atZone(ZoneId.systemDefault()).toLocalDate()
        val localDateB = instantB.atZone(ZoneId.systemDefault()).toLocalDate()
        return localDateA.isEqual(localDateB)
    }

    fun separatorTimestamp(item: RoomMessageListItem, index: Int): String? {
        if (index >= (apiMessageCount-1) && hasMorePages && !isPageLoading) {
            fetchChatMessages(pageNumber) { }
        }

        val timestamp = item.message.timestamp ?: Instant.MIN
        val nextIndex = index + 1
        val messageCount = allMessages.value?.count() ?: 0
        val formatterManager = FormatterManager.getInstance()
        if (nextIndex < messageCount) {
            allMessages.value?.get(nextIndex)?.message?.let { nextMessage ->
                nextMessage.timestamp?.let { nextTimestamp ->
                    if (!sameTimestamp(timestamp, nextTimestamp)) {
                        return formatterManager.format_humanReadableSmsDate(nextivaApplication, calendarManager, timestamp)
                    }
                }
            }
        } else if (nextIndex == messageCount) {
            return formatterManager.format_humanReadableSmsDate(nextivaApplication, calendarManager, timestamp)
        }
        return null
    }

    fun displayName(room: DbRoom?, isToolbarTitle: Boolean = false) : String {
        var displayName = ""

        room?.let { room ->
            if (room.typeEnum() == RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM) {
                val myRoom = nextivaApplication.getString(R.string.my_status_my_room)
                displayName = myRoom

                sessionManager.userDetails?.fullName?.let {
                    if (!TextUtils.isEmpty(it)) {
                        displayName = if (isToolbarTitle) {
                            val roomTitle = nextivaApplication.getString(R.string.connect_room_toolbar_title)
                            "$it's $roomTitle"
                        } else {
                            "$myRoom ($it)"
                        }
                    }
                }
            } else {
                room.name?.let { displayName = it }
            }
        }

        return displayName
    }

    private fun updateSingleContact() {
        viewModelScope.launch(Dispatchers.IO) {
            dbRoom?.value?.let { room ->
                if (room.isChat() && (room.members?.count() ?: 0) <= 2) {
                    dbRoom.value?.members?.firstOrNull { it.userUuid != sessionManager.userInfo?.comNextivaUseruuid }?.let { member ->
                        singleContact.postValue(dbManager.getConnectContactFromUuidInThread(member.userUuid).value)
                    }
                }
            }
        }
    }

    fun phoneIconAction(activity: FragmentActivity) : (() -> Unit)? {
        if (singleContact.value != null) {
            return {
                singleContact.value?.let { contact ->
                    contact.allPhoneNumbers?.filter { it.type != Enums.Contacts.PhoneTypes.FAX }?.let { phoneNumbers ->
                        if (phoneNumbers.size > 1) {
                            ConnectMenuDialog(getPhoneNumberListItems(phoneNumbers)) { phoneNumber ->
                                (phoneNumber as? PhoneNumber)?.strippedNumber?.let {
                                    placeCall(activity, contact, it)
                                }
                            }.show(activity.supportFragmentManager, null)
                        } else {
                            phoneNumbers.firstOrNull()?.strippedNumber?.let {
                                placeCall(activity, contact, it)
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun placeCall(activity: Activity, nextivaContact: NextivaContact, number: String) {
        val participantInfo = ParticipantInfo(callType = Enums.Sip.CallTypes.VOICE,
                                                contactId = nextivaContact.userId,
                                                numberToCall = number,
                                                displayName = nextivaContact.uiName)

        callManager.processParticipantInfo(activity, Enums.Analytics.ScreenName.CONNECT_TEAM_CHAT_LIST, participantInfo, mCompositeDisposable, this)
    }

    override fun onParticipantInfoProcessed(
        activity: Activity,
        analyticsScreenName: String,
        participantInfo: ParticipantInfo,
        retrievalNumber: String?,
        compositeDisposable: CompositeDisposable
    ) {
        callManager.makeCall(activity, analyticsScreenName, participantInfo, mCompositeDisposable)
    }

    private fun getPhoneNumberListItems(phoneNumbers: List<PhoneNumber>?): java.util.ArrayList<DialogContactActionDetailListItem> {
        val listItems: java.util.ArrayList<DialogContactActionDetailListItem> =
            java.util.ArrayList()

        phoneNumbers?.forEach { phoneNumber ->
            val number = if (PhoneNumberUtils.formatNumber(phoneNumber.number, Locale.getDefault().country).isNullOrEmpty()) {
                phoneNumber.number
            } else {
                PhoneNumberUtils.formatNumber(phoneNumber.number, Locale.getDefault().country)
            }

            listItems.add(
                DialogContactActionDetailListItem(
                    StringUtil.getPhoneNumberTypeLabel(nextivaApplication, phoneNumber, false, true),
                number ?: "",
                null)
            )
        }

        return listItems
    }

    fun convertAttachments(attachments: List<Uri>?): List<MultipartBody.Part> {
        var parts = arrayListOf<MultipartBody.Part>()
        attachments?.forEach { attachment ->
            val extension = FileUtil.getFileExtensionFromUri(attachment, nextivaApplication)
            val mimeType = MessageUtil.getMimeType(attachment, nextivaApplication) ?: "application/$extension"
            attachment.path?.let { attachmentPath ->
                var fileName = FileUtil.getFileNameFromUri(attachment, nextivaApplication) ?:
                    attachmentPath.substring(attachmentPath.lastIndexOf('/') + 1)

                if (!extension.isNullOrEmpty() && !mimeType.isNullOrEmpty()) {
                    val file = File(nextivaApplication.cacheDir, fileName)
                    file.createNewFile()

                    if (FileUtil.RESIZEABLE_FILE_TYPES.contains(extension)) {
                        FileUtil.compressImage(nextivaApplication, attachment, file, MAX_ATTACHMENT_SIZE)
                    } else {
                        FileUtil.saveFile(nextivaApplication, attachment, file)
                    }

                    val fileToUpload = MultipartBody.Part.createFormData(
                        "files",
                        fileName,
                        RequestBody.create(mimeType.toMediaTypeOrNull(), file)
                    )

                    parts.add(fileToUpload)
                }
            }
        }

        return parts
    }

    private fun sendTextMessage(text: String?, messageId: String) {
        if (messageId.endsWith(DbChatMessage.UNSENT_FLAG)) {
            storeUnsentMessage(text ?: "", failedToSendMessageId = messageId)
        }

        roomId.value?.let { roomId ->
            isSending.postValue(true)
            mCompositeDisposable.add(
                platformRoomsRepository.sendChatMessage(roomId, text ?: "", messageId)
                    .subscribe { sendChatMessageResponse ->
                        sendChatMessageResponse?.let {
                            fetchChatMessages(PlatformRoomsApiManager.START_PAGE_NUMBER) {
                                isSending.postValue(false)
                            }
                        }
                    })
        }
    }

    private fun sendTextMessage(text: String?, failedToSendMessage: DbChatMessage? = null) {
        val roomId = roomId?.value ?: return
        if (text != null) {
            isSending.postValue(true)
            mCompositeDisposable.add(
                platformRoomsRepository.sendChatMessage(roomId, text)
                    .doOnError {
                        if (failedToSendMessage == null) {
                            storeUnsentMessage(text)
                        }
                        isSending.postValue(false)
                    }
                    .subscribe { sendChatMessageResponse ->
                        if (sendChatMessageResponse != null) {
                            failedToSendMessage?.id?.let {
                                roomsDbManager.deleteChatMessage(roomId, it)
                            }
                            fetchChatMessages(PlatformRoomsApiManager.START_PAGE_NUMBER) {
                                isSending.postValue(false)
                            }
                        } else {
                            if (failedToSendMessage == null) {
                                storeUnsentMessage(text)
                            }
                            isSending.postValue(false)
                        }
                    })
        } else {
            fetchChatMessages(PlatformRoomsApiManager.START_PAGE_NUMBER) {
                isSending.postValue(false)
            }
        }
    }

    private fun sendTextMessageWithAttachments(text: String?, attachmentUriList: List<Uri>, failedToSendMessage: DbChatMessage? = null) {
        val roomId = roomId?.value ?: return
        val filesToUpload = convertAttachments(attachmentUriList)
        mCompositeDisposable.add(
            platformRoomsRepository.sendChatAttachments(roomId, filesToUpload)
                .doOnError {
                    if (failedToSendMessage == null) {
                        storeUnsentMessage(text ?: "", attachmentUriList)
                    }
                    isSending.postValue(false)
                }
                .subscribe { roomAttachmentResponse ->
                    roomAttachmentResponse?.let {
                        failedToSendMessage?.id?.let {
                            roomsDbManager.deleteChatMessage(roomId, it)
                        }
                        sendTextMessage(text, it.id)
                    }
                })
        selectedAttachments.postValue(null)
    }

    private fun storeUnsentMessage(text: String, attachmentUriList: List<Uri>? = null, failedToSendMessageId: String? = null) {
        val roomId = roomId.value ?: return
        val id = failedToSendMessageId ?: "${UUID.randomUUID()}${DbChatMessage.UNSENT_FLAG}"
        val senderId = sessionManager.phoneNumberInformation?.userUuid ?: return

        val message = ChatMessage(
            id = id,
            roomId = roomId,
            senderId = senderId,
            text = text
        )

        val attachments = attachmentUriList?.map { uri ->
            ChatMessageAttachment(
                filename = FileUtil.getFileNameFromUri(uri, nextivaApplication),
                link = uri.toString(),
                contentType = MessageUtil.getMimeType(uri, nextivaApplication))
        }
        attachments?.let {
            message.attachments = ArrayList(it)
        }

        roomsDbManager.saveRoomChatMessages(arrayListOf(message))
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    isSending.postValue(false)
                }

                override fun onError(throwable: Throwable) {
                    isSending.postValue(false)
                }
            })
    }

    private fun updateMessage(text: String) {
        roomId.value?.let { room ->
            messageIndexMarkedForEdit.value?.let { messageIndex ->
                allMessages.value?.get(messageIndex)?.message?.let { message ->
                    if (message.text == text && (selectedAttachments.value?.isEmpty() == true)) {
                        onEditMessageCancel()
                        return
                    }

                    message.text = text
                    messageIndexMarkedForEdit.postValue(null)
                    editMessage.postValue(null)
                    isSending.postValue(true)
                    mCompositeDisposable.add(
                        platformRoomsRepository.updateChatMessage(room, message.id, text)
                            .subscribe { sendChatMessageResponse ->
                                sendChatMessageResponse?.let {
                                    val filesToUpload = convertAttachments(selectedAttachments.value?.map { it.uri })
                                    if (filesToUpload.isEmpty()) {
                                        roomsDbManager.updateRoomChatMessage(message)
                                        isSending.postValue(false)
                                        roomId.postValue(room) // force refresh of the conversation view
                                    } else {
                                        mCompositeDisposable.add(
                                            platformRoomsRepository.sendChatAttachments(room, filesToUpload, message.id)
                                                .subscribe { roomAttachmentResponse ->
                                                    roomAttachmentResponse?.let {
                                                        roomsDbManager.updateRoomChatMessage(message)
                                                        isSending.postValue(false)
                                                        roomId.postValue(room)
                                                    }
                                                })
                                        selectedAttachments.postValue(null)
                                    }
                                }
                            })
                }
            }
        }
    }

    fun hasSent(item: RoomMessageListItem): Boolean {
        return !(item.message.id.endsWith(DbChatMessage.UNSENT_FLAG))
    }

    fun retrySend(item: RoomMessageListItem) {
        val attachmentUriList = item.message.attachments?.map { Uri.parse(it.reference.link) }
        if (attachmentUriList.isNullOrEmpty()) {
            sendTextMessage(item.message.text, item.message)
        } else {
            sendTextMessageWithAttachments(item.message.text, attachmentUriList, item.message)
        }
    }

    fun deleteMessageDatabase() {
        val validRoomId = roomId.value ?: return
        val index = messageIndexMarkedForDeletion.value ?: return
        val message = allMessages.value?.get(index)?.message ?: return
        deleteUndoMessage = message
        unmarkForDeletion()
        roomsDbManager.deleteChatMessage(validRoomId, message.id)
    }

    fun deleteMessageNetwork() {
        val message = deleteUndoMessage ?: return
        roomId.value?.let { roomId ->
            mCompositeDisposable.add(
                platformRoomsRepository.deleteChatMessage(roomId, message.id)
                    .subscribe { isSuccess ->
                        if (isSuccess) {
                            message.attachments?.forEach { attachment ->
                                File(nextivaApplication.cacheDir, attachment.filename).delete()
                            }
                        }
                    })
        }
    }

    fun undoDeleteMessage() {
        val message = deleteUndoMessage ?: return
        roomsDbManager.undoDeleteChatMessage(message)
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() { }
                override fun onError(throwable: Throwable) { }
            })
    }

    private fun fetchChatMessages(fetchPage: Int, onSaveFinishedCallback: () -> Unit) {
        if (TextUtils.isEmpty(roomId.value)) return
        roomId.value?.let {
            isPageLoading = true
            platformRoomsRepository.fetchChatMessages(it, fetchPage, mCompositeDisposable) { pageInfo ->
                apiMessageCount += PlatformRoomsApiManager.CHAT_MESSAGE_PAGE_SIZE
                hasMorePages = (pageInfo.previousPage != pageInfo.nextPage) && (nextPage != pageInfo.nextPage)
                if (hasMorePages) {
                    pageNumber += 1
                }
                nextPage = pageInfo.nextPage
                isPageLoading = false
                onSaveFinishedCallback()
            }
        }
    }

    private fun resetMessageCount(){
        dbRoom.value?.let { DbRoom ->
            if(DbRoom.unreadMessageCount != null && DbRoom.unreadMessageCount!! > 0){
                roomsDbManager.modifyUnreadMessageCountByRoomId(count = 0, roomId = DbRoom.roomId)
            }
        }
    }

    private fun createRoom(text: String?) {
        val memberUuids = newRoomMemberList?.toMutableList() ?: return
        sessionManager.userInfo?.comNextivaUseruuid?.let { memberUuids.add(it) }
        viewModelScope.launch(Dispatchers.IO) {
            val members = memberUuids.mapNotNull { dbManager.getConnectContactFromUuidInThread(it).value }
            platformRoomsRepository.createRoom(members)
                .subscribe { room ->
                    if (room != null) {
                        roomId.postValue(room.id)
                        roomsDbManager.saveRooms(arrayListOf(room))
                            .observeOn(schedulerProvider.ui())
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {
                                    onSend(text)
                                }

                                override fun onError(e: Throwable) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                }
                            })
                    }
                }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Audio File Attachments
    // --------------------------------------------------------------------------------------------
    fun playAudioAttachment(messageId: String, attachment: ChatMessageAttachment) {
        attachmentAudioFilePlayer.activeItemId = attachment.id
        attachmentAudioFilePlayer.activeItemFilename = attachment.filename
        attachmentAudioFilePlayer.activeItemUrl = attachment.url

        getAudioFile()?.also { audioFile ->
            initializeAudioPlayer(audioFile)
        } ?: run {
            with(attachmentAudioFilePlayer) {
                downloadAudioAttachment(
                        link = activeItemUrl,
                        messageId = messageId,
                        attachmentId = activeItemId.ifEmpty { attachment.id },
                        contentType = getContentTypeFromFileName(activeItemFilename)
                )
            }
        }
    }

    fun pauseAudio() {
        nextivaMediaPlayer.pausePlaying()
    }

    private fun getAudioFile(): File? {
        val messageId = attachmentAudioFilePlayer.activeItemId
        val fileName = attachmentAudioFilePlayer.activeItemFilename
        return if (messageId.isEmpty()) {
            nextivaMediaPlayer.getAudioFileFromCacheByName(nextivaApplication, fileName)
        } else {
            nextivaMediaPlayer.getAudioFileFromCache(nextivaApplication, messageId)
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

    private fun downloadAudioAttachment(link: String, contentType: String, messageId: String, attachmentId: String) {
        sessionManager.sessionId?.let {
            roomsDbManager.saveAudioDataFromLinkWithReturn(roomId.value, messageId, attachmentId, link, it, sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
            )
                .flatMap { contentData -> MessageUtil.createAudioCacheFile(nextivaApplication, messageId, contentType, contentData) }
                .map { file ->
                    schedulerProvider.ui().scheduleDirect { initializeAudioPlayer(file) }
                    MessageUtil.getAudioFileDuration(file.path, nextivaApplication)
                }
                .flatMap { duration -> roomsDbManager.saveAudioFileDuration(roomId.value, messageId, attachmentId, duration) }
                .observeOn(schedulerProvider.ui())
                .subscribe(object : DisposableSingleObserver<Long>() {
                    override fun onSuccess(duration: Long) {
                        attachmentAudioFilePlayer.updateDuration(duration)
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
        }
    }

    fun audioProgressDragged(progress: Int) {
        val progressMilliSeconds = attachmentAudioFilePlayer.duration * (progress.toDouble() / 100)
        nextivaMediaPlayer.setProgress(progressMilliSeconds.toInt(), true)
    }

    fun toggleSpeaker(attachmentId: String) {
        val enabled = attachmentAudioFilePlayer.speakerEnabledLiveData(attachmentId).value ?: false
        attachmentAudioFilePlayer.updateSpeakerEnabled(attachmentId, !enabled)

        val playerSpeakerEnabled = nextivaMediaPlayer.isSpeakerPhoneEnabled()
        if (!enabled != playerSpeakerEnabled) {
            nextivaMediaPlayer.toggleSpeakerPhone(nextivaApplication)
        }
    }

    // --------------------------------------------------------------------------------------------
    // MessageTextFieldInterface
    // --------------------------------------------------------------------------------------------
    override val isSending: MutableLiveData<Boolean> = MutableLiveData(false)
    override val editMessage: MutableLiveData<String?> = MutableLiveData(null)
    override val setDraftMessage: MutableLiveData<String?> = MutableLiveData(null)
    override val errorMessages: MutableLiveData<List<String>?> = MutableLiveData(null)
    override val selectedAttachments: MutableLiveData<List<AttachmentInfo>> = MutableLiveData(listOf())
    override val sendingPhoneNumbers: MutableLiveData<ArrayList<ConversationViewModel.SendingPhoneNumber>?> = MutableLiveData(null)
    override val conversationDetails: MutableLiveData<SmsConversationDetails?> = MutableLiveData(null)
    override val sendingViaBanner: MutableLiveData<SendingViaBanner?> = MutableLiveData(null)

    override fun onEditMessageCancel() {
        messageIndexMarkedForEdit.postValue(null)
        editMessage.postValue(null)
    }

    override fun onSendingViaBannerClosed() {
        sendingViaBanner.postValue(null)
    }

    override fun onSend(text: String?) {
        if (messageIndexMarkedForEdit.value != null && text != null) {
            updateMessage(text)
            return
        }

        if (TextUtils.isEmpty(roomId.value)) {
            createRoom(text)
        } else {
            isSending.postValue(true)

            val attachmentUriList = selectedAttachments.value?.map { it.uri }
            if (attachmentUriList.isNullOrEmpty()) {
                sendTextMessage(text)
            } else {
                sendTextMessageWithAttachments(text, attachmentUriList)
            }
        }
    }

    override fun onValueChanged(text: String) { }

    override fun addAttachments(attachments: List<Uri>) {
        var updatedAttachmentList = ArrayList<AttachmentInfo>()
        selectedAttachments.value?.forEach {
            updatedAttachmentList += it
        }

        attachments.forEach {
            viewModelScope.launch(Dispatchers.IO) {
                updatedAttachmentList += attachmentInfo(nextivaApplication, it)

                val postList = ArrayList<AttachmentInfo>()
                updatedAttachmentList.forEach { postList.add(it) }

                selectedAttachments.postValue(postList)
                refreshErrors(updatedAttachmentList)
            }
        }
    }

    override fun removeAttachment(info: AttachmentInfo) {
        var updatedAttachmentList = ArrayList<AttachmentInfo>()
        selectedAttachments.value?.forEach {
            if (it.uri != info.uri) {
                updatedAttachmentList.add(it)
            }
        }
        selectedAttachments.postValue(updatedAttachmentList)
        refreshErrors(updatedAttachmentList)
    }

    fun refreshErrors(attachments: ArrayList<AttachmentInfo>) {
        var sizeError = false
        var typeError = false
        val countError = attachments?.size ?: 0 > MAX_ATTACHMENTS

        attachments?.forEach { attachment ->
            if (attachment.byteSize > MAX_ATTACHMENT_SIZE && !attachment.resizeableType) {
                sizeError = true
            }
            if (attachment.excludedType) {
                typeError = true
            }
        }

        var errorList = ArrayList<String>()
        if (sizeError) {
            errorList += nextivaApplication.getString(R.string.room_conversation_file_size_error)
        }
        if (typeError) {
            errorList += nextivaApplication.getString(R.string.room_conversation_file_type_error)
        }
        if (countError) {
            errorList += nextivaApplication.getString(R.string.room_conversation_file_count_error)
        }

        errorMessages.postValue(errorList)
    }

    override fun isExcludedType(uri: Uri) : Boolean {
        return FileUtil.hasExtension(nextivaApplication, uri, FileUtil.EXCLUDED_TYPES)
    }

    override fun onAlertError() {

    }

    override fun isImageError(uri: Uri, byteSize: Long): Boolean {
        if (byteSize > MAX_ATTACHMENT_SIZE &&
            !FileUtil.hasExtension(nextivaApplication, uri, FileUtil.RESIZEABLE_FILE_TYPES)) {
            return true
        }
        return isExcludedType(uri)
    }

    override fun hasThumbnail(uri: Uri): Boolean {
        return FileUtil.hasExtension(nextivaApplication, uri, FileUtil.THUMBNAIL_FILE_TYPES)
    }

    override fun menuItems() : List<AttachmentMenuItems> {
        return listOf(AttachmentMenuItems.TAKE_PICTURE, AttachmentMenuItems.CHOOSE_PHOTO_OR_VIDEO, AttachmentMenuItems.ATTACH_FILE)
    }

    // --------------------------------------------------------------------------------------------
}