package com.nextiva.nextivaapp.android.managers;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.THERMAL_STATUS_CRITICAL;
import static android.os.PowerManager.THERMAL_STATUS_EMERGENCY;
import static android.os.PowerManager.THERMAL_STATUS_LIGHT;
import static android.os.PowerManager.THERMAL_STATUS_MODERATE;
import static android.os.PowerManager.THERMAL_STATUS_NONE;
import static android.os.PowerManager.THERMAL_STATUS_SEVERE;
import static android.os.PowerManager.THERMAL_STATUS_SHUTDOWN;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.PowerManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.WebSocketManager;
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.util.NetworkUtil;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;

import java.net.Inet6Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NextivaConnectionStateManager implements ConnectionStateManager {

    private final static String ACTION_XMPP_CONNECTION_STATE_CHANGED = "XMPP_CONNECTION_STATE_CHANGED";

    private final MutableLiveData<Integer> mXmppConnectionStateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mInternetConnectionStateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUMSConnectionStateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mSipConnectionStateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mConnectWebSocketConnectStateMutableLiveData = new MutableLiveData<>();

    private final Application mApplication;
    private final XMPPConnectionActionManager mXMPPConnectionActionManager;

    private PJSipManager mSipManager;
    private WebSocketManager mWebSocketManager;

    @Inject
    public NextivaConnectionStateManager(Application application, XMPPConnectionActionManager xmppConnectionActionManager) {
        mApplication = application;
        mXMPPConnectionActionManager = xmppConnectionActionManager;

        setUpNetworkConnectionReceiver();
        mSipConnectionStateMutableLiveData.postValue(true);
    }

    @Override
    public LiveData<Integer> getXmppConnectionStateLiveData() {
        return mXmppConnectionStateMutableLiveData;
    }

    @Override
    public LiveData<Integer> getInternetConnectionStateLiveData() {
        return mInternetConnectionStateMutableLiveData;
    }

    @Override
    public LiveData<Boolean> getUMSConnectionStateLiveData() {
        return mUMSConnectionStateMutableLiveData;
    }

    @Override
    public LiveData<Boolean> getSIPConnectionStateLiveData() {
        return mSipConnectionStateMutableLiveData;
    }

    private void setUpNetworkConnectionReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ACTION_XMPP_CONNECTION_STATE_CHANGED);

        BroadcastReceiver connectionBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = NetworkUtil.getConnectivityStatus(context);

                if (status != Enums.Net.InternetConnectionTypes.NOT_CONNECTED) {
                    if (mSipManager != null && mSipManager.isCallActive()) {
                        if (mSipManager != null) {
                            //mSipManager.connectionHasUpdated();
                        }
                    }

                    if (!isXmppConnected()) {
                        mXMPPConnectionActionManager.startConnection();
                    }

                    if (!isConnectWebSocketConnected() && mWebSocketManager != null) {
                        mWebSocketManager.setup();
                    }

                } else {
                    mXMPPConnectionActionManager.disconnectConnection();
                    mXMPPConnectionActionManager.stopConnection();
                    mUMSConnectionStateMutableLiveData.setValue(false);

                }

                mInternetConnectionStateMutableLiveData.setValue(status);
                thermalStatusUpdate();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mApplication.registerReceiver(connectionBroadcastReceiver, filter, Context.RECEIVER_EXPORTED);

        } else {
            mApplication.registerReceiver(connectionBroadcastReceiver, filter);
        }
    }

    private void thermalStatusUpdate(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) mApplication.getSystemService(POWER_SERVICE);

            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.POWER_SAVING_MODE_ENABLED,
                                                           powerManager.isPowerSaveMode());
            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.DEVICE_IDLE_MODE,
                                                           powerManager.isDeviceIdleMode());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String thermalStatus = "";
                switch (powerManager.getCurrentThermalStatus()) {
                    case THERMAL_STATUS_NONE:
                        thermalStatus = "NONE";
                        break;
                    case THERMAL_STATUS_LIGHT:
                        thermalStatus = "LIGHT";
                        break;
                    case THERMAL_STATUS_MODERATE:
                        thermalStatus = "MODERATE";
                        break;
                    case THERMAL_STATUS_SEVERE:
                        thermalStatus = "SEVERE";
                        break;
                    case THERMAL_STATUS_SHUTDOWN:
                        thermalStatus = "SHUTDOWN";
                        break;
                    case THERMAL_STATUS_CRITICAL:
                        thermalStatus = "CRITICAL";
                        break;
                    case THERMAL_STATUS_EMERGENCY:
                        thermalStatus = "EMERGENCY";
                        break;
                    default:
                        thermalStatus = "";
                        break;
                }

                FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.THERMAL_STATUS,
                                                               thermalStatus);
            }

        }
    }

    @Override
    public void postXmppConnectionState(@Enums.Xmpp.ConnectionStates.ConnectionState final int connectionState) {
        FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.XMPP_STATE, connectionState);
        mXmppConnectionStateMutableLiveData.postValue(connectionState);
    }

    @Override
    public void postIsUMSConnected(final boolean connected) {
        FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.UMS_CONNECTED, connected);
        mUMSConnectionStateMutableLiveData.postValue(connected);
    }

    @Override
    public void postIsConnectWebSocketConnected(final boolean connected) {
        mConnectWebSocketConnectStateMutableLiveData.postValue(connected);
    }

    @Override
    public boolean isXmppConnected() {
        boolean connected = mXmppConnectionStateMutableLiveData.getValue() != null && mXmppConnectionStateMutableLiveData.getValue() == Enums.Xmpp.ConnectionStates.CONNECTED;
        FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.XMPP_CONNECTED, connected);
        return connected;
    }

    @Override
    public boolean isConnectWebSocketConnected() {
        if (mConnectWebSocketConnectStateMutableLiveData.getValue() != null) {
            return mConnectWebSocketConnectStateMutableLiveData.getValue();

        } else {
            return false;
        }
    }

    @Override
    public boolean isInternetConnected() {
        boolean connected =  mInternetConnectionStateMutableLiveData.getValue() != null
                && (mInternetConnectionStateMutableLiveData.getValue() == Enums.Net.InternetConnectionTypes.WIFI
                || mInternetConnectionStateMutableLiveData.getValue() == Enums.Net.InternetConnectionTypes.MOBILE
                || mInternetConnectionStateMutableLiveData.getValue() == ConnectivityManager.TYPE_ETHERNET
                || mInternetConnectionStateMutableLiveData.getValue() == ConnectivityManager.TYPE_BLUETOOTH);
        FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.INTERNET_CONNECTED, connected);
        updateInternetTypeOnCrashlytics();
        return connected;

    }

    @Override
    public String getInternetConnectionType()
    {
        String internetConnectionType = Enums.InternetConnectTypes.UNKNOWN;
        if(mInternetConnectionStateMutableLiveData.getValue() != null) {
            switch (mInternetConnectionStateMutableLiveData.getValue()) {
                case Enums.Net.InternetConnectionTypes.WIFI:
                    internetConnectionType = Enums.InternetConnectTypes.WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                case Enums.Net.InternetConnectionTypes.MOBILE:
                    internetConnectionType = Enums.InternetConnectTypes.MOBILE;
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    internetConnectionType = Enums.InternetConnectTypes.ETHERNET;
                    break;
                case ConnectivityManager.TYPE_BLUETOOTH:
                    internetConnectionType = Enums.InternetConnectTypes.BLUETOOTH;
                    break;
                case ConnectivityManager.TYPE_DUMMY:
                    internetConnectionType = Enums.InternetConnectTypes.DUMMY;
                    break;
                case ConnectivityManager.TYPE_MOBILE_DUN:
                    internetConnectionType = Enums.InternetConnectTypes.MOBILE_DUN;
                    break;
                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                    internetConnectionType = Enums.InternetConnectTypes.MOBILE_HIPRI;
                    break;
                case ConnectivityManager.TYPE_MOBILE_SUPL:
                    internetConnectionType = Enums.InternetConnectTypes.MOBILE_SUPL;
                    break;
                case ConnectivityManager.TYPE_VPN:
                    internetConnectionType = Enums.InternetConnectTypes.VPN;
                    break;
                case ConnectivityManager.TYPE_WIMAX:
                    internetConnectionType = Enums.InternetConnectTypes.WIMAX;
                    break;
                default:
                    internetConnectionType = "" + mInternetConnectionStateMutableLiveData.getValue();
            }
        }

        return internetConnectionType;

    }

    @Override
    public boolean hasIpv6Address() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            boolean isIpv6 = false;
            while (interfaces.hasMoreElements()) {
                networkInterface = interfaces.nextElement();
                for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    if (!address.getAddress().isLoopbackAddress() && address.getAddress() instanceof Inet6Address) {
                        isIpv6 = true;
                    }
                }
            }
            return isIpv6;
        } catch (SocketException exception) {
            return false;
        }
    }

    private void updateInternetTypeOnCrashlytics(){
        if(mInternetConnectionStateMutableLiveData.getValue() != null) {
            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.INTERNET_TYPE, getInternetConnectionType());
        }
    }

    @Override
    public boolean isUMSConnected() {
        if (mUMSConnectionStateMutableLiveData.getValue() != null) {
            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.UMS_CONNECTED, mUMSConnectionStateMutableLiveData.getValue());
            return mUMSConnectionStateMutableLiveData.getValue();
        } else {
            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.UMS_CONNECTED, false);
            return false;
        }
    }

    @Override
    public boolean isSipConnected() {
        if (mSipConnectionStateMutableLiveData.getValue() != null) {
            return mSipConnectionStateMutableLiveData.getValue();
        } else {
            return true;
        }
    }

    @Override
    public boolean isCallActive()
    {
        if(mSipManager != null)
        {
            return mSipManager.isCallActive();
        }
        else
            return false;
    }

    @Override
    public void updateSipRegistration(boolean isSipRegistered) {
        LogUtil.d("Is Registered :" + isSipRegistered);
        mSipConnectionStateMutableLiveData.postValue(isSipRegistered);
    }

    @Override
    public void setSipManager(PJSipManager sipManager) {
        mSipManager = sipManager;
    }

    @Override
    public void setWebSocketManager(WebSocketManager webSocketManager) {
        mWebSocketManager = webSocketManager;
    }
}