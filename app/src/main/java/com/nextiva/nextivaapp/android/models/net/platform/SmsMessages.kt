package com.nextiva.nextivaapp.android.models.net.platform

data class SmsMessages(
    var `data`: List<Data>? = null,
    var first: String? = null,
    var last: String? = null,
    var next: String? = null,
    var prev: String? = null,
    var totalCount: Int? = null
)