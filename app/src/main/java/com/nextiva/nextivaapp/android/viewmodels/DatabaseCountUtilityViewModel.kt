package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.map
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DatabaseCountUtilityListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.util.DbConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DatabaseCountUtilityViewModel @Inject constructor(application: Application, var nextivaApplication: Application, var dbManager: DbManager) : BaseViewModel(application) {

    val baseListItemsLiveData = dbManager.tableCountsLiveData
            .map { tableCountModel ->
                val listItems: ArrayList<BaseListItem> = ArrayList()

                listItems.add(DatabaseCountUtilityListItem(R.string.fa_address_book,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_POSTAL_ADDRESSES,
                        tableCountModel.addressCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_paperclip,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_ATTACHMENTS,
                        tableCountModel.attachmentCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_custom_outbound_call,
                        Enums.FontAwesomeIconType.CUSTOM,
                        DbConstants.TABLE_NAME_CALL_LOG_ENTRIES,
                        tableCountModel.callLogCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_user,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_CONTACTS,
                        tableCountModel.contactCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_calendar,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_DATES,
                        tableCountModel.dateCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_envelope,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_EMAILS,
                        tableCountModel.emailCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_users,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_GROUPS,
                        tableCountModel.groupCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_custom_report,
                        Enums.FontAwesomeIconType.CUSTOM,
                        DbConstants.TABLE_NAME_LOGGING,
                        tableCountModel.loggingCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_custom_team_chat,
                        Enums.FontAwesomeIconType.CUSTOM,
                        DbConstants.TABLE_NAME_MESSAGES,
                        tableCountModel.smsMessageCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_bell,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_MESSAGE_STATE,
                        tableCountModel.messageStateCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_user,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_PARTICIPANT,
                        tableCountModel.participantCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_phone_alt,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_PHONES,
                        tableCountModel.phoneCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_lightbulb_on,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_PRESENCES,
                        tableCountModel.presenceCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_user,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_RECIPIENT,
                        tableCountModel.recipientCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_user,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_SENDER,
                        tableCountModel.senderCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_globe,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_SESSION,
                        tableCountModel.sessionCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_comment_dots,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_SMS_MESSAGE,
                        tableCountModel.smsMessageCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_smile,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_SOCIAL_MEDIA_ACCOUNTS,
                        tableCountModel.socialMediaAccountCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_smile,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_VCARDS,
                        tableCountModel.vCardCount ?: 0))
                listItems.add(DatabaseCountUtilityListItem(R.string.fa_voicemail,
                        Enums.FontAwesomeIconType.REGULAR,
                        DbConstants.TABLE_NAME_VOICEMAILS,
                        tableCountModel.voicemailCount ?: 0))

                listItems
            }
}