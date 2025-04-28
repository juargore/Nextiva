/*
 * Copyright (c) 2024 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.platform.user.info

import com.google.gson.annotations.SerializedName

data class UserAdmin(
    @SerializedName("superAdmin") var superAdmin: Boolean
)
