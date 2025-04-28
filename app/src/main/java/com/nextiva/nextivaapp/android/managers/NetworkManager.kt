package com.nextiva.nextivaapp.android.managers

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.nextiva.nextivaapp.android.util.extensions.notNull
import java.net.Inet4Address
import javax.inject.Inject

class NetworkManager @Inject constructor(
    private val context: Application
) {

    private lateinit var listener: (() -> Unit)
    private var currentIpAddress: String? = null
    private var connectivityManager: ConnectivityManager? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            val isWiFi = connectivityManager?.getNetworkCapabilities(network)?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI)
            val isCellular = connectivityManager?.getNetworkCapabilities(network)?.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR)

            connectivityManager?.getLinkProperties(connectivityManager?.activeNetwork)?.linkAddresses?.forEach {
                if(it.address is Inet4Address) {
                    if(it.address.hostAddress != currentIpAddress) {
                        if(currentIpAddress.notNull()) {
                            Log.d("NetworkManager", "NetworkManager : Detected an IpAddress change: ${it.address.hostAddress}")
                            listener.invoke()
                        }
                        currentIpAddress = it.address.hostAddress
                    }
                }
            }


            Log.d("NetworkManager", "NetworkManager : Network Available - Wifi: $isWiFi : Cellular: $isCellular : $network")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            val isWiFi = connectivityManager?.getNetworkCapabilities(network)?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI)
            val isCellular = connectivityManager?.getNetworkCapabilities(network)?.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR)

            Log.d("NetworkManager", "NetworkManager : Network Lost - Wifi: $isWiFi : Cellular: $isCellular")
        }
    }

    fun initialize(listener: (() -> Unit)) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.registerDefaultNetworkCallback(networkCallback)
        this.listener = listener
    }



}