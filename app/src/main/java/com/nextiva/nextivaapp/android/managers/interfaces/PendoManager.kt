package com.nextiva.nextivaapp.android.managers.interfaces

interface PendoManager {
    suspend fun getPendoData(email: String) : String?
}