package com.nextiva.nextivaapp.android.fragments
//
//import android.app.Activity
//import android.content.Intent
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.ChatConversationActivity
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConferencePhoneNumberListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactDetailListItem
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.db.model.DbGroup
//import com.nextiva.nextivaapp.android.db.model.DbPresence
//import com.nextiva.nextivaapp.android.db.model.PhoneNumber
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeContactDetailListFragment
//import com.nextiva.nextivaapp.android.mocks.values.ContactLists
//import com.nextiva.nextivaapp.android.models.NextivaContact
//import com.nextiva.nextivaapp.android.models.Resource
//import com.nextiva.nextivaapp.android.models.SingleEvent
//import com.nextiva.nextivaapp.android.net.buses.RxEvents
//import com.nextiva.nextivaapp.android.viewmodels.ContactDetailViewModel
//import com.nhaarman.mockito_kotlin.*
//import io.mockk.every
//import io.mockk.mockkStatic
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertFalse
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import javax.inject.Inject
//
//class ContactDetailListFragmentTest : BaseRobolectricTest() {
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
//    @Inject
//    lateinit var connectionStateManager: ConnectionStateManager
//
//    private val mockViewModel: ContactDetailViewModel = mock()
//    private val mockContactUpdatedMutableLiveData: MutableLiveData<Resource<Void?>> = mock()
//    private val mockSendSmsDialogMutableLiveData: MutableLiveData<SingleEvent<ArrayList<String>>> = mock()
//    private val mockSelectPhoneNumberListDialogMutableLiveData: MutableLiveData<SingleEvent<ArrayList<String>>> = mock()
//    private val mockContactDetailListItemsMutableLiveData: MutableLiveData<ArrayList<BaseListItem>> = mock()
//    private val mockXmppErrorEventMutableLiveData: MutableLiveData<SingleEvent<RxEvents.XmppErrorEvent>> = mock()
//    private val mockLocalContactAlreadyExistsLiveData: MutableLiveData<Void> = mock()
//    private val mockRefreshingLocalContactsLiveData: MutableLiveData<Boolean> = mock()
//    private val mockShowGroupsLiveData: MutableLiveData<ArrayList<DbGroup>> = mock()
//    private val mockRemoveFromGroupsLiveData: MutableLiveData<ArrayList<DbGroup>> = mock()
//    private val fragment: ContactDetailListFragment = ContactDetailListFragment.newInstance(ContactLists.getNextivaContactTestList()[0])
//
//    private val mockChatConversationIntent = Intent()
//
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(ContactDetailViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.contactUpdatedLiveData).thenReturn(mockContactUpdatedMutableLiveData)
//        whenever(mockViewModel.sendSmsDialogLiveData).thenReturn(mockSendSmsDialogMutableLiveData)
//        whenever(mockViewModel.selectPhoneNumberListDialogLiveData).thenReturn(mockSelectPhoneNumberListDialogMutableLiveData)
//        whenever(mockViewModel.contactDetailListItemsLiveData).thenReturn(mockContactDetailListItemsMutableLiveData)
//        whenever(mockViewModel.xmppErrorEventLiveData).thenReturn(mockXmppErrorEventMutableLiveData)
//        whenever(mockViewModel.localContactAlreadyExistsLiveData).thenReturn(mockLocalContactAlreadyExistsLiveData)
//        whenever(mockViewModel.refreshingLocalContactsLiveData).thenReturn(mockRefreshingLocalContactsLiveData)
//        whenever(mockViewModel.showAddToGroupsListDialogMutableLiveData).thenReturn(mockShowGroupsLiveData)
//        whenever(mockViewModel.showRemoveFromGroupsListDialogMutableLiveData).thenReturn(mockRemoveFromGroupsLiveData)
//
//        SupportFragmentTestUtil.startFragment(fragment, FakeContactDetailListFragment::class.java)
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
//        verify(mockContactUpdatedMutableLiveData).observe(eq(fragment), any())
//        verify(mockSendSmsDialogMutableLiveData).observe(eq(fragment), any())
//        verify(mockSelectPhoneNumberListDialogMutableLiveData).observe(eq(fragment), any())
//        verify(mockContactDetailListItemsMutableLiveData).observe(eq(fragment), any())
//        verify(mockXmppErrorEventMutableLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun getLayoutId_returnsCorrectLayoutId() {
//        assertEquals(R.layout.fragment_contact_detail_list, fragment.layoutId)
//    }
//
//    @Test
//    fun getSwipeRefreshLayoutId_returnsCorrectSwipeRefreshLayoutId() {
//        assertEquals(0, fragment.swipeRefreshLayoutId)
//    }
//
//    @Test
//    fun getRecyclerViewId_returnsCorrectRecyclerViewId() {
//        assertEquals(R.id.contact_detail_list_recycler_view, fragment.recyclerViewId)
//    }
//
//    @Test
//    fun getAnalyticsScreenName_returnsCorrectAnalyticsScreenName() {
//        assertEquals("contact_details_screen", fragment.analyticScreenName)
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
//    fun onContactEdited_callsToViewModel() {
//        reset(mockViewModel)
//
//        val nextivaContact = ContactLists.getNextivaContactTestList()[0]
//        fragment.onContactEdited(nextivaContact)
//
//        verify(mockViewModel).nextivaContact = nextivaContact
//        assertEquals(0, fragment.mRecyclerView.adapter!!.itemCount)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction1ButtonClicked_phoneNumberViewTypeHasSubTitle_callsToViewModel() {
//        fragment.onDetailItemViewListItemAction1ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.PHONE_NUMBER, "Work Number", "(111) 222-1111", R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(mockViewModel).makeCall(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS, "(111) 222-1111", Enums.Sip.CallTypes.VOICE)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction1ButtonClicked_phoneNumberViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction1ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.PHONE_NUMBER, "Work Number", null, R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction1ButtonClicked_extensionViewTypeHasSubTitle_callsToViewModel() {
//        fragment.onDetailItemViewListItemAction1ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.EXTENSION, "Work Extension", "1111", R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(mockViewModel).makeCall(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS, "1111", Enums.Sip.CallTypes.VOICE)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction1ButtonClicked_extensionViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction1ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.EXTENSION, "Work Extension", null, R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction1ButtonClicked_conferenceViewTypeHasSubTitle_callsToViewModel() {
//        val conferenceNumber = PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1231231234", "123456", "123456")
//
//        fragment.onDetailItemViewListItemAction1ButtonClicked(
//                ConferencePhoneNumberListItem(Enums.Contacts.DetailViewTypes.CONFERENCE_PHONE_NUMBER, "Conference Number", conferenceNumber.number, conferenceNumber.pinOne, conferenceNumber.pinTwo, conferenceNumber.assembledPhoneNumber))
//
//        verify(mockViewModel).makeCall(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS, conferenceNumber.assembledPhoneNumber, Enums.Sip.CallTypes.VOICE)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction1ButtonClicked_conferenceViewType_callsToAnalyticsManager() {
//        val conferenceNumber = PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1231231234", "123456", "123456")
//
//        fragment.onDetailItemViewListItemAction1ButtonClicked(
//                ConferencePhoneNumberListItem(Enums.Contacts.DetailViewTypes.CONFERENCE_PHONE_NUMBER, "Conference Number", conferenceNumber.number, conferenceNumber.pinOne, conferenceNumber.pinTwo, conferenceNumber.assembledPhoneNumber))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_phoneNumberViewTypeHasSubTitle_callsToViewModel() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.PHONE_NUMBER, "Work Number", "(111) 222-1111", R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(mockViewModel).makeCall(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS, "(111) 222-1111", Enums.Sip.CallTypes.VIDEO)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_phoneNumberViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.PHONE_NUMBER, "Work Number", null, R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_VIDEO_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_extensionViewTypeHasSubTitle_callsToViewModel() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.EXTENSION, "Work Extension", "1111", R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(mockViewModel).makeCall(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS, "1111", Enums.Sip.CallTypes.VIDEO)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_extensionViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.EXTENSION, "Work Extension", null, R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_VIDEO_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToContactsViewTypeHasSubTitle_noInternet_callsToDialogManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.ADD_TO_CONTACTS, "Add to Contacts", null, 0, R.drawable.ic_add, false))
//
//        verify(dialogManager).showErrorDialog(fragment.context!!, Enums.Analytics.ScreenName.CONTACT_DETAILS)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToContactsViewTypeHasSubTitle_callsToViewModel() {
//        whenever(connectionStateManager.isInternetConnected).thenReturn(true)
//
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.ADD_TO_CONTACTS, "Add to Contacts", null, 0, R.drawable.ic_add, false))
//
//        verify(mockViewModel).addContactToRoster()
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToContactsViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.ADD_TO_CONTACTS, "Add to Contacts", null, 0, R.drawable.ic_add, false))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.ADD_TO_CONTACTS_LIST_ITEM_ADD_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToLocalContactsViewTypeHasSubTitle_callsToViewModel() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.ADD_TO_LOCAL_CONTACT, "Add to Local Contacts", null, 0, R.drawable.ic_group_add, false))
//
//        verify(mockViewModel).addToLocalContact(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_addToLocalContactsViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.ADD_TO_LOCAL_CONTACT, "Add to Local Contacts", null, 0, R.drawable.ic_group_add, false))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.ADD_TO_LOCAL_CONTACTS_LIST_ITEM_ADD_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_sendPersonalSmsViewTypeHasSubTitle_callsToViewModel() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.SEND_PERSONAL_SMS, "Send SMS", null, 0, R.drawable.ic_chat, true))
//
//        verify(mockViewModel).sendSms(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_sendPersonalSmsViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.SEND_PERSONAL_SMS, "Send SMS", null, 0, R.drawable.ic_chat, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.SEND_SMS_LIST_ITEM_SEND_SMS_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_emailAddressViewTypeHasSubTitle_callsToViewModel() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.EMAIL_ADDRESS, "Work Email", "address@address.com", 0, R.drawable.ic_email, true))
//
//        verify(mockViewModel).sendEmailIntent(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS, "address@address.com")
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_emailAddressViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.EMAIL_ADDRESS, "Work Email", "address@address.com", 0, R.drawable.ic_email, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.EMAIL_LIST_ITEM_EMAIL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_imAddressViewTypeHasSubTitle_showsToast() {
//        val nextivaContact = NextivaContact("")
//        nextivaContact.jid = "jid@jid.im"
//
//        whenever(mockViewModel.nextivaContact).thenReturn(nextivaContact)
//
//        val intent: Intent = mock()
//
//        mockkStatic(ChatConversationActivity::class)
//        every { ChatConversationActivity.newIntent(com.nhaarman.mockito_kotlin.any(), nextivaContact.jid, com.nhaarman.mockito_kotlin.any()) } returns intent
//
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.IM_ADDRESS, "IM Address", "jid@jid.im", 0, R.drawable.ic_chat, true))
//
//        assertFalse(fragment.isVisible)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_imAddressViewType_callsToAnalyticsManager() {
//        val presence = DbPresence()
//        val nextivaContact = NextivaContact("")
//        nextivaContact.jid = "jid@jid.im"
//        nextivaContact.presence = presence
//
//        whenever(mockViewModel.nextivaContact).thenReturn(nextivaContact)
//
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.IM_ADDRESS, "IM Address", "jid@jid.im", 0, R.drawable.ic_chat, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.IM_ADDRESS_LIST_ITEM_CHAT_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_favoriteViewTypeHasSubTitle_noInternet_callsToDialogManager() {
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.FAVORITE, "Add to Favorites", null, 0, R.drawable.ic_star_hollow, false))
//
//        verify(dialogManager).showErrorDialog(fragment.context!!, Enums.Analytics.ScreenName.CONTACT_DETAILS)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_favoriteViewTypeHasSubTitle_callsToViewModel() {
//        whenever(connectionStateManager.isInternetConnected).thenReturn(true)
//
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.FAVORITE, "Add to Favorites", null, 0, R.drawable.ic_star_hollow, false))
//
//        verify(mockViewModel).isFavorite = true
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_favoriteViewTypeFavoriteSet_callsToAnalyticsManager() {
//        whenever(mockViewModel.isFavorite).thenReturn(true)
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.FAVORITE, "Add to Favorites", null, 0, R.drawable.ic_star_hollow, false))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.ADD_TO_FAVORITES_LIST_ITEM_UNFAVORITE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_favoriteViewTypeFavoriteNotSet_callsToAnalyticsManager() {
//        whenever(mockViewModel.isFavorite).thenReturn(false)
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.FAVORITE, "Add to Favorites", null, 0, R.drawable.ic_star_hollow, false))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.ADD_TO_FAVORITES_LIST_ITEM_FAVORITE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_conferenceViewTypeHasSubTitle_callsToViewModel() {
//        val conferenceNumber = PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1231231234", "123456", "123456")
//
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ConferencePhoneNumberListItem(Enums.Contacts.DetailViewTypes.CONFERENCE_PHONE_NUMBER, "Conference Number", conferenceNumber.number, conferenceNumber.pinOne, conferenceNumber.pinTwo, conferenceNumber.assembledPhoneNumber))
//
//        verify(mockViewModel).makeCall(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS, conferenceNumber.assembledPhoneNumber, Enums.Sip.CallTypes.VIDEO)
//    }
//
//    @Test
//    fun onDetailItemViewListItemAction2ButtonClicked_conferenceViewType_callsToAnalyticsManager() {
//        val conferenceNumber = PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1231231234", "123456", "123456")
//
//        fragment.onDetailItemViewListItemAction2ButtonClicked(
//                ConferencePhoneNumberListItem(Enums.Contacts.DetailViewTypes.CONFERENCE_PHONE_NUMBER, "Conference Number", conferenceNumber.number, conferenceNumber.pinOne, conferenceNumber.pinTwo, conferenceNumber.assembledPhoneNumber))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_VIDEO_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_callsToViewModel() {
//        fragment.onDetailItemViewListItemLongClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.FAVORITE, "Add to Favorites", null, 0, R.drawable.ic_star_hollow, false))
//
//        verify(mockViewModel).copyListItemText(any())
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_phoneViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemLongClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.PHONE_NUMBER, "Work Number", null, R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_COPIED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_extensionViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemLongClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.EXTENSION, "Work Extension", null, R.drawable.ic_call, R.drawable.ic_video, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_COPIED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_emailAddressViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemLongClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.EMAIL_ADDRESS, "Work Email", "address@address.com", 0, R.drawable.ic_email, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.EMAIL_LIST_ITEM_COPIED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_imAddressViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemLongClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.IM_ADDRESS, "IM Address", "jid@jid.im", 0, R.drawable.ic_chat, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.IM_ADDRESS_LIST_ITEM_COPIED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_lastNameViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemLongClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.LAST_NAME, "Last Name", "User's Last Name", 0, 0, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.LAST_NAME_LIST_ITEM_COPIED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_firstNameViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemLongClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.FIRST_NAME, "First Name", "User's First Name", 0, 0, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.FIRST_NAME_LIST_ITEM_COPIED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_companyViewType_callsToAnalyticsManager() {
//        fragment.onDetailItemViewListItemLongClicked(
//                ContactDetailListItem(Enums.Contacts.DetailViewTypes.COMPANY, "Company", "Acme", 0, 0, true))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.COMPANY_LIST_ITEM_COPIED)
//    }
//
//    @Test
//    fun onDetailItemViewListItemLongClicked_conferenceViewType_callsToAnalyticsManager() {
//        val conferenceNumber = PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1231231234", "123456", "123456")
//
//        fragment.onDetailItemViewListItemLongClicked(
//                ConferencePhoneNumberListItem(Enums.Contacts.DetailViewTypes.CONFERENCE_PHONE_NUMBER, "Conference Number", conferenceNumber.number, conferenceNumber.pinOne, conferenceNumber.pinTwo, conferenceNumber.assembledPhoneNumber))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.PHONE_NUMBER_LIST_ITEM_COPIED)
//    }
//
//    @Test
//    fun contactUpdatedObserverValueUpdated_loading_showsProgressDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Void?>>> = argumentCaptor()
//
//        verify(mockContactUpdatedMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(Resource.loading(null))
//
//        verify(dialogManager).showProgressDialog(any(), eq(Enums.Analytics.ScreenName.CONTACT_DETAILS), eq(R.string.progress_processing))
//    }
//
//    @Test
//    fun contactUpdatedObserverValueUpdated_success_dismissesDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Void?>>> = argumentCaptor()
//        val mockFragmentListener: ContactDetailListFragment.ContactDetailListFragmentListener = mock()
//        fragment.setContactDetailListFragmentListener(mockFragmentListener)
//
//        verify(mockContactUpdatedMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(Resource.success(null))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun contactUpdatedObserverValueUpdated_success_setsActivityResult() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Void?>>> = argumentCaptor()
//        val mockFragmentListener: ContactDetailListFragment.ContactDetailListFragmentListener = mock()
//        fragment.setContactDetailListFragmentListener(mockFragmentListener)
//
//        verify(mockContactUpdatedMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(Resource.success(null))
//
//        verify(mockFragmentListener).setActivityResult(Activity.RESULT_OK)
//    }
//
//    @Test
//    fun contactUpdatedObserverValueUpdated_error_callsToDialogManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<Resource<Void?>>> = argumentCaptor()
//        val mockFragmentListener: ContactDetailListFragment.ContactDetailListFragmentListener = mock()
//        fragment.setContactDetailListFragmentListener(mockFragmentListener)
//
//        verify(mockContactUpdatedMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(Resource.error("Message", null))
//
//        verify(dialogManager).showErrorDialog(fragment.context!!, Enums.Analytics.ScreenName.CONTACT_DETAILS)
//    }
//
//    @Test
//    fun sendSmsDialogObserverValueUpdated_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSendSmsDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun sendSmsDialogObserverValueUpdated_callsToDialogManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSendSmsDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        verify(dialogManager).showSimpleListDialog(any(),
//                eq("Select Phone Number"),
//                eq(numbers),
//                any(),
//                any())
//    }
//
//    @Test
//    fun sendSmsDialogObserverValueUpdated_selectNumber_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSendSmsDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        val listDialogListenerArgumentCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(any(),
//                eq("Select Phone Number"),
//                eq(numbers),
//                listDialogListenerArgumentCaptor.capture(),
//                any())
//
//        listDialogListenerArgumentCaptor.firstValue.onSelectionMade(1)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_NUMBER_SELECTED)
//    }
//
//    @Test
//    fun sendSmsDialogObserverValueUpdated_selectNumber_callsToViewModelToSendSmsIntent() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSendSmsDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        val listDialogListenerArgumentCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(any(),
//                eq("Select Phone Number"),
//                eq(numbers),
//                listDialogListenerArgumentCaptor.capture(),
//                any())
//
//        listDialogListenerArgumentCaptor.firstValue.onSelectionMade(1)
//
//        verify(mockViewModel).sendSmsIntent(fragment.activity!!, Enums.Analytics.ScreenName.CONTACT_DETAILS, "2223334444")
//    }
//
//    @Test
//    fun sendSmsDialogObserverValueUpdated_selectCancel_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSendSmsDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(any(),
//                eq("Select Phone Number"),
//                eq(numbers),
//                any(),
//                buttonCaptor.capture())
//
//        buttonCaptor.firstValue.onClick(MaterialDialog.Builder(fragment.activity!!).build(), DialogAction.NEGATIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun selectPhoneNumberListDialogObserverValueUpdated_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSelectPhoneNumberListDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun selectPhoneNumberListDialogObserverValueUpdated_selectNumber_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSelectPhoneNumberListDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        val listDialogListenerArgumentCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(any(),
//                eq("Select Phone Number"),
//                eq("Only one phone number can be imported. Select which phone number you would like to be imported with this contact."),
//                eq(numbers),
//                listDialogListenerArgumentCaptor.capture(),
//                any())
//
//        listDialogListenerArgumentCaptor.firstValue.onSelectionMade(1)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_NUMBER_SELECTED)
//    }
//
//    @Test
//    fun selectPhoneNumberListDialogObserverValueUpdated_selectNumber_callsToViewModelToAddContact() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSelectPhoneNumberListDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        val listDialogListenerArgumentCaptor: KArgumentCaptor<SimpleListDialogListener> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(any(),
//                eq("Select Phone Number"),
//                eq("Only one phone number can be imported. Select which phone number you would like to be imported with this contact."),
//                eq(numbers),
//                listDialogListenerArgumentCaptor.capture(),
//                any())
//
//        listDialogListenerArgumentCaptor.firstValue.onSelectionMade(1)
//
//        verify(mockViewModel).addContactToRosterWithPhoneNumber("2223334444")
//    }
//
//    @Test
//    fun selectPhoneNumberListDialogObserverValueUpdated_selectCancel_callsToAnalyticsManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<ArrayList<String>>>> = argumentCaptor()
//
//        val numbers: ArrayList<String> = ArrayList()
//        numbers.add("1112223333")
//        numbers.add("2223334444")
//        numbers.add("33344455555")
//
//        whenever(mockViewModel.nextivaContact).thenReturn(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockSelectPhoneNumberListDialogMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(numbers))
//
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(dialogManager).showSimpleListDialog(any(),
//                eq("Select Phone Number"),
//                eq("Only one phone number can be imported. Select which phone number you would like to be imported with this contact."),
//                eq(numbers),
//                any(),
//                buttonCaptor.capture())
//
//        buttonCaptor.firstValue.onClick(MaterialDialog.Builder(fragment.activity!!).build(), DialogAction.NEGATIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.CONTACT_DETAILS, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun contactDetailListItemsObserverValueUpdated_setsListItems() {
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<BaseListItem>>> = argumentCaptor()
//
//        val listItems: ArrayList<BaseListItem> = getDetailItemList(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockContactDetailListItemsMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(listItems)
//
//        assertEquals(15, fragment.mRecyclerView.adapter!!.itemCount)
//    }
//
//    @Test
//    fun xmppErrorEventObserverValueUpdated_callsToDialogManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<RxEvents.XmppErrorEvent>>> = argumentCaptor()
//
//        val listItems: ArrayList<BaseListItem> = getDetailItemList(ContactLists.getNextivaContactTestList()[0])
//
//        verify(mockXmppErrorEventMutableLiveData).observe(eq(fragment), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(SingleEvent(RxEvents.XmppErrorEvent(Exception())))
//
//        verify(dialogManager).showErrorDialog(fragment.context!!, Enums.Analytics.ScreenName.CONTACT_DETAILS)
//    }
//
//    private fun getDetailItemList(nextivaContact: NextivaContact): ArrayList<BaseListItem> {
//        val itemList: ArrayList<BaseListItem> = ArrayList()
//
//        val conferenceNumber = PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1231231234", "123456", "123456")
//
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.PHONE_NUMBER, "Work Number", "(111) 222-1111", R.drawable.ic_call, R.drawable.ic_video, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.PHONE_NUMBER, "Personal Number", "(111) 222-1112", R.drawable.ic_call, R.drawable.ic_video, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.EXTENSION, "Work Extension", "1111", R.drawable.ic_call, R.drawable.ic_video, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.EXTENSION, "Work Extension", "1112", R.drawable.ic_call, R.drawable.ic_video, true))
//        itemList.add(ConferencePhoneNumberListItem(Enums.Contacts.DetailViewTypes.CONFERENCE_PHONE_NUMBER, "Conference Number", conferenceNumber.number, conferenceNumber.pinOne, conferenceNumber.pinTwo, conferenceNumber.assembledPhoneNumber))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.ADD_TO_LOCAL_CONTACT, "Add to Local Contacts", null, 0, R.drawable.ic_group_add, false))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.ADD_TO_CONTACTS, "Add to Contacts", null, 0, R.drawable.ic_add, false))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.IM_ADDRESS, "IM Address", nextivaContact.jid, 0, R.drawable.ic_chat, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.SEND_PERSONAL_SMS, "Send SMS", null, 0, R.drawable.ic_chat, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.EMAIL_ADDRESS, "Work Email", "address@address.com", 0, R.drawable.ic_email, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.EMAIL_ADDRESS, "Personal Email", "address2@address.com", 0, R.drawable.ic_email, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.LAST_NAME, "Last Name", nextivaContact.lastName, 0, 0, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.FIRST_NAME, "First Name", nextivaContact.firstName, 0, 0, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.COMPANY, "Company", nextivaContact.company, 0, 0, true))
//        itemList.add(ContactDetailListItem(Enums.Contacts.DetailViewTypes.FAVORITE, "Add to Favorites", null, 0, R.drawable.ic_star_hollow, false))
//
//        return itemList
//    }
//}