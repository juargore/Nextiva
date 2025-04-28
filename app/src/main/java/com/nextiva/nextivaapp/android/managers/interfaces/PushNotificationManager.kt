/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android.managers.interfaces

interface PushNotificationManager {
    fun getToken(): String

    fun deleteToken()

    fun enableFCM()

    fun enableFCMWithResponse(tokenCallback: (String?) -> Unit)

    fun disableFCM()
}