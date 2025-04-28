package com.nextiva.nextivaapp.android.models.net.platform.featureFlags

data class FeatureFlags(var featureFlags: ArrayList<FeatureFlag>?) {
    fun isFeatureEnabled(feature: String): Boolean {
        featureFlags?.firstOrNull { it.name.equals(feature) }?.let {
            return it.isEnabled == true && it.isManuallyDisabled != true
        }

        return false
    }
}