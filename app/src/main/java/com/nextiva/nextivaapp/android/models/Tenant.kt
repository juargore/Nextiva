package com.nextiva.nextivaapp.android.models


import com.google.gson.annotations.SerializedName

data class Tenant(
    @field:SerializedName("voice")
    val voice: Boolean? = null,

    @field:SerializedName("userDescription")
    val userDescription: String? = null,

    @field:SerializedName("accountName")
    val accountName: String? = null,

    @field:SerializedName("domain")
    val domain: String? = null,

    @field:SerializedName("accountNumber")
    val accountNumber: Int? = null)
