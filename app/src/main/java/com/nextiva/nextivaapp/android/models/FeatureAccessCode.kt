package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.constants.Enums

data class FeatureAccessCode(var code: String?,
                             @Enums.Service.FeatureAccessCodes.FeatureAccessCode
                             var codeName: String?)