package com.nextiva.nextivaapp.android.view.compose.viewstate
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.models.AvatarInfo
data class ConnectContactListItemViewState(
    val id: String? = null,
    val contactName: String? = null,
    val contactPhoneNumbers: ArrayList<PhoneNumber>? = ArrayList(),
    val avatarInfo: AvatarInfo? = null,
    val hasLeftNWay: Boolean = false,
    val onItemClick: ((String) -> Unit)? = null,
    val searchTerm: String? = null
)
