///*
// * Copyright (c) 2017 Nextiva, Inc. to Present.
// * All rights reserved.
// */
//
package com.nextiva.nextivaapp.android.fragments
//
//import android.widget.Spinner
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.adapters.CallHistoryListFilterAdapter
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeCallHistoryListenerActivity
//import com.nextiva.nextivaapp.android.models.CallLogEntry
//import com.nextiva.nextivaapp.android.models.Resource
//import com.nextiva.nextivaapp.android.viewmodels.NewCallCallHistoryListViewModel
//import com.nhaarman.mockito_kotlin.mock
//import com.nhaarman.mockito_kotlin.whenever
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.CoreMatchers.instanceOf
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import org.mockito.ArgumentMatchers.any
//import org.mockito.Mockito.times
//import org.mockito.Mockito.verify
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment
//import org.threeten.bp.Instant
//import java.io.IOException
//import javax.inject.Inject
//
//class NewCallCallHistoryListFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    @Inject
//    lateinit var dialogManager: DialogManager
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    private lateinit var fragment: NewCallCallHistoryListFragment
//
//    private val mockFragmentListener: CallHistoryListFragment.CallHistoryListFragmentListener = mock()
//    private val mockViewModel: NewCallCallHistoryListViewModel = mock()
//    private val mockStopRefreshingLiveData: LiveData<Void> = mock()
//    private val mockAllCallLogListItemsLiveData: LiveData<ArrayList<BaseListItem>> = mock()
//    private val mockMissedCallLogListItemsLiveData: LiveData<ArrayList<BaseListItem>> = mock()
//    private val mockDeleteAllCallLogEntriesLiveData: LiveData<Resource<Any>> = mock()
//    private val mockDeleteCallLogEntryLiveData: LiveData<Resource<Any>> = mock()
//    private val mockBaseListItemsLiveData: LiveData<ArrayList<BaseListItem>> = mock()
//
//    private lateinit var callLogEntry1: CallLogEntry
//    private lateinit var callLogEntry2: CallLogEntry
//    private lateinit var callLogEntry3: CallLogEntry
//    private lateinit var callHistoryListItem1: CallHistoryListItem
//    private lateinit var callHistoryListItem2: CallHistoryListItem
//    private lateinit var callHistoryListItem3: CallHistoryListItem
//    private lateinit var allCallListItems: ArrayList<BaseListItem>
//    private lateinit var missedCallListItems: ArrayList<BaseListItem>
//
//    @Throws(IOException::class)
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(NewCallCallHistoryListViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.stopRefreshingLiveData).thenReturn(mockStopRefreshingLiveData)
//        whenever(mockViewModel.allCallLogListItemsLiveData).thenReturn(mockAllCallLogListItemsLiveData)
//        whenever(mockViewModel.missedCallLogListItemsLiveData).thenReturn(mockMissedCallLogListItemsLiveData)
//        whenever(mockViewModel.deleteAllCallLogEntriesLiveData).thenReturn(mockDeleteAllCallLogEntriesLiveData)
//        whenever(mockViewModel.deleteCallLogEntryLiveData).thenReturn(mockDeleteCallLogEntryLiveData)
//        whenever(mockViewModel.baseListItemsListLiveData).thenReturn(mockBaseListItemsLiveData)
//
//        fragment = NewCallCallHistoryListFragment.newInstance()
//        startFragment(fragment, FakeCallHistoryListenerActivity::class.java)
//        fragment.setFragmentListener(mockFragmentListener)
//
//        callLogEntry1 = CallLogEntry("1", "Display1", Instant.ofEpochSecond(1523914557L), "1", "2223334444", Enums.Calls.CallTypes.PLACED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0)
//        callLogEntry2 = CallLogEntry("2", "Display2", Instant.ofEpochSecond(1523896864L), "1", "3334445555", Enums.Calls.CallTypes.RECEIVED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0)
//        callLogEntry3 = CallLogEntry("3", "Display3", Instant.ofEpochSecond(1523661568L), "1", "4445556666", Enums.Calls.CallTypes.MISSED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0)
//
//        callHistoryListItem1 = CallHistoryListItem(callLogEntry1, true, true, null)
//        callHistoryListItem2 = CallHistoryListItem(callLogEntry2, true, true, null)
//        callHistoryListItem3 = CallHistoryListItem(callLogEntry3, true, true, null)
//
//        allCallListItems = ArrayList()
//        missedCallListItems = ArrayList()
//
//        allCallListItems.add(callHistoryListItem1)
//        allCallListItems.add(callHistoryListItem2)
//        allCallListItems.add(callHistoryListItem3)
//
//        missedCallListItems.add(callHistoryListItem3)
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
//    fun onCreateView_doesNotCallToAnalyticsManager() {
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.NEW_CALL_CALL_HISTORY_LIST, Enums.Analytics.EventName.ALL_CALL_LOGS_FILTER_SELECTED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.NEW_CALL_CALL_HISTORY_LIST, Enums.Analytics.EventName.MISSED_CALL_LOGS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun onCreate_setsCorrectViewModelClass() {
//        assertThat(fragment.mViewModel, instanceOf(NewCallCallHistoryListViewModel::class.java))
//    }
//
//    @Test
//    fun getAnalyticsScreenName_returnsCorrectAnalyticsScreenName() {
//        assertEquals("new_call_call_history_list_screen", fragment.analyticScreenName)
//    }
//
//    @Test
//    fun selectedAllFilter_callsToAnalyticsManager() {
//        val spinner = fragment.view!!.findViewById<Spinner>(R.id.call_history_filter_spinner)
//        spinner.viewTreeObserver.dispatchOnGlobalLayout()
//        spinner.setSelection(CallHistoryListFilterAdapter.FILTER_ALL)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.NEW_CALL_CALL_HISTORY_LIST, Enums.Analytics.EventName.ALL_CALL_LOGS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun selectedMissedFilter_callsToAnalyticsManager() {
//        val spinner = fragment.view!!.findViewById<Spinner>(R.id.call_history_filter_spinner)
//        spinner.viewTreeObserver.dispatchOnGlobalLayout()
//        spinner.setSelection(CallHistoryListFilterAdapter.FILTER_MISSED)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.NEW_CALL_CALL_HISTORY_LIST, Enums.Analytics.EventName.MISSED_CALL_LOGS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun onCallHistoryListItemClicked_callsToAnalyticsManager() {
//        fragment.onCallHistoryListItemClicked(callHistoryListItem1)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.NEW_CALL_CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_doesNotCallToAnalyticsManager() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.NEW_CALL_CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_LONG_PRESSED)
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_doesNotShowDialog() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        verify(dialogManager, times(0)).showSimpleListDialog(any(), any(), any(), any(), any())
//    }
//}
