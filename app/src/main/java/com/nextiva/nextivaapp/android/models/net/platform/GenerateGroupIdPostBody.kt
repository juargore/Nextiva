package com.nextiva.nextivaapp.android.models.net.platform

data class GenerateGroupIdPostBody(
    val considerSenderAsIndividualUser: Boolean = false,
    val contactsId: ArrayList<String> = ArrayList(),
    val phoneNumbers: ArrayList<String> = ArrayList(),
    val teamIds: ArrayList<String> = ArrayList()
)

data class GroupIdResponse(val groupId: String?)
