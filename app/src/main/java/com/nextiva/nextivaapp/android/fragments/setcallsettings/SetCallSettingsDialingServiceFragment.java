/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsDialingServiceBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetCallSettingsDialingServiceFragment extends SetCallSettingsBaseFragment<Integer> {

    protected RadioGroup mRadioGroup;
    protected RadioButton mCallBackRadioButton;
    protected RadioButton mCallThroughRadioButton;
    protected RadioButton mThisPhoneRadioButton;

    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected ConfigManager mConfigManager;
    @Inject
    protected IntentManager mIntentManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;

    @IdRes
    private int mSelectedRadioButtonId;

    private final RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = (group, checkedId) -> {
        if (checkedId == R.id.set_call_settings_dialing_service_voip_radio_button) {
            mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.VOIP_RADIO_BUTTON_CHECKED);
        } else if (checkedId == R.id.set_call_settings_dialing_service_call_back_radio_button) {
            mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_BACK_RADIO_BUTTON_CHECKED);
        } else if (checkedId == R.id.set_call_settings_dialing_service_call_through_radio_button) {
            mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_THROUGH_RADIO_BUTTON_CHECKED);
        } else if (checkedId == R.id.set_call_settings_dialing_service_this_phone_radio_button) {
            mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.THIS_PHONE_RADIO_BUTTON_CHECKED);
        } else if (checkedId == R.id.set_call_settings_dialing_service_always_ask_radio_button) {
            mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.ALWAYS_ASK_RADIO_BUTTON_CHECKED);
        }
    };

    public SetCallSettingsDialingServiceFragment() {
        // Required empty public constructor
    }

    public static SetCallSettingsDialingServiceFragment newInstance(@Enums.Service.DialingServiceTypes.DialingServiceType int dialingServiceType) {
        Bundle args = new Bundle();

        SetCallSettingsDialingServiceFragment fragment = new SetCallSettingsDialingServiceFragment();
        args.putSerializable(PARAMS_CALL_SETTINGS_TYPE, SharedPreferencesManager.DIALING_SERVICE);
        args.putInt(PARAMS_CALL_SETTINGS_VALUE, dialingServiceType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }

    }

    // Remove the onAttach method
    // @Override
    // public void onAttach(@NonNull Context context) {
    //    super.onAttach(context);
    //    AndroidSupportInjection.inject(this);
    // }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = bindViews(inflater, container);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentSetCallSettingsDialingServiceBinding binding = FragmentSetCallSettingsDialingServiceBinding.inflate(inflater, container, false);

        mRadioGroup = binding.setCallSettingsDialingServiceRadioGroup;
        mCallBackRadioButton = binding.setCallSettingsDialingServiceCallBackRadioButton;
        mCallThroughRadioButton = binding.setCallSettingsDialingServiceCallThroughRadioButton;
        mThisPhoneRadioButton = binding.setCallSettingsDialingServiceThisPhoneRadioButton;

        binding.setCallSettingsDialingServiceVoipRadioButton.setOnCheckedChangeListener(this::onRadioGroupCheckedChanged);
        mCallBackRadioButton.setOnCheckedChangeListener(this::onRadioGroupCheckedChanged);
        mCallThroughRadioButton.setOnCheckedChangeListener(this::onRadioGroupCheckedChanged);
        mThisPhoneRadioButton.setOnCheckedChangeListener(this::onRadioGroupCheckedChanged);
        binding.setCallSettingsDialingServiceAlwaysAskRadioButton.setOnCheckedChangeListener(this::onRadioGroupCheckedChanged);

        return binding.getRoot();
    }

    protected void onRadioGroupCheckedChanged(CompoundButton button, boolean checked) {
        if (checked) {
            mSelectedRadioButtonId = button.getId();

            if (mSetCallSettingsListener != null) {
                mSetCallSettingsListener.onFormUpdated();
            }
        }
    }

    @Enums.Service.DialingServiceTypes.DialingServiceType
    private int getDialingServiceTypeBySelectedRadioButton() {
        if (mSelectedRadioButtonId == R.id.set_call_settings_dialing_service_voip_radio_button) {
            return Enums.Service.DialingServiceTypes.VOIP;
        } else if (mSelectedRadioButtonId == R.id.set_call_settings_dialing_service_call_back_radio_button) {
            return Enums.Service.DialingServiceTypes.CALL_BACK;
        } else if (mSelectedRadioButtonId == R.id.set_call_settings_dialing_service_call_through_radio_button) {
            return Enums.Service.DialingServiceTypes.CALL_THROUGH;
        } else if (mSelectedRadioButtonId == R.id.set_call_settings_dialing_service_this_phone_radio_button) {
            return Enums.Service.DialingServiceTypes.THIS_PHONE;
        } else if (mSelectedRadioButtonId == R.id.set_call_settings_dialing_service_always_ask_radio_button) {
            return Enums.Service.DialingServiceTypes.ALWAYS_ASK;
        }

        return Enums.Service.DialingServiceTypes.NONE;
    }

    @IdRes
    private int getRadioButtonIdByCachedDialingServiceType() {
        switch (mSettingsManager.getDialingService()) {
            case Enums.Service.DialingServiceTypes.VOIP: {
                return R.id.set_call_settings_dialing_service_voip_radio_button;
            }
            case Enums.Service.DialingServiceTypes.CALL_BACK: {
                return R.id.set_call_settings_dialing_service_call_back_radio_button;
            }
            case Enums.Service.DialingServiceTypes.CALL_THROUGH: {
                return R.id.set_call_settings_dialing_service_call_through_radio_button;
            }
            case Enums.Service.DialingServiceTypes.THIS_PHONE: {
                return R.id.set_call_settings_dialing_service_this_phone_radio_button;
            }
            case Enums.Service.DialingServiceTypes.ALWAYS_ASK:
            case Enums.Service.DialingServiceTypes.NONE: {
                return R.id.set_call_settings_dialing_service_always_ask_radio_button;
            }
        }

        return R.id.set_call_settings_dialing_service_always_ask_radio_button;
    }

    // --------------------------------------------------------------------------------------------
    // SetCallSettingsBaseFragment Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_set_call_settings_dialing_service;
    }

    @Override
    public int getToolbarTitleStringResId() {
        return R.string.set_call_settings_dialing_service_toolbar;
    }

    protected void setupScreenWidgets() {
        if (getActivity() == null || mConfigManager == null || mSessionManager == null) {
            return;
        }

        boolean isRemoteOfficeLicensingEnabled = mConfigManager.getRemoteOfficeEnabled() && mSessionManager.getRemoteOfficeServiceSettings() != null;
        boolean isNextivaAnywhereLicensingEnabled = mConfigManager.getNextivaAnywhereEnabled() && mSessionManager.getNextivaAnywhereServiceSettings() != null;

        if (isRemoteOfficeLicensingEnabled || isNextivaAnywhereLicensingEnabled) {
            mCallBackRadioButton.setVisibility(View.VISIBLE);
        } else {
            mCallBackRadioButton.setVisibility(View.GONE);
        }

        if (isNextivaAnywhereLicensingEnabled) {
            mCallThroughRadioButton.setVisibility(View.VISIBLE);
        } else {
            mCallThroughRadioButton.setVisibility(View.GONE);
        }

        mRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCallBackRadioButton.setEnabled(mSessionManager.getIsCallBackEnabled(mSessionManager.getRemoteOfficeServiceSettings(), mSessionManager.getNextivaAnywhereServiceSettings()));
        mCallThroughRadioButton.setEnabled(mSessionManager.getIsCallThroughEnabled(mSessionManager.getNextivaAnywhereServiceSettings(), mSettingsManager.getPhoneNumber()));
        mThisPhoneRadioButton.setEnabled(mIntentManager.isAbleToMakePhoneCall(getActivity()));
    }

    @Override
    public void setupCallSettings() {
        mRadioGroup.setOnCheckedChangeListener(null);
        mRadioGroup.check(getRadioButtonIdByCachedDialingServiceType());
        mRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean changesMade() {
        return mSettingsManager.getDialingService() != getDialingServiceTypeBySelectedRadioButton();
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Integer getFormCallSettings() {
        return getDialingServiceTypeBySelectedRadioButton();
    }

    @Override
    public void saveForm() {
        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.SAVE_BUTTON_PRESSED);

        validateForm((type, callSettings) -> {
            mSettingsManager.setDialingService(callSettings);

            Intent data = new Intent();
            data.putExtra(Constants.EXTRA_CALL_SETTINGS_KEY, type);
            data.putExtra(Constants.EXTRA_CALL_SETTINGS_VALUE, callSettings);

            if (mSetCallSettingsListener != null) {
                mSetCallSettingsListener.onCallSettingsSaved(data);
            }
        });
    }

    @NonNull
    @Override
    public String getAnalyticScreenName() {
        return Enums.Analytics.ScreenName.DIALING_SERVICE_SCREEN;
    }
    // --------------------------------------------------------------------------------------------
}