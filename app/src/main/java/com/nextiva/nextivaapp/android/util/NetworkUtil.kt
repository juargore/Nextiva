package com.nextiva.nextivaapp.android.util

import android.content.Context
import android.net.ConnectivityManager
import com.nextiva.nextivaapp.android.constants.Enums

class NetworkUtil {

    companion object {
        @JvmStatic
        fun getConnectivityStatus(context: Context): Int {
            val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connectivityManager.activeNetworkInfo?.let {
                return when (it.type) {
                    ConnectivityManager.TYPE_WIFI -> Enums.Net.InternetConnectionTypes.WIFI
                    ConnectivityManager.TYPE_MOBILE -> Enums.Net.InternetConnectionTypes.MOBILE
                    ConnectivityManager.TYPE_ETHERNET -> Enums.Net.InternetConnectionTypes.ETHERNET
                    else -> Enums.Net.InternetConnectionTypes.NOT_CONNECTED
                }
            }

            return Enums.Net.InternetConnectionTypes.NOT_CONNECTED
        }
    }
}