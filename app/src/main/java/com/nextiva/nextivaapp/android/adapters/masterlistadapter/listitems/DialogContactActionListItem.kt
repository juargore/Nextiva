package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

class DialogContactActionListItem(
    var icon: Int,
    var title: String,
    var action: Int,
    var isEnabled: Boolean,
    var isExpandable: Boolean,
    var isExpanded: Boolean,
    var data: Any? = null
) : BaseListItem() {
    constructor(icon: Int, title: String, action: Int) : this(
        icon,
        title,
        action,
        false,
        false,
        false
    )
}