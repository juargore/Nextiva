package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.models.ConnectContactStripped
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactListItemViewState

class ConnectContactListItem() : BaseListItem() {

    var strippedContact: ConnectContactStripped? = null
    var nextivaContact: NextivaContact? = null
    var searchTerm: String? = null
    var groupValue: String? = null
    var importState: Int? = null
    var setSelected: (() -> Unit)? = null
    var showIcons: Boolean = true
    var showSelfIndicator: Boolean = false
    var isForSearchInNewMessage: Boolean = false
    var connectContactListItemViewState: ConnectContactListItemViewState? = null
    var isBlocked: Boolean = false

    constructor(strippedContact: ConnectContactStripped, groupValue: String): this() {
        this.strippedContact = strippedContact
        this.groupValue = groupValue
    }

    constructor(strippedContact: ConnectContactStripped, groupValue: String, isBlocked: Boolean): this() {
        this.strippedContact = strippedContact
        this.groupValue = groupValue
        this.isBlocked = isBlocked
    }

    constructor(nextivaContact: NextivaContact, searchTerm: String): this() {
        this.nextivaContact = nextivaContact
        this.searchTerm = searchTerm
    }

    constructor(nextivaContact: NextivaContact, searchTerm: String, importState: Int?): this() {
        this.nextivaContact = nextivaContact
        this.searchTerm = searchTerm
        this.importState = importState
    }

    constructor(nextivaContact: NextivaContact, searchTerm: String, showIcons: Boolean): this() {
        this.nextivaContact = nextivaContact
        this.searchTerm = searchTerm
        this.showIcons = showIcons
    }

    constructor(nextivaContact: NextivaContact, searchTerm: String, showIcons: Boolean, isForSearch: Boolean): this() {
        this.nextivaContact = nextivaContact
        this.searchTerm = searchTerm
        this.showIcons = showIcons
        this.isForSearchInNewMessage = isForSearch
    }

    constructor(nextivaContact: NextivaContact, showIcons: Boolean, showSelfIndicator: Boolean): this() {
        this.nextivaContact = nextivaContact
        this.showIcons = showIcons
        this.showSelfIndicator = showSelfIndicator
    }

    constructor(nextivaContact: NextivaContact, showIcons: Boolean): this() {
        this.nextivaContact = nextivaContact
        this.showIcons = showIcons
    }

    constructor(
        nextivaContact: NextivaContact,
        searchTerm: String,
        connectContactListItemViewState: ConnectContactListItemViewState
    ) : this() {
        this.nextivaContact = nextivaContact
        this.searchTerm = searchTerm
        this.connectContactListItemViewState = connectContactListItemViewState
    }
}