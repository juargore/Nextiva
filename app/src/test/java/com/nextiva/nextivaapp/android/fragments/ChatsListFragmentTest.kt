package com.nextiva.nextivaapp.android.fragments

//
//import android.view.View
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
//import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.net.buses.RxEvents
//import com.nextiva.nextivaapp.android.viewmodels.ChatsListViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.*
//import org.junit.Test
//import org.robolectric.fakes.RoboMenuItem
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import java.io.IOException
//import javax.inject.Inject
//
//class ChatsListFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var fragment: ChatsListFragment
//
//    private val mockViewModel: ChatsListViewModel = mock()
//    private val mockProcessChatConversationsLiveData: LiveData<Void> = mock()
//    private val mockMarkAllMessagesReadResponseLiveData: LiveData<Boolean> = mock()
//    private val mockChatConversationListItemLiveData: LiveData<ArrayList<BaseListItem>> = mock()
//    private val mockXmppErrorEventLiveData: LiveData<RxEvents.XmppErrorEvent> = mock()
//    private val mockRegisterDeviceResponseLiveData: LiveData<RxEvents.RegisterDeviceResponseEvent> = mock()
//    private val mockChatConversationParticipantsEventLiveData: LiveData<RxEvents.ChatConversationParticipantsEvent> = mock()
//
//    @Throws(IOException::class)
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(ChatsListViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.getMarkAllMessagesReadResponseLiveData()).thenReturn(mockMarkAllMessagesReadResponseLiveData)
//        whenever(mockViewModel.getProcessChatConversationListItemsFailedLiveData()).thenReturn(mockProcessChatConversationsLiveData)
//        whenever(mockViewModel.getXmppErrorEventLiveData()).thenReturn(mockXmppErrorEventLiveData)
//        whenever(mockViewModel.getRegisterDeviceResponseEventLiveData()).thenReturn(mockRegisterDeviceResponseLiveData)
//        whenever(mockViewModel.getChatParticipantsChangedEventLiveData()).thenReturn(mockChatConversationParticipantsEventLiveData)
//        whenever(mockViewModel.baseListItemsLiveData).thenReturn(mockChatConversationListItemLiveData)
//
//        fragment = ChatsListFragment.newInstance()
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
//    fun onCreateViews_getsLiveDatas() {
//        verify(mockViewModel).getMarkAllMessagesReadResponseLiveData()
//        verify(mockViewModel).getProcessChatConversationListItemsFailedLiveData()
//        verify(mockViewModel).getXmppErrorEventLiveData()
//        verify(mockViewModel).getRegisterDeviceResponseEventLiveData()
//        verify(mockViewModel).getChatParticipantsChangedEventLiveData()
//        verify(mockViewModel).baseListItemsLiveData
//
//        verify(mockViewModel).requestChatParticipants(any())
//    }
//
//    @Test
//    fun onOptionsItemSelected_selectsMarkAllRead() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.chats_list_menu_item_mark_all_read)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(fragment.mDialogManager).showProgressDialog(any(), any(), any())
//        verify(fragment.mAnalyticsManager).logEvent(Enums.Analytics.ScreenName.CHATS_LIST, "mark_all_read_button_pressed")
//        verify(mockViewModel).markAllMessagesRead()
//    }
//
//    @Test
//    fun markAllMessagesReadResponseObserver_successful_noErrorDialog() {
//        reset(fragment.mDialogManager)
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockMarkAllMessagesReadResponseLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        verify(fragment.mDialogManager).dismissProgressDialog()
//        verify(fragment.mDialogManager, never()).showErrorDialog(any(), any())
//    }
//
//    @Test
//    fun markAllMessagesReadResponseObserver_unsuccessful_showsErrorDialog() {
//        reset(fragment.mDialogManager)
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockMarkAllMessagesReadResponseLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(false)
//
//        verify(fragment.mDialogManager).dismissProgressDialog()
//        verify(fragment.mDialogManager).showErrorDialog(any(), eq(Enums.Analytics.ScreenName.CHATS_LIST))
//    }
//
//    @Test
//    fun chatConversationListItemObserver_updatesViews_emptyState_callsToViewModel() {
//        reset(mockViewModel)
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<BaseListItem>>> = argumentCaptor()
//        verify(mockChatConversationListItemLiveData).observe(eq(fragment), argumentCaptor.capture())
//        val itemList: ArrayList<BaseListItem> = ArrayList()
//
//        argumentCaptor.firstValue.onChanged(itemList)
//
//        assertFalse(fragment.mIsRefreshing)
//        assertEquals(View.GONE, fragment.mRecyclerView.visibility)
//        assertEquals(View.VISIBLE, fragment.mEmptyStateView!!.visibility)
//        verify(mockViewModel).requestChatParticipants(any())
//    }
//
//    @Test
//    fun chatConversationListItemObserver_updatesViews_showsRecyclerView_callsToViewModel() {
//        reset(mockViewModel)
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<BaseListItem>>> = argumentCaptor()
//        verify(mockChatConversationListItemLiveData).observe(eq(fragment), argumentCaptor.capture())
//        val itemList: ArrayList<BaseListItem> = ArrayList()
//        itemList.add(ChatConversationListItem(mock(), mock()))
//
//        argumentCaptor.firstValue.onChanged(itemList)
//
//        assertFalse(fragment.mIsRefreshing)
//        assertEquals(View.VISIBLE, fragment.mRecyclerView.visibility)
//        assertEquals(View.GONE, fragment.mEmptyStateView!!.visibility)
//        verify(mockViewModel).requestChatParticipants(any())
//    }
//
//    @Test
//    fun registerDeviceResponseEventObserver_successful_callsToViewModel() {
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.RegisterDeviceResponseEvent>> = argumentCaptor()
//        verify(mockRegisterDeviceResponseLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.RegisterDeviceResponseEvent(true))
//
//        assertTrue(fragment.mIsRefreshing)
//        verify(mockViewModel, times(2)).getChatConversations()
//    }
//
//    @Test
//    fun registerDeviceResponseEventObserver_unsuccessful_doesNothing() {
//        reset(mockViewModel)
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.RegisterDeviceResponseEvent>> = argumentCaptor()
//        verify(mockRegisterDeviceResponseLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.RegisterDeviceResponseEvent(false))
//
//        verify(mockViewModel, never()).getChatConversations()
//    }
//}

