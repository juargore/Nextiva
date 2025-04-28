package com.nextiva.nextivaapp.android.adapters.listitems

import com.nextiva.nextivaapp.android.BasePowerMockTest
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatMessageListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.models.ChatMessage
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatMessageListItemTest : BasePowerMockTest() {

    lateinit var listItem: ChatMessageListItem

    private val chatMessage: ChatMessage = ChatMessage(null, null, null, Enums.Chats.ConversationTypes.GROUP_ALERT, null, false, false, null, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null)
    private val bubbleType = Enums.Chats.MessageBubbleTypes.END
    private val humanReadableDatetime = "Today"
    private val avatarBytes = byteArrayOf()

    override fun setup() {
        super.setup()

        listItem = ChatMessageListItem(chatMessage, bubbleType, humanReadableDatetime, avatarBytes)
    }

    @Test
    fun getData_returnsCorrectValue() {
        assertEquals(chatMessage, listItem.data)
    }

    @Test
    fun setData_setsCorrectValue() {
        val chatMessage2 = ChatMessage(null, null, null, Enums.Chats.ConversationTypes.CHAT, null, true, false, null, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null)

        assertEquals(chatMessage, listItem.data)

        listItem.data = chatMessage2

        assertEquals(chatMessage2, listItem.data)
    }

    @Test
    fun getBubbleType_returnsCorrectValue() {
        assertEquals(Enums.Chats.MessageBubbleTypes.END, listItem.bubbleType)
    }

    @Test
    fun setBubbleType_setsCorrectValue() {
        assertEquals(Enums.Chats.MessageBubbleTypes.END, listItem.bubbleType)

        listItem.bubbleType = Enums.Chats.MessageBubbleTypes.MIDDLE

        assertEquals(Enums.Chats.MessageBubbleTypes.MIDDLE, listItem.bubbleType)
    }

    @Test
    fun getHumanReadableDatetime_returnsCorrectValue() {
        assertEquals("Today", listItem.humanReadableDatetime)
    }

    @Test
    fun setHumanReadableDatetime_setsCorrectValue() {
        val humanReadableDatetime2 = "Yesterday"

        assertEquals("Today", listItem.humanReadableDatetime)

        listItem.humanReadableDatetime = humanReadableDatetime2

        assertEquals(humanReadableDatetime2, listItem.humanReadableDatetime)
    }

    @Test
    fun getAvatarBytes_returnsCorrectValue() {
        assertEquals(avatarBytes, listItem.avatarBytes)
    }

    @Test
    fun setAvatarBytes_setsCorrectValue() {
        val avatarBytes2 = byteArrayOf(2)

        assertEquals(avatarBytes, listItem.avatarBytes)

        listItem.avatarBytes = avatarBytes2

        assertEquals(avatarBytes2, listItem.avatarBytes)
    }
}