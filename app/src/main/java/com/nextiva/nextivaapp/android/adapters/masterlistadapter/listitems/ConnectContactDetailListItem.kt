package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.constants.Enums

open class ConnectContactDetailListItem(var uiName: String?,
                                        var title: String?,
                                        var subtitle: String?,
                                        var isClickable: Boolean,
                                        var maxSubtitleLines: Int,
                                        var iconId: Int,
                                        var isBlocked: Boolean = false,
                                        @Enums.FontAwesomeIconType.Type var iconType: Int,
                                        @Enums.Platform.ConnectContactDetailClickAction.Action var actionType: Int?) : BaseListItem() {

    constructor(uiName: String?,
                title: String?,
                subtitle: String?,
                isClickable: Boolean,
                iconId: Int,
                @Enums.FontAwesomeIconType.Type iconType: Int,
                @Enums.Platform.ConnectContactDetailClickAction.Action actionType: Int) :
            this(uiName, title, subtitle, isClickable, 1, iconId, false, iconType, actionType)

    constructor(uiName: String?,
                title: String?,
                subtitle: String?,
                isClickable: Boolean,
                iconId: Int,
                isBlocked: Boolean,
                @Enums.FontAwesomeIconType.Type iconType: Int,
                @Enums.Platform.ConnectContactDetailClickAction.Action actionType: Int) :
            this(uiName, title, subtitle, isClickable, 1, iconId, isBlocked, iconType, actionType)
}