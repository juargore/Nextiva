package com.nextiva.nextivaapp.android.features.messaging.helpers

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.text.TextPaint
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.StringUtil
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.util.extensions.orZero
import java.util.Locale
import javax.inject.Inject

class SmsTitleHelper @Inject constructor(
    val dbManager: DbManager,
    val sessionManager: SessionManager
) {

    fun getSMSConversationParticipantInfo(
        conversationDetails: SmsConversationDetails,
        width: Int,
        paint: TextPaint?,
        context: Context
    ): SmsTitleInfo {
        val allParticipantNames: ArrayList<String> = ArrayList()
        var avatarInfo = AvatarInfo.Builder()
            .setFontAwesomeIconResId(R.string.fa_user)
            .isConnect(true).build()

        conversationDetails.allSavedTeams = sessionManager.allTeams
        conversationDetails.userTeams = sessionManager.usersTeams

        val participantsList = conversationDetails.getParticipantsList()
        val teams = conversationDetails.getAllTeams()

        participantsList?.filter {
            // Filter any duplicated number from a participants with a TeamChat:Number
            !teams.any { team ->
                CallUtil.arePhoneNumbersEqual(team.teamPhoneNumber, it.phoneNumber)
            }
        }?.forEach { participant ->

            // Use same logic to get participant's name whether is team conversation
            // or OneToOne conversation, otherwise we could show different
            // names for same number

            val nameToAdd = participant.contact?.uiName?.nullIfEmpty() ?: participant.uiName?.nullIfEmpty() ?: participant.name?.nullIfEmpty() ?: participant.phoneNumber?.let { number ->

                // Extract all participants name, using its number to search in contact DB,
                // order of preference (there may be multiple contacts with same number somehow):
                // 1) connect User contact
                // 2) connect Personal contact
                // 3) Nextiva Team
                // 4) Nextiva Shared
                // 5) call centre
                // 6) call flow
                // 7) local device

                val contact = dbManager.getConnectContactFromPhoneNumberInThread(number).value

                val name = contact?.uiName ?: contact?.displayName
                participant.contact = contact

                if (!CallUtil.arePhoneNumbersEqual(number, sessionManager.userDetails?.telephoneNumber ?: "")) {
                    (name ?: (PhoneNumberUtils.formatNumber(number, Locale.getDefault().country) ?: number)).nullIfEmpty()
                } else
                    null
            }

            // Add name to participant's name list
            nameToAdd?.nullIfEmpty()?.let { allParticipantNames.add(it) }
        }

        val type = when (participantsList?.size.orZero() + teams.size.orZero()) {
            Enums.SMSMessages.ConversationTypes.SELF_MESSAGE_PARTICIPANT_COUNT -> SMSType.Self
            Enums.SMSMessages.ConversationTypes.MESSAGE_CONVERSATION_SINGLE_PARTICIPANT_COUNT -> SMSType.OneToOne
            else -> SMSType.Multiple
        }

        val title = when (type) {
            SMSType.Self -> {
                avatarInfo.displayName = sessionManager.userDetails?.fullName
                sessionManager.userDetails?.fullName.orEmpty()
            }

            SMSType.OneToOne -> {
                // Single Participant is a person
                participantsList?.firstOrNull()?.let {
                    avatarInfo.presence = it.presence
                    //maybe we can change the code below when we validate for duplicate phone numbers
                    it.phoneNumber?.let { phoneNumber->
                        val dbName = dbManager.getConnectContactFromPhoneNumberInThread(phoneNumber).value?.uiName
                        (dbName ?: it.name ?: it.contact?.uiName ?: it.uiName)?.let { avatarDisplayName ->
                            avatarInfo.displayName = avatarDisplayName
                        }
                        dbName?.nullIfEmpty() ?: it.name?.nullIfEmpty() ?: it.contact?.uiName?.nullIfEmpty() ?: it.uiName?.nullIfEmpty() ?: CallUtil.phoneNumberFormatNumberDefaultCountry(it.phoneNumber)
                    }
                } ?: teams.firstOrNull()?.let { team ->
                    // Single participant is a Team
                    avatarInfo = team.avatarInfo
                    team.uiName
                } ?: "(Unknown)"
            }

            SMSType.Multiple -> {
                avatarInfo.setCounter(participantsList?.size.orZero() + teams.size.orZero())
                StringUtil.getGroupChatParticipantsString(
                    allParticipantNames,
                    teams,
                    width,
                    paint,
                    context
                )
            }
        }

        return SmsTitleInfo(
            avatarInfo = avatarInfo,
            allParticipantsName = allParticipantNames,
            type = type,
            smsTitleName = title
        )
    }
}

enum class SMSType {
    Self,           // Self messages
    OneToOne,       // Direct SMS with one member
    Multiple       // Multiple contacts
}


data class SmsTitleInfo(
    val avatarInfo: AvatarInfo,
    val allParticipantsName: List<String>,
    val type: SMSType,
    val smsTitleName: String
)