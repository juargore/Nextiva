package com.nextiva.nextivaapp.android.features.rooms.view

import androidx.lifecycle.LiveData
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SimpleBaseListItem
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.models.ListHeaderRow

class ConnectRoomsHeaderListItem(data: ListHeaderRow,
                                 itemType: RoomsEnums.ConnectRoomsGroups,
                                 countLiveData: LiveData<Int>,
                                 var isExpanded: Boolean) : SimpleBaseListItem<ListHeaderRow?>(data) {
    var itemType: RoomsEnums.ConnectRoomsGroups = itemType
    var updateCount: ((Int) -> Unit)? = null
    var currentCount: Int? = null

    init {
        countLiveData.observeForever { count ->
            currentCount = count
            updateCount?.let { updateCount -> updateCount(count) }
        }
    }
}