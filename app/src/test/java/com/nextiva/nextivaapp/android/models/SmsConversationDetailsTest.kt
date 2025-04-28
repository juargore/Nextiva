package com.nextiva.nextivaapp.android.models

import android.text.TextUtils
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.ConversationViewModel
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildGroupSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildOneToOneSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildOneToOneSmsMessageForDetails
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildTeamAndExternalSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildTeamSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildTwoTeamSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.externalParticipant1
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.externalParticipant2
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.ourNumber
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.ourUuid
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.team1
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.team1Members
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.team2
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.team2Members
import io.mockk.every
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SmsConversationDetailsTest : BaseRobolectricTest() {

    override fun setup() {
        super.setup()
        mockkStatic(TextUtils::class)
        every { TextUtils.equals("17203103329", ourNumber) } returns true
    }

    private fun setupConversationDetails(conversationDetails: SmsConversationDetails) {
        conversationDetails.userTeams = listOf(team1)
        conversationDetails.allSavedTeams = listOf(team1, team2)
        conversationDetails.isNewChat = false
    }

    @Test
    fun oneToOneSms_getDestinationNumbers() {
        val conversationDetails = SmsConversationDetails(buildOneToOneSmsMessage(externalParticipant1), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals(1, conversationDetails.getDestinationNumbers().size)
    }

    @Test
    fun onToOneSms_getConversationId() {
        val conversationDetails = SmsConversationDetails(buildOneToOneSmsMessage(externalParticipant1), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals("${externalParticipant1.phoneNumber},$ourNumber", conversationDetails.getConversationId())
    }

    @Test
    fun groupSms_getDestinationNumbers() {
        val conversationDetails = SmsConversationDetails(buildGroupSmsMessage(), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals(2, conversationDetails.getDestinationNumbers().size)
    }

    @Test
    fun groupSms_getConversationId() {
        val conversationDetails = SmsConversationDetails(buildGroupSmsMessage(), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals("${externalParticipant1.phoneNumber},$ourNumber,${externalParticipant2.phoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun teamSms_getDestinationNumbers() {
        val conversationDetails = SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals(1, conversationDetails.getDestinationNumbers().size)
    }

    @Test
    fun teamSms_getConversationId() {
        val conversationDetails = SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals("${team1.teamPhoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun twoTeamSms_getDestinationNumbers() {
        val conversationDetails = SmsConversationDetails(buildTwoTeamSmsMessage(), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals(2, conversationDetails.getDestinationNumbers().size)
    }

    @Test
    fun twoTeamSms_getConversationId() {
        val conversationDetails = SmsConversationDetails(buildTwoTeamSmsMessage(), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals("${team1.teamPhoneNumber},${team2.teamPhoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun teamAndExternalParticipant_getDestinationNumbers() {
        val conversationDetails = SmsConversationDetails(buildTeamAndExternalSmsMessage(), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals(2, conversationDetails.getDestinationNumbers().size)
    }

    @Test
    fun teamSmsAndExternalParticipant_getConversationId() {
        val conversationDetails = SmsConversationDetails(buildTeamAndExternalSmsMessage(), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals("${team1.teamPhoneNumber},${externalParticipant1.phoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun oneToOneWithTeam1Member_isNewChat_updateSendingNumberToTeam1_conversationIdUpdates() {
        val conversationDetails = SmsConversationDetails(buildOneToOneSmsMessageForDetails(externalParticipant2), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals("${ourNumber},${externalParticipant2.phoneNumber}", conversationDetails.getConversationId())

        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(team1.teamPhoneNumber ?: "", team1), null, true)
        conversationDetails.participants?.addAll(team1Members)

        assertEquals("${team1.teamPhoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun oneToOneWithNoTeam1Member_isNewChat_updateSendingNumberToTeam1_conversationIdUpdates() {
        val conversationDetails = SmsConversationDetails(buildOneToOneSmsMessageForDetails(externalParticipant1), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        assertEquals("${externalParticipant1.phoneNumber},$ourNumber", conversationDetails.getConversationId())

        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(team1.teamPhoneNumber ?: "", team1), null, true)
        conversationDetails.participants?.addAll(team1Members)

        assertEquals("${team1.teamPhoneNumber},${externalParticipant1.phoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun oneToOneWithTeam_userNotTeamMember_conversationIdHasOurNumber() {
        val conversationDetails = SmsConversationDetails(buildTeamSmsMessage(team2, team2Members), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)
        assertEquals("${team2.teamPhoneNumber},$ourNumber", conversationDetails.getConversationId())
    }

    @Test
    fun oneToOneWithTeam_userTeamMember_teamLicenseDisabled_sendingNumberIsOurs() {
        val conversationDetails = SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)
        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(ourNumber, null), null)

        conversationDetails.isTeamSmsEnabled = false

        assertEquals("${team1.teamPhoneNumber}", conversationDetails.getConversationId())
        assertEquals(ourNumber, conversationDetails.getSendingPhoneNumber()?.phoneNumber)
    }

    @Test
    fun oneToOneWithTeam_userTeamMember_teamLicenseEnabled_conversationIdJustTeam() {
        val conversationDetails = SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        conversationDetails.isTeamSmsEnabled = true

        assertEquals("${team1.teamPhoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun newChat_userTeamMember_toTeam_teamLicenseDisabled() {
        val conversationDetails = SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(ourNumber, null), null)
        conversationDetails.isTeamSmsEnabled = false
        conversationDetails.isNewChat = true

        assertEquals("${team1.teamPhoneNumber},$ourNumber", conversationDetails.getConversationId())
    }

    @Test
    fun newChat_userTeamMember_toTeam_teamLicenseEnabled() {
        val conversationDetails = SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(ourNumber, null), null)
        conversationDetails.isTeamSmsEnabled = true
        conversationDetails.isNewChat = true

        assertEquals("${team1.teamPhoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun newChat_userTeamMember_externalTeamMember_switchToTeam_teamLicenseEnabled() {
        val conversationDetails = SmsConversationDetails(buildOneToOneSmsMessageForDetails(externalParticipant2), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(ourNumber, null), null)
        conversationDetails.isTeamSmsEnabled = true
        conversationDetails.isNewChat = true

        assertEquals("$ourNumber,${externalParticipant2.phoneNumber}", conversationDetails.getConversationId())

        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(team1.teamPhoneNumber ?: "", team1), null, true)
        conversationDetails.participants?.addAll(team1Members)

        assertEquals("${team1.teamPhoneNumber}", conversationDetails.getConversationId())
    }

    @Test
    fun newChat_userTeamMember_externalNotTeamMember_switchToTeam_teamLicenseEnabled() {
        val conversationDetails = SmsConversationDetails(buildOneToOneSmsMessageForDetails(externalParticipant1), ourNumber, ourUuid)
        setupConversationDetails(conversationDetails)

        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(ourNumber, null), null)
        conversationDetails.isTeamSmsEnabled = true
        conversationDetails.isNewChat = true

        assertEquals("${externalParticipant1.phoneNumber},$ourNumber", conversationDetails.getConversationId())

        conversationDetails.updateSendingPhoneNumber(ConversationViewModel.SendingPhoneNumber(team1.teamPhoneNumber ?: "", team1), null, true)
        conversationDetails.participants?.addAll(team1Members)

        assertEquals("${team1.teamPhoneNumber},${externalParticipant1.phoneNumber}", conversationDetails.getConversationId())
    }
}