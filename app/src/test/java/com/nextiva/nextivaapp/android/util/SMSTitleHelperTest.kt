package com.nextiva.nextivaapp.android.util

import android.text.TextPaint
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.features.messaging.helpers.SMSType
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleHelper
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.annotation.LooperMode

@OptIn(ExperimentalCoroutinesApi::class)
@LooperMode(LooperMode.Mode.PAUSED)
class SMSTitleHelperTest : BaseRobolectricTest() {

    private var mockDbManager: DbManager = mock()

    private var mockSessionManager: SessionManager = mock()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    private val contact1 = NextivaContact().apply {
        userId = "000be0e2-d6c6-11ec-878b-005056a3635e"
        firstName = "Joe"
        lastName = "Connect"
        displayName = "Joe Connect"
        phoneNumbers = ArrayList<PhoneNumber?>().apply { add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "15551111111")) }
    }

    override fun setup() {
        super.setup()
        Dispatchers.setMain(dispatcher)
        whenever(mockDbManager.getContactFromPhoneNumberInThread(any())).thenReturn(
            DbResponse(contact1)
        )
        whenever(mockDbManager.getConnectContactFromPhoneNumberInThread(any())).thenReturn(
            DbResponse(contact1)
        )
    }


    @Test
    fun testTeamSms() = runTest {

        val userTeams = listOf(SmsConversationMocks.team1)
        val allSavedTeams = listOf(
            SmsConversationMocks.team1,
            SmsConversationMocks.team2
        )

        whenever(mockSessionManager.allTeams).thenReturn(allSavedTeams)
        whenever(mockSessionManager.usersTeams).thenReturn(userTeams)
        whenever(mockSessionManager.isNextivaConnectEnabled).thenReturn(true)

        val helper = SmsTitleHelper(mockDbManager, mockSessionManager)
        val conversationDetails = getTeamsConversationDetails()

        val titleInfo = helper.getSMSConversationParticipantInfo(
            conversationDetails = conversationDetails,
            width = 1,
            paint = TextPaint(),
            context = ApplicationProvider.getApplicationContext()
        )

        assertEquals(SmsConversationMocks.team1.teamName, titleInfo.smsTitleName)
    }

    @Test
    fun testOneToOne() = runTest {

        whenever(mockSessionManager.allTeams).thenReturn(null)
        whenever(mockSessionManager.usersTeams).thenReturn(null)
        whenever(mockSessionManager.isNextivaConnectEnabled).thenReturn(true)

        val helper = SmsTitleHelper(mockDbManager, mockSessionManager)
        val conversationDetails = getOneToOneConversationDetails()

        val titleInfo = helper.getSMSConversationParticipantInfo(
            conversationDetails = conversationDetails,
            width = 1000,
            paint = TextPaint(),
            context = ApplicationProvider.getApplicationContext()
        )

        assertEquals(SMSType.OneToOne, titleInfo.type)
        assertEquals(contact1.displayName, titleInfo.smsTitleName)
    }

    private fun getTeamsConversationDetails(): SmsConversationDetails {
        val conversationDetails = SmsConversationDetails(
            SmsConversationMocks.buildTeamSmsMessage(
                SmsConversationMocks.team1,
                SmsConversationMocks.team1Members
            ), SmsConversationMocks.ourNumber, SmsConversationMocks.ourUuid
        )
        conversationDetails.isNewChat = false
        return conversationDetails
    }

    private fun getOneToOneConversationDetails(): SmsConversationDetails {
        val conversationDetails = SmsConversationDetails(
            SmsConversationMocks.buildOneToOneSmsMessageForDetails(
                SmsConversationMocks.externalParticipant2
            ), SmsConversationMocks.ourNumber, SmsConversationMocks.ourUuid
        )
        conversationDetails.isNewChat = false
        return conversationDetails
    }
}