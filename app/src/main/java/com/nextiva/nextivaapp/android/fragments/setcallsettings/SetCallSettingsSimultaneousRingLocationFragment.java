/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DELETE_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_CANCEL_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_DELETE_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SAVE_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.ADD_SIMULTANEOUS_RING_LOCATION;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.EDIT_SIMULTANEOUS_RING_LOCATION;

import android.content.Intent;
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
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsSimultaneousRingLocationBinding;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsPutResponseEvent;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;
import com.nextiva.nextivaapp.android.util.extensions.EditTextExtensionsKt;
import com.nextiva.nextivaapp.android.view.inputfilters.CallSettingsPhoneNumberInputFilter;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.functions.Consumer;

@AndroidEntryPoint
public class SetCallSettingsSimultaneousRingLocationFragment extends SetCallSettingsBaseFragment<SimultaneousRingLocation> {

    private static final String PARAMS_SIMULTANEOUS_RING_SERVICE_SETTINGS = "PARAMS_SIMULTANEOUS_RING_SERVICE_SETTINGS";

    protected TextInputLayout mPhoneNumberTextInputLayout;
    protected EditText mPhoneNumberEditText;
    protected CheckBox mAnswerConfirmationRequiredCheckBox;

    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected UserRepository mUserRepository;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;

    private ServiceSettings mSimultaneousRingServiceSettings;

    private final Consumer<ServiceSettingsPutResponseEvent> mServiceSettingsPutResponseEventConsumer = new Consumer<ServiceSettingsPutResponseEvent>() {
        @Override
        public void accept(ServiceSettingsPutResponseEvent event) {
            if (getActivity() == null) {
                return;
            }

            mDialogManager.dismissProgressDialog();

            if (event.isSuccessful() && event.getServiceSettings() != null) {
                Intent data = new Intent();
                data.putExtra(Constants.EXTRA_SERVICE_SETTINGS, event.getServiceSettings());

                if (mSetCallSettingsListener != null) {
                    mSetCallSettingsListener.onCallSettingsSaved(data);
                }

            } else {
                if (event.getErrorInfo() != null) {
                    mDialogManager.showErrorDialog(getActivity(),
                                                   getAnalyticScreenName(),
                                                   event.getErrorInfo().getEventName(),
                                                   event.getErrorInfo().getDialogTitle(getActivity()),
                                                   event.getErrorInfo().getDialogContent(getActivity()),
                                                   ((dialog, which) -> {
                                                   }));

                } else {
                    mDialogManager.showErrorDialog(getActivity(), getAnalyticScreenName());
                }
            }
        }
    };

    public SetCallSettingsSimultaneousRingLocationFragment() {
        // Required empty public constructor
    }

    public static SetCallSettingsSimultaneousRingLocationFragment newInstance(ServiceSettings simultaneousRingServiceSettings, SimultaneousRingLocation callSettings) {
        Bundle args = new Bundle();

        SetCallSettingsSimultaneousRingLocationFragment fragment = new SetCallSettingsSimultaneousRingLocationFragment();
        args.putSerializable(PARAMS_SIMULTANEOUS_RING_SERVICE_SETTINGS, simultaneousRingServiceSettings);
        args.putString(PARAMS_CALL_SETTINGS_TYPE, Enums.CallSettings.FORM_TYPE_SIMULTANEOUS_RING_LOCATION);
        args.putSerializable(PARAMS_CALL_SETTINGS_VALUE, callSettings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }

        if (getArguments() != null) {
            mSimultaneousRingServiceSettings = (ServiceSettings) getArguments().getSerializable(PARAMS_SIMULTANEOUS_RING_SERVICE_SETTINGS);
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
        FragmentSetCallSettingsSimultaneousRingLocationBinding binding = FragmentSetCallSettingsSimultaneousRingLocationBinding.inflate(inflater, container, false);

        mPhoneNumberTextInputLayout = binding.setCallSettingsSimultaneousRingLocationPhoneNumberLayout;
        mPhoneNumberEditText = binding.setCallSettingsSimultaneousRingLocationPhoneNumberEditText;
        mAnswerConfirmationRequiredCheckBox = binding.setCallSettingsSimultaneousRingLocationAnswerConfirmationRequiredCheckBox;

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
        mAnswerConfirmationRequiredCheckBox.setEnabled(editable.length() > 0);

        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }
    }

