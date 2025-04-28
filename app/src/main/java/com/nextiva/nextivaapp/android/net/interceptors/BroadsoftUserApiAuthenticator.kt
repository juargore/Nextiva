package com.nextiva.nextivaapp.android.net.interceptors

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class BroadsoftUserApiAuthenticator @Inject constructor(val authorizationHeader: String) :
    Authenticator {

    // --------------------------------------------------------------------------------------------
    // Authenticator Methods
    // --------------------------------------------------------------------------------------------
    override fun authenticate(route: Route?, response: Response): Request? {
        val builder = response.request.newBuilder()
        builder.header("Authorization", authorizationHeader)
        val priorResponse = response.priorResponse
        if (priorResponse != null) {
            builder.method(priorResponse.request.method, priorResponse.request.body)
        }

        return builder.build()
    }

    // --------------------------------------------------------------------------------------------
}