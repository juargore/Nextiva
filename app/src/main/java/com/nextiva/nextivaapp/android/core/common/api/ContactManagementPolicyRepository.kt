package com.nextiva.nextivaapp.android.core.common.api

import io.reactivex.Single

interface ContactManagementPolicyRepository {
    fun getContactManagementPrivilege(): Single<Boolean>
}
