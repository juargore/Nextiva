///*
// * Copyright (c) 2017 Nextiva, Inc. to Present.
// * All rights reserved.
// */
//
package com.nextiva.nextivaapp.android.fragments
//
//import android.os.Bundle
//import android.view.View
//import android.widget.Spinner
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.adapters.CallHistoryListFilterAdapter
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeCallHistoryListenerActivity
//import com.nextiva.nextivaapp.android.models.CallLogEntry
//import com.nextiva.nextivaapp.android.models.Resource
//import com.nextiva.nextivaapp.android.view.EmptyStateView
//import com.nextiva.nextivaapp.android.view.TestMenuItem
//import com.nextiva.nextivaapp.android.viewmodels.CallHistoryListViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import org.mockito.ArgumentCaptor
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment
//import org.threeten.bp.Instant
//import java.io.IOException
//import javax.inject.Inject
//
//
//class CallHistoryListFragmentTest : BaseRobolectricTest() {
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
//    private lateinit var fragment: CallHistoryListFragment
//
//    private val mockFragmentListener: CallHistoryListFragment.CallHistoryListFragmentListener = mock()
//    private val mockViewModel: CallHistoryListViewModel = mock()
//    private val mockStopRefreshingLiveData: LiveData<Void> = mock()
//    private val mockBaseCallLogListItemsLiveData: LiveData<ArrayList<BaseListItem>> = mock()
//    private val mockDeleteAllCallLogEntriesLiveData: LiveData<Resource<Any>> = mock()
//    private val mockDeleteCallLogEntryLiveData: LiveData<Resource<Any>> = mock()
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
//        whenever(viewModelFactory.create(CallHistoryListViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.stopRefreshingLiveData).thenReturn(mockStopRefreshingLiveData)
//        whenever(mockViewModel.baseListItemsListLiveData).thenReturn(mockBaseCallLogListItemsLiveData)
//        whenever(mockViewModel.deleteAllCallLogEntriesLiveData).thenReturn(mockDeleteAllCallLogEntriesLiveData)
//        whenever(mockViewModel.deleteCallLogEntryLiveData).thenReturn(mockDeleteCallLogEntryLiveData)
//
//        fragment = CallHistoryListFragment.newInstance()
//        startFragment(fragment, FakeCallHistoryListenerActivity::class.java)
//        fragment.setFragmentListener(mockFragmentListener)
//
//        callLogEntry1 = CallLogEntry("1", "Display1", Instant.ofEpochSecond(1523661568L), "1", "2223334444", Enums.Calls.CallTypes.PLACED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0)
//        callLogEntry2 = CallLogEntry("2", "Display2", Instant.ofEpochSecond(1523896864L), "1", "3334445555", Enums.Calls.CallTypes.RECEIVED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0)
//        callLogEntry3 = CallLogEntry("3", "Display3", Instant.ofEpochSecond(1523914557L), "1", "4445556666", Enums.Calls.CallTypes.MISSED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0)
//
//        callHistoryListItem1 = CallHistoryListItem(callLogEntry1, true, true, null)
//        callHistoryListItem2 = CallHistoryListItem(callLogEntry2, true, true, null)
//        callHistoryListItem3 = CallHistoryListItem(callLogEntry3, true, true, null)
//
//        allCallListItems = arrayListOf(callHistoryListItem1, callHistoryListItem2, callHistoryListItem3)
//        missedCallListItems = arrayListOf(callHistoryListItem3)
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
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.ALL_CALL_LOGS_FILTER_SELECTED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.MISSED_CALL_LOGS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun getLayoutId_returnsCorrectLayoutId() {
//        assertEquals(R.layout.fragment_call_history_list, fragment.layoutId)
//    }
//
//    @Test
//    fun getSwipeRefreshLayoutId_returnsCorrectSwipeRefreshLayoutId() {
//        assertEquals(R.id.call_history_list_swipe_refresh_layout, fragment.swipeRefreshLayoutId)
//    }
//
//    @Test
//    fun getRecyclerViewId_returnsCorrectRecyclerViewId() {
//        assertEquals(R.id.call_history_list_recycler_view, fragment.recyclerViewId)
//    }
//
//    @Test
//    fun getEmptyStateViewId_returnsCorrectEmptyStateViewId() {
//        assertEquals(R.id.call_history_list_empty_state_view, fragment.emptyStateViewId)
//    }
//
//    @Test
//    fun getAnalyticsScreenName_returnsCorrectAnalyticsScreenName() {
//        assertEquals("call_history_list_screen", fragment.analyticScreenName)
//    }
//
//    @Test
//    fun fetchItemList_callsToViewModel() {
//        reset(mockViewModel)
//
//        fragment.stopRefreshing()
//        fragment.fetchItemList(false)
//
//        verify(mockViewModel).getCallLogEntries()
//    }
//
//    @Test
//    fun onCreateView_observesViewModelLiveDatas() {
//        verify(mockBaseCallLogListItemsLiveData).observe(eq(fragment), any())
//        verify(mockDeleteAllCallLogEntriesLiveData).observe(eq(fragment), any())
//        verify(mockDeleteCallLogEntryLiveData).observe(eq(fragment), any())
//        verify(mockStopRefreshingLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun getCallLogListItemsObserverValueUpdated_emptyInput_showsEmptyState() {
//        val emptyStateView = fragment.view!!.findViewById<EmptyStateView>(R.id.call_history_list_empty_state_view)
//        val recyclerView = fragment.view!!.findViewById<RecyclerView>(R.id.call_history_list_recycler_view)
//
//        populateList(CallHistoryListFilterAdapter.FILTER_ALL, ArrayList())
//
//        assertEquals(View.VISIBLE, emptyStateView.visibility)
//        assertEquals(View.GONE, recyclerView.visibility)
//    }
//
//    @Test
//    fun getCallLogListItemsObserverValueUpdated_populatedInput_allCallsFilter_showsRecyclerView() {
//        val emptyStateView = fragment.view!!.findViewById<EmptyStateView>(R.id.call_history_list_empty_state_view)
//        val recyclerView = fragment.view!!.findViewById<RecyclerView>(R.id.call_history_list_recycler_view)
//
//        populateList(CallHistoryListFilterAdapter.FILTER_ALL, allCallListItems)
//
//        assertEquals(View.GONE, emptyStateView.visibility)
//        assertEquals(View.VISIBLE, recyclerView.visibility)
//        assertEquals(3, recyclerView.adapter!!.itemCount)
//    }
//
//    @Test
//    fun getCallLogListItemsObserverValueUpdated_populatedInput_missedCallsFilter_showsRecyclerView() {
//        val emptyStateView = fragment.view!!.findViewById<EmptyStateView>(R.id.call_history_list_empty_state_view)
//        val recyclerView = fragment.view!!.findViewById<RecyclerView>(R.id.call_history_list_recycler_view)
//
//        populateList(CallHistoryListFilterAdapter.FILTER_MISSED, missedCallListItems)
//
//        assertEquals(View.GONE, emptyStateView.visibility)
//        assertEquals(View.VISIBLE, recyclerView.visibility)
//        assertEquals(1, recyclerView.adapter!!.itemCount)
//    }
//
//    @Test
//    fun deleteCallLogEntryObserverValueUpdated_loadingValue_callsToDialogManagerToShowProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Any>>> = argumentCaptor()
//        verify(mockDeleteCallLogEntryLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.loading(null))
//
//        verify(dialogManager).showProgressDialog(fragment.activity!!, Enums.Analytics.ScreenName.CALL_HISTORY_LIST, R.string.progress_processing)
//    }
//
//    @Test
//    fun deleteCallLogEntryObserverValueUpdated_successValue_callsToDialogManagerToDismissProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Any>>> = argumentCaptor()
//        verify(mockDeleteCallLogEntryLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.success(true))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun deleteCallLogEntryObserverValueUpdated_errorValue_callsToDialogManagerToDismissProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Any>>> = argumentCaptor()
//        verify(mockDeleteCallLogEntryLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.error("Message", null))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun deleteCallLogEntryObserverValueUpdated_errorValue_callsToDialogManagerToShowGenericErrorDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Any>>> = argumentCaptor()
//        verify(mockDeleteCallLogEntryLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.error("Message", null))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.CALL_HISTORY_LIST)
//    }
//
//    @Test
//    fun deleteAllCallLogEntriesObserverValueUpdated_loadingValue_callsToDialogManagerToShowProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Any>>> = argumentCaptor()
//        verify(mockDeleteAllCallLogEntriesLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.loading(null))
//
//        verify(dialogManager).showProgressDialog(fragment.activity!!, Enums.Analytics.ScreenName.CALL_HISTORY_LIST, R.string.progress_processing)
//    }
//
//    @Test
//    fun deleteAllCallLogEntriesObserverValueUpdated_successValue_callsToDialogManagerToDismissProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Any>>> = argumentCaptor()
//        verify(mockDeleteAllCallLogEntriesLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.success(true))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun deleteAllCallLogEntriesObserverValueUpdated_errorValue_callsToDialogManagerToDismissProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Any>>> = argumentCaptor()
//        verify(mockDeleteAllCallLogEntriesLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.error("Message", null))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun deleteAllCallLogEntriesObserverValueUpdated_errorValue_callsToDialogManagerToShowGenericErrorDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Any>>> = argumentCaptor()
//        verify(mockDeleteAllCallLogEntriesLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.error("Message", null))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.CALL_HISTORY_LIST)
//    }
//
//    @Test
//    fun deleteAllCalls_clickMenuItem_callsToDialogManager() {
//        fragment.onOptionsItemSelected(object : TestMenuItem() {
//            override fun getItemId(): Int {
//                return R.id.call_history_list_action_delete_call_history
//            }
//        })
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(0),
//                eq(R.string.call_history_list_delete_call_history_message),
//                eq(R.string.general_delete),
//                buttonCaptor.capture(),
//                eq(R.string.general_cancel),
//                any())
//    }
//
//    @Test
//    fun deleteAllCalls_clickMenuItem_callsToAnalyticsManager() {
//        fragment.onOptionsItemSelected(object : TestMenuItem() {
//            override fun getItemId(): Int {
//                return R.id.call_history_list_action_delete_call_history
//            }
//        })
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.DELETE_ALL_CALL_HISTORY_BUTTON_PRESSED)
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.DELETE_CALL_HISTORY_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun deleteAllCalls_acceptDialog_callsToAnalyticsManager() {
//        fragment.onOptionsItemSelected(object : TestMenuItem() {
//            override fun getItemId(): Int {
//                return R.id.call_history_list_action_delete_call_history
//            }
//        })
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(0),
//                eq(R.string.call_history_list_delete_call_history_message),
//                eq(R.string.general_delete),
//                buttonCaptor.capture(),
//                eq(R.string.general_cancel),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.DELETE_CALL_HISTORY_DIALOG_DELETE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun deleteAllCalls_acceptDialog_callsToViewModel() {
//        fragment.onOptionsItemSelected(object : TestMenuItem() {
//            override fun getItemId(): Int {
//                return R.id.call_history_list_action_delete_call_history
//            }
//        })
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(0),
//                eq(R.string.call_history_list_delete_call_history_message),
//                eq(R.string.general_delete),
//                buttonCaptor.capture(),
//                eq(R.string.general_cancel),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(mockViewModel).deleteAllCallLogs()
//    }
//
//    @Test
//    fun deleteAllCalls_declineDialog_callsToAnalyticsManager() {
//        fragment.onOptionsItemSelected(object : TestMenuItem() {
//            override fun getItemId(): Int {
//                return R.id.call_history_list_action_delete_call_history
//            }
//        })
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(0),
//                eq(R.string.call_history_list_delete_call_history_message),
//                eq(R.string.general_delete),
//                any(),
//                eq(R.string.general_cancel),
//                buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.NEGATIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.DELETE_CALL_HISTORY_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onSaveInstanceState_savesSelectedFilterOptionPosition() {
//        val bundle = Bundle()
//        fragment.onSaveInstanceState(bundle)
//
//        assertEquals(CallHistoryListFilterAdapter.FILTER_ALL, bundle.getInt("SELECTED_FILTER_OPTION_STATE"))
//    }
//
//    @Test
//    fun selectedAllFilter_observesViewModel() {
//        val spinner = fragment.view!!.findViewById<Spinner>(R.id.call_history_filter_spinner)
//        spinner.viewTreeObserver.dispatchOnGlobalLayout()
//        spinner.setSelection(CallHistoryListFilterAdapter.FILTER_ALL)
//
//        // Two times because it is called when the view is loaded and when we set the selection
//        verify(mockViewModel, times(2)).loadSelectedFilter(CallHistoryListFilterAdapter.FILTER_ALL)
//    }
//
//    @Test
//    fun selectedAllFilter_callsToAnalyticsManager() {
//        val spinner = fragment.view!!.findViewById<Spinner>(R.id.call_history_filter_spinner)
//        spinner.viewTreeObserver.dispatchOnGlobalLayout()
//        spinner.setSelection(CallHistoryListFilterAdapter.FILTER_ALL)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.ALL_CALL_LOGS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun selectedMissedFilter_observesViewModel() {
//        val spinner = fragment.view!!.findViewById<Spinner>(R.id.call_history_filter_spinner)
//        spinner.viewTreeObserver.dispatchOnGlobalLayout()
//        spinner.setSelection(CallHistoryListFilterAdapter.FILTER_MISSED)
//
//        verify(mockViewModel).loadSelectedFilter(CallHistoryListFilterAdapter.FILTER_MISSED)
//    }
//
//    @Test
//    fun selectedMissedFilter_callsToAnalyticsManager() {
//        val spinner = fragment.view!!.findViewById<Spinner>(R.id.call_history_filter_spinner)
//        spinner.viewTreeObserver.dispatchOnGlobalLayout()
//        spinner.setSelection(CallHistoryListFilterAdapter.FILTER_MISSED)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.MISSED_CALL_LOGS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun onCallHistoryListItemClicked_callsToFragmentListener() {
//        fragment.onCallHistoryListItemClicked(callHistoryListItem1)
//
//        verify(mockFragmentListener).onCallHistoryListItemClicked(fragment, callHistoryListItem1)
//    }
//
//    @Test
//    fun onCallHistoryListItemClicked_callsToAnalyticsManager() {
//        fragment.onCallHistoryListItemClicked(callHistoryListItem1)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_callsToDialogManager() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        val itemsListCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val listListenerCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq("Display1"),
//                itemsListCaptor.capture(),
//                listListenerCaptor.capture(),
//                any())
//
//        val itemsList = itemsListCaptor.firstValue
//        assertEquals("Video Call", itemsList[1])
//        assertEquals("Delete", itemsList[0])
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_callsToAnalyticsManager() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_LONG_PRESSED)
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_placeVideoOption_callsToViewModel() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        val itemsListCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val listListenerCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq("Display1"),
//                itemsListCaptor.capture(),
//                listListenerCaptor.capture(),
//                any())
//
//        listListenerCaptor.firstValue.onSelectionMade(1)
//
//        verify(mockViewModel).placeCall(fragment.activity!!, Enums.Analytics.ScreenName.CALL_HISTORY_LIST, callLogEntry1, Enums.Sip.CallTypes.VIDEO, Enums.Service.DialingServiceTypes.NONE)
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_placeVideoOption_callsToAnalyticsManager() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        val itemsListCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val listListenerCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq("Display1"),
//                itemsListCaptor.capture(),
//                listListenerCaptor.capture(),
//                any())
//
//        listListenerCaptor.firstValue.onSelectionMade(1)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_VIDEO_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_removeOption_callsToViewModel() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        val itemsListCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val listListenerCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq("Display1"),
//                itemsListCaptor.capture(),
//                listListenerCaptor.capture(),
//                any())
//
//        listListenerCaptor.firstValue.onSelectionMade(0)
//
//        verify(mockViewModel).deleteCallLog(Enums.Calls.CallTypes.PLACED, "1")
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_removeOption_callsToAnalyticsManager() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        val itemsListCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val listListenerCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq("Display1"),
//                itemsListCaptor.capture(),
//                listListenerCaptor.capture(),
//                any())
//
//        listListenerCaptor.firstValue.onSelectionMade(0)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_DELETE_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onCallHistoryListItemLongClicked_cancelOption_callsToAnalyticsManager() {
//        fragment.onCallHistoryListItemLongClicked(callHistoryListItem1)
//
//        val itemsListCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq("Display1"),
//                itemsListCaptor.capture(),
//                any(),
//                buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_OPTIONS_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onQueryTextSubmit_callsToViewModelToUpdateSearchTerm() {
//        fragment.onQueryTextSubmit("Search")
//
//        verify(mockViewModel).onSearchTermUpdated("Search")
//    }
//
//    @Test
//    fun onQueryTextChange_callsToViewModelToUpdateSearchTerm() {
//        fragment.onQueryTextChange("Search")
//
//        verify(mockViewModel).onSearchTermUpdated("Search")
//    }
//
//    @Test
//    fun onCallHistoryCallButtonClicked_callsToViewModel() {
//        fragment.onCallHistoryCallButtonClicked(callHistoryListItem1)
//
//        verify(mockViewModel).placeCall(fragment.activity!!, Enums.Analytics.ScreenName.CALL_HISTORY_LIST, callLogEntry1, Enums.Sip.CallTypes.VOICE, Enums.Service.DialingServiceTypes.NONE)
//    }
//
//    @Test
//    fun onCallHistoryCallButtonClicked_callsToAnalyticsManager() {
//        fragment.onCallHistoryCallButtonClicked(callHistoryListItem1)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_HISTORY_LIST, Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED)
//    }
//
//    private fun populateList(selectedFilter: Int, listItems: ArrayList<BaseListItem>) {
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<BaseListItem>>> = argumentCaptor()
//        // Two times because this was observed when the fragment was started
//        verify(mockBaseCallLogListItemsLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(listItems)
//
//        val spinner = fragment.view!!.findViewById<Spinner>(R.id.call_history_filter_spinner)
//        spinner.setSelection(selectedFilter)
//    }
//}