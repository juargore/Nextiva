package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

class ConnectHomeListItem(var icon: Int,
                          var channel: Int,
                          var count: Int?,
                          var showDivider: Boolean = false) : BaseListItem() {

    constructor(icon: Int, channel: Int, count: Int?): this(icon, channel, count, false)

    var updateCount: ((Int) -> Unit)? = null
}