package com.nextiva.nextivaapp.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class NetWorkReceiver extends BroadcastReceiver {
        private static NetWorkListener mListener;

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) && null != context) {
                    int netWorkState = getNetWorkState(context);
                    if (mListener != null) {
                        mListener.onNetworkChange(netWorkState);
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        public interface NetWorkListener {
            public void onNetworkChange(int netMobile);
        }

        private int getNetWorkState(@NonNull Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                    return ConnectivityManager.TYPE_WIFI;
                } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                    return ConnectivityManager.TYPE_WIFI;
                }
            } else {
                return -1;
            }

        }else{
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            Network[] networks = connMgr.getAllNetworks();
            for (int i=0; i < networks.length; i++) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                if (networkInfo.isConnected()) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return ConnectivityManager.TYPE_MOBILE;
                    } else {
                        return ConnectivityManager.TYPE_WIFI;
                    }
                }
            }
        }
        return -1;
    }

    public static void setListener(NetWorkListener listener) {
        mListener = listener;
    }
}
