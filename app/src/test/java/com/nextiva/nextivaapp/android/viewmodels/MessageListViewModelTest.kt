/*
 * Copyright (c) 2024 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.compose.ui.state.ToggleableState
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.messaging.helpers.NotificationEvent
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.MessageListViewModel
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildEditModeViewState
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildTwoTeamSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.groupId
import com.nextiva.nextivaapp.android.models.CurrentUser
import com.nextiva.nextivaapp.android.models.UserDetails
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockkStatic
import io.reactivex.Single
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.powermock.api.mockito.PowerMockito.`when`
import org.robolectric.RobolectricTestRunner
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
class MessageListViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var application: Application
    @Inject
    lateinit var schedulerProvider: SchedulerProvider
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var settingsManager: SettingsManager
    @Inject
    lateinit var smsManagementRepository: SmsManagementRepository
    @Inject
    lateinit var calendarManager: CalendarManager
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var connectionStateManager: ConnectionStateManager
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var conversationRepository: ConversationRepository

    private val mockUserDetails: UserDetails = mock()

    private lateinit var viewModel: MessageListViewModel

    override fun setup() {
        super.setup()
        hiltRule.inject()

        viewModel = MessageListViewModel(
            application,
            application,
            smsManagementRepository,
            dbManager,
            sessionManager,
            conversationRepository,
            settingsManager,
            schedulerProvider
        )

        whenever(sessionManager.currentUser).thenReturn(CurrentUser(
            SmsConversationMocks.ourUuid,
            "234234",
            "Active",
            "Display Name",
            "userEmail@nextiva.com",
            "123123",
            "Name",
            "mobileconnect.nextos.com",
            null,
            null,
            "Display")
        )

        whenever(dbManager.getConversationDetailsFrom(any())).thenReturn(null)
        whenever(sessionManager.usersTeams).thenReturn(listOf(SmsConversationMocks.team1))
        whenever(sessionManager.allTeams).thenReturn(listOf(SmsConversationMocks.team1, SmsConversationMocks.team2))
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(true)
        whenever(sessionManager.isTeamSmsEnabled).thenReturn(true)
        whenever(sessionManager.userDetails).thenReturn(mockUserDetails)
        whenever(mockUserDetails.telephoneNumber).thenReturn(SmsConversationMocks.ourNumber.removePrefix("1"))

        viewModel.connectEditModeViewStateLiveData.value = buildEditModeViewState()

        mockkStatic(CallUtil::class)
        every { CallUtil.arePhoneNumbersEqual(SmsConversationMocks.ourNumber, SmsConversationMocks.ourNumber) } returns true
    }

    @Test
    fun testPerformBulkAction_DeleteConversations() {
        val conversationIds = listOf("conversationId1", "conversationId2")
        `when`(conversationRepository.bulkDeleteConversations(any(), any())).thenReturn(Single.just(true))
        viewModel.conversationSelectedItemList = conversationIds.toMutableSet()

        viewModel.performBulkActionOnCommunications(BulkActionsConversationData.JOB_TYPE_DELETE)

        assertEquals(
            NotificationEvent(
                status = true,
                event = NotificationEvent.Event.BULK_DELETE
            ),
            viewModel.notificationEvent.value!!
        )
        verify(conversationRepository).bulkDeleteConversations(any(), any())
    }

    @Test
    fun `deleteSingleConversation sets correct LiveData values and calls repository`() {
        val expectedProgressValue = R.string.progress_deleting
        whenever(conversationRepository.bulkDeleteConversations(any(), any())).thenReturn(Single.just(true))
        viewModel.deleteSingleConversation(groupId)
        assertEquals(expectedProgressValue, viewModel.userRepoApiSmsStartedLiveData.value)
        verify(conversationRepository).bulkDeleteConversations(any(), any())

        assertEquals(
            NotificationEvent(
                status = true,
                event = NotificationEvent.Event.SINGLE_DELETE
            ),
            viewModel.notificationEvent.value!!
        )
    }

    @Test
    fun `deleteSingleConversation updates LiveData on success`() {
        whenever(conversationRepository.bulkDeleteConversations(any(), any())).thenReturn(Single.just(true))
        viewModel.deleteSingleConversation(groupId)
        assertEquals(true, viewModel.userRepoApiSmsFinishedLiveData.value)
    }

    @Test
    fun `updateReadStatusForSingleSms sets correct LiveData and updates DB when status is read`() {
        val status = BulkActionsConversationData.MODIFICATION_STATUS_READ
        val expectedProgressValue = R.string.progress_updating
        whenever(conversationRepository.bulkUpdateConversations(any())).thenReturn(Single.just(true))
        viewModel.updateReadStatusForSingleSms(groupId, status)
        verify(dbManager).updateReadStatusForGroupId(groupId)
        assertEquals(expectedProgressValue, viewModel.userRepoApiSmsStartedLiveData.value)
        verify(conversationRepository).bulkUpdateConversations(any())
    }

    @Test
    fun `updateReadStatusForSingleSms updates DB correctly and handles success when status is not read`() {
        val status = "not_read"
        val mostRecentMessage = buildTwoTeamSmsMessage()
        whenever(dbManager.getMostRecentMessageFromConversationWithoutDraft(groupId, Enums.SMSMessages.SentStatus.DRAFT)).thenReturn(mostRecentMessage)
        whenever(conversationRepository.bulkUpdateConversations(any())).thenReturn(Single.just(true))
        viewModel.updateReadStatusForSingleSms(groupId, status)
        verify(conversationRepository).bulkUpdateConversations(any())
        assertEquals(true, viewModel.userRepoApiSmsFinishedLiveData.value)
        assertEquals(
            NotificationEvent(
                status = true,
                event = NotificationEvent.Event.SINGLE_READ_STATUS
            ),
            viewModel.notificationEvent.value!!
        )
    }

    @Test
    fun testPerformBulkAction_UpdateReadStatus() {
        val readStatus = BulkActionsConversationData.MODIFICATION_STATUS_READ
        `when`(conversationRepository.bulkUpdateConversations(any())).thenReturn(Single.just(true))

        viewModel.performBulkActionOnCommunications(BulkActionsConversationData.JOB_TYPE_UPDATE, readStatus)

        assertTrue(viewModel.updateReadStatusResultLiveData.value!!.first)
        assertEquals(readStatus, viewModel.updateReadStatusResultLiveData.value!!.second)
        verify(conversationRepository).bulkUpdateConversations(any())
    }

    @Test
    fun testUpdateEditModeViewState_AllParametersNull() {
        viewModel.updateEditModeViewState()

        val viewState = viewModel.connectEditModeViewStateLiveData.value

        assertNotNull(viewState)
        assertEquals(ToggleableState.Off, viewState?.selectAllCheckState)
        assertFalse(viewState?.shouldShowBulkUpdateActionIcons ?: true)
        assertFalse(viewState?.shouldShowBulkDeleteActionIcons ?: true)
        assertEquals("", viewState?.itemCountDescription)
    }

    @Test
    fun testUpdateEditModeViewState_SelectAllCheckStateNotNull() {
        viewModel.updateEditModeViewState(selectAllCheckState = ToggleableState.On)

        val viewState = viewModel.connectEditModeViewStateLiveData.value

        assertNotNull(viewState)
        assertEquals(ToggleableState.On, viewState?.selectAllCheckState)
    }

    @Test
    fun testUpdateEditModeViewState_SelectAllCheckStateNotNull1() {
        assertEquals(true, viewModel.isTeamSmsEnabled())
        assertEquals(true, viewModel.isTeamSmsLicenseEnabled())
    }
}