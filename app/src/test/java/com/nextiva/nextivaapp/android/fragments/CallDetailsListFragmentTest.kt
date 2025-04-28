package com.nextiva.nextivaapp.android.fragments
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailPhoneNumberListItem
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.models.CallLogEntry
//import com.nextiva.nextivaapp.android.viewmodels.CallDetailsViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import org.threeten.bp.Instant
//import javax.inject.Inject
//
//class CallDetailsListFragmentTest : BaseRobolectricTest() {
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
//    private lateinit var fragment: CallDetailsListFragment
//
//    private lateinit var callLogEntry1: CallLogEntry
//
//    private val mockViewModel: CallDetailsViewModel = mock()
//    private val mockBaseListItemsListLiveData: LiveData<ArrayList<BaseListItem>> = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(CallDetailsViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.baseListItemsListLiveData).thenReturn(mockBaseListItemsListLiveData)
//
//        callLogEntry1 = CallLogEntry("1", "Display1", Instant.ofEpochSecond(1523661568L), "1", "2223334444", Enums.Calls.CallTypes.PLACED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "Test", 0)
//
//        fragment = CallDetailsListFragment.newInstance(callLogEntry1)
//        SupportFragmentTestUtil.startFragment(fragment)
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
//    fun newInstance_correctlySetsArguments() {
//        val intent = CallDetailsListFragment.newInstance(callLogEntry1)
//
//        assertEquals(callLogEntry1, intent.arguments!!.getSerializable("PARAMS_CALL_LOG_ENTRY"))
//    }
//
//    @Test
//    fun onCreateView_observesViewModelLiveDatas() {
//        verify(mockBaseListItemsListLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun onCreateView_callsToViewModelToSetCallLogEntry() {
//        verify(mockViewModel).setCallLogEntry(callLogEntry1)
//    }
//
//    @Test
//    fun getLayoutId_returnsCorrectLayoutId() {
//        assertEquals(R.layout.fragment_call_details_list, fragment.layoutId)
//    }
//
//    @Test
//    fun getSwipeRefreshLayoutId_returnsCorrectSwipeRefreshLayoutId() {
//        assertEquals(0, fragment.swipeRefreshLayoutId)
//    }
//
//    @Test
//    fun getRecyclerViewId_returnsCorrectRecyclerViewId() {
//        assertEquals(R.id.call_details_list_recycler_view, fragment.recyclerViewId)
//    }
//
//    @Test
//    fun getAnalyticsScreenName_returnsCorrectAnalyticsScreenName() {
//        assertEquals("call_details_screen", fragment.analyticScreenName)
//    }
//
//    @Test
//    fun fetchItemList_callsToViewModel() {
//        reset(mockViewModel)
//
//        fragment.stopRefreshing()
//        fragment.fetchItemList(false)
//
//        verify(mockViewModel).populateAdapter()
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction1ButtonClicked_phoneNumberListItemWithSubTitle_callsToViewModelToPlaceCall() {
//        fragment.onDetailItemViewListItemAction1ButtonClicked(CallDetailPhoneNumberListItem("Title", "2223334444", 0, 0))
//
//        verify(mockViewModel).placeCall(fragment.activity!!, Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Sip.CallTypes.VOICE)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction1ButtonClicked_phoneNumberListItemWithSubTitle_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction1ButtonClicked(CallDetailPhoneNumberListItem("Title", "2223334444", 0, 0))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_phoneNumberListItemWithSubTitle_callsToViewModelToPlaceCall() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(CallDetailPhoneNumberListItem("Title", "2223334444", 0, 0))
//
//        verify(mockViewModel).placeCall(fragment.activity!!, Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Sip.CallTypes.VIDEO)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_phoneNumberListItemWithSubTitle_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(CallDetailPhoneNumberListItem("Title", "2223334444", 0, 0))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_VIDEO_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToContactsListItem_callsToViewModelToAddToRosterContacts() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(CallDetailListItem(Enums.Calls.DetailViewTypes.ADD_TO_CONTACTS, "Title", "2223334444", 0, 0, false))
//
//        verify(mockViewModel).addToRosterContacts()
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToContactsListItem_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(CallDetailListItem(Enums.Calls.DetailViewTypes.ADD_TO_CONTACTS, "Title", "2223334444", 0, 0, false))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.ADD_TO_CONTACTS_LIST_ITEM_ADD_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToLocalContactsListItem_callsToViewModelToAddToLocalContacts() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(CallDetailListItem(Enums.Calls.DetailViewTypes.ADD_TO_LOCAL_CONTACT, "Title", "2223334444", 0, 0, false))
//
//        verify(mockViewModel).addToLocalContacts(fragment.activity!!, Enums.Analytics.ScreenName.CALL_DETAILS)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToLocalContactsListItem_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(CallDetailListItem(Enums.Calls.DetailViewTypes.ADD_TO_LOCAL_CONTACT, "Title", "2223334444", 0, 0, false))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.ADD_TO_LOCAL_CONTACTS_LIST_ITEM_ADD_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_sendPersonalSmsListItem_callsToViewModelToSendPersonalSms() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(CallDetailListItem(Enums.Calls.DetailViewTypes.SEND_PERSONAL_SMS, "Title", "2223334444", 0, 0, false))
//
//        verify(mockViewModel).sendPersonalSms(fragment.activity!!, Enums.Analytics.ScreenName.CALL_DETAILS)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_sendPersonalSmsListItem_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(CallDetailListItem(Enums.Calls.DetailViewTypes.SEND_PERSONAL_SMS, "Title", "2223334444", 0, 0, false))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.SEND_SMS_LIST_ITEM_SEND_SMS_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_phoneNumberListItem_callsToViewModelToCopyText() {
//        fragment.onDetailItemViewListItemLongClicked(CallDetailPhoneNumberListItem("Title", "2223334444", 0, 0))
//
//        verify(mockViewModel).copyPhoneNumber("Title", "2223334444")
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_phoneNumberListItem_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemLongClicked(CallDetailPhoneNumberListItem("Title", "2223334444", 0, 0))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_COPIED)
//    }
//}