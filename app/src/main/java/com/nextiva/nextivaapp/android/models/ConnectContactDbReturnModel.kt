package com.nextiva.nextivaapp.android.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.nextiva.nextivaapp.android.constants.Enums

data class ConnectContactDbReturnModel(@ColumnInfo(name = "groupValue") private var _groupValue: Int,
        @Embedded var contact: ConnectContactStripped) {
    val groupValue: String
        get() {
        return when (_groupValue) {
            0 -> Enums.Platform.ConnectContactGroups.FAVORITES
            1 -> Enums.Platform.ConnectContactGroups.TEAMMATES
            2 -> Enums.Platform.ConnectContactGroups.BUSINESS
            else -> Enums.Platform.ConnectContactGroups.ALL_CONTACTS
        }
    }
}