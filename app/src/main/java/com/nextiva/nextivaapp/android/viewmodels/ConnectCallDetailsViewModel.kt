package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import android.telephony.PhoneNumberUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.Voicemail
import com.nextiva.nextivaapp.android.util.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ConnectCallDetailsViewModel @Inject constructor(
    application: Application,
    val nextivaApplication: Application,
    val dbManager: DbManager,
    val blockingNumberManager: BlockingNumberManager,
    val conversationRepository: ConversationRepository
) : BaseViewModel(application) {
    private var baseListItemsLiveData: MutableLiveData<ArrayList<BaseListItem>> = MutableLiveData()

    var callLogEntry: CallLogEntry? = null
    var voicemail: Voicemail? = null

    fun getDetailListItems() {
        baseListItemsLiveData.value = when {
            callLogEntry != null -> callLogEntry?.let {
                if (!it.isRead) {
                    it.callLogId?.let { messageId ->
                        conversationRepository.markCallRead(messageId)
                            .subscribe(object : DisposableSingleObserver<Boolean>() {
                                override fun onSuccess(success: Boolean) {
                                    if (success) {
                                        dbManager.markCallLogEntryRead(it.callLogId).subscribe()
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    LogUtil.e("Error marking call read ${e.localizedMessage}")
                                }
                            })
                    }
                }

                getCallLogListItems(it)
            }
            voicemail != null -> voicemail?.let { getVoicemailListItems(it) }
            else -> null
        }
    }

    private fun getCallLogListItems(callLogEntry: CallLogEntry): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        listItems.add(ConnectCallDetailListItem(callLogEntry))

        val number = if (PhoneNumberUtils.formatNumber(callLogEntry.phoneNumber, Locale.getDefault().country).isNullOrEmpty()) {
            callLogEntry.phoneNumber
        } else {
            PhoneNumberUtils.formatNumber(callLogEntry.phoneNumber, Locale.getDefault().country)
        }

        listItems.add(ConnectContactDetailListItem(
            uiName = callLogEntry.uiName,
            title = nextivaApplication.getString(R.string.phone_type_general),
            subtitle = number,
            isClickable = true,
            iconId = R.string.fa_phone_alt,
            isBlocked = number?.let { blockingNumberManager.isNumberBlocked(it) } ?: false,
            iconType = Enums.FontAwesomeIconType.REGULAR,
            actionType = Enums.Platform.ConnectContactDetailClickAction.PHONE))

        return listItems
    }

    private fun getVoicemailListItems(voicemail: Voicemail): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        listItems.add(ConnectCallDetailListItem(voicemail))

        val number = if (PhoneNumberUtils.formatNumber(voicemail.address, Locale.getDefault().country).isNullOrEmpty()) {
            voicemail.address
        } else {
            PhoneNumberUtils.formatNumber(voicemail.address, Locale.getDefault().country)
        }

        listItems.add(ConnectContactDetailListItem(
            uiName = voicemail.uiName,
            title = nextivaApplication.getString(R.string.phone_type_general),
            subtitle = number,
            isClickable = true,
            iconId = R.string.fa_phone_alt,
            isBlocked = number?.let { blockingNumberManager.isNumberBlocked(it) } ?: false,
            iconType = Enums.FontAwesomeIconType.REGULAR,
            actionType = Enums.Platform.ConnectContactDetailClickAction.NONE))

        return listItems
    }

    fun getBaseListItemsLiveData(): LiveData<ArrayList<BaseListItem>> {
        return baseListItemsLiveData
    }
}