/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_NO_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_YES_BUTTON_PRESSED;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsRemoteOfficeBinding;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsGetResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsPutResponseEvent;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.extensions.EditTextExtensionsKt;
import com.nextiva.nextivaapp.android.view.inputfilters.CallSettingsPhoneNumberInputFilter;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.functions.Consumer;

@AndroidEntryPoint
public class SetCallSettingsRemoteOfficeFragment extends SetCallSettingsBaseFragment<ServiceSettings> {

    protected TextInputLayout mPhoneNumberTextInputLayout;
    protected EditText mPhoneNumberEditText;
    protected CheckBox mEnabledCheckBox;

    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected UserRepository mUserRepository;
    @Inject
    protected AnalyticsManager mAnalyticsManager;

    private final Consumer<ServiceSettingsGetResponseEvent> mServiceSettingsGetResponseEventConsumer = new Consumer<ServiceSettingsGetResponseEvent>() {
        @Override
        public void accept(ServiceSettingsGetResponseEvent event) {
            if (getActivity() == null) {
                return;
            }

            mDialogManager.dismissProgressDialog();

            if (event.isSuccessful() && event.getServiceSettings() != null) {
                onCallSettingsRetrieved(event.getServiceSettings());

            } else {
                mDialogManager.showErrorDialog(getActivity(), getAnalyticScreenName());
            }
        }
    };
    private final Consumer<ServiceSettingsPutResponseEvent> mServiceSettingsPutResponseEventConsumer = new Consumer<ServiceSettingsPutResponseEvent>() {
        @Override
        public void accept(ServiceSettingsPutResponseEvent event) {
            if (getActivity() == null) {
                return;
            }

            mDialogManager.dismissProgressDialog();

            if (event.isSuccessful() && event.getServiceSettings() != null) {
                onCallSettingsSaved(event.getServiceSettings());

            } else {
                mDialogManager.showErrorDialog(getActivity(), getAnalyticScreenName());
            }
        }
    };

    public SetCallSettingsRemoteOfficeFragment() {
        // Required empty public constructor
    }

    public static SetCallSettingsRemoteOfficeFragment newInstance(@NonNull ServiceSettings serviceSettings) {
        Bundle args = new Bundle();
        args.putString(PARAMS_CALL_SETTINGS_TYPE, serviceSettings.getType());
        args.putSerializable(PARAMS_CALL_SETTINGS_VALUE, serviceSettings);

        SetCallSettingsRemoteOfficeFragment fragment = new SetCallSettingsRemoteOfficeFragment();
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

    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() == null) {
            return;
        }

