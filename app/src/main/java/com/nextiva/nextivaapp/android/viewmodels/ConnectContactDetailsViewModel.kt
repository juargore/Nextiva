package com.nextiva.nextivaapp.android.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailFooterListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationActivity
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoom
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.view.RoomConversationActivity
import com.nextiva.nextivaapp.android.managers.BlockingState
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.ListHeaderRow
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.models.net.platform.GenerateGroupIdPostBody
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectEmailType
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectPhoneType
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.Event
import com.nextiva.nextivaapp.android.util.StringUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableCompletableObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ConnectContactDetailsViewModel @Inject constructor(
    application: Application,
    private val nextivaApplication: Application,
    private val platformContactsRepository: PlatformContactsRepository,
    private val blockingNumberManager: BlockingNumberManager,
    val platformRoomsRepository: PlatformRoomsRepository,
    val dbManager: DbManager,
    val sessionManager: SessionManager,
    val callManager: CallManager,
    val roomsDbManager: RoomsDbManager,
    val smsManagementRepository: SmsManagementRepository,
    val sipManager: PJSipManager,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel(application) {
    private var baseListItemsLiveData: MutableLiveData<ArrayList<BaseListItem>> = MutableLiveData()
    var contactLiveData: MediatorLiveData<NextivaContact?> = MediatorLiveData()

    private var headerListItems: ArrayList<ConnectContactDetailHeaderListItem>? = ArrayList()

    private var allTeams = sessionManager.allTeams
    val activeCallLiveData = sipManager.activeCallLiveData

    private val _groupId: MutableLiveData<Event<Pair<String, String>>> = MutableLiveData()
    val groupId: LiveData<Event<Pair<String, String>>> = _groupId
    private var job: Job? = null

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _addContactPressed: MutableLiveData<Event<Int>> = MutableLiveData()
    val addContactPressed: LiveData<Event<Int>> = _addContactPressed

    private val _blockingFeatureEvent: MutableLiveData<BlockingState?> = MutableLiveData()
    val blockingFeatureEvent: LiveData<BlockingState?> = _blockingFeatureEvent

    val nextivaContact: NextivaContact?
        get() = contactLiveData.value

    var room: ConnectRoom? = null

    fun getDetailListItems() {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        if (headerListItems.isNullOrEmpty()) {
            headerListItems = ArrayList()
            headerListItems?.add(
                ConnectContactDetailHeaderListItem(
                    ListHeaderRow(nextivaApplication.getString(R.string.connect_contact_section_header_contact_information)),
                    getPrimaryContactInformationListItems(),
                    Enums.Platform.ConnectContactDetailSections.PRIMARY,
                    isExpanded = true,
                    isShowingMore = false,
                    shouldShowHeaderDetails = true
                ))

            headerListItems?.add(ConnectContactDetailHeaderListItem(
                    ListHeaderRow(nextivaApplication.getString(R.string.connect_contact_section_header_additional_details)),
                    getAdditionalContactInformationListItems(),
                    Enums.Platform.ConnectContactDetailSections.ADDITIONAL,
                    isExpanded = true,
                    isShowingMore = true,
                    shouldShowHeaderDetails = true))
        }

        headerListItems?.forEach {
            when (it.itemType) {
                Enums.Platform.ConnectContactDetailSections.PRIMARY -> addHeaderListItem(it, listItems, getPrimaryContactInformationListItems())
                Enums.Platform.ConnectContactDetailSections.ADDITIONAL -> addHeaderListItem(it, listItems, getAdditionalContactInformationListItems())
            }
        }

        nextivaContact?.let {
            if (it.contactType != Enums.Contacts.ContactTypes.LOCAL && it.contactType != Enums.Contacts.ContactTypes.CONNECT_UNKNOWN && it.contactType != Enums.Contacts.ContactTypes.CONNECT_TEAM && it.contactType != Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW) {
                listItems.add(ConnectContactDetailFooterListItem(it))
            }
        }

        baseListItemsLiveData.postValue(listItems)
    }

    private fun addHeaderListItem(headerListItem: ConnectContactDetailHeaderListItem,
                                  baseListItemsList: ArrayList<BaseListItem>,
                                  childrenListItemsList: ArrayList<BaseListItem>) {
        if (childrenListItemsList.isNotEmpty()) {
            headerListItem.baseListItemsList?.clear()

            headerListItem.baseListItemsList?.addAll(childrenListItemsList)
            baseListItemsList.add(headerListItem)

            if (headerListItem.isExpanded) {
                baseListItemsList.addAll(childrenListItemsList)
            }
        }
    }

    private fun getPrimaryContactInformationListItems(): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        nextivaContact?.categorizedNumbersSorted?.let { categorizedNumbers ->
            for (category in categorizedNumbers) {
                listItems.add(
                    ConnectContactCategoryListItem(
                        title = ConnectPhoneType.fromIntType(category.value[0].type).labelIdentifier,
                        textStyle = R.style.DS_Caption1Heavy,
                        textColor = R.color.connectGrey09,
                        topPadding = R.dimen.general_padding_medium,
                        iconId = R.string.fa_phone_alt,
                        iconType = Enums.FontAwesomeIconType.REGULAR
                    )
                )

                for (phoneNumber in category.value) {
                    val number = phoneNumber.number?.let {
                        CallUtil.getNumberWithExtensionFormatted(
                            it
                        )
                    }

                    listItems.add(
                        ConnectContactCategoryListItem(
                            title = number,
                            data = phoneNumber,
                            isBlocked = phoneNumber.strippedNumber?.let { isNumberBlocked(it) } ?: false,
                            isPhoneOrExtension = true,
                            textStyle = R.style.DS_Body1,
                            textColor = R.color.connectPrimaryBlue,
                            clipboardTag = nextivaApplication.getString(
                                R.string.general_copied_to_clipboard_label,
                                nextivaContact?.uiName,
                                phoneNumber.label
                            ),
                            iconId = R.string.fa_phone_alt
                        )
                    )
                }
            }
        }

        nextivaContact?.categorizedEmailAddresses?.let { categorizedEmailAddress ->
            for (category in categorizedEmailAddress) {
                listItems.add(
                    ConnectContactCategoryListItem(
                        title = ConnectEmailType.fromIntType(category.value[0].type).labelIdentifier,
                        textStyle = R.style.DS_Caption1Heavy,
                        textColor = R.color.connectGrey09,
                        topPadding = R.dimen.general_padding_medium,
                        iconId = R.string.fa_envelope,
                        iconType = Enums.FontAwesomeIconType.REGULAR
                    )
                )

                for (emailAddress in category.value) {
                    emailAddress.address?.let { address ->

                        listItems.add(
                            ConnectContactCategoryListItem(
                                title = address,
                                data = emailAddress,
                                textStyle = R.style.DS_Body1,
                                textColor = R.color.connectPrimaryBlue,
                                clipboardTag = nextivaApplication.getString(
                                    R.string.general_copied_to_clipboard_label,
                                    nextivaContact?.uiName,
                                    address
                                ),
                                iconId = R.string.fa_envelope
                            )
                        )
                    }
                }
            }
        }

        return listItems
    }

    fun isTeamSmsEnabled(contact: NextivaContact): Boolean {
        var matchedTeam: SmsTeam? = null

        allTeams.forEach { team ->
            contact.allPhoneNumbers?.firstOrNull { CallUtil.arePhoneNumbersEqual(it.strippedNumber, team.teamPhoneNumber) }?.let { matchedTeam = team }
        }

        return matchedTeam?.smsEnabled ?: false
    }

    private fun getAdditionalContactInformationListItems(): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        if (nextivaContact?.contactType == Enums.Contacts.ContactTypes.CONNECT_USER) {
            room?.let { room ->
                val currentUser = sessionManager.userInfo?.comNextivaUseruuid
                val isMember = room.members?.firstOrNull { it.userUuid == currentUser } != null
                val isMyRoom = room.type == RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value

                listItems.add(ConnectContactDetailListItem(
                    nextivaContact?.uiName,
                    nextivaApplication.getString(R.string.connect_contact_details_room_title),
                    room.name,
                    isClickable = false,
                    iconId = R.string.fa_door_open,
                    iconType = Enums.FontAwesomeIconType.REGULAR,
                    actionType = Enums.Platform.ConnectContactDetailClickAction.NONE))

                if (isMember || room.locked == false) {
                    listItems.add(ConnectContactDetailListItem(
                        room.name,
                        nextivaApplication.getString(
                            if (isMyRoom)
                                R.string.connect_contact_details_my_room_details
                            else
                                R.string.connect_contact_details_room_details),
                        null,
                        isClickable = true,
                        iconId = R.string.fa_list_ul,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.ROOM_DETAILS))

                    listItems.add(ConnectContactDetailListItem(
                        room.name,
                        nextivaApplication.getString(
                            if (isMyRoom)
                                R.string.connect_contact_details_my_room_conversation
                            else if (isMember)
                                R.string.connect_contact_details_room_conversation
                            else
                                R.string.connect_contact_details_join_room),
                        null,
                        isClickable = true,
                        iconId = R.string.fa_comments_alt,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.ROOM_CONVERSATION))
                }
            }
        } else {
            nextivaContact?.company?.let { company ->
                listItems.add(ConnectContactDetailListItem(
                        nextivaContact?.uiName,
                        nextivaApplication.getString(R.string.connect_contact_details_company_title),
                        company,
                        isClickable = false,
                        iconId = R.string.fa_building,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.NONE))
            }

            nextivaContact?.title?.let { jobTitle ->
                listItems.add(ConnectContactDetailListItem(
                        nextivaContact?.uiName,
                        nextivaApplication.getString(R.string.connect_contact_details_job_title_title),
                        jobTitle,
                        isClickable = false,
                        iconId = R.string.fa_user,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.NONE))
            }

            nextivaContact?.department?.let { department ->
                listItems.add(ConnectContactDetailListItem(
                        nextivaContact?.uiName,
                        nextivaApplication.getString(R.string.connect_contact_details_department_title),
                        department,
                        isClickable = false,
                        iconId = R.string.fa_suitcase,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.NONE))
            }

            nextivaContact?.dates?.let { dates ->
                for (date in dates) {
                    date.date?.let { dateValue ->
                        var formattedDate = dateValue

                        try {
                            var format = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())
                            val formatted = format.parse(dateValue)
                            format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            formatted?.let { formattedDate = format.format(it) }

                        } catch (e: Exception) {

                        }

                        if (dateValue.isNotBlank()) {
                            listItems.add(ConnectContactDetailListItem(
                                    nextivaContact?.uiName,
                                    StringUtil.getConnectDateLabel(nextivaApplication, date.type
                                            ?: Enums.Contacts.DateType.OTHER),
                                    formattedDate,
                                    isClickable = false,
                                    iconId = R.string.fa_calendar,
                                    iconType = Enums.FontAwesomeIconType.REGULAR,
                                    actionType = Enums.Platform.ConnectContactDetailClickAction.NONE))
                        }
                    }
                }
            }

            nextivaContact?.website?.let { website ->
                listItems.add(ConnectContactDetailListItem(
                        nextivaContact?.uiName,
                        nextivaApplication.getString(R.string.connect_contact_details_website_title),
                        website,
                        isClickable = true,
                        iconId = R.string.fa_link,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.LINK))
            }

            nextivaContact?.socialMediaAccounts?.let { socialMediaAccounts ->
                for (account in socialMediaAccounts) {
                    account.link?.let { link ->
                        if (link.isNotBlank()) {
                            listItems.add(ConnectContactDetailListItem(
                                    nextivaContact?.uiName,
                                    StringUtil.getConnectSocialMediaLabel(nextivaApplication, account.type
                                            ?: Enums.Contacts.SocialMediaType.OTHER),
                                    link,
                                    isClickable = true,
                                    iconId = account.getIconId(),
                                    iconType = account.getIconType(),
                                    actionType = Enums.Platform.ConnectContactDetailClickAction.LINK))
                        }
                    }
                }
            }

            nextivaContact?.addresses?.let { addresses ->
                for (address in addresses) {
                    address.type?.let { type ->
                        listItems.add(ConnectContactDetailListItem(
                                nextivaContact?.uiName,
                                StringUtil.getConnectAddressLabel(nextivaApplication, type),
                                address.getConnectDetailSubtitle(),
                                isClickable = true,
                                maxSubtitleLines = if (address.addressLineTwo.isNullOrEmpty()) 3 else 4,
                                iconId = R.string.fa_map_marker_alt,
                                iconType = Enums.FontAwesomeIconType.REGULAR,
                                actionType = Enums.Platform.ConnectContactDetailClickAction.ADDRESS))
                    }
                }
            }

            nextivaContact?.description?.let { description ->
                listItems.add(ConnectContactDetailListItem(
                        nextivaContact?.uiName,
                        nextivaApplication.getString(R.string.connect_contact_details_description_title),
                        description,
                        isClickable = false,
                        maxSubtitleLines = Int.MAX_VALUE,
                        iconId = R.string.fa_file_alt,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.NONE))
            }
        }

        return listItems
    }

    fun onShowMore() {
        headerListItems?.firstOrNull()?.isShowingMore = headerListItems?.firstOrNull()?.isShowingMore == false
        getDetailListItems()
    }

    fun getBaseListItemsLiveData(): LiveData<ArrayList<BaseListItem>> {
        return baseListItemsLiveData
    }

    fun toggleFavorite(onSuccessCallback: ((Boolean) -> Unit)) {
        nextivaContact?.userId?.let { contactId ->
            nextivaContact?.isFavorite?.let { isFavorite ->
                platformContactsRepository.setContactFavorite(contactId,
                        if (nextivaContact?.contactType == Enums.Contacts.ContactTypes.CONNECT_USER)
                            Enums.Contacts.ContactTypesValue.CONNECT_USER else
                            Enums.Contacts.ContactTypesValue.CONNECT_SHARED,
                        !isFavorite)
                        .subscribe { success ->
                            if (success) {
                                nextivaContact?.setIsFavorite(nextivaContact?.isFavorite == false)
                            }

                            onSuccessCallback(success)
                        }
            }
        }
    }

    fun resetContactLiveData() {
        contactLiveData = MediatorLiveData()
    }

    fun setContact(contact: NextivaContact) {
        if (contact.contactType != Enums.Contacts.ContactTypes.CONNECT_UNKNOWN) {
            contactLiveData.addSource(dbManager.getContactLiveData(contact.userId)) {
                contactLiveData.value = it
            }

        } else {
            contactLiveData.value = contact
        }

        // userId can be null for unknown not saved numbers
        contact.userId?.let {
            if (sessionManager.isTeamchatEnabled(nextivaApplication)) {
                val currentUser = sessionManager.userInfo?.comNextivaUseruuid
                if (contact.userId == currentUser) {
                    platformRoomsRepository.fetchMyRoom(true, compositeDisposable) { room ->
                        this.room = room
                        getDetailListItems()
                    }
                } else {
                    platformRoomsRepository.fetchContactRoom(contact.userId, compositeDisposable) { room ->
                        this.room = room
                        getDetailListItems()
                    }
                }
            }
        }
    }

    fun joinRoom() {
        val roomId = room?.id ?: return
        val currentUser = sessionManager.userInfo?.comNextivaUseruuid
        val isMember = room?.members?.firstOrNull { it.userUuid == currentUser } != null
        if (!isMember) {
            viewModelScope.launch(Dispatchers.IO) {
                dbManager.getConnectContactFromUuidInThread(currentUser)?.value?.let { currentUserNextivaContact ->
                    mCompositeDisposable.add(
                        platformRoomsRepository.addRoomMembers(roomId, listOf(currentUserNextivaContact))
                            .subscribe { sendChatMessageResponse ->
                                val roomsList = sendChatMessageResponse.roomsList
                                if (roomsList != null) {
                                    roomsDbManager.saveRooms(roomsList)
                                        .observeOn(schedulerProvider.ui())
                                        .subscribe(object : DisposableCompletableObserver() {
                                            override fun onComplete() {}
                                            override fun onError(e: Throwable) {}
                                        })
                                }
                            }
                    )
                }
            }
        }
    }

    fun isContactImported(): Boolean {
        nextivaContact?.lookupKey?.let { lookUpKey ->
            if (lookUpKey.isNotEmpty()) {
                if (dbManager.doesLocalContactWithLookupKeyExist(lookUpKey)) {
                    return true
                }
            }
        }

        return false
    }

    fun isBlockingFeatureEnabled(): Boolean {
        return sessionManager.isBlockNumberForCallingEnabled
    }

    fun isShowSms(): Boolean {
        return sessionManager.isShowSms
    }

    fun isEnableSmsButtons(): Boolean {
        return sessionManager.isSmsEnabled
    }

    fun getPhoneNumberListItems(phoneNumbers: List<PhoneNumber>?): ArrayList<DialogContactActionDetailListItem> {
        val listItems: ArrayList<DialogContactActionDetailListItem> = ArrayList()

        phoneNumbers?.forEach { phoneNumber ->
            val number = if (PhoneNumberUtils.formatNumber(phoneNumber.number, Locale.getDefault().country).isNullOrEmpty()) {
                phoneNumber.number
            } else {
                PhoneNumberUtils.formatNumber(phoneNumber.number, Locale.getDefault().country)
            }

            listItems.add(
                DialogContactActionDetailListItem(
                    StringUtil.getPhoneNumberTypeLabel(
                        nextivaApplication,
                        phoneNumber,
                        false,
                        true
                    ),
                    number ?: "",
                    null,
                    data = phoneNumber
                )
            )
        }

        return listItems
    }

    fun processCallInfo(
        activity: Activity,
        @ScreenName.Screen analyticsScreenName: String,
        numberToCall: String?,
        @Enums.Sip.CallTypes.Type callType: Int,
        metadata: String? = null,
        processCallInfoCallBack: CallManager.ProcessParticipantInfoCallBack
    ) {
        val participantInfo = ParticipantInfo(
            callType = callType,
            displayName = nextivaContact?.uiName,
            contactId = nextivaContact?.userId,
            numberToCall = numberToCall!!,
            metadata = metadata
        )

        callManager.processParticipantInfo(activity, analyticsScreenName, participantInfo, mCompositeDisposable, processCallInfoCallBack)
    }

    fun makeCall(activity: Activity, @ScreenName.Screen analyticsScreenName: String, participantInfo: ParticipantInfo) {
        callManager.makeCall(activity, analyticsScreenName, participantInfo, mCompositeDisposable)
    }

    fun getSmsIntent(number: String): Intent {
        var groupValue: String? = CallUtil.getFormattedNumber(CallUtil.getStrippedPhoneNumber(number))
        var ourNumber = ""

        sessionManager.userDetails?.telephoneNumber?.let { telephoneNumber ->
            ourNumber = CallUtil.getCountryCode() + CallUtil.getStrippedPhoneNumber(telephoneNumber)
            groupValue = "$groupValue,$ourNumber"
        }

        val cleanedNumber = if (CallUtil.isCountryCodeAdded(CallUtil.cleanPhoneNumber(number))) {
            CallUtil.cleanPhoneNumber(number)
        } else {
            CallUtil.getCountryCode() + CallUtil.cleanPhoneNumber(number)
        }

        val participant = SmsParticipant(contactLiveData.value?.uiName, null, cleanedNumber, contactLiveData.value?.userId, null, null)
        val conversationDetails = SmsConversationDetails(getSortedGroupValue(groupValue ?: ""), listOf(participant), ourNumber, sessionManager.currentUser?.userUuid ?: "")
        conversationDetails.groupId = groupId.value?.peekContent()?.second

        allTeams.firstOrNull { CallUtil.arePhoneNumbersEqual(it.teamPhoneNumber, number) }?.let { team ->
            conversationDetails.teams = listOf(team)
            conversationDetails.participants = null
        }

        return ConversationActivity.newIntent(nextivaApplication,
                conversationDetails,
                false,
                Enums.Chats.ConversationTypes.SMS,
                Enums.Chats.ChatScreens.CONVERSATION)
    }

    fun getChatIntent(): Intent? {
        val contact = nextivaContact ?: return null
        val userIdList = mutableListOf(contact.userId, sessionManager.userInfo?.comNextivaUseruuid.toString())
        val chatRoom = roomsDbManager.getRoomInThread(userIdList)
        val roomId = chatRoom?.roomId  ?: ""
        var title = contact.displayName ?: ""
        return RoomConversationActivity.newIntent(nextivaApplication, roomId, title, "", arrayOf(contact.userId))
    }

    private fun getSortedGroupValue(groupValue: String): String {
        val sortingGroupValueList = ArrayList(listOf(*groupValue.trim { it <= ' ' }.replace("\\s".toRegex(), "").split(",".toRegex()).toTypedArray()))
        sortingGroupValueList.sortWith { s, t1 -> s.trim { it <= ' ' }.toLong().compareTo(t1.trim { it <= ' ' }.toLong()) }
        return TextUtils.join(",", sortingGroupValueList).trim { it <= ' ' }
    }

    fun isXbertContact(): Boolean {
        return nextivaContact?.aliases?.lowercase()?.contains(Constants.Contacts.Aliases.XBERT_ALIASES) == true
    }

    fun fetchGroupId(phoneNumber: String){
        _loading.value = true
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            representingTeam(phoneNumber)?.let { team ->
                val body = GenerateGroupIdPostBody()
                body.teamIds.add(team.teamId.orEmpty())
                // Group Id calculated using API Endpoint because this chat has a Team
                smsManagementRepository.generateGroupId(body)?.let { groupId ->
                    if (isActive) _groupId.postValue(Event(Pair(phoneNumber, groupId)))
                }
            } ?: run {
                sessionManager.userDetails?.telephoneNumber?.let { number ->
                    val selfNumber = CallUtil.getStrippedNumberWithCountryCode(
                        number.split("x")[0].replace(
                            "[^0-9]".toRegex(),
                            ""
                        )
                    )
                    if(isActive) _groupId.postValue(Event(Pair(phoneNumber, getPlainGroupId(listOf(phoneNumber, selfNumber)))))
                }
            }
            _loading.postValue(false)
        }
    }

    private fun representingTeam(phoneNumber : String) : SmsTeam? {
        sessionManager.allTeams.forEach { team ->
            if (CallUtil.arePhoneNumbersEqual(
                    phoneNumber,
                    team.teamPhoneNumber
                )
            ) {
                return team
            }
        }
        return null
    }

    private fun getPlainGroupId(numbers: List<String>) : String {
        return numbers.toSet().sorted().joinToString("")
    }

    fun onAddContactPressed(index: Int) {
        _addContactPressed.value = Event(index)
    }

    fun isNumberBlocked(phoneNumber: String): Boolean {
        val number = CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber)
        return blockingNumberManager.isNumberBlocked(number)
    }

    fun clearBlockingFeatureEvent() {
        _blockingFeatureEvent.value = null
    }

    fun updateBlockingFeature(phoneNumber: String) {
        val sessionId = sessionManager.sessionId ?: return
        val userInfo = sessionManager.userInfo ?: return
        val accountNumber = userInfo.comNextivaCorpAccountNumber ?: return
        val userUUID = userInfo.comNextivaUseruuid ?: return

        val number = CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber)
        val isNumberBlocked = isNumberBlocked(number)

        blockingNumberManager.blockOrUnblockNumber(
            phoneNumber = number,
            willBlock = !isNumberBlocked,
            accountNumber = accountNumber.toString(),
            sessionId = sessionId,
            userUUID = userUUID
        ) { result: BlockingState ->
            _blockingFeatureEvent.postValue(result)
        }
    }
}
