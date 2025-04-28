/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.core.view.WindowInsetsCompat;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.google.android.material.appbar.AppBarLayout;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ActivityDevicePhoneNumberBinding;
import com.nextiva.nextivaapp.android.fragments.DevicePhoneNumberFragment;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;

import java.lang.annotation.Retention;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DevicePhoneNumberActivity extends BaseActivity implements
        DevicePhoneNumberFragment.DevicePhoneNumberFragmentListener {

    @Retention(SOURCE)
    @StringDef( {
            ONBOARDING,
            PREFERENCE
    })
    private @interface ScreenType {
    }

    private static final String ONBOARDING = "com.nextiva.nextivaapp.android.ONBOARDING";
    private static final String PREFERENCE = "com.nextiva.nextivaapp.android.PREFERENCE";

    private static final String PARAMS_SCREEN_TYPE = "PARAMS_SCREEN_TYPE";

    protected AppBarLayout mAppBarLayout;
    protected Toolbar mToolbar;
    protected ImageView mLogoImageView;

    @Inject
    protected AnalyticsManager mAnalyticsManager;
    @Inject
    protected IntentManager mIntentManager;

    @ScreenType
    private String mScreenType;

    public static Intent newOnboardingIntent(Context context) {
        Intent intent = new Intent(context, DevicePhoneNumberActivity.class);
        intent.putExtra(PARAMS_SCREEN_TYPE, ONBOARDING);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent newPreferenceIntent(Context context) {
        Intent intent = new Intent(context, DevicePhoneNumberActivity.class);
        intent.putExtra(PARAMS_SCREEN_TYPE, PREFERENCE);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindViews());

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            if (extras.containsKey(PARAMS_SCREEN_TYPE)) {
                mScreenType = extras.getString(PARAMS_SCREEN_TYPE);
            }
        }

        if (TextUtils.isEmpty(mScreenType)) {
            finish();
        }

        if (TextUtils.equals(ONBOARDING, mScreenType)) {
            mAppBarLayout.setVisibility(View.GONE);
            mLogoImageView.setVisibility(View.VISIBLE);

        } else if (TextUtils.equals(PREFERENCE, mScreenType)) {
            mLogoImageView.setVisibility(View.GONE);
            mAppBarLayout.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MaterialMenuDrawable materialMenuDrawable = new MaterialMenuDrawable(DevicePhoneNumberActivity.this,
                                                                             ContextCompat.getColor(DevicePhoneNumberActivity.this, R.color.white),
                                                                             MaterialMenuDrawable.Stroke.REGULAR);
        materialMenuDrawable.setIconState(MaterialMenuDrawable.IconState.ARROW);

        mToolbar.setNavigationIcon(materialMenuDrawable);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mToolbar.setNavigationContentDescription(R.string.back_button_accessibility_id);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TextUtils.equals(ONBOARDING, mScreenType)) {
            mAnalyticsManager.logScreenView(Enums.Analytics.ScreenName.ONBOARDING_THIS_PHONE_NUMBER);
        } else if (TextUtils.equals(PREFERENCE, mScreenType)) {
            //TODO Should this track a different analytic event?
            mAnalyticsManager.logScreenView(Enums.Analytics.ScreenName.ONBOARDING_THIS_PHONE_NUMBER);
        }
    }

    private View bindViews() {
        ActivityDevicePhoneNumberBinding binding = ActivityDevicePhoneNumberBinding.inflate(getLayoutInflater());

        mAppBarLayout = binding.devicePhoneNumberAppBarLayout;
        mToolbar = binding.devicePhoneNumberToolbar;
        mLogoImageView = binding.devicePhoneNumberImageView;

        overrideEdgeToEdge(binding.getRoot());
        return binding.getRoot();
    }

    // --------------------------------------------------------------------------------------------
    // DevicePhoneNumberFragment.DevicePhoneNumberFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onPhoneNumberSaved() {
        if (TextUtils.equals(ONBOARDING, mScreenType)) {
            startActivity(mIntentManager.getInitialIntent(DevicePhoneNumberActivity.this));

        } else if (TextUtils.equals(PREFERENCE, mScreenType)) {
            finish();
        }
    }
    // --------------------------------------------------------------------------------------------
}
