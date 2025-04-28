package com.nextiva.nextivaapp.android.viewmodels

import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.UserDetails
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
class AppPreferencesViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var sipManager: PJSipManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var conversationRepository: ConversationRepository

    private lateinit var viewModel: AppPreferenceViewModel

    override fun setup() {
        super.setup()

        hiltRule.inject()
        viewModel = AppPreferenceViewModel(ApplicationProvider.getApplicationContext(),
                settingsManager,
                logManager,
                dbManager,
                calendarManager,
                sipManager,
                sessionManager,
                conversationRepository)

        whenever(sessionManager.userDetails).thenReturn(UserDetails())
    }

    @Test
    fun setupLogger_callsToLogManager() {
        viewModel.setupLogger()

        verify(logManager).setupLogger()
    }

    @Test
    fun expireContactsCache_callsToDbManager() {
        viewModel.expireContactsCache()

        verify(dbManager).expireContactCache()
    }
    
    @Test
    fun enableLogging_callsToSettingsManager() {
        viewModel.enableLogging(true)

        verify(settingsManager).enableLogging = true
    }

    @Test
    fun enableFileLogging_callsToSettingsManager() {
        viewModel.enableFileLogging(true)

        verify(settingsManager).fileLogging = true
    }

    @Test
    fun enableXmppLogging_callsToSettingsManager() {
        viewModel.enableXmppLogging(true)

        verify(settingsManager).xmppLogging = true
    }

    @Test
    fun enableSipLogging_callsToSettingsManager() {
        viewModel.enableSipLogging(true)

        verify(settingsManager).sipLogging = true
    }

    @Test
    fun isLoggingEnabled_returnValueFromSettingsManager() {
        whenever(settingsManager.enableLogging).thenReturn(true)

        assertEquals(true, viewModel.isLoggingEnabled())
        verify(settingsManager).enableLogging
    }

    @Test
    fun isFileEnabled_returnValueFromSettingsManager() {
        whenever(settingsManager.fileLogging).thenReturn(true)

        assertEquals(true, viewModel.isFileLoggingEnabled())
        verify(settingsManager).fileLogging
    }

    @Test
    fun isXmppLoggingEnabled_returnValueFromSettingsManager() {
        whenever(settingsManager.xmppLogging).thenReturn(true)

        assertEquals(true, viewModel.isXmppLoggingEnabled())
        verify(settingsManager).xmppLogging
    }

    @Test
    fun deleteDirectory_checksForList_willDeleteDirectory() {
        val directory: File = mock()

        whenever(directory.isDirectory).thenReturn(true)
        whenever(directory.list()).thenReturn(null)

        viewModel.deleteDirectory(directory)

        verify(directory).list()
        verify(directory).delete()
    }
}