/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers;

import static com.nextiva.nextivaapp.android.constants.Enums.Logging.UserDatas.MOBILE_CONFIG_URL;

import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import com.burgstaller.okhttp.digest.Credentials;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MobileConfigRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.net.BroadsoftMobileApi;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.MobileConfigResponseEvent;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.util.BroadsoftUtil;

import javax.inject.Inject;

import io.reactivex.Single;
import retrofit2.HttpException;

/**
 * Created by adammacdonald on 2/24/18.
 */

public class BroadsoftMobileApiManager extends BaseApiManager implements
        MobileConfigRepository {

    private final SessionManager mSessionManager;
    private final ConfigManager mConfigManager;
    private final SchedulerProvider mSchedulerProvider;
    private final NetManager mNetManager;

    private final BroadsoftMobileApi mBroadsoftMobileApi;
    private final Credentials mDigestAuthCredentials;

    @Inject
    public BroadsoftMobileApiManager(Application application,
                                     SessionManager sessionManager,
                                     ConfigManager configManager,
                                     LogManager logManager,
                                     SchedulerProvider schedulerProvider,
                                     BroadsoftMobileApi broadsoftMobileApi,
                                     Credentials digestAuthCredentials,
                                     NetManager netManager) {

        super(application, logManager);

        mSessionManager = sessionManager;
        mConfigManager = configManager;
        mSchedulerProvider = schedulerProvider;
        mBroadsoftMobileApi = broadsoftMobileApi;
        mDigestAuthCredentials = digestAuthCredentials;
        mNetManager = netManager;
    }

    // --------------------------------------------------------------------------------------------
    // MobileConfigRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<MobileConfigResponseEvent> getMobileConfig() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (TextUtils.isEmpty(mSessionManager.getAccessDeviceUsername()) ||
                TextUtils.isEmpty(mSessionManager.getAccessDevicePassword()) ||
                TextUtils.isEmpty(mSessionManager.getAccessDeviceTypeUrl())) {

            String missingData = "";

            if(TextUtils.isEmpty(mSessionManager.getAccessDeviceUsername()))
                missingData = mApplication.getString(R.string.access_device_username);

            if(TextUtils.isEmpty(mSessionManager.getAccessDevicePassword())) {
                if (!missingData.isEmpty())
                    missingData += ", ";

                missingData += mApplication.getString(R.string.access_device_password);
            }

            if(TextUtils.isEmpty(mSessionManager.getAccessDeviceTypeUrl())) {
                if(!missingData.isEmpty())
                    missingData += ", ";

                missingData += mApplication.getString(R.string.access_device_type_url);
            }

            Exception exception = new Exception(mApplication.getString(R.string.login_error_session_data_missing, missingData));
            FirebaseCrashlytics.getInstance().recordException(exception);

            Toast.makeText(mApplication.getApplicationContext(), mApplication.getString(R.string.login_error_session_data_missing, missingData), Toast.LENGTH_LONG).show();
            return Single.just(new MobileConfigResponseEvent(false));
        }

        mDigestAuthCredentials.setUserName(mSessionManager.getAccessDeviceUsername());
        mDigestAuthCredentials.setPassword(mSessionManager.getAccessDevicePassword());

        String url = mSessionManager.getAccessDeviceTypeUrl() + "mobile-config.xml";

        FirebaseCrashlytics.getInstance().setCustomKey(MOBILE_CONFIG_URL, url);

        return mBroadsoftMobileApi.getMobilConfig(getAppVersionHeader(), url)
                .subscribeOn(mSchedulerProvider.io())
                .flatMap(response -> {
                    if (response.code() == Enums.ResponseCodes.ClientFailureResponses.UNAUTHORIZED) {
                        return mBroadsoftMobileApi.getMobilConfig(getAppVersionHeader(), url);
                    }

                    return Single.just(response);
                })
                .map(response -> {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            logServerSuccess(response);

                            BroadsoftUtil.processMobileConfig(mConfigManager, mNetManager, response.body());

                            return new MobileConfigResponseEvent(true);

                        } else {
                            FirebaseCrashlytics.getInstance().recordException(new Exception(mApplication.getString(R.string.failed_to_login_failed_to_retrieve_mobile_config)));
                            logServerParseFailure(response);
                            return new MobileConfigResponseEvent(false);
                        }

                    } else {
                        FirebaseCrashlytics.getInstance().recordException(new Exception(response.toString()));
                        throw new HttpException(response);
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);

                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    mDigestAuthCredentials.setUserName(null);
                    mDigestAuthCredentials.setPassword(null);

                    return new MobileConfigResponseEvent(false);
                })
                .observeOn(mSchedulerProvider.ui());
    }
    // --------------------------------------------------------------------------------------------
}
