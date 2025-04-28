package com.nextiva.nextivaapp.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.Completable
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
class DashboardViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var sessionManager: SessionManager

    private val mockMarkAllMessagesReadObserver: Observer<Void> = mock()

    private lateinit var viewModel: DashboardViewModel

    override fun setup() {
        super.setup()
        hiltRule.inject()
        viewModel = DashboardViewModel(ApplicationProvider.getApplicationContext(),
                dbManager,
                sessionManager)

        viewModel.getCallLogEntriesMarkedReadLiveData().observeForever(mockMarkAllMessagesReadObserver)
    }

    @Test
    fun getNewVoicemailCount_callsToSessionManager() {
        whenever(sessionManager.newVoicemailMessagesCount).thenReturn(5)

        viewModel.getNewVoicemailCount()

        verify(sessionManager).newVoicemailMessagesCount
    }

    @Test
    fun getUnreadChatMessageCount_callsToDbManager() {
        val mockLiveData: LiveData<Int> = mock()
        whenever(dbManager.unreadChatMessagesCount).thenReturn(mockLiveData)

        viewModel.getUnreadChatMessageCount()

        verify(dbManager).unreadChatMessagesCount
    }

    @Test
    fun getUnreadCallLogEntriesCount_callsToDbManager() {
        val mockLiveData: LiveData<Int> = mock()
        whenever(dbManager.unreadCallLogEntriesCount).thenReturn(mockLiveData)

        viewModel.getUnreadCallLogEntriesCount()

        verify(dbManager).unreadCallLogEntriesCount
    }

    @Test
    fun markAllCallLogEntriesRead_callsToDbManager() {
        whenever(dbManager.markAllCallLogEntriesRead()).thenReturn(Completable.complete())

        viewModel.markAllCallLogEntriesRead()

        verify(mockMarkAllMessagesReadObserver).onChanged(anyOrNull())
        verify(dbManager).markAllCallLogEntriesRead()
    }
}