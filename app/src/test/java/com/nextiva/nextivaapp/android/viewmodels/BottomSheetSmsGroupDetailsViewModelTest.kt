package com.nextiva.nextivaapp.android.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.mocks.values.BottomSheetSmsDetailsMock
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.util.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.whenever
import org.robolectric.annotation.LooperMode
import javax.inject.Inject
import kotlin.math.abs

@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
@LooperMode(LooperMode.Mode.PAUSED)
internal class BottomSheetSmsGroupDetailsViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var callManager: CallManager

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var sipManager: PJSipManager

    @Inject
    lateinit var platformContactsRepository: PlatformContactsRepository

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var scheduleProvider: SchedulerProvider

    private lateinit var viewModel: BottomSheetSmsGroupDetailsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    override fun setup() {
        super.setup()

        hiltRule.inject()

        Dispatchers.setMain(testDispatcher)

        whenever(dbManager.getConnectContactFromPhoneNumberInThread("16232239093")).thenReturn(
            BottomSheetSmsDetailsMock().dbResponse[0]
        )
        whenever(dbManager.getConnectContactFromPhoneNumberInThread("19143390856")).thenReturn(
            BottomSheetSmsDetailsMock().dbResponse[1]
        )
        whenever(dbManager.getConnectContactFromPhoneNumberInThread("19702872263")).thenReturn(
            BottomSheetSmsDetailsMock().dbResponse[2]
        )
        whenever(dbManager.getConnectContactFromPhoneNumberInThread("17203103342")).thenReturn(
            BottomSheetSmsDetailsMock().dbResponse[3]
        )
        whenever(dbManager.getConnectContactFromPhoneNumberInThread("12174329604")).thenReturn(
            BottomSheetSmsDetailsMock().dbResponse[4]
        )

        viewModel = BottomSheetSmsGroupDetailsViewModel(
            application = ApplicationProvider.getApplicationContext(),
            nextivaApplication = ApplicationProvider.getApplicationContext(),
            dbManager = dbManager,
            sessionManager = sessionManager,
            schedulerProvider = scheduleProvider
        )
    }

    @Test
    fun testConversationContacts() = runTest {
        val conversationDetails = BottomSheetSmsDetailsMock().conversationDetails
        viewModel.getPreloadContacts(conversationDetails = conversationDetails)
        val result = viewModel.preload.getOrAwaitValue()
        val participantSize =
            abs(conversationDetails.participants?.size.orZero() - conversationDetails.teamMembers?.size.orZero())
        val expectedSize = participantSize + conversationDetails.teams?.size.orZero()
        assertEquals(result.size, expectedSize)
    }
}