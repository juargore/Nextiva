package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.ui.PendingMessageData
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.ConversationViewModel
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.DatadogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildGroupSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildOneToOneSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildTeamAndExternalSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildTeamAndOurNumberAsExternalSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildTeamSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.buildTwoTeamSmsMessage
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.externalParticipant1
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.externalParticipant2
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.ourNumber
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.ourParticipantWithContacts
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.ourUuid
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.team1
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.team1Members
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.team2
import com.nextiva.nextivaapp.android.mocks.values.SmsConversationMocks.team2Members
import com.nextiva.nextivaapp.android.models.CurrentUser
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.UserDetails
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.Event
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockkStatic
import io.reactivex.Single
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
class ConversationViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val sendingPhoneNumberObserver: Observer<ConversationViewModel.SendingPhoneNumber> = mock()
    private val uiNameTextChangedObserver: Observer<NextivaContact?> = mock()
    private val chatMessageListItemsObserver: Observer<PagingData<BaseListItem>> = mock()
    private val showNoInternetObserver: Observer<Void?> = mock()
    private val smsMessageSendingObserver: Observer<Void?> = mock()
    private val showProgressDialogObserver: Observer<Void?> = mock()
    private val dismissProgressBarDialogObserver: Observer<Void?> = mock()
    private val processedCallInfoObserver: Observer<ParticipantInfo> = mock()
    private val maxRateExceededObserver: Observer<Boolean> = mock()
    private val pendingMessageObserver: Observer<Event<PendingMessageData>> = mock()

    @Inject
    lateinit var application: Application
    @Inject
    lateinit var schedulerProvider: SchedulerProvider
    @Inject
    lateinit var dbManager: DbManager
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
    @Inject
    lateinit var dataDogManager: DatadogManager

    private val mockUserDetails: UserDetails = mock()

    private lateinit var viewModel: ConversationViewModel

    override fun setup() {
        super.setup()

        hiltRule.inject()
        viewModel = ConversationViewModel(ApplicationProvider.getApplicationContext(),
            schedulerProvider,
            dbManager,
            smsManagementRepository,
            calendarManager,
            sessionManager,
            connectionStateManager,
            analyticsManager,
            sharedPreferencesManager,
            notificationManager,
            dataDogManager,
            conversationRepository)

        whenever(sessionManager.currentUser).thenReturn(CurrentUser(
            ourUuid,
            "234234",
            "Active",
            "Display Name",
            "userEmail@nextiva.com",
            "123123",
            "Name",
            "mobileconnect.nextos.com",
            null,
            null,
            "Display"))

        whenever(dbManager.getConversationDetailsFrom(any())).thenReturn(null)
        whenever(sessionManager.usersTeams).thenReturn(listOf(team1))
        whenever(sessionManager.allTeams).thenReturn(listOf(team1, team2))
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(true)
        whenever(sessionManager.userDetails).thenReturn(mockUserDetails)
        whenever(mockUserDetails.telephoneNumber).thenReturn(ourNumber.removePrefix("1"))

        mockkStatic(CallUtil::class)
        every { CallUtil.arePhoneNumbersEqual(ourNumber, ourNumber) } returns true

        viewModel.sendingPhoneNumber.observeForever(sendingPhoneNumberObserver)
        viewModel.uiNameTextChangedLiveData.observeForever(uiNameTextChangedObserver)
        viewModel.showNoInternetLiveData.observeForever(showNoInternetObserver)
        viewModel.showProgressDialog.observeForever(showProgressDialogObserver)
        viewModel.dismissProgressBarDialog.observeForever(dismissProgressBarDialogObserver)
        viewModel.processedCallInfoMutableLiveData.observeForever(processedCallInfoObserver)
        viewModel.maxRateExceededLiveData.observeForever(maxRateExceededObserver)
        viewModel.pendingMessage.observeForever(pendingMessageObserver)
    }

    private fun getSetupBundle(conversationDetails: SmsConversationDetails, isNewChat: Boolean = false, isCallOptionsEnabled: Boolean = false, chatType: String = Enums.Chats.ConversationTypes.SMS): Bundle {
        val bundle = Bundle()

        bundle.putBoolean(Constants.Chats.PARAMS_IS_CALL_OPTIONS_DISABLED, isCallOptionsEnabled)
        bundle.putString(Constants.Chats.PARAMS_CHAT_TYPE, chatType)
        bundle.putBoolean(Constants.Chats.PARAMS_IS_NEW_CHAT, isNewChat)
        bundle.putString(Constants.Chats.PARAMS_SMS_CONVERSATION_DETAILS, GsonUtil.getJSON(conversationDetails))

        return bundle
    }

    @Test
    fun setup_oneToOneConversation_setsUpConversationDetailsCorrectly() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildOneToOneSmsMessage(externalParticipant1), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${externalParticipant1.phoneNumber},$ourNumber", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_groupConversation_setsUpConversationDetailsCorrectly() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildGroupSmsMessage(), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${externalParticipant1.phoneNumber},$ourNumber,${externalParticipant2.phoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_teamConversation_setsUpConversationDetailsCorrectly() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_twoTeamConversation_setsUpConversationDetailsCorrectly() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTwoTeamSmsMessage(), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber},${team2.teamPhoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_teamAndExternalParticipantConversation_setsUpConversationDetailsCorrectly() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamAndExternalSmsMessage(), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber},${externalParticipant1.phoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun existingConversation_numberSwitch_oneToOneWithTeammate_primaryToTeam() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildOneToOneSmsMessage(externalParticipant2), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("$ourNumber,${externalParticipant2.phoneNumber}", viewModel.conversationDetails?.getConversationId())

        viewModel.setSenderPhoneNumber(team1.teamPhoneNumber ?: "", team1.teamName, null, true)
        viewModel.conversationDetails?.participants?.addAll(team1Members)

        assertEquals(false, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun existingConversation_numberSwitch_oneToOneWithNonTeammate_primaryToTeam() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildOneToOneSmsMessage(externalParticipant1), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${externalParticipant1.phoneNumber},$ourNumber", viewModel.conversationDetails?.getConversationId())

        viewModel.setSenderPhoneNumber(team1.teamPhoneNumber ?: "", team1.teamName, null, true)
        viewModel.conversationDetails?.participants?.addAll(team1Members)

        assertEquals(false, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber},${externalParticipant1.phoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun existingConversation_ourUserAsExternalAndTeam_showsCorrectInitialConversation() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamAndOurNumberAsExternalSmsMessage(), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun newChat_numberSwitch_oneToOneWithTeammate_primaryToTeam() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildOneToOneSmsMessage(externalParticipant2), ourNumber, ourUuid), true), true)

        assertEquals(true, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("$ourNumber,${externalParticipant2.phoneNumber}", viewModel.conversationDetails?.getConversationId())

        viewModel.setSenderPhoneNumber(team1.teamPhoneNumber ?: "", team1.teamName, null, true)
        viewModel.conversationDetails?.participants?.addAll(team1Members)

        assertEquals(false, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun newChat_numberSwitch_oneToOneWithNonTeammate_primaryToTeam() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildOneToOneSmsMessage(externalParticipant1), ourNumber, ourUuid), true), true)

        assertEquals(true, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${externalParticipant1.phoneNumber},$ourNumber", viewModel.conversationDetails?.getConversationId())

        viewModel.setSenderPhoneNumber(team1.teamPhoneNumber ?: "", team1.teamName, null, true)
        viewModel.conversationDetails?.participants?.addAll(team1Members)

        assertEquals(false, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber},${externalParticipant1.phoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun newChat_ourUserAsExternalAndTeam_showsCorrectInitialConversation() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamAndOurNumberAsExternalSmsMessage(), ourNumber, ourUuid), true), true)

        assertEquals(true, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(team1.teamPhoneNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_oneToOneConversation_setsUpConversationDetailsCorrectly_teamSmsLicenseDisabled() {
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(false)

        viewModel.setup(getSetupBundle(SmsConversationDetails(buildOneToOneSmsMessage(externalParticipant1), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${externalParticipant1.phoneNumber},$ourNumber", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_groupConversation_setsUpConversationDetailsCorrectly_teamSmsLicenseDisabled() {
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(false)

        viewModel.setup(getSetupBundle(SmsConversationDetails(buildGroupSmsMessage(), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${externalParticipant1.phoneNumber},$ourNumber,${externalParticipant2.phoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_teamConversation_setsUpConversationDetailsCorrectly_teamSmsLicenseDisabled() {
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(false)

        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_twoTeamConversation_setsUpConversationDetailsCorrectly_teamSmsLicenseDisabled() {
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(false)

        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTwoTeamSmsMessage(), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber},${team2.teamPhoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun setup_teamAndExternalParticipantConversation_setsUpConversationDetailsCorrectly_teamSmsLicenseDisabled() {
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(false)

        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamAndExternalSmsMessage(), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber},${externalParticipant1.phoneNumber}", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun existingConversation_ourUserAsExternalAndTeam_showsCorrectInitialConversation_teamSmsLicenseDisabled() {
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(false)

        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamAndOurNumberAsExternalSmsMessage(), ourNumber, ourUuid)), true)

        assertEquals(false, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber},$ourNumber", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun newChat_ourUserAsExternalAndTeam_showsCorrectInitialConversation_teamSmsLicenseDisabled() {
        whenever(sessionManager.isTeamSmsLicenseEnabled).thenReturn(false)

        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamAndOurNumberAsExternalSmsMessage(), ourNumber, ourUuid), true), true)

        assertEquals(true, viewModel.conversationDetails?.isNewChat)
        assertEquals(true, viewModel.conversationDetails?.isInit)
        assertEquals(ourNumber, viewModel.conversationDetails?.getSendingPhoneNumber()?.phoneNumber)
        assertEquals("${team1.teamPhoneNumber},$ourNumber", viewModel.conversationDetails?.getConversationId())
    }

    @Test
    fun teamSms_getFromItemsForTeamSMSBanner_returnsOneItemForAdministration() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTeamSmsMessage(team1, team1Members), ourNumber, ourUuid)), true)

        val result = viewModel.getFromItemsForTeamSMSBanner()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("1 217-432-9596", result[0].text)
        assertEquals("Administration", result[0].label)
        assertEquals(true, result[0].isChecked)
        assertEquals(false, result[0].isSelected)
    }

    @Test
    fun twoTeamsSms_getToItemsForTeamSMSBanner_returnsZeroResultsSinceContactHasNoPhoneNumbers() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTwoTeamSmsMessage(), ourNumber, ourUuid)), true)

        val result = viewModel.getToItemsForTeamSMSBanner(team2Members)

        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun twoTeamSms_getToItemsForTeamSMSBanner_returnsTwoResultsSinceContactHasTwoPhoneNumbers() {
        viewModel.setup(getSetupBundle(SmsConversationDetails(buildTwoTeamSmsMessage(), ourNumber, ourUuid)), true)

        val result = viewModel.getToItemsForTeamSMSBanner(listOf(ourParticipantWithContacts))

        assertNotNull(result)
        assertEquals(2, result.size)

        assertEquals("(720) 310-3342", result[0].text)
        assertEquals("Administration", result[0].label)
        assertEquals(false, result[0].isChecked)
        assertEquals(false, result[0].isSelected)

        assertEquals("(720) 310-3346", result[1].text)
        assertEquals("Mirsada N", result[1].label)
        assertEquals(false, result[0].isChecked)
        assertEquals(false, result[0].isSelected)
    }

    @Test
    fun `deleteSingleMessage sets correct LiveData values and calls repository`() {
        val expectedProgressValue = R.string.progress_deleting
        whenever(conversationRepository.deleteMessage(any())).thenReturn(Single.just(true))
        viewModel.deleteMessage(
                SmsMessageListItem(
                        data = SmsMessage(messageId = "67d17139-5494-4f1a-b9af-5d9fac976297"),
                        humanReadableDatetime = "",
                        showTimeSeparator = false,
                        showHumanReadableTime = true,
                        humanReadableDate = "",
                        humanReadableTime = ""
                )
        )
        assertEquals(expectedProgressValue, viewModel.userRepoApiSmsStartedLiveData.value)
        Mockito.verify(conversationRepository).deleteMessage(any())
    }
}
