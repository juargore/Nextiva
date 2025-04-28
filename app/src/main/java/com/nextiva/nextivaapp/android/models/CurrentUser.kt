package com.nextiva.nextivaapp.android.models

data class CurrentUser(
    val userUuid: String?,
    val corpAccountNumber: String?,
    val userStatus: String?,
    val profileDisplayName: String?,
    val email: String?,
    val profileId: String?,
    val lastName: String?,
    val domain: String?,
    val locationId: String?,
    val loginId: String?,
    val firstName: String?) {

    constructor() : this(null, null, null, null, null, null, null, null, null, null, null)
}
