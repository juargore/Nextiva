package com.nextiva.nextivaapp.android.mocks.activities;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener;
import com.nextiva.nextivaapp.android.listeners.ToolbarListener;

public class FakeSetCallSettingsActivity extends FragmentActivity implements
        ToolbarListener,
        SetCallSettingsListener {

    // --------------------------------------------------------------------------------------------
    // SetCallSettingsListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onFormUpdated() {
    }

    @Override
    public void onCallSettingsRetrieved(@Nullable Intent data) {
    }

    @Override
    public void onCallSettingsSaved(@Nullable Intent data) {
    }

    @Override
    public void onCallSettingsDeleted(@Nullable Intent data) {
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // ToolbarListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void setToolbarTitle(int titleResId) {

    }

    @Override
    public void setToolbarElevation(float elevation) {

    }
    // --------------------------------------------------------------------------------------------
}
