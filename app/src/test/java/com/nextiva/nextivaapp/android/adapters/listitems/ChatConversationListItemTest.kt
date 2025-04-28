package com.nextiva.nextivaapp.android.adapters.listitems

import com.nextiva.nextivaapp.android.BasePowerMockTest
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.models.ChatConversation
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatConversationListItemTest : BasePowerMockTest() {

    lateinit var listItem: ChatConversationListItem

    private val chatConversation: ChatConversation = ChatConversation(Enums.Chats.ConversationTypes.CHAT)
    private val avatarBytes = byteArrayOf()
    private val formattedTimeString = "2018-01-02"
    private val displayName = "My Name"

    override fun setup() {
        super.setup()

        listItem = ChatConversationListItem(chatConversation, DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, -10, null, Enums.Contacts.PresenceTypes.AVAILABLE))
        listItem.avatarBytes = avatarBytes
        listItem.formattedTimeString = formattedTimeString
        listItem.displayName = displayName
    }

    @Test
    fun getData_returnsCorrectValue() {
        assertEquals(chatConversation, listItem.data)
    }

    @Test
    fun setData_setsCorrectValue() {
        val chatConversation2 = ChatConversation(Enums.Chats.ConversationTypes.GROUP_ALERT)

        assertEquals(chatConversation, listItem.data)

        listItem.data = chatConversation2

        assertEquals(chatConversation2, listItem.data)
    }

    @Test
    fun getPresence_returnsCorrectValue() {
        assertEquals(Enums.Contacts.PresenceStates.AVAILABLE, listItem.presence.state)
    }

    @Test
    fun setPresence_setsCorrectValue() {
        assertEquals(Enums.Contacts.PresenceStates.AVAILABLE, listItem.presence.state)

        listItem.presence.state = Enums.Contacts.PresenceStates.AWAY

        assertEquals(Enums.Contacts.PresenceStates.AWAY, listItem.presence.state)
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

    @Test
    fun getFormattedTimeString_returnsCorrectValue() {
        assertEquals(formattedTimeString, listItem.formattedTimeString)
    }

    @Test
    fun setFormattedTimeString_setsCorrectValue() {
        val formattedTimeString2 = "2020-03-04"

        assertEquals(formattedTimeString, listItem.formattedTimeString)

        listItem.formattedTimeString = formattedTimeString2

        assertEquals(formattedTimeString2, listItem.formattedTimeString)
    }

    @Test
    fun getDisplayName_returnsCorrectValue() {
        assertEquals(displayName, listItem.displayName)
    }

    @Test
    fun setDisplayName_setsCorrectValue() {
        val displayName2 = "New Name"

        assertEquals(displayName, listItem.displayName)

        listItem.displayName = displayName2

        assertEquals(displayName2, listItem.displayName)
    }
}