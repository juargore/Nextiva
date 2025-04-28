package com.nextiva.nextivaapp.android.core.analytics.interfaces

interface AnalyticEvent {
    var name: String
    var properties: Map<String, Any>?
}