package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.constants.Enums

data class ContactQuery(
    var query: String? = null,
    var filter: IntArray = intArrayOf(
        Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
        Enums.Contacts.ContactTypes.CONNECT_SHARED,
        Enums.Contacts.ContactTypes.CONNECT_USER,
        Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW,
        Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS,
        Enums.Contacts.ContactTypes.CONNECT_TEAM
    ),
    var state : PositionState = PositionState.AllItems
) {

    enum class PositionState {
        AllItems, RecentContacts, Search
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactQuery

        if (query != other.query) return false
        if (!filter.contentEquals(other.filter)) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = query.hashCode()
        result = 31 * result + filter.contentHashCode()
        result = 31 * result + state.hashCode()
        return result
    }
}