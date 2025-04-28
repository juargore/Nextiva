package com.nextiva.nextivaapp.android.viewmodels

import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.ContactQuery
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
internal class BottomSheetContactSelectionViewModelTest : BaseRobolectricTest() {

    @Inject
    lateinit var dbManager: DbManager

    private lateinit var viewModel: BottomSheetContactSelectionViewModel

    @Inject
    lateinit var platformContactsRepository: PlatformContactsRepository

    @Inject
    lateinit var scheduleProvider: SchedulerProvider

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferencesManager

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    override fun setup() {
        super.setup()

        hiltRule.inject()

        viewModel = BottomSheetContactSelectionViewModel(
            application = ((ApplicationProvider.getApplicationContext())),
            dbManager = dbManager,
            platformContactsRepository = platformContactsRepository,
            schedulerProvider = scheduleProvider,
            sharedPreferencesManager = sharedPreferenceManager
        )
    }

    @Test
    fun `queryUpdated emits ContactQuery with RecentContacts state when query is empty`() = runTest {
        val expectedFilter = intArrayOf(
            Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
            Enums.Contacts.ContactTypes.CONNECT_SHARED,
            Enums.Contacts.ContactTypes.CONNECT_USER,
            Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS
        )

        val expectedQuery = ContactQuery("", expectedFilter, ContactQuery.PositionState.RecentContacts)
        launch { viewModel.queryUpdated("")}

        advanceUntilIdle()
        val result = viewModel.query.first().state.name
        assertEquals(expectedQuery.state.name, result)
    }

    @Test
    fun `queryUpdated emits ContactQuery with Search state when query is not empty`() = runTest {
        // Given
        val expectedFilter = intArrayOf(
            Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
            Enums.Contacts.ContactTypes.CONNECT_SHARED,
            Enums.Contacts.ContactTypes.CONNECT_USER,
            Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS
        )
        val expectedQuery = ContactQuery("", expectedFilter, ContactQuery.PositionState.Search)

        // When
        launch { viewModel.queryUpdated("Anjan")}
        advanceUntilIdle()

        // Then
        val result = viewModel.query.first().state.name
        assertEquals(expectedQuery.state.name, result)
    }
}