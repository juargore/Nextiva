package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.google.firebase.perf.metrics.resource.ResourceType
import com.nextiva.nextivaapp.android.constants.Enums

data class ConnectContactCategoryListItem(
    val title: Any?,
    val data: Any? = null,
    val clipboardTag: String? = null,
    val isBlocked: Boolean? = null,
    val isPhoneOrExtension: Boolean? = null,
    @ResourceType val textColor: Int,
    @ResourceType val textStyle: Int,
    @ResourceType val iconId: Int? = null,
    @ResourceType val topPadding: Int? = null,
    @Enums.FontAwesomeIconType.Type val iconType: Int? = null
) : BaseListItem()
