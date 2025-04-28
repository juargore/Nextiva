package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem

data class ContactListItemLiveDataValue(var listItems: ArrayList<BaseListItem>?, var isSearching: Boolean = false)