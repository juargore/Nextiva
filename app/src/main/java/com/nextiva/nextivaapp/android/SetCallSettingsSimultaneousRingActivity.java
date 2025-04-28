/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.constants.FragmentTags;
import com.nextiva.nextivaapp.android.databinding.ActivitySetCallSettingsSimultaneousRingBinding;
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsSimultaneousRingFragment;
import com.nextiva.nextivaapp.android.models.ServiceSettings;

public class SetCallSettingsSimultaneousRingActivity extends BaseActivity {

    private static final String PARAMS_SERVICE_SETTINGS = "PARAMS_CALL_SETTING";

    protected Toolbar mToolbar;

    private MaterialMenuDrawable mMaterialMenuDrawable;

    private ServiceSettings mServiceSettings;

//    public static Intent newIntent(@NonNull Context context, ServiceSettings serviceSettings) {
//        Intent intent = new Intent(context, SetCallSettingsSimultaneousRingActivity.class);
//        intent.putExtra(PARAMS_SERVICE_SETTINGS, serviceSettings);
//        return intent;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindViews());

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            if (extras.containsKey(PARAMS_SERVICE_SETTINGS)) {
                mServiceSettings = (ServiceSettings) extras.getSerializable(PARAMS_SERVICE_SETTINGS);
            }
        }

        if (mServiceSettings == null || !TextUtils.equals(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL, mServiceSettings.getType())) {
            finish();
        }

        setupToolbar();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.set_call_settings_simultaneous_ring_fragment_container_layout,
                            SetCallSettingsSimultaneousRingFragment.Companion.newInstance(mServiceSettings),
                            FragmentTags.SIMULTANEOUS_RING_LOCATIONS_LIST);
        transaction.commit();
    }

    private View bindViews() {
        ActivitySetCallSettingsSimultaneousRingBinding binding = ActivitySetCallSettingsSimultaneousRingBinding.inflate(getLayoutInflater());

        mToolbar = binding.setCallSettingsSimultaneousRingToolbar;

        overrideEdgeToEdge(binding.getRoot());

        return binding.getRoot();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        mMaterialMenuDrawable = new MaterialMenuDrawable(SetCallSettingsSimultaneousRingActivity.this,
                                                         ContextCompat.getColor(SetCallSettingsSimultaneousRingActivity.this, R.color.white),
                                                         MaterialMenuDrawable.Stroke.REGULAR);
        mMaterialMenuDrawable.setIconState(MaterialMenuDrawable.IconState.ARROW);
        mToolbar.setNavigationIcon(mMaterialMenuDrawable);
        mToolbar.setNavigationContentDescription(R.string.back_button_accessibility_id);
        mToolbar.setNavigationOnClickListener(v -> {
            if (mMaterialMenuDrawable.getIconState() == MaterialMenuDrawable.IconState.ARROW) {
                onBackPressed();
            }
        });
    }
}