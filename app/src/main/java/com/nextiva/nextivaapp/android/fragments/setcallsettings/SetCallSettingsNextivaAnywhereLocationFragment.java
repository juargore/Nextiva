/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_NO_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_YES_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_CONTROL_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_CONTROL_SWITCH_UNCHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_NO_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_YES_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DELETE_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_CANCEL_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_DELETE_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SAVE_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsNextivaAnywhereLocationBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.Resource;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationSaveResponseEvent;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.ViewUtil;
import com.nextiva.nextivaapp.android.util.extensions.EditTextExtensionsKt;
import com.nextiva.nextivaapp.android.view.inputfilters.CallSettingsPhoneNumberInputFilter;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;
import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereLocationViewModel;
import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereLocationViewModel.ValidationEvent;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetCallSettingsNextivaAnywhereLocationFragment extends SetCallSettingsBaseFragment<NextivaAnywhereLocation> implements
        TextWatcher {

    private static final String PARAMS_NEXTIVA_ANYWHERE_SERVICE_SETTINGS = "PARAMS_NEXTIVA_ANYWHERE_SERVICE_SETTINGS";

    protected ViewGroup mMasterLayout;
    protected TextInputLayout mPhoneNumberTextInputLayout;
    protected EditText mPhoneNumberEditText;
    protected TextInputLayout mDescriptionLayout;
    protected EditText mDescriptionEditText;
    protected CheckBox mEnableThisLocationCheckBox;
    protected CheckBox mCallControlCheckBox;
    protected CheckBox mPreventDivertingCallsCheckBox;
    protected CheckBox mAnswerConfirmationCheckBox;

    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;

    private SetCallSettingsNextivaAnywhereLocationViewModel mViewModel;

    private final CompoundButton.OnCheckedChangeListener mCallControlOnCheckedChangeListener = (buttonView, isChecked) -> {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), isChecked ? CALL_CONTROL_SWITCH_CHECKED : CALL_CONTROL_SWITCH_UNCHECKED);
    };

    private final CompoundButton.OnCheckedChangeListener mPreventDivertingCallsOnCheckedChangeListener = (buttonView, isChecked) -> {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), isChecked ? PREVENT_DIVERTING_CALLS_SWITCH_CHECKED : PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED);
    };

    private final Observer<Resource<NextivaAnywhereLocation>> mEditingLocationObserver = resource -> {
        if (getActivity() == null) {
            return;
        }

        if (resource != null) {
            switch (resource.getStatus()) {
                case Enums.Net.StatusTypes.LOADING: {
                    mDialogManager.showProgressDialog(getActivity(), getAnalyticScreenName(), R.string.progress_processing);
                    break;
                }
                case Enums.Net.StatusTypes.SUCCESS: {
                    mDialogManager.dismissProgressDialog();
                    setupCallSettings();

                    if (mSetCallSettingsListener != null) {
                        mSetCallSettingsListener.onFormUpdated();
                    }
                    break;
                }
                case Enums.Net.StatusTypes.ERROR: {
                    mDialogManager.dismissProgressDialog();
                    mDialogManager.showErrorDialog(getActivity(), getAnalyticScreenName());
                    break;
                }
            }
        }
    };
    private final Observer<SingleEvent<ValidationEvent>> mSaveLocationValidationEventObserver = singleEvent -> {
        if (getActivity() == null) {
            return;
        }

        if (singleEvent != null && singleEvent.getContentIfNotHandled() != null) {
            final CheckDialingServiceConflictCallBack callBack = nextivaAnywhereLocation ->
                    mViewModel.saveNextivaAnywhereLocation(singleEvent.peekContent().getProposedServiceSettings(),
                                                           nextivaAnywhereLocation);

            if (singleEvent.peekContent().isCallBackConflicting()) {
                showCallBackConflictDialog(singleEvent.peekContent().getNextivaAnywhereLocation(), callBack);

            } else if (singleEvent.peekContent().isCallThroughConflicting()) {
                showCallThroughConflictDialog(singleEvent.peekContent().getNextivaAnywhereLocation(), callBack);

            } else {
                callBack.onSaveSetting(singleEvent.peekContent().getNextivaAnywhereLocation());
            }
        }
    };
    private final Observer<Resource<NextivaAnywhereLocationSaveResponseEvent>> mSaveLocationObserver = resource -> {
        if (getActivity() == null) {
            return;
        }

        if (resource != null) {
            switch (resource.getStatus()) {
                case Enums.Net.StatusTypes.LOADING: {
                    mDialogManager.showProgressDialog(getActivity(), getAnalyticScreenName(), R.string.progress_processing);
                    break;
                }
                case Enums.Net.StatusTypes.SUCCESS: {
                    mDialogManager.dismissProgressDialog();

                    Intent data = new Intent();
                    data.putExtra(Constants.EXTRA_CALL_SETTINGS_KEY, mCallSettingsType);
                    data.putExtra(Constants.EXTRA_CALL_SETTINGS_VALUE, resource.getData() != null ? resource.getData().getNextivaAnywhereLocation() : null);
                    data.putExtra(Constants.EXTRA_OLD_PHONE_NUMBER, resource.getData() != null ? resource.getData().getOldPhoneNumber() : null);
                    data.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_TYPE_SAVED);

                    if (mSetCallSettingsListener != null) {
                        mSetCallSettingsListener.onCallSettingsSaved(data);
                    }

                    break;
                }
                case Enums.Net.StatusTypes.ERROR: {
                    mDialogManager.dismissProgressDialog();

                    if (resource.getData() != null && resource.getData().getErrorInfo() != null) {
                        mDialogManager.showErrorDialog(getActivity(),
                                                       getAnalyticScreenName(),
                                                       resource.getData().getErrorInfo().getEventName(),
                                                       resource.getData().getErrorInfo().getDialogTitle(getActivity()),
                                                       resource.getData().getErrorInfo().getDialogContent(getActivity()),
                                                       ((dialog, which) -> {
                                                       }));

                    } else {
                        mDialogManager.showErrorDialog(getActivity(), getAnalyticScreenName());
                    }
                    break;
                }
            }
        }
    };
    private final Observer<SingleEvent<ValidationEvent>> mDeleteLocationValidationEventObserver = singleEvent -> {
        if (getActivity() == null) {
            return;
        }

        if (singleEvent != null && singleEvent.getContentIfNotHandled() != null) {
            final CheckDialingServiceConflictCallBack callBack = nextivaAnywhereLocation -> mViewModel.deleteNextivaAnywhereLocation(singleEvent.peekContent().getProposedServiceSettings(),
                                                                                                                                     nextivaAnywhereLocation);

            if (singleEvent.peekContent().isCallBackConflicting()) {
                showCallBackConflictDialog(singleEvent.peekContent().getNextivaAnywhereLocation(), callBack);

            } else if (singleEvent.peekContent().isCallThroughConflicting()) {
                showCallThroughConflictDialog(singleEvent.peekContent().getNextivaAnywhereLocation(), callBack);

            } else {
                mDialogManager.showDialog(
                        getActivity(),
                        0,
                        R.string.set_call_settings_nextiva_anywhere_location_delete_location_content,
                        R.string.general_delete,
                        (dialog, which) -> {
                            callBack.onSaveSetting(singleEvent.peekContent().getNextivaAnywhereLocation());
                            mAnalyticsManager.logEvent(getAnalyticScreenName(), DELETE_LOCATION_DIALOG_DELETE_BUTTON_PRESSED);
                        },
                        R.string.general_cancel,
                        (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), DELETE_LOCATION_DIALOG_CANCEL_BUTTON_PRESSED));

                mAnalyticsManager.logEvent(getAnalyticScreenName(), DELETE_LOCATION_DIALOG_SHOWN);
            }
        }
    };
    private final Observer<Resource<NextivaAnywhereLocation>> mDeleteLocationObserver = resource -> {
        if (getActivity() == null) {
            return;
        }

        if (resource != null) {
            switch (resource.getStatus()) {
                case Enums.Net.StatusTypes.LOADING: {
                    mDialogManager.showProgressDialog(getActivity(), getAnalyticScreenName(), R.string.progress_processing);
                    break;
                }
                case Enums.Net.StatusTypes.SUCCESS: {
                    mDialogManager.dismissProgressDialog();

                    Intent data = new Intent();
                    data.putExtra(Constants.EXTRA_CALL_SETTINGS_KEY, mCallSettingsType);
                    data.putExtra(Constants.EXTRA_CALL_SETTINGS_VALUE, resource.getData());
                    data.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_TYPE_DELETED);

                    if (mSetCallSettingsListener != null) {
                        mSetCallSettingsListener.onCallSettingsDeleted(data);
                    }

                    break;
                }
                case Enums.Net.StatusTypes.ERROR: {
                    mDialogManager.dismissProgressDialog();
                    mDialogManager.showErrorDialog(getActivity(), getAnalyticScreenName());
                    break;
                }
            }
        }
    };

    public SetCallSettingsNextivaAnywhereLocationFragment() {
        // Required empty public constructor
    }

    public static SetCallSettingsNextivaAnywhereLocationFragment newInstance(
            @NonNull ServiceSettings nextivaAnywhereServiceSettings,
            @Nullable NextivaAnywhereLocation nextivaAnywhereLocation) {

        Bundle args = new Bundle();
        args.putSerializable(PARAMS_NEXTIVA_ANYWHERE_SERVICE_SETTINGS, nextivaAnywhereServiceSettings);
        args.putString(PARAMS_CALL_SETTINGS_TYPE, Enums.CallSettings.FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION);
        args.putSerializable(PARAMS_CALL_SETTINGS_VALUE, nextivaAnywhereLocation);

        SetCallSettingsNextivaAnywhereLocationFragment fragment = new SetCallSettingsNextivaAnywhereLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }

        mViewModel = new ViewModelProvider(this).get(SetCallSettingsNextivaAnywhereLocationViewModel.class);
        mViewModel.getEditingLocationLiveData().observe(this, mEditingLocationObserver);
        mViewModel.getSaveLocationValidationEventLiveData().observe(this, mSaveLocationValidationEventObserver);
        mViewModel.getSaveLocationLiveData().observe(this, mSaveLocationObserver);
        mViewModel.getDeleteLocationValidationEventLiveData().observe(this, mDeleteLocationValidationEventObserver);
        mViewModel.getDeleteLocationLiveData().observe(this, mDeleteLocationObserver);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mViewModel.setNextivaAnywhereServiceSettings((ServiceSettings) getArguments().getSerializable(PARAMS_NEXTIVA_ANYWHERE_SERVICE_SETTINGS));
            mViewModel.setEditingLocation(mCallSettings);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = bindViews(inflater, container);
        super.onCreateView(inflater, container, savedInstanceState);

        EditTextExtensionsKt.makeClearableEditText(mPhoneNumberEditText);
        EditTextExtensionsKt.makeClearableEditText(mDescriptionEditText);

        return view;
    }

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentSetCallSettingsNextivaAnywhereLocationBinding binding = FragmentSetCallSettingsNextivaAnywhereLocationBinding.inflate(inflater, container, false);

        mMasterLayout = binding.setCallSettingsNextivaAnywhereLocationMasterLayout;
        mPhoneNumberTextInputLayout = binding.setCallSettingsNextivaAnywhereLocationPhoneNumberLayout;
        mPhoneNumberEditText = binding.setCallSettingsNextivaAnywhereLocationPhoneNumberEditText;
        mDescriptionLayout = binding.setCallSettingsNextivaAnywhereLocationDescriptionLayout;
        mDescriptionEditText = binding.setCallSettingsNextivaAnywhereLocationDescriptionEditText;
        mEnableThisLocationCheckBox = binding.setCallSettingsNextivaAnywhereLocationEnableThisLocationCheckBox;
        mCallControlCheckBox = binding.setCallSettingsNextivaAnywhereLocationCallControlCheckBox;
        mPreventDivertingCallsCheckBox = binding.setCallSettingsNextivaAnywhereLocationPreventDivertingCallsCheckBox;
        mAnswerConfirmationCheckBox = binding.setCallSettingsNextivaAnywhereLocationAnswerConfirmationCheckBox;

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
        mPhoneNumberEditText.setOnEditorActionListener((textView, i, keyEvent) -> onPhoneNumberEditorAction(i));

        return binding.getRoot();
    }

    protected void afterPhoneNumberTextChanged(Editable editable) {
        setFormEnabled(editable.length() > 0);

        if (editable.length() == 0) {
            mEnableThisLocationCheckBox.setOnCheckedChangeListener(null);
            mEnableThisLocationCheckBox.setChecked(false);
            mEnableThisLocationCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);
        }
    }

    protected boolean onPhoneNumberEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (!mDescriptionEditText.isEnabled()) {
                ViewUtil.hideKeyboard(mPhoneNumberEditText);
                mMasterLayout.requestFocus();
                return true;
            }
        }

        return false;
    }

    private void showCallBackConflictDialog(final NextivaAnywhereLocation updatedLocation, final CheckDialingServiceConflictCallBack callBack) {
        if (getActivity() == null) {
            return;
        }

        mDialogManager.showDialog(
                getActivity(),
                R.string.dialing_service_conflict_call_back_title,
                R.string.dialing_service_conflict_call_back_message,
                R.string.general_yes,
                (dialog, which) -> {
                    callBack.onSaveSetting(updatedLocation);
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_BACK_CONFLICT_DIALOG_YES_BUTTON_PRESSED);
                },
                R.string.general_no,
                (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_BACK_CONFLICT_DIALOG_NO_BUTTON_PRESSED));

        mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_BACK_CONFLICT_DIALOG_SHOWN);
    }

    private void showCallThroughConflictDialog(final NextivaAnywhereLocation updatedLocation, final CheckDialingServiceConflictCallBack callBack) {
        if (getActivity() == null) {
            return;
        }

        mDialogManager.showDialog(
                getActivity(),
                R.string.dialing_service_conflict_call_through_title,
                R.string.dialing_service_conflict_call_through_message,
                R.string.general_yes,
                (dialog, which) -> {
                    callBack.onSaveSetting(updatedLocation);
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_THROUGH_CONFLICT_DIALOG_YES_BUTTON_PRESSED);
                },
                R.string.general_no,
                (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_THROUGH_CONFLICT_DIALOG_NO_BUTTON_PRESSED));

        mAnalyticsManager.logEvent(getAnalyticScreenName(), CALL_THROUGH_CONFLICT_DIALOG_SHOWN);
    }

    private void setFormEnabled(boolean enabled) {
        mDescriptionLayout.setEnabled(enabled);
        mEnableThisLocationCheckBox.setEnabled(enabled);
        mCallControlCheckBox.setEnabled(enabled);
        mPreventDivertingCallsCheckBox.setEnabled(enabled);
        mAnswerConfirmationCheckBox.setEnabled(enabled);
    }

    // --------------------------------------------------------------------------------------------
    // SetCallSettingsBaseFragment Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_set_call_settings_nextiva_anywhere_location;
    }

    @Override
    public int getToolbarTitleStringResId() {
        return mViewModel.getEditingLocation() == null ?
                R.string.set_call_settings_nextiva_anywhere_add_location_toolbar :
                R.string.set_call_settings_nextiva_anywhere_edit_location_toolbar;
    }

    @Override
    public void setupScreenWidgets() {
        ArrayList<InputFilter> inputFiltersList = new ArrayList<>(Arrays.asList(mPhoneNumberEditText.getFilters()));
        inputFiltersList.add(new CallSettingsPhoneNumberInputFilter());

        mPhoneNumberEditText.setFilters(inputFiltersList.toArray(new InputFilter[0]));
        mPhoneNumberEditText.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());
        mPhoneNumberEditText.addTextChangedListener(SetCallSettingsNextivaAnywhereLocationFragment.this);
        mDescriptionEditText.addTextChangedListener(SetCallSettingsNextivaAnywhereLocationFragment.this);

        mViewModel.fetchNextivaAnywhereLocation();

        mEnableThisLocationCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);
        mCallControlCheckBox.setOnCheckedChangeListener(mCallControlOnCheckedChangeListener);
        mPreventDivertingCallsCheckBox.setOnCheckedChangeListener(mPreventDivertingCallsOnCheckedChangeListener);
        mAnswerConfirmationCheckBox.setOnCheckedChangeListener(mAnswerConfirmationOnCheckedChangeListener);
    }

    @Override
    protected void setupCallSettings() {
        NextivaAnywhereLocation editingLocation = mViewModel.getEditingLocation();

        if (editingLocation != null) {
            mPhoneNumberTextInputLayout.setHintAnimationEnabled(false);
            mPhoneNumberEditText.removeTextChangedListener(SetCallSettingsNextivaAnywhereLocationFragment.this);
            mPhoneNumberEditText.setText(CallUtil.cleanForTextWatcher(editingLocation.getPhoneNumber()));
            mPhoneNumberEditText.addTextChangedListener(SetCallSettingsNextivaAnywhereLocationFragment.this);
            mPhoneNumberTextInputLayout.setHintAnimationEnabled(true);

            mDescriptionLayout.setHintAnimationEnabled(false);
            mDescriptionEditText.removeTextChangedListener(SetCallSettingsNextivaAnywhereLocationFragment.this);
            mDescriptionEditText.setText(editingLocation.getDescription());
            mDescriptionEditText.addTextChangedListener(SetCallSettingsNextivaAnywhereLocationFragment.this);
            mDescriptionLayout.setHintAnimationEnabled(true);

            mEnableThisLocationCheckBox.setOnCheckedChangeListener(null);
            mEnableThisLocationCheckBox.setChecked(editingLocation.getActive());
            mEnableThisLocationCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);

            mCallControlCheckBox.setOnCheckedChangeListener(null);
            mCallControlCheckBox.setChecked(editingLocation.getCallControlEnabled());
            mCallControlCheckBox.setOnCheckedChangeListener(mCallControlOnCheckedChangeListener);

            mPreventDivertingCallsCheckBox.setOnCheckedChangeListener(null);
            mPreventDivertingCallsCheckBox.setChecked(editingLocation.getPreventDivertingCalls());
            mPreventDivertingCallsCheckBox.setOnCheckedChangeListener(mPreventDivertingCallsOnCheckedChangeListener);

            mAnswerConfirmationCheckBox.setOnCheckedChangeListener(null);
            mAnswerConfirmationCheckBox.setChecked(editingLocation.getAnswerConfirmationRequired());
            mAnswerConfirmationCheckBox.setOnCheckedChangeListener(mAnswerConfirmationOnCheckedChangeListener);
        }

        setFormEnabled(!TextUtils.isEmpty(mPhoneNumberEditText.getText().toString()));
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean changesMade() {
        return mViewModel.changesMade(mPhoneNumberEditText.getText().toString(),
                                      mDescriptionEditText.getText().toString(),
                                      mEnableThisLocationCheckBox.isChecked(),
                                      mCallControlCheckBox.isChecked(),
                                      mPreventDivertingCallsCheckBox.isChecked(),
                                      mAnswerConfirmationCheckBox.isChecked());
    }

    @Override
    public boolean enableSaveButton() {
        return super.enableSaveButton() && !TextUtils.isEmpty(mPhoneNumberEditText.getText().toString());
    }

    @Override
    public boolean enableDeleteButton() {
        return mViewModel.getEditingLocation() != null;
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public NextivaAnywhereLocation getFormCallSettings() {
        String description = mDescriptionEditText.getText().toString();
        if (mViewModel.getEditingLocation() == null && TextUtils.isEmpty(description)) {
            description = null;
        }

        return new NextivaAnywhereLocation(
                mPhoneNumberEditText.getText().toString(),
                description,
                mEnableThisLocationCheckBox.isChecked(),
                mCallControlCheckBox.isChecked(),
                mPreventDivertingCallsCheckBox.isChecked(),
                mAnswerConfirmationCheckBox.isChecked());
    }

    @Override
    public void saveForm() {
        mAnalyticsManager.logEvent(getAnalyticScreenName(), SAVE_BUTTON_PRESSED);

        mViewModel.validateSaveNextivaAnywhereLocation(getFormCallSettings());
    }

    @Override
    public void deleteForm() {
        mAnalyticsManager.logEvent(EDIT_NEXTIVA_ANYWHERE_LOCATION, DELETE_BUTTON_PRESSED);

        mViewModel.validateDeleteNextivaAnywhereLocation();
    }

    @NonNull
    @Override
    public String getAnalyticScreenName() {
        return mViewModel.getEditingLocation() == null ? ADD_NEXTIVA_ANYWHERE_LOCATION : EDIT_NEXTIVA_ANYWHERE_LOCATION;
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // TextWatcher Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mSetCallSettingsListener != null) {
            mSetCallSettingsListener.onFormUpdated();
        }
    }
    // --------------------------------------------------------------------------------------------

    private interface CheckDialingServiceConflictCallBack {
        void onSaveSetting(NextivaAnywhereLocation nextivaAnywhereLocation);
    }
}
