/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsDoNotDisturbBinding;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsGetResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsPutResponseEvent;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.functions.Consumer;

@AndroidEntryPoint
public class SetCallSettingsDoNotDisturbFragment extends SetCallSettingsBaseFragment<ServiceSettings> {

    protected CheckBox mEnabledCheckBox;
    protected CheckBox mRingSplashCheckBox;

    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected UserRepository mUserRepository;
    @Inject
    protected SessionManager mSessionManager;
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

    public SetCallSettingsDoNotDisturbFragment() {
        // Required empty public constructor
    }

    public static SetCallSettingsDoNotDisturbFragment newInstance(@NonNull ServiceSettings serviceSettings) {
        Bundle args = new Bundle();
        args.putString(PARAMS_CALL_SETTINGS_TYPE, serviceSettings.getType());
        args.putSerializable(PARAMS_CALL_SETTINGS_VALUE, serviceSettings);

        SetCallSettingsDoNotDisturbFragment fragment = new SetCallSettingsDoNotDisturbFragment();
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
        FragmentSetCallSettingsDoNotDisturbBinding binding = FragmentSetCallSettingsDoNotDisturbBinding.inflate(inflater, container, false);

        mEnabledCheckBox = binding.setCallSettingsDoNotDisturbEnabledCheckBox;
        mRingSplashCheckBox = binding.setCallSettingsDoNotDisturbRingSplashCheckBox;

        return binding.getRoot();
    }

    // --------------------------------------------------------------------------------------------
    // SetCallSettingsBaseFragment Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_set_call_settings_do_not_disturb;
    }

    @Override
    public int getToolbarTitleStringResId() {
        return R.string.set_call_settings_do_not_disturb_toolbar;
    }

    @Override
    protected void setupScreenWidgets() {
        super.setupScreenWidgets();
        mEnabledCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);
        mRingSplashCheckBox.setOnCheckedChangeListener(mRingSplashOnCheckedChangeListener);
    }

    @Override
    public void setupCallSettings() {
        if (mCallSettings != null) {
            mEnabledCheckBox.setOnCheckedChangeListener(null);
            mEnabledCheckBox.setChecked(mCallSettings.getActive());
            mEnabledCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener);

            mRingSplashCheckBox.setOnCheckedChangeListener(null);
            mRingSplashCheckBox.setChecked(mCallSettings.getRingSplashEnabled());
            mRingSplashCheckBox.setOnCheckedChangeListener(mRingSplashOnCheckedChangeListener);

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
        return mCallSettings != null &&
                (mEnabledCheckBox.isChecked() != mCallSettings.getActive() ||
                        mRingSplashCheckBox.isChecked() != mCallSettings.getRingSplashEnabled());
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public ServiceSettings getFormCallSettings() {
        ServiceSettings serviceSettings = new ServiceSettings(mCallSettings);
        serviceSettings.setActive(mEnabledCheckBox.isChecked());
        serviceSettings.setRingSplashEnabled(mRingSplashCheckBox.isChecked());

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

    @Override
    public int getHelpTextResId() {
        return R.string.set_call_settings_do_not_disturb_help_text;
    }

    @NonNull
    @Override
    public String getAnalyticScreenName() {
        return Enums.Analytics.ScreenName.DO_NOT_DISTURB;
    }
    // --------------------------------------------------------------------------------------------
}