    private void sendUpdatedServiceSetting(@NonNull ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList) {
        if (getActivity() == null) {
            return;
        }

        ServiceSettings serviceSettings = new ServiceSettings(mSimultaneousRingServiceSettings.getType(), mSimultaneousRingServiceSettings.getUri());
        serviceSettings.setActive(!simultaneousRingLocationsList.isEmpty() && mSimultaneousRingServiceSettings.getActive());
        serviceSettings.setDontRingWhileOnCall(mSimultaneousRingServiceSettings.getDontRingWhileOnCall());
        serviceSettings.setSimultaneousRingLocationsList(simultaneousRingLocationsList);

        mDialogManager.showProgressDialog(getActivity(), getAnalyticScreenName(), R.string.progress_processing);
        mCompositeDisposable.add(
                mUserRepository.putServiceSettings(
                        serviceSettings)
                        .subscribe(mServiceSettingsPutResponseEventConsumer));
    }

    // --------------------------------------------------------------------------------------------
    // SetCallSettingsBaseFragment Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_set_call_settings_simultaneous_ring_location;
    }

    @Override
    public int getToolbarTitleStringResId() {
        return mCallSettings == null ?
                R.string.set_call_settings_simultaneous_ring_add_location_toolbar :
                R.string.set_call_settings_simultaneous_ring_edit_location_toolbar;
    }

    @Override
    protected void setupScreenWidgets() {
        ArrayList<InputFilter> inputFiltersList = new ArrayList<>(Arrays.asList(mPhoneNumberEditText.getFilters()));
        inputFiltersList.add(new CallSettingsPhoneNumberInputFilter());

        mPhoneNumberEditText.setFilters(inputFiltersList.toArray(new InputFilter[0]));
        mPhoneNumberEditText.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());

        mAnswerConfirmationRequiredCheckBox.setOnCheckedChangeListener(mAnswerConfirmationOnCheckedChangeListener);
    }

    @Override
    public void setupCallSettings() {
        if (mCallSettings != null) {
            mPhoneNumberTextInputLayout.setHintAnimationEnabled(false);
            mPhoneNumberEditText.setText(CallUtil.cleanForTextWatcher(mCallSettings.getPhoneNumber()));
            mPhoneNumberTextInputLayout.setHintAnimationEnabled(true);

            mAnswerConfirmationRequiredCheckBox.setOnCheckedChangeListener(null);
            mAnswerConfirmationRequiredCheckBox.setChecked(mCallSettings.getAnswerConfirmationRequired());
            mAnswerConfirmationRequiredCheckBox.setOnCheckedChangeListener(mAnswerConfirmationOnCheckedChangeListener);
        }

        mAnswerConfirmationRequiredCheckBox.setEnabled(!TextUtils.isEmpty(mPhoneNumberEditText.getText().toString()));
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean changesMade() {
        if (mCallSettings != null) {
            String existingPhoneNumber = "";

            if (!TextUtils.isEmpty(mCallSettings.getPhoneNumber())) {
                existingPhoneNumber = mCallSettings.getPhoneNumber();
            }

            return StringUtil.changesMade(CallUtil.cleanForTextWatcher(mPhoneNumberEditText.getText().toString()), CallUtil.cleanForTextWatcher(existingPhoneNumber)) ||
                    mAnswerConfirmationRequiredCheckBox.isChecked() != mCallSettings.getAnswerConfirmationRequired();

        } else {
            return !TextUtils.isEmpty(mPhoneNumberEditText.getText().toString()) ||
                    mAnswerConfirmationRequiredCheckBox.isChecked();
        }
    }

    @Override
    public boolean enableSaveButton() {
        return super.enableSaveButton() && !TextUtils.isEmpty(mPhoneNumberEditText.getText().toString());
    }

    @Override
    public boolean enableDeleteButton() {
        return mCallSettings != null;
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public SimultaneousRingLocation getFormCallSettings() {
        return new SimultaneousRingLocation(mPhoneNumberEditText.getText().toString(), mAnswerConfirmationRequiredCheckBox.isChecked());
    }

    @Override
    public void saveForm() {
        if (getActivity() == null) {
            return;
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), SAVE_BUTTON_PRESSED);

        validateForm((type, callSettings) -> {
            ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList = new ArrayList<>();

            if (mCallSettings != null && !TextUtils.isEmpty(mCallSettings.getPhoneNumber())) {
                if (mSimultaneousRingServiceSettings.getSimultaneousRingLocationsList() != null) {
                    for (SimultaneousRingLocation location : mSimultaneousRingServiceSettings.getSimultaneousRingLocationsList()) {
                        if (location == null) {
                            continue;
                        }

                        if (TextUtils.equals(mCallSettings.getPhoneNumber(), location.getPhoneNumber())) {
                            simultaneousRingLocationsList.add(callSettings);

                        } else {
                            simultaneousRingLocationsList.add(new SimultaneousRingLocation(location));
                        }
                    }
                }

            } else {
                if (mSimultaneousRingServiceSettings.getSimultaneousRingLocationsList() != null) {
                    simultaneousRingLocationsList.addAll(mSimultaneousRingServiceSettings.getSimultaneousRingLocationsList());
                }

                simultaneousRingLocationsList.add(callSettings);
            }

            sendUpdatedServiceSetting(simultaneousRingLocationsList);
        });
    }

    @Override
    public void deleteForm() {
        if (getActivity() == null) {
            return;
        }

        mDialogManager.showDialog(
                getActivity(),
                0,
                R.string.set_call_settings_simultaneous_ring_location_delete_location_content,
                R.string.general_delete,
                (dialog, which) -> {
                    ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList = new ArrayList<>();

                    if (mCallSettings != null &&
                            !TextUtils.isEmpty(mCallSettings.getPhoneNumber()) &&
                            mSimultaneousRingServiceSettings.getSimultaneousRingLocationsList() != null) {

                        for (SimultaneousRingLocation location : mSimultaneousRingServiceSettings.getSimultaneousRingLocationsList()) {
                            if (location == null) {
                                continue;
                            }

                            if (!TextUtils.equals(mCallSettings.getPhoneNumber(), location.getPhoneNumber())) {
                                simultaneousRingLocationsList.add(location);
                            }
                        }
                    }

                    sendUpdatedServiceSetting(simultaneousRingLocationsList);

                    mAnalyticsManager.logEvent(EDIT_SIMULTANEOUS_RING_LOCATION, DELETE_LOCATION_DIALOG_DELETE_BUTTON_PRESSED);
                },
                R.string.general_cancel,
                (dialog, which) -> mAnalyticsManager.logEvent(EDIT_SIMULTANEOUS_RING_LOCATION, DELETE_LOCATION_DIALOG_CANCEL_BUTTON_PRESSED));

        mAnalyticsManager.logEvent(EDIT_SIMULTANEOUS_RING_LOCATION, DELETE_BUTTON_PRESSED);
        mAnalyticsManager.logEvent(EDIT_SIMULTANEOUS_RING_LOCATION, DELETE_LOCATION_DIALOG_SHOWN);
    }

    @NonNull
    @Override
    public String getAnalyticScreenName() {
        return mCallSettings == null ? ADD_SIMULTANEOUS_RING_LOCATION : EDIT_SIMULTANEOUS_RING_LOCATION;
    }
    // --------------------------------------------------------------------------------------------
}
