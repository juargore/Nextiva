package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName

data class BulkUpdateUserMessageState(var messageIds: List<String>?,
                                      @SerializedName("userMessageStateDto") var userMessageState: UserMessageState)