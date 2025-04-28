/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_NO_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_YES_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SAVE_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.THIS_PHONE_NUMBER;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsThisPhoneNumberBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.extensions.EditTextExtensionsKt;
import com.nextiva.nextivaapp.android.view.inputfilters.CallSettingsPhoneNumberInputFilter;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetCallSettingsThisPhoneNumberFragment extends SetCallSettingsBaseFragment<String> {

    protected TextInputLayout mPhoneNumberTextInputLayout;
    protected EditText mPhoneNumberEditText;

    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;

    public SetCallSettingsThisPhoneNumberFragment() {
        // Required empty public constructor
    }

    public static SetCallSettingsThisPhoneNumberFragment newInstance(String thisPhoneNumber) {
        Bundle args = new Bundle();

        SetCallSettingsThisPhoneNumberFragment fragment = new SetCallSettingsThisPhoneNumberFragment();
        args.putString(PARAMS_CALL_SETTINGS_TYPE, SharedPreferencesManager.THIS_PHONE_NUMBER);
        args.putSerializable(PARAMS_CALL_SETTINGS_VALUE, thisPhoneNumber);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = bindViews(inflater, container);
        super.onCreateView(inflater, container, savedInstanceState);

        EditTextExtensionsKt.makeClearableEditText(mPhoneNumberEditText);

        return view;
    }

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentSetCallSettingsThisPhoneNumberBinding binding = FragmentSetCallSettingsThisPhoneNumberBinding.inflate(inflater, container, false);

        mPhoneNumberTextInputLayout = binding.setCallSettingsThisPhoneNumberPhoneNumberLayout;
        mPhoneNumberEditText = binding.setCallSettingsThisPhoneNumberPhoneNumberEditText;

        mPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                afterPhoneNumberTextChanged(editable);
            }
        });

        return binding.getRoot();
    }

    protected void afterPhoneNumberTextChanged(Editable editable) {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }
    }

    // --------------------------------------------------------------------------------------------
    // SetCallSettingsBaseFragment Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_set_call_settings_this_phone_number;
    }

    @Override
    public int getToolbarTitleStringResId() {
        return R.string.set_call_settings_this_phone_number_toolbar;
    }

    @Override
    protected void setupScreenWidgets() {
        ArrayList<InputFilter> inputFiltersList = new ArrayList<>(Arrays.asList(mPhoneNumberEditText.getFilters()));
        inputFiltersList.add(new CallSettingsPhoneNumberInputFilter());

        mPhoneNumberEditText.setFilters(inputFiltersList.toArray(new InputFilter[0]));
        mPhoneNumberEditText.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());
    }

    @Override
    public void setupCallSettings() {
        mPhoneNumberTextInputLayout.setHintAnimationEnabled(false);
        mPhoneNumberEditText.setText(CallUtil.cleanForTextWatcher(mCallSettings));
        mPhoneNumberTextInputLayout.setHintAnimationEnabled(true);
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean changesMade() {
        String existingPhoneNumber = "";

        if (!TextUtils.isEmpty(mCallSettings)) {
            existingPhoneNumber = mCallSettings;
        }

        String phoneNumber = CallUtil.getStrippedPhoneNumber(mPhoneNumberEditText.getText().toString());
        return phoneNumber.length() >= getResources().getInteger(R.integer.general_phone_number_min_length) && phoneNumber.length() <= getResources().getInteger(R.integer.general_phone_number_max_length) && !TextUtils.equals(phoneNumber, CallUtil.getStrippedPhoneNumber(existingPhoneNumber));
    }

    @Override
    public boolean enableSaveButton() {
        return super.enableSaveButton() && !TextUtils.isEmpty(mPhoneNumberEditText.getText().toString());
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public String getFormCallSettings() {
        return mPhoneNumberEditText.getText().toString();
    }

    @Override
    public void validateForm(final ValidateCallSettingCallBack<String> callBack) {
        if (getActivity() == null) {
            return;
        }

        if (mSettingsManager.getDialingService() == Enums.Service.DialingServiceTypes.CALL_THROUGH) {
            if (!mSessionManager.getIsCallThroughEnabled(mSessionManager.getNextivaAnywhereServiceSettings(), mPhoneNumberEditText.getText().toString())) {
                mDialogManager.showDialog(
                        getActivity(),
                        R.string.dialing_service_conflict_call_through_title,
                        R.string.dialing_service_conflict_call_through_message,
                        R.string.general_yes,
                        (dialog, which) -> {
                            mSettingsManager.setDialingService(Enums.Service.DialingServiceTypes.VOIP);
                            callBack.onSaveCallSettings(mCallSettingsType, mPhoneNumberEditText.getText().toString());

                            mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_THROUGH_CONFLICT_DIALOG_YES_BUTTON_PRESSED);
                        },
                        R.string.general_no,
                        (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_THROUGH_CONFLICT_DIALOG_NO_BUTTON_PRESSED));

                mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_THROUGH_CONFLICT_DIALOG_SHOWN);

                return;
            }
        }

        callBack.onSaveCallSettings(mCallSettingsType, mPhoneNumberEditText.getText().toString());
    }

    @Override
    public void saveForm() {
        mAnalyticsManager.logEvent(getAnalyticScreenName(), SAVE_BUTTON_PRESSED);

        validateForm((type, callSettings) -> {
            mSettingsManager.setPhoneNumber(callSettings);

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
        return THIS_PHONE_NUMBER;
    }
    // --------------------------------------------------------------------------------------------
}
