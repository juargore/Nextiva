package com.nextiva.nextivaapp.android.features.rooms.model

import com.nextiva.nextivaapp.android.db.model.DbPresence

class RoomMessageListItem(var message: DbChatMessage, var presence: DbPresence? = null)
