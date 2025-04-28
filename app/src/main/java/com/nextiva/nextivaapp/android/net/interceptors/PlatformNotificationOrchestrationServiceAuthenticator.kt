package com.nextiva.nextivaapp.android.net.interceptors

import com.nextiva.nextivaapp.android.constants.Enums
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class PlatformNotificationOrchestrationServiceAuthenticator @Inject constructor() : Authenticator {

    private var authorizationHeader: String? = null

    fun setAuthorizationHeader(authorizationHeader: String?) {
        this.authorizationHeader = authorizationHeader
    }

    // --------------------------------------------------------------------------------------------
    // Authenticator Methods
    // --------------------------------------------------------------------------------------------
    override fun authenticate(route: Route?, response: Response): Request? {
        val builder = response.request.newBuilder()

        authorizationHeader?.let { authorizationHeader ->
            builder.header("Authorization", authorizationHeader)
        }

        val priorResponse = response.priorResponse
        if (priorResponse?.request != null) {
            builder.method(priorResponse.request.method, priorResponse.request.body)
        }
        builder.header("User-Agent", Enums.Net.USER_AGENT)
        return builder.build()
    }

    // --------------------------------------------------------------------------------------------
}