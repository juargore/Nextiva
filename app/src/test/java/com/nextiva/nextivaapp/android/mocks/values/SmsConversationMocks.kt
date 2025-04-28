package com.nextiva.nextivaapp.android.mocks.values

import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.models.ConnectContactStripped
import com.nextiva.nextivaapp.android.models.ConnectEditModeViewState
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.models.net.platform.teams.TeamMemberResponse
import org.threeten.bp.Instant
import java.util.UUID

object SmsConversationMocks {
    val ourNumber = "17203103329"
    val ourUuid = "000be0e2-d6c6-11ec-878b-005056a3635e"
    val groupId = "916bd2a3-4c9a-3603-b48b-9a85b0d096d8"

    val ourParticipant = SmsParticipant("Joe Connect", "", ourNumber, ourUuid, "", null)
    val ourExternalParticipant = SmsParticipant("Joe Connect", "", ourNumber, "", "", null)

    val ourParticipantWithContacts = SmsParticipant("Joe Connect", "", ourNumber, ourUuid, "", listOf(
            ConnectContactStripped(
                    dbId = 1597,
                    contactTypeId = "c9395547-b43f-11ec-b729-005056a33d5a",
                    contactType = 7,
                    displayName = "Tyla Connect 1",
                    firstName = "Tyla",
                    lastName = "Connect 1",
                    favorite = false,
                    uiName = "Tyla Connect 1",
                    phoneNumbers = listOf(
                            PhoneNumber(
                                    id = 1772,
                                    contactId = 1597,
                                    number = "7203103342",
                                    strippedNumber = "7203103342",
                                    type = 0,
                                    label = "Administration",
                                    extension = "1234",
                                    pinOne = null,
                                    pinTwo = null,
                                    transactionId = "5db00145-9536-485e-b3cd-044e1a5fe9e0"
                            ),
                            PhoneNumber(
                                    id = 1773,
                                    contactId = 1597,
                                    number = "7203103346",
                                    strippedNumber = "7203103346",
                                    type = 1,
                                    label = "Mirsada N",
                                    extension = "1234",
                                    pinOne = null,
                                    pinTwo = null,
                                    transactionId = "5db00145-9536-485e-b3cd-044e1a5fe9e0"
                            )
                    ),
                    presences = listOf(
                        DbPresence(
                                id = 60,
                                contactId = 1597,
                                userId = "c9395547-b43f-11ec-b729-005056a33d5a",
                                jid = null,
                                state = 6,
                                type = null,
                                priority = null,
                                status = "RC -- make it the BEST phone app in the world!!!",
                                statusExpiryTime = "0001-01-01T00:00:00Z",
                                transactionId = null,
                                inCall = false
                        )
                    ),
                    vCards = listOf()
            ))
    )

    val externalParticipant1 = SmsParticipant("Joe Connect 2", "", "14702292762", "5cea4c2d-e106-11ec-8c37-005056a31519", "", null)
    val externalParticipant2 = SmsParticipant("Joe Connect 3", "", "19707796149", "19f4cde9-4a3f-11ed-bd9c-005056a31519", "", null)
    val team1Members = listOf(
        SmsParticipant("Joe Connect", "", "", "000be0e2-d6c6-11ec-878b-005056a3635e", "688452", null),
        SmsParticipant("Ebrahim connect 3", "", "", "3653e1d0-cd5c-11ec-8f87-005056a33d5a", "688452", null),
        SmsParticipant("Brian Connect", "", "", "a4db3aad-b5cf-11ec-964e-005056a31519", "688452", null),
        SmsParticipant("Sakshi Connect 2", "", "", "a2a872ee-d6c6-11ec-8c37-005056a31519", "688452", null),
        SmsParticipant("Hugo Two", "", "", "98a35b46-06cc-11ed-973e-005056a33d5a", "688452", null),
        SmsParticipant("Arturo 1", "", "", "8230f4b9-da0e-11ed-9594-00505692b190", "688452", null),
        SmsParticipant("Tyla Connect 3", "", "", "7647658d-b440-11ec-99e2-005056a3635e", "688452", null),
        SmsParticipant("Jacques Connect", "", "", "80fe95b0-dad8-11ec-8c37-005056a31519", "688452", null),
        SmsParticipant("Joe Connect 3", "", "", "19f4cde9-4a3f-11ed-bd9c-005056a31519", "688452", null)
    )
    val team1 = SmsTeam(null, "688452", "Administration", "12174329596", "688452", team1Members.map { TeamMemberResponse(it.userUUID, it.name) } as? ArrayList<TeamMemberResponse>, true)
    val team2Members = listOf(
        SmsParticipant("Stephen Connect", "", "", "c68b2520-b5e4-11ec-b729-005056a33d5a", "688450", null),
        SmsParticipant("Thad Connect", "", "", "ba905acd-ba93-11ec-b729-005056a33d5a", "688450", null),
        SmsParticipant("Doug Z Connect 1", "", "", "e29d8e75-c727-11ec-8c37-005056a31519", "688450", null),
        SmsParticipant("Mirsada Connect 1", "", "", "3d81ad46-b443-11ec-99e2-005056a3635e", "688450", null),
        SmsParticipant("Doug A Connect 2", "", "", "16ed9107-fe04-11ec-a279-005056a33d5a", "688450", null),
        SmsParticipant("Peter Connect", "", "", "be04617a-d7c1-11ec-878b-005056a3635e", "688450", null)
    )
    val team2 = SmsTeam(null, "688450", "Billing", "12174329597", "688450", team2Members.map { TeamMemberResponse(it.userUUID, it.name) } as? ArrayList<TeamMemberResponse>, true)

    fun buildOneToOneSmsMessageForDetails(participant: SmsParticipant): SmsMessage {
        val smsMessage = SmsMessage()
        smsMessage.messageId = UUID.randomUUID().toString()
        smsMessage.channel = "SMS"
        smsMessage.body = "Test Message"
        smsMessage.preview = "Test Message"
        smsMessage.sent = Instant.now()
        smsMessage.groupValue = "${participant.phoneNumber},$ourNumber"
        smsMessage.groupId = "${participant.phoneNumber},$ourNumber"
        smsMessage.sender = listOf(ourParticipant)
        smsMessage.recipientParticipantsList = listOf(participant)

        return smsMessage
    }

    fun buildOneToOneSmsMessage(participant: SmsParticipant): SmsMessage {
        val smsMessage = SmsMessage()
        smsMessage.messageId = UUID.randomUUID().toString()
        smsMessage.channel = "SMS"
        smsMessage.body = "Test Message"
        smsMessage.preview = "Test Message"
        smsMessage.sent = Instant.now()
        smsMessage.groupValue = "${participant.phoneNumber},$ourNumber"
        smsMessage.groupId = "${participant.phoneNumber},$ourNumber"
        smsMessage.sender = listOf(ourExternalParticipant)
        smsMessage.recipientParticipantsList = listOf(participant)

        return smsMessage
    }

    fun buildGroupSmsMessage(): SmsMessage {
        val smsMessage = SmsMessage()
        smsMessage.messageId = UUID.randomUUID().toString()
        smsMessage.channel = "SMS"
        smsMessage.body = "Test Message"
        smsMessage.preview = "Test Message"
        smsMessage.sent = Instant.now()
        smsMessage.groupValue = "14702292762,$ourNumber,19707796149"
        smsMessage.groupId = "14702292762,$ourNumber,19707796149"
        smsMessage.sender = listOf(ourParticipant)
        smsMessage.recipientParticipantsList = listOf(externalParticipant1, externalParticipant2)

        return smsMessage
    }

    fun buildTeamSmsMessage(team: SmsTeam, teamMembers: List<SmsParticipant>): SmsMessage {
        val smsMessage = SmsMessage()
        smsMessage.messageId = UUID.randomUUID().toString()
        smsMessage.channel = "SMS"
        smsMessage.body = "Joe C: TeamTest2"
        smsMessage.preview = "Joe C: TeamTest2"
        smsMessage.sent = Instant.now()
        smsMessage.groupId = "916bd2a3-4c9a-3603-b48b-9a85b0d096d8"
        smsMessage.recipientParticipantsList = teamMembers
        smsMessage.teams = listOf(team)

        return smsMessage
    }

    fun buildTwoTeamSmsMessage(): SmsMessage {
        val smsMessage = SmsMessage()
        smsMessage.messageId = UUID.randomUUID().toString()
        smsMessage.channel = "SMS"
        smsMessage.body = "Joe C: TeamTest2"
        smsMessage.preview = "Joe C: TeamTest2"
        smsMessage.sent = Instant.now()
        smsMessage.groupId = "22107bbf-ae1c-3905-aeaa-21c63e04f24b"

        val participants: ArrayList<SmsParticipant> = ArrayList()
        participants.addAll(team2Members)
        participants.addAll(team1Members)
        smsMessage.recipientParticipantsList = participants
        smsMessage.teams = listOf(team1, team2)

        return smsMessage
    }

    fun buildTeamAndExternalSmsMessage(): SmsMessage {
        val smsMessage = SmsMessage()
        smsMessage.messageId = UUID.randomUUID().toString()
        smsMessage.channel = "SMS"
        smsMessage.body = "Joe C: TeamTest2"
        smsMessage.preview = "Joe C: TeamTest2"
        smsMessage.sent = Instant.now()
        smsMessage.groupValue = "14702292762"
        smsMessage.groupId = "453026dc-6b68-35ed-a36b-e31a286029cb"

        val participants: ArrayList<SmsParticipant> = ArrayList()
        participants.add(externalParticipant1)
        participants.addAll(team1Members)
        smsMessage.recipientParticipantsList = participants
        smsMessage.teams = listOf(team1)

        return smsMessage
    }

    fun buildTeamAndOurNumberAsExternalSmsMessage(): SmsMessage {
        val smsMessage = SmsMessage()
        smsMessage.messageId = UUID.randomUUID().toString()
        smsMessage.channel = "SMS"
        smsMessage.body = "Joe C: TeamTest2"
        smsMessage.preview = "Joe C: TeamTest2"
        smsMessage.sent = Instant.now()
        smsMessage.groupValue = "14702292762"
        smsMessage.groupId = "453026dc-6b68-35ed-a36b-e31a286029cb"

        val participants: ArrayList<SmsParticipant> = ArrayList()
        participants.add(ourExternalParticipant)
        participants.addAll(team1Members)
        smsMessage.recipientParticipantsList = participants
        smsMessage.teams = listOf(team1)

        return smsMessage
    }

    fun buildEditModeViewState(): ConnectEditModeViewState {
        return ConnectEditModeViewState(
                itemCountDescription = "",
                onSelectAllCheckedChanged = { },
                onMarkUnReadIconClicked = { },
                onMarkReadIconClicked = { },
                onDeleteIconClicked = { },
                onDoneClicked = { }
        )
    }
}
