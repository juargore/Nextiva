/*
package com.nextiva.nextivaapp.android.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.di.TestNextivaComponent
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
import com.nextiva.nextivaapp.android.models.ChatConversation
import com.nextiva.nextivaapp.android.models.ChatMessage
import com.nextiva.nextivaapp.android.models.UserDetails
import com.nextiva.nextivaapp.android.net.buses.RxBus
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager
import com.nhaarman.mockito_kotlin.*
import io.mockk.every
import io.mockk.mockkStatic
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.inject.Inject

class MessageFragmentViewModelTest : BaseRobolectricTest() {

    private val markAllMessagesReadResponseObserver: Observer<Boolean> = mock()
    private val processChatConversationListItemsFailedObserver: Observer<Void> = mock()
    private val xmppErrorEventObserver: Observer<RxEvents.XmppErrorEvent> = mock()
    private val registerDeviceResponseEventObserver: Observer<RxEvents.RegisterDeviceResponseEvent> = mock()
    private val chatParticipantsChangedEventObserver: Observer<RxEvents.ChatConversationParticipantsEvent> = mock()
    private val baseListItemsLiveDataObserver: Observer<ArrayList<BaseListItem>> = mock()

    private val dbChatMessageLiveData: MutableLiveData<List<ChatMessage>> = MutableLiveData()

    @Inject
    lateinit var xmppConnectionActionManager: XMPPConnectionActionManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var umsRepository: UmsRepository

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var smsManagementRepository: SmsManagementRepository

    private lateinit var viewModel: MessageFragmentViewModel

    override fun setup() {
        super.setup()

        //((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)

        whenever(dbManager.chatMessagesLiveData).thenReturn(dbChatMessageLiveData)

        viewModel = MessageFragmentViewModel(
                ApplicationProvider.getApplicationContext(),
                xmppConnectionActionManager,
                sessionManager,
                calendarManager,
                dbManager,
                umsRepository,
                dialogManager,
                schedulerProvider,
                userRepository,
                smsManagementRepository)

        viewModel.getMarkAllMessagesReadResponseLiveData().observeForever(markAllMessagesReadResponseObserver)
        viewModel.getProcessChatConversationListItemsFailedLiveData().observeForever(processChatConversationListItemsFailedObserver)
        viewModel.getXmppErrorEventLiveData().observeForever(xmppErrorEventObserver)
        viewModel.getRegisterDeviceResponseEventLiveData().observeForever(registerDeviceResponseEventObserver)
        viewModel.getChatParticipantsChangedEventLiveData().observeForever(chatParticipantsChangedEventObserver)
        viewModel.baseListItemsLiveData.observeForever(baseListItemsLiveDataObserver)
    }

    @Test
    fun liveDataChatMapFunction_setsBaseListItemsLiveData() {
        val baseListItemsArgumentCaptor: KArgumentCaptor<ArrayList<BaseListItem>> = argumentCaptor()
        val mockFormatterManager: FormatterManager = mock()

        mockkStatic(FormatterManager::class)
        every { FormatterManager.getInstance() } returns mockFormatterManager

        dbChatMessageLiveData.value = getChatMessageList()

        verify(baseListItemsLiveDataObserver).onChanged(baseListItemsArgumentCaptor.capture())

        val baseListItems = baseListItemsArgumentCaptor.firstValue

        assertEquals(2, baseListItems.size)
        assertTrue(baseListItems[0] is ChatConversationListItem)
    }

    @Test
    fun requestChatParticipants_containsGroupChat_requestsParticipants() {
        viewModel.requestChatParticipants(getMockListItemsWithGroupChat())

        verify(xmppConnectionActionManager).requestChatParticipants("jid@jid.im")
    }

    @Test
    fun requestChatParticipants_noGroupChat_doesntRequestsParticipants() {
        reset(xmppConnectionActionManager)

        viewModel.requestChatParticipants(getMockListItemsWithNoGroupChat())

        verify(xmppConnectionActionManager, never()).requestChatParticipants(any())
    }

    @Test
    fun markAllMessagesRead_callsToUmsRepository() {
        whenever(umsRepository.markAllMessagesRead(viewModel.compositeDisposable)).thenReturn(Single.just(true))

        viewModel.markAllMessagesRead()

        verify(umsRepository).markAllMessagesRead(viewModel.compositeDisposable)
        verify(markAllMessagesReadResponseObserver).onChanged(true)
    }

    @Test
    fun saveChatMessages_callsToDbManager() {
        val mockList: ArrayList<ChatConversation> = ArrayList()

        whenever(dbManager.saveChatMessages(eq(mockList), any())).thenReturn(Completable.complete())

        viewModel.saveChatMessages(mockList)

        verify(dbManager).saveChatMessages(eq(mockList), any())
    }

    @Test
    fun getChatConversations_umsClientIsSetup_callsToUms_unsuccessful() {
        reset(processChatConversationListItemsFailedObserver)
        val event: RxEvents.ChatConversationsResponseEvent = RxEvents.ChatConversationsResponseEvent(false, null)

        whenever(umsRepository.isClientSetup).thenReturn(true)
        whenever(umsRepository.getChatConversations(any())).thenReturn(Single.just(event))

        viewModel.getChatConversations()

        verify(umsRepository).getChatConversations(eq(0))
        verify(processChatConversationListItemsFailedObserver).onChanged(null)
    }

    @Test
    fun getChatConversations_umsClientIsSetup_callsToUms_successful() {
        reset(processChatConversationListItemsFailedObserver)
        val event: RxEvents.ChatConversationsResponseEvent = RxEvents.ChatConversationsResponseEvent(true, ArrayList())

        whenever(umsRepository.isClientSetup).thenReturn(true)
        whenever(umsRepository.getChatConversations(any())).thenReturn(Single.just(event))

        viewModel.getChatConversations()

        verify(umsRepository).getChatConversations(eq(0))
        verify(processChatConversationListItemsFailedObserver, never()).onChanged(null)
    }

    @Test
    fun getChatConversations_umsClientNotSetup_doesntCallToUms() {
        reset(umsRepository)
        whenever(umsRepository.isClientSetup).thenReturn(false)

        viewModel.getChatConversations()

        verify(umsRepository, never()).getChatConversations(eq(0))
    }

    @Test
    fun getUiNameFromJid_callsToDbManager() {
        whenever(dbManager.getUINameFromJid("jid@jid.im")).thenReturn("UiName")

        viewModel.getUiNameFromJid("jid@jid.im")

        verify(dbManager).getUINameFromJid("jid@jid.im")
        assertEquals(viewModel.getUiNameFromJid("jid@jid.im"), "UiName")
    }

    @Test
    fun getOurJid_callsToSessionManager() {
        val mockUserDetails: UserDetails = mock()

        whenever(sessionManager.userDetails).thenReturn(mockUserDetails)
        whenever(mockUserDetails.impId).thenReturn("jid@jid.im")

        assertEquals(viewModel.getOurJid(), "jid@jid.im")
        verify(sessionManager).userDetails
    }

    @Test
    fun onChatConversationParticipantsEvent_setsLiveData() {
        val chatConversationParticipantsEvent = RxEvents.ChatConversationParticipantsEvent(true, "mucJid@jid.im", ArrayList())

        RxBus.publish(chatConversationParticipantsEvent)

        verify(chatParticipantsChangedEventObserver).onChanged(chatConversationParticipantsEvent)
    }

    @Test
    fun onChatConversationParticipantLeftEvent_callsToXmppConnectionManager() {
        val chatConversationParticipantLeftEvent = RxEvents.ChatConversationParticipantLeftEvent("jid@jid.im", "mucJid@jid.im")

        RxBus.publish(chatConversationParticipantLeftEvent)

        verify(xmppConnectionActionManager).requestChatParticipants("mucJid@jid.im")
    }

    @Test
    fun onChatConversationParticipantJoinedEvent_callsToXmppConnectionManager() {
        val chatConversationParticipantJoinedEvent = RxEvents.ChatConversationParticipantJoinedEvent("jid@jid.im", "mucJid@jid.im")

        RxBus.publish(chatConversationParticipantJoinedEvent)

        verify(xmppConnectionActionManager).requestChatParticipants("mucJid@jid.im")
    }

    @Test
    fun onRegisterDeviceResponseEvent_setsLiveData() {
        val registerDeviceResponseEvent = RxEvents.RegisterDeviceResponseEvent(true)

        RxBus.publish(registerDeviceResponseEvent)

        verify(registerDeviceResponseEventObserver).onChanged(registerDeviceResponseEvent)
    }

    @Test
    fun onXmppErrorEvent_setsLiveData() {
        val xmppErrorEvent = RxEvents.XmppErrorEvent(Exception())

        RxBus.publish(xmppErrorEvent)

        verify(xmppErrorEventObserver).onChanged(xmppErrorEvent)
    }

    private fun getChatMessageList(): List<ChatMessage> {
        val chatMessage1 = ChatMessage("messageId1", "to1", "from1", Enums.Chats.ConversationTypes.CHAT, "body1", false, true,
                1234L, "threadId1", null, "en", "participant", "guestFirst1", "guestLast1", Enums.Chats.SentStatus.SUCCESSFUL, "chatWith1")
        val chatMessage2 = ChatMessage("messageId2", "to1", "from1", Enums.Chats.ConversationTypes.CHAT, "body2", false, true,
                1234L, "threadId1", null, "en", "participant", "guestFirst2", "guestLast2", Enums.Chats.SentStatus.SUCCESSFUL, "chatWith1")
        val chatMessage3 = ChatMessage("messageId3", "to3", "from4", Enums.Chats.ConversationTypes.CHAT, "body3", false, true,
                1234L, "threadId2", null, "en", "participant", "guestFirst2", "guestLast2", Enums.Chats.SentStatus.SUCCESSFUL, "chatWith2")
        val chatMessage4 = ChatMessage("messageId4", "to1", "from1", Enums.Chats.ConversationTypes.CHAT, "body4", false, true,
                1234L, "threadId2", null, "en", "participant", "guestFirst2", "guestLast2", Enums.Chats.SentStatus.SUCCESSFUL, "chatWith2")

        return listOf(chatMessage1, chatMessage2, chatMessage3, chatMessage4)
    }

    private fun getMockListItemsWithGroupChat(): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()
        val chatConversation1 = ChatConversation(Enums.Chats.ConversationTypes.GROUP_CHAT)
        chatConversation1.chatWith = "jid@jid.im"
        listItems.add(ChatConversationListItem(chatConversation1, mock()))

        val chatConversation2 = ChatConversation(Enums.Chats.ConversationTypes.CHAT)
        chatConversation2.chatWith = "jid2@jid.im"
        listItems.add(ChatConversationListItem(chatConversation2, mock()))

        return listItems
    }

    private fun getMockListItemsWithNoGroupChat(): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()
        val chatConversation1 = ChatConversation(Enums.Chats.ConversationTypes.CHAT)
        chatConversation1.chatWith = "jid@jid.im"
        listItems.add(ChatConversationListItem(chatConversation1, mock()))

        val chatConversation2 = ChatConversation(Enums.Chats.ConversationTypes.CHAT)
        chatConversation2.chatWith = "jid2@jid.im"
        listItems.add(ChatConversationListItem(chatConversation2, mock()))

        return listItems
    }
}*/
