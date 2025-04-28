package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.models.ListHeaderRow

class ConnectContactHeaderListItem(data: ListHeaderRow,
                                   @Enums.Platform.ConnectContactGroups.GroupType
                                   itemType: String,
                                   countLiveData: LiveData<Int>,
                                   var isExpanded: Boolean,
                                   var isLoading: Boolean) : SimpleBaseListItem<ListHeaderRow?>(data) {
    @Enums.Platform.ConnectContactGroups.GroupType
    var itemType: String? = itemType
    var updateCount: ((Int) -> Unit)? = null
    var currentCount: Int? = null
    val isLoadingLiveData = MutableLiveData(isLoading)

    init {
        countLiveData.observeForever { count ->
            currentCount = count
            updateCount?.let { updateCount -> updateCount(count) }
        }
    }
}
