package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import java.io.Serializable

data class SendToFromItem(
    val toItems: ArrayList<BottomSheetMenuListItem>,
    val fromItems: ArrayList<BottomSheetMenuListItem>,
    val hasDisabledPrimaryNumber: Boolean,
    val shouldShowMultipleContacts: Boolean,
    val doneClickListener: (BottomSheetMenuListItem?, BottomSheetMenuListItem?) -> (Unit)
) : Serializable
