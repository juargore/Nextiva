package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * == External Source Fields ==
 *
 * Swagger documentation:
 * https://platform-contacts-orchestration-service.qa.s1.nextiva.io/swagger-ui/index.html#/BulkAction/importExternalSourceContactsV2
 *
 * @param externalId: External Identifier of the contact
 * @param sourceIdentity: minLength: 0 | maxLength: 1024 | An identity from the source provider. ie. email or account id.
 * @param type: Enum [ broadsoft, callCenter, callFlow, csv, gmail, mobile, native-google, native-ms365, nextos_crm, o365, team, user ]
 * */
data class ConnectContactExternalSource(
    @SerializedName("externalId") var externalId: String?,
    @SerializedName("sourceIdentity") var sourceIdentity: String?,
    @SerializedName("type") var type: String?,
) : Serializable
