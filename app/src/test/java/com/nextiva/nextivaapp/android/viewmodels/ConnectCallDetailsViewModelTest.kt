package com.nextiva.nextivaapp.android.viewmodels

import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.Voicemail
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@HiltAndroidTest
class ConnectCallDetailsViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockConversationRepository: ConversationRepository = mock()
    private val mockDbManager: DbManager = mock()
    private val mockBlockingNumberManager: BlockingNumberManager = mock()

    private lateinit var viewModel: ConnectCallDetailsViewModel


    override fun setup() {
        super.setup()
        hiltRule.inject()

        viewModel = ConnectCallDetailsViewModel(
            ApplicationProvider.getApplicationContext(),
            ApplicationProvider.getApplicationContext(),
            mockDbManager,
            mockBlockingNumberManager,
            mockConversationRepository)

        `when`(mockDbManager.markCallLogEntryRead(anyString())).thenReturn(Completable.complete())
        `when`(mockConversationRepository.markCallRead(anyString())).thenReturn(Single.just(true))

    }

    @Test
    fun getDetailListItems_callLogEntryNotNull_marksCallAsRead() {
        val callLogEntry = CallLogEntry(
            "123",
            "Display Name",
            1523661568000L,
            "1",
            "1112223333",
            Enums.Calls.CallTypes.MISSED,
            byteArrayOf(2),
            "UI Name",
            Enums.Contacts.PresenceStates.AVAILABLE,
            -10,
            "My Status",
            0,
            "jid@jid.im",
            Enums.Contacts.PresenceTypes.AVAILABLE,
            123,
            ""
        );
        callLogEntry.setIsRead(false)
        viewModel.callLogEntry = callLogEntry

        viewModel.getDetailListItems()

        verify(mockConversationRepository).markCallRead("123")
    }

    @Test
    fun getDetailListItems_callLogEntryNotNullAndRead_doesNotMarkCallAsRead() {

        val callLogEntry = CallLogEntry(
            "callLogId",
            "Display Name",
            1523661568000L,
            "1",
            "1112223333",
            Enums.Calls.CallTypes.MISSED,
            byteArrayOf(2),
            "UI Name",
            Enums.Contacts.PresenceStates.AVAILABLE,
            -10,
            "My Status",
            0,
            "jid@jid.im",
            Enums.Contacts.PresenceTypes.AVAILABLE,
            123,
            ""
        );
        callLogEntry.setIsRead(true)
        viewModel.callLogEntry = callLogEntry

        viewModel.getDetailListItems()

        verify(mockConversationRepository, never()).markCallRead(anyString())
    }

    @Test
    fun getDetailListItems_voicemailNotNull_populatesListItems() {
        val voicemail =  Voicemail(
            voicemailId = "123",
            duration = 60,
            address = "1112223333",
            name = "Display Name",
            userId = "1",
            isRead = false,
            time = 1523661568000L,
            messageId = "1",
            transcription = "This is a transcription of the voicemail.",
            rating = "5",
            actualVoiceMailId = "123",
            formattedPhoneNumber = "1112223333",
            avatar = byteArrayOf(2),
            uiName = "UI Name",
            presenceState = Enums.Contacts.PresenceStates.AVAILABLE,
            presencePriority = -10,
            statusText = "My Status",
            presenceType = Enums.Contacts.PresenceTypes.AVAILABLE,
            jid = "jid@jid.im",
            contactType = 123,
            callerId = null
        )
        viewModel.voicemail = voicemail

        viewModel.getDetailListItems()

        assertNotNull(viewModel.getBaseListItemsLiveData().value)
    }

    @Test
    fun getDetailListItems_callLogEntryAndVoicemailNull_setsListItemsToNull() {
        viewModel.callLogEntry = null
        viewModel.voicemail = null

        viewModel.getDetailListItems()

        assertNull(viewModel.getBaseListItemsLiveData().value)
    }
}