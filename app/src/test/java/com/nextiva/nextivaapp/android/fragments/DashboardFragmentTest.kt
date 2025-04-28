package com.nextiva.nextivaapp.android.fragments
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.adapters.DashboardViewPagerAdapter
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.PollingManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.viewmodels.DashboardViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentController
//import javax.inject.Inject
//
//class DashboardFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    @Inject
//    lateinit var pollingManager: PollingManager
//
//    private lateinit var fragment: DashboardFragment
//    private lateinit var fragmentController: SupportFragmentController<DashboardFragment>
//
//    private val mockViewModel: DashboardViewModel = mock()
//    private val mockMarkCallLogEntriesReadLiveData: LiveData<Void> = mock()
//    private val mockUnreadCallLogEntriesLiveData: LiveData<Int> = mock()
//    private val mockUnreadChatMessageCountLiveData: LiveData<Int> = mock()
//    private val mockPollingCompleteLiveData: LiveData<Boolean> = mock()
//    private val mockNewVoicemailCountLiveData: LiveData<Int> = mock()
//
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(DashboardViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.getCallLogEntriesMarkedReadLiveData()).thenReturn(mockMarkCallLogEntriesReadLiveData)
//        whenever(mockViewModel.getUnreadCallLogEntriesCount()).thenReturn(mockUnreadCallLogEntriesLiveData)
//        whenever(mockViewModel.getUnreadChatMessageCount()).thenReturn(mockUnreadChatMessageCountLiveData)
//        whenever(pollingManager.pollingCompleteLiveData).thenReturn(mockPollingCompleteLiveData)
//
//        whenever(mockViewModel.getNewVoicemailCountLiveData()).thenReturn(mockNewVoicemailCountLiveData)
//
//        fragment = DashboardFragment.newInstance(DashboardViewPagerAdapter.CONTACTS_LIST_TAB_INDEX)
//        fragmentController = SupportFragmentController.of(fragment).create(null).start().resume()
//    }
//
//    override fun after() {
//        super.after()
//        fragment.onPause()
//        fragment.onStop()
//        fragment.onDestroy()
//    }
//
//    @Test
//    fun onCreate_setsUp() {
//        verify(mockViewModel).getCallLogEntriesMarkedReadLiveData()
//        verify(pollingManager).pollingCompleteLiveData
//        verify(mockViewModel).getUnreadCallLogEntriesCount()
//        verify(mockViewModel, times(2)).getUnreadChatMessageCount()
//        verify(mockUnreadChatMessageCountLiveData).value
//
//        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.CONTACTS_LIST)
//    }
//
//    @Test
//    fun onPageSelected_selectsDialerTab_logsScreenView() {
//        fragment.onPageSelected(DashboardViewPagerAdapter.DIALER_TAB_INDEX)
//
//        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.DIALER)
//    }
//
//    @Test
//    fun onPageSelected_selectsChatsListTab_logsScreenView() {
//        fragment.onPageSelected(DashboardViewPagerAdapter.CHATS_LIST_TAB_INDEX)
//
//        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.CHATS_LIST)
//    }
//
//    @Test
//    fun onPageSelected_selectsCallHistoryTab_logsScreenView() {
//        fragment.onPageSelected(DashboardViewPagerAdapter.CALL_HISTORY_LIST_TAB_INDEX)
//
//        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.CALL_HISTORY_LIST)
//    }
//
//    @Test
//    fun onPageSelected_selectsContactsTab_logsScreenView() {
//        reset(analyticsManager)
//
//        fragment.onPageSelected(DashboardViewPagerAdapter.DIALER_TAB_INDEX)
//        fragment.onPageSelected(DashboardViewPagerAdapter.CONTACTS_LIST_TAB_INDEX)
//
//        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.CONTACTS_LIST)
//    }
//
//    @Test
//    fun onPageSelected_selectsTabFromCallHistory_marksCallLogsRead() {
//        fragment.onPageSelected(DashboardViewPagerAdapter.CALL_HISTORY_LIST_TAB_INDEX)
//        fragment.onPageSelected(DashboardViewPagerAdapter.CONTACTS_LIST_TAB_INDEX)
//
//        verify(mockViewModel).markAllCallLogEntriesRead()
//    }
//}