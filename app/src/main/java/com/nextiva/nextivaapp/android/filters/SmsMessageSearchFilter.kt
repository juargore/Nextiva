package com.nextiva.nextivaapp.android.filters

import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.widget.Filter
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.MessageUtil
import java.util.LinkedList
import java.util.Locale
import java.util.Queue

class SmsMessageSearchFilter(private val mFilterCallback: SmsMessageSearchFilterCallBack?, private val dbManager: DbManager, private val mSessionManager: SessionManager) : Filter() {

    private var mSmsMessagesList: List<SmsMessage?>? = null

    private val mIsFullListQueue: Queue<Boolean> = LinkedList()

    fun filter(smsMessageList: List<SmsMessage?>?, constraint: CharSequence?) {
        mSmsMessagesList = smsMessageList
        super.filter(constraint)
    }

    fun filter(constraint: CharSequence?, isFullList: Boolean) {
        super.filter(constraint)
        mIsFullListQueue.add(isFullList)
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        val filterResults = FilterResults()
        if (!TextUtils.isEmpty(constraint)) {
            filterResults.values = filterSmsMessages(constraint.toString())
        } else {
            filterResults.values = mSmsMessagesList
        }
        return filterResults
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        val isFullList = mIsFullListQueue.poll()
        mFilterCallback?.onSmsSearchFilterResults(results.values as List<SmsMessage?>?, isFullList
                ?: true)
    }

    private fun filterSmsMessages(input: String): List<SmsMessage?>? {
        return if (TextUtils.isEmpty(input)) {
            mSmsMessagesList
        } else {
            val smsListFiltered: MutableList<SmsMessage> = ArrayList()
            for (smsMessage in mSmsMessagesList!!) {
                if (filterMessagesByString(smsMessage, input.toLowerCase(Locale.ROOT), mSessionManager, dbManager)) {
                    if (smsMessage != null) {
                        smsListFiltered.add(smsMessage)
                    }
                }
            }
            smsListFiltered
        }
    }

    interface SmsMessageSearchFilterCallBack {
        fun onSmsSearchFilterResults(smsMessageList: List<SmsMessage?>?, isFullList: Boolean)
    }


    private fun filterMessagesByString(message: SmsMessage?, input: String?, mSessionManager: SessionManager, dbManager: DbManager): Boolean {
        if (message == null) {
            return false
        }

        var participantNumbers: List<String>? = message.groupValue?.split(",")?.map { it.trim() }
        val phoneNumber = "1" + mSessionManager.userDetails?.telephoneNumber
        participantNumbers = participantNumbers?.filter { it != phoneNumber }
        val dbParticipantsList: List<SmsParticipant>?
        dbParticipantsList = MessageUtil.getDisplayNameStringList(participantNumbers, message)
        var displayNameString = getUiNameList(dbParticipantsList, mSessionManager, dbManager);

        if (message.sender != null && input != null) {
            for ((_, name, _, phoneNumber) in message.sender!!) {
                if (phoneNumber != null && phoneNumber.contains(input) || name != null && name.contains(input)) {
                    return true
                }
            }
        }
        if (message.recipientParticipantsList != null && input != null) {
            for ((_, name, _, phoneNumber) in message.recipientParticipantsList!!) {
                if (phoneNumber != null && phoneNumber.contains(input) || name != null && name.contains(input)) {
                    return true
                }
            }
        }
        if (displayNameString != null && input != null) {
            for (name in displayNameString) {
                if (name.contains(input)) {
                    return true;
                }
            }
        }
        return false
    }

    fun getUiNameList(dbParticipantsList: List<SmsParticipant>?, mSessionManager: SessionManager, dbManager: DbManager): ArrayList<String>? {
        var uiNamesList: ArrayList<String>? = ArrayList()
        if (dbParticipantsList != null) {
            for (participant in dbParticipantsList) {
                if (!TextUtils.isEmpty(participant.name)) {
                    participant.name?.let { uiNamesList?.add(it) }
                } else if (!TextUtils.equals(participant.phoneNumber?.let { CallUtil.getStrippedPhoneNumber(it) },
                                CallUtil.getStrippedPhoneNumber(mSessionManager.userDetails?.telephoneNumber
                                        ?: ""))) {
                    uiNamesList?.add(dbManager.getUiNameFromPhoneNumber(participant.phoneNumber?.let { CallUtil.getStrippedPhoneNumber(it) })
                            ?: CallUtil.phoneNumberFormatNumberDefaultCountry(participant.phoneNumber))
                } else {

                    uiNamesList?.add(PhoneNumberUtils.formatNumber(participant.phoneNumber))
                }
            }
            return uiNamesList
        }
        return uiNamesList

    }

}