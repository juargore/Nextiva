/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.models.net.platform.user.device

import com.google.gson.annotations.SerializedName

/**
 * Created by Thaddeus Dannar on 3/1/23.
 */
data class Policies (
    @SerializedName("enableCallDecline") var enableCallDecline: Boolean?)