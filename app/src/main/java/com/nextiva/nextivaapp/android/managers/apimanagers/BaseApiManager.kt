/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.util.StringUtil.redactApiUrl
import retrofit2.HttpException
import retrofit2.Response

/**
 * Created by adammacdonald on 2/2/18.
 */
abstract class BaseApiManager(
    @JvmField protected val mApplication: Application,
    @JvmField protected val mLogManager: LogManager
) {
    val appVersionHeader: String
        get() = "android-" + BuildConfig.VERSION_NAME

    fun logServerSuccess(response: Response<*>) {
        response.raw().request.let { request ->
            mLogManager.logToFile(Enums.Logging.STATE_INFO, "API Success ${request.method} ${redactApiUrl(request.url.toString())}, Code: [${response.code()}]")
        }
    }

    protected fun logServerParseFailure(response: Response<*>) {
        response.raw().request.let { request ->
            mLogManager.logToFile(Enums.Logging.STATE_FAILURE, "API Failure ${request.method} ${redactApiUrl(request.url.toString())}, Code: [${response.code()}]")
        }
    }

    protected fun logServerResponseError(throwable: Throwable) {
        val message = if (throwable is HttpException && throwable.response()?.raw()?.request != null) {
            val request = throwable.response()?.raw()?.request
            "API Error ${request?.method} ${redactApiUrl(request?.url.toString())} ${redactApiUrl(throwable.message)}"

        } else {
            redactApiUrl(throwable.message)
        }

        FirebaseCrashlytics.getInstance().recordException(throwable)
        mLogManager.logToFile(Enums.Logging.STATE_ERROR, message)
    }
}
