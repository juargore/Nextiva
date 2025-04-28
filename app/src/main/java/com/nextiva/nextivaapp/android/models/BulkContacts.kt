package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContact

data class BulkContacts(
    val contacts: List<ConnectContact>,
    val duplicateUpdateStrategyType: String
)