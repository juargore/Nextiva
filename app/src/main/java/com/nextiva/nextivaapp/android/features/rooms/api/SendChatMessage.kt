package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendChatMessageRequest(@SerializedName("text") var text: String,
                                  @SerializedName("memberDetails") var memberDetails: ArrayList<SendMessageMemberDetails>?) : Serializable

data class SendChatMessageResponse(@SerializedName("messageId") var messageId: String) : Serializable

data class SendMessageMemberDetails(@SerializedName("id") var id: String,
                                    @SerializedName("firstName") var firstName: String,
                                    @SerializedName("lastName") var lastName: String,
                                    @SerializedName("email") var email: String) : Serializable
