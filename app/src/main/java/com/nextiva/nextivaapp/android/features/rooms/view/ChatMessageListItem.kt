package com.nextiva.nextivaapp.android.features.rooms.view

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.model.DbChatMessage

class ChatMessageListItem() : BaseListItem() {

    lateinit var chatMessage: DbChatMessage
    lateinit var groupValue: RoomsEnums.ConnectRoomsGroups

    constructor(chatMessage: DbChatMessage, groupValue: RoomsEnums.ConnectRoomsGroups): this() {
        this.chatMessage = chatMessage
        this.groupValue = groupValue
    }
}