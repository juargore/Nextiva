package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.models.CallLogEntry

class ConnectCallHistoryListItem(
    val callEntry: CallLogEntry,
    val searchTerm: String?,
    var isChecked: Boolean?,
    var isBlocked: Boolean = false
): BaseListItem()