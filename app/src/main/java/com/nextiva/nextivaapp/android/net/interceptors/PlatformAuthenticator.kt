package com.nextiva.nextivaapp.android.net.interceptors

import com.nextiva.nextivaapp.android.constants.Enums
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class PlatformAuthenticator @Inject constructor() : Authenticator {

    private var authorizationHeader: String? = null

    private val MAX_RETRY_ATTEMPTS = 1
    private var retryCount = 0

    fun setAuthorizationHeader(authorizationHeader: String?) {
        this.authorizationHeader = authorizationHeader
    }

    // --------------------------------------------------------------------------------------------
    // Authenticator Methods
    // --------------------------------------------------------------------------------------------
    override fun authenticate(route: Route?, response: Response): Request? {
        if (retryCount >= MAX_RETRY_ATTEMPTS) {
            // If retry count exceeds maximum attempts, return null to stop retrying.
            return null
        }

        val builder = response.request.newBuilder()

        authorizationHeader?.let { authorizationHeader ->
            builder.header("Authorization", authorizationHeader)
        }

        retryCount++

        val priorResponse = response.priorResponse
        if (priorResponse?.request != null) {
            builder.method(priorResponse.request.method, priorResponse.request.body)
        }

        builder.header("User-Agent", Enums.Net.USER_AGENT)
        return builder.build()
    }

    // --------------------------------------------------------------------------------------------
}