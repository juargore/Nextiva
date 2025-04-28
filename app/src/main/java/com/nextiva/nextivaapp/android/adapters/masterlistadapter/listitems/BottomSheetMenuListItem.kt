package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.constants.Enums

class BottomSheetMenuListItem(var text: String, val icon: Int?, var iconType: Int?, var isSelected: Boolean = false, var isChecked: Boolean = false, var label: String?, var labelColor: Int?, var itemId: String?) {
    constructor(text: String): this(text, null, null, false, false, null, null, null)
    constructor(text: String, isSelected: Boolean): this(text, null, null, isSelected, false, null, null, null)
    constructor(text: String, icon: Int): this(text, icon, Enums.FontAwesomeIconType.REGULAR, false, false, null, null, null)
    constructor(text: String, label: String?, isChecked: Boolean): this(text, null, null, false, isChecked, label, null, null)
    constructor(text: String, icon: Int, label: String?, labelColor: Int?): this(text, icon, null, false, false, label, labelColor, null)
    constructor(text: String, icon: Int, isSelected: Boolean, itemId: String): this(text, icon, Enums.FontAwesomeIconType.REGULAR, isSelected, false, null, null, itemId)
}