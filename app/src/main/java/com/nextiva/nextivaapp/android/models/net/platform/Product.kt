package com.nextiva.nextivaapp.android.models.net.platform

data class Product(var product: String? = "",
                   var features: List<String>? = null) {

    fun containsFeature(feature: String): Boolean {
        features?.firstOrNull { it == feature }?.let {
            return true
        }

        return false
    }
}