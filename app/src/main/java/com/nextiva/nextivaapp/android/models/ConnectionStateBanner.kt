package com.nextiva.nextivaapp.android.models

data class ConnectionStateBanner(var bannerString: String?,
                                 var shouldShowInternetSettings: Boolean = false) {

    constructor() : this(null, false)
}