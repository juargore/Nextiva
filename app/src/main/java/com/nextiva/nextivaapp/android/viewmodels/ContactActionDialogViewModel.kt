package com.nextiva.nextivaapp.android.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationActivity
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.models.net.platform.GenerateGroupIdPostBody
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.CallUtil.isExtensionNumber
import com.nextiva.nextivaapp.android.util.StringUtil
import com.nextiva.nextivaapp.android.util.extensions.default
import com.nextiva.nextivaapp.android.util.extensions.extractDtfmTone
import com.nextiva.nextivaapp.android.util.extensions.extractFirstNumber
import com.nextiva.nextivaapp.android.util.extensions.notNull
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.removeWhitespaces
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ContactActionDialogViewModel @Inject constructor(
    application: Application,
    val nextivaApplication: Application,
    val sessionManager: SessionManager,
    val dbManager: DbManager,
    val callManager: CallManager,
    val blockingNumberManager: BlockingNumberManager,
    val smsManagementRepository: SmsManagementRepository
) : BaseViewModel(application) {

    private val makeACallListItem: BaseListItem = DialogContactActionListItem(R.string.fa_phone_alt,
            nextivaApplication.getString(R.string.connect_contact_long_click_dialog_call),
            Enums.Platform.ConnectContactLongClickAction.MAKE_A_CALL,
            isEnabled = false,
            isExpandable = false,
            isExpanded = false)
    private val dialExtensionListItem: BaseListItem = DialogContactActionListItem(R.string.fa_phone_alt,
        nextivaApplication.getString(R.string.connect_contact_long_click_dialog_extension),
        Enums.Platform.ConnectContactLongClickAction.DIAL_EXTENSION,
        isEnabled = false,
        isExpandable = false,
        isExpanded = false)
    private val sendATextListItem: BaseListItem = DialogContactActionListItem(R.string.fa_comment_dots,
            nextivaApplication.getString(R.string.connect_contact_long_click_dialog_text),
            Enums.Platform.ConnectContactLongClickAction.SEND_A_TEXT,
            isEnabled = false,
            isExpandable = false,
            isExpanded = false)
    private val copyToClipboardListItem: BaseListItem = DialogContactActionListItem(R.string.fa_copy,
            nextivaApplication.getString(R.string.connect_contact_long_click_dialog_copy),
            Enums.Platform.ConnectContactLongClickAction.COPY_TO_CLIPBOARD,
            isEnabled = false,
            isExpandable = false,
            isExpanded = false)
    private val blockNumberListItem: BaseListItem = DialogContactActionListItem(R.string.fa_ban,
        nextivaApplication.getString(R.string.connect_contact_long_click_block_number),
        Enums.Platform.ConnectContactLongClickAction.BLOCK_NUMBER,
        isEnabled = false,
        isExpandable = false,
        isExpanded = false)

    private var baseListItemsLiveData: MutableLiveData<ArrayList<BaseListItem>> = MutableLiveData()

    var nextivaContact: NextivaContact? = null
    var phoneId: Long? = null
    var isImporting = false
    var fromLongPress = false
    private var selectedPhoneNumber: PhoneNumber? = null

    private val _groupId : MutableLiveData<Pair<String, String>> = MutableLiveData()
    val groupId : LiveData<Pair<String,String>> = _groupId
    private val _loading : MutableLiveData<Boolean> = MutableLiveData(false)
    val loading : LiveData<Boolean> = _loading

    private var allTeams = sessionManager.allTeams

    fun refreshListItems() {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        var isContactImported = false

        viewModelScope.launch(context = Dispatchers.IO) {

            nextivaContact?.lookupKey?.let {
                isContactImported = dbManager.doesLocalContactWithLookupKeyExist(it)
            }

            nextivaContact?.let { nextivaContact ->
                var phoneTypeInfo = nextivaContact.allPhoneNumbers?.firstOrNull { phoneNumber ->
                    phoneNumber.id == phoneId.default(0)
                }?.let { phoneNumber ->
                    phoneNumber.strippedNumber?.let {
                        selectedPhoneNumber = phoneNumber
                        val typeInfo = getNumberTypeInfo(phoneNumber.type)
                        DialogContactActionHeaderListItem.PhoneTypeInfo(
                            phoneNumber = CallUtil.getNumberWithExtensionFormatted(
                                selectedPhoneNumber?.number.orEmpty()
                            ),
                            numberType = typeInfo.first,
                            color = typeInfo.second
                        )
                    }
                }


                if (phoneTypeInfo == null && selectedPhoneNumber == null) {
                    phoneTypeInfo = getPhoneTypeInfoForUnsavedNumber(nextivaContact)
                }

                listItems.add(
                    DialogContactActionHeaderListItem(
                        contact = nextivaContact,
                        phoneTypeInfo = phoneTypeInfo,
                        showImport = isImporting,
                        isImported = isContactImported
                    )
                )
            }

            val isCallFlow =
                nextivaContact?.contactType == Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW

            if (selectedPhoneNumber != null) {
                (makeACallListItem as? DialogContactActionListItem)?.isEnabled = true
                (makeACallListItem as? DialogContactActionListItem)?.data = selectedPhoneNumber
                (copyToClipboardListItem as? DialogContactActionListItem)?.isEnabled = true

                val smsPhoneNumbers =
                    nextivaContact?.allPhoneNumbers?.filter { CallUtil.isValidSMSNumber(it.strippedNumber) }
                val isEnabled = smsPhoneNumbers?.isNotEmpty() == true && sessionManager.isShowSms
                (sendATextListItem as? DialogContactActionListItem)?.isEnabled =
                    !selectedNumberIsExtension() && isEnabled && !isCallFlow

                listItems.add(makeACallListItem)

                selectedPhoneNumber?.extension?.nullIfEmpty()?.let {
                    (dialExtensionListItem as? DialogContactActionListItem)?.isEnabled = true
                    (dialExtensionListItem as? DialogContactActionListItem)?.data = selectedPhoneNumber
                    listItems.add(dialExtensionListItem)
                }

                if (!isCallFlow) {
                    listItems.add(sendATextListItem)
                    listItems.add(copyToClipboardListItem)
                }
                
                if (sessionManager.isBlockNumberForCallingEnabled && !isExtensionNumber(selectedPhoneNumber?.strippedNumber) && !fromLongPress) {
                    (blockNumberListItem as? DialogContactActionListItem)?.let { item ->
                        selectedPhoneNumber?.strippedNumber?.takeIf { isNumberBlocked(it) }?.let {
                            item.title = nextivaApplication.getString(R.string.connect_call_details_unblock_number)
                        }
                        item.isEnabled = true
                        listItems.add(item)
                    }
                }
            } else {
                nextivaContact?.allPhoneNumbers?.filter { it.type != Enums.Contacts.PhoneTypes.FAX }
                    ?.let { phoneNumbers ->
                        (makeACallListItem as? DialogContactActionListItem)?.isExpandable =
                            phoneNumbers.size > 1 || phoneNumbers.firstOrNull()?.let {
                                it.number?.length == 4 || it.strippedNumber?.length == 4
                            } == true
                        (makeACallListItem as? DialogContactActionListItem)?.isEnabled =
                            phoneNumbers.isNotEmpty()
                        (sendATextListItem as? DialogContactActionListItem)?.isExpandable =
                            phoneNumbers.size > 1 && (sessionManager.isSmsEnabled || sessionManager.isTeamSmsEnabled)
                        (sendATextListItem as? DialogContactActionListItem)?.isEnabled =
                            phoneNumbers.isNotEmpty() && (sessionManager.isSmsEnabled || sessionManager.isTeamSmsEnabled)

                        listItems.add(makeACallListItem)

                        if ((makeACallListItem as? DialogContactActionListItem)?.isExpanded == true) {
                            listItems.addAll(
                                getPhoneNumberListItems(
                                    Enums.Platform.ConnectContactLongClickAction.MAKE_A_CALL,
                                    phoneNumbers
                                )
                            )
                        }

                        phoneNumbers.firstOrNull { it.extension?.isEmpty() == false }?.let {
                            (dialExtensionListItem as? DialogContactActionListItem)?.isEnabled = true
                            (dialExtensionListItem as? DialogContactActionListItem)?.data = it
                            listItems.add(dialExtensionListItem)
                        }

                        phoneNumbers.filter {
                            it.strippedNumber?.let { strippedNumber ->
                                strippedNumber.length == 10 || (strippedNumber.length == 11 && strippedNumber.startsWith(
                                    "1"
                                ))
                            }
                                ?: kotlin.run { false }
                        }.let { smsNumbers ->
                            (sendATextListItem as? DialogContactActionListItem)?.isExpandable =
                                smsNumbers.size > 1
                            (sendATextListItem as? DialogContactActionListItem)?.isEnabled =
                                smsNumbers.isNotEmpty() && (sessionManager.isSmsEnabled || sessionManager.isTeamSmsEnabled)

                            // Do not add sms if is a call center
                            if (sessionManager.isShowSms && !isCallFlow)
                                listItems.add(sendATextListItem)

                            if (!isCallFlow && (sessionManager.isSmsEnabled || sessionManager.isTeamSmsEnabled) && (sendATextListItem as? DialogContactActionListItem)?.isExpanded == true) {
                                listItems.addAll(
                                    getPhoneNumberListItems(
                                        Enums.Platform.ConnectContactLongClickAction.SEND_A_TEXT,
                                        phoneNumbers
                                    )
                                )
                            }

                            if (sessionManager.isBlockNumberForCallingEnabled && !isExtensionNumber(selectedPhoneNumber?.strippedNumber) && !fromLongPress) {
                                (blockNumberListItem as? DialogContactActionListItem)?.let { item ->
                                    nextivaContact?.phoneNumbers?.firstOrNull()?.number?.takeIf { isNumberBlocked(it) }?.let {
                                        item.title = nextivaApplication.getString(R.string.connect_call_details_unblock_number)
                                    }
                                    item.isEnabled = true
                                    listItems.add(item)
                                }
                            }

                        }
                    }
            }
            baseListItemsLiveData.postValue(listItems)
        }
    }

    private fun getPhoneTypeInfoForUnsavedNumber(nextivaContact: NextivaContact) =
        DialogContactActionHeaderListItem.PhoneTypeInfo(
            phoneNumber = nextivaContact.phoneNumbers?.firstOrNull()?.number.orEmpty(),
            numberType = 2131952375,
            color = 2131100558
        )

    private fun getNumberTypeInfo(type: Int) = when(type) {
        Enums.Contacts.PhoneTypes.MOBILE_PHONE -> Pair(R.string.connect_create_contact_mobile_phone_type, R.color.phoneTypeMobile)
        Enums.Contacts.PhoneTypes.WORK_PHONE -> Pair(R.string.connect_create_contact_work_phone_type, R.color.phoneTypeWork)
        Enums.Contacts.PhoneTypes.HOME_PHONE -> Pair(R.string.connect_create_contact_home_phone_type, R.color.phoneTypeHome)
        else -> Pair(R.string.connect_create_contact_other_phone_type, R.color.phoneTypeOther)
    }

    fun updatePresence(presence: DbPresence) {
        nextivaContact?.presence = presence
        refreshListItems()
    }

    private fun getPhoneNumberListItems(action: Int, phoneNumbers: List<PhoneNumber>): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        phoneNumbers.forEach { phoneNumber ->
            val number = if (PhoneNumberUtils.formatNumber(phoneNumber.number, Locale.getDefault().country).isNullOrEmpty()) {
                phoneNumber.number
            } else {
                PhoneNumberUtils.formatNumber(phoneNumber.number, Locale.getDefault().country)
            }

            listItems.add(
                DialogContactActionDetailListItem(
                    action = action,
                    subtitle = number ?: "",
                    title = StringUtil.getPhoneNumberTypeLabel(
                        context = nextivaApplication,
                        phoneNumber = phoneNumber,
                        isRosterContact = false,
                        isConnect = true
                    )
                )
            )
        }

        return listItems
    }

    fun getValueForAction(action: Int): String = when (action) {
        Enums.Platform.ConnectContactLongClickAction.MAKE_A_CALL,
        Enums.Platform.ConnectContactLongClickAction.SEND_A_TEXT,
        Enums.Platform.ConnectContactLongClickAction.BLOCK_NUMBER,
        Enums.Platform.ConnectContactLongClickAction.COPY_TO_CLIPBOARD ->
            selectedPhoneNumber?.number ?: nextivaContact?.allPhoneNumbers?.firstOrNull()?.number ?: nextivaContact?.phoneNumbers?.firstOrNull()?.number
        else -> ""
    } ?: ""

    fun selectedNumberIsExtension() = selectedPhoneNumber?.type == Enums.Contacts.PhoneTypes.WORK_EXTENSION

    fun getSmsIntent(number: String): Intent {
        val numberCleaned = number.replace("+", "")
        var groupValue: String? = CallUtil.getFormattedNumber(CallUtil.getStrippedPhoneNumber(numberCleaned))
        var ourNumber = ""

        sessionManager.userDetails?.let { userDetails ->
            userDetails.telephoneNumber?.let { telephoneNumber ->
                ourNumber = CallUtil.getCountryCode() + CallUtil.getStrippedPhoneNumber(telephoneNumber)
                groupValue = "$groupValue,$ourNumber"
            }
        }

        val sortedGroupValue = getSortedGroupValue(groupValue ?: "")
        val conversationDetails = SmsConversationDetails(sortedGroupValue, listOf(
            SmsParticipant(CallUtil.getFormattedNumber(CallUtil.getStrippedPhoneNumber(numberCleaned)).removeWhitespaces(), nextivaContact?.userId)
        ), ourNumber, sessionManager.currentUser?.userUuid ?: "")
        conversationDetails.groupId = sortedGroupValue.replace(",", "")

        allTeams.firstOrNull { CallUtil.arePhoneNumbersEqual(it.teamPhoneNumber, numberCleaned) }?.let { team ->
            conversationDetails.teams = listOf(team)
            conversationDetails.participants = null
        }

        return ConversationActivity.newIntent(nextivaApplication,
                conversationDetails,
                false,
                Enums.Chats.ConversationTypes.SMS,
                Enums.Chats.ChatScreens.CONVERSATION)

    }

    fun placeCall(
        activity: Activity,
        @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
        strippedNumber: String?,
        number: String?
    ) {
        number?.let {
            mCompositeDisposable.add(
                    dbManager.getConnectContactFromPhoneNumber(strippedNumber)
                            .onErrorReturn { DbResponse(NextivaContact("")) }
                            .subscribe { dbResponse: DbResponse<NextivaContact?>? ->
                                val participantInfo = ParticipantInfo(
                                    numberToCall = number.extractFirstNumber(),
                                    dialingServiceType = Enums.Service.DialingServiceTypes.NONE,
                                    metadata = number.extractDtfmTone()
                                )

                                if (dbResponse != null && dbResponse.value != null) {
                                    participantInfo.displayName = dbResponse.value?.displayName
                                    participantInfo.contactId = dbResponse.value?.userId
                                }

                                callManager.makeCall(
                                        activity,
                                        analyticsScreenName,
                                        participantInfo,
                                        mCompositeDisposable
                                )
                            })
        }
    }

    fun isTeamSmsEnabled(contact: NextivaContact): Boolean {
        var matchedTeam: SmsTeam? = null

        allTeams.forEach { team ->
            contact.allPhoneNumbers?.firstOrNull { CallUtil.arePhoneNumbersEqual(it.strippedNumber, team.teamPhoneNumber) }?.let { matchedTeam = team }
        }

        return matchedTeam?.smsEnabled ?: false
    }

    private fun getSortedGroupValue(groupValue: String): String {
        val sortingGroupValueList = ArrayList(listOf(*groupValue.trim { it <= ' ' }.replace("\\s".toRegex(), "").split(",".toRegex()).toTypedArray()))
        sortingGroupValueList.sortWith { s, t1 -> s.trim { it <= ' ' }.toLong().compareTo(t1.trim { it <= ' ' }.toLong()) }
        return TextUtils.join(",", sortingGroupValueList).trim { it <= ' ' }
    }


    fun getBaseListItemsLiveData(): LiveData<ArrayList<BaseListItem>> {
        return baseListItemsLiveData
    }

    fun fetchGroupId(phoneNumber: String){
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val teamId = sessionManager.allTeams.firstOrNull { team ->
                team.teamPhoneNumber?.equals(phoneNumber).orFalse()
            }?.teamId

            if(teamId.notNull()) {
                // Group Id calculated using API Endpoint because this chat has a Team
                val body = GenerateGroupIdPostBody()
                body.teamIds.add(teamId.orEmpty())
                smsManagementRepository.generateGroupId(body)?.let { groupId ->
                    _groupId.postValue(Pair(phoneNumber, groupId))
                }
            } else {
                sessionManager.userDetails?.telephoneNumber?.let { number ->
                    val selfNumber = CallUtil.getStrippedNumberWithCountryCode(
                        number.split("x")[0].replace(
                            "[^0-9]".toRegex(),
                            ""
                        )
                    )
                    val contactsNumber = CallUtil.getStrippedNumberWithCountryCode(
                        phoneNumber.split("x")[0].replace(
                            "[^0-9]".toRegex(),
                            ""
                        )
                    )
                    _groupId.postValue(
                        Pair(contactsNumber, getPlainGroupId(listOf(contactsNumber, selfNumber)))
                    )
                }
            }
            _loading.postValue(false)
        }
    }

    private fun isNumberBlocked(phoneNumber: String): Boolean {
        val number = CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber)
        return blockingNumberManager.isNumberBlocked(number)
    }

    private fun getPlainGroupId(numbers: List<String>) : String {
        return numbers.toSet().sorted().joinToString("")
    }
}
