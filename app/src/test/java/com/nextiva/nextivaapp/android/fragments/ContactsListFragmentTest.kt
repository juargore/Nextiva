package com.nextiva.nextivaapp.android.fragments
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.paging.PagedList
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.constants.RequestCodes
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeContactsListListenerActivity
//import com.nextiva.nextivaapp.android.models.ListHeaderRow
//import com.nextiva.nextivaapp.android.models.NextivaContact
//import com.nextiva.nextivaapp.android.models.SingleEvent
//import com.nextiva.nextivaapp.android.net.buses.RxEvents
//import com.nextiva.nextivaapp.android.viewmodels.ContactsListViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.*
//import org.junit.Test
//import org.robolectric.Shadows
//import org.robolectric.fakes.RoboMenuItem
//import org.robolectric.shadows.ShadowLooper
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import javax.inject.Inject
//
//class ContactsListFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    @Inject
//    lateinit var dialogManager: DialogManager
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//    @Inject
//    lateinit var connectionStateManager: ConnectionStateManager
//
//    private lateinit var fragment: ContactsListFragment
//
//    private val mockViewModel: ContactsListViewModel = mock()
//    private val mockBaseListItemsListLiveData: LiveData<ArrayList<BaseListItem>> = mock()
//    private val mockContactListItemsPagedListLiveData: LiveData<PagedList<ContactListItem>> = mock()
//    private val mockSelectedFilterTypeLiveData: LiveData<Int> = mock()
//    private val mockPermissionRequiredStateVisibleLiveData: LiveData<Boolean> = mock()
//    private val mockRefreshingLiveData: LiveData<SingleEvent<Boolean>> = mock()
//    private val mockErrorLiveData: LiveData<SingleEvent<Boolean>> = mock()
//    private val mockContactUpdatedLiveData: LiveData<SingleEvent<Boolean>> = mock()
//    private val mockXmppErrorEventLiveData: LiveData<SingleEvent<RxEvents.XmppErrorEvent>> = mock()
//    private val mockFragmentListener: ContactsListFragment.ContactsListFragmentListener = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(ContactsListViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.baseListItemsListLiveData).thenReturn(mockBaseListItemsListLiveData)
//        whenever(mockViewModel.contactListItemsPagedListLiveData).thenReturn(mockContactListItemsPagedListLiveData)
//        whenever(mockViewModel.selectedFilterTypeLiveData).thenReturn(mockSelectedFilterTypeLiveData)
//        whenever(mockViewModel.permissionRequiredStateVisibleLiveData).thenReturn(mockPermissionRequiredStateVisibleLiveData)
//        whenever(mockViewModel.refreshingLiveData).thenReturn(mockRefreshingLiveData)
//        whenever(mockViewModel.errorLiveData).thenReturn(mockErrorLiveData)
//        whenever(mockViewModel.xmppErrorEventLiveData).thenReturn(mockXmppErrorEventLiveData)
//        whenever(mockViewModel.contactUpdatedLiveData).thenReturn(mockContactUpdatedLiveData)
//        whenever(connectionStateManager.umsConnectionStateLiveData).thenReturn(mock())
//
//        fragment = ContactsListFragment.newInstance()
//        SupportFragmentTestUtil.startFragment(fragment, FakeContactsListListenerActivity::class.java)
//        fragment.mFragmentListener = mockFragmentListener
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
//    fun getLayoutId_returnsCorrectLayoutId() {
//        assertEquals(R.layout.fragment_contacts_list, fragment.layoutId)
//    }
//
//    @Test
//    fun getSwipeRefreshLayoutId_returnsCorrectSwipeRefreshLayoutId() {
//        assertEquals(R.id.contacts_list_swipe_refresh_layout, fragment.swipeRefreshLayoutId)
//    }
//
//    @Test
//    fun getRecyclerViewId_returnsCorrectRecyclerViewId() {
//        assertEquals(R.id.contacts_list_recycler_view, fragment.recyclerViewId)
//    }
//
//    @Test
//    fun getEmptyStateViewId_returnsCorrectEmptyStateViewId() {
//        assertEquals(R.id.contacts_list_empty_state_view, fragment.emptyStateViewId)
//    }
//
//    @Test
//    fun getAnalyticsScreenName_returnsCorrectAnalyticsScreenName() {
//        assertEquals("contacts_list_screen", fragment.analyticScreenName)
//    }
//
//    @Test
//    fun fetchItemList_callsToViewModel() {
//        reset(mockViewModel)
//
//        fragment.stopRefreshing()
//        fragment.fetchItemList(false)
//
//        verify(mockViewModel).fetchContacts(fragment.activity!!, Enums.Analytics.ScreenName.CONTACTS_LIST, false)
//    }
//
//    @Test
//    fun showEmptyState_hidesPermissionRequiredLayout() {
//        fragment.mPermissionRequiredLayout.visibility = View.VISIBLE
//        assertEquals(View.VISIBLE, fragment.mPermissionRequiredLayout.visibility)
//
//        fragment.showEmptyState()
//
//        assertEquals(View.GONE, fragment.mPermissionRequiredLayout.visibility)
//    }
//
//    @Test
//    fun showRecyclerView_hidesPermissionRequiredLayout() {
//        fragment.mPermissionRequiredLayout.visibility = View.VISIBLE
//        assertEquals(View.VISIBLE, fragment.mPermissionRequiredLayout.visibility)
//
//        fragment.showRecyclerView()
//
//        assertEquals(View.GONE, fragment.mPermissionRequiredLayout.visibility)
//    }
//
//    @Test
//    fun onCreateView_observesViewModelLiveDatas() {
//        verify(mockSelectedFilterTypeLiveData).observe(eq(fragment), any())
//        verify(mockBaseListItemsListLiveData).observe(eq(fragment), any())
//        verify(mockPermissionRequiredStateVisibleLiveData).observe(eq(fragment), any())
//        verify(mockRefreshingLiveData).observe(eq(fragment), any())
//        verify(mockErrorLiveData).observe(eq(fragment), any())
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun onCreateView_setsDefaultSelectedFilterType() {
//        verify(mockViewModel).loadSelectedFilter(Enums.Contacts.FilterTypes.ROSTER_ALL)
//    }
//
//    @Test
//    fun onCreateView_doesNotCallToAnalyticsManager() {
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.MY_CONTACTS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun onCreateView_setsPermissionRequiredText() {
//        assertEquals("In order to see your local contacts, touch the button below to give permission for " + (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.app_name) + " to get your contacts from your phone.",
//                fragment.mPermissionRequiredTextView.text.toString())
//    }
//
//    //TODO Need to determine how to see a saved instance state to test this
////    @Test
////    fun onCreateView_withSavedInstanceState_setsCorrectSelectedFilterType() {
////        whenever(mockSelectedFilterTypeLiveData.value).thenReturn(Enums.Contacts.FILTER_TYPE_DIRECTORY)
////
////        val saveBundle = Bundle()
////        saveBundle.putInt("SELECTED_FILTER_OPTION_TYPE", Enums.Contacts.FILTER_TYPE_DIRECTORY)
////
////        val activity = buildActivity(FakeContactsListListenerActivity::class.java).create(saveBundle).start().resume().get()
////
////        val fragment = ContactsListFragment.newInstance()
////
////        val fragmentManager = activity.supportFragmentManager
////        val fragmentTransaction = fragmentManager.beginTransaction()
////        fragmentTransaction.add(fragment, null)
////        fragmentTransaction.commit()
////
////        val argumentCaptor: KArgumentCaptor<Int> = argumentCaptor()
////
////        verify(mockViewModel, times(2)).loadSelectedFilter(argumentCaptor.capture())
////
////        assertEquals(Enums.Contacts.FILTER_TYPE_DIRECTORY, argumentCaptor.secondValue)
////    }
//
//    @Test
//    fun onContactListItemClicked_callsToFragmentListener() {
//        val listItem = ContactListItem(NextivaContact("1234"), "search", true)
//
//        fragment.onContactListItemClicked(listItem)
//
//        verify(mockFragmentListener).onContactListItemClicked(fragment, listItem)
//    }
//
//    @Test
//    fun onContactListItemClicked_callsToAnalyticsManager() {
//        val listItem = ContactListItem(NextivaContact("1234"), "search", true)
//
//        fragment.onContactListItemClicked(listItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.CONTACT_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun onContactListItemLongClicked_callsToAnalyticsManager() {
//        whenever(mockViewModel.shouldShowLongPressOptions()).thenReturn(true)
//
//        val nextivaContact = spy(NextivaContact("1234"))
//        whenever(nextivaContact.uiName).thenReturn("UI Name")
//
//        val listItem = ContactListItem(nextivaContact, "search", true)
//
//        fragment.onContactListItemLongClicked(listItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.CONTACT_LIST_ITEM_LONG_PRESSED)
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.CONTACTS_LIST_ITEM_OPTIONS_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun onContactListItemLongClicked_callsToDialogManagerToShowDialog() {
//        whenever(mockViewModel.shouldShowLongPressOptions()).thenReturn(true)
//
//        val nextivaContact = spy(NextivaContact("1234"))
//        whenever(nextivaContact.uiName).thenReturn("UI Name")
//
//        val listItem = ContactListItem(nextivaContact, "search", true)
//
//        fragment.onContactListItemLongClicked(listItem)
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq("UI Name"),
//                any(),
//                any(),
//                any())
//    }
//
//    @Test
//    fun onContactListItemLongClicked_selectEditOption_callsToAnalyticsManager() {
//        whenever(mockViewModel.shouldShowLongPressOptions()).thenReturn(true)
//
//        val nextivaContact = NextivaContact("1234")
//        nextivaContact.contactType = Enums.Contacts.ContactTypes.ENTERPRISE
//        val listItem = ContactListItem(nextivaContact, "search", true)
//
//        fragment.onContactListItemLongClicked(listItem)
//
//        val dialogOptionsCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val selectionListenerCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq(null),
//                dialogOptionsCaptor.capture(),
//                selectionListenerCaptor.capture(),
//                any())
//
//        assertEquals("Edit", dialogOptionsCaptor.firstValue[0])
//
//        selectionListenerCaptor.firstValue.onSelectionMade(0)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.CONTACTS_LIST_ITEM_OPTIONS_DIALOG_EDIT_CONTACT_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onContactListItemLongClicked_selectEditOptionWithEnterpriseContact_callsToStartEditEnterpriseContactActivity() {
//        whenever(mockViewModel.shouldShowLongPressOptions()).thenReturn(true)
//
//        val nextivaContact = NextivaContact("1234")
//        nextivaContact.contactType = Enums.Contacts.ContactTypes.ENTERPRISE
//        val listItem = ContactListItem(nextivaContact, "search", true)
//
//        fragment.onContactListItemLongClicked(listItem)
//
//        val dialogOptionsCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val selectionListenerCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq(null),
//                dialogOptionsCaptor.capture(),
//                selectionListenerCaptor.capture(),
//                any())
//
//        assertEquals("Edit", dialogOptionsCaptor.firstValue[0])
//
//        selectionListenerCaptor.firstValue.onSelectionMade(0)
//
//        val shadowActivity = Shadows.shadowOf(fragment.activity)
//        val actualIntent = shadowActivity.nextStartedActivityForResult
//
//        assertEquals(RequestCodes.EDIT_CONTACT_REQUEST_CODE, (actualIntent.requestCode and 0x0000ffff))
//        assertEquals("com.nextiva.nextivaapp.android.ENTERPRISE_CONTACT", actualIntent.intent.getSerializableExtra("PARAMS_SCREEN_TYPE"))
//        assertEquals(nextivaContact, actualIntent.intent.getSerializableExtra("PARAMS_EDITING_CONTACT"))
//    }
//
//    @Test
//    fun onContactListItemLongClicked_selectEditOptionWithConferenceContact_callsToStartEditConferenceContactActivity() {
//        whenever(mockViewModel.shouldShowLongPressOptions()).thenReturn(true)
//
//        val nextivaContact = NextivaContact("1234")
//        nextivaContact.contactType = Enums.Contacts.ContactTypes.CONFERENCE
//        val listItem = ContactListItem(nextivaContact, "search", true)
//
//        fragment.onContactListItemLongClicked(listItem)
//
//        val dialogOptionsCaptor: KArgumentCaptor<List<String>> = argumentCaptor()
//        val selectionListenerCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq(null),
//                dialogOptionsCaptor.capture(),
//                selectionListenerCaptor.capture(),
//                any())
//
//        assertEquals("Edit", dialogOptionsCaptor.firstValue[0])
//
//        selectionListenerCaptor.firstValue.onSelectionMade(0)
//
//        val shadowActivity = Shadows.shadowOf(fragment.activity)
//        val actualIntent = shadowActivity.nextStartedActivityForResult
//
//        assertEquals(RequestCodes.EDIT_CONTACT_REQUEST_CODE, (actualIntent.requestCode and 0x0000ffff))
//        assertEquals("com.nextiva.nextivaapp.android.CONFERENCE_CONTACT", actualIntent.intent.getSerializableExtra("PARAMS_SCREEN_TYPE"))
//        assertEquals(nextivaContact, actualIntent.intent.getSerializableExtra("PARAMS_EDITING_CONTACT"))
//    }
//
//    @Test
//    fun onContactListItemLongClicked_selectCancel_callsToAnalyticsManager() {
//        whenever(mockViewModel.shouldShowLongPressOptions()).thenReturn(true)
//
//        val nextivaContact = NextivaContact("1234")
//        nextivaContact.contactType = Enums.Contacts.ContactTypes.ENTERPRISE
//        val listItem = ContactListItem(nextivaContact, "search", true)
//
//        fragment.onContactListItemLongClicked(listItem)
//
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(
//                eq(fragment.activity!!),
//                eq(null),
//                any(),
//                any(),
//                buttonCaptor.capture())
//
//        buttonCaptor.firstValue.onClick(MaterialDialog.Builder(fragment.activity!!).build(), DialogAction.NEGATIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.CONTACTS_LIST_ITEM_OPTIONS_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onContactHeaderListItemClicked_callsToViewModelToToggleHeader() {
//        val headerListItem = HeaderListItem(ListHeaderRow("Title"), arrayListOf(), true, false)
//
//        fragment.onContactHeaderListItemClicked(headerListItem)
//
//        verify(mockViewModel).onContactHeaderListItemClicked()
//    }
//
//    @Test
//    fun onContactHeaderListItemClicked_expandedHeader_callsToAnalyticsManager() {
//        val headerListItem = HeaderListItem(ListHeaderRow("Title"), arrayListOf(), true, false)
//        headerListItem.isExpanded = true
//
//        fragment.onContactHeaderListItemClicked(headerListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.SECTION_HEADER_EXPANDED)
//    }
//
//    @Test
//    fun onContactHeaderListItemClicked_collapsedHeader_callsToAnalyticsManager() {
//        val headerListItem = HeaderListItem(ListHeaderRow("Title"), arrayListOf(), true, false)
//        headerListItem.isExpanded = false
//
//        fragment.onContactHeaderListItemClicked(headerListItem)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.SECTION_HEADER_COLLAPSED)
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
//    fun onActivityResult_editContactRequestCode_callsToViewModelToProcessResult() {
//        val data = Intent()
//        fragment.onActivityResult(RequestCodes.EDIT_CONTACT_REQUEST_CODE, Activity.RESULT_OK, data)
//
//        verify(mockViewModel).onActivityResult(
//                fragment.activity!!,
//                Enums.Analytics.ScreenName.CONTACTS_LIST,
//                RequestCodes.EDIT_CONTACT_REQUEST_CODE,
//                Activity.RESULT_OK,
//                data)
//    }
//
//    @Test
//    fun onActivityResult_addContactRequestCode_callsToViewModelToProcessResult() {
//        val data = Intent()
//        fragment.onActivityResult(RequestCodes.ADD_CONTACT_REQUEST_CODE, Activity.RESULT_OK, data)
//
//        verify(mockViewModel).onActivityResult(
//                fragment.activity!!,
//                Enums.Analytics.ScreenName.CONTACTS_LIST,
//                RequestCodes.ADD_CONTACT_REQUEST_CODE,
//                Activity.RESULT_OK,
//                data)
//    }
//
//    @Test
//    fun onActivityResult_contactDetailsRequestCode_callsToViewModelToProcessResult() {
//        val data = Intent()
//        fragment.onActivityResult(RequestCodes.CONTACT_DETAILS_REQUEST_CODE, Activity.RESULT_OK, data)
//
//        verify(mockViewModel).onActivityResult(
//                fragment.activity!!,
//                Enums.Analytics.ScreenName.CONTACTS_LIST,
//                RequestCodes.CONTACT_DETAILS_REQUEST_CODE,
//                Activity.RESULT_OK,
//                data)
//    }
//
//    @Test
//    fun clickRequestPermissionButton_callsToAnalyticsManager() {
//        val requestPermissionButton = fragment.view!!.findViewById<Button>(R.id.contacts_list_permission_required_button)
//
//        if (requestPermissionButton.isClickable) {
//            requestPermissionButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.LOCAL_CONTACTS_REQUEST_PERMISSION_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun clickRequestPermissionButton_callsToViewModelToRequestPermission() {
//        val requestPermissionButton = fragment.view!!.findViewById<Button>(R.id.contacts_list_permission_required_button)
//
//        if (requestPermissionButton.isClickable) {
//            requestPermissionButton.performClick()
//        }
//
//        verify(mockViewModel).onRequestContactsPermission(fragment.activity!!, Enums.Analytics.ScreenName.CONTACTS_LIST)
//    }
//
//    @Test
//    fun onSaveInstanceState_savesSelectedFilterType() {
//        whenever(mockSelectedFilterTypeLiveData.value).thenReturn(Enums.Contacts.FilterTypes.LOCAL_ADDRESS_BOOK)
//
//        val outState = Bundle()
//
//        fragment.onSaveInstanceState(outState)
//
//        assertEquals(Enums.Contacts.FilterTypes.LOCAL_ADDRESS_BOOK, outState.getInt("SELECTED_FILTER_OPTION_TYPE"))
//    }
//
//    @Test
//    fun onFabSpeedDialClicked_addEnterpriseContact_startsAddEnterpriseContactActivity() {
//        fragment.onFabSpeedDialClicked(DashboardFragment.FAB_ADD_CONTACT_POSITION)
//
//        val shadowActivity = Shadows.shadowOf(fragment.activity)
//        val actualIntent = shadowActivity.nextStartedActivityForResult
//
//        assertEquals(RequestCodes.ADD_CONTACT_REQUEST_CODE, (actualIntent.requestCode and 0x0000ffff))
//        assertEquals("com.nextiva.nextivaapp.android.ENTERPRISE_CONTACT", actualIntent.intent.getSerializableExtra("PARAMS_SCREEN_TYPE"))
//        assertNull(actualIntent.intent.getSerializableExtra("PARAMS_EDITING_CONTACT"))
//    }
//
//    @Test
//    fun onFabSpeedDialClicked_addEnterpriseContact_callsToAnalyticsManager() {
//        fragment.onFabSpeedDialClicked(DashboardFragment.FAB_ADD_CONTACT_POSITION)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.ADD_ENTERPRISE_CONTACT_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onFabSpeedDialClicked_addConferenceContact_startsAddConferenceContactActivity() {
//        fragment.onFabSpeedDialClicked(DashboardFragment.FAB_ADD_CONFERENCE_POSITION)
//
//        val shadowActivity = Shadows.shadowOf(fragment.activity)
//        val actualIntent = shadowActivity.nextStartedActivityForResult
//
//        assertEquals(RequestCodes.ADD_CONTACT_REQUEST_CODE, (actualIntent.requestCode and 0x0000ffff))
//        assertEquals("com.nextiva.nextivaapp.android.CONFERENCE_CONTACT", actualIntent.intent.getSerializableExtra("PARAMS_SCREEN_TYPE"))
//        assertNull(actualIntent.intent.getSerializableExtra("PARAMS_EDITING_CONTACT"))
//    }
//
//    @Test
//    fun onFabSpeedDialClicked_addConferenceContact_callsToAnalyticsManager() {
//        fragment.onFabSpeedDialClicked(DashboardFragment.FAB_ADD_CONFERENCE_POSITION)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.ADD_CONFERENCE_CONTACT_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun addEnterpriseContact_startsAddEnterpriseContactActivity() {
//        fragment.addEnterpriseContact()
//
//        val shadowActivity = Shadows.shadowOf(fragment.activity)
//        val actualIntent = shadowActivity.nextStartedActivityForResult
//
//        assertEquals(RequestCodes.ADD_CONTACT_REQUEST_CODE, (actualIntent.requestCode and 0x0000ffff))
//        assertEquals("com.nextiva.nextivaapp.android.ENTERPRISE_CONTACT", actualIntent.intent.getSerializableExtra("PARAMS_SCREEN_TYPE"))
//        assertNull(actualIntent.intent.getSerializableExtra("PARAMS_EDITING_CONTACT"))
//    }
//
//    @Test
//    fun addConferenceContact_startsAddConferenceContactActivity() {
//        fragment.addConferenceContact()
//
//        val shadowActivity = Shadows.shadowOf(fragment.activity)
//        val actualIntent = shadowActivity.nextStartedActivityForResult
//
//        assertEquals(RequestCodes.ADD_CONTACT_REQUEST_CODE, (actualIntent.requestCode and 0x0000ffff))
//        assertEquals("com.nextiva.nextivaapp.android.CONFERENCE_CONTACT", actualIntent.intent.getSerializableExtra("PARAMS_SCREEN_TYPE"))
//        assertNull(actualIntent.intent.getSerializableExtra("PARAMS_EDITING_CONTACT"))
//    }
//
//    @Test
//    fun onOptionsItemSelected_filterMenuItem_callsToAnalyticsManager() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_action_filter_contacts_list)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.FILTER_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onOptionsItemSelected_allRoster_callsToViewModelToSetFilter() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_filter_contacts_list_all_roster_contacts)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(mockViewModel).loadSelectedFilter(Enums.Contacts.FilterTypes.ROSTER_ALL)
//        verify(mockViewModel).fetchContacts(fragment.activity!!, Enums.Analytics.ScreenName.CONTACTS_LIST, false)
//    }
//
//    @Test
//    fun onOptionsItemSelected_allRoster_callsToAnalyticsManager() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_filter_contacts_list_all_roster_contacts)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.MY_CONTACTS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun onOptionsItemSelected_onlineRoster_callsToViewModelToSetFilter() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_filter_contacts_list_online_roster_contacts)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(mockViewModel).loadSelectedFilter(Enums.Contacts.FilterTypes.ROSTER_ONLINE)
//        verify(mockViewModel).fetchContacts(fragment.activity!!, Enums.Analytics.ScreenName.CONTACTS_LIST, false)
//    }
//
//    @Test
//    fun onOptionsItemSelected_onlineRoster_callsToAnalyticsManager() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_filter_contacts_list_online_roster_contacts)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.MY_ONLINE_CONTACTS_FILTER_SELECTED)
//    }
//
//    @Test
//    fun onOptionsItemSelected_local_callsToViewModelToSetFilter() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_filter_contacts_list_local_contacts)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(mockViewModel).loadSelectedFilter(Enums.Contacts.FilterTypes.LOCAL_ADDRESS_BOOK)
//        verify(mockViewModel).fetchContacts(fragment.activity!!, Enums.Analytics.ScreenName.CONTACTS_LIST, false)
//    }
//
//    @Test
//    fun onOptionsItemSelected_local_callsToAnalyticsManager() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_filter_contacts_list_local_contacts)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.LOCAL_ADDRESS_BOOK_FILTER_SELECTED)
//    }
//
//    @Test
//    fun onOptionsItemSelected_directory_callsToViewModelToSetFilter() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_filter_contacts_list_directory_contacts)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(mockViewModel).loadSelectedFilter(Enums.Contacts.FilterTypes.DIRECTORY)
//        verify(mockViewModel).fetchContacts(fragment.activity!!, Enums.Analytics.ScreenName.CONTACTS_LIST, false)
//    }
//
//    @Test
//    fun onOptionsItemSelected_directory_callsToAnalyticsManager() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.contacts_list_filter_contacts_list_directory_contacts)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACTS_LIST, Enums.Analytics.EventName.DIRECTORY_FILTER_SELECTED)
//    }
//
//    @Test
//    fun baseListItemsObserverValueUpdated_updatesAdapterListItems() {
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<BaseListItem>>> = argumentCaptor()
//        verify(mockBaseListItemsListLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(arrayListOf(ContactListItem(NextivaContact("111"), null, true)))
//
//        assertEquals(1, fragment.mRecyclerView.adapter!!.itemCount)
//
//        argumentCaptor.firstValue.onChanged(arrayListOf(
//                ContactListItem(NextivaContact("111"), null, true),
//                ContactListItem(NextivaContact("222"), null, true)))
//
//        assertEquals(2, fragment.mRecyclerView.adapter!!.itemCount)
//
//        argumentCaptor.firstValue.onChanged(arrayListOf(
//                ContactListItem(NextivaContact("111"), null, true),
//                ContactListItem(NextivaContact("222"), null, true),
//                ContactListItem(NextivaContact("333"), null, true)))
//
//        assertEquals(3, fragment.mRecyclerView.adapter!!.itemCount)
//    }
//
//    @Test
//    fun baseListItemsObserverValueUpdated_emptyList_showsEmptyState() {
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<BaseListItem>>> = argumentCaptor()
//        verify(mockBaseListItemsListLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(arrayListOf())
//
//        assertEquals(View.GONE, fragment.mRecyclerView.visibility)
//        assertEquals(View.VISIBLE, fragment.mEmptyStateView!!.visibility)
//    }
//
//    @Test
//    fun baseListItemsObserverValueUpdated_filledList_showsList() {
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<BaseListItem>>> = argumentCaptor()
//        verify(mockBaseListItemsListLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(arrayListOf(ContactListItem(NextivaContact("111"), null, true)))
//
//        assertEquals(View.VISIBLE, fragment.mRecyclerView.visibility)
//        assertEquals(View.GONE, fragment.mEmptyStateView!!.visibility)
//    }
//
//    @Test
//    fun selectedFilterTypeObserverValueUpdated_allRoster_showsCorrectFilterViews() {
//        val argumentCaptor: KArgumentCaptor<Observer<Int>> = argumentCaptor()
//        verify(mockSelectedFilterTypeLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Enums.Contacts.FilterTypes.ROSTER_ALL)
//
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
//
//        val shadowDrawable = Shadows.shadowOf((fragment.mFilterImageSwitcher!!.currentView as ImageView).drawable)
//        assertEquals(R.drawable.ic_people_outline, shadowDrawable.createdFromResId)
//        assertEquals("My Contacts", (fragment.mFilterTextSwitcher!!.currentView as TextView).text.toString())
//    }
//
//    @Test
//    fun selectedFilterTypeObserverValueUpdated_onlineRoster_showsCorrectFilterViews() {
//        val argumentCaptor: KArgumentCaptor<Observer<Int>> = argumentCaptor()
//        verify(mockSelectedFilterTypeLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Enums.Contacts.FilterTypes.ROSTER_ONLINE)
//
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
//
//        val shadowDrawable = Shadows.shadowOf((fragment.mFilterImageSwitcher!!.currentView as ImageView).drawable)
//        assertEquals(R.drawable.ic_people_outline, shadowDrawable.createdFromResId)
//        assertEquals("My Online Contacts", (fragment.mFilterTextSwitcher!!.currentView as TextView).text.toString())
//    }
//
//    @Test
//    fun selectedFilterTypeObserverValueUpdated_local_showsCorrectFilterViews() {
//        val argumentCaptor: KArgumentCaptor<Observer<Int>> = argumentCaptor()
//        verify(mockSelectedFilterTypeLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Enums.Contacts.FilterTypes.LOCAL_ADDRESS_BOOK)
//
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
//
//        val shadowDrawable = Shadows.shadowOf((fragment.mFilterImageSwitcher!!.currentView as ImageView).drawable)
//        assertEquals(R.drawable.ic_phone_android, shadowDrawable.createdFromResId)
//        assertEquals("Local Address Book", (fragment.mFilterTextSwitcher!!.currentView as TextView).text.toString())
//    }
//
//    @Test
//    fun selectedFilterTypeObserverValueUpdated_directory_showsCorrectFilterViews() {
//        val argumentCaptor: KArgumentCaptor<Observer<Int>> = argumentCaptor()
//        verify(mockSelectedFilterTypeLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Enums.Contacts.FilterTypes.DIRECTORY)
//
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
//
//        val shadowDrawable = Shadows.shadowOf((fragment.mFilterImageSwitcher!!.currentView as ImageView).drawable)
//        assertEquals(R.drawable.ic_globe, shadowDrawable.createdFromResId)
//        assertEquals("Directory", (fragment.mFilterTextSwitcher!!.currentView as TextView).text.toString())
//    }
//
//    @Test
//    fun permissionObserverValueUpdated_callsToDialogManagerToDismissProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockPermissionRequiredStateVisibleLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun permissionObserverValueUpdated_shouldShowPermissionRequired_showsPermissionRequiredLayout() {
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockPermissionRequiredStateVisibleLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        assertEquals(View.VISIBLE, fragment.mPermissionRequiredLayout.visibility)
//    }
//
//    @Test
//    fun permissionObserverValueUpdated_shouldHidePermissionRequired_showsCorrectLayout() {
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockPermissionRequiredStateVisibleLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(false)
//
//        assertEquals(View.VISIBLE, fragment.mEmptyStateView!!.visibility)
//    }
//
//    @Test
//    fun refreshingObserverValueUpdated_trueValue_startsRefreshing() {
//        fragment.mIsRefreshing = false
//
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockRefreshingLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
//
//        assertTrue(fragment.mIsRefreshing)
//        assertTrue(fragment.mSwipeRefreshLayout!!.isRefreshing)
//    }
//
//    @Test
//    fun refreshingObserverValueUpdated_falseValue_stopsRefreshing() {
//        fragment.mIsRefreshing = true
//
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockRefreshingLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(false))
//
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
//
//        assertFalse(fragment.mIsRefreshing)
//        assertFalse(fragment.mSwipeRefreshLayout!!.isRefreshing)
//    }
//
//    @Test
//    fun errorObserverValueUpdated_callsToDialogManagerToDismissProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockErrorLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun errorObserverValueUpdated_callsToDialogManagerToShowErrorDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockErrorLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.CONTACTS_LIST)
//    }
//
////    @Test
////    fun xmppErrorEventObserverValueUpdated_showsToast() {
////        mockkStatic(Toast::class)
////
////        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<XmppErrorEvent>>> = argumentCaptor()
////        verify(mockXmppErrorEventLiveData).observe(eq(fragment), argumentCaptor.capture())
////
////        argumentCaptor.firstValue.onChanged(SingleEvent(XmppErrorEvent(false, Exception("Something Happened"))))
////
////        io.mockk.verify {
////            Toast.makeText(fragment.activity!!,
////                    "XMPP error exception.",
////                    Toast.LENGTH_SHORT)
////        }
////    }
//}