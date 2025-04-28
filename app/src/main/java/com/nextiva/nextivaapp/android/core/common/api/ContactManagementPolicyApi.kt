package com.nextiva.nextivaapp.android.core.common.api

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.io.Serializable

interface ContactManagementPolicyApi {
    @POST("rest-api/authorization/v1/policyDecisionRequests")
    fun getContactManagementPrivilege(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String,
        @Body body: Array<ContactManagementPrivilegeRequestBody> = arrayOf(
            ContactManagementPrivilegeRequestBody(corpAccountNumber)
        )
    ) : Single<Response<Array<ContactManagementPolicyResponseDto>>>

    data class ContactManagementPrivilegeRequestBody(
        @SerializedName("action") var action: Action,
        @SerializedName("resource") var resource: Resource,
        @SerializedName("resourceOrg") var resourceOrg: ResourceOrg
    ) : Serializable {
        constructor(corpAccountNumber: String) : this(Action(), Resource(), ResourceOrg(corpAccountNumber))
    }

    data class Action(
        @SerializedName("name") var name: ActionName
    ) : Serializable {
        constructor() : this(ActionName.DELETE)
    }

    data class Resource(
        @SerializedName("name") var name: ResourceName,
        @SerializedName("org") var org: Org,
        @SerializedName("scope") var scope: Scope,
        @SerializedName("type") var type: ResourceReferenceType
    ) : Serializable {
        constructor() : this(
            ResourceName.BUSINESS_CONTACT, Org(), Scope(),
            ResourceReferenceType.ENTITY
        )
    }

    data class Org(
        @SerializedName("id") var id: Int
    ) : Serializable {
        constructor() : this(1)
    }

    data class Scope(
        @SerializedName("name") var name: ResourceName
    ) : Serializable {
        constructor() : this(ResourceName.PLATFORM_CONTACT)
    }

    data class ResourceOrg(
        @SerializedName("externalId") var externalId: String,
        @SerializedName("type") var type: OrgType
    ) : Serializable {
        constructor(corpAccountNumber: String) : this(corpAccountNumber, OrgType.CORP_ACCOUNT)
    }

    enum class Cascade { EXPLICIT, IMPLICIT, NONE }
    enum class ActionName { DELETE }
    enum class OrgType { CORP_ACCOUNT, LOCATION, MASTER_ACCOUNT, MEETING, ORG, TEAM }
    enum class ResourceReferenceType { ATTRIBUTE, ENTITY }
    enum class ResourceName { BUSINESS_CONTACT, PLATFORM_CONTACT }

}
