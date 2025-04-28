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
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.db.model.DbPresence
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.models.CallInfo
//import com.nextiva.nextivaapp.android.models.NextivaContact
//import com.nextiva.nextivaapp.android.net.buses.RxEvents
//import com.nextiva.nextivaapp.android.util.StringUtil
//import com.nextiva.nextivaapp.android.viewmodels.ChatConversationViewModel
//import com.nhaarman.mockito_kotlin.*
//import io.mockk.every
//import io.mockk.mockkStatic
//import org.junit.Assert.*
//import org.junit.Test
//import org.robolectric.fakes.RoboMenuItem
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import java.io.IOException
//import javax.inject.Inject
//
//class ChatConversationFragmentTest : BaseRobolectricTest() {
//    @Inject
//    lateinit var callManager: CallManager
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var fragment: ChatConversationFragment
//
//    private val mockViewModel: ChatConversationViewModel = mock()
//
//    private val mockInitGroupChatLiveData: LiveData<Void> = mock()
//    private val mockUiNameTextChangedLiveData: LiveData<NextivaContact?> = mock()
//    private val mockXmppErrorEventLiveData: LiveData<RxEvents.XmppErrorEvent> = mock()
//    private val mockOpenGroupChatUiNameTextChangedLiveData: LiveData<ArrayList<String>> = mock()
//    private val mockChatConversationJoinedLiveData: LiveData<Boolean> = mock()
//    private val mockChatConversationLeftLiveData: LiveData<Boolean> = mock()
//    private val mockGenericErrorEventLiveData: LiveData<Void> = mock()
//    private val mockChatMessagesLiveData: LiveData<ArrayList<BaseListItem>> = mock()
//    private val mockContactActionLiveData: LiveData<RxEvents.EnterpriseContactByImpIdResponseEvent> = mock()
//    private val mockPresenceLiveData: LiveData<DbPresence> = mock()
//    private val mockSendPendingChatMessagesLiveData: LiveData<Boolean> = mock()
//    private val mockIsXmppConnectingLiveData: LiveData<Boolean> = mock()
//    private val mockShowNoInternetLiveData: LiveData<Void> = mock()
//
//    @Throws(IOException::class)
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(ChatConversationViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.getInitGroupChatLiveData()).thenReturn(mockInitGroupChatLiveData)
//        whenever(mockViewModel.getUiNameTextChangedLiveData()).thenReturn(mockUiNameTextChangedLiveData)
//        whenever(mockViewModel.getXmppErrorEventLiveData()).thenReturn(mockXmppErrorEventLiveData)
//        whenever(mockViewModel.getOpenGroupChatUiNameTextChangedLiveData()).thenReturn(mockOpenGroupChatUiNameTextChangedLiveData)
//        whenever(mockViewModel.getChatConversationJoinedLiveData()).thenReturn(mockChatConversationJoinedLiveData)
//        whenever(mockViewModel.getChatConversationLeftLiveData()).thenReturn(mockChatConversationLeftLiveData)
//        whenever(mockViewModel.getGenericErrorEventLiveData()).thenReturn(mockGenericErrorEventLiveData)
//        whenever(mockViewModel.getChatMessagesLiveData()).thenReturn(mockChatMessagesLiveData)
//        whenever(mockViewModel.getContactActionLiveData()).thenReturn(mockContactActionLiveData)
//        whenever(mockViewModel.getPresenceLiveData()).thenReturn(mockPresenceLiveData)
//        whenever(mockViewModel.getSendPendingChatMessagesLiveData()).thenReturn(mockSendPendingChatMessagesLiveData)
//        whenever(mockViewModel.getIsXmppConnectingLiveData()).thenReturn(mockIsXmppConnectingLiveData)
//        whenever(mockViewModel.getShowNoInternetLiveData()).thenReturn(mockShowNoInternetLiveData)
//
//        whenever(mockViewModel.chatType).thenReturn(Enums.Chats.ConversationTypes.GROUP_CHAT)
//        whenever(mockViewModel.toJid).thenReturn("jid@jid.im")
//        fragment = ChatConversationFragment.newInstance("jid@jid.im", Enums.Chats.ConversationTypes.GROUP_CHAT, null, true, false)
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
//    fun onCreateViews_getsLiveData() {
//        verify(mockViewModel).getInitGroupChatLiveData()
//        verify(mockViewModel).getUiNameTextChangedLiveData()
//        verify(mockViewModel).getXmppErrorEventLiveData()
//        verify(mockViewModel).getOpenGroupChatUiNameTextChangedLiveData()
//        verify(mockViewModel).getChatConversationJoinedLiveData()
//        verify(mockViewModel).getChatConversationLeftLiveData()
//        verify(mockViewModel).getGenericErrorEventLiveData()
//        verify(mockViewModel).getChatMessagesLiveData()
//        verify(mockViewModel).getContactActionLiveData()
//    }
//
//    @Test
//    fun sendImageOnClick_sendsMessage() {
//        fragment.messageEditText.setText("A Message")
//        fragment.sendImageView.performClick()
//
//        verify(mockViewModel).sendChatMessage("A Message")
//        assertTrue(fragment.messageEditText.text.isEmpty())
//        assertEquals(false, fragment.sendImageView.isEnabled)
//    }
//
//    @Test
//    fun onOptionsItemSelected_viewProfile_callsToViewModel() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.chat_conversation_contact_action_view_profile)
//
//        fragment.onOptionsItemSelected(menuItem)
//
//        verify(fragment.mDialogManager).showProgressDialog(any(), any(), any())
//        verify(mockViewModel).contactAction(null)
//    }
//
//    @Test
//    fun onOptionsItemSelected_call_callsToViewModel() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.chat_conversation_contact_action_call)
//
//        fragment.onOptionsItemSelected(menuItem)
//
//        verify(fragment.mDialogManager).showProgressDialog(any(), any(), any())
//        verify(mockViewModel).contactAction(Enums.Sip.CallTypes.VOICE)
//    }
//
//    @Test
//    fun onOptionsItemSelected_viewProfile_callsToViewProfile() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.chat_conversation_contact_action_video)
//
//        fragment.onOptionsItemSelected(menuItem)
//
//        verify(fragment.mDialogManager).showProgressDialog(any(), any(), any())
//        verify(mockViewModel).contactAction(Enums.Sip.CallTypes.VIDEO)
//    }
//
//    @Test
//    fun onOptionsSelected_leaveChat_callsToViewModel() {
//        reset(mockViewModel)
//
//        val menuItem = RoboMenuItem(R.id.chats_details_menu_item_leave_chat)
//
//        fragment.onOptionsItemSelected(menuItem)
//
//        verify(mockViewModel).leaveGroupChat()
//    }
//
//    @Test
//    fun initGroupChatObserver_setsUpGroupChatUi() {
//        reset(fragment.mDialogManager)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Void>> = argumentCaptor()
//        verify(mockInitGroupChatLiveData).observe(any(), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        assertEquals(View.VISIBLE, fragment.groupChatIcon.visibility)
//        verify(fragment.mDialogManager).showProgressDialog(any(), any(), any())
//        verify(mockViewModel).joinGroupChat()
//    }
//
//    @Test
//    fun xmppErrorEventObserver_showsDialog() {
//        reset(fragment.mDialogManager)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.XmppErrorEvent>> = argumentCaptor()
//        verify(mockXmppErrorEventLiveData).observe(any(), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.XmppErrorEvent(Exception()))
//
//        verify(fragment.mDialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun chatConversationJoinObserver_roomIsClosed_setsUi() {
//        reset(fragment.mDialogManager)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockChatConversationJoinedLiveData).observe(any(), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        verify(fragment.mDialogManager).dismissProgressDialog()
//        assertEquals("Group Chat (Closed)", fragment.uiNameTextView.text)
//        assertFalse(fragment.messageEditText.isEnabled)
//    }
//
//    @Test
//    fun chatConversationJoinObserver_roomIsntClosed_doesntSetUi() {
//        reset(fragment.mDialogManager)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockChatConversationJoinedLiveData).observe(any(), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        verify(fragment.mDialogManager).dismissProgressDialog()
//        assertNotSame("Group Chat (Closed)", fragment.uiNameTextView.text)
//    }
//
//    @Test
//    fun chatConversationLeftObserver_isSuccessful_noErrorDialog() {
//        reset(fragment.mDialogManager)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockChatConversationLeftLiveData).observe(any(), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        verify(fragment.mDialogManager).dismissProgressDialog()
//        verify(fragment.mDialogManager, never()).showErrorDialog(any(), any())
//    }
//
//    @Test
//    fun chatConversationLeftObserver_notSuccessful_showsErrorDialog() {
//        reset(fragment.mDialogManager)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockChatConversationLeftLiveData).observe(any(), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(false)
//
//        verify(fragment.mDialogManager).dismissProgressDialog()
//        verify(fragment.mDialogManager).showErrorDialog(any(), any())
//    }
//
//    @Test
//    fun openGroupChatUiNameTextChangedObserver_noUiNames() {
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<String>>> = argumentCaptor()
//        verify(mockOpenGroupChatUiNameTextChangedLiveData).observe(any(), argumentCaptor.capture())
//
//        val uiNames: ArrayList<String> = ArrayList()
//        argumentCaptor.firstValue.onChanged(uiNames)
//
//        assertEquals("Group Chat", fragment.uiNameTextView.text)
//    }
//
//    @Test
//    fun openGroupChatUiNameTextChangedObserver_setsUiNames() {
//        val argumentCaptor: KArgumentCaptor<Observer<ArrayList<String>>> = argumentCaptor()
//        verify(mockOpenGroupChatUiNameTextChangedLiveData).observe(any(), argumentCaptor.capture())
//
//        mockkStatic(StringUtil::class)
//        every { StringUtil.getGroupChatParticipantsString(any(), any(), any(), any()) } returns "UI NAMES"
//
//        val uiNames: ArrayList<String> = ArrayList()
//        uiNames.add("Ui Name")
//        argumentCaptor.firstValue.onChanged(uiNames)
//
//        assertEquals("UI NAMES", fragment.uiNameTextView.text)
//    }
//
//    @Test
//    fun uiNameTextChangedObserver_noContact_groupChatClosed() {
//        val argumentCaptor: KArgumentCaptor<Observer<NextivaContact?>> = argumentCaptor()
//        verify(mockUiNameTextChangedLiveData).observe(any(), argumentCaptor.capture())
//
//        whenever(mockViewModel.chatType).thenReturn(Enums.Chats.ConversationTypes.GROUP_CHAT)
//        whenever(mockViewModel.isRoomClosed).thenReturn(true)
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        assertEquals("Group Chat (Closed)", fragment.uiNameTextView.text)
//    }
//
//    @Test
//    fun uiNameTextChangedObserver_noContact_groupChatOpen() {
//        val argumentCaptor: KArgumentCaptor<Observer<NextivaContact?>> = argumentCaptor()
//        verify(mockUiNameTextChangedLiveData).observe(any(), argumentCaptor.capture())
//
//        whenever(mockViewModel.chatType).thenReturn(Enums.Chats.ConversationTypes.GROUP_CHAT)
//        whenever(mockViewModel.isRoomClosed).thenReturn(false)
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        assertEquals("Group Chat", fragment.uiNameTextView.text)
//    }
//
//    @Test
//    fun uiNameTextChangedObserver_noContact_singleUserChat() {
//        val argumentCaptor: KArgumentCaptor<Observer<NextivaContact?>> = argumentCaptor()
//        verify(mockUiNameTextChangedLiveData).observe(any(), argumentCaptor.capture())
//
//        whenever(mockViewModel.chatType).thenReturn(Enums.Chats.ConversationTypes.CHAT)
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        assertEquals("jid@jid.im", fragment.uiNameTextView.text)
//    }
//
//    @Test
//    fun uiNameTextChangedObserver_hasContact_setsUi() {
//        val argumentCaptor: KArgumentCaptor<Observer<NextivaContact?>> = argumentCaptor()
//        verify(mockUiNameTextChangedLiveData).observe(any(), argumentCaptor.capture())
//
//        whenever(mockViewModel.chatType).thenReturn(Enums.Chats.ConversationTypes.GROUP_CHAT)
//        whenever(mockViewModel.isRoomClosed).thenReturn(false)
//
//        val contact = NextivaContact("Hi")
//        contact.displayName = "Display Name"
//        argumentCaptor.firstValue.onChanged(contact)
//
//        assertEquals("Display Name", fragment.uiNameTextView.text)
//    }
//
//    @Test
//    fun genericErrorEventObserver_showsDialog() {
//        reset(fragment.mDialogManager)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Void>> = argumentCaptor()
//        verify(mockGenericErrorEventLiveData).observe(any(), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        verify(fragment.mDialogManager).showErrorDialog(any(), any())
//    }
//
//    @Test
//    fun contactActionResponseObserver_voiceCall_callsToCallManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.EnterpriseContactByImpIdResponseEvent>> = argumentCaptor()
//        val callInfoCaptor: KArgumentCaptor<CallInfo> = argumentCaptor()
//        verify(mockContactActionLiveData).observe(any(), argumentCaptor.capture())
//
//        val contact = NextivaContact("Hi")
//
//        argumentCaptor.firstValue.onChanged(RxEvents.EnterpriseContactByImpIdResponseEvent(true, contact, Enums.Sip.CallTypes.VOICE))
//        verify(callManager).makeCall(any(), any(), callInfoCaptor.capture(), any())
//
//        assertEquals(Enums.Sip.CallTypes.VOICE, callInfoCaptor.firstValue.callType)
//        assertEquals(contact, callInfoCaptor.firstValue.nextivaContact)
//    }
//
//    @Test
//    fun contactActionResponseObserver_videoCall_callsToCallManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.EnterpriseContactByImpIdResponseEvent>> = argumentCaptor()
//        val callInfoCaptor: KArgumentCaptor<CallInfo> = argumentCaptor()
//        verify(mockContactActionLiveData).observe(any(), argumentCaptor.capture())
//
//        val contact = NextivaContact("Hi")
//
//        argumentCaptor.firstValue.onChanged(RxEvents.EnterpriseContactByImpIdResponseEvent(true, contact, Enums.Sip.CallTypes.VIDEO))
//        verify(callManager).makeCall(any(), any(), callInfoCaptor.capture(), any())
//
//        assertEquals(Enums.Sip.CallTypes.VIDEO, callInfoCaptor.firstValue.callType)
//        assertEquals(contact, callInfoCaptor.firstValue.nextivaContact)
//    }
//
//    @Test
//    fun contactActionResponseObserver_contactNull_logsEvent() {
//        reset(fragment.mDialogManager)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.EnterpriseContactByImpIdResponseEvent>> = argumentCaptor()
//        verify(mockContactActionLiveData).observe(any(), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.EnterpriseContactByImpIdResponseEvent(true, null, Enums.Sip.CallTypes.VOICE))
//        verify(fragment.analyticsManager).logEvent(any(), any())
//    }
//}