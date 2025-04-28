package com.nextiva.androidNextivaAuth.data.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class UserInfo(

	@field:SerializedName("com.nextiva.useruuid")
	val comNextivaUseruuid: String? = null,

	@field:SerializedName("com.nextiva.corpAccountNumber")
	val comNextivaCorpAccountNumber: Int? = null,

	@field:SerializedName("com.nextiva.loginId")
	val comNextivaLoginId: String? = null,

	@field:SerializedName("com.nextiva.email")
	val comNextivaEmail: String? = null,

	@field:SerializedName("com.nextiva.firstName")
	val comNextivaFirstName: String? = null,

	@field:SerializedName("com.nextiva.lastName")
	val comNextivaLastName: String? = null
)
