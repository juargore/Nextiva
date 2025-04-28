/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.datadog.android.Datadog;
import com.datadog.android.privacy.TrackingConsent;
import com.datadog.android.rum.Rum;
import com.datadog.android.rum.RumConfiguration;
import com.datadog.android.rum.tracking.ActivityViewTrackingStrategy;
import com.datadog.android.rum.tracking.ComponentPredicate;
import com.datadog.android.rum.tracking.FragmentViewTrackingStrategy;
import com.datadog.android.rum.tracking.MixedViewTrackingStrategy;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.plugins.RxJavaPlugins;
import sdk.pendo.io.Pendo;

/**
 * Created by adammacdonald on 2/2/18.
 */
@HiltAndroidApp
public class NextivaApplication extends MultiDexApplication implements LifecycleObserver, Configuration.Provider {

    @Inject
    public HiltWorkerFactory workerFactory;
    private boolean isAppInBackground;
    private BaseActivity mCurrentActivity;
    private MockWebServer mMockWebServer;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeWorkManager();
        setupCrashlytics();
        FirebaseApp.initializeApp(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        // https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (mCurrentActivity != null) {
                mCurrentActivity.logError(throwable);
            }
            FirebaseCrashlytics.getInstance().recordException(throwable);
        });

        setupDatadog();
        setupAndroidThreeTen();
        setupPendo();
    }

    protected void setupCrashlytics() {
        FirebaseApp.initializeApp(this);
        //Fabric.with(this, new Crashlytics());
    }

    protected void setupAndroidThreeTen() {
        AndroidThreeTen.init(this);
    }

    protected void setupDatadog() {

        String clientToken = "pubf480cde17408b04cd61a7cebd8d04368";
        String environment = getString(R.string.app_environment);
        String applicationId = "89dfc29c-fb5f-4fe5-8330-a865cdf0a081";
        String service = BuildConfig.APPLICATION_ID;

        com.datadog.android.core.configuration.Configuration configuration = new com.datadog.android.core.configuration.Configuration.Builder(clientToken, environment, BuildConfig.FLAVOR, service)
                .build();

        Datadog.initialize(this, configuration, TrackingConsent.GRANTED);

        ActivityViewTrackingStrategy activityTrackingStrategy = new ActivityViewTrackingStrategy(true, new ComponentPredicate<Activity>() {
            @Override
            public boolean accept(Activity activity) {
                return true;
            }

            @Nullable
            @Override
            public String getViewName(Activity activity) {
                return activity.getClass().getSimpleName();
            }
        });

        FragmentViewTrackingStrategy fragmentTrackingStrategy = new FragmentViewTrackingStrategy(true, new ComponentPredicate<Fragment>() {
            @Override
            public boolean accept(Fragment fragment) {
                return true;
            }

            @Nullable
            @Override
            public String getViewName(Fragment fragment) {
                return fragment.getClass().getSimpleName();
            }
        });

        MixedViewTrackingStrategy mixedTrackingStrategy = new MixedViewTrackingStrategy(activityTrackingStrategy, fragmentTrackingStrategy);

        if (Datadog.isInitialized()) {
            RumConfiguration rumConfig = new RumConfiguration.Builder(applicationId)
                    .trackUserInteractions()
                    .trackBackgroundEvents(true)
                    .trackFrustrations(true)
                    .trackLongTasks(1000L)
                    .useViewTrackingStrategy(mixedTrackingStrategy).build();

            Rum.enable(rumConfig);
        }
    }

    protected void setupPendo() {
        Pendo.setup(
                this,
                BuildConfig.PENDO_API_KEY,
                null,
                null);
    }

    public boolean isAppInBackground() {
        return isAppInBackground;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        isAppInBackground = true;

        if (mCurrentActivity != null) {
            mCurrentActivity.stopPolling();
            mCurrentActivity.stopConnectWebSocketConnection();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        isAppInBackground = false;

        if (mCurrentActivity != null) {
            mCurrentActivity.enableCallLogDataDogEvent();
            mCurrentActivity.startConnectWebSocketConnection();
            mCurrentActivity.sendInitialPresencePing();
            mCurrentActivity.getAllSmsMessages();
            mCurrentActivity.updateProducts();
            mCurrentActivity.startPolling();
            mCurrentActivity.updateFeatureFlags();
        }
    }

    public void setCurrentActivity(BaseActivity baseActivity) {
        mCurrentActivity = baseActivity;
    }

    public BaseActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    private void initializeWorkManager() {
        if (!WorkManager.isInitialized()) {
            LogUtil.d("WorkManager", "Initializing WorkManager");
            Configuration config = new Configuration.Builder()
                    .setWorkerFactory(workerFactory)
                    .build();
            WorkManager.initialize(this, config);
        } else {
            LogUtil.d("WorkManager", "Attempted to initialize WorkManager more than once");
        }
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .setWorkerFactory(workerFactory)
                .build();
    }

    public MockWebServer getMockWebServer() {
        return mMockWebServer;
    }
}