package com.nextiva.nextivaapp.android.net.interceptors

import com.google.common.util.concurrent.RateLimiter
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

// Limit APIs to 5 calls per second
class RateLimiterInterceptor : Interceptor {

    // https://github.com/google/guava/issues/2797 <- Why RateLimiter is still in Beta.
    private val rateLimiter: RateLimiter = RateLimiter.create(5.0)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        rateLimiter.acquire(1)
        return chain.proceed(chain.request())
    }
}