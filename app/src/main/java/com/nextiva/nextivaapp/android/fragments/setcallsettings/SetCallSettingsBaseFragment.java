/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_UNCHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.BACK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ENABLED_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ENABLED_SWITCH_UNCHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.RING_SPLASH_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.RING_SPLASH_SWITCH_UNCHECKED;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.fragments.BaseFragment;
import com.nextiva.nextivaapp.android.interfaces.BackFragmentListener;
import com.nextiva.nextivaapp.android.interfaces.CallSettingsForm;
import com.nextiva.nextivaapp.android.interfaces.EntryForm;
import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener;
import com.nextiva.nextivaapp.android.listeners.ToolbarListener;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.util.MenuUtil;

import java.io.Serializable;

import javax.inject.Inject;

public abstract class SetCallSettingsBaseFragment<T extends Serializable> extends BaseFragment implements
        EntryForm,
        CallSettingsForm<T>,
        BackFragmentListener {

    static final String PARAMS_CALL_SETTINGS_TYPE = "PARAMS_CALL_SETTINGS_TYPE";
    static final String PARAMS_CALL_SETTINGS_VALUE = "PARAMS_CALL_SETTINGS_VALUE";

    @Inject
    protected AnalyticsManager mAnalyticsManager;

    private ToolbarListener mToolbarListener;
    protected SetCallSettingsListener mSetCallSettingsListener;

    String mCallSettingsType;
    T mCallSettings;

    final CompoundButton.OnCheckedChangeListener mEnabledOnCheckedChangeListener = (buttonView, isChecked) -> {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), isChecked ? ENABLED_SWITCH_CHECKED : ENABLED_SWITCH_UNCHECKED);
    };
    final CompoundButton.OnCheckedChangeListener mRingSplashOnCheckedChangeListener = (buttonView, isChecked) -> {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), isChecked ? RING_SPLASH_SWITCH_CHECKED : RING_SPLASH_SWITCH_UNCHECKED);
    };
    final CompoundButton.OnCheckedChangeListener mAnswerConfirmationOnCheckedChangeListener = (buttonView, isChecked) -> {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), isChecked ? ANSWER_CONFIRMATION_SWITCH_CHECKED : ANSWER_CONFIRMATION_SWITCH_UNCHECKED);
    };

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mCallSettingsType = getArguments().getString(PARAMS_CALL_SETTINGS_TYPE);
            mCallSettings = (T) getArguments().getSerializable(PARAMS_CALL_SETTINGS_VALUE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAnalyticsManager.logScreenView(getAnalyticScreenName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);

        setupScreenWidgets();

        setupCallSettings();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mToolbarListener = (ToolbarListener) context;
        } catch (ClassCastException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            throw (new UnsupportedOperationException(context.getClass().getSimpleName() + " must implement ToolbarListener."));
        }

        try {
            mSetCallSettingsListener = (SetCallSettingsListener) context;
        } catch (ClassCastException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            throw (new UnsupportedOperationException(context.getClass().getSimpleName() + " must implement SetCallSettingsListener."));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mToolbarListener != null) {
            mToolbarListener.setToolbarTitle(getToolbarTitleStringResId());
        }

        MenuUtil.setMenuContentDescriptions(menu);
    }

    @LayoutRes
    public abstract int getLayoutResId();

    @StringRes
    public abstract int getToolbarTitleStringResId();

    protected abstract void setupCallSettings();

    protected void setupScreenWidgets() {

    }

    void onCallSettingsRetrieved(T serviceSettings) {
        mCallSettings = serviceSettings;

        Intent data = new Intent();
        data.putExtra(com.nextiva.nextivaapp.android.constants.Constants.EXTRA_SERVICE_SETTINGS, mCallSettings);

        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onCallSettingsRetrieved(data);
        }

        setupCallSettings();
    }

    void onCallSettingsSaved(T serviceSettings) {
        Intent data = new Intent();
        data.putExtra(com.nextiva.nextivaapp.android.constants.Constants.EXTRA_SERVICE_SETTINGS, serviceSettings);

        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onCallSettingsSaved(data);
        }
    }

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean enableSaveButton() {
        return changesMade();
    }

    @Override
    public boolean enableDeleteButton() {
        return false;
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void validateForm(ValidateCallSettingCallBack<T> callBack) {
        callBack.onSaveCallSettings(mCallSettingsType, getFormCallSettings());
    }

    @Override
    public void deleteForm() {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onCallSettingsDeleted(null);
        }
    }

    @Override
    public int getFormTitleResId() {
        return 0;
    }

    @Override
    public int getHelpTextResId() {
        return 0;
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // BackFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        mAnalyticsManager.logEvent(getAnalyticScreenName(), BACK_BUTTON_PRESSED);
    }
    // --------------------------------------------------------------------------------------------

}
