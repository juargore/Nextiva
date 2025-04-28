package com.nextiva.nextivaapp.android.net.interceptors

import com.nextiva.nextivaapp.android.constants.Enums
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestWithUserAgent: Request = originalRequest.newBuilder()
            .header("User-Agent", Enums.Net.USER_AGENT)
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}