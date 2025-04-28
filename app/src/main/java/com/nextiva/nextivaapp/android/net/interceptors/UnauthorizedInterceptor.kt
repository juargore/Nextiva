/*
 * Copyright (c) 2024. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.net.interceptors

import com.nextiva.nextivaapp.android.constants.Enums.ResponseCodes.ClientFailureResponses.UNAUTHORIZED
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.util.LogUtil
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Created by Thaddeus Dannar on 6/25/24.
 */
class UnauthorizedInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == UNAUTHORIZED) {
            LogUtil.d("UnauthorizedInterceptor", "HTTP Received 401 error")
            sessionManager.sessionId = null
        }

        return response
    }
}
