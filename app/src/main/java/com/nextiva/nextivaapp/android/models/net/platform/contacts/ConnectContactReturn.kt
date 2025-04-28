package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.nextiva.nextivaapp.android.models.NextivaContact

data class ConnectContactReturn(val totalCount: Int?,
                                val next: String?,
                                val contactList: ArrayList<NextivaContact>?)