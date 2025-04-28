package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.models.ListHeaderRow

class ConnectContactDetailHeaderListItem(data: ListHeaderRow, var baseListItemsList: ArrayList<BaseListItem>?, itemType: String, var isExpanded: Boolean, var isShowingMore: Boolean, var shouldShowHeaderDetails: Boolean) : SimpleBaseListItem<ListHeaderRow?>(data) {
    var itemType: String? = itemType

    fun addToListItems(listItem: BaseListItem) {
        if (baseListItemsList == null) {
            baseListItemsList = ArrayList()
        }

        baseListItemsList?.add(listItem)
    }
}