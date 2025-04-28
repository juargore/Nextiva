/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsCallForwardWhenUnansweredBinding;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsGetResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsPutResponseEvent;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.extensions.EditTextExtensionsKt;
import com.nextiva.nextivaapp.android.view.inputfilters.CallSettingsPhoneNumberInputFilter;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.functions.Consumer;

@AndroidEntryPoint
public class SetCallSettingsCallForwardWhenUnansweredFragment extends SetCallSettingsBaseFragment<ServiceSettings> implements
        AdapterView.OnItemSelectedListener {

    private static final String DEFAULT_NUMBER_RINGS = "3";

    protected TextInputLayout mPhoneNumberTextInputLayout;
    protected EditText mPhoneNumberEditText;
    protected Spinner mNumberOfRingsSpinner;
    protected CheckBox mEnabledCheckBox;

    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected UserRepository mUserRepository;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;

    private ArrayAdapter<String> mSpinnerAdapter;

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

    public SetCallSettingsCallForwardWhenUnansweredFragment() {
        // Required empty public constructor
    }

    public static SetCallSettingsCallForwardWhenUnansweredFragment newInstance(@NonNull ServiceSettings serviceSettings) {
        Bundle args = new Bundle();
        args.putString(PARAMS_CALL_SETTINGS_TYPE, serviceSettings.getType());
        args.putSerializable(PARAMS_CALL_SETTINGS_VALUE, serviceSettings);

        SetCallSettingsCallForwardWhenUnansweredFragment fragment = new SetCallSettingsCallForwardWhenUnansweredFragment();
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
        FragmentSetCallSettingsCallForwardWhenUnansweredBinding binding = FragmentSetCallSettingsCallForwardWhenUnansweredBinding.inflate(inflater, container, false);

        mPhoneNumberTextInputLayout = binding.setCallSettingsCallForwardWhenUnansweredPhoneNumberLayout;
        mPhoneNumberEditText = binding.setCallSettingsCallForwardWhenUnansweredPhoneNumberEditText;
        mNumberOfRingsSpinner = binding.setCallSettingsCallForwardWhenUnansweredNumberOfRingsSpinner;
        mEnabledCheckBox = binding.setCallSettingsCallForwardWhenUnansweredEnabledCheckBox;

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
        return R.layout.fragment_set_call_settings_call_forward_when_unanswered;
    }

    @Override
    public int getToolbarTitleStringResId() {
        return R.string.set_call_settings_call_forwarding_forward_when_unanswered_toolbar;
    }

    @Override
    protected void setupScreenWidgets() {
        if (getActivity() == null) {
            return;
        }

        ArrayList<InputFilter> inputFiltersList = new ArrayList<>(Arrays.asList(mPhoneNumberEditText.getFilters()));
        inputFiltersList.add(new CallSettingsPhoneNumberInputFilter());

        mPhoneNumberEditText.setFilters(inputFiltersList.toArray(new InputFilter[0]));
        mPhoneNumberEditText.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());

        final List<String> spinnerOptionsList = Arrays.asList(getResources().getStringArray(R.array.call_settings_call_forwarding_when_no_answer_number_of_rings_options));

        mSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, spinnerOptionsList);
        mNumberOfRingsSpinner.setAdapter(mSpinnerAdapter);

        mEnabledCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);
    }

    @Override
    public void setupCallSettings() {
        if (mCallSettings != null) {
            mPhoneNumberTextInputLayout.setHintAnimationEnabled(false);
            mPhoneNumberEditText.setText(CallUtil.cleanForTextWatcher(mCallSettings.getForwardToPhoneNumber()));
            mPhoneNumberTextInputLayout.setHintAnimationEnabled(true);

            mEnabledCheckBox.setOnCheckedChangeListener(null);
            mEnabledCheckBox.setEnabled(!TextUtils.isEmpty(mPhoneNumberEditText.getText().toString()));
            mEnabledCheckBox.setChecked(mCallSettings.getActive());
            mEnabledCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);

            int selectedIndex = mSpinnerAdapter.getPosition(DEFAULT_NUMBER_RINGS);

            if (mCallSettings.getNumberOfRings() != null) {
                if (mCallSettings.getNumberOfRings() == 0) {
                    selectedIndex = mSpinnerAdapter.getPosition(getString(R.string.set_call_settings_call_forwarding_forward_when_unanswered_no_rings));
                } else {
                    selectedIndex = mSpinnerAdapter.getPosition(String.valueOf(mCallSettings.getNumberOfRings()));
                }
            }

            mNumberOfRingsSpinner.setOnItemSelectedListener(null);
            mNumberOfRingsSpinner.setSelection(selectedIndex);
            mNumberOfRingsSpinner.setOnItemSelectedListener(SetCallSettingsCallForwardWhenUnansweredFragment.this);
            mNumberOfRingsSpinner.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.NUMBER_OF_RINGS_BUTTON_PRESSED);
                }
                v.performClick();

                return false;
            });

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

        if (!TextUtils.isEmpty(mCallSettings.getForwardToPhoneNumber())) {
            existingPhoneNumber = mCallSettings.getForwardToPhoneNumber();
        }

        Integer numberOfRings = 0;

        if (mNumberOfRingsSpinner.getSelectedItemPosition() == 0) {
            numberOfRings = 0;

        } else {
            int numberOfRingsSpinnerSelectedItemPosition = mNumberOfRingsSpinner.getSelectedItemPosition();
            String spinnerAdapterItem = mSpinnerAdapter.getItem(numberOfRingsSpinnerSelectedItemPosition);

            if (spinnerAdapterItem != null) {
                numberOfRings = Integer.valueOf(spinnerAdapterItem);
            }
        }


        return mCallSettings != null &&
                (!TextUtils.equals(CallUtil.cleanForTextWatcher(mPhoneNumberEditText.getText().toString()), CallUtil.cleanForTextWatcher(existingPhoneNumber)) ||
                        !numberOfRings.equals(mCallSettings.getNumberOfRings()) ||
                        mEnabledCheckBox.isChecked() != mCallSettings.getActive());
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public ServiceSettings getFormCallSettings() {
        ServiceSettings serviceSettings = new ServiceSettings(mCallSettings);
        serviceSettings.setForwardToPhoneNumber(mPhoneNumberEditText.getText().toString());
        serviceSettings.setActive(mEnabledCheckBox.isChecked());

        if (mNumberOfRingsSpinner.getSelectedItemPosition() == 0) {
            serviceSettings.setNumberOfRings(0);
        } else {
            try {
                int numberOfRingsSpinnerSelectedItemPosition = mNumberOfRingsSpinner.getSelectedItemPosition();
                String spinnerAdapterItem = mSpinnerAdapter.getItem(numberOfRingsSpinnerSelectedItemPosition);

                if (spinnerAdapterItem != null) {
                    serviceSettings.setNumberOfRings(Integer.parseInt(spinnerAdapterItem));
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                serviceSettings.setNumberOfRings(0);
            }
        }

        return serviceSettings;
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

    @NonNull
    @Override
    public String getAnalyticScreenName() {
        return Enums.Analytics.ScreenName.CALL_FORWARDING_WHEN_UNANSWERED_SCREEN;
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // AdapterView.OnItemSelectedListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    // --------------------------------------------------------------------------------------------
}
