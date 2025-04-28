package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.models.NextivaContact

class DialogContactActionHeaderListItem(
    var contact: NextivaContact,
    val phoneTypeInfo: PhoneTypeInfo?,
    val showImport: Boolean,
    val isImported: Boolean
) : BaseListItem() {

    data class PhoneTypeInfo(
        val phoneNumber: String,
        val numberType: Int,
        val color: Int
    )
}

