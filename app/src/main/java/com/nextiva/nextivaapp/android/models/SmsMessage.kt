/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android.models

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.Relation
import com.nextiva.nextivaapp.android.db.model.DbAttachment
import com.nextiva.nextivaapp.android.db.model.DbMessageState
import com.nextiva.nextivaapp.android.db.model.DbParticipant
import com.nextiva.nextivaapp.android.db.model.DbRecipient
import com.nextiva.nextivaapp.android.db.model.DbSender
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.db.model.SmsTeamRelation
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.util.CallUtil
import org.jetbrains.annotations.TestOnly
import org.threeten.bp.Instant
import java.io.Serializable
import java.util.Collections

data class SmsMessage(
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_ID) var id: Long?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID) var messageId: String?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_CHANNEL) var channel: String?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_BODY) var body: String?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_PREVIEW) var preview: String?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_SENT) var sent: Instant?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_PRIORITY) var priority: String?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_GROUP_VALUE) var groupValue: String?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_IS_SENDER) var isSender: Boolean?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_SENT_STATUS) var sentStatus: Int?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_GROUP_ID) var groupId: String?,
        @field:ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_UI_NAME) var senderUiName: String?,
        @field:ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_PHOTO_DATA) var photoData: ByteArray?,
        @field:ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_CONVERSATION_ID) var conversationId: String?,
        @Relation(parentColumn = DbConstants.SMS_MESSAGE_COLUMN_NAME_ID,
                entityColumn = DbConstants.MESSAGE_STATE_COLUMN_NAME_SMS_ID,
                entity = DbMessageState::class)
        private var _messageStates: List<DbMessageState>?,
        @Relation(parentColumn = DbConstants.SMS_MESSAGE_COLUMN_NAME_ID,
                entityColumn = DbConstants.ATTACHMENTS_COLUMN_SMS_ID,
                entity = DbAttachment::class)
        var attachments: List<DbAttachment>?,
        @Relation(parentColumn = DbConstants.SMS_MESSAGE_COLUMN_NAME_ID,
                entityColumn = DbConstants.PARTICIPANT_COLUMN_NAME_ID,
                entity = DbParticipant::class,
                associateBy = Junction(value = DbRecipient::class,
                        parentColumn = DbConstants.RECIPIENT_COLUMN_NAME_MESSAGE_ID,
                        entityColumn = DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID))
        var recipientParticipantsList: List<SmsParticipant>?,
        @Relation(parentColumn = DbConstants.SMS_MESSAGE_COLUMN_NAME_ID,
                entityColumn = DbConstants.PARTICIPANT_COLUMN_NAME_ID,
                entity = DbParticipant::class,
                associateBy = Junction(value = DbSender::class,
                        parentColumn = DbConstants.SENDER_COLUMN_NAME_MESSAGE_ID,
                        entityColumn = DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID))
        var sender: List<SmsParticipant>? = null,
        @Relation(parentColumn = DbConstants.SMS_MESSAGE_COLUMN_NAME_ID,
                entityColumn = DbConstants.SMS_TEAM_COLUMN_NAME_ID,
                entity = SmsTeam::class,
                associateBy = Junction(value = SmsTeamRelation::class,
                parentColumn = DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_MESSAGE_ID,
                entityColumn = DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_TEAM_ID))
        var teams: List<SmsTeam>? = null) : Serializable {

        val messageState: DbMessageState?
                get() = _messageStates?.firstOrNull()

        val teamMembers: ArrayList<SmsParticipant>
                get() {
                        val teamMembers: ArrayList<SmsParticipant> = ArrayList()

                        sender?.forEach { sender ->
                                if (sender.phoneNumber.isNullOrEmpty()) {
                                        teamMembers.add(sender)

                                } else if (!teams.isNullOrEmpty() && !sender.teamUuids.isNullOrEmpty() &&
                                        !Collections.disjoint(teams?.map { it.teamId } ?: ArrayList<String>(), sender.teamUuids ?: ArrayList<String>())) {
                                        teamMembers.add(sender)
                                }
                        }

                        recipientParticipantsList?.forEach { recipient ->
                                if (recipient.phoneNumber.isNullOrEmpty()) {
                                        teamMembers.add(recipient)

                                } else if (!teams.isNullOrEmpty() &&
                                        !recipient.teamUuids.isNullOrEmpty() &&
                                        !Collections.disjoint(teams?.map { it.teamId } ?: ArrayList<String>(), recipient.teamUuids ?: ArrayList<String>())) {
                                        teamMembers.add(recipient)
                                }
                        }

                        return teamMembers
                }

        @Ignore
        constructor() : this(null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null)

        @TestOnly
        constructor(messageId: String) : this(null, messageId, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null)

        fun getDisplayNameString(phoneNumbersList: List<String>?): List<SmsParticipant>? {
                val allParticipants: List<SmsParticipant>? = recipientParticipantsList?.let { sender?.plus(it)?.distinct() }
                if (!allParticipants.isNullOrEmpty()) {
                        return allParticipants.filter {
                                var isMatched = false
                                if (!phoneNumbersList.isNullOrEmpty()) {
                                        for (phoneNumber in phoneNumbersList) {
                                                if (it.phoneNumber == phoneNumber) {
                                                        isMatched = true
                                                        break
                                                }
                                        }
                                }
                                isMatched
                        }
                }

                return null
        }

        fun getParticipantsList(ourPhoneNumber: String): List<SmsParticipant> {
                val participantList: ArrayList<SmsParticipant> = ArrayList()
                sender?.forEach { sender ->
                        if (Collections.disjoint(teams?.map { it.teamId } ?: ArrayList<String>(), sender.teamUuids ?: ArrayList<String>())) {
                                participantList.add(sender)
                        }
                }

                recipientParticipantsList?.forEach { recipient ->
                        if (Collections.disjoint(teams?.map { it.teamId } ?: ArrayList<String>(), recipient.teamUuids ?: ArrayList<String>())) {
                                participantList.add(recipient)
                        }
                }

                teamMembers.forEach { teamMember -> participantList.add(teamMember) }
                if ((teams?.size ?: 0) > 0) participantList.removeIf { CallUtil.arePhoneNumbersEqual(it.phoneNumber, ourPhoneNumber) }

                return participantList.distinct()
        }

        fun isOurExternalNumberAdded(ourNumber: String?): Boolean {
                sender?.forEach { sender ->
                        if (CallUtil.arePhoneNumbersEqual(ourNumber, sender.phoneNumber)) {
                                return true
                        }
                }

                recipientParticipantsList?.forEach { recipient ->
                        if (CallUtil.arePhoneNumbersEqual(ourNumber, recipient.phoneNumber)) {
                                return true
                        }
                }

                return false
        }

        fun containsUserSmsEnabledTeam(ourTeams: List<SmsTeam>): Boolean {
                var containsUserEnabledTeam = false

                teams?.forEach teamsLoop@ { team ->
                        ourTeams.forEach { ourTeam ->
                                if (team.teamId == ourTeam.teamId && ourTeam.smsEnabled == true) {
                                        containsUserEnabledTeam = true
                                        return@teamsLoop
                                }
                        }
                }

                return containsUserEnabledTeam
        }

        fun getUserIdForPresence() = sender?.firstOrNull()?.userUUID

//      This returns a true if the user is part of a team included in the message and does not equal
//      the logged in user.  The web is showing UI names based on if the participant has a phone
//      number or not.  This may be useful in the future.
//        fun getParticipantsList(ourPhoneNumber: String, ourUuid: String?): List<SmsParticipant>? {
//                return recipientParticipantsList?.let {
//                        sender?.plus(it)?.distinct()
//                }?.filter { participant ->
//                         return@filter Collections.disjoint(teams?.map { it.teamId } ?: ArrayList<String>(), participant.teamUuids ?: ArrayList<String>()) &&
//                              !(ourUuid?.equalsIgnoringEmpty(participant.userUUID) == true ||
//                              participant.phoneNumber?.nullIfEmpty()?.let { CallUtil.getStrippedPhoneNumber(it) }
//                              ?.equalsIgnoringEmpty(CallUtil.getStrippedPhoneNumber(ourPhoneNumber)) == true)
//                }
//        }
}