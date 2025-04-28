/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android.managers

import android.text.TextUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager
import com.nextiva.nextivaapp.android.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NextivaPushNotificationManager @Inject constructor() : PushNotificationManager {

    private var token: String? = null

    override fun getToken(): String {
        return token ?: ""
    }

    override fun deleteToken() {
        if (!TextUtils.isEmpty(token)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    FirebaseMessaging.getInstance().deleteToken()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun enableFCMWithResponse(tokenCallback: (String?) -> Unit) {
        LogUtil.d("NextivaPushNotificationManager", "enableFCM")
        setupFirebaseMessaging { tokenCallback(it) }
    }

    override fun enableFCM() {
        LogUtil.d("NextivaPushNotificationManager", "enableFCM")
        setupFirebaseMessaging { }
    }

    private fun setupFirebaseMessaging(tokenCallback: (String?) -> Unit) {
        LogUtil.d("NextivaPushNotificationManager", "Setting up FirebaseMessaging")
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    // Could not get FirebaseMessagingToken
                    LogUtil.e("NextivaPushNotificationManager", "Fetching FCM registration token failed: " + task.exception)
                    tokenCallback(null)
                    return@addOnCompleteListener
                }

                token = task.result
                if (token != null) {
                    // Got FirebaseMessagingToken
                    tokenCallback(token)
                } else {
                    LogUtil.e("NextivaPushNotificationManager", "FCM token is null")
                    tokenCallback(null)
                }
            }
    }

    override fun disableFCM() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        token = null
        deleteToken()
    }
}