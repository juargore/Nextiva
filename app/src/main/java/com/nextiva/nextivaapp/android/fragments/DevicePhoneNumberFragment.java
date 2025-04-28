/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.APP_PREFERENCES;
import static com.nextiva.nextivaapp.android.util.ApplicationUtil.isEmulator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentDevicePhoneNumberBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.extensions.EditTextExtensionsKt;
import com.nextiva.nextivaapp.android.view.inputfilters.CallSettingsPhoneNumberInputFilter;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;
import com.nextiva.nextivaapp.android.viewmodels.DevicePhoneNumberViewModel;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DevicePhoneNumberFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final int RESOLVE_HINT = 1008;

    private TextInputLayout mPhoneNumberTextInputLayout;
    private AppCompatEditText mPhoneNumberEditText;
    private AppCompatButton mContinueButton;
    private SwitchCompat enableLogSwitch;

    @Inject
    protected AnalyticsManager mAnalyticsManager;
    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected PermissionManager mPermissionManager;
    @Inject
    protected LogManager mLogManager;


    private DevicePhoneNumberFragmentListener mFragmentListener;
    private GoogleApiClient mGoogleApiClient;
    private DevicePhoneNumberViewModel mViewModel;

    private boolean mIsNumberDialogShowing = false;

    public DevicePhoneNumberFragment() {
        // Required empty public constructor
    }

    public static DevicePhoneNumberFragment newInstance() {
        DevicePhoneNumberFragment fragment = new DevicePhoneNumberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }

        mViewModel = new ViewModelProvider(this).get(DevicePhoneNumberViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mIsNumberDialogShowing = false;

        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    mPhoneNumberEditText.setText(credential.getId());
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = bindViews(inflater, container);

        ArrayList<InputFilter> inputFiltersList = new ArrayList<>(Arrays.asList(mPhoneNumberEditText.getFilters()));
        inputFiltersList.add(new CallSettingsPhoneNumberInputFilter());

        mPhoneNumberEditText.setFilters(inputFiltersList.toArray(new InputFilter[0]));
        mPhoneNumberEditText.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());
        requestEnableLogSwitchCheckedChanged(enableLogSwitch.isChecked());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null && TextUtils.isEmpty(mViewModel.getPhoneNumber())) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Auth.CREDENTIALS_API)
                    .build();

            mGoogleApiClient.connect();

        } else {
            mPhoneNumberEditText.setText(mViewModel.getPhoneNumber());
        }

        mPhoneNumberTextInputLayout.setHintAnimationEnabled(false);
        mPhoneNumberTextInputLayout.setHintAnimationEnabled(true);

        if (!TextUtils.isEmpty(mPhoneNumberEditText.getText())) {
            mPhoneNumberEditText.setSelection(mPhoneNumberEditText.getText().toString().length());
        }
        EditTextExtensionsKt.makeClearableEditText(mPhoneNumberEditText);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            setFragmentListener((DevicePhoneNumberFragmentListener) context);
        } catch (ClassCastException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            throw (new UnsupportedOperationException(context.getClass().getSimpleName() + " must implement DevicePhoneNumberFragmentListener."));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        Activity activity = getActivity();

        if (activity != null && !isEmulator()) {
            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
            try {
                if (!mIsNumberDialogShowing) {

                    startIntentSenderForResult(intent.getIntentSender(), 1008, null, 0, 0, 0, null);
                    mIsNumberDialogShowing = true;
                }
            } catch (IntentSender.SendIntentException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentDevicePhoneNumberBinding binding = FragmentDevicePhoneNumberBinding.inflate(inflater, container, false);

        mPhoneNumberTextInputLayout = binding.devicePhoneNumberTextInputLayout;
        mPhoneNumberEditText = binding.devicePhoneNumberPhoneNumberEditText;
        mContinueButton = binding.devicePhoneNumberContinueButton;
        enableLogSwitch = binding.requestEnableLogSwitch;

        enableLogSwitch.setOnCheckedChangeListener((compoundButton, b) -> requestEnableLogSwitchCheckedChanged(b));
        mContinueButton.setOnClickListener(v -> continueButtonOnClick());
        mPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                afterPhoneNumberEditTextChanged(editable);
            }
        });

        return binding.getRoot();
    }

    protected void afterPhoneNumberEditTextChanged(Editable editable) {
        String phoneNumber = CallUtil.getStrippedPhoneNumber(editable.toString());
        mContinueButton.setEnabled(phoneNumber.length() >= getResources().getInteger(R.integer.general_phone_number_min_length) && phoneNumber.length() <= getResources().getInteger(R.integer.general_phone_number_max_length));
    }

    protected void continueButtonOnClick() {
        Activity activity = getActivity();

        if (activity != null) {
            if (!TextUtils.isEmpty(mPhoneNumberEditText.getText())) {
                mViewModel.setPhoneNumber(mPhoneNumberEditText.getText().toString());
            }

            if (mFragmentListener != null) {
                mFragmentListener.onPhoneNumberSaved();
            }

            mAnalyticsManager.logEvent(Enums.Analytics.ScreenName.ONBOARDING_THIS_PHONE_NUMBER, Enums.Analytics.EventName.CONTINUE_BUTTON_PRESSED);
        }
    }

    void requestEnableLogSwitchCheckedChanged(boolean checked) {
        Activity activity = getActivity();

        if (checked && activity != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            mPermissionManager.requestFileLoggingPermission(
                    activity,
                    APP_PREFERENCES,
                    () -> {
                        setLogging(checked);
                    },
                    () -> {
                        setLogging(false);
                        enableLogSwitch.setChecked(false);
                    });
        } else {
            setLogging(checked);
        }
    }


    void setLogging(boolean isLog) {
        mLogManager.setupLogger();
        mSettingsManager.setEnableLogging(isLog);
        mSettingsManager.setFileLogging(isLog);
        mSettingsManager.setSipLogging(isLog);
        mSettingsManager.setXMPPLogging(isLog);
    }

    @VisibleForTesting
    public void setFragmentListener(DevicePhoneNumberFragmentListener fragmentListener) {
        mFragmentListener = fragmentListener;
    }

    public interface DevicePhoneNumberFragmentListener {
        void onPhoneNumberSaved();
    }
}
