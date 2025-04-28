package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.db.model.SocialMediaAccount
import com.nextiva.nextivaapp.android.util.extensions.BiLet
import java.io.Serializable

data class ConnectSocialMedia(
    @SerializedName("type") var type: String?,
    @SerializedName("label") var label: String?,
    @SerializedName("handle") var handle: String?,
) : Serializable {
    fun toSocialMediaAccount(): SocialMediaAccount? {
        return handle?.let {
            SocialMediaAccount(
                type = ConnectSocialMediaType.fromString(this.type).numericType,
                link = this.handle,
            )
        }
    }

    companion object {
        fun fromSocialMediaAccount(socialMediaAccount: SocialMediaAccount): ConnectSocialMedia? =
            BiLet(socialMediaAccount.link, socialMediaAccount.type)?.let { pair ->
                val link = pair.first
                val type = ConnectSocialMediaType.fromIntType(pair.second).value
                ConnectSocialMedia(
                    type = type,
                    label = type,
                    handle = link
                )
            }
    }
}