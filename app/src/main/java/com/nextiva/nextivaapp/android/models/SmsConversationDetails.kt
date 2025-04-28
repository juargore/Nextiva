package com.nextiva.nextivaapp.android.models

import android.text.TextUtils
import com.google.common.annotations.VisibleForTesting
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.ConversationViewModel
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.util.extensions.orZero
import java.io.Serializable
import java.util.Collections

data class SmsConversationDetails(var groupValue: String?,
                                  var groupId: String?,
                                  var teams: List<SmsTeam>?,
                                  var teamMembers: ArrayList<SmsParticipant>?,
                                  var participants: ArrayList<SmsParticipant>?) : Serializable {

    private var ourPhoneNumber: String? = null
    var ourUuid: String? = null

    private var sendingPhoneNumber: ConversationViewModel.SendingPhoneNumber? = null

    var userTeams: List<SmsTeam>? = null
    var allSavedTeams: List<SmsTeam>? = null
    var isTeamSmsEnabled: Boolean = true

    // If isInit then we don't want the sending number to be factored into the conversation shown since
    // we want to show the existing conversation based on what conversation the user opened.  New chat
    // ignores this value since we want to show the user the conversation they will be sending the message to.
    var isInit: Boolean = true
    var isNewChat: Boolean = false
        set(value) {
            if (value) {
                val convertedTeams: ArrayList<SmsTeam> = ArrayList()
                val newParticipants: ArrayList<SmsParticipant> = ArrayList()

                participants?.forEach { participant ->
                    if (participant.representingTeam != null) {
                        participant.representingTeam?.let { team ->
                            convertedTeams.add(team)
                        }
                    } else {
                        newParticipants.add(participant)
                    }
                }

                teams?.forEach { convertedTeams.add(it) }
                teams = convertedTeams.distinct().sortedBy { it.teamId }
                participants = newParticipants
            }

            field = value
        }

    constructor(): this(null, null, null, null, null)

    constructor(groupValue: String?, ourPhoneNumber: String, ourUuid: String): this(groupValue, null, null, null, null) {
        this.groupId = groupValue?.replace(",", "")
        this.ourPhoneNumber = ourPhoneNumber
        this.ourUuid = ourUuid
    }

    constructor(groupValue: String?, recipients: List<SmsParticipant>?, ourPhoneNumber: String, ourUuid: String): this(groupValue, null, null, null, recipients?.let { ArrayList(it) }) {
        this.groupId = groupValue?.replace(",", "")
        this.ourPhoneNumber = ourPhoneNumber
        this.ourUuid = ourUuid
    }

    constructor(smsMessage: SmsMessage, ourPhoneNumber: String, ourUuid: String): this(smsMessage.groupValue,
        smsMessage.groupId,
        smsMessage.teams?.sortedBy { it.teamId },
        smsMessage.teamMembers,
        null) {

        val newParticipants: ArrayList<SmsParticipant> = ArrayList()
        smsMessage.sender?.forEach { newParticipants.add(it) }
        smsMessage.recipientParticipantsList?.forEach { newParticipants.add(it) }
        participants = newParticipants

        this.ourPhoneNumber = ourPhoneNumber
        this.ourUuid = ourUuid
    }
    // isNewChat, ourPhoneNumber, sending numbers values will remain
    fun updateFromSmsMessage(smsMessage: SmsMessage) {
        groupId = smsMessage.groupId
        groupValue = smsMessage.groupValue
        teamMembers = smsMessage.teamMembers

        val newParticipants: ArrayList<SmsParticipant> = ArrayList()
        smsMessage.sender?.forEach { newParticipants.add(it) }
        smsMessage.recipientParticipantsList?.forEach { newParticipants.add(it) }
        participants = newParticipants
    }

    fun updateSendingPhoneNumber(sendingPhoneNumber: ConversationViewModel.SendingPhoneNumber, selectedParticipants: ArrayList<SmsParticipant>?, isSwitchingConversation: Boolean = false) {
        val groupValueList = ArrayList(getParticipantsList()?.map { it.phoneNumber ?: "" } ?: ArrayList())
        this.sendingPhoneNumber = sendingPhoneNumber

        if (isSwitchingConversation) {
            isInit = false
        }

        if (sendingPhoneNumber.team != null) {
            participants?.removeIf { CallUtil.arePhoneNumbersEqual(it.phoneNumber, ourPhoneNumber) }
            groupValueList.remove(ourPhoneNumber)

        } else {
            groupValueList.add(ourPhoneNumber)
            if (selectedParticipants != null) {
                participants?.addAll(selectedParticipants)
            }
        }
        groupValue = getSortedGroupValue(groupValueList.joinToString(","))
    }

    private fun getSortedGroupValue(groupValue: String): String {
        val sortingGroupValueList = ArrayList(listOf(*groupValue.trim { it <= ' ' }.replace("\\s".toRegex(), "").split(",".toRegex()).toTypedArray()))
        sortingGroupValueList.removeIf { it.isEmpty() || it.isBlank() }
        sortingGroupValueList.sortWith { s, t1 -> s.trim { it <= ' ' }.toLong().compareTo(t1.trim { it <= ' ' }.toLong()) }
        val sortedGroupValueList = TextUtils.join(",", sortingGroupValueList)

        return if (sortedGroupValueList.isNullOrEmpty()) "" else TextUtils.join(",", sortingGroupValueList).trim { it <= ' ' }
    }

    fun removeRecipient(phoneNumber: String) {
        participants?.removeIf { !CallUtil.arePhoneNumbersEqual(phoneNumber, it.phoneNumber) }
    }

    fun getParticipantsList(ourPhoneNumber: String): List<SmsParticipant>? {
        this.ourPhoneNumber = ourPhoneNumber
        return getParticipantsList()
    }

    fun getParticipantsList(): List<SmsParticipant>? {
        if (participants.isNullOrEmpty() && getAllTeams().isEmpty() && !groupValue.isNullOrEmpty()) {
            val participantNumbers: List<String>? = groupValue?.split(",")?.map { it.trim() }
            val phoneNumber = if (CallUtil.isCountryCodeAdded(ourPhoneNumber)) ourPhoneNumber else CallUtil.getCountryCode() + ourPhoneNumber

            var filteredParticipantNumbers = participantNumbers?.filter { it != phoneNumber }
            if (filteredParticipantNumbers?.isEmpty() == true) {
                filteredParticipantNumbers = participantNumbers
            }

            return filteredParticipantNumbers?.map { SmsParticipant(it) }

        } else {
            val participantList: ArrayList<SmsParticipant> = ArrayList()
            val teamIds = getAllTeams().map { it.teamId }
            val teamLegacyIds = getAllTeams().map { it.legacyId }
            var isTeamMember: Boolean

            participants?.forEach { participant ->
                isTeamMember = false

                getAllTeams().forEach { team ->
                    if (team.members?.any { it.id == participant.userUUID || it.id == participant.contact?.userId } == true) {
                        isTeamMember = true
                    }
                }

                if (!isTeamMember
                    && (Collections.disjoint(teamIds, participant.teamUuids ?: ArrayList<String>()) && Collections.disjoint(teamLegacyIds, participant.teamUuids ?: ArrayList<String>()))
                    && !participant.phoneNumber.isNullOrEmpty()
                    && !(CallUtil.arePhoneNumbersEqual(participant.phoneNumber, CallUtil.getStrippedPhoneNumber(ourPhoneNumber ?: "")) || ourUuid?.equals(participant.userUUID) == true)
                ) {
                    participantList.add(participant)
                }
            }

            return participantList.distinctBy { it.phoneNumber }
        }
    }

    fun getDestinationNumbers(): List<String> {
        val destinationNumbers: ArrayList<String> = ArrayList()

        getParticipantsList()?.filter { it.representingTeam == null }?.forEach { participant ->
            if (participant.representingTeam != null) {
                participant.representingTeam?.teamPhoneNumber?.let { destinationNumbers.add(it) }

            } else {
                var isTeamMember = false
                teams?.forEach { team ->
                    team.members?.forEach { member ->
                        if (member.id == participant.userUUID) {
                            isTeamMember = true
                        }
                    }
                }
                if (sendingPhoneNumber?.team?.members?.any { it.id == participant.userUUID } != true && !isTeamMember) {
                    participant.phoneNumber?.let { destinationNumbers.add(it) }
                }
            }
        }

        sendingPhoneNumber?.team?.teamPhoneNumber?.let { destinationNumbers.add(it) }

        teams?.forEach { team ->
            team.teamPhoneNumber?.let { destinationNumbers.add(it) }
        }

        return destinationNumbers.filter { it.isNotEmpty() }.distinct()
    }

    fun getConversationId(): String {
        val participantList: ArrayList<String> = ArrayList()
        var isTeamSms = false

        if (!groupValue.isNullOrEmpty() && participants.isNullOrEmpty() && getAllTeams().isEmpty()) {
            groupValue?.split(",")?.map { it.trim() }?.forEach { participant ->
                participantList.add(participant)
            }

        } else {
            participants?.forEach { participant ->
                if (participant.representingTeam != null) {
                    participant.representingTeam?.teamPhoneNumber?.nullIfEmpty()?.let { participantList.add(it) }
                    isTeamSms = true

                } else {
                    if (!isParticipantTeamMember(participant) || ((!isNewChat && isInit) && (!participant.phoneNumber.isNullOrEmpty() && participant.teamUuids.isNullOrEmpty()))) {
                        participant.phoneNumber?.nullIfEmpty()?.let { participantList.add(it) }
                    }
                }
            }

            if (getAllTeams().isNotEmpty() || participants?.any { it.representingTeam != null } == true) {
                getAllTeams().forEach { team ->
                    team.teamPhoneNumber?.nullIfEmpty()?.let { participantList.add(it) }
                }

                participants?.filter { it.representingTeam != null }?.forEach { participant ->
                    participant.representingTeam?.teamPhoneNumber?.nullIfEmpty()?.let { participantList.add(it) }
                }

                isTeamSms = true

            } else if (groupId.isNullOrEmpty()) {
                ourPhoneNumber?.nullIfEmpty()?.let { participantList.add(it) }
            }
        }

        val isOurTeamSelected = (isTeamSms && !isOurTeamSelected()) || (isNewChat && !isTeamSmsEnabled)
        val isOurNumberSender = CallUtil.arePhoneNumbersEqual(sendingPhoneNumber?.phoneNumber, ourPhoneNumber) && !isInit
        val isOurUserNotParticipant = participants?.isNotEmpty() == true && participants?.any { it.userUUID == ourUuid } == false

        if ((isTeamSms && (isOurTeamSelected || isOurNumberSender)) || (!isTeamSms && (isOurTeamSelected || isOurNumberSender || isOurUserNotParticipant))) {
            ourPhoneNumber?.nullIfEmpty()?.let { participantList.add(it) }
        }

        participantList.sort()

        return participantList.distinct().joinToString(separator = ",")
    }

    fun getAllTeams(): List<SmsTeam> {
        val allTeams: ArrayList<SmsTeam> = ArrayList()
        teams?.let { teamsList ->
            val filteredTeams = if (teamsList.size == 1) {
                teamsList
            } else {
                teamsList.filter { team1 ->
                    !teamsList.any { team2 ->
                        team2 != team1 && (team2.teamId == team1.teamId || team2.teamId == team1.legacyId)
                    }
                }
            }
            allTeams.addAll(filteredTeams)
        }
        sendingPhoneNumber?.team?.let { allTeams.add(it) }
        return allTeams.distinctBy { it.teamId }
    }

    private fun isParticipantTeamMember(participant: SmsParticipant): Boolean {
        val conversationTeams = allSavedTeams?.filter { getAllTeams().map { team -> team.teamId }.contains(it.teamId) || getAllTeams().map { team -> team.legacyId }.contains(it.teamId) }

        if (getAllTeams().firstOrNull { team -> team.members?.firstOrNull { it.id == participant.userUUID } != null } != null) {
            return true
        }

        if (conversationTeams?.firstOrNull { team -> team.members?.firstOrNull { it.id == participant.userUUID } != null } != null) {
            return true
        }

        return false
    }

    fun getTotalTeamsInConversation(): Int {
        return if (isNewChat || !isInit) getAllTeams().size else teams?.size.orZero()
    }

    fun isOurTeamSelected(): Boolean {
        var isOurTeamSelected = false
        val teamsToUse = if (isNewChat || !isInit) getAllTeams() else teams

        if (userTeams == null) {
            teamsToUse?.forEach { team ->
                team.members?.forEach { member ->
                    if (member.id == ourUuid) {
                        isOurTeamSelected = true
                    }
                }
            }

        } else {
            teamsToUse?.forEach { team ->
                userTeams?.firstOrNull { it.teamId == team.teamId }?.let { savedTeam ->
                    savedTeam.members?.forEach { member ->
                        if (member.id == ourUuid) {
                            isOurTeamSelected = true
                        }
                    }
                }
            }
        }

        val ourParticipants = teamMembers?.filter {
            it.contact?.allPhoneNumbers?.any { number -> CallUtil.arePhoneNumbersEqual(number.strippedNumber, ourPhoneNumber) } == true
        }

        ourParticipants?.forEach { participant ->
            if (!Collections.disjoint(teamsToUse?.map { it.teamId } ?: ArrayList<String>(), participant.teamUuids ?: ArrayList<String>())) {
                isOurTeamSelected = true
            }
        }

        return isOurTeamSelected
    }

    @VisibleForTesting
    fun getSendingPhoneNumber(): ConversationViewModel.SendingPhoneNumber? {
        return sendingPhoneNumber
    }
}