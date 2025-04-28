package com.nextiva.nextivaapp.android.util

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager

object ApplicationUtil {

    @JvmStatic
    fun isAppInForeground(application: Application): Boolean {
        val activityManager = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == application.packageName) {
                return true
            }
        }

        return false
    }


    @JvmStatic
    fun isNightModeEnabled(context: Context, settingsManager: SettingsManager): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val currentUserSetting = settingsManager.nightModeState

        return when {
            currentUserSetting == Enums.Session.NightModeState.NIGHT_MODE_STATE_LIGHT -> false
            currentUserSetting == Enums.Session.NightModeState.NIGHT_MODE_STATE_DARK -> true
            currentNightMode == Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    @JvmStatic
    fun updateNightMode(settingsManager: SettingsManager, activity: Activity) {
        if ((settingsManager.nightModeState == Enums.Session.NightModeState.NIGHT_MODE_STATE_DARK && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) ||
                (settingsManager.nightModeState == Enums.Session.NightModeState.NIGHT_MODE_STATE_LIGHT && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) ||
                (settingsManager.nightModeState == Enums.Session.NightModeState.NIGHT_MODE_STATE_AUTO && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) ||
                (settingsManager.nightModeState == Enums.Session.NightModeState.NIGHT_MODE_STATE_SYSTEM_DEFAULT && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {

            when {
                settingsManager.nightModeState == Enums.Session.NightModeState.NIGHT_MODE_STATE_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                settingsManager.nightModeState == Enums.Session.NightModeState.NIGHT_MODE_STATE_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                settingsManager.nightModeState == Enums.Session.NightModeState.NIGHT_MODE_STATE_AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                settingsManager.nightModeState == Enums.Session.NightModeState.NIGHT_MODE_STATE_SYSTEM_DEFAULT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
            }

            ActivityCompat.recreate(activity)
        }
    }

    @JvmStatic
    fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
    }
}