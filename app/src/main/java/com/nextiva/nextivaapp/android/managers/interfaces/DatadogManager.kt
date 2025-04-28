package com.nextiva.nextivaapp.android.managers.interfaces

interface DatadogManager {
    fun setUserInfo()

    // Java compat
    fun monitorError(name: String, attributes: ArrayList<Pair<String, Object>>)

    // Java compat
    fun performCustomAction(name: String, attributes: ArrayList<Pair<String, Object>>)

    fun monitorError(name: String, attributes: Map<String, Any?>)

    fun performCustomAction(name: String, attributes: Map<String, Any?>)

    fun getAttribute(name: String, value: Any?): Pair<String, Any?>
}