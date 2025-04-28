package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

class HealthCheckListItem(var title: String, var isChecking: Boolean = false) : BaseListItem() {
    var enabled: Boolean = true
    var clearResult: (() -> Unit)? = null
    var updateResult: ((Pair<Long?, Boolean>) -> Unit)? = null

    constructor(title: String): this(title, false)
}