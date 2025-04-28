package com.nextiva.nextivaapp.android.fragments
//
//import android.content.Intent
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.SetCallSettingsActivity
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LocalSettingListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.constants.RequestCodes
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeCallHistoryListenerActivity
//import com.nextiva.nextivaapp.android.models.Resource
//import com.nextiva.nextivaapp.android.models.ServiceSettings
//import com.nextiva.nextivaapp.android.models.SingleEvent
//import com.nextiva.nextivaapp.android.viewmodels.CallSettingsListViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertTrue
//import org.junit.Test
//import org.robolectric.Shadows
//import org.robolectric.shadows.ShadowToast
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import javax.inject.Inject
//
//class CallSettingsListFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    @Inject
//    protected lateinit var dialogManager: DialogManager
//
//    @Inject
//    protected lateinit var analyticsManager: AnalyticsManager
//
//    private lateinit var fragment: CallSettingsListFragment
//
//    private val mockViewModel: CallSettingsListViewModel = mock()
//    private val mockListItemsListLiveData: LiveData<Resource<List<BaseListItem>>> = mock()
//    private val mockNavigateToSetCallSettingsIntentLiveData: LiveData<Resource<SingleEvent<Intent>>> = mock()
//    private val mockInternetConnectionErrorLiveData: LiveData<SingleEvent<Boolean>> = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(CallSettingsListViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.listItemsListLiveData).thenReturn(mockListItemsListLiveData)
//        whenever(mockViewModel.navigateToSetCallSettingsIntentLiveData).thenReturn(mockNavigateToSetCallSettingsIntentLiveData)
//        whenever(mockViewModel.internetConnectionErrorLiveData).thenReturn(mockInternetConnectionErrorLiveData)
//
//        fragment = CallSettingsListFragment.newInstance()
//        SupportFragmentTestUtil.startFragment(fragment, FakeCallHistoryListenerActivity::class.java)
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
//    fun onCreateView_observesViewModelLiveDatas() {
//        verify(mockListItemsListLiveData).observe(eq(fragment), any())
//        verify(mockNavigateToSetCallSettingsIntentLiveData).observe(eq(fragment), any())
//        verify(mockInternetConnectionErrorLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun onResume_callsToAnalyticsManager() {
//        verify(analyticsManager).logScreenView("call_settings_list_screen")
//    }
//
//    @Test
//    fun listItemsObserverValueUpdated_success_updatesRecyclerViewListItems() {
//        val recyclerView = fragment.view!!.findViewById<RecyclerView>(R.id.call_settings_recycler_view)
//
//        val listItemsList: ArrayList<BaseListItem> = ArrayList()
//        listItemsList.add(ServiceSettingsListItem(ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null), "Title", "Subtitle"))
//        listItemsList.add(ServiceSettingsListItem(ServiceSettings("Simultaneous Ring Personal", "uri", null, null, null, null, null, null, null, null, null, null), "Title", "Subtitle"))
//        listItemsList.add(ServiceSettingsListItem(ServiceSettings("Call Forwarding Always", "uri", null, null, null, null, null, null, null, null, null, null), "Title", "Subtitle"))
//
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<List<BaseListItem>>>> = argumentCaptor()
//        verify(mockListItemsListLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.success(listItemsList))
//        assertEquals(3, recyclerView.adapter!!.itemCount)
//    }
//
//    @Test
//    fun listItemsObserverValueUpdated_error_callsToDialogManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<List<BaseListItem>>>> = argumentCaptor()
//        verify(mockListItemsListLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.error("Message", null))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.CALL_SETTINGS_LIST)
//    }
//
//    @Test
//    fun navigateToSetCallSettingsObserverValueUpdated_success_navigatesToIntent() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<SingleEvent<Intent>>>> = argumentCaptor()
//        verify(mockNavigateToSetCallSettingsIntentLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        val intent = SetCallSettingsActivity.newIntent(fragment.context!!, Enums.Service.TYPE_DO_NOT_DISTURB)
//
//        argumentCaptor.firstValue.onChanged(Resource.success(SingleEvent(intent)))
//
//        val shadowActivity = Shadows.shadowOf(fragment.activity)
//        val actualIntent = shadowActivity.nextStartedActivityForResult
//
//        assertEquals(intent, actualIntent.intent)
//        assertEquals(RequestCodes.SET_SERVICE_SETTINGS_REQUEST_CODE, (actualIntent.requestCode and 0x0000ffff))
//    }
//
//    @Test
//    fun navigateToSetCallSettingsObserverValueUpdated_error_showsToast() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<SingleEvent<Intent>>>> = argumentCaptor()
//        verify(mockNavigateToSetCallSettingsIntentLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Resource.error("Message", null))
//
//        assertTrue(ShadowToast.showedToast("Unknown Call Setting"))
//    }
//
//    @Test
//    fun internetConnectionErrorObserverValueUpdated_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockInternetConnectionErrorLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.NO_INTERNET_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun internetConnectionErrorObserverValueUpdated_callsToDialogManagerToShowDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockInternetConnectionErrorLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.error_no_internet_title),
//                eq(R.string.error_no_internet_call_settings_message),
//                eq(R.string.general_ok),
//                any(),
//                eq(R.string.error_no_internet_settings_button),
//                buttonCaptor.capture())
//    }
//
//    @Test
//    fun internetConnectionErrorObserverValueUpdated_acceptInternetSettingsDialog_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockInternetConnectionErrorLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.error_no_internet_title),
//                eq(R.string.error_no_internet_call_settings_message),
//                eq(R.string.general_ok),
//                any(),
//                eq(R.string.error_no_internet_settings_button),
//                buttonCaptor.capture())
//
//        buttonCaptor.firstValue.onClick(MaterialDialog.Builder(fragment.activity!!).build(), DialogAction.NEGATIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.NO_INTERNET_DIALOG_SETTINGS_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun internetConnectionErrorObserverValueUpdated_acceptInternetSettingsDialog_callsToViewModelToNavigateToInternetSettings() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockInternetConnectionErrorLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.error_no_internet_title),
//                eq(R.string.error_no_internet_call_settings_message),
//                eq(R.string.general_ok),
//                any(),
//                eq(R.string.error_no_internet_settings_button),
//                buttonCaptor.capture())
//
//        buttonCaptor.firstValue.onClick(MaterialDialog.Builder(fragment.activity!!).build(), DialogAction.NEGATIVE)
//
//        verify(mockViewModel).navigateToInternetSettings(fragment.activity!!)
//    }
//
//    @Test
//    fun internetConnectionErrorObserverValueUpdated_cancelInternetSettingsDialog_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockInternetConnectionErrorLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.error_no_internet_title),
//                eq(R.string.error_no_internet_call_settings_message),
//                eq(R.string.general_ok),
//                buttonCaptor.capture(),
//                eq(R.string.error_no_internet_settings_button),
//                any())
//
//        buttonCaptor.firstValue.onClick(MaterialDialog.Builder(fragment.activity!!).build(), DialogAction.NEGATIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.NO_INTERNET_DIALOG_OK_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onActivityResult_callsToViewModel() {
//        val data = Intent()
//
//        fragment.onActivityResult(1, 2, data)
//
//        verify(mockViewModel).onActivityResult(1, 2, data)
//    }
//
//    @Test
//    fun getLayoutId_returnsCorrectLayoutId() {
//        assertEquals(R.layout.fragment_call_settings_list, fragment.layoutId)
//    }
//
//    @Test
//    fun getSwipeRefreshLayoutId_returnsCorrectSwipeRefreshLayoutId() {
//        assertEquals(R.id.call_settings_swipe_refresh_layout, fragment.swipeRefreshLayoutId)
//    }
//
//    @Test
//    fun getRecyclerViewId_returnsCorrectRecyclerViewId() {
//        assertEquals(R.id.call_settings_recycler_view, fragment.recyclerViewId)
//    }
//
//    @Test
//    fun getAnalyticsScreenName_returnsCorrectAnalyticsScreenName() {
//        assertEquals("call_settings_list_screen", fragment.analyticScreenName)
//    }
//
//    @Test
//    fun fetchItemList_callsToViewModel() {
//        reset(mockViewModel)
//
//        fragment.stopRefreshing()
//        fragment.fetchItemList(false)
//
//        verify(mockViewModel).getServiceSettings()
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_callsToViewModel() {
//        val serviceSettings = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(mockViewModel).onDetailItemViewListItemClicked(serviceSettingsListItem)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_nextivaAnywhereServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.NEXTIVA_ANYWHERE_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_simultaneousRingServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.SIMULTANEOUS_RING_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_remoteOfficeServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.REMOTE_OFFICE_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_callForwardingAlwaysServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.CALL_FORWARDING_ALWAYS_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_callForwardingWhenUnreachableServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.CALL_FORWARDING_WHEN_UNREACHABLE_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_callForwardingWhenUnansweredServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.CALL_FORWARDING_WHEN_UNANSWERED_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_callForwardingWhenBusyServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_BUSY, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.CALL_FORWARDING_WHEN_BUSY_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_doNotDisturbServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_DO_NOT_DISTURB, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.DO_NOT_DISTURB_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_blockMyCallerIdServiceSettings_callsToAnalyticsManager() {
//        val serviceSettings = ServiceSettings(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING, "uri", null, null, null, null, null, null, null, null, null, null)
//        val serviceSettingsListItem = ServiceSettingsListItem(serviceSettings, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(serviceSettingsListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.BLOCK_MY_CALLER_ID_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_dialingServiceServiceSettings_callsToAnalyticsManager() {
//        val localSettingListItem = LocalSettingListItem(SharedPreferencesManager.DIALING_SERVICE, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(localSettingListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.DIALING_SERVICE_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemClicked_thisPhoneNumberServiceSettings_callsToAnalyticsManager() {
//        val localSettingListItem = LocalSettingListItem(SharedPreferencesManager.THIS_PHONE_NUMBER, "Title", "Subtitle")
//
//        fragment.onDetailItemViewListItemClicked(localSettingListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.THIS_PHONE_NUMBER_LIST_ITEM_PRESSED)
//    }
//}