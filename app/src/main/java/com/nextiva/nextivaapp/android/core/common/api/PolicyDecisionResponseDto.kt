package com.nextiva.nextivaapp.android.core.common.api

import com.google.gson.annotations.SerializedName

data class ContactManagementPolicyResponseDto(
    @SerializedName("action") var action: Action,
    @SerializedName("resource") var resource: Resource,
    @SerializedName("resourceOrg") var resourceOrg: ResourceOrg,
    @SerializedName("grant") var grant: Boolean
)

data class Action(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: ContactManagementPolicyApi.ActionName?
)

data class Resource(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: ContactManagementPolicyApi.ResourceName?,
    @SerializedName("type") var type: ContactManagementPolicyApi.ResourceReferenceType?,
    @SerializedName("scope") var scope: Scope,
    @SerializedName("org") var org: Org
)

data class Scope(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: ContactManagementPolicyApi.ResourceName?
)

data class Org(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("type") var type: ContactManagementPolicyApi.OrgType?,
    @SerializedName("cascade") var cascade: ContactManagementPolicyApi.Cascade?
)

data class ResourceOrg(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("type") var type: ContactManagementPolicyApi.OrgType?,
    @SerializedName("externalId") var externalId: String,
    @SerializedName("cascade") var cascade: ContactManagementPolicyApi.Cascade?
)
