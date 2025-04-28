package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountInformation(@SerializedName("corpAcctNbr") val corporateAccountNumber: String?,
                              @SerializedName("enterpriseName") val enterpriseName: String?,
                              @SerializedName("featureRolloutGroup") val featureRolloutGroup: Int?,
                              @SerializedName("demoAccount") val demoAccount: Boolean?,
                              @SerializedName("platformVoice") val platformVoice: Boolean?,
                              @SerializedName("hipaa") val hipaa: Boolean?,
                              @SerializedName("channel") val channel: Boolean?,
                              @SerializedName("cluserId") val clusterId: Int?,
                              @SerializedName("onboardingStatus") val onboardingStatus: String?,
                              @SerializedName("numExtensions") val numExtensions: Int?,
                              @SerializedName("domainName") val domainName: String?) : Serializable {

    constructor() : this(null, null, null,
            null, null, null, null, null, null,
            null, null)
}