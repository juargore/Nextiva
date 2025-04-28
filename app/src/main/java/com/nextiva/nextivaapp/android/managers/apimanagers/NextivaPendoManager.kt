package com.nextiva.nextivaapp.android.managers.apimanagers

import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.managers.interfaces.PendoManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.net.PendoRequestBody
import com.nextiva.nextivaapp.android.net.PendoApi
import com.nextiva.nextivaapp.android.util.LogUtil
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NextivaPendoManager @Inject constructor(
    private val api: PendoApi,
    private val preferencesManager: SharedPreferencesManager
): PendoManager {

    override suspend fun getPendoData(email: String) : String? {
        // ISO8601 (for example 2006-01-02T15:04:05.999-05:00)
        var iso8601 = Regex("\\d{4}-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d|Z")

        val timestamp: String? = try {
            preferencesManager.getString(SharedPreferencesManager.PENDO_FIRST_VISIT_ANDROID, null)
        } catch(e: Exception) {
            preferencesManager.removeKey(SharedPreferencesManager.PENDO_FIRST_VISIT_ANDROID)
            null
        }
        return timestamp?.takeIf { iso8601.containsMatchIn(it) }?.apply {
        } ?: run {
            try {
                val response = api.getPendoData(
                    appPendoKey = BuildConfig.PENDO_API_KEY,
                    integrationKey = BuildConfig.PENDO_INTEGRATION_KEY,
                    body = PendoRequestBody(email)
                )
                response.body()?.results?.firstOrNull()?.firstAndroidNativeVisit?.takeIf {
                    iso8601.containsMatchIn(it)
                } ?: run {
                    iso8601Format(Date.from(Instant.now()))
                }.apply {
                    preferencesManager.setString(SharedPreferencesManager.PENDO_FIRST_VISIT_ANDROID, this)
                }
            } catch (e: Exception) {
                LogUtil.d("PendoManager", " Error GetPendoData : ${e.message}, generating local timestamp....")
                null
            }
        }
    }

    private fun iso8601Format(date: Date): String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(date)
    }
}