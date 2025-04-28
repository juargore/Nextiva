package com.nextiva.nextivaapp.android.models.net.platform

data class Products(private var products: ArrayList<Product>?) {
    fun isFeatureEnabled(feature: String): Boolean {
        products?.forEach { product ->
            if (product.containsFeature(feature)) {
                return true
            }
        }

        return false
    }
}