        mDialogManager.showProgressDialog(getActivity(), getAnalyticScreenName(), R.string.progress_processing);
        mCompositeDisposable.add(
                mUserRepository.getSingleServiceSettings(
                        mCallSettings.getType(),
                        mCallSettings.getUri())
                        .subscribe(mServiceSettingsGetResponseEventConsumer));
    }

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentSetCallSettingsRemoteOfficeBinding binding = FragmentSetCallSettingsRemoteOfficeBinding.inflate(inflater, container, false);

        mPhoneNumberTextInputLayout = binding.setCallSettingsRemoteOfficePhoneNumberLayout;
        mPhoneNumberEditText = binding.setCallSettingsRemoteOfficePhoneNumberEditText;
        mEnabledCheckBox = binding.setCallSettingsRemoteOfficeEnabledCheckBox;

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
        mEnabledCheckBox.setEnabled(editable.length() > 0);

        if (editable.length() == 0) {
            mEnabledCheckBox.setOnCheckedChangeListener(null);
            mEnabledCheckBox.setChecked(false);
            mEnabledCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);
        }

        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }
    }

    // --------------------------------------------------------------------------------------------
    // SetCallSettingsBaseFragment Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_set_call_settings_remote_office;
    }

    @Override
    public int getToolbarTitleStringResId() {
        return R.string.set_call_settings_remote_office_toolbar;
    }

    @Override
    protected void setupScreenWidgets() {
        ArrayList<InputFilter> inputFiltersList = new ArrayList<>(Arrays.asList(mPhoneNumberEditText.getFilters()));
        inputFiltersList.add(new CallSettingsPhoneNumberInputFilter());

        mPhoneNumberEditText.setFilters(inputFiltersList.toArray(new InputFilter[0]));
        mPhoneNumberEditText.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());

        mEnabledCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);
    }

    @Override
    public void setupCallSettings() {
        if (mCallSettings != null) {
            mPhoneNumberTextInputLayout.setHintAnimationEnabled(false);
            mPhoneNumberEditText.setText(CallUtil.cleanForTextWatcher(mCallSettings.getRemoteOfficeNumber()));
            mPhoneNumberTextInputLayout.setHintAnimationEnabled(true);

            mEnabledCheckBox.setOnCheckedChangeListener(null);
            mEnabledCheckBox.setEnabled(!TextUtils.isEmpty(mPhoneNumberEditText.getText().toString()));
            mEnabledCheckBox.setChecked(mCallSettings.getActive());
            mEnabledCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);

            if (mSetCallSettingsListener != null) {
                mSetCallSettingsListener.onFormUpdated();
            }
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean changesMade() {
        String existingPhoneNumber = "";

        if (!TextUtils.isEmpty(mCallSettings.getRemoteOfficeNumber())) {
            existingPhoneNumber = mCallSettings.getRemoteOfficeNumber();
        }

        return mCallSettings != null &&
                (!TextUtils.equals(CallUtil.cleanForTextWatcher(mPhoneNumberEditText.getText().toString()), CallUtil.cleanForTextWatcher(existingPhoneNumber)) ||
                        mEnabledCheckBox.isChecked() != mCallSettings.getActive());
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public ServiceSettings getFormCallSettings() {
        ServiceSettings serviceSettings = new ServiceSettings(mCallSettings);
        serviceSettings.setRemoteOfficeNumber(mPhoneNumberEditText.getText().toString());
        serviceSettings.setActive(mEnabledCheckBox.isChecked());

        return serviceSettings;
    }

    @Override
    public void validateForm(final ValidateCallSettingCallBack<ServiceSettings> callBack) {
        if (getActivity() == null) {
            return;
        }

        final ServiceSettings proposedServiceSettings = getFormCallSettings();

        if (mSettingsManager.getDialingService() == Enums.Service.DialingServiceTypes.CALL_BACK) {
            if (!mSessionManager.getIsCallBackEnabled(proposedServiceSettings, mSessionManager.getNextivaAnywhereServiceSettings())) {
                mDialogManager.showDialog(
                        getActivity(),
                        R.string.dialing_service_conflict_call_back_title,
                        R.string.dialing_service_conflict_call_back_message,
                        R.string.general_yes,
                        (dialog, which) -> {
                            mSettingsManager.setDialingService(Enums.Service.DialingServiceTypes.VOIP);
                            callBack.onSaveCallSettings(mCallSettingsType, proposedServiceSettings);
                            mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_BACK_CONFLICT_DIALOG_YES_BUTTON_PRESSED);
                        },
                        R.string.general_no,
                        (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_BACK_CONFLICT_DIALOG_NO_BUTTON_PRESSED));

                mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_BACK_CONFLICT_DIALOG_SHOWN);
                return;
            }
        }

        callBack.onSaveCallSettings(mCallSettingsType, proposedServiceSettings);
    }

    @Override
    public void saveForm() {
        if (getActivity() == null) {
            return;
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.SAVE_BUTTON_PRESSED);

        validateForm((type, callSettings) -> {
            mDialogManager.showProgressDialog(getActivity(), getAnalyticScreenName(), R.string.progress_processing);
            mCompositeDisposable.add(
                    mUserRepository.putServiceSettings(callSettings)
                            .subscribe(mServiceSettingsPutResponseEventConsumer));
        });
    }

    @Override
    public int getFormTitleResId() {
        return getToolbarTitleStringResId();
    }

    @Override
    public int getHelpTextResId() {
        return R.string.set_call_settings_remote_office_help_text;
    }

    @NonNull
    @Override
    public String getAnalyticScreenName() {
        return Enums.Analytics.ScreenName.REMOTE_OFFICE;
    }
    // --------------------------------------------------------------------------------------------
}
