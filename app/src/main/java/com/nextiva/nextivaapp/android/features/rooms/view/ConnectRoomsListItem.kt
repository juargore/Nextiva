package com.nextiva.nextivaapp.android.features.rooms.view

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom

class ConnectRoomsListItem() : BaseListItem() {

    lateinit var room: DbRoom
    lateinit var groupValue: RoomsEnums.ConnectRoomsGroups

    constructor(room: DbRoom, groupValue: RoomsEnums.ConnectRoomsGroups): this() {
        this.room = room
        this.groupValue = groupValue
    }
